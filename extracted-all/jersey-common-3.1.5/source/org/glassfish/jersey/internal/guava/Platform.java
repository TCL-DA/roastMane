package org.glassfish.jersey.internal.guava;

import java.lang.reflect.Array;

final class Platform {
   private Platform() {
   }

   static long systemNanoTime() {
      return System.nanoTime();
   }

   static <T> T[] newArray(T[] reference, int length) {
      Class<?> type = reference.getClass().getComponentType();
      return (T[])((Object[])Array.newInstance(type, length));
   }
}
