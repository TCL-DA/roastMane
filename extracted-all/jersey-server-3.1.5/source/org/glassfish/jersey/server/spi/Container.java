package org.glassfish.jersey.server.spi;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.SERVER)
public interface Container {
   int DEFAULT_HTTP_PORT = 80;
   int DEFAULT_HTTPS_PORT = 443;

   ResourceConfig getConfiguration();

   ApplicationHandler getApplicationHandler();

   void reload();

   void reload(ResourceConfig var1);
}
