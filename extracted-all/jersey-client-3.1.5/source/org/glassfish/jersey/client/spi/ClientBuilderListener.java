package org.glassfish.jersey.client.spi;

import jakarta.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.Beta;

@Beta
public interface ClientBuilderListener {
   void onNewBuilder(ClientBuilder var1);
}
