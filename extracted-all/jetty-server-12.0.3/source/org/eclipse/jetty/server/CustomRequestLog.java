package org.eclipse.jetty.server;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.QuotedCSV;
import org.eclipse.jetty.http.pathmap.PathMappings;
import org.eclipse.jetty.util.DateCache;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("Custom format request log")
public class CustomRequestLog extends ContainerLifeCycle implements RequestLog {
   public static final String DEFAULT_DATE_FORMAT = "dd/MMM/yyyy:HH:mm:ss ZZZ";
   public static final String NCSA_FORMAT = "%{client}a - %u %t \"%r\" %s %O";
   public static final String EXTENDED_NCSA_FORMAT = "%{client}a - %u %t \"%r\" %s %O \"%{Referer}i\" \"%{User-Agent}i\"";
   public static final String LOG_DETAIL = CustomRequestLog.class.getName() + ".logDetail";
   private static final Logger LOG = LoggerFactory.getLogger(CustomRequestLog.class);
   private static final ThreadLocal<StringBuilder> _buffers = ThreadLocal.withInitial(() -> new StringBuilder(256));
   private final RequestLog.Writer _requestLogWriter;
   private final MethodHandle _logHandle;
   private final String _formatString;
   private transient PathMappings<String> _ignorePathMap;
   private String[] _ignorePaths;
   private BiPredicate<Request, Response> _filter;

   public CustomRequestLog() {
      this(new Slf4jRequestLogWriter(), "%{client}a - %u %t \"%r\" %s %O \"%{Referer}i\" \"%{User-Agent}i\"");
   }

   public CustomRequestLog(String file) {
      this(file, "%{client}a - %u %t \"%r\" %s %O \"%{Referer}i\" \"%{User-Agent}i\"");
   }

   public CustomRequestLog(String file, String format) {
      this(new RequestLogWriter(file), format);
   }

   public CustomRequestLog(RequestLog.Writer writer, String formatString) {
      this._formatString = formatString;
      this._requestLogWriter = writer;
      this.addBean(this._requestLogWriter);

      try {
         this._logHandle = this.getLogHandle(formatString);
      } catch (IllegalAccessException | NoSuchMethodException var4) {
         throw new IllegalStateException(var4);
      }
   }

   public void setFilter(BiPredicate<Request, Response> filter) {
      this._filter = filter;
   }

   @ManagedAttribute("The RequestLogWriter")
   public RequestLog.Writer getWriter() {
      return this._requestLogWriter;
   }

   @Override
   public void log(Request request, Response response) {
      try {
         if (this._ignorePathMap != null && this._ignorePathMap.getMatched(request.getHttpURI().toString()) != null) {
            return;
         }

         if (this._filter != null && !this._filter.test(request, response)) {
            return;
         }

         StringBuilder sb = _buffers.get();
         sb.setLength(0);
         this._logHandle.invoke((StringBuilder)sb, (Request)request, (Response)response);
         String log = sb.toString();
         this._requestLogWriter.write(log);
      } catch (Throwable var5) {
         LOG.warn("Unable to log request", var5);
      }
   }

   public void setIgnorePaths(String[] ignorePaths) {
      this._ignorePaths = ignorePaths;
   }

   public String[] getIgnorePaths() {
      return this._ignorePaths;
   }

   @ManagedAttribute("format string")
   public String getFormatString() {
      return this._formatString;
   }

   protected void doStart() throws Exception {
      if (this._ignorePaths != null && this._ignorePaths.length > 0) {
         this._ignorePathMap = new PathMappings();

         for (String ignorePath : this._ignorePaths) {
            this._ignorePathMap.put(ignorePath, ignorePath);
         }
      } else {
         this._ignorePathMap = null;
      }

      super.doStart();
   }

   private static void append(StringBuilder buf, String s) {
      if (s != null && s.length() != 0) {
         buf.append(s);
      } else {
         buf.append('-');
      }
   }

   private static void append(String s, StringBuilder buf) {
      append(buf, s);
   }

