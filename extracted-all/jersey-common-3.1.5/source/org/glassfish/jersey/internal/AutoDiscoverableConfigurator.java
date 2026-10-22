package org.glassfish.jersey.internal;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Configuration;
import java.util.List;
import java.util.stream.Collectors;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;

public class AutoDiscoverableConfigurator extends AbstractServiceFinderConfigurator<AutoDiscoverable> {
   public AutoDiscoverableConfigurator(RuntimeType runtimeType) {
      super(AutoDiscoverable.class, runtimeType);
   }

   @Override
   public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
      Configuration configuration = bootstrapBag.getConfiguration();
      List<AutoDiscoverable> autoDiscoverables = this.loadImplementations(configuration.getProperties())
         .stream()
         .peek(implClass -> injectionManager.register(Bindings.service((Class<AutoDiscoverable>)implClass).to(AutoDiscoverable.class)))
         .map(injectionManager::createAndInitialize)
         .collect(Collectors.toList());
      bootstrapBag.setAutoDiscoverables(autoDiscoverables);
   }
}
