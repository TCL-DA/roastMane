package org.eclipse.jetty.server;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.eclipse.jetty.http.CookieCache;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.io.Content.Source;
import org.eclipse.jetty.server.internal.HttpChannelState;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Fields;
import org.eclipse.jetty.util.HostPort;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.UrlEncoded;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.thread.Invocable;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;

public interface Request extends Attributes, Source {
   String CACHE_ATTRIBUTE = Request.class.getCanonicalName() + ".CookieCache";
   String COOKIE_ATTRIBUTE = Request.class.getCanonicalName() + ".Cookies";
   List<Locale> DEFAULT_LOCALES = List.of(Locale.getDefault());

   String getId();

   Components getComponents();

   ConnectionMetaData getConnectionMetaData();

   String getMethod();

   HttpURI getHttpURI();

   Context getContext();

   static String getContextPath(Request request) {
      return request.getContext().getContextPath();
   }

   static String getPathInContext(Request request) {
      return request.getContext().getPathInContext(request.getHttpURI().getCanonicalPath());
   }

   HttpFields getHeaders();

   void demand(Runnable var1);

   HttpFields getTrailers();

   static long getTimeStamp(Request request) {
      return System.currentTimeMillis() - NanoTime.millisSince(request.getHeadersNanoTime());
   }

   long getBeginNanoTime();

   long getHeadersNanoTime();

   boolean isSecure();

   Chunk read();

   boolean consumeAvailable();

   default void push(org.eclipse.jetty.http.MetaData.Request resource) {
      throw new UnsupportedOperationException();
   }

   void addIdleTimeoutListener(Predicate<TimeoutException> var1);

   void addFailureListener(Consumer<Throwable> var1);

   TunnelSupport getTunnelSupport();

   void addHttpStreamWrapper(Function<HttpStream, HttpStream> var1);

   Session getSession(boolean var1);

   static Request asReadOnly(final Request request) {
      return new Request.Wrapper() {
         @Override
         public void addHttpStreamWrapper(Function<HttpStream, HttpStream> wrapper) {
            throw new UnsupportedOperationException();
         }

         @Override
         public Chunk read() {
            throw new UnsupportedOperationException();
         }

         @Override
         public void demand(Runnable demandCallback) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void fail(Throwable failure) {
            throw new UnsupportedOperationException();
         }
      };
   }

   static String getLocalAddr(Request request) {
      if (request == null) {
         return null;
      } else {
         SocketAddress local = request.getConnectionMetaData().getLocalSocketAddress();
         if (local instanceof InetSocketAddress) {
            InetAddress address = ((InetSocketAddress)local).getAddress();
            String result = address == null ? ((InetSocketAddress)local).getHostString() : address.getHostAddress();
            return HostPort.normalizeHost(result);
         } else {
            return local == null ? null : local.toString();
         }
      }
   }

   static int getLocalPort(Request request) {
      if (request == null) {
         return -1;
      } else {
         SocketAddress local = request.getConnectionMetaData().getLocalSocketAddress();
         return local instanceof InetSocketAddress ? ((InetSocketAddress)local).getPort() : -1;
      }
   }

   static String getRemoteAddr(Request request) {
      if (request == null) {
         return null;
      } else {
         SocketAddress remote = request.getConnectionMetaData().getRemoteSocketAddress();
         if (remote instanceof InetSocketAddress inetSocketAddress) {
            if (inetSocketAddress.isUnresolved()) {
               return inetSocketAddress.getHostString();
            } else {
               InetAddress address = inetSocketAddress.getAddress();
               String result = address == null ? inetSocketAddress.getHostString() : address.getHostAddress();
               return HostPort.normalizeHost(result);
            }
         } else {
            return remote == null ? null : remote.toString();
         }
      }
   }

   static int getRemotePort(Request request) {
      if (request == null) {
         return -1;
      } else {
         SocketAddress remote = request.getConnectionMetaData().getRemoteSocketAddress();
         return remote instanceof InetSocketAddress ? ((InetSocketAddress)remote).getPort() : -1;
      }
   }

   static String getServerName(Request request) {
      if (request == null) {
         return null;
      } else {
         HttpURI uri = request.getHttpURI();
         if (uri.hasAuthority()) {
            return HostPort.normalizeHost(uri.getHost());
         } else {
            HostPort authority = request.getConnectionMetaData().getServerAuthority();
            return authority != null ? authority.getHost() : null;
         }
      }
   }

   static int getServerPort(Request request) {
      if (request == null) {
         return -1;
      } else {
         HttpURI uri = request.getHttpURI();
         if (uri.hasAuthority() && uri.getPort() > 0) {
            return uri.getPort();
         } else {
            HostPort authority = request.getConnectionMetaData().getHttpConfiguration().getServerAuthority();
            if (authority != null && authority.getPort() > 0) {
               return authority.getPort();
            } else {
               HttpScheme scheme = (HttpScheme)HttpScheme.CACHE.get(request.getHttpURI().getScheme());
               if (scheme != null && scheme.getDefaultPort() > 0) {
                  return scheme.getDefaultPort();
               } else {
                  if (request.getConnectionMetaData().getLocalSocketAddress() instanceof InetSocketAddress inetSocketAddress && inetSocketAddress.getPort() > 0
                     )
                   {
                     return inetSocketAddress.getPort();
                  }

                  return -1;
               }
            }
         }
      }
   }

