package org.eclipse.yasson.internal.deserializer.types;

import jakarta.json.bind.JsonbException;
import java.lang.reflect.Type;
import java.sql.Date;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.JsonbConfigProperties;
import org.eclipse.yasson.internal.JsonbDateFormatter;
import org.eclipse.yasson.internal.deserializer.JustReturn;
import org.eclipse.yasson.internal.deserializer.ModelDeserializer;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

abstract class AbstractDateDeserializer<T> extends TypeDeserializer {
   static final ZoneId UTC = ZoneId.of("UTC");
   private ModelDeserializer<String> actualDeserializer;

   AbstractDateDeserializer(TypeDeserializerBuilder builder) {
      super(builder);
      this.actualDeserializer = this.actualDeserializer(builder.getConfigProperties(), builder.getCustomization());
   }

   AbstractDateDeserializer(Class<Date> clazz) {
      super(new TypeDeserializerBuilder(clazz, null, null, JustReturn.instance()));
      this.actualDeserializer = null;
   }

   private ModelDeserializer<String> actualDeserializer(JsonbConfigProperties properties, Customization customization) {
      JsonbDateFormatter formatter = this.getJsonbDateFormatter(properties, customization);
      if ("##time-in-millis".equals(formatter.getFormat())) {
         return (value, context) -> this.fromInstant(Instant.ofEpochMilli(Long.parseLong(value)));
      } else if (formatter.getDateTimeFormatter() != null) {
         return (value, context) -> this.parseWithFormatterInternal(value, formatter.getDateTimeFormatter());
      } else {
         DateTimeFormatter configDateTimeFormatter = properties.getConfigDateFormatter().getDateTimeFormatter();
         if (configDateTimeFormatter != null) {
            return (value, context) -> this.parseWithFormatterInternal(value, configDateTimeFormatter);
         } else if (properties.isStrictIJson()) {
            return (value, context) -> this.parseWithFormatterInternal(value, JsonbDateFormatter.IJSON_DATE_FORMATTER);
         } else {
            Locale locale = properties.getLocale(formatter.getLocale());
            return (value, context) -> {
               try {
                  return this.parseDefault(value, locale);
               } catch (DateTimeException var5x) {
                  throw new JsonbException(Messages.getMessage(MessageKeys.DATE_PARSE_ERROR, value, this.getType()), var5x);
               }
            };
         }
      }
   }

   private JsonbDateFormatter getJsonbDateFormatter(JsonbConfigProperties properties, Customization customization) {
      return Optional.ofNullable(customization.getDeserializeDateFormatter()).orElse(properties.getConfigDateFormatter());
   }

   @Override
   public Object deserializeStringValue(String value, DeserializationContextImpl context, Type rType) {
      if (this.actualDeserializer == null) {
         this.actualDeserializer = this.actualDeserializer(context.getJsonbContext().getConfigProperties(), context.getCustomization());
      }

      return this.actualDeserializer.deserialize(value, context);
   }

   abstract T fromInstant(Instant var1);

   abstract T parseDefault(String var1, Locale var2);

   abstract T parseWithFormatter(String var1, DateTimeFormatter var2);

   private T parseWithFormatterInternal(String jsonValue, DateTimeFormatter formatter) {
      try {
         return this.parseWithFormatter(jsonValue, formatter);
      } catch (DateTimeException var4) {
         throw new JsonbException(Messages.getMessage(MessageKeys.DATE_PARSE_ERROR, jsonValue, this.getType()), var4);
      }
   }

   protected DateTimeFormatter getZonedFormatter(DateTimeFormatter formatter) {
      return formatter.getZone() != null ? formatter : formatter.withZone(UTC);
   }
}
