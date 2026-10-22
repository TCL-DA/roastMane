package org.glassfish.hk2.api;

import org.glassfish.hk2.extension.ServiceLocatorGenerator;
import org.glassfish.hk2.internal.ServiceLocatorFactoryImpl;

public abstract class ServiceLocatorFactory {
   private static ServiceLocatorFactory INSTANCE = new ServiceLocatorFactoryImpl();

   public static ServiceLocatorFactory getInstance() {
      return INSTANCE;
   }

   public abstract ServiceLocator create(String var1);

   public abstract ServiceLocator create(String var1, ServiceLocator var2);

   public abstract ServiceLocator create(String var1, ServiceLocator var2, ServiceLocatorGenerator var3);

   public abstract ServiceLocator create(String var1, ServiceLocator var2, ServiceLocatorGenerator var3, ServiceLocatorFactory.CreatePolicy var4);

   public abstract ServiceLocator find(String var1);

   public abstract void destroy(String var1);

   public abstract void destroy(ServiceLocator var1);

   public abstract void addListener(ServiceLocatorListener var1);

   public abstract void removeListener(ServiceLocatorListener var1);

   public static enum CreatePolicy {
      RETURN,
      DESTROY,
      ERROR;
   }
}
