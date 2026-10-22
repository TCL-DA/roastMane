package org.eclipse.yasson.internal.serializer.types;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.eclipse.yasson.internal.JsonbDateFormatter;

class LocalDateSerializer extends AbstractDateSerializer<LocalDate> {
   private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE.withZone(UTC);

   LocalDateSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   protected Instant toInstant(LocalDate value) {
      return Instant.from(value.atStartOfDay(UTC));
   }

   protected String formatDefault(LocalDate value, Locale locale) {
      return DEFAULT_FORMAT.withLocale(locale).format(value);
   }

   protected String formatStrictIJson(LocalDate value) {
      ZonedDateTime zonedDateTime = value.atTime(0, 0, 0).atZone(UTC);
      return JsonbDateFormatter.IJSON_DATE_FORMATTER.withZone(UTC).format(zonedDateTime);
   }
}
