package org.glassfish.jersey.server.internal.monitoring;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.Date;
import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.ApplicationInfo;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

@Priority(1000)
public final class ApplicationInfoListener implements ApplicationEventListener {
   public static final int PRIORITY = 1000;
   @Inject
   private Provider<Ref<ApplicationInfo>> applicationInfoRefProvider;

   @Override
   public RequestEventListener onRequest(RequestEvent requestEvent) {
      return null;
   }

   @Override
   public void onEvent(ApplicationEvent event) {
      ApplicationEvent.Type type = event.getType();
      switch (type) {
         case RELOAD_FINISHED:
         case INITIALIZATION_FINISHED:
            this.processApplicationStatistics(event);
      }
   }

   private void processApplicationStatistics(ApplicationEvent event) {
      long now = System.currentTimeMillis();
      ApplicationInfo applicationInfo = new ApplicationInfoImpl(
         event.getResourceConfig(), new Date(now), event.getRegisteredClasses(), event.getRegisteredInstances(), event.getProviders()
      );
      ((Ref)this.applicationInfoRefProvider.get()).set(applicationInfo);
   }
}
