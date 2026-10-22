package org.jvnet.hk2.internal;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.Visibility;
import org.jvnet.hk2.external.runtime.ServiceLocatorRuntimeBean;

@Singleton
@Visibility(DescriptorVisibility.LOCAL)
public class ServiceLocatorRuntimeImpl implements ServiceLocatorRuntimeBean {
   private final ServiceLocatorImpl locator;

   @Inject
   private ServiceLocatorRuntimeImpl(ServiceLocator locator) {
      this.locator = (ServiceLocatorImpl)locator;
   }

   @Override
   public int getNumberOfDescriptors() {
      return this.locator.getNumberOfDescriptors();
   }

   @Override
   public int getNumberOfChildren() {
      return this.locator.getNumberOfChildren();
   }

   @Override
   public int getServiceCacheSize() {
      return this.locator.getServiceCacheSize();
   }

   @Override
   public int getServiceCacheMaximumSize() {
      return this.locator.getServiceCacheMaximumSize();
   }

   @Override
   public void clearServiceCache() {
      this.locator.clearServiceCache();
   }

   @Override
   public int getReflectionCacheSize() {
      return this.locator.getReflectionCacheSize();
   }

   @Override
   public void clearReflectionCache() {
      this.locator.clearReflectionCache();
   }
}
