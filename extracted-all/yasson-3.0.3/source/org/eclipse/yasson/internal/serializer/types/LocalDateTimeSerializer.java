package org.eclipse.yasson.internal.serializer.types;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.eclipse.yasson.internal.JsonbDateFormatter;

class LocalDateTimeSerializer extends AbstractDateSerializer<LocalDateTime> {
   LocalDateTimeSerializer(TypeSerializerBuilder builder) {
      super(builder);
   }

   protected Instant toInstant(LocalDateTime value) {
      return value.atZone(UTC).toInstant();
   }

   protected String formatDefault(LocalDateTime value, Locale locale) {
      return DateTimeFormatter.ISO_LOCAL_DATE_TIME.withLocale(locale).format(value);
   }

   protected String formatWithFormatter(LocalDateTime value, DateTimeFormatter formatter) {
      return this.getZonedFormatter(formatter).format(value);
   }

   protected String formatStrictIJson(LocalDateTime value) {
      ZonedDateTime zonedDateTime = value.atZone(UTC);
      return JsonbDateFormatter.IJSON_DATE_FORMATTER.format(zonedDateTime);
   }
}
