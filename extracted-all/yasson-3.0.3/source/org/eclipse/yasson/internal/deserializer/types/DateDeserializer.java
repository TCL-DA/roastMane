package org.eclipse.yasson.internal.deserializer.types;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

class DateDeserializer extends AbstractDateDeserializer<Date> {
   private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

   DateDeserializer(TypeDeserializerBuilder builder) {
      super(builder);
   }

   Date fromInstant(Instant instant) {
      return new Date(instant.toEpochMilli());
   }

   Date parseDefault(String jsonValue, Locale locale) {
      return parseWithOrWithoutZone(jsonValue, DEFAULT_DATE_TIME_FORMATTER.withLocale(locale));
   }

   Date parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
      return parseWithOrWithoutZone(jsonValue, formatter);
   }

   private static Date parseWithOrWithoutZone(String jsonValue, DateTimeFormatter formatter) {
      ZonedDateTime parsed;
      if (formatter.getZone() == null) {
         parsed = ZonedDateTime.parse(jsonValue, formatter.withZone(UTC));
      } else {
         parsed = ZonedDateTime.parse(jsonValue, formatter);
      }

      return Date.from(parsed.toInstant());
   }
}
