package org.eclipse.yasson.internal.deserializer.types;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

class InstantDeserializer extends AbstractDateDeserializer<Instant> {
   private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_INSTANT.withZone(UTC);

   InstantDeserializer(TypeDeserializerBuilder builder) {
      super(builder);
   }

   protected Instant fromInstant(Instant instant) {
      return instant;
   }

   protected Instant parseDefault(String jsonValue, Locale locale) {
      return Instant.from(DEFAULT_FORMATTER.withLocale(locale).parse(jsonValue));
   }

   protected Instant parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
      return Instant.from(this.getZonedFormatter(formatter).parse(jsonValue));
   }
}
