package org.eclipse.yasson.internal.serializer.types;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

class SqlTimestampSerializer extends AbstractDateSerializer<Timestamp> {
   private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_DATE_TIME.withZone(UTC);

   SqlTimestampSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   protected Instant toInstant(Timestamp value) {
      return value.toInstant();
   }

   protected String formatDefault(Timestamp value, Locale locale) {
      return DEFAULT_FORMATTER.withLocale(locale).format(this.toInstant(value));
   }
}
