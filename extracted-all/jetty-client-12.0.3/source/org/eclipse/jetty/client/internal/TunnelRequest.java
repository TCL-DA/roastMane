package org.eclipse.jetty.client.internal;

import java.net.URI;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.transport.HttpConversation;
import org.eclipse.jetty.client.transport.HttpRequest;
import org.eclipse.jetty.http.HttpMethod;

public class TunnelRequest extends HttpRequest {
   private final URI proxyURI;

   public TunnelRequest(HttpClient client, URI proxyURI) {
      this(client, new HttpConversation(), proxyURI);
   }

   private TunnelRequest(HttpClient client, HttpConversation conversation, URI proxyURI) {
      super(client, conversation, proxyURI);
      this.proxyURI = proxyURI;
      this.method(HttpMethod.CONNECT);
   }

   @Override
   protected HttpRequest copyInstance(URI newURI) {
      return new TunnelRequest(this.getHttpClient(), this.getConversation(), newURI);
   }

   @Override
   public URI getURI() {
      return this.proxyURI;
   }
}
