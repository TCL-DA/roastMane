package jakarta.json.bind;

import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.config.PropertyNamingStrategy;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class JsonbConfig {
   private final Map<String, Object> configuration = new HashMap<>();
   public static final String FORMATTING = "jsonb.formatting";
   public static final String ENCODING = "jsonb.encoding";
   public static final String PROPERTY_NAMING_STRATEGY = "jsonb.property-naming-strategy";
   public static final String PROPERTY_ORDER_STRATEGY = "jsonb.property-order-strategy";
   public static final String NULL_VALUES = "jsonb.null-values";
   public static final String STRICT_IJSON = "jsonb.strict-ijson";
   public static final String PROPERTY_VISIBILITY_STRATEGY = "jsonb.property-visibility-strategy";
   public static final String ADAPTERS = "jsonb.adapters";
   public static final String SERIALIZERS = "jsonb.serializers";
   public static final String DESERIALIZERS = "jsonb.derializers";
   public static final String BINARY_DATA_STRATEGY = "jsonb.binary-data-strategy";
   public static final String DATE_FORMAT = "jsonb.date-format";
   public static final String LOCALE = "jsonb.locale";
   public static final String CREATOR_PARAMETERS_REQUIRED = "jsonb.creator-parameters-required";

   public final JsonbConfig setProperty(String name, Object value) {
      this.configuration.put(name, value);
      return this;
   }

   public final Optional<Object> getProperty(String name) {
      return Optional.ofNullable(this.configuration.get(name));
   }

   public final Map<String, Object> getAsMap() {
      return Collections.unmodifiableMap(this.configuration);
   }

   public final JsonbConfig withFormatting(Boolean formatted) {
      return this.setProperty("jsonb.formatting", formatted);
   }

   public final JsonbConfig withNullValues(Boolean serializeNullValues) {
      return this.setProperty("jsonb.null-values", serializeNullValues);
   }

   public final JsonbConfig withEncoding(String encoding) {
      return this.setProperty("jsonb.encoding", encoding);
   }

   public final JsonbConfig withStrictIJSON(Boolean enabled) {
      return this.setProperty("jsonb.strict-ijson", enabled);
   }

   public final JsonbConfig withPropertyNamingStrategy(PropertyNamingStrategy propertyNamingStrategy) {
      return this.setProperty("jsonb.property-naming-strategy", propertyNamingStrategy);
   }

   public final JsonbConfig withPropertyNamingStrategy(String propertyNamingStrategy) {
      return this.setProperty("jsonb.property-naming-strategy", propertyNamingStrategy);
   }

   public final JsonbConfig withPropertyOrderStrategy(String propertyOrderStrategy) {
      return this.setProperty("jsonb.property-order-strategy", propertyOrderStrategy);
   }

   public final JsonbConfig withPropertyVisibilityStrategy(PropertyVisibilityStrategy propertyVisibilityStrategy) {
      return this.setProperty("jsonb.property-visibility-strategy", propertyVisibilityStrategy);
   }

   public final JsonbConfig withAdapters(JsonbAdapter... adapters) {
      this.mergeProperties("jsonb.adapters", adapters, JsonbAdapter.class);
      return this;
   }

   public final JsonbConfig withSerializers(JsonbSerializer... serializers) {
      this.mergeProperties("jsonb.serializers", serializers, JsonbSerializer.class);
      return this;
   }

   public final JsonbConfig withDeserializers(JsonbDeserializer... deserializers) {
      this.mergeProperties("jsonb.derializers", deserializers, JsonbDeserializer.class);
      return this;
   }

   public final JsonbConfig withBinaryDataStrategy(String binaryDataStrategy) {
      return this.setProperty("jsonb.binary-data-strategy", binaryDataStrategy);
   }

   public final JsonbConfig withDateFormat(String dateFormat, Locale locale) {
      return this.setProperty("jsonb.date-format", dateFormat).setProperty("jsonb.locale", locale != null ? locale : Locale.getDefault());
   }

   public final JsonbConfig withLocale(Locale locale) {
      return this.setProperty("jsonb.locale", locale);
   }

   public final JsonbConfig withCreatorParametersRequired(boolean requiredParameters) {
      return this.setProperty("jsonb.creator-parameters-required", requiredParameters);
   }

   private <T> void mergeProperties(String propertyKey, T[] values, Class<T> tClass) {
      Optional<Object> property = this.getProperty(propertyKey);
      if (!property.isPresent()) {
         this.setProperty(propertyKey, values);
      } else {
         T[] storedValues = (T[])property.get();
         T[] newValues = (T[])Array.newInstance(tClass, storedValues.length + values.length);
         System.arraycopy(storedValues, 0, newValues, 0, storedValues.length);
         System.arraycopy(values, 0, newValues, storedValues.length, values.length);
         this.setProperty(propertyKey, newValues);
      }
   }
}
