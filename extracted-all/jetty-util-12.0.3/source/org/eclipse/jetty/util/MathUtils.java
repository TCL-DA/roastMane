package org.eclipse.jetty.util;

public class MathUtils {
   private MathUtils() {
   }

   public static boolean sumOverflows(int a, int b) {
      try {
         Math.addExact(a, b);
         return false;
      } catch (ArithmeticException var3) {
         return true;
      }
   }

   public static long cappedAdd(long a, long b) {
      try {
         return Math.addExact(a, b);
      } catch (ArithmeticException var5) {
         return Long.MAX_VALUE;
      }
   }

   public static int cappedAdd(int a, int b, int maxValue) {
      try {
         int sum = Math.addExact(a, b);
         return Math.min(sum, maxValue);
      } catch (ArithmeticException var4) {
         return maxValue;
      }
   }
}
