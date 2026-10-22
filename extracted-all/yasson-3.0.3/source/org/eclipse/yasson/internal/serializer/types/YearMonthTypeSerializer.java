package org.eclipse.yasson.internal.serializer.types;

import java.time.Instant;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

class YearMonthTypeSerializer extends AbstractDateSerializer<YearMonth> {
   private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM").withZone(UTC);

   YearMonthTypeSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   protected Instant toInstant(YearMonth value) {
      return value.atDay(1).atStartOfDay(UTC).toInstant();
   }

   protected String formatDefault(YearMonth value, Locale locale) {
      return DEFAULT_FORMAT.withLocale(locale).format(value);
   }
}
