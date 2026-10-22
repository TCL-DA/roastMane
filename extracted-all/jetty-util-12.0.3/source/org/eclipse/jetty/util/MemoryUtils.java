package org.eclipse.jetty.util;

public class MemoryUtils {
   private static final int cacheLineBytes;

   private MemoryUtils() {
   }

   public static int getCacheLineBytes() {
      return cacheLineBytes;
   }

   public static int getIntegersPerCacheLine() {
      return getCacheLineBytes() >> 2;
   }

   public static int getLongsPerCacheLine() {
      return getCacheLineBytes() >> 3;
   }

   static {
      int defaultValue = 64;
      int value = defaultValue;

      try {
         value = Integer.parseInt(System.getProperty("org.eclipse.jetty.util.cacheLineBytes", String.valueOf(defaultValue)));
      } catch (Exception var3) {
      }

      cacheLineBytes = value;
   }
}
