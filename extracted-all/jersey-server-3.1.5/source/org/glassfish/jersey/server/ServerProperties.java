package org.glassfish.jersey.server;

import jakarta.ws.rs.RuntimeType;
import java.util.Map;
import org.glassfish.jersey.internal.util.PropertiesClass;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.internal.util.PropertyAlias;

@PropertiesClass
public final class ServerProperties {
   public static final String PROVIDER_PACKAGES = "jersey.config.server.provider.packages";
   public static final String PROVIDER_SCANNING_RECURSIVE = "jersey.config.server.provider.scanning.recursive";
   public static final String PROVIDER_CLASSPATH = "jersey.config.server.provider.classpath";
   public static final String PROVIDER_CLASSNAMES = "jersey.config.server.provider.classnames";
   public static final String MEDIA_TYPE_MAPPINGS = "jersey.config.server.mediaTypeMappings";
   public static final String LANGUAGE_MAPPINGS = "jersey.config.server.languageMappings";
   public static final String HTTP_METHOD_OVERRIDE = "jersey.config.server.httpMethodOverride";
   public static final String WADL_GENERATOR_CONFIG = "jersey.config.server.wadl.generatorConfig";
   public static final String WADL_FEATURE_DISABLE = "jersey.config.server.wadl.disableWadl";
   public static final String BV_FEATURE_DISABLE = "jersey.config.beanValidation.disable.server";
   public static final String BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK = "jersey.config.beanValidation.disable.validateOnExecutableCheck.server";
   public static final String BV_SEND_ERROR_IN_RESPONSE = "jersey.config.beanValidation.enableOutputValidationErrorEntity.server";
   public static final String REDUCE_CONTEXT_PATH_SLASHES_ENABLED = "jersey.config.server.reduceContextPathSlashes.enabled";
   @PropertyAlias
   public static final String FEATURE_AUTO_DISCOVERY_DISABLE = "jersey.config.server.disableAutoDiscovery";
   @PropertyAlias
   public static final String OUTBOUND_CONTENT_LENGTH_BUFFER = "jersey.config.server.contentLength.buffer";
   @PropertyAlias
   public static final String JSON_PROCESSING_FEATURE_DISABLE = "jersey.config.server.disableJsonProcessing";
   @PropertyAlias
   public static final String METAINF_SERVICES_LOOKUP_DISABLE = "jersey.config.server.disableMetainfServicesLookup";
   @PropertyAlias
   public static final String MOXY_JSON_FEATURE_DISABLE = "jersey.config.server.disableMoxyJson";
   public static final String RESOURCE_VALIDATION_DISABLE = "jersey.config.server.resource.validation.disable";
   public static final String RESOURCE_VALIDATION_IGNORE_ERRORS = "jersey.config.server.resource.validation.ignoreErrors";
   public static final String MONITORING_ENABLED = "jersey.config.server.monitoring.enabled";
   public static final String MONITORING_STATISTICS_ENABLED = "jersey.config.server.monitoring.statistics.enabled";
   public static final String MONITORING_STATISTICS_MBEANS_ENABLED = "jersey.config.server.monitoring.statistics.mbeans.enabled";
   public static final String MONITORING_STATISTICS_REFRESH_INTERVAL = "jersey.config.server.monitoring.statistics.refresh.interval";
   public static final String APPLICATION_NAME = "jersey.config.server.application.name";
   public static final String TRACING = "jersey.config.server.tracing.type";
   public static final String TRACING_THRESHOLD = "jersey.config.server.tracing.threshold";
   public static final String RESPONSE_SET_STATUS_OVER_SEND_ERROR = "jersey.config.server.response.setStatusOverSendError";
   public static final String PROCESSING_RESPONSE_ERRORS_ENABLED = "jersey.config.server.exception.processResponseErrors";
   public static final String SUBRESOURCE_LOCATOR_CACHE_SIZE = "jersey.config.server.subresource.cache.size";
   public static final int SUBRESOURCE_LOCATOR_DEFAULT_CACHE_SIZE = 64;
   public static final String SUBRESOURCE_LOCATOR_CACHE_AGE = "jersey.config.server.subresource.cache.age";
   public static final String SUBRESOURCE_LOCATOR_CACHE_JERSEY_RESOURCE_ENABLED = "jersey.config.server.subresource.cache.jersey.resource.enabled";
   public static final String LOCATION_HEADER_RELATIVE_URI_RESOLUTION_RFC7231 = "jersey.config.server.headers.location.relative.resolution.rfc7231";
   public static final String LOCATION_HEADER_RELATIVE_URI_RESOLUTION_DISABLED = "jersey.config.server.headers.location.relative.resolution.disabled";
   public static final String UNWRAP_COMPLETION_STAGE_IN_WRITER_ENABLE = "jersey.config.server.unwrap.completion.stage.writer.enable";
   public static final String EMPTY_REQUEST_MEDIA_TYPE_MATCHES_ANY_CONSUMES = "jersey.config.server.empty.request.media.matches.any.consumes";
   public static final String WEBSERVER_ALLOW_PRIVILEGED_PORTS = "jersey.config.server.bootstrap.webserver.allow.privileged.ports";
   public static final String WEBSERVER_AUTO_START = "jersey.config.server.bootstrap.webserver.autostart";
   public static final String WEBSERVER_CLASS = "jersey.config.server.bootstrap.webserver.class";
   public static final String COLLISION_BUFFER_POWER_JVM_ARG = "jersey.config.server.monitoring.collision.buffer.power";

   private ServerProperties() {
   }

   public static <T> T getValue(Map<String, ?> properties, String key, Class<T> type) {
      return (T)PropertiesHelper.getValue(properties, key, type, null);
   }

   public static <T> T getValue(Map<String, ?> properties, String key, T defaultValue) {
      return (T)PropertiesHelper.getValue(properties, key, defaultValue, null);
   }

   public static <T> T getValue(Map<String, ?> properties, String key, T defaultValue, Class<T> type) {
      return (T)PropertiesHelper.getValue(properties, key, defaultValue, type, null);
   }

   public static <T> T getValue(Map<String, ?> properties, RuntimeType runtimeType, String key, T defaultValue, Class<T> type) {
      return (T)PropertiesHelper.getValue(properties, runtimeType, key, defaultValue, type, null);
   }
}
