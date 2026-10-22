package org.eclipse.yasson.internal.deserializer.types;

import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.Function;
import javax.xml.datatype.XMLGregorianCalendar;
import org.eclipse.yasson.internal.BuiltInTypes;
import org.eclipse.yasson.internal.JsonbConfigProperties;
import org.eclipse.yasson.internal.deserializer.JustReturn;
import org.eclipse.yasson.internal.deserializer.ModelDeserializer;
import org.eclipse.yasson.internal.deserializer.NullCheckDeserializer;
import org.eclipse.yasson.internal.deserializer.PositionChecker;
import org.eclipse.yasson.internal.deserializer.ValueExtractor;
import org.eclipse.yasson.internal.model.customization.Customization;

public class TypeDeserializers {
   private static final Map<Class<?>, Function<TypeDeserializerBuilder, TypeDeserializer>> DESERIALIZERS = new HashMap<>();
   private static final Map<Class<?>, Class<?>> OPTIONAL_TYPES = new HashMap<>();

   private TypeDeserializers() {
      throw new IllegalStateException("Utility classes cannot be instantiated");
   }

   public static ModelDeserializer<JsonParser> getTypeDeserializer(
      Class<?> clazz, Customization customization, JsonbConfigProperties properties, ModelDeserializer<Object> delegate, Set<Event> events
   ) {
      Event[] eventArray = events.toArray(new Event[0]);
      if (OPTIONAL_TYPES.containsKey(clazz)) {
         Class<?> optionalType = OPTIONAL_TYPES.get(clazz);
         TypeDeserializerBuilder builder = new TypeDeserializerBuilder(optionalType, customization, properties, JustReturn.instance());
         ValueExtractor valueExtractor = new ValueExtractor(DESERIALIZERS.get(optionalType).apply(builder));
         PositionChecker positionChecker = new PositionChecker(valueExtractor, clazz, eventArray);
         if (OptionalLong.class.equals(clazz)) {
            return new OptionalLongDeserializer(positionChecker, delegate);
         } else if (OptionalInt.class.equals(clazz)) {
            return new OptionalIntDeserializer(positionChecker, delegate);
         } else if (OptionalDouble.class.equals(clazz)) {
            return new OptionalDoubleDeserializer(positionChecker, delegate);
         } else {
            throw new JsonbException("Unsupported Optional type for deserialization: " + clazz);
         }
      } else {
         TypeDeserializerBuilder builder = new TypeDeserializerBuilder(clazz, customization, properties, delegate);
         if (DESERIALIZERS.containsKey(clazz)) {
            ValueExtractor valueExtractor = new ValueExtractor(DESERIALIZERS.get(clazz).apply(builder));
            return new NullCheckDeserializer(new PositionChecker(valueExtractor, clazz, eventArray), delegate);
         } else if (JsonValue.class.equals(clazz)) {
            return new JsonValueDeserializer(builder, JsonValue.NULL);
         } else if (JsonValue.class.isAssignableFrom(clazz)) {
            return new JsonValueDeserializer(builder, null);
         } else {
            ModelDeserializer<JsonParser> deserializer = assignableCases(builder, eventArray);
            return deserializer != null ? new NullCheckDeserializer(deserializer, delegate) : null;
         }
      }
   }

   private static ModelDeserializer<JsonParser> assignableCases(TypeDeserializerBuilder builder, Event[] checker) {
      if (Enum.class.isAssignableFrom(builder.getClazz())) {
         return new PositionChecker(new ValueExtractor(new EnumDeserializer(builder)), builder.getClazz(), checker);
      } else {
         return Object.class.equals(builder.getClazz()) ? new ObjectTypeDeserializer(builder) : null;
      }
   }

