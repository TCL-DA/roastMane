package org.glassfish.jersey.message.filtering;

import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

public final class SelectableEntityFilteringFeature implements Feature {
   public static final String QUERY_PARAM_NAME = "jersey.config.entityFiltering.selectable.query";

   public boolean configure(FeatureContext context) {
      Configuration config = context.getConfiguration();
      if (!config.isRegistered(SelectableEntityProcessor.class)) {
         if (!config.isRegistered(EntityFilteringFeature.class)) {
            context.register(EntityFilteringFeature.class);
         }

         context.register(SelectableEntityProcessor.class);
         context.register(SelectableScopeResolver.class);
         return true;
      } else {
         return true;
      }
   }
}
