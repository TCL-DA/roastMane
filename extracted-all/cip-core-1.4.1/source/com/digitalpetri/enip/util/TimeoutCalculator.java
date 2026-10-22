package com.digitalpetri.enip.util;

import java.time.Duration;

public class TimeoutCalculator {
   private static final int MIN_TIMEOUT = 1;
   private static final int MAX_TIMEOUT = 8355840;

   public static int calculateTimeoutBytes(Duration timeout) {
      int desiredTimeout = (int)timeout.toMillis();
      if (desiredTimeout < 1) {
         desiredTimeout = 1;
      }

      if (desiredTimeout > 8355840) {
         desiredTimeout = 8355840;
      }

      boolean precisionLost = false;
      int shifts = 0;

      int multiplier;
      for (multiplier = desiredTimeout; multiplier > 255; shifts++) {
         precisionLost |= (multiplier & 1) == 1;
         multiplier >>= 1;
      }

      if (precisionLost) {
         if (++multiplier > 255) {
            multiplier >>= 1;
            shifts++;
         }
      }

      assert shifts <= 15;

      int tick = (int)Math.pow(2.0, (double)shifts);

      assert tick >= 1 && tick <= 32768;

      assert multiplier >= 1 && multiplier <= 255;

      return shifts << 8 | multiplier;
   }
}
