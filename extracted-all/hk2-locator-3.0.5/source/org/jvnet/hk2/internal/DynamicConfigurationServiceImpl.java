package org.jvnet.hk2.internal;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.Populator;
import org.glassfish.hk2.api.ServiceLocator;

@Singleton
public class DynamicConfigurationServiceImpl implements DynamicConfigurationService {
   private final ServiceLocatorImpl locator;
   private final Populator populator;

   @Inject
   private DynamicConfigurationServiceImpl(ServiceLocator locator) {
      this.locator = (ServiceLocatorImpl)locator;
      this.populator = new PopulatorImpl(locator, this);
   }

   public DynamicConfiguration createDynamicConfiguration() {
      return new DynamicConfigurationImpl(this.locator);
   }

   public Populator getPopulator() {
      return this.populator;
   }
}
