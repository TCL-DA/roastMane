package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.JsonNumber;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbException;
import java.lang.reflect.Type;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.Function;
import javax.xml.datatype.XMLGregorianCalendar;
import org.eclipse.yasson.internal.BuiltInTypes;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.serializer.ModelSerializer;
import org.eclipse.yasson.internal.serializer.SerializationModelCreator;

public class TypeSerializers {
   private static final Map<Class<?>, Function<TypeSerializerBuilder, ModelSerializer>> SERIALIZERS;
   private static final Set<Class<?>> SUPPORTED_MAP_KEYS;
   private static final Map<Class<?>, Class<?>> OPTIONALS;

   private TypeSerializers() {
      throw new IllegalStateException("Util class cannot be instantiated");
   }

   public static boolean isSupportedMapKey(Class<?> clazz) {
      return Enum.class.isAssignableFrom(clazz) || SUPPORTED_MAP_KEYS.contains(clazz);
   }

   public static ModelSerializer getTypeSerializer(Class<?> clazz, Customization customization, JsonbContext jsonbContext) {
      return getTypeSerializer(Collections.emptyList(), clazz, customization, jsonbContext, false);
   }

   public static ModelSerializer getTypeSerializer(List<Type> chain, Class<?> clazz, Customization customization, JsonbContext jsonbContext, boolean key) {
      Class<?> current = clazz;
      List<Type> chainClone = new LinkedList<>(chain);
      TypeSerializerBuilder builder = new TypeSerializerBuilder(chainClone, clazz, customization, jsonbContext, key);
      ModelSerializer typeSerializer = null;
      if (Object.class.equals(clazz)) {
         return SERIALIZERS.get(clazz).apply(builder);
      } else if (OPTIONALS.containsKey(clazz)) {
         Class<?> optionalInner = OPTIONALS.get(clazz);
         ModelSerializer serializer = getTypeSerializer(chainClone, optionalInner, customization, jsonbContext, key);
         if (OptionalInt.class.equals(clazz)) {
            return new OptionalIntSerializer(serializer);
         } else if (OptionalLong.class.equals(clazz)) {
            return new OptionalLongSerializer(serializer);
         } else if (OptionalDouble.class.equals(clazz)) {
            return new OptionalDoubleSerializer(serializer);
         } else {
            throw new JsonbException("Unsupported Optional type for serialization: " + clazz);
         }
      } else {
         if (Enum.class.isAssignableFrom(clazz)) {
            typeSerializer = new EnumSerializer(builder);
         } else if (JsonValue.class.isAssignableFrom(clazz)) {
            typeSerializer = new JsonValueSerializer(builder);
         }

         if (typeSerializer == null) {
            do {
               if (SERIALIZERS.containsKey(current)) {
                  typeSerializer = SERIALIZERS.get(current).apply(builder);
                  break;
               }

               current = current.getSuperclass();
            } while (!Object.class.equals(current) && current != null);
         }

         if (key) {
            return typeSerializer;
         } else {
            return typeSerializer == null ? null : SerializationModelCreator.wrapInCommonSet(typeSerializer, customization, jsonbContext);
         }
      }
   }

   static {
      Map<Class<?>, Function<TypeSerializerBuilder, ModelSerializer>> cache = new HashMap<>();
      cache.put(Byte.class, ByteSerializer::new);
      cache.put(byte.class, ByteSerializer::new);
      cache.put(BigDecimal.class, BigDecimalSerializer::new);
      cache.put(BigInteger.class, BigIntegerSerializer::new);
      cache.put(Boolean.class, BooleanSerializer::new);
      cache.put(boolean.class, BooleanSerializer::new);
      cache.put(Calendar.class, CalendarSerializer::new);
      cache.put(Character.class, CharSerializer::new);
      cache.put(char.class, CharSerializer::new);
      cache.put(Date.class, DateSerializer::new);
      cache.put(Double.class, DoubleSerializer::new);
      cache.put(double.class, DoubleSerializer::new);
      cache.put(Duration.class, DurationSerializer::new);
      cache.put(Float.class, FloatSerializer::new);
      cache.put(float.class, FloatSerializer::new);
      cache.put(Integer.class, IntegerSerializer::new);
      cache.put(int.class, IntegerSerializer::new);
      cache.put(Instant.class, InstantSerializer::new);
      cache.put(LocalDateTime.class, LocalDateTimeSerializer::new);
      cache.put(LocalDate.class, LocalDateSerializer::new);
      cache.put(LocalTime.class, LocalTimeSerializer::new);
      cache.put(Long.class, LongSerializer::new);
      cache.put(long.class, LongSerializer::new);
      cache.put(MonthDay.class, MonthDayTypeSerializer::new);
      cache.put(Number.class, NumberSerializer::new);
      cache.put(Object.class, ObjectTypeSerializer::new);
      cache.put(OffsetDateTime.class, OffsetDateTimeSerializer::new);
      cache.put(OffsetTime.class, OffsetTimeSerializer::new);
      cache.put(Path.class, PathSerializer::new);
      cache.put(Period.class, PeriodSerializer::new);
      cache.put(Short.class, ShortSerializer::new);
      cache.put(short.class, ShortSerializer::new);
      cache.put(String.class, StringSerializer::new);
      cache.put(TimeZone.class, TimeZoneSerializer::new);
      cache.put(URI.class, UriSerializer::new);
      cache.put(URL.class, UrlSerializer::new);
      cache.put(UUID.class, UuidSerializer::new);
      if (BuiltInTypes.isClassAvailable("javax.xml.datatype.XMLGregorianCalendar")) {
         cache.put(XMLGregorianCalendar.class, XmlGregorianCalendarSerializer::new);
      }

      cache.put(YearMonth.class, YearMonthTypeSerializer::new);
      cache.put(ZonedDateTime.class, ZonedDateTimeSerializer::new);
      cache.put(ZoneId.class, ZoneIdSerializer::new);
      cache.put(ZoneOffset.class, ZoneOffsetSerializer::new);
      if (BuiltInTypes.isClassAvailable("java.sql.Date")) {
         cache.put(Date.class, SqlDateSerializer::new);
         cache.put(java.sql.Date.class, SqlDateSerializer::new);
         cache.put(Timestamp.class, SqlTimestampSerializer::new);
      }

      SERIALIZERS = Map.copyOf(cache);
      Map<Class<?>, Class<?>> optionals = new HashMap<>();
      optionals.put(OptionalDouble.class, Double.class);
      optionals.put(OptionalInt.class, Integer.class);
      optionals.put(OptionalLong.class, Long.class);
      OPTIONALS = Map.copyOf(optionals);
      Set<Class<?>> mapKeys = new HashSet<>(SERIALIZERS.keySet());
      mapKeys.addAll(optionals.keySet());
      mapKeys.add(JsonNumber.class);
      mapKeys.add(JsonString.class);
      mapKeys.remove(Object.class);
      SUPPORTED_MAP_KEYS = Set.copyOf(mapKeys);
   }
}
