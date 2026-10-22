package org.glassfish.jersey.internal.guava;

import java.util.Arrays;

final class Ints {
   public static final int MAX_POWER_OF_TWO = 1073741824;
   private static final byte[] asciiDigits = new byte[128];

   private Ints() {
   }

   public static int saturatedCast(long value) {
      if (value > 2147483647L) {
         return Integer.MAX_VALUE;
      } else {
         return value < -2147483648L ? Integer.MIN_VALUE : (int)value;
      }
   }

   static {
      Arrays.fill(asciiDigits, (byte)-1);

      for (int i = 0; i <= 9; i++) {
         asciiDigits[48 + i] = (byte)i;
      }

      for (int i = 0; i <= 26; i++) {
         asciiDigits[65 + i] = (byte)(10 + i);
         asciiDigits[97 + i] = (byte)(10 + i);
      }
   }
}
