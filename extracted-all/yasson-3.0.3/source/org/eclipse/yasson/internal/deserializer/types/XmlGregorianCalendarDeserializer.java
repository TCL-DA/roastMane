package org.eclipse.yasson.internal.deserializer.types;

import jakarta.json.bind.JsonbException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class XmlGregorianCalendarDeserializer extends AbstractDateDeserializer<XMLGregorianCalendar> {
   private static final LocalTime ZERO_LOCAL_TIME = LocalTime.parse("00:00:00");
   private final Calendar calendarTemplate = new GregorianCalendar();
   private final DatatypeFactory datatypeFactory;

   XmlGregorianCalendarDeserializer(TypeDeserializerBuilder builder) {
      super(builder);
      this.calendarTemplate.clear();
      this.calendarTemplate.setTimeZone(TimeZone.getTimeZone(UTC));

      try {
         this.datatypeFactory = DatatypeFactory.newInstance();
      } catch (DatatypeConfigurationException var3) {
         throw new JsonbException(Messages.getMessage(MessageKeys.DATATYPE_FACTORY_CREATION_FAILED), var3);
      }
   }

   protected XMLGregorianCalendar fromInstant(Instant instant) {
      GregorianCalendar calendar = (GregorianCalendar)this.calendarTemplate.clone();
      calendar.setTimeInMillis(instant.toEpochMilli());
      return this.datatypeFactory.newXMLGregorianCalendar(calendar);
   }

   protected XMLGregorianCalendar parseDefault(String jsonValue, Locale locale) {
      DateTimeFormatter formatter = jsonValue.contains("T") ? DateTimeFormatter.ISO_DATE_TIME : DateTimeFormatter.ISO_DATE;
      return this.parseWithFormatter(jsonValue, formatter.withLocale(locale));
   }

   protected XMLGregorianCalendar parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
      TemporalAccessor parsed = formatter.parse(jsonValue);
      LocalTime time = parsed.query(TemporalQueries.localTime());
      ZoneId zone = parsed.query(TemporalQueries.zone());
      if (zone == null) {
         zone = UTC;
      }

      if (time == null) {
         time = ZERO_LOCAL_TIME;
      }

      ZonedDateTime result = LocalDate.from(parsed).atTime(time).atZone(zone);
      return this.datatypeFactory.newXMLGregorianCalendar(GregorianCalendar.from(result));
   }
}
