package org.glassfish.jersey.server.internal.monitoring;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.FeatureContext;
import org.glassfish.jersey.internal.spi.ForcedAutoDiscoverable;
import org.glassfish.jersey.server.ServerProperties;

@ConstrainedTo(RuntimeType.SERVER)
@Priority(2000)
public final class MonitoringAutodiscoverable implements ForcedAutoDiscoverable {
   public void configure(FeatureContext context) {
      if (!context.getConfiguration().isRegistered(MonitoringFeature.class)) {
         Boolean monitoringEnabled = ServerProperties.getValue(
            context.getConfiguration().getProperties(), "jersey.config.server.monitoring.enabled", Boolean.FALSE
         );
         Boolean statisticsEnabled = ServerProperties.getValue(
            context.getConfiguration().getProperties(), "jersey.config.server.monitoring.statistics.enabled", Boolean.FALSE
         );
         Boolean mbeansEnabled = ServerProperties.getValue(
            context.getConfiguration().getProperties(), "jersey.config.server.monitoring.statistics.mbeans.enabled", Boolean.FALSE
         );
         if (monitoringEnabled || statisticsEnabled || mbeansEnabled) {
            context.register(MonitoringFeature.class);
         }
      }
   }
}
