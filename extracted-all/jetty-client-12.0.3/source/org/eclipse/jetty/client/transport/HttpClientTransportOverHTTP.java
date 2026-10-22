package org.eclipse.jetty.client.transport;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.client.AbstractConnectorHttpClientTransport;
import org.eclipse.jetty.client.Destination;
import org.eclipse.jetty.client.DuplexConnectionPool;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.ProcessorUtils;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("The HTTP/1.1 client transport")
public class HttpClientTransportOverHTTP extends AbstractConnectorHttpClientTransport {
   public static final Origin.Protocol HTTP11 = new Origin.Protocol(List.of("http/1.1"), false);
   private static final Logger LOG = LoggerFactory.getLogger(HttpClientTransportOverHTTP.class);
   private final ClientConnectionFactory factory = new HttpClientConnectionFactory();
   private int headerCacheSize = 1024;
   private boolean headerCacheCaseSensitive;

   public HttpClientTransportOverHTTP() {
      this(Math.max(1, ProcessorUtils.availableProcessors() / 2));
   }

   public HttpClientTransportOverHTTP(int selectors) {
      this(new ClientConnector());
      this.getClientConnector().setSelectors(selectors);
   }

   public HttpClientTransportOverHTTP(ClientConnector connector) {
      super(connector);
      this.setConnectionPoolFactory(destination -> new DuplexConnectionPool(destination, this.getHttpClient().getMaxConnectionsPerDestination()));
   }

   @Override
   public Origin newOrigin(Request request) {
      return this.getHttpClient().createOrigin(request, HTTP11);
   }

   @Override
   public Destination newDestination(Origin origin) {
      SocketAddress address = origin.getAddress().getSocketAddress();
      return new HttpDestination(this.getHttpClient(), origin, this.getClientConnector().isIntrinsicallySecure(address));
   }

   public Connection newConnection(EndPoint endPoint, Map<String, Object> context) throws IOException {
      Connection connection = this.factory.newConnection(endPoint, context);
      if (LOG.isDebugEnabled()) {
         LOG.debug("Created {}", connection);
      }

      return connection;
   }

   @ManagedAttribute("The maximum allowed size in bytes for an HTTP header field cache")
   public int getHeaderCacheSize() {
      return this.headerCacheSize;
   }

   public void setHeaderCacheSize(int headerCacheSize) {
      this.headerCacheSize = headerCacheSize;
   }

   @ManagedAttribute("Whether the header field cache is case sensitive")
   public boolean isHeaderCacheCaseSensitive() {
      return this.headerCacheCaseSensitive;
   }

   public void setHeaderCacheCaseSensitive(boolean headerCacheCaseSensitive) {
      this.headerCacheCaseSensitive = headerCacheCaseSensitive;
   }
}
