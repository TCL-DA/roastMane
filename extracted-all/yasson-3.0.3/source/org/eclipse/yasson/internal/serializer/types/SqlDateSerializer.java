package org.eclipse.yasson.internal.serializer.types;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

class SqlDateSerializer extends DateSerializer<Date> {
   SqlDateSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   @Override
   protected Instant toInstant(Date value) {
      return value instanceof java.sql.Date ? Instant.ofEpochMilli(value.getTime()) : super.toInstant(value);
   }

   @Override
   protected String formatDefault(Date value, Locale locale) {
      return value instanceof java.sql.Date ? value.toString() + "Z" : super.formatDefault(value, locale);
   }

   @Override
   protected String formatWithFormatter(Date value, DateTimeFormatter formatter) {
      return value instanceof java.sql.Date ? ((java.sql.Date)value).toLocalDate().format(formatter) : super.formatWithFormatter(value, formatter);
   }
}
