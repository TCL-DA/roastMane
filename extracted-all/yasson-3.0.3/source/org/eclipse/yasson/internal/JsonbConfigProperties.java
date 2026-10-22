package org.eclipse.yasson.internal;

import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.config.PropertyNamingStrategy;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import jakarta.json.bind.serializer.JsonbSerializer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import org.eclipse.yasson.internal.model.PropertyModel;
import org.eclipse.yasson.internal.model.ReverseTreeMap;
import org.eclipse.yasson.internal.model.customization.PropertyOrdering;
import org.eclipse.yasson.internal.model.customization.StrategiesProvider;
import org.eclipse.yasson.internal.model.customization.VisibilityStrategiesProvider;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

public class JsonbConfigProperties {
   private static final Map<String, Class<? extends Map>> PROPERTY_ORDER_STRATEGY_MAPS = Map.of(
      "LEXICOGRAPHICAL", TreeMap.class, "REVERSE", ReverseTreeMap.class, "ANY", HashMap.class
   );
   private final JsonbConfig jsonbConfig;
   private final PropertyVisibilityStrategy propertyVisibilityStrategy;
   private final PropertyNamingStrategy propertyNamingStrategy;
   private final PropertyOrdering propertyOrdering;
   private final JsonbDateFormatter dateFormatter;
   private final Locale locale;
   private final String binaryDataStrategy;
   private final boolean nullable;
   private final boolean failOnUnknownProperties;
   private final boolean strictIJson;
   private final boolean zeroTimeDefaulting;
   private final boolean requiredCreatorParameters;
   private final boolean dateInMillisecondsAsString;
   private final Map<Class<?>, Class<?>> userTypeMapping;
   private final Class<?> defaultMapImplType;
   private final JsonbSerializer<Object> nullSerializer;
   private final Set<Class<?>> eagerInitClasses;
   private final boolean forceMapArraySerializerForNullKeys;

   public JsonbConfigProperties(JsonbConfig jsonbConfig) {
      this.jsonbConfig = jsonbConfig;
      this.binaryDataStrategy = this.initBinaryDataStrategy();
      this.propertyNamingStrategy = this.initPropertyNamingStrategy();
      this.propertyVisibilityStrategy = this.initPropertyVisibilityStrategy();
      this.propertyOrdering = new PropertyOrdering(this.initOrderStrategy());
      this.locale = this.initConfigLocale();
      this.dateFormatter = this.initDateFormatter(this.locale);
      this.nullable = this.initConfigNullable();
      this.failOnUnknownProperties = this.initConfigFailOnUnknownProperties();
      this.strictIJson = this.initStrictJson();
      this.userTypeMapping = this.initUserTypeMapping();
      this.zeroTimeDefaulting = this.initZeroTimeDefaultingForJavaTime();
      this.defaultMapImplType = this.initDefaultMapImplType();
      this.nullSerializer = this.initNullSerializer();
      this.eagerInitClasses = this.initEagerInitClasses();
      this.requiredCreatorParameters = this.initRequiredCreatorParameters();
      this.forceMapArraySerializerForNullKeys = this.initForceMapArraySerializerForNullKeys();
      this.dateInMillisecondsAsString = this.initDateInMillisecondsAsString();
   }

   private Class<? extends Map> initDefaultMapImplType() {
      String propertyOrder = this.getConfigProperty("jsonb.property-order-strategy", String.class, "ANY");
      return PROPERTY_ORDER_STRATEGY_MAPS.getOrDefault(propertyOrder, HashMap.class);
   }

   private boolean initZeroTimeDefaultingForJavaTime() {
      return this.getConfigProperty("jsonb.zero-time-defaulting", Boolean.class, false);
   }

   private Map<Class<?>, Class<?>> initUserTypeMapping() {
      return this.getConfigProperty("jsonb.user-type-mapping", Map.class, Collections.emptyMap());
   }

