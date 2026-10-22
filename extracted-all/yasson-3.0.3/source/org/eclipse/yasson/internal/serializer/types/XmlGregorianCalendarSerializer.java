package org.eclipse.yasson.internal.serializer.types;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import javax.xml.datatype.XMLGregorianCalendar;

class XmlGregorianCalendarSerializer extends AbstractDateSerializer<XMLGregorianCalendar> {
   XmlGregorianCalendarSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   protected Instant toInstant(XMLGregorianCalendar value) {
      return Instant.ofEpochMilli(value.toGregorianCalendar().getTimeInMillis());
   }

   protected String formatDefault(XMLGregorianCalendar value, Locale locale) {
      DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
      return formatter.withLocale(locale).withZone(value.toGregorianCalendar().getTimeZone().toZoneId()).format(this.toTemporalAccessor(value));
   }

   protected TemporalAccessor toTemporalAccessor(XMLGregorianCalendar object) {
      return ZonedDateTime.ofInstant(
         Instant.ofEpochMilli(object.toGregorianCalendar().getTimeInMillis()), object.toGregorianCalendar().getTimeZone().toZoneId()
      );
   }
}
