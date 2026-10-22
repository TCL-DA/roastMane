package org.eclipse.yasson;

import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.serializer.JsonbSerializer;
import java.util.Map;

public class YassonConfig extends JsonbConfig {
   public static final String FAIL_ON_UNKNOWN_PROPERTIES = "jsonb.fail-on-unknown-properties";
   public static final String USER_TYPE_MAPPING = "jsonb.user-type-mapping";
   public static final String ZERO_TIME_PARSE_DEFAULTING = "jsonb.zero-time-defaulting";
   public static final String NULL_ROOT_SERIALIZER = "yasson.null-root-serializer";
   public static final String EAGER_PARSE_CLASSES = "yasson.eager-parse-classes";
   public static final String FORCE_MAP_ARRAY_SERIALIZER_FOR_NULL_KEYS = "yasson.force-map-array-serializer-for-null-keys";
   public static final String DATE_TIME_IN_MILLIS_AS_A_STRING = "yasson.time-in-millis-as-a-string";

   public YassonConfig withFailOnUnknownProperties(boolean failOnUnknownProperties) {
      this.setProperty("jsonb.fail-on-unknown-properties", failOnUnknownProperties);
      return this;
   }

   public YassonConfig withUserTypeMapping(Map<Class<?>, Class<?>> mapping) {
      this.setProperty("jsonb.user-type-mapping", mapping);
      return this;
   }

   public YassonConfig withZeroTimeParseDefaulting(boolean defaultZeroHour) {
      this.setProperty("jsonb.zero-time-defaulting", defaultZeroHour);
      return this;
   }

   public YassonConfig withNullRootSerializer(JsonbSerializer<?> nullSerializer) {
      this.setProperty("yasson.null-root-serializer", nullSerializer);
      return this;
   }

   public YassonConfig withEagerParsing(Class<?>... classes) {
      this.setProperty("yasson.eager-parse-classes", classes);
      return this;
   }

   public YassonConfig withForceMapArraySerializerForNullKeys(boolean value) {
      this.setProperty("yasson.force-map-array-serializer-for-null-keys", value);
      return this;
   }

   public YassonConfig withTimeInMillisAsAString(boolean value) {
      this.setProperty("yasson.time-in-millis-as-a-string", value);
      return this;
   }
}
