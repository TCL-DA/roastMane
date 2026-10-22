package org.glassfish.jersey.spi;

import java.util.Map;
import java.util.Optional;
import org.glassfish.jersey.ExtendedConfig;

public interface ExternalConfigurationModel<CONFIG> extends ExtendedConfig {
   <T> T as(String var1, Class<T> var2);

   <T> Optional<T> getOptionalProperty(String var1, Class<T> var2);

   ExternalConfigurationModel mergeProperties(Map<String, Object> var1);

   CONFIG getConfig();
}