   private MethodHandle getLogHandle(String formatString) throws NoSuchMethodException, IllegalAccessException {
      Lookup lookup = MethodHandles.lookup();
      MethodHandle append = lookup.findStatic(CustomRequestLog.class, "append", MethodType.methodType(void.class, String.class, StringBuilder.class));
      MethodHandle logHandle = lookup.findStatic(
         CustomRequestLog.class, "logNothing", MethodType.methodType(void.class, StringBuilder.class, Request.class, Response.class)
      );
      List<CustomRequestLog.Token> tokens = getTokens(formatString);
      Collections.reverse(tokens);

      for (CustomRequestLog.Token t : tokens) {
         if (t.isLiteralString()) {
            logHandle = this.updateLogHandle(logHandle, append, t.literal);
         } else {
            if (!t.isPercentCode()) {
               throw new IllegalStateException("bad token " + t);
            }

            logHandle = this.updateLogHandle(logHandle, append, lookup, t.code, t.arg, t.modifiers, t.negated);
         }
      }

      return logHandle;
   }

   private static List<CustomRequestLog.Token> getTokens(String formatString) {
      Pattern PATTERN = Pattern.compile(
         "^(?:%(?<MOD>!?[0-9,]+)?(?:\\{(?<ARG>[^}]+)})?(?<CODE>(?:(?:ti)|(?:to)|[a-zA-Z%]))|(?<LITERAL>[^%]+))(?<REMAINING>.*)", 40
      );
      List<CustomRequestLog.Token> tokens = new ArrayList<>();
      String remaining = formatString;

      while (remaining.length() > 0) {
         Matcher m = PATTERN.matcher(remaining);
         if (!m.matches()) {
            throw new IllegalArgumentException("Invalid format string: " + formatString);
         }

         if (m.group("CODE") != null) {
            String code = m.group("CODE");
            String arg = m.group("ARG");
            String modifierString = m.group("MOD");
            List<Integer> modifiers = null;
            boolean negated = false;
            if (modifierString != null) {
               if (modifierString.startsWith("!")) {
                  modifierString = modifierString.substring(1);
                  negated = true;
               }

               modifiers = new QuotedCSV(new String[]{modifierString}).getValues().stream().map(Integer::parseInt).collect(Collectors.toList());
            }

            tokens.add(new CustomRequestLog.Token(code, arg, modifiers, negated));
         } else {
            if (m.group("LITERAL") == null) {
               throw new IllegalStateException("formatString parsing error: " + formatString);
            }

            String literal = m.group("LITERAL");
            tokens.add(new CustomRequestLog.Token(literal));
         }

         remaining = m.group("REMAINING");
      }

      return tokens;
   }

   private static boolean modify(List<Integer> modifiers, Boolean negated, StringBuilder b, Request request, Response response) {
      return negated ? !modifiers.contains(response.getStatus()) : modifiers.contains(response.getStatus());
   }

   private MethodHandle updateLogHandle(MethodHandle logHandle, MethodHandle append, String literal) {
      return MethodHandles.foldArguments(
         logHandle, MethodHandles.dropArguments(MethodHandles.dropArguments(append.bindTo(literal), 1, Request.class), 2, Response.class)
      );
   }

