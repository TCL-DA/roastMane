package org.glassfish.jersey.internal.inject;

import jakarta.ws.rs.ext.ParamConverterProvider;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.BootstrapConfigurator;

public class ParamConverterConfigurator implements BootstrapConfigurator {
   @Override
   public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
      ClassBinding<ParamConverters.AggregatedProvider> aggregatedConverters = Bindings.service(ParamConverters.AggregatedProvider.class)
         .to(ParamConverterProvider.class);
      injectionManager.register((Binding)aggregatedConverters);
   }
}