   private JsonbDateFormatter initDateFormatter(Locale locale) {
      String dateFormat = this.getGlobalConfigJsonbDateFormat();
      if (!"##default".equals(dateFormat) && !"##time-in-millis".equals(dateFormat)) {
         DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder().appendPattern(dateFormat);
         if (this.isZeroTimeDefaulting()) {
            builder.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0L);
            builder.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0L);
            builder.parseDefaulting(ChronoField.HOUR_OF_DAY, 0L);
         }

         return new JsonbDateFormatter(builder.toFormatter(locale), dateFormat, locale.toLanguageTag());
      } else {
         return new JsonbDateFormatter(dateFormat, locale.toLanguageTag());
      }
   }

   private String getGlobalConfigJsonbDateFormat() {
      return this.getConfigProperty("jsonb.date-format", String.class, "##default");
   }

   private Consumer<List<PropertyModel>> initOrderStrategy() {
      return StrategiesProvider.getOrderingFunction(this.getPropertyOrderStrategy());
   }

   private String getPropertyOrderStrategy() {
      return this.getConfigProperty("jsonb.property-order-strategy", String.class, "LEXICOGRAPHICAL");
   }

   private PropertyNamingStrategy initPropertyNamingStrategy() {
      Optional<Object> property = this.jsonbConfig.getProperty("jsonb.property-naming-strategy");
      if (property.isEmpty()) {
         return StrategiesProvider.getPropertyNamingStrategy("IDENTITY");
      } else {
         Object propertyNamingStrategy = property.get();
         if (propertyNamingStrategy instanceof String) {
            return StrategiesProvider.getPropertyNamingStrategy((String)propertyNamingStrategy);
         } else if (!(propertyNamingStrategy instanceof PropertyNamingStrategy)) {
            throw new JsonbException(Messages.getMessage(MessageKeys.PROPERTY_NAMING_STRATEGY_INVALID));
         } else {
            return (PropertyNamingStrategy)property.get();
         }
      }
   }

   private PropertyVisibilityStrategy initPropertyVisibilityStrategy() {
      Optional<Object> property = this.jsonbConfig.getProperty("jsonb.property-visibility-strategy");
      if (property.isEmpty()) {
         return null;
      } else {
         Object propertyVisibilityStrategy = property.get();
         if (propertyVisibilityStrategy instanceof String) {
            return VisibilityStrategiesProvider.getStrategy((String)propertyVisibilityStrategy);
         } else if (!(propertyVisibilityStrategy instanceof PropertyVisibilityStrategy)) {
            throw new JsonbException("JsonbConfig.PROPERTY_VISIBILITY_STRATEGY must be instance of " + PropertyVisibilityStrategy.class);
         } else {
            return (PropertyVisibilityStrategy)propertyVisibilityStrategy;
         }
      }
   }

   private String initBinaryDataStrategy() {
      return this.getConfigProperty("jsonb.strict-ijson", Boolean.class, false)
         ? "BASE_64_URL"
         : this.getConfigProperty("jsonb.binary-data-strategy", String.class, "BYTE");
   }

   private boolean initConfigNullable() {
      return this.getConfigProperty("jsonb.null-values", Boolean.class, false);
   }

   private boolean initConfigFailOnUnknownProperties() {
      return this.getConfigProperty("jsonb.fail-on-unknown-properties", Boolean.class, false);
   }

   private boolean initRequiredCreatorParameters() {
      String sysProp = AccessController.doPrivileged((PrivilegedAction<String>)(() -> System.getProperty("jsonb.creator-parameters-required")));
      return sysProp != null ? Boolean.parseBoolean(sysProp) : this.getConfigProperty("jsonb.creator-parameters-required", Boolean.class, false);
   }

   private boolean initDateInMillisecondsAsString() {
      String sysProp = AccessController.doPrivileged((PrivilegedAction<String>)(() -> System.getProperty("yasson.time-in-millis-as-a-string")));
      return sysProp != null ? Boolean.parseBoolean(sysProp) : this.getConfigProperty("yasson.time-in-millis-as-a-string", Boolean.class, false);
   }

   private JsonbSerializer<Object> initNullSerializer() {
      return this.jsonbConfig.getProperty("yasson.null-root-serializer").map(o -> {
         if (!(o instanceof JsonbSerializer)) {
            throw new JsonbException("YassonConfig.NULL_ROOT_SERIALIZER must be instance of " + JsonbSerializer.class + "<Object>");
         } else {
            return (JsonbSerializer<Object>)o;
         }
      }).orElse(null);
   }

   private Set<Class<?>> initEagerInitClasses() {
      Optional<Object> property = this.jsonbConfig.getProperty("yasson.eager-parse-classes");
      if (property.isEmpty()) {
         return Collections.emptySet();
      } else {
         Object eagerInitClasses = property.get();
         if (!(eagerInitClasses instanceof Class[])) {
            throw new JsonbException("YassonConfig.EAGER_PARSE_CLASSES must be instance of Class<?>[]");
         } else {
            return new HashSet<>(Arrays.asList((Class<?>[])eagerInitClasses));
         }
      }
   }

   private boolean initForceMapArraySerializerForNullKeys() {
      return this.getConfigProperty("yasson.force-map-array-serializer-for-null-keys", Boolean.class, false);
   }

   public boolean getConfigNullable() {
      return this.nullable;
   }

   public boolean getConfigFailOnUnknownProperties() {
      return this.failOnUnknownProperties;
   }

   private <T> T getConfigProperty(String propertyName, Class<T> propertyType, T defaultValue) {
      Objects.requireNonNull(defaultValue, "Default value cannot be null");
      return this.jsonbConfig
         .getProperty(propertyName)
         .or(() -> Optional.of(defaultValue))
         .filter(propertyType::isInstance)
         .map(propertyType::cast)
         .orElseThrow(() -> new JsonbException(Messages.getMessage(MessageKeys.JSONB_CONFIG_PROPERTY_INVALID_TYPE, propertyName, propertyType.getSimpleName())));
   }

   public String getBinaryDataStrategy() {
      return this.binaryDataStrategy;
   }

   public Locale getLocale(String locale) {
      return locale.equals("##default") ? this.locale : Locale.forLanguageTag(locale);
   }

   private Locale initConfigLocale() {
      return this.getConfigProperty("jsonb.locale", Locale.class, Locale.getDefault());
   }

   private boolean initStrictJson() {
      return this.getConfigProperty("jsonb.strict-ijson", Boolean.class, false);
   }

   public PropertyVisibilityStrategy getPropertyVisibilityStrategy() {
      return this.propertyVisibilityStrategy;
   }

   public PropertyNamingStrategy getPropertyNamingStrategy() {
      return this.propertyNamingStrategy;
   }

   public JsonbDateFormatter getConfigDateFormatter() {
      return this.dateFormatter;
   }

   public PropertyOrdering getPropertyOrdering() {
      return this.propertyOrdering;
   }

   public boolean isStrictIJson() {
      return this.strictIJson;
   }

   public Map<Class<?>, Class<?>> getUserTypeMapping() {
      return this.userTypeMapping;
   }

   public boolean isZeroTimeDefaulting() {
      return this.zeroTimeDefaulting;
   }

   public Class<?> getDefaultMapImplType() {
      return this.defaultMapImplType;
   }

   public JsonbSerializer<Object> getNullSerializer() {
      return this.nullSerializer;
   }

   public boolean hasRequiredCreatorParameters() {
      return this.requiredCreatorParameters;
   }

   public Set<Class<?>> getEagerInitClasses() {
      return this.eagerInitClasses;
   }

   public boolean isForceMapArraySerializerForNullKeys() {
      return this.forceMapArraySerializerForNullKeys;
   }

   public boolean isDateInMillisecondsAsString() {
      return this.dateInMillisecondsAsString;
   }
}