   private MethodHandle updateLogHandle(
      MethodHandle logHandle, MethodHandle append, Lookup lookup, String code, String arg, List<Integer> modifiers, boolean negated
   ) throws NoSuchMethodException, IllegalAccessException {
      MethodType logType = MethodType.methodType(void.class, StringBuilder.class, Request.class, Response.class);
      MethodType logTypeArg = MethodType.methodType(void.class, String.class, StringBuilder.class, Request.class, Response.class);

      MethodHandle specificHandle = switch (code) {
         case "%" -> MethodHandles.dropArguments(MethodHandles.dropArguments(append.bindTo("%"), 1, Request.class), 2, Response.class);
         case "a" -> {
            if (StringUtil.isEmpty(arg)) {
               arg = "server";
            }
            String method = switch (arg) {
               case "server" -> "logServerHost";
               case "client" -> "logClientHost";
               case "local" -> "logLocalHost";
               case "remote" -> "logRemoteHost";
               default -> throw new IllegalArgumentException("Invalid arg for %a");
            };
            yield lookup.findStatic(CustomRequestLog.class, method, logType);
         }
         case "p" -> {
            if (StringUtil.isEmpty(arg)) {
               arg = "server";
            }
            String method = switch (arg) {
               case "server" -> "logServerPort";
               case "client" -> "logClientPort";
               case "local" -> "logLocalPort";
               case "remote" -> "logRemotePort";
               default -> throw new IllegalArgumentException("Invalid arg for %p");
            };
            yield lookup.findStatic(CustomRequestLog.class, method, logType);
         }
         case "I" -> {
            String method;
            if (StringUtil.isEmpty(arg)) {
               method = "logBytesReceived";
            } else {
               if (!arg.equalsIgnoreCase("clf")) {
                  throw new IllegalArgumentException("Invalid argument for %I");
               }

               method = "logBytesReceivedCLF";
            }

            yield lookup.findStatic(CustomRequestLog.class, method, logType);
         }
         case "O" -> {
            String method;
            if (StringUtil.isEmpty(arg)) {
               method = "logBytesSent";
            } else {
               if (!arg.equalsIgnoreCase("clf")) {
                  throw new IllegalArgumentException("Invalid argument for %O");
               }

               method = "logBytesSentCLF";
            }

            yield lookup.findStatic(CustomRequestLog.class, method, logType);
         }
         case "S" -> {
            String method;
            if (StringUtil.isEmpty(arg)) {
               method = "logBytesTransferred";
            } else {
               if (!arg.equalsIgnoreCase("clf")) {
                  throw new IllegalArgumentException("Invalid argument for %S");
               }

               method = "logBytesTransferredCLF";
            }

            yield lookup.findStatic(CustomRequestLog.class, method, logType);
         }
         case "C" -> StringUtil.isEmpty(arg)
         ? lookup.findStatic(CustomRequestLog.class, "logRequestCookies", logType)
         : lookup.findStatic(CustomRequestLog.class, "logRequestCookie", logTypeArg).bindTo(arg);
         case "D" -> lookup.findStatic(CustomRequestLog.class, "logLatencyMicroseconds", logType);
         case "e" -> {
            if (StringUtil.isEmpty(arg)) {
               throw new IllegalArgumentException("No arg for %e");
            }

            yield lookup.findStatic(CustomRequestLog.class, "logEnvironmentVar", logTypeArg).bindTo(arg);
         }
         case "f" -> lookup.findStatic(CustomRequestLog.class, "logFilename", logType);
         case "H" -> lookup.findStatic(CustomRequestLog.class, "logRequestProtocol", logType);
         case "i" -> {
            if (StringUtil.isEmpty(arg)) {
               throw new IllegalArgumentException("No arg for %i");
            }

            yield lookup.findStatic(CustomRequestLog.class, "logRequestHeader", logTypeArg).bindTo(arg);
         }
         case "k" -> lookup.findStatic(CustomRequestLog.class, "logKeepAliveRequests", logType);
         case "m" -> lookup.findStatic(CustomRequestLog.class, "logRequestMethod", logType);
         case "o" -> {
            if (StringUtil.isEmpty(arg)) {
               throw new IllegalArgumentException("No arg for %o");
            }

            yield lookup.findStatic(CustomRequestLog.class, "logResponseHeader", logTypeArg).bindTo(arg);
         }
         case "q" -> lookup.findStatic(CustomRequestLog.class, "logQueryString", logType);
         case "r" -> lookup.findStatic(CustomRequestLog.class, "logRequestFirstLine", logType);
         case "R" -> lookup.findStatic(CustomRequestLog.class, "logRequestHandler", logType);
         case "s" -> lookup.findStatic(CustomRequestLog.class, "logResponseStatus", logType);
         case "t" -> {
            String format = "dd/MMM/yyyy:HH:mm:ss ZZZ";
            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            Locale locale = Locale.getDefault();
            if (arg != null && !arg.isEmpty()) {
               String[] args = arg.split("\\|");
               switch (args.length) {
                  case 1:
                     format = args[0];
                     break;
                  case 2:
                     format = args[0];
                     timeZone = TimeZone.getTimeZone(args[1]);
                     break;
                  case 3:
                     format = args[0];
                     timeZone = TimeZone.getTimeZone(args[1]);
                     locale = Locale.forLanguageTag(args[2]);
                     break;
                  default:
                     throw new IllegalArgumentException("Too many \"|\" characters in %t");
               }
            }

            DateCache logDateCache = new DateCache(format, locale, timeZone);
            MethodType logTypeDateCache = MethodType.methodType(void.class, DateCache.class, StringBuilder.class, Request.class, Response.class);
            yield lookup.findStatic(CustomRequestLog.class, "logRequestTime", logTypeDateCache).bindTo(logDateCache);
         }
         case "T" -> {
            if (arg == null) {
               arg = "s";
            }
            String method = switch (arg) {
               case "s" -> "logLatencySeconds";
               case "us" -> "logLatencyMicroseconds";
               case "ms" -> "logLatencyMilliseconds";
               default -> throw new IllegalArgumentException("Invalid arg for %T");
            };
            yield lookup.findStatic(CustomRequestLog.class, method, logType);
         }
         case "u" -> {
            String method;
            if (StringUtil.isEmpty(arg)) {
               method = "logRequestAuthentication";
            } else {
               if (!"d".equals(arg)) {
                  throw new IllegalArgumentException("Invalid arg for %u: " + arg);
               }

               method = "logRequestAuthenticationWithDeferred";
            }

            yield lookup.findStatic(CustomRequestLog.class, method, logType);
         }
         case "U" -> lookup.findStatic(CustomRequestLog.class, "logUrlRequestPath", logType);
         case "X" -> lookup.findStatic(CustomRequestLog.class, "logConnectionStatus", logType);
         case "ti" -> {
            if (StringUtil.isEmpty(arg)) {
               throw new IllegalArgumentException("No arg for %ti");
            }

            yield lookup.findStatic(CustomRequestLog.class, "logRequestTrailer", logTypeArg).bindTo(arg);
         }
         case "to" -> {
            if (StringUtil.isEmpty(arg)) {
               throw new IllegalArgumentException("No arg for %to");
            }

            yield lookup.findStatic(CustomRequestLog.class, "logResponseTrailer", logTypeArg).bindTo(arg);
         }
         default -> throw new IllegalArgumentException("Unsupported code %" + code);
      };
      if (modifiers != null && !modifiers.isEmpty()) {
         MethodHandle dash = this.updateLogHandle(logHandle, append, "-");
         MethodHandle log = MethodHandles.foldArguments(logHandle, specificHandle);
         MethodHandle modifierTest = lookup.findStatic(
            CustomRequestLog.class,
            "modify",
            MethodType.methodType(boolean.class, List.class, Boolean.class, StringBuilder.class, Request.class, Response.class)
         );
         modifierTest = modifierTest.bindTo(modifiers).bindTo(negated);
         return MethodHandles.guardWithTest(modifierTest, log, dash);
      } else {
         return MethodHandles.foldArguments(logHandle, specificHandle);
      }
   }

