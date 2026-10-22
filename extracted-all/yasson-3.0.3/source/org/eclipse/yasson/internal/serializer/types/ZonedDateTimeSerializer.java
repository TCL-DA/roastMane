package org.eclipse.yasson.internal.serializer.types;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

class ZonedDateTimeSerializer extends AbstractDateSerializer<ZonedDateTime> {
   ZonedDateTimeSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   protected Instant toInstant(ZonedDateTime value) {
      return value.toInstant();
   }

   protected String formatDefault(ZonedDateTime value, Locale locale) {
      return DateTimeFormatter.ISO_ZONED_DATE_TIME.withLocale(locale).format(value);
   }
}
