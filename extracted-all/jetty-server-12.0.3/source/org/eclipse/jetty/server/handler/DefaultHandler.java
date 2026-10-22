package org.eclipse.jetty.server.handler;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;
import org.eclipse.jetty.http.DateGenerator;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.http.MimeTypes.Type;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHandler extends Handler.Abstract {
   private static final Logger LOG = LoggerFactory.getLogger(DefaultHandler.class);
   private final long _faviconModifiedMs = System.currentTimeMillis() / 1000L * 1000L;
   private final HttpField _faviconModified = new PreEncodedHttpField(HttpHeader.LAST_MODIFIED, DateGenerator.formatDate(this._faviconModifiedMs));
   private ByteBuffer _favicon;
   private boolean _serveFavIcon = true;
   private boolean _showContexts = true;

   public DefaultHandler() {
      this(true, true);
   }

   public DefaultHandler(@Name("serveFavIcon") boolean serveFavIcon, @Name("showContexts") boolean showContexts) {
      super(InvocationType.NON_BLOCKING);
      this._serveFavIcon = serveFavIcon;
      this._showContexts = showContexts;
   }

   @Override
   public void setServer(Server server) {
      super.setServer(server);
      if (server != null) {
         this.initFavIcon();
      }
   }

   private void initFavIcon() {
      if (this._favicon == null) {
         Server server = Objects.requireNonNull(this.getServer());
         byte[] favbytes = null;

         try {
            Resource faviconRes = server.getDefaultFavicon();
            if (faviconRes != null) {
               try (InputStream is = faviconRes.newInputStream()) {
                  favbytes = IO.readBytes(is);
               }
            }
         } catch (Exception var14) {
            LOG.warn("Unable to find default favicon", var14);
         } finally {
            this._favicon = BufferUtil.toBuffer(favbytes);
         }
      }
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      String method = request.getMethod();
      if (this.isServeFavIcon() && this._favicon != null && HttpMethod.GET.is(method) && Request.getPathInContext(request).equals("/favicon.ico")) {
         ByteBuffer content = BufferUtil.EMPTY_BUFFER;
         if (this._faviconModifiedMs > 0L && request.getHeaders().getDateField(HttpHeader.IF_MODIFIED_SINCE) == this._faviconModifiedMs) {
            response.setStatus(304);
         } else {
            response.setStatus(200);
            response.getHeaders().put(HttpHeader.CONTENT_TYPE, "image/x-icon");
            response.getHeaders().put(HttpHeader.CONTENT_LENGTH, (long)this._favicon.remaining());
            response.getHeaders().add(this._faviconModified);
            response.getHeaders().put(HttpHeader.CACHE_CONTROL.toString(), "max-age=360000,public");
            content = this._favicon.slice();
         }

         response.write(true, content, callback);
         return true;
      } else if (this.isShowContexts() && HttpMethod.GET.is(method) && Request.getPathInContext(request).equals("/")) {
         response.setStatus(404);
         response.getHeaders().put(HttpHeader.CONTENT_TYPE, Type.TEXT_HTML_UTF_8.toString());

         boolean var19;
         try (
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
         ) {
            writer.append("<!DOCTYPE html>\n");
            writer.append("<html lang=\"en\">\n<head>\n");
            writer.append("<title>Error 404 - Not Found</title>\n");
            writer.append("<meta charset=\"utf-8\">\n");
            writer.append(
               "<style>body { font-family: sans-serif; } table, td { border: 1px solid #333; } td, th { padding: 5px; } thead, tfoot { background-color: #333; color: #fff; } </style>\n"
            );
            writer.append("</head>\n<body>\n");
            writer.append("<h2>Error 404 - Not Found.</h2>\n");
            writer.append("<p>No context on this server matched or handled this request.</p>\n");
            writer.append("<p>Contexts known to this server are:</p>\n");
            writer.append("<table class=\"contexts\"><thead><tr>");
            writer.append("<th>Context Path</th>");
            writer.append("<th>Display Name</th>");
            writer.append("<th>Status</th>");
            writer.append("<th>LifeCycle</th>");
            writer.append("</tr></thead><tbody>\n");
            Server server = this.getServer();

            for (ContextHandler context : server == null ? Collections.emptyList() : server.getDescendants(ContextHandler.class)) {
               writer.append("<tr><td>");
               String contextPath = context.getContextPath();
               String href = URIUtil.encodePath(contextPath);
               if (contextPath.length() > 1 && !contextPath.endsWith("/")) {
                  href = href + "/";
               }

               if (context.isRunning()) {
                  writer.append("<a href=\"").append(href).append("\">");
               }

               writer.append(StringUtil.replace(contextPath, "%", "&#37;"));
               if (context.isRunning()) {
                  writer.append("</a>");
               }

               writer.append("</td><td>");
               if (StringUtil.isNotBlank(context.getDisplayName())) {
                  writer.append(StringUtil.sanitizeXmlString(context.getDisplayName()));
               }

               writer.append("&nbsp;</td><td>");
               if (context.isAvailable()) {
                  writer.append("Available");
               } else {
                  writer.append("<em>Not</em> Available");
               }

               writer.append("</td><td>");
               writer.append(context.getState());
               writer.append("</td></tr>\n");
            }

            writer.append("</tbody></table><hr/>\n");
            writer.append("<a href=\"https://eclipse.org/jetty\"><img alt=\"icon\" src=\"/favicon.ico\"/></a>&nbsp;");
            writer.append("<a href=\"https://eclipse.org/jetty\">Powered by Eclipse Jetty:// Server</a><hr/>\n");
            writer.append("</body>\n</html>\n");
            writer.flush();
            ByteBuffer content = BufferUtil.toBuffer(outputStream.toByteArray());
            response.getHeaders().put(HttpHeader.CONTENT_LENGTH, (long)content.remaining());
            response.write(true, content, callback);
            var19 = true;
         }

         return var19;
      } else {
         Response.writeError(request, response, callback, 404, null);
         return true;
      }
   }

   @ManagedAttribute("True if the favicon.ico is served")
   public boolean isServeFavIcon() {
      return this._serveFavIcon;
   }

   public void setServeFavIcon(boolean serveFavIcon) {
      this._serveFavIcon = serveFavIcon;
   }

   @ManagedAttribute("True if the contexts should be shown in the default 404 page")
   public boolean isShowContexts() {
      return this._showContexts;
   }

   public void setShowContexts(boolean show) {
      this._showContexts = show;
   }

   @Override
   public String toString() {
      String name = TypeUtil.toShortName(this.getClass());
      return String.format("%s@%x{showContext=%b,favIcon=%b,%s}", name, this.hashCode(), this._showContexts, this._serveFavIcon, this.getState());
   }
}
