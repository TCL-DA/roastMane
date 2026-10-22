package org.eclipse.yasson.internal.deserializer.types;

import java.time.Instant;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

class YearMonthTypeDeserializer extends AbstractDateDeserializer<YearMonth> {
   private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM").withZone(UTC);

   YearMonthTypeDeserializer(TypeDeserializerBuilder builder) {
      super(builder);
   }

   protected YearMonth fromInstant(Instant instant) {
      return YearMonth.from(instant.atZone(UTC));
   }

   protected YearMonth parseDefault(String jsonValue, Locale locale) {
      return YearMonth.parse(jsonValue, DEFAULT_FORMAT.withLocale(locale));
   }

   protected YearMonth parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
      return YearMonth.parse(jsonValue, formatter);
   }
}
