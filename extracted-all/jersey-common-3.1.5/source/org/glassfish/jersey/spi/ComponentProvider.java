package org.glassfish.jersey.spi;

import java.util.Collections;
import java.util.Set;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.model.ContractProvider;

public interface ComponentProvider {
   void initialize(InjectionManager var1);

   boolean bind(Class<?> var1, Set<Class<?>> var2);

   default boolean bind(Class<?> component, ContractProvider contractProvider) {
      Set<Class<?>> contracts = contractProvider == null ? Collections.emptySet() : contractProvider.getContracts();
      return this.bind(component, contracts);
   }

   void done();
}
