package org.eclipse.jetty.server.handler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.jetty.http.HttpException;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.http.QuotedQualityCSV;
import org.eclipse.jetty.http.HttpFields.Mutable;
import org.eclipse.jetty.http.MimeTypes.Type;
import org.eclipse.jetty.io.ByteBufferOutputStream;
import org.eclipse.jetty.io.Retainable;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.Callback.Nested;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public class ErrorHandler implements Request.Handler {
   private static final Logger LOG = LoggerFactory.getLogger(ErrorHandler.class);
   public static final String ERROR_STATUS = "org.eclipse.jetty.server.error_status";
   public static final String ERROR_MESSAGE = "org.eclipse.jetty.server.error_message";
   public static final String ERROR_EXCEPTION = "org.eclipse.jetty.server.error_exception";
   public static final String ERROR_CONTEXT = "org.eclipse.jetty.server.error_context";
   public static final Set<String> ERROR_METHODS = Set.of("GET", "POST", "HEAD");
   public static final HttpField ERROR_CACHE_CONTROL = new PreEncodedHttpField(HttpHeader.CACHE_CONTROL, "must-revalidate,no-cache,no-store");
   boolean _showStacks = true;
   boolean _showMessageInTitle = true;
   HttpField _cacheControl = new PreEncodedHttpField(HttpHeader.CACHE_CONTROL, "must-revalidate,no-cache,no-store");

   public boolean errorPageForMethod(String method) {
      return ERROR_METHODS.contains(method);
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("handle({}, {}, {})", new Object[]{request, response, callback});
      }

      if (this._cacheControl != null) {
         response.getHeaders().put(this._cacheControl);
      }

      int code = response.getStatus();
      String message = (String)request.getAttribute("org.eclipse.jetty.server.error_message");
      Throwable cause = (Throwable)request.getAttribute("org.eclipse.jetty.server.error_exception");
      if (cause instanceof HttpException httpException) {
         code = httpException.getCode();
         response.setStatus(code);
         if (message == null) {
            message = httpException.getReason();
         }
      }

      if (this.errorPageForMethod(request.getMethod()) && !HttpStatus.hasNoBody(code)) {
         if (message == null) {
            message = cause == null ? HttpStatus.getMessage(code) : cause.toString();
         }

         try {
            this.generateResponse(request, response, code, message, cause, callback);
         } catch (Throwable var8) {
            LOG.warn("Failure whilst generate error response", var8);
         }
      } else {
         callback.succeeded();
      }

      return true;
   }

   protected void generateResponse(Request request, Response response, int code, String message, Throwable cause, Callback callback) throws IOException {
      List<String> acceptable = request.getHeaders().getQualityCSV(HttpHeader.ACCEPT, QuotedQualityCSV.MOST_SPECIFIC_MIME_ORDERING);
      if (acceptable.isEmpty()) {
         if (request.getHeaders().contains(HttpHeader.ACCEPT)) {
            callback.succeeded();
            return;
         }

         acceptable = Collections.singletonList(Type.TEXT_HTML.asString());
      }

      List<Charset> charsets = request.getHeaders().getQualityCSV(HttpHeader.ACCEPT_CHARSET).stream().map(s -> {
         try {
            return "*".equals(s) ? StandardCharsets.UTF_8 : Charset.forName(s);
         } catch (Throwable var2x) {
            return null;
         }
      }).filter(Objects::nonNull).collect(Collectors.toList());
      if (charsets.isEmpty()) {
         charsets = List.of(StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8);
         if (request.getHeaders().contains(HttpHeader.ACCEPT_CHARSET)) {
            callback.succeeded();
            return;
         }
      }

      for (String mimeType : acceptable) {
         if (this.generateAcceptableResponse(request, response, callback, mimeType, charsets, code, message, cause)) {
            return;
         }
      }

      callback.succeeded();
   }

   protected boolean generateAcceptableResponse(
      Request request, Response response, Callback callback, String contentType, List<Charset> charsets, int code, String message, Throwable cause
   ) throws IOException {
      Type type;
      Charset charset;
      switch (contentType) {
         case "text/html":
         case "text/*":
         case "*/*":
            type = Type.TEXT_HTML;
            charset = charsets.stream().findFirst().orElse(StandardCharsets.ISO_8859_1);
            break;
         case "text/json":
         case "application/json":
            if (charsets.contains(StandardCharsets.UTF_8)) {
               charset = StandardCharsets.UTF_8;
            } else {
               if (!charsets.contains(StandardCharsets.ISO_8859_1)) {
                  return false;
               }

               charset = StandardCharsets.ISO_8859_1;
            }

            type = Type.TEXT_JSON.is(contentType) ? Type.TEXT_JSON : Type.APPLICATION_JSON;
            break;
         case "text/plain":
            type = Type.TEXT_PLAIN;
            charset = charsets.stream().findFirst().orElse(StandardCharsets.ISO_8859_1);
            break;
         default:
            return false;
      }

      int bufferSize = request.getConnectionMetaData().getHttpConfiguration().getOutputBufferSize();
      bufferSize = Math.min(8192, bufferSize);
      RetainableByteBuffer buffer = request.getComponents().getByteBufferPool().acquire(bufferSize, false);

      try {
         boolean showStacks = this.isShowStacks();

         while (true) {
            try {
               buffer.clear();
               ByteBufferOutputStream out = new ByteBufferOutputStream(buffer.getByteBuffer());
               PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, charset));
               switch (type) {
                  case TEXT_HTML:
                     this.writeErrorHtml(request, writer, charset, code, message, cause, showStacks);
                     break;
                  case TEXT_JSON:
                  case APPLICATION_JSON:
                     this.writeErrorJson(request, writer, code, message, cause, showStacks);
                     break;
                  case TEXT_PLAIN:
                     this.writeErrorPlain(request, writer, code, message, cause, showStacks);
                     break;
                  default:
                     throw new IllegalStateException();
               }

               writer.flush();
               break;
            } catch (BufferOverflowException var16) {
               if (!showStacks) {
                  if (LOG.isDebugEnabled()) {
                     LOG.warn("Error page too large: >{} {} {} {}", new Object[]{bufferSize, code, message, request, var16});
                  } else {
                     LOG.warn("Error page too large: >{} {} {} {}", new Object[]{bufferSize, code, message, request});
                  }
                  break;
               }

               if (LOG.isDebugEnabled()) {
                  LOG.debug("Disable stacks for " + var16.toString());
               }

               showStacks = false;
            }
         }

         if (!buffer.hasRemaining()) {
            buffer.release();
            callback.succeeded();
            return true;
         } else {
            response.getHeaders().put(type.getContentTypeField(charset));
            response.write(true, buffer.getByteBuffer(), new ErrorHandler.WriteErrorCallback(callback, buffer));
            return true;
         }
      } catch (Throwable var17) {
         buffer.release();
         throw var17;
      }
   }

   protected void writeErrorHtml(Request request, Writer writer, Charset charset, int code, String message, Throwable cause, boolean showStacks) throws IOException {
      if (message == null) {
         message = HttpStatus.getMessage(code);
      }

      writer.write("<html>\n<head>\n");
      this.writeErrorHtmlMeta(request, writer, charset);
      this.writeErrorHtmlHead(request, writer, code, message);
      writer.write("</head>\n<body>\n");
      this.writeErrorHtmlBody(request, writer, code, message, cause, showStacks);
      writer.write("\n</body>\n</html>\n");
   }

   protected void writeErrorHtmlMeta(Request request, Writer writer, Charset charset) throws IOException {
      writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=");
      writer.write(charset.name());
      writer.write("\"/>\n");
   }

   protected void writeErrorHtmlHead(Request request, Writer writer, int code, String message) throws IOException {
      writer.write("<title>Error ");
      String status = Integer.toString(code);
      writer.write(status);
      if (this.isShowMessageInTitle() && message != null && !message.equals(status)) {
         writer.write(32);
         writer.write(StringUtil.sanitizeXmlString(message));
      }

      writer.write("</title>\n");
   }

   protected void writeErrorHtmlBody(Request request, Writer writer, int code, String message, Throwable cause, boolean showStacks) throws IOException {
      String uri = request.getHttpURI().toString();
      this.writeErrorHtmlMessage(request, writer, code, message, cause, uri);
      if (showStacks) {
         this.writeErrorHtmlStacks(request, writer);
      }

      request.getConnectionMetaData().getHttpConfiguration().writePoweredBy(writer, "<hr/>", "<hr/>\n");
   }

   protected void writeErrorHtmlMessage(Request request, Writer writer, int code, String message, Throwable cause, String uri) throws IOException {
      writer.write("<h2>HTTP ERROR ");
      String status = Integer.toString(code);
      writer.write(status);
      if (message != null && !message.equals(status)) {
         writer.write(32);
         writer.write(StringUtil.sanitizeXmlString(message));
      }

      writer.write("</h2>\n");
      writer.write("<table>\n");
      this.htmlRow(writer, "URI", uri);
      this.htmlRow(writer, "STATUS", status);
      this.htmlRow(writer, "MESSAGE", message);

      while (cause != null) {
         this.htmlRow(writer, "CAUSED BY", cause);
         cause = cause.getCause();
      }

      writer.write("</table>\n");
   }

   private void htmlRow(Writer writer, String tag, Object value) throws IOException {
      writer.write("<tr><th>");
      writer.write(tag);
      writer.write(":</th><td>");
      if (value == null) {
         writer.write("-");
      } else {
         writer.write(StringUtil.sanitizeXmlString(value.toString()));
      }

      writer.write("</td></tr>\n");
   }

   protected void writeErrorPlain(Request request, PrintWriter writer, int code, String message, Throwable cause, boolean showStacks) {
      writer.write("HTTP ERROR ");
      writer.write(Integer.toString(code));
      if (this.isShowMessageInTitle()) {
         writer.write(32);
         writer.write(StringUtil.sanitizeXmlString(message));
      }

      writer.write("\n");
      writer.printf("URI: %s%n", request.getHttpURI());
      writer.printf("STATUS: %s%n", code);
      writer.printf("MESSAGE: %s%n", message);

      while (cause != null) {
         writer.printf("CAUSED BY %s%n", cause);
         if (showStacks) {
            cause.printStackTrace(writer);
         }

         cause = cause.getCause();
      }
   }

   protected void writeErrorJson(Request request, PrintWriter writer, int code, String message, Throwable cause, boolean showStacks) {
      Map<String, String> json = new HashMap<>();
      json.put("url", request.getHttpURI().toString());
      json.put("status", Integer.toString(code));
      json.put("message", message);
      int c = 0;

      while (cause != null) {
         json.put("cause" + c++, cause.toString());
         cause = cause.getCause();
      }

      writer.append(
         json.entrySet()
            .stream()
            .map(e -> HttpField.NAME_VALUE_TOKENIZER.quote(e.getKey()) + ":" + HttpField.NAME_VALUE_TOKENIZER.quote(StringUtil.sanitizeXmlString(e.getValue())))
            .collect(Collectors.joining(",\n", "{\n", "\n}"))
      );
   }

   protected void writeErrorHtmlStacks(Request request, Writer writer) throws IOException {
      Throwable th = (Throwable)request.getAttribute("org.eclipse.jetty.server.error_exception");
      if (th != null) {
         writer.write("<h3>Caused by:</h3><pre>");

         try (
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
         ) {
            th.printStackTrace(pw);
            pw.flush();
            this.write(writer, sw.getBuffer().toString());
         }

         writer.write("</pre>\n");
      }
   }

   public ByteBuffer badMessageError(int status, String reason, Mutable fields) {
      if (reason == null) {
         reason = HttpStatus.getMessage(status);
      }

      if (HttpStatus.hasNoBody(status)) {
         return BufferUtil.EMPTY_BUFFER;
      } else {
         fields.put(HttpHeader.CONTENT_TYPE, Type.TEXT_HTML_8859_1.asString());
         return BufferUtil.toBuffer("<h1>Bad Message " + status + "</h1><pre>reason: " + reason + "</pre>");
      }
   }

   @ManagedAttribute("The value of the Cache-Control response header")
   public String getCacheControl() {
      return this._cacheControl == null ? null : this._cacheControl.getValue();
   }

   public void setCacheControl(String cacheControl) {
      this._cacheControl = cacheControl == null ? null : new PreEncodedHttpField(HttpHeader.CACHE_CONTROL, cacheControl);
   }

   @ManagedAttribute("Whether the error page shows the stack trace")
   public boolean isShowStacks() {
      return this._showStacks;
   }

   public void setShowStacks(boolean showStacks) {
      this._showStacks = showStacks;
   }

   @ManagedAttribute("Whether the error message is shown in the error page title")
   public boolean isShowMessageInTitle() {
      return this._showMessageInTitle;
   }

   public void setShowMessageInTitle(boolean showMessageInTitle) {
      this._showMessageInTitle = showMessageInTitle;
   }

   protected void write(Writer writer, String string) throws IOException {
      if (string != null) {
         writer.write(StringUtil.sanitizeXmlString(string));
      }
   }

   public static Request.Handler getErrorHandler(Server server, ContextHandler context) {
      Request.Handler errorHandler = null;
      if (context != null) {
         errorHandler = context.getErrorHandler();
      }

      if (errorHandler == null && server != null) {
         errorHandler = server.getErrorHandler();
      }

      return errorHandler;
   }

   public static class ErrorRequest extends Request.Wrapper {
      private final int _status;
      private final String _message;
      private final Throwable _cause;

      public ErrorRequest(Request request, int status, String message, Throwable cause) {
         super(request);
         this._status = status;
         this._message = message;
         this._cause = cause;
      }

      @Override
      public Chunk read() {
         return Chunk.EOF;
      }

      @Override
      public void demand(Runnable demandCallback) {
         demandCallback.run();
      }

      public Object getAttribute(String name) {
         return switch (name) {
            case "org.eclipse.jetty.server.error_message" -> this._message;
            case "org.eclipse.jetty.server.error_exception" -> this._cause;
            case "org.eclipse.jetty.server.error_status" -> this._status;
            default -> super.getAttribute(name);
         };
      }

      public Set<String> getAttributeNameSet() {
         Set<String> names = new HashSet<>(super.getAttributeNameSet());
         if (this._message != null) {
            names.add("org.eclipse.jetty.server.error_message");
         }

         if (this._status > 0) {
            names.add("org.eclipse.jetty.server.error_status");
         }

         if (this._cause != null) {
            names.add("org.eclipse.jetty.server.error_exception");
         }

         return names;
      }

      @Override
      public String toString() {
         return "%s@%x:%s".formatted(this.getClass().getSimpleName(), this.hashCode(), this.getWrapped());
      }
   }

   private static class WriteErrorCallback extends Nested {
      private final Retainable _retainable;

      public WriteErrorCallback(Callback callback, Retainable retainable) {
         super(callback);
         this._retainable = retainable;
      }

      public void completed() {
         this._retainable.release();
      }
   }
}
