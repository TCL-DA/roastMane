package org.eclipse.yasson.internal.serializer.types;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

class OffsetDateTimeSerializer extends AbstractDateSerializer<OffsetDateTime> {
   OffsetDateTimeSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   protected Instant toInstant(OffsetDateTime value) {
      return value.toInstant();
   }

   protected String formatDefault(OffsetDateTime value, Locale locale) {
      return DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(locale).format(value);
   }
}
