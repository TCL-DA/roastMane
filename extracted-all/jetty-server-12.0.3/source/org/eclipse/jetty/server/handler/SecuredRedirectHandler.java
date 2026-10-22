package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.URIUtil;

public class SecuredRedirectHandler extends Handler.Wrapper {
   private final int _redirectCode;

   public SecuredRedirectHandler() {
      this(null, 302);
   }

   public SecuredRedirectHandler(int code) {
      this(null, code);
   }

   public SecuredRedirectHandler(Handler handler) {
      this(handler, 302);
   }

   public SecuredRedirectHandler(Handler handler, int code) {
      super(handler);
      if (!HttpStatus.isRedirection(code)) {
         throw new IllegalArgumentException("Not a 3xx redirect code");
      } else {
         this._redirectCode = code;
      }
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      if (request.isSecure()) {
         return super.handle(request, response, callback);
      } else {
         HttpConfiguration httpConfig = request.getConnectionMetaData().getHttpConfiguration();
         int securePort = httpConfig.getSecurePort();
         if (securePort > 0) {
            String secureScheme = httpConfig.getSecureScheme();
            String url = URIUtil.newURI(
               secureScheme, Request.getServerName(request), securePort, request.getHttpURI().getPath(), request.getHttpURI().getQuery()
            );
            response.getHeaders().put(HttpHeader.LOCATION, url);
            response.setStatus(this._redirectCode);
            response.write(true, null, callback);
         } else {
            Response.writeError(request, response, callback, 403, "HttpConfiguration.securePort not configured");
         }

         return true;
      }
   }
}
