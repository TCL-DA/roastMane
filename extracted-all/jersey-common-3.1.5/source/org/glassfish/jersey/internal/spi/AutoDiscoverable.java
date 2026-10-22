package org.glassfish.jersey.internal.spi;

import jakarta.ws.rs.core.FeatureContext;

public interface AutoDiscoverable {
   int DEFAULT_PRIORITY = 2000;

   void configure(FeatureContext var1);
}
