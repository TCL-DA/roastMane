package org.glassfish.jersey.jetty;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.jetty.internal.LocalizationMessages;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.Container;

public final class JettyHttpContainer implements Container {
   public ResourceConfig getConfiguration() {
      throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
   }

   public void reload() {
      throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
   }

   public void reload(ResourceConfig configuration) {
      throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
   }

   public ApplicationHandler getApplicationHandler() {
      throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
   }

   JettyHttpContainer(Application application, Object parentContext) {
      throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
   }

   JettyHttpContainer(Application application) {
      throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
   }

   JettyHttpContainer(Class<? extends Application> applicationClass) {
      throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
   }
}
