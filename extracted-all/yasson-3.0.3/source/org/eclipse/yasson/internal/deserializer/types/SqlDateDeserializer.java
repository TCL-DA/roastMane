package org.eclipse.yasson.internal.deserializer.types;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.eclipse.yasson.internal.DeserializationContextImpl;

public class SqlDateDeserializer extends AbstractDateDeserializer<Date> implements JsonbDeserializer<Date> {
   private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_DATE.withZone(UTC);

   SqlDateDeserializer(TypeDeserializerBuilder builder) {
      super(builder);
   }

   public SqlDateDeserializer() {
      super(Date.class);
   }

   protected Date fromInstant(Instant instant) {
      return new Date(instant.toEpochMilli());
   }

   protected Date parseDefault(String jsonValue, Locale locale) {
      return Date.valueOf(LocalDate.parse(jsonValue, DEFAULT_FORMATTER.withLocale(locale)));
   }

   protected Date parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
      return Date.valueOf(LocalDate.parse(jsonValue, formatter));
   }

   public Date deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
      DeserializationContextImpl context = (DeserializationContextImpl)ctx;
      return (Date)this.deserialize(parser.getString(), context);
   }
}
