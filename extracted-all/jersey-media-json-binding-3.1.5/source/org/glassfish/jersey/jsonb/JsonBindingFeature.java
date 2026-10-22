package org.glassfish.jersey.jsonb;

import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.jsonb.internal.JsonBindingProvider;

public class JsonBindingFeature implements Feature {
   private static final String JSON_FEATURE = JsonBindingFeature.class.getSimpleName();

   public boolean configure(FeatureContext context) {
      Configuration config = context.getConfiguration();
      String jsonFeature = (String)CommonProperties.getValue(
         config.getProperties(), config.getRuntimeType(), "jersey.config.jsonFeature", JSON_FEATURE, String.class
      );
      if (!JSON_FEATURE.equalsIgnoreCase(jsonFeature)) {
         return false;
      } else {
         context.property(PropertiesHelper.getPropertyNameForRuntime("jersey.config.jsonFeature", config.getRuntimeType()), JSON_FEATURE);
         context.register(JsonBindingProvider.class);
         return true;
      }
   }
}
