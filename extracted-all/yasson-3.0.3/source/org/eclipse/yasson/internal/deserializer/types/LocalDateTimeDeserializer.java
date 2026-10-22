package org.eclipse.yasson.internal.deserializer.types;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

class LocalDateTimeDeserializer extends AbstractDateDeserializer<LocalDateTime> {
   LocalDateTimeDeserializer(TypeDeserializerBuilder builder) {
      super(builder);
   }

   protected LocalDateTime fromInstant(Instant instant) {
      return LocalDateTime.ofInstant(instant, UTC);
   }

   protected LocalDateTime parseDefault(String jsonValue, Locale locale) {
      return LocalDateTime.parse(jsonValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME.withLocale(locale));
   }

   protected LocalDateTime parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
      return LocalDateTime.parse(jsonValue, formatter);
   }
}
