package org.glassfish.jersey.server.monitoring;

import java.util.Set;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.ResourceModel;

public interface ApplicationEvent {
   ApplicationEvent.Type getType();

   ResourceConfig getResourceConfig();

   Set<Class<?>> getRegisteredClasses();

   Set<Object> getRegisteredInstances();

   Set<Class<?>> getProviders();

   ResourceModel getResourceModel();

   public static enum Type {
      INITIALIZATION_START,
      INITIALIZATION_APP_FINISHED,
      INITIALIZATION_FINISHED,
      DESTROY_FINISHED,
      RELOAD_FINISHED;
   }
}
