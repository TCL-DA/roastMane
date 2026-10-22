package org.apache.logging.log4j.util;

@InternalApi
public final class Cast {
   public static <T> T cast(final Object o) {
      return (T)(o == null ? null : o);
   }

   private Cast() {
   }
}
