package org.eclipse.jetty.alpn.client;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLEngine;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.NegotiatingClientConnection;

public class ALPNClientConnection extends NegotiatingClientConnection {
   private final List<String> protocols;

   public ALPNClientConnection(
      EndPoint endPoint, Executor executor, ClientConnectionFactory connectionFactory, SSLEngine sslEngine, Map<String, Object> context, List<String> protocols
   ) {
      super(endPoint, executor, sslEngine, connectionFactory, context);
      this.protocols = protocols;
   }

   public List<String> getProtocols() {
      return this.protocols;
   }

   public void selected(String protocol) {
      this.completed(protocol);
   }
}
