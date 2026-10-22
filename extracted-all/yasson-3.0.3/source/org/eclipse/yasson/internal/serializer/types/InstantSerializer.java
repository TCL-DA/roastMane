package org.eclipse.yasson.internal.serializer.types;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.eclipse.yasson.internal.JsonbDateFormatter;

class InstantSerializer extends AbstractDateSerializer<Instant> {
   InstantSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   protected Instant toInstant(Instant value) {
      return value;
   }

   protected String formatDefault(Instant value, Locale locale) {
      return DateTimeFormatter.ISO_INSTANT.withLocale(locale).format(value);
   }

   protected String formatWithFormatter(Instant value, DateTimeFormatter formatter) {
      return formatter.withZone(UTC).format(value);
   }

   protected String formatStrictIJson(Instant value) {
      return JsonbDateFormatter.IJSON_DATE_FORMATTER.withZone(UTC).format(value);
   }
}
