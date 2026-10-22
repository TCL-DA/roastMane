package org.glassfish.jersey.server;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Configuration;
import org.glassfish.jersey.internal.AbstractServiceFinderConfigurator;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import org.glassfish.jersey.server.spi.ContainerProvider;

class ContainerProviderConfigurator extends AbstractServiceFinderConfigurator<ContainerProvider> {
   ContainerProviderConfigurator(RuntimeType runtimeType) {
      super(ContainerProvider.class, runtimeType);
   }

   public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
      Configuration configuration = bootstrapBag.getConfiguration();
      this.loadImplementations(configuration.getProperties())
         .forEach(implClass -> injectionManager.register(Bindings.service(implClass).to(AutoDiscoverable.class)));
   }
}
