package org.eclipse.jetty.client;

import java.util.Map;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public abstract class AbstractHttpClientTransport extends ContainerLifeCycle implements HttpClientTransport {
   private static final Logger LOG = LoggerFactory.getLogger(HttpClientTransport.class);
   private HttpClient client;
   private ConnectionPool.Factory factory;

   protected HttpClient getHttpClient() {
      return this.client;
   }

   @Override
   public void setHttpClient(HttpClient client) {
      this.client = client;
   }

   @Override
   public ConnectionPool.Factory getConnectionPoolFactory() {
      return this.factory;
   }

   @Override
   public void setConnectionPoolFactory(ConnectionPool.Factory factory) {
      this.factory = factory;
   }

   protected void connectFailed(Map<String, Object> context, Throwable failure) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Could not connect to {}", context.get("org.eclipse.jetty.client.destination"));
      }

      Promise<Connection> promise = (Promise<Connection>)context.get("org.eclipse.jetty.client.connection.promise");
      promise.failed(failure);
   }
}
