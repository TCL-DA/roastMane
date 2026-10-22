package org.glassfish.jersey.internal.config;

class SystemPropertiesConfigurationProvider extends ExternalConfigurationProviderImpl {
   public SystemPropertiesConfigurationProvider() {
      super(new JerseySystemPropertiesConfigurationModel());
   }
}
