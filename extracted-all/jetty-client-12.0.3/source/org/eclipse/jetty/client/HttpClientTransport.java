package org.eclipse.jetty.client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import org.eclipse.jetty.io.ClientConnectionFactory;

public interface HttpClientTransport extends ClientConnectionFactory {
   String HTTP_DESTINATION_CONTEXT_KEY = "org.eclipse.jetty.client.destination";
   String HTTP_CONNECTION_PROMISE_CONTEXT_KEY = "org.eclipse.jetty.client.connection.promise";

   void setHttpClient(HttpClient var1);

   Origin newOrigin(Request var1);

   Destination newDestination(Origin var1);

   @Deprecated
   void connect(InetSocketAddress var1, Map<String, Object> var2);

   default void connect(SocketAddress address, Map<String, Object> context) {
      if (address instanceof InetSocketAddress) {
         this.connect((InetSocketAddress)address, context);
      } else {
         throw new UnsupportedOperationException("Unsupported SocketAddress " + address);
      }
   }

   ConnectionPool.Factory getConnectionPoolFactory();

   void setConnectionPoolFactory(ConnectionPool.Factory var1);
}