   static {
      DESERIALIZERS.put(BigInteger.class, BigIntegerDeserializer::new);
      DESERIALIZERS.put(BigDecimal.class, BigDecimalDeserializer::new);
      DESERIALIZERS.put(Boolean.class, BooleanDeserializer::new);
      DESERIALIZERS.put(boolean.class, BooleanDeserializer::new);
      DESERIALIZERS.put(Byte.class, ByteDeserializer::new);
      DESERIALIZERS.put(byte.class, ByteDeserializer::new);
      DESERIALIZERS.put(Calendar.class, CalendarDeserializer::new);
      DESERIALIZERS.put(char.class, CharDeserializer::new);
      DESERIALIZERS.put(Character.class, CharDeserializer::new);
      DESERIALIZERS.put(Date.class, DateDeserializer::new);
      DESERIALIZERS.put(Double.class, DoubleDeserializer::new);
      DESERIALIZERS.put(double.class, DoubleDeserializer::new);
      DESERIALIZERS.put(Duration.class, DurationDeserializer::new);
      DESERIALIZERS.put(Float.class, FloatDeserializer::new);
      DESERIALIZERS.put(float.class, FloatDeserializer::new);
      DESERIALIZERS.put(GregorianCalendar.class, CalendarDeserializer::new);
      DESERIALIZERS.put(Instant.class, InstantDeserializer::new);
      DESERIALIZERS.put(Integer.class, IntegerDeserializer::new);
      DESERIALIZERS.put(int.class, IntegerDeserializer::new);
      DESERIALIZERS.put(LocalDate.class, LocalDateDeserializer::new);
      DESERIALIZERS.put(LocalDateTime.class, LocalDateTimeDeserializer::new);
      DESERIALIZERS.put(LocalTime.class, LocalTimeDeserializer::new);
      DESERIALIZERS.put(Long.class, LongDeserializer::new);
      DESERIALIZERS.put(long.class, LongDeserializer::new);
      DESERIALIZERS.put(MonthDay.class, MonthDayTypeDeserializer::new);
      DESERIALIZERS.put(Number.class, NumberDeserializer::new);
      DESERIALIZERS.put(OffsetDateTime.class, OffsetDateTimeDeserializer::new);
      DESERIALIZERS.put(OffsetTime.class, OffsetTimeDeserializer::new);
      DESERIALIZERS.put(Path.class, PathDeserializer::new);
      DESERIALIZERS.put(Period.class, PeriodDeserializer::new);
      DESERIALIZERS.put(Short.class, ShortDeserializer::new);
      DESERIALIZERS.put(short.class, ShortDeserializer::new);
      DESERIALIZERS.put(String.class, StringDeserializer::new);
      DESERIALIZERS.put(SimpleTimeZone.class, TimeZoneDeserializer::new);
      DESERIALIZERS.put(TimeZone.class, TimeZoneDeserializer::new);
      DESERIALIZERS.put(URI.class, UriDeserializer::new);
      DESERIALIZERS.put(URL.class, UrlDeserializer::new);
      DESERIALIZERS.put(UUID.class, UuidDeserializer::new);
      if (BuiltInTypes.isClassAvailable("javax.xml.datatype.XMLGregorianCalendar")) {
         DESERIALIZERS.put(XMLGregorianCalendar.class, XmlGregorianCalendarDeserializer::new);
      }

      DESERIALIZERS.put(YearMonth.class, YearMonthTypeDeserializer::new);
      DESERIALIZERS.put(ZonedDateTime.class, ZonedDateTimeDeserializer::new);
      DESERIALIZERS.put(ZoneId.class, ZoneIdDeserializer::new);
      DESERIALIZERS.put(ZoneOffset.class, ZoneOffsetDeserializer::new);
      if (BuiltInTypes.isClassAvailable("java.sql.Date")) {
         DESERIALIZERS.put(java.sql.Date.class, SqlDateDeserializer::new);
         DESERIALIZERS.put(Timestamp.class, SqlTimestampDeserializer::new);
      }

      OPTIONAL_TYPES.put(OptionalLong.class, Long.class);
      OPTIONAL_TYPES.put(OptionalInt.class, Integer.class);
      OPTIONAL_TYPES.put(OptionalDouble.class, Double.class);
   }
}
