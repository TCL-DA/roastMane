package org.eclipse.yasson.internal.serializer.types;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;
import org.eclipse.yasson.internal.JsonbDateFormatter;

class DateSerializer<T extends Date> extends AbstractDateSerializer<T> {
   private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME.withZone(UTC);

   DateSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   protected Instant toInstant(Date value) {
      return value.toInstant();
   }

   protected String formatDefault(Date value, Locale locale) {
      return DEFAULT_DATE_FORMATTER.withLocale(locale).format(this.toInstant(value));
   }

   protected String formatWithFormatter(Date value, DateTimeFormatter formatter) {
      return this.getZonedFormatter(formatter).format(this.toTemporalAccessor(value));
   }

   protected String formatStrictIJson(Date value) {
      return JsonbDateFormatter.IJSON_DATE_FORMATTER.withZone(UTC).format(this.toTemporalAccessor(value));
   }

   protected TemporalAccessor toTemporalAccessor(Date object) {
      return this.toInstant(object);
   }
}
