package org.glassfish.jersey.internal;

import org.glassfish.jersey.internal.inject.InjectionManager;

public interface BootstrapConfigurator {
   void init(InjectionManager var1, BootstrapBag var2);

   default void postInit(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
   }
}
