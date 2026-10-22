package org.glassfish.jersey.server;

import java.util.logging.Logger;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.BootstrapConfigurator;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.model.Resource;

class ResourceBagConfigurator implements BootstrapConfigurator {
   private static final Logger LOGGER = Logger.getLogger(ResourceBagConfigurator.class.getName());

   public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
      ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
      ResourceConfig runtimeConfig = serverBag.getRuntimeConfig();
      boolean disableValidation = ServerProperties.getValue(
         runtimeConfig.getProperties(), "jersey.config.server.resource.validation.disable", Boolean.FALSE, Boolean.class
      );
      ResourceBag.Builder resourceBagBuilder = new ResourceBag.Builder();

      for (Resource programmaticResource : runtimeConfig.getResources()) {
         resourceBagBuilder.registerProgrammaticResource(programmaticResource);
      }

      for (Class<?> c : runtimeConfig.getClasses()) {
         try {
            Resource resource = Resource.from(c, disableValidation);
            if (resource != null) {
               resourceBagBuilder.registerResource(c, resource);
            }
         } catch (IllegalArgumentException var11) {
            LOGGER.warning(var11.getMessage());
         }
      }

      for (Object o : runtimeConfig.getSingletons()) {
         try {
            Resource resource = Resource.from(o.getClass(), disableValidation);
            if (resource != null) {
               resourceBagBuilder.registerResource(o, resource);
            }
         } catch (IllegalArgumentException var10) {
            LOGGER.warning(var10.getMessage());
         }
      }

      serverBag.setResourceBag(resourceBagBuilder.build());
   }
}
