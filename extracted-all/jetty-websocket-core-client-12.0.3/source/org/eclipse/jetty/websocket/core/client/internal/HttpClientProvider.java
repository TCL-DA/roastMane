package org.eclipse.jetty.websocket.core.client.internal;

import org.eclipse.jetty.client.HttpClient;

public interface HttpClientProvider {
   static HttpClient get() {
      HttpClientProvider xmlProvider = new XmlHttpClientProvider();
      HttpClient client = xmlProvider.newHttpClient();
      return client != null ? client : newDefaultHttpClient();
   }

   private static HttpClient newDefaultHttpClient() {
      return new HttpClient();
   }

   default HttpClient newHttpClient() {
      return newDefaultHttpClient();
   }
}
