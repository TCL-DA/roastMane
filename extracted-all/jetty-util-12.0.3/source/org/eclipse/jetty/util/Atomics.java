package org.eclipse.jetty.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Atomics {
   private Atomics() {
   }

   public static boolean updateMin(AtomicLong currentMin, long newValue) {
      for (long oldValue = currentMin.get(); newValue < oldValue; oldValue = currentMin.get()) {
         if (currentMin.compareAndSet(oldValue, newValue)) {
            return true;
         }
      }

      return false;
   }

   public static boolean updateMin(AtomicInteger currentMin, int newValue) {
      for (int oldValue = currentMin.get(); newValue < oldValue; oldValue = currentMin.get()) {
         if (currentMin.compareAndSet(oldValue, newValue)) {
            return true;
         }
      }

      return false;
   }

   public static boolean updateMax(AtomicLong currentMax, long newValue) {
      for (long oldValue = currentMax.get(); newValue > oldValue; oldValue = currentMax.get()) {
         if (currentMax.compareAndSet(oldValue, newValue)) {
            return true;
         }
      }

      return false;
   }

   public static boolean updateMax(AtomicInteger currentMax, int newValue) {
      for (int oldValue = currentMax.get(); newValue > oldValue; oldValue = currentMax.get()) {
         if (currentMax.compareAndSet(oldValue, newValue)) {
            return true;
         }
      }

      return false;
   }
}
