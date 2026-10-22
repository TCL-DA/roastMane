package org.glassfish.jersey.server.wadl.internal;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.FeatureContext;
import org.glassfish.jersey.internal.spi.ForcedAutoDiscoverable;
import org.glassfish.jersey.server.wadl.WadlFeature;

@ConstrainedTo(RuntimeType.SERVER)
@Priority(2000)
public final class WadlAutoDiscoverable implements ForcedAutoDiscoverable {
   public void configure(FeatureContext context) {
      if (!context.getConfiguration().isRegistered(WadlFeature.class)) {
         context.register(WadlFeature.class);
      }
   }
}
