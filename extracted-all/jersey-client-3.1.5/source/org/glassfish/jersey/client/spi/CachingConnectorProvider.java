package org.glassfish.jersey.client.spi;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Configuration;

public class CachingConnectorProvider implements ConnectorProvider {
   private final ConnectorProvider delegate;
   private Connector connector;

   public CachingConnectorProvider(ConnectorProvider delegate) {
      this.delegate = delegate;
   }

   @Override
   public synchronized Connector getConnector(Client client, Configuration runtimeConfig) {
      if (this.connector == null) {
         this.connector = this.delegate.getConnector(client, runtimeConfig);
      }

      return this.connector;
   }
}
