package org.apache.logging.log4j.util;

@InternalApi
public final class EnglishEnums {
   private EnglishEnums() {
   }

   public static <T extends Enum<T>> T valueOf(final Class<T> enumType, final String name) {
      return valueOf(enumType, name, null);
   }

   public static <T extends Enum<T>> T valueOf(final Class<T> enumType, final String name, final T defaultValue) {
      return name == null ? defaultValue : Enum.valueOf(enumType, Strings.toRootUpperCase(name));
   }
}
