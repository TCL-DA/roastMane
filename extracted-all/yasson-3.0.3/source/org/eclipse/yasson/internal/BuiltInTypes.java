package org.eclipse.yasson.internal;

import jakarta.json.JsonValue;
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
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import javax.xml.datatype.XMLGregorianCalendar;

public class BuiltInTypes {
   private static final Set<Class<?>> BUILD_IN_SUPPORT;

   private BuiltInTypes() {
      throw new IllegalStateException("Util class cannot be instantiated");
   }

   public static boolean isClassAvailable(String className) {
      try {
         Class.forName(className);
         return true;
      } catch (LinkageError | ClassNotFoundException var2) {
         return false;
      }
   }

   public static boolean isKnownType(Class<?> clazz) {
      boolean knownContainerValueType = Collection.class.isAssignableFrom(clazz)
         || Map.class.isAssignableFrom(clazz)
         || JsonValue.class.isAssignableFrom(clazz)
         || Optional.class.isAssignableFrom(clazz)
         || clazz.isArray();
      return knownContainerValueType || findIfClassIsSupported(clazz);
   }

   private static boolean findIfClassIsSupported(Class<?> clazz) {
      Class<?> current = clazz;

      while (!BUILD_IN_SUPPORT.contains(current)) {
         current = current.getSuperclass();
         if (current == null) {
            return false;
         }
      }

      return true;
   }

   static {
      Set<Class<?>> buildInTypes = new HashSet<>();
      buildInTypes.add(Byte.class);
      buildInTypes.add(byte.class);
      buildInTypes.add(BigDecimal.class);
      buildInTypes.add(BigInteger.class);
      buildInTypes.add(Boolean.class);
      buildInTypes.add(boolean.class);
      buildInTypes.add(Calendar.class);
      buildInTypes.add(Character.class);
      buildInTypes.add(char.class);
      buildInTypes.add(Date.class);
      buildInTypes.add(Double.class);
      buildInTypes.add(double.class);
      buildInTypes.add(Duration.class);
      buildInTypes.add(Float.class);
      buildInTypes.add(float.class);
      buildInTypes.add(Integer.class);
      buildInTypes.add(int.class);
      buildInTypes.add(Instant.class);
      buildInTypes.add(LocalDateTime.class);
      buildInTypes.add(LocalDate.class);
      buildInTypes.add(LocalTime.class);
      buildInTypes.add(Long.class);
      buildInTypes.add(long.class);
      buildInTypes.add(Number.class);
      buildInTypes.add(OffsetDateTime.class);
      buildInTypes.add(OffsetTime.class);
      buildInTypes.add(OptionalDouble.class);
      buildInTypes.add(OptionalInt.class);
      buildInTypes.add(OptionalLong.class);
      buildInTypes.add(Path.class);
      buildInTypes.add(Period.class);
      buildInTypes.add(Short.class);
      buildInTypes.add(short.class);
      buildInTypes.add(String.class);
      buildInTypes.add(TimeZone.class);
      buildInTypes.add(URI.class);
      buildInTypes.add(URL.class);
      buildInTypes.add(UUID.class);
      if (isClassAvailable("javax.xml.datatype.XMLGregorianCalendar")) {
         buildInTypes.add(XMLGregorianCalendar.class);
      }

      buildInTypes.add(ZonedDateTime.class);
      buildInTypes.add(ZoneId.class);
      buildInTypes.add(ZoneOffset.class);
      if (isClassAvailable("java.sql.Date")) {
         buildInTypes.add(java.sql.Date.class);
         buildInTypes.add(Timestamp.class);
      }

      BUILD_IN_SUPPORT = Set.copyOf(buildInTypes);
   }
}
