package org.glassfish.jersey.server;

import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Application;
import java.util.Collection;
import java.util.Collections;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.BootstrapConfigurator;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.server.spi.ComponentProvider;

class ApplicationConfigurator implements BootstrapConfigurator {
   private Application application;
   private Class<? extends Application> applicationClass;

   ApplicationConfigurator(Application application) {
      this.application = application;
   }

   ApplicationConfigurator(Class<? extends Application> applicationClass) {
      this.applicationClass = applicationClass;
   }

   public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
      ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
      Application resultApplication;
      if (this.application != null) {
         if (this.application instanceof ResourceConfig) {
            ResourceConfig rc = (ResourceConfig)this.application;
            if (rc.getApplicationClass() != null) {
               rc.setApplication(createApplication(injectionManager, rc.getApplicationClass(), serverBag.getComponentProviders()));
            }
         }

         resultApplication = this.application;
      } else {
         resultApplication = createApplication(injectionManager, this.applicationClass, serverBag.getComponentProviders());
      }

      serverBag.setApplication(resultApplication);
      injectionManager.register(Bindings.service(resultApplication).to(Application.class));
   }

   private static Application createApplication(
      InjectionManager injectionManager, Class<? extends Application> applicationClass, Value<Collection<ComponentProvider>> componentProvidersValue
   ) {
      if (applicationClass == ResourceConfig.class) {
         return new ResourceConfig();
      } else if (applicationClass == Application.class) {
         return new Application();
      } else {
         Collection<ComponentProvider> componentProviders = (Collection<ComponentProvider>)componentProvidersValue.get();
         boolean appClassBound = false;

         for (ComponentProvider cp : componentProviders) {
            if (cp.bind(applicationClass, Collections.emptySet())) {
               appClassBound = true;
               break;
            }
         }

         if (!appClassBound && applicationClass.isAnnotationPresent(Singleton.class)) {
            injectionManager.register(Bindings.serviceAsContract(applicationClass).in(Singleton.class));
            appClassBound = true;
         }

         Application app = appClassBound
            ? (Application)injectionManager.getInstance(applicationClass)
            : (Application)injectionManager.createAndInitialize(applicationClass);
         if (app instanceof ResourceConfig) {
            ResourceConfig _rc = (ResourceConfig)app;
            Class<? extends Application> innerAppClass = _rc.getApplicationClass();
            if (innerAppClass != null) {
               Application innerApp = createApplication(injectionManager, innerAppClass, componentProvidersValue);
               _rc.setApplication(innerApp);
            }
         }

         return app;
      }
   }
}
