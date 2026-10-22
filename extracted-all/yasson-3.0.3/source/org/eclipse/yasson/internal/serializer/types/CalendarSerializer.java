package org.eclipse.yasson.internal.serializer.types;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Locale;

class CalendarSerializer extends AbstractDateSerializer<Calendar> {
   CalendarSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   protected Instant toInstant(Calendar value) {
      return value.toInstant();
   }

   protected String formatDefault(Calendar value, Locale locale) {
      DateTimeFormatter formatter = !value.isSet(10) && !value.isSet(11) ? DateTimeFormatter.ISO_DATE : DateTimeFormatter.ISO_DATE_TIME;
      return formatter.withZone(value.getTimeZone().toZoneId()).withLocale(locale).format(this.toTemporalAccessor(value));
   }

   protected TemporalAccessor toTemporalAccessor(Calendar object) {
      return ZonedDateTime.ofInstant(Instant.ofEpochMilli(object.getTimeInMillis()), object.getTimeZone().toZoneId());
   }
}
