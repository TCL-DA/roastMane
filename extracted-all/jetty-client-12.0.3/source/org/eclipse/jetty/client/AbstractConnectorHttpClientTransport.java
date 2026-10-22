package org.eclipse.jetty.client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import org.eclipse.jetty.client.transport.HttpDestination;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject
public abstract class AbstractConnectorHttpClientTransport extends AbstractHttpClientTransport {
   private final ClientConnector connector;

   protected AbstractConnectorHttpClientTransport(ClientConnector connector) {
      this.connector = Objects.requireNonNull(connector);
      this.addBean(connector);
   }

   public ClientConnector getClientConnector() {
      return this.connector;
   }

   @ManagedAttribute(
      value = "The number of selectors",
      readonly = true
   )
   public int getSelectors() {
      return this.connector.getSelectors();
   }

   protected void doStart() throws Exception {
      HttpClient httpClient = this.getHttpClient();
      this.connector.setBindAddress(httpClient.getBindAddress());
      this.connector.setByteBufferPool(httpClient.getByteBufferPool());
      this.connector.setConnectBlocking(httpClient.isConnectBlocking());
      this.connector.setConnectTimeout(Duration.ofMillis(httpClient.getConnectTimeout()));
      this.connector.setExecutor(httpClient.getExecutor());
      this.connector.setIdleTimeout(Duration.ofMillis(httpClient.getIdleTimeout()));
      this.connector.setScheduler(httpClient.getScheduler());
      this.connector.setSslContextFactory(httpClient.getSslContextFactory());
      super.doStart();
   }

   @Override
   public void connect(SocketAddress address, Map<String, Object> context) {
      HttpDestination destination = (HttpDestination)context.get("org.eclipse.jetty.client.destination");
      context.put("org.eclipse.jetty.client.connector.clientConnectionFactory", destination.getClientConnectionFactory());
      Promise<Connection> promise = (Promise<Connection>)context.get("org.eclipse.jetty.client.connection.promise");
      context.put("org.eclipse.jetty.client.connector.connectionPromise", Promise.from(ioConnection -> {
      }, promise::failed));
      this.connector.connect(address, context);
   }

   @Override
   public void connect(InetSocketAddress address, Map<String, Object> context) {
      this.connect((SocketAddress)address, context);
   }
}
