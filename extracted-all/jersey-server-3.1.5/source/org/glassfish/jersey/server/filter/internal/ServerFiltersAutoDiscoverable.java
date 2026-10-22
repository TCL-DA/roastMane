package org.glassfish.jersey.server.filter.internal;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.FeatureContext;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import org.glassfish.jersey.server.filter.UriConnegFilter;

@ConstrainedTo(RuntimeType.SERVER)
@Priority(2000)
public final class ServerFiltersAutoDiscoverable implements AutoDiscoverable {
   public void configure(FeatureContext context) {
      Configuration config = context.getConfiguration();
      Object languageMappings = config.getProperty("jersey.config.server.languageMappings");
      Object mediaTypesMappings = config.getProperty("jersey.config.server.mediaTypeMappings");
      if (!config.isRegistered(UriConnegFilter.class) && (languageMappings != null || mediaTypesMappings != null)) {
         context.register(UriConnegFilter.class);
      }
   }
}