   static List<Locale> getLocales(Request request) {
      HttpFields fields = request.getHeaders();
      if (fields == null) {
         return DEFAULT_LOCALES;
      } else {
         List<String> acceptable = fields.getQualityCSV(HttpHeader.ACCEPT_LANGUAGE);

         return (List<Locale>)(switch (acceptable.size()) {
            case 0 -> DEFAULT_LOCALES;
            case 1 -> List.of(Locale.forLanguageTag(acceptable.get(0)));
            default -> {
               List<Locale> locales = acceptable.stream().map(Locale::forLanguageTag).toList();
               List<Locale> known = locales.stream().filter(MimeTypes::isKnownLocale).toList();
               if (known.size() == locales.size()) {
                  yield locales;
               } else {
                  List<Locale> unknown = locales.stream().filter(l -> !MimeTypes.isKnownLocale(l)).toList();
                  List<Locale> var6 = new ArrayList<>(known);
                  var6.addAll(unknown);
                  yield var6;
               }
            }
         });
      }
   }

   static InputStream asInputStream(Request request) {
      return Source.asInputStream(request);
   }

   static Charset getCharset(Request request) throws IllegalCharsetNameException, UnsupportedCharsetException {
      String contentType = request.getHeaders().get(HttpHeader.CONTENT_TYPE);
      return Objects.requireNonNullElse(request.getContext().getMimeTypes(), MimeTypes.DEFAULTS).getCharset(contentType);
   }

   static Fields extractQueryParameters(Request request) {
      String query = request.getHttpURI().getQuery();
      if (StringUtil.isBlank(query)) {
         return Fields.EMPTY;
      } else {
         Fields fields = new Fields(true);
         if (StringUtil.isNotBlank(query)) {
            UrlEncoded.decodeUtf8To(query, fields);
         }

         return fields;
      }
   }

   static Fields extractQueryParameters(Request request, Charset charset) {
      Fields fields = new Fields(true);
      String query = request.getHttpURI().getQuery();
      if (StringUtil.isNotBlank(query)) {
         UrlEncoded.decodeTo(query, fields::add, charset);
      }

      return fields;
   }

   static Fields getParameters(Request request) throws Exception {
      return getParametersAsync(request).get();
   }

   static CompletableFuture<Fields> getParametersAsync(Request request) {
      Fields queryFields = extractQueryParameters(request);
      CompletableFuture<Fields> contentFields = FormFields.from(request);
      return contentFields.thenApply(formFields -> Fields.combine(queryFields, formFields));
   }

   static List<HttpCookie> getCookies(Request request) {
      List<HttpCookie> cookies = (List<HttpCookie>)request.getAttribute(COOKIE_ATTRIBUTE);
      if (cookies != null) {
         return cookies;
      } else {
         CookieCache cookieCache = (CookieCache)request.getComponents().getCache().getAttribute(CACHE_ATTRIBUTE);
         if (cookieCache == null) {
            cookieCache = new CookieCache(request.getConnectionMetaData().getHttpConfiguration().getRequestCookieCompliance(), null);
            request.getComponents().getCache().setAttribute(CACHE_ATTRIBUTE, cookieCache);
         }

         cookies = cookieCache.getCookies(request.getHeaders());
         request.setAttribute(COOKIE_ATTRIBUTE, cookies);
         return cookies;
      }
   }

   static String toRedirectURI(Request request, String location) {
      if (!URIUtil.hasScheme(location) && !request.getConnectionMetaData().getHttpConfiguration().isRelativeRedirectAllowed()) {
         StringBuilder url = new StringBuilder(128);
         HttpURI uri = request.getHttpURI();
         URIUtil.appendSchemeHostPort(url, uri.getScheme(), getServerName(request), getServerPort(request));
         if (location.startsWith("/")) {
            location = URIUtil.normalizePathQuery(location);
         } else {
            String path = uri.getPath();
            String parent = path.endsWith("/") ? path : URIUtil.parentPath(path);
            location = URIUtil.normalizePathQuery(URIUtil.addEncodedPaths(parent, location));
            if (location != null && !location.startsWith("/")) {
               url.append('/');
            }
         }

         if (location == null) {
            throw new IllegalStateException("redirect path cannot be above root");
         }

         url.append(location);
         location = url.toString();
      }

      return location;
   }

