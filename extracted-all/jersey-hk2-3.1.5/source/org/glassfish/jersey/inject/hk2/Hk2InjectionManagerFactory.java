package org.glassfish.jersey.inject.hk2;

import jakarta.annotation.Priority;
import java.security.AccessController;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.InjectionManagerFactory;
import org.glassfish.jersey.internal.util.PropertiesHelper;

@Priority(10)
public class Hk2InjectionManagerFactory implements InjectionManagerFactory {
   public static final String HK2_INJECTION_MANAGER_STRATEGY = "org.glassfish.jersey.hk2.injection.manager.strategy";

   public InjectionManager create(Object parent) {
      return this.initInjectionManager(getStrategy().createInjectionManager(parent));
   }

   public static boolean isImmediateStrategy() {
      return getStrategy() == Hk2InjectionManagerFactory.Hk2InjectionManagerStrategy.IMMEDIATE;
   }

   private static Hk2InjectionManagerFactory.Hk2InjectionManagerStrategy getStrategy() {
      String value = AccessController.doPrivileged(PropertiesHelper.getSystemProperty("org.glassfish.jersey.hk2.injection.manager.strategy"));
      if (value == null || value.isEmpty()) {
         return Hk2InjectionManagerFactory.Hk2InjectionManagerStrategy.IMMEDIATE;
      } else if ("immediate".equalsIgnoreCase(value)) {
         return Hk2InjectionManagerFactory.Hk2InjectionManagerStrategy.IMMEDIATE;
      } else if ("delayed".equalsIgnoreCase(value)) {
         return Hk2InjectionManagerFactory.Hk2InjectionManagerStrategy.DELAYED;
      } else {
         throw new IllegalStateException(
            "Illegal value of org.glassfish.jersey.hk2.injection.manager.strategy. Expected \"immediate\" or \"delayed\", the actual value is: " + value
         );
      }
   }

   private InjectionManager initInjectionManager(InjectionManager injectionManager) {
      injectionManager.register(Bindings.service(injectionManager).to(InjectionManager.class));
      return injectionManager;
   }

   private static enum Hk2InjectionManagerStrategy {
      IMMEDIATE {
         @Override
         InjectionManager createInjectionManager(Object parent) {
            return new ImmediateHk2InjectionManager(parent);
         }
      },
      DELAYED {
         @Override
         InjectionManager createInjectionManager(Object parent) {
            return new DelayedHk2InjectionManager(parent);
         }
      };

      private Hk2InjectionManagerStrategy() {
      }

      abstract InjectionManager createInjectionManager(Object var1);
   }
}
