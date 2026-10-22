package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.eclipse.yasson.internal.JsonbConfigProperties;
import org.eclipse.yasson.internal.JsonbDateFormatter;
import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.model.customization.Customization;

abstract class AbstractDateSerializer<T> extends TypeSerializer<T> {
   static final ZoneId UTC = ZoneId.of("UTC");
   private final Function<T, String> toStringSerializer;
   private final BiConsumer<T, JsonGenerator> valueWriter;

   AbstractDateSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
      Customization customization = serializerBuilder.getCustomization();
      JsonbConfigProperties properties = serializerBuilder.getJsonbContext().getConfigProperties();
      JsonbDateFormatter formatter = this.getJsonbDateFormatter(properties, customization);
      this.toStringSerializer = this.valueSerializer(serializerBuilder);
      if ("##time-in-millis".equals(formatter.getFormat()) && !properties.isDateInMillisecondsAsString()) {
         this.valueWriter = (value, generator) -> generator.write(this.toInstant(value).toEpochMilli());
      } else {
         this.valueWriter = (value, generator) -> generator.write(this.toStringSerializer.apply(value));
      }
   }

   private Function<T, String> valueSerializer(TypeSerializerBuilder serializerBuilder) {
      Customization customization = serializerBuilder.getCustomization();
      JsonbConfigProperties properties = serializerBuilder.getJsonbContext().getConfigProperties();
      JsonbDateFormatter formatter = this.getJsonbDateFormatter(properties, customization);
      if ("##time-in-millis".equals(formatter.getFormat())) {
         return value -> String.valueOf(this.toInstant(value).toEpochMilli());
      } else if (formatter.getDateTimeFormatter() != null) {
         DateTimeFormatter dateTimeFormatter = formatter.getDateTimeFormatter();
         return value -> this.formatWithFormatter(value, dateTimeFormatter);
      } else {
         DateTimeFormatter configDateTimeFormatter = properties.getConfigDateFormatter().getDateTimeFormatter();
         if (configDateTimeFormatter != null) {
            return value -> this.formatWithFormatter(value, configDateTimeFormatter);
         } else if (properties.isStrictIJson()) {
            return this::formatStrictIJson;
         } else {
            Locale locale = properties.getLocale(formatter.getLocale());
            return value -> this.formatDefault(value, locale);
         }
      }
   }

   private JsonbDateFormatter getJsonbDateFormatter(JsonbConfigProperties properties, Customization customization) {
      return Optional.ofNullable(customization.getSerializeDateFormatter()).orElse(properties.getConfigDateFormatter());
   }

   protected TemporalAccessor toTemporalAccessor(T value) {
      return (TemporalAccessor)value;
   }

   protected abstract Instant toInstant(T var1);

   protected abstract String formatDefault(T var1, Locale var2);

   protected String formatWithFormatter(T value, DateTimeFormatter formatter) {
      return formatter.format(this.toTemporalAccessor(value));
   }

   protected String formatStrictIJson(T value) {
      return JsonbDateFormatter.IJSON_DATE_FORMATTER.format(this.toTemporalAccessor(value));
   }

   protected DateTimeFormatter getZonedFormatter(DateTimeFormatter formatter) {
      return formatter.getZone() != null ? formatter : formatter.withZone(UTC);
   }

   @Override
   void serializeValue(T value, JsonGenerator generator, SerializationContextImpl context) {
      this.valueWriter.accept(value, generator);
   }

   @Override
   void serializeKey(T key, JsonGenerator generator, SerializationContextImpl context) {
      generator.writeKey(this.toStringSerializer.apply(key));
   }
}
