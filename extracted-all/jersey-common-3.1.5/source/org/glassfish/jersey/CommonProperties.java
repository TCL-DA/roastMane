package org.glassfish.jersey;

import jakarta.ws.rs.RuntimeType;
import java.util.HashMap;
import java.util.Map;
import org.glassfish.jersey.internal.util.PropertiesClass;
import org.glassfish.jersey.internal.util.PropertiesHelper;

@PropertiesClass
public final class CommonProperties {
   private static final Map<String, String> LEGACY_FALLBACK_MAP = new HashMap<>();
   public static final String ALLOW_SYSTEM_PROPERTIES_PROVIDER = "jersey.config.allowSystemPropertiesProvider";
   public static final String FEATURE_AUTO_DISCOVERY_DISABLE = "jersey.config.disableAutoDiscovery";
   public static final String FEATURE_AUTO_DISCOVERY_DISABLE_CLIENT = "jersey.config.client.disableAutoDiscovery";
   public static final String FEATURE_AUTO_DISCOVERY_DISABLE_SERVER = "jersey.config.server.disableAutoDiscovery";
   public static final String JSON_PROCESSING_FEATURE_DISABLE = "jersey.config.disableJsonProcessing";
   public static final String JSON_PROCESSING_FEATURE_DISABLE_CLIENT = "jersey.config.client.disableJsonProcessing";
   public static final String JSON_PROCESSING_FEATURE_DISABLE_SERVER = "jersey.config.server.disableJsonProcessing";
   public static final String METAINF_SERVICES_LOOKUP_DISABLE = "jersey.config.disableMetainfServicesLookup";
   public static final String METAINF_SERVICES_LOOKUP_DISABLE_CLIENT = "jersey.config.client.disableMetainfServicesLookup";
   public static final String METAINF_SERVICES_LOOKUP_DISABLE_SERVER = "jersey.config.server.disableMetainfServicesLookup";
   public static final String MOXY_JSON_FEATURE_DISABLE = "jersey.config.disableMoxyJson";
   public static final String MOXY_JSON_FEATURE_DISABLE_CLIENT = "jersey.config.client.disableMoxyJson";
   public static final String MOXY_JSON_FEATURE_DISABLE_SERVER = "jersey.config.server.disableMoxyJson";
   public static final String OUTBOUND_CONTENT_LENGTH_BUFFER = "jersey.config.contentLength.buffer";
   public static final String OUTBOUND_CONTENT_LENGTH_BUFFER_CLIENT = "jersey.config.client.contentLength.buffer";
   public static final String OUTBOUND_CONTENT_LENGTH_BUFFER_SERVER = "jersey.config.server.contentLength.buffer";
   public static final String PROVIDER_DEFAULT_DISABLE = "jersey.config.disableDefaultProvider";
   public static final String JAXRS_SERVICE_LOADING_ENABLE = "jakarta.ws.rs.loadServices";
   public static final String JSON_JACKSON_ENABLED_MODULES = "jersey.config.json.jackson.enabled.modules";
   public static final String JSON_JACKSON_ENABLED_MODULES_CLIENT = "jersey.config.client.json.jackson.enabled.modules";
   public static final String JSON_JACKSON_ENABLED_MODULES_SERVER = "jersey.config.server.json.jackson.enabled.modules";
   public static final String JSON_JACKSON_DISABLED_MODULES = "jersey.config.json.jackson.disabled.modules";
   public static final String JSON_JACKSON_DISABLED_MODULES_CLIENT = "jersey.config.client.json.jackson.disabled.modules";
   public static final String JSON_JACKSON_DISABLED_MODULES_SERVER = "jersey.config.server.json.jackson.disabled.modules";
   public static final String PARAM_CONVERTERS_THROW_IAE = "jersey.config.paramconverters.throw.iae";

   private CommonProperties() {
   }

   public static <T> T getValue(Map<String, ?> properties, String propertyName, Class<T> type) {
      return PropertiesHelper.getValue(properties, propertyName, type, LEGACY_FALLBACK_MAP);
   }

   public static <T> T getValue(Map<String, ?> properties, String propertyName, T defaultValue) {
      return PropertiesHelper.getValue(properties, propertyName, defaultValue, LEGACY_FALLBACK_MAP);
   }

   public static <T> T getValue(Map<String, ?> properties, RuntimeType runtime, String propertyName, T defaultValue) {
      return PropertiesHelper.getValue(properties, runtime, propertyName, defaultValue, LEGACY_FALLBACK_MAP);
   }

   public static <T> T getValue(Map<String, ?> properties, RuntimeType runtime, String propertyName, T defaultValue, Class<T> type) {
      return PropertiesHelper.getValue(properties, runtime, propertyName, defaultValue, type, LEGACY_FALLBACK_MAP);
   }

   public static <T> T getValue(Map<String, ?> properties, RuntimeType runtime, String propertyName, Class<T> type) {
      return PropertiesHelper.getValue(properties, runtime, propertyName, type, LEGACY_FALLBACK_MAP);
   }

   static {
      LEGACY_FALLBACK_MAP.put("jersey.config.client.contentLength.buffer", "jersey.config.contentLength.buffer.client");
      LEGACY_FALLBACK_MAP.put("jersey.config.server.contentLength.buffer", "jersey.config.contentLength.buffer.server");
      LEGACY_FALLBACK_MAP.put("jersey.config.client.disableAutoDiscovery", "jersey.config.disableAutoDiscovery.client");
      LEGACY_FALLBACK_MAP.put("jersey.config.server.disableAutoDiscovery", "jersey.config.disableAutoDiscovery.server");
      LEGACY_FALLBACK_MAP.put("jersey.config.client.disableJsonProcessing", "jersey.config.disableJsonProcessing.client");
      LEGACY_FALLBACK_MAP.put("jersey.config.server.disableJsonProcessing", "jersey.config.disableJsonProcessing.server");
      LEGACY_FALLBACK_MAP.put("jersey.config.client.disableMetainfServicesLookup", "jersey.config.disableMetainfServicesLookup.client");
      LEGACY_FALLBACK_MAP.put("jersey.config.server.disableMetainfServicesLookup", "jersey.config.disableMetainfServicesLookup.server");
      LEGACY_FALLBACK_MAP.put("jersey.config.client.disableMoxyJson", "jersey.config.disableMoxyJson.client");
      LEGACY_FALLBACK_MAP.put("jersey.config.server.disableMoxyJson", "jersey.config.disableMoxyJson.server");
   }
}
