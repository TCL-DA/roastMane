package org.eclipse.yasson.internal.deserializer.types;

import java.time.Instant;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

class MonthDayTypeDeserializer extends AbstractDateDeserializer<MonthDay> {
   private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ofPattern("--MM-dd").withZone(UTC);

   MonthDayTypeDeserializer(TypeDeserializerBuilder builder) {
      super(builder);
   }

   protected MonthDay fromInstant(Instant instant) {
      return MonthDay.from(instant.atZone(UTC));
   }

   protected MonthDay parseDefault(String jsonValue, Locale locale) {
      return MonthDay.parse(jsonValue, DEFAULT_FORMAT.withLocale(locale));
   }

   protected MonthDay parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
      return MonthDay.parse(jsonValue, formatter);
   }
}