   private static void logNothing(StringBuilder b, Request request, Response response) {
   }

   private static void logServerHost(StringBuilder b, Request request, Response response) {
      append(b, Request.getServerName(request));
   }

   private static void logClientHost(StringBuilder b, Request request, Response response) {
      append(b, Request.getRemoteAddr(request));
   }

   private static void logLocalHost(StringBuilder b, Request request, Response response) {
      append(b, Request.getLocalAddr(Request.unWrap(request)));
   }

   private static void logRemoteHost(StringBuilder b, Request request, Response response) {
      append(b, Request.getRemoteAddr(Request.unWrap(request)));
   }

   private static void logServerPort(StringBuilder b, Request request, Response response) {
      b.append(Request.getServerPort(request));
   }

   private static void logClientPort(StringBuilder b, Request request, Response response) {
      b.append(Request.getRemotePort(request));
   }

   private static void logLocalPort(StringBuilder b, Request request, Response response) {
      b.append(Request.getLocalPort(Request.unWrap(request)));
   }

   private static void logRemotePort(StringBuilder b, Request request, Response response) {
      b.append(Request.getRemotePort(Request.unWrap(request)));
   }

   private static void logResponseSize(StringBuilder b, Request request, Response response) {
      b.append(Response.getContentBytesWritten(response));
   }

