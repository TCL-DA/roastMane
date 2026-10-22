package org.eclipse.yasson.internal.serializer.types;

import java.time.Instant;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

class MonthDayTypeSerializer extends AbstractDateSerializer<MonthDay> {
   private static final int YEAR_NUMBER = Year.now().getValue();
   private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ofPattern("--MM-dd").withZone(UTC);

   MonthDayTypeSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   protected Instant toInstant(MonthDay value) {
      return value.atYear(YEAR_NUMBER).atStartOfDay(UTC).toInstant();
   }

   protected String formatDefault(MonthDay value, Locale locale) {
      return DEFAULT_FORMAT.withLocale(locale).format(value);
   }
}
