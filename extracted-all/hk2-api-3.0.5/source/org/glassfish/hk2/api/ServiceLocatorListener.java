package org.glassfish.hk2.api;

import java.util.Set;

public interface ServiceLocatorListener {
   void initialize(Set<ServiceLocator> var1);

   void locatorAdded(ServiceLocator var1);

   void locatorDestroyed(ServiceLocator var1);
}
