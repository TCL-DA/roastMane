package org.eclipse.jetty.security.authentication;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.security.AuthenticationState;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.security.UserIdentity;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

public class BasicAuthenticator extends LoginAuthenticator {
   private Charset _charset;

   public Charset getCharset() {
      return this._charset;
   }

   public void setCharset(Charset charset) {
      this._charset = charset;
   }

   @Override
   public String getAuthenticationType() {
      return "BASIC";
   }

   @Override
   public AuthenticationState validateRequest(Request req, Response res, Callback callback) throws ServerAuthException {
      String credentials = req.getHeaders().get(HttpHeader.AUTHORIZATION);
      if (credentials != null) {
         int space = credentials.indexOf(32);
         if (space > 0) {
            String method = credentials.substring(0, space);
            if ("Basic".equalsIgnoreCase(method)) {
               credentials = credentials.substring(space + 1);
               Charset charset = this.getCharset();
               if (charset == null) {
                  charset = StandardCharsets.ISO_8859_1;
               }

               credentials = new String(Base64.getDecoder().decode(credentials), charset);
               int i = credentials.indexOf(58);
               if (i > 0) {
                  String username = credentials.substring(0, i);
                  String password = credentials.substring(i + 1);
                  UserIdentity user = this.login(username, password, req, res);
                  if (user != null) {
                     return new LoginAuthenticator.UserAuthenticationSucceeded(this.getAuthenticationType(), user);
                  }
               }
            }
         }
      }

      if (res.isCommitted()) {
         return null;
      } else {
         String value = "Basic realm=\"" + this._loginService.getName() + "\"";
         Charset charsetx = this.getCharset();
         if (charsetx != null) {
            value = value + ", charset=\"" + charsetx.name() + "\"";
         }

         res.getHeaders().put(HttpHeader.WWW_AUTHENTICATE.asString(), value);
         Response.writeError(req, res, callback, 401);
         return AuthenticationState.CHALLENGE;
      }
   }

   public static String authorization(String user, String password) {
      return "Basic " + Base64.getEncoder().encodeToString((user + ":" + password).getBytes(StandardCharsets.ISO_8859_1));
   }
}
