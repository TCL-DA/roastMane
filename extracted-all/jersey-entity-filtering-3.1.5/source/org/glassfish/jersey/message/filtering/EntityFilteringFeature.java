package org.glassfish.jersey.message.filtering;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

public final class EntityFilteringFeature implements Feature {
   public static final String ENTITY_FILTERING_SCOPE = "jersey.config.entityFiltering.scope";

   public boolean configure(FeatureContext context) {
      Configuration config = context.getConfiguration();
      if (!config.isRegistered(EntityFilteringProcessor.class)) {
         if (!config.isRegistered(EntityFilteringBinder.class)) {
            context.register(new EntityFilteringBinder());
         }

         context.register(EntityFilteringProcessor.class);
         if (!config.isRegistered(DefaultEntityProcessor.class)) {
            context.register(DefaultEntityProcessor.class);
         }

         context.register(EntityFilteringScopeResolver.class);
         if (RuntimeType.SERVER == config.getRuntimeType()) {
            context.register(ServerScopeProvider.class);
         } else {
            context.register(CommonScopeProvider.class);
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean enabled(Configuration config) {
      return config.isRegistered(EntityFilteringFeature.class)
         || config.isRegistered(SecurityEntityFilteringFeature.class)
         || config.isRegistered(SelectableEntityFilteringFeature.class);
   }
}
