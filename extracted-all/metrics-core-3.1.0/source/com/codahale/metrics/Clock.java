package com.codahale.metrics;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public abstract class Clock {
   private static final Clock DEFAULT = new Clock.UserTimeClock();

   public abstract long getTick();

   public long getTime() {
      return System.currentTimeMillis();
   }

   public static Clock defaultClock() {
      return DEFAULT;
   }

   public static class CpuTimeClock extends Clock {
      private static final ThreadMXBean THREAD_MX_BEAN = ManagementFactory.getThreadMXBean();

      @Override
      public long getTick() {
         return THREAD_MX_BEAN.getCurrentThreadCpuTime();
      }
   }

   public static class UserTimeClock extends Clock {
      @Override
      public long getTick() {
         return System.nanoTime();
      }
   }
}
