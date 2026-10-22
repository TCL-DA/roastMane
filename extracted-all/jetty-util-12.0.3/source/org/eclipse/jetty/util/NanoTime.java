package org.eclipse.jetty.util;

import java.util.concurrent.TimeUnit;

public class NanoTime {
   public static long now() {
      return System.nanoTime();
   }

   public static long elapsed(long beginNanoTime, long endNanoTime) {
      return endNanoTime - beginNanoTime;
   }

   public static long since(long beginNanoTime) {
      return elapsed(beginNanoTime, now());
   }

   public static long until(long endNanoTime) {
      return elapsed(now(), endNanoTime);
   }

   public static long millisElapsed(long beginNanoTime, long endNanoTime) {
      return TimeUnit.NANOSECONDS.toMillis(elapsed(beginNanoTime, endNanoTime));
   }

   public static long millisSince(long beginNanoTime) {
      return millisElapsed(beginNanoTime, now());
   }

   public static long millisUntil(long endNanoTime) {
      return millisElapsed(now(), endNanoTime);
   }

   public static long secondsElapsed(long beginNanoTime, long endNanoTime) {
      return TimeUnit.NANOSECONDS.toSeconds(elapsed(beginNanoTime, endNanoTime));
   }

   public static long secondsSince(long beginNanoTime) {
      return secondsElapsed(beginNanoTime, now());
   }

   public static long secondsUntil(long endNanoTime) {
      return secondsElapsed(now(), endNanoTime);
   }

   public static boolean isBefore(long nanoTime1, long nanoTime2) {
      return nanoTime1 - nanoTime2 < 0L;
   }

   public static boolean isBeforeOrSame(long nanoTime1, long nanoTime2) {
      return nanoTime1 - nanoTime2 <= 0L;
   }

   public static void spinWait(long nanos) {
      long start = now();

      while (since(start) < nanos) {
         Thread.onSpinWait();
      }
   }

   private NanoTime() {
   }
}
