package org.glassfish.jersey.jsonb.internal;

import jakarta.annotation.Priority;
import jakarta.ws.rs.core.FeatureContext;
import org.glassfish.jersey.internal.spi.ForcedAutoDiscoverable;
import org.glassfish.jersey.jsonb.JsonBindingFeature;

@Priority(1800)
public class JsonBindingAutoDiscoverable implements ForcedAutoDiscoverable {
   public void configure(FeatureContext context) {
      if (!context.getConfiguration().isRegistered(JsonBindingFeature.class)) {
         context.register(JsonBindingFeature.class);
      }
   }
}
