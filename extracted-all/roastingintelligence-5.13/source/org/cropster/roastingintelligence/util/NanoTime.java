package org.cropster.roastingintelligence.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NanoTime {
   private static final Logger LOGGER = LoggerFactory.getLogger(NanoTime.class);
   private static volatile long _nanos;
   private static volatile long _millis;

   public static synchronized void sync() {
      _nanos = System.nanoTime();
      _millis = System.currentTimeMillis();
   }

   public static synchronized long currentTimeMillis() {
      long elapsedMillis = Math.round((double)(System.nanoTime() - _nanos) / 1000000.0);
      long currentTimeMillis = _millis + elapsedMillis;
      if (LOGGER.isTraceEnabled()) {
         long diff = System.currentTimeMillis() - currentTimeMillis;
         if (Math.abs(diff) > 1L) {
            LOGGER.trace("nanoTime/currentTimeMillis = {}", diff);
         }
      }

      return currentTimeMillis;
   }

   private NanoTime() {
   }

   static {
      sync();
   }
}
