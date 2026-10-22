package org.glassfish.jersey.client.spi;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Configuration;

public interface ConnectorProvider {
   Connector getConnector(Client var1, Configuration var2);
}
