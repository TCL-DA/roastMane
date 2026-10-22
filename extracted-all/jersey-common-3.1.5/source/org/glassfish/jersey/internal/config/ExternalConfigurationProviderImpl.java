package org.glassfish.jersey.internal.config;

import java.util.Map;
import org.glassfish.jersey.spi.ExternalConfigurationModel;
import org.glassfish.jersey.spi.ExternalConfigurationProvider;

public class ExternalConfigurationProviderImpl implements ExternalConfigurationProvider {
   protected final ExternalConfigurationModel<?> model;

   protected ExternalConfigurationProviderImpl(ExternalConfigurationModel<?> model) {
      this.model = model;
   }

   @Override
   public Map<String, Object> getProperties() {
      return this.model.getProperties();
   }

   @Override
   public ExternalConfigurationModel getConfiguration() {
      return this.model;
   }

   @Override
   public ExternalConfigurationModel merge(ExternalConfigurationModel input) {
      return input == null ? this.model : this.model.mergeProperties(input.getProperties());
   }
}