   private static void logResponseSizeCLF(StringBuilder b, Request request, Response response) {
      long written = Response.getContentBytesWritten(response);
      if (written == 0L) {
         b.append('-');
      } else {
         b.append(written);
      }
   }

   private static void logBytesSent(StringBuilder b, Request request, Response response) {
      b.append(Response.getContentBytesWritten(response));
   }

   private static void logBytesSentCLF(StringBuilder b, Request request, Response response) {
      long sent = Response.getContentBytesWritten(response);
      if (sent == 0L) {
         b.append('-');
      } else {
         b.append(sent);
      }
   }

   private static void logBytesReceived(StringBuilder b, Request request, Response response) {
      b.append(Request.getContentBytesRead(request));
   }

   private static void logBytesReceivedCLF(StringBuilder b, Request request, Response response) {
      long received = Request.getContentBytesRead(request);
      if (received == 0L) {
         b.append('-');
      } else {
         b.append(received);
      }
   }

   private static void logBytesTransferred(StringBuilder b, Request request, Response response) {
      b.append(Request.getContentBytesRead(request) + Response.getContentBytesWritten(response));
   }

   private static void logBytesTransferredCLF(StringBuilder b, Request request, Response response) {
      long transferred = Request.getContentBytesRead(request) + Response.getContentBytesWritten(response);
      if (transferred == 0L) {
         b.append('-');
      } else {
         b.append(transferred);
      }
   }

   private static void logRequestCookie(String arg, StringBuilder b, Request request, Response response) {
      List<HttpCookie> cookies = Request.getCookies(request);
      if (cookies != null) {
         for (HttpCookie c : cookies) {
            if (arg.equals(c.getName())) {
               b.append(c.getValue());
               return;
            }
         }
      }

      b.append('-');
   }

   private static void logRequestCookies(StringBuilder b, Request request, Response response) {
      List<HttpCookie> cookies = Request.getCookies(request);
      if (cookies != null && cookies.size() != 0) {
         for (int i = 0; i < cookies.size(); i++) {
            if (i != 0) {
               b.append(';');
            }

            b.append(cookies.get(i).getName());
            b.append('=');
            b.append(cookies.get(i).getValue());
         }
      } else {
         b.append('-');
      }
   }

   private static void logEnvironmentVar(String arg, StringBuilder b, Request request, Response response) {
      append(b, System.getenv(arg));
   }

   private static void logFilename(StringBuilder b, Request request, Response response) {
      CustomRequestLog.LogDetail logDetail = (CustomRequestLog.LogDetail)request.getAttribute(LOG_DETAIL);
      if (logDetail != null && logDetail.realPath != null) {
         b.append(logDetail.realPath);
      } else {
         Context context = request.getContext();
         Resource baseResource = context.getBaseResource();
         if (baseResource != null) {
            String fileName = baseResource.resolve(Request.getPathInContext(request)).getName();
            append(b, fileName);
         } else {
            b.append("-");
         }
      }
   }

   private static void logRequestProtocol(StringBuilder b, Request request, Response response) {
      append(b, request.getConnectionMetaData().getProtocol());
   }

   private static void logRequestHeader(String arg, StringBuilder b, Request request, Response response) {
      append(b, request.getHeaders().get(arg));
   }

   private static void logKeepAliveRequests(StringBuilder b, Request request, Response response) {
      long requests = request.getConnectionMetaData().getConnection().getMessagesIn();
      if (requests >= 0L) {
         b.append(requests);
      } else {
         b.append('-');
      }
   }

   private static void logRequestMethod(StringBuilder b, Request request, Response response) {
      append(b, request.getMethod());
   }

