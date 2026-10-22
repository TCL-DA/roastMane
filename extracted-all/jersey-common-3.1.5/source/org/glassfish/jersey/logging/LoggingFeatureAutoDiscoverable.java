package org.glassfish.jersey.logging;

import jakarta.annotation.Priority;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.FeatureContext;
import java.util.Map;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;

@Priority(2000)
public final class LoggingFeatureAutoDiscoverable implements AutoDiscoverable {
   @Override
   public void configure(FeatureContext context) {
      if (!context.getConfiguration().isRegistered(LoggingFeature.class)) {
         Map properties = context.getConfiguration().getProperties();
         if (this.commonPropertyConfigured(properties)
            || context.getConfiguration().getRuntimeType() == RuntimeType.CLIENT && this.clientConfigured(properties)
            || context.getConfiguration().getRuntimeType() == RuntimeType.SERVER && this.serverConfigured(properties)) {
            context.register(LoggingFeature.class);
         }
      }
   }

   private boolean commonPropertyConfigured(Map properties) {
      return properties.containsKey("jersey.config.logging.logger.name")
         || properties.containsKey("jersey.config.logging.logger.level")
         || properties.containsKey("jersey.config.logging.verbosity")
         || properties.containsKey("jersey.config.logging.entity.maxSize")
         || properties.containsKey("jersey.config.logging.separator")
         || properties.containsKey("jersey.config.logging.headers.redact");
   }

   private boolean clientConfigured(Map properties) {
      return properties.containsKey("jersey.config.client.logging.logger.name")
         || properties.containsKey("jersey.config.client.logging.logger.level")
         || properties.containsKey("jersey.config.client.logging.verbosity")
         || properties.containsKey("jersey.config.client.logging.entity.maxSize")
         || properties.containsKey("jersey.config.client.logging.separator")
         || properties.containsKey("jersey.config.client.logging.headers.redact");
   }

   private boolean serverConfigured(Map properties) {
      return properties.containsKey("jersey.config.server.logging.logger.name")
         || properties.containsKey("jersey.config.server.logging.logger.level")
         || properties.containsKey("jersey.config.server.logging.verbosity")
         || properties.containsKey("jersey.config.server.logging.entity.maxSize")
         || properties.containsKey("jersey.config.server.logging.separator")
         || properties.containsKey("jersey.config.server.logging.headers.redact");
   }
}
