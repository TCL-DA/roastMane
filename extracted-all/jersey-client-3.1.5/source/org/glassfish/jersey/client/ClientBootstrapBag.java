package org.glassfish.jersey.client;

import jakarta.ws.rs.core.GenericType;
import java.util.Collection;
import org.glassfish.jersey.client.inject.ParameterUpdaterProvider;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.spi.ComponentProvider;

public class ClientBootstrapBag extends BootstrapBag {
   private ParameterUpdaterProvider parameterUpdaterProvider;
   private LazyValue<Collection<ComponentProvider>> componentProviders;

   public ParameterUpdaterProvider getParameterUpdaterProvider() {
      requireNonNull(this.parameterUpdaterProvider, ParameterUpdaterProvider.class);
      return this.parameterUpdaterProvider;
   }

   public void setParameterUpdaterProvider(ParameterUpdaterProvider provider) {
      this.parameterUpdaterProvider = provider;
   }

   LazyValue<Collection<ComponentProvider>> getComponentProviders() {
      requireNonNull(this.componentProviders, (new GenericType<LazyValue<Collection<ComponentProvider>>>() {
      }).getType());
      return this.componentProviders;
   }

   void setComponentProviders(LazyValue<Collection<ComponentProvider>> componentProviders) {
      this.componentProviders = componentProviders;
   }
}
