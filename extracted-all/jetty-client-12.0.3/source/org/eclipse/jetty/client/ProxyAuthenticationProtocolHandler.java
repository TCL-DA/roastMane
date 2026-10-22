package org.eclipse.jetty.client;

import java.net.URI;
import org.eclipse.jetty.client.transport.HttpDestination;
import org.eclipse.jetty.http.HttpHeader;

public class ProxyAuthenticationProtocolHandler extends AuthenticationProtocolHandler {
   public static final String NAME = "proxy-authenticate";
   private static final String ATTRIBUTE = ProxyAuthenticationProtocolHandler.class.getName() + ".attribute";

   public ProxyAuthenticationProtocolHandler(HttpClient client) {
      this(client, 16384);
   }

   public ProxyAuthenticationProtocolHandler(HttpClient client, int maxContentLength) {
      super(client, maxContentLength);
   }

   @Override
   public String getName() {
      return "proxy-authenticate";
   }

   @Override
   public boolean accept(Request request, Response response) {
      return response.getStatus() == 407;
   }

   @Override
   protected HttpHeader getAuthenticateHeader() {
      return HttpHeader.PROXY_AUTHENTICATE;
   }

   @Override
   protected HttpHeader getAuthorizationHeader() {
      return HttpHeader.PROXY_AUTHORIZATION;
   }

   @Override
   protected URI getAuthenticationURI(Request request) {
      HttpDestination destination = (HttpDestination)this.getHttpClient().resolveDestination(request);
      ProxyConfiguration.Proxy proxy = destination.getProxy();
      return proxy != null ? proxy.getURI() : request.getURI();
   }

   @Override
   protected String getAuthenticationAttribute() {
      return ATTRIBUTE;
   }
}
