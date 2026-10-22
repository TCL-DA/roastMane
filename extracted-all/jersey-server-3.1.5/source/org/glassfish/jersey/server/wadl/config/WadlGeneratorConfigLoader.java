package org.glassfish.jersey.server.wadl.config;

import jakarta.ws.rs.ProcessingException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.server.internal.LocalizationMessages;

public class WadlGeneratorConfigLoader {
   public static WadlGeneratorConfig loadWadlGeneratorsFromConfig(Map<String, Object> properties) {
      Object wadlGeneratorConfigProperty = properties.get("jersey.config.server.wadl.generatorConfig");
      if (wadlGeneratorConfigProperty == null) {
         return new WadlGeneratorConfig() {
            @Override
            public List<WadlGeneratorDescription> configure() {
               return Collections.emptyList();
            }
         };
      } else {
         try {
            if (wadlGeneratorConfigProperty instanceof WadlGeneratorConfig) {
               return (WadlGeneratorConfig)wadlGeneratorConfigProperty;
            } else {
               Class<? extends WadlGeneratorConfig> configClazz;
               if (wadlGeneratorConfigProperty instanceof Class) {
                  configClazz = ((Class)wadlGeneratorConfigProperty).asSubclass(WadlGeneratorConfig.class);
               } else {
                  if (!(wadlGeneratorConfigProperty instanceof String)) {
                     throw new ProcessingException(
                        LocalizationMessages.ERROR_WADL_GENERATOR_CONFIG_LOADER_PROPERTY(
                           "jersey.config.server.wadl.generatorConfig", wadlGeneratorConfigProperty.getClass().getName()
                        )
                     );
                  }

                  configClazz = AccessController.<Class>doPrivileged(ReflectionHelper.classForNameWithExceptionPEA((String)wadlGeneratorConfigProperty))
                     .asSubclass(WadlGeneratorConfig.class);
               }

               return configClazz.newInstance();
            }
         } catch (PrivilegedActionException var3) {
            throw new ProcessingException(LocalizationMessages.ERROR_WADL_GENERATOR_CONFIG_LOADER("jersey.config.server.wadl.generatorConfig"), var3.getCause());
         } catch (Exception var4) {
            throw new ProcessingException(LocalizationMessages.ERROR_WADL_GENERATOR_CONFIG_LOADER("jersey.config.server.wadl.generatorConfig"), var4);
         }
      }
   }
}
