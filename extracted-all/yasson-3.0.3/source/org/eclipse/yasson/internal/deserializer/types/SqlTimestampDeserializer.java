package org.eclipse.yasson.internal.deserializer.types;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

class SqlTimestampDeserializer extends AbstractDateDeserializer<Timestamp> {
   private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_DATE_TIME.withZone(UTC);

   SqlTimestampDeserializer(TypeDeserializerBuilder builder) {
      super(builder);
   }

   protected Timestamp fromInstant(Instant instant) {
      return Timestamp.from(instant);
   }

   protected Timestamp parseDefault(String jsonValue, Locale locale) {
      TemporalAccessor parsed = DEFAULT_FORMATTER.withLocale(locale).parse(jsonValue);
      return Timestamp.from(this.getInstant(parsed));
   }

   protected Timestamp parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
      TemporalAccessor parsed = this.getZonedFormatter(formatter).parse(jsonValue);
      return Timestamp.from(this.getInstant(parsed));
   }

   private Instant getInstant(TemporalAccessor parsed) {
      LocalDateTime local = LocalDateTime.from(parsed);
      return local.atZone(ZoneId.of("UTC")).toInstant();
   }
}
