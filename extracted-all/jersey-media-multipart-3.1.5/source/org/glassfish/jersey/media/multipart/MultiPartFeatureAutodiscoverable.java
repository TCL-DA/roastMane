package org.glassfish.jersey.media.multipart;

import jakarta.ws.rs.core.FeatureContext;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;

public class MultiPartFeatureAutodiscoverable implements AutoDiscoverable {
   public void configure(FeatureContext context) {
      context.register(MultiPartFeature.class);
   }
}
