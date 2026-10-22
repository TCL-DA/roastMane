package org.glassfish.jersey.message.filtering;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

public final class SecurityEntityFilteringFeature implements Feature {
   public boolean configure(FeatureContext context) {
      Configuration config = context.getConfiguration();
      if (!config.isRegistered(SecurityEntityProcessor.class)) {
         if (!config.isRegistered(RolesAllowedDynamicFeature.class)) {
            context.register(RolesAllowedDynamicFeature.class);
         }

         if (!config.isRegistered(EntityFilteringBinder.class)) {
            context.register(new EntityFilteringBinder());
         }

         context.register(SecurityEntityProcessor.class);
         if (!config.isRegistered(DefaultEntityProcessor.class)) {
            context.register(DefaultEntityProcessor.class);
         }

         context.register(SecurityScopeResolver.class);
         if (RuntimeType.SERVER.equals(config.getRuntimeType())) {
            context.register(SecurityServerScopeResolver.class);
         }

         if (RuntimeType.SERVER == config.getRuntimeType()) {
            context.register(SecurityServerScopeProvider.class);
         } else {
            context.register(CommonScopeProvider.class);
         }

         return true;
      } else {
         return false;
      }
   }
}
