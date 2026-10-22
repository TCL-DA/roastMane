package org.glassfish.jersey.internal;

import jakarta.ws.rs.core.Configuration;
import org.glassfish.jersey.internal.util.PropertiesHelper;

public interface PropertiesResolver {
   <T> T resolveProperty(String var1, Class<T> var2);

   <T> T resolveProperty(String var1, T var2);

   static PropertiesResolver create(final Configuration configuration, final PropertiesDelegate delegate) {
      return new PropertiesResolver() {
         @Override
         public <T> T resolveProperty(String name, Class<T> type) {
            return this.resolveProperty(name, null, type);
         }

         @Override
         public <T> T resolveProperty(String name, T defaultValue) {
            return this.resolveProperty(name, defaultValue, (Class<T>)defaultValue.getClass());
         }

         private <T> T resolveProperty(String name, Object defaultValue, Class<T> type) {
            Object result = delegate.getProperty(name);
            if (result == null) {
               result = configuration.getProperty(name);
               if (result == null) {
                  result = defaultValue;
               }
            }

            return result == null ? null : PropertiesHelper.convertValue(result, type);
         }
      };
   }
}