   static Request serveAs(final Request request, final HttpURI uri) {
      if (request.getHttpURI().equals(uri)) {
         return request;
      } else {
         Request.ServeAs serveAs = as(request, Request.ServeAs.class);
         return (Request)(serveAs != null ? serveAs.wrap(request, uri) : new Request.Wrapper() {
            @Override
            public HttpURI getHttpURI() {
               return uri;
            }
         });
      }
   }

   static <T> T as(Request request, Class<T> type) {
      while (request != null) {
         if (type.isInstance(request)) {
            return (T)request;
         }

         request = request instanceof Request.Wrapper wrapper ? wrapper.getWrapped() : null;
      }

      return null;
   }

   static <T, R> R get(Request request, Class<T> type, Function<T, R> getter) {
      T t = as(request, type);
      return t == null ? null : getter.apply(t);
   }

   static Request unWrap(Request request) {
      while (request instanceof Request.Wrapper) {
         Request.Wrapper wrapped = (Request.Wrapper)request;
         request = wrapped.getWrapped();
      }

      return request;
   }

   static long getContentBytesRead(Request request) {
      return unWrap(request) instanceof HttpChannelState.ChannelRequest channelRequest ? channelRequest.getContentBytesRead() : -1L;
   }

   static HttpURI newHttpURIFrom(Request request, String newEncodedPathInContext) {
      return HttpURI.build(request.getHttpURI()).path(URIUtil.addPaths(getContextPath(request), newEncodedPathInContext)).asImmutable();
   }

   static Request.AuthenticationState getAuthenticationState(Request request) {
      Object var2 = request.getAttribute(Request.AuthenticationState.class.getName());
      return var2 instanceof Request.AuthenticationState ? (Request.AuthenticationState)var2 : null;
   }

   static void setAuthenticationState(Request request, Request.AuthenticationState state) {
      request.setAttribute(Request.AuthenticationState.class.getName(), state);
   }

   public interface AuthenticationState {
      default Principal getUserPrincipal() {
         return null;
      }
   }

   @ManagedObject
   @FunctionalInterface
   public interface Handler extends Invocable {
      boolean handle(Request var1, Response var2, Callback var3) throws Exception;

      @ManagedAttribute("The InvocationType of this Handler")
      default InvocationType getInvocationType() {
         return InvocationType.BLOCKING;
      }
   }

   public interface ServeAs extends Request {
      Request wrap(Request var1, HttpURI var2);
   }

   public static class Wrapper extends org.eclipse.jetty.util.Attributes.Wrapper implements Request {
      public Wrapper(Request wrapped) {
         super(wrapped);
      }

      @Override
      public String getId() {
         return this.getWrapped().getId();
      }

      @Override
      public Components getComponents() {
         return this.getWrapped().getComponents();
      }

      @Override
      public ConnectionMetaData getConnectionMetaData() {
         return this.getWrapped().getConnectionMetaData();
      }

      @Override
      public String getMethod() {
         return this.getWrapped().getMethod();
      }

      @Override
      public HttpURI getHttpURI() {
         return this.getWrapped().getHttpURI();
      }

      @Override
      public Context getContext() {
         return this.getWrapped().getContext();
      }

      @Override
      public HttpFields getHeaders() {
         return this.getWrapped().getHeaders();
      }

      @Override
      public HttpFields getTrailers() {
         return this.getWrapped().getTrailers();
      }

      @Override
      public long getBeginNanoTime() {
         return this.getWrapped().getBeginNanoTime();
      }

      @Override
      public long getHeadersNanoTime() {
         return this.getWrapped().getHeadersNanoTime();
      }

      @Override
      public boolean isSecure() {
         return this.getWrapped().isSecure();
      }

      public long getLength() {
         return this.getWrapped().getLength();
      }

      @Override
      public Chunk read() {
         return this.getWrapped().read();
      }

      @Override
      public boolean consumeAvailable() {
         return this.getWrapped().consumeAvailable();
      }

      @Override
      public void demand(Runnable demandCallback) {
         this.getWrapped().demand(demandCallback);
      }

      public void fail(Throwable failure) {
         this.getWrapped().fail(failure);
      }

      @Override
      public void push(org.eclipse.jetty.http.MetaData.Request resource) {
         this.getWrapped().push(resource);
      }

      @Override
      public void addIdleTimeoutListener(Predicate<TimeoutException> onIdleTimeout) {
         this.getWrapped().addIdleTimeoutListener(onIdleTimeout);
      }

      @Override
      public void addFailureListener(Consumer<Throwable> onFailure) {
         this.getWrapped().addFailureListener(onFailure);
      }

      @Override
      public TunnelSupport getTunnelSupport() {
         return this.getWrapped().getTunnelSupport();
      }

      @Override
      public void addHttpStreamWrapper(Function<HttpStream, HttpStream> wrapper) {
         this.getWrapped().addHttpStreamWrapper(wrapper);
      }

      @Override
      public Session getSession(boolean create) {
         return this.getWrapped().getSession(create);
      }

      public Request getWrapped() {
         return (Request)super.getWrapped();
      }
   }
}
