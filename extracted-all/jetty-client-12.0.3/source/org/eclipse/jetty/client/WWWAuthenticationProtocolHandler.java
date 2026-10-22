package org.eclipse.jetty.client;

import java.net.URI;
import org.eclipse.jetty.http.HttpHeader;

public class WWWAuthenticationProtocolHandler extends AuthenticationProtocolHandler {
   public static final String NAME = "www-authenticate";
   private static final String ATTRIBUTE = WWWAuthenticationProtocolHandler.class.getName() + ".attribute";

   public WWWAuthenticationProtocolHandler(HttpClient client) {
      this(client, 16384);
   }

   public WWWAuthenticationProtocolHandler(HttpClient client, int maxContentLength) {
      super(client, maxContentLength);
   }

   @Override
   public String getName() {
      return "www-authenticate";
   }

   @Override
   public boolean accept(Request request, Response response) {
      return response.getStatus() == 401;
   }

   @Override
   protected HttpHeader getAuthenticateHeader() {
      return HttpHeader.WWW_AUTHENTICATE;
   }

   @Override
   protected HttpHeader getAuthorizationHeader() {
      return HttpHeader.AUTHORIZATION;
   }

   @Override
   protected URI getAuthenticationURI(Request request) {
      return request.getURI();
   }

   @Override
   protected String getAuthenticationAttribute() {
      return ATTRIBUTE;
   }
}
