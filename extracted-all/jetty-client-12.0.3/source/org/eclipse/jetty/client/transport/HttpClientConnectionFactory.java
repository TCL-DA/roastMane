package org.eclipse.jetty.client.transport;

import java.util.List;
import java.util.Map;
import org.eclipse.jetty.client.transport.internal.HttpConnectionOverHTTP;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.ClientConnectionFactory.Info;

public class HttpClientConnectionFactory implements ClientConnectionFactory {
   public static final Info HTTP11 = new HttpClientConnectionFactory.HTTP11(new HttpClientConnectionFactory());

   public Connection newConnection(EndPoint endPoint, Map<String, Object> context) {
      HttpConnectionOverHTTP connection = new HttpConnectionOverHTTP(endPoint, context);
      return this.customize(connection, context);
   }

   private static class HTTP11 extends Info {
      private static final List<String> protocols = List.of("http/1.1");

      private HTTP11(ClientConnectionFactory factory) {
         super(factory);
      }

      public List<String> getProtocols(boolean secure) {
         return protocols;
      }

      public String toString() {
         return String.format("%s@%x%s", this.getClass().getSimpleName(), this.hashCode(), protocols);
      }
   }
}
