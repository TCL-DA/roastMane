package org.glassfish.jersey.jackson.internal;

import jakarta.annotation.Priority;
import jakarta.ws.rs.core.FeatureContext;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import org.glassfish.jersey.jackson.JacksonFeature;

@Priority(2000)
public class JacksonAutoDiscoverable implements AutoDiscoverable {
   public void configure(FeatureContext context) {
      if (!context.getConfiguration().isRegistered(JacksonFeature.class)) {
         context.register(JacksonFeature.class);
      }
   }
}