   private static void logResponseHeader(String arg, StringBuilder b, Request request, Response response) {
      append(b, response.getHeaders().get(arg));
   }

   private static void logQueryString(StringBuilder b, Request request, Response response) {
      append(b, "?" + request.getHttpURI().getQuery());
   }

   private static void logRequestFirstLine(StringBuilder b, Request request, Response response) {
      append(b, request.getMethod());
      b.append(" ");
      append(b, request.getHttpURI().getPathQuery());
      b.append(" ");
      append(b, request.getConnectionMetaData().getProtocol());
   }

   private static void logRequestHandler(StringBuilder b, Request request, Response response) {
      CustomRequestLog.LogDetail logDetail = (CustomRequestLog.LogDetail)request.getAttribute(LOG_DETAIL);
      append(b, logDetail == null ? null : logDetail.handlerName);
   }

   private static void logResponseStatus(StringBuilder b, Request request, Response response) {
      b.append(response.getStatus());
   }

   private static void logRequestTime(DateCache dateCache, StringBuilder b, Request request, Response response) {
      b.append('[');
      append(b, dateCache.format(Request.getTimeStamp(request)));
      b.append(']');
   }

   private static void logLatencyMicroseconds(StringBuilder b, Request request, Response response) {
      logLatency(b, request, TimeUnit.MICROSECONDS);
   }

   private static void logLatencyMilliseconds(StringBuilder b, Request request, Response response) {
      logLatency(b, request, TimeUnit.MILLISECONDS);
   }

   private static void logLatencySeconds(StringBuilder b, Request request, Response response) {
      logLatency(b, request, TimeUnit.SECONDS);
   }

   private static void logLatency(StringBuilder b, Request request, TimeUnit unit) {
      b.append(unit.convert(NanoTime.since(request.getBeginNanoTime()), TimeUnit.NANOSECONDS));
   }

   private static void logRequestAuthentication(StringBuilder b, Request request, Response response) {
      Request.AuthenticationState authenticationState = Request.getAuthenticationState(request);
      Principal userPrincipal = authenticationState == null ? null : authenticationState.getUserPrincipal();
      append(b, userPrincipal == null ? null : userPrincipal.getName());
   }

   private static void logRequestAuthenticationWithDeferred(StringBuilder b, Request request, Response response) {
      logRequestAuthentication(b, request, response);
   }

   private static void logUrlRequestPath(StringBuilder b, Request request, Response response) {
      append(b, request.getHttpURI().getPath());
   }

   private static void logConnectionStatus(StringBuilder b, Request request, Response response) {
      b.append((char)(response.isCompletedSuccessfully() ? (request.getConnectionMetaData().isPersistent() ? '+' : '-') : 'X'));
   }

   private static void logRequestTrailer(String arg, StringBuilder b, Request request, Response response) {
      HttpFields trailers = request.getTrailers();
      if (trailers != null) {
         append(b, trailers.get(arg));
      } else {
         b.append('-');
      }
   }

   private static void logResponseTrailer(String arg, StringBuilder b, Request request, Response response) {
      Supplier<HttpFields> supplier = response.getTrailersSupplier();
      HttpFields trailers = supplier == null ? null : supplier.get();
      if (trailers != null) {
         append(b, trailers.get(arg));
      } else {
         b.append('-');
      }
   }

   public static record LogDetail(String handlerName, String realPath) {
   }

   private static class Token {
      public final String code;
      public final String arg;
      public final List<Integer> modifiers;
      public final boolean negated;
      public final String literal;

      public Token(String code, String arg, List<Integer> modifiers, boolean negated) {
         this.code = code;
         this.arg = arg;
         this.modifiers = modifiers;
         this.negated = negated;
         this.literal = null;
      }

      public Token(String literal) {
         this.code = null;
         this.arg = null;
         this.modifiers = null;
         this.negated = false;
         this.literal = literal;
      }

      public boolean isLiteralString() {
         return this.literal != null;
      }

      public boolean isPercentCode() {
         return this.code != null;
      }
   }
}
