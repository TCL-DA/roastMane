package org.glassfish.jersey.client;

import java.util.Map;
import org.glassfish.jersey.internal.util.PropertiesClass;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.internal.util.PropertyAlias;

@PropertiesClass
public final class ClientProperties {
   public static final String FOLLOW_REDIRECTS = "jersey.config.client.followRedirects";
   public static final String READ_TIMEOUT = "jersey.config.client.readTimeout";
   public static final String CONNECT_TIMEOUT = "jersey.config.client.connectTimeout";
   public static final String CHUNKED_ENCODING_SIZE = "jersey.config.client.chunkedEncodingSize";
   public static final int DEFAULT_CHUNK_SIZE = 4096;
   public static final String ASYNC_THREADPOOL_SIZE = "jersey.config.client.async.threadPoolSize";
   public static final String BACKGROUND_SCHEDULER_THREADPOOL_SIZE = "jersey.config.client.backgroundScheduler.threadPoolSize";
   public static final String USE_ENCODING = "jersey.config.client.useEncoding";
   public static final String IGNORE_EXCEPTION_RESPONSE = "jersey.config.client.ignoreExceptionResponse";
   @PropertyAlias
   public static final String FEATURE_AUTO_DISCOVERY_DISABLE = "jersey.config.client.disableAutoDiscovery";
   @PropertyAlias
   public static final String OUTBOUND_CONTENT_LENGTH_BUFFER = "jersey.config.client.contentLength.buffer";
   @PropertyAlias
   public static final String JSON_PROCESSING_FEATURE_DISABLE = "jersey.config.client.disableJsonProcessing";
   @PropertyAlias
   public static final String METAINF_SERVICES_LOOKUP_DISABLE = "jersey.config.client.disableMetainfServicesLookup";
   @PropertyAlias
   public static final String MOXY_JSON_FEATURE_DISABLE = "jersey.config.client.disableMoxyJson";
   public static final String SUPPRESS_HTTP_COMPLIANCE_VALIDATION = "jersey.config.client.suppressHttpComplianceValidation";
   public static final String DIGESTAUTH_URI_CACHE_SIZELIMIT = "jersey.config.client.digestAuthUriCacheSizeLimit";
   public static final String PROXY_URI = "jersey.config.client.proxy.uri";
   public static final String PROXY_USERNAME = "jersey.config.client.proxy.username";
   public static final String PROXY_PASSWORD = "jersey.config.client.proxy.password";
   public static final String REQUEST_ENTITY_PROCESSING = "jersey.config.client.request.entity.processing";
   public static final String EXPECT_100_CONTINUE = "jersey.config.client.request.expect.100.continue.processing";
   public static final String EXPECT_100_CONTINUE_THRESHOLD_SIZE = "jersey.config.client.request.expect.100.continue.threshold.size";
   public static final Long DEFAULT_EXPECT_100_CONTINUE_THRESHOLD_SIZE = 65536L;
   public static final String QUERY_PARAM_STYLE = "jersey.config.client.uri.query.param.style";
   public static final String SNI_HOST_NAME = "jersey.config.client.sniHostName";
   public static final String CONNECTOR_PROVIDER = "jersey.config.client.connector.provider";
   public static final String SSL_CONTEXT_SUPPLIER = "jersey.config.client.ssl.context.supplier";

   private ClientProperties() {
   }

   public static <T> T getValue(Map<String, ?> properties, String key, T defaultValue) {
      return (T)PropertiesHelper.getValue(properties, key, defaultValue, null);
   }

   public static <T> T getValue(Map<String, ?> properties, String key, T defaultValue, Class<T> type) {
      return (T)PropertiesHelper.getValue(properties, key, defaultValue, type, null);
   }

   public static <T> T getValue(Map<String, ?> properties, String key, Class<T> type) {
      return (T)PropertiesHelper.getValue(properties, key, type, null);
   }
}
