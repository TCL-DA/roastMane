package org.glassfish.jersey.inject.hk2;

import jakarta.inject.Singleton;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScope;

public class Hk2BootstrapBinder extends AbstractBinder {
   private final ServiceLocator serviceLocator;

   Hk2BootstrapBinder(ServiceLocator serviceLocator) {
      this.serviceLocator = serviceLocator;
   }

   protected void configure() {
      this.install(
         new Binder[]{
            new JerseyClassAnalyzer.Binder(this.serviceLocator),
            new RequestContext.Binder(),
            new ContextInjectionResolverImpl.Binder(),
            new JerseyErrorService.Binder()
         }
      );
      this.bind(Hk2RequestScope.class).to(RequestScope.class).in(Singleton.class);
   }
}
