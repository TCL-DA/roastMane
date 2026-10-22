package org.apache.commons.math3.genetics;

import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.exception.NumberIsTooSmallException;

public class FixedElapsedTime implements StoppingCondition {
   private final long maxTimePeriod;
   private long endTime = -1L;

   public FixedElapsedTime(long maxTime) throws NumberIsTooSmallException {
      this(maxTime, TimeUnit.SECONDS);
   }

   public FixedElapsedTime(long maxTime, TimeUnit unit) throws NumberIsTooSmallException {
      if (maxTime < 0L) {
         throw new NumberIsTooSmallException(maxTime, 0, true);
      } else {
         this.maxTimePeriod = unit.toNanos(maxTime);
      }
   }

   @Override
   public boolean isSatisfied(Population population) {
      if (this.endTime < 0L) {
         this.endTime = System.nanoTime() + this.maxTimePeriod;
      }

      return System.nanoTime() >= this.endTime;
   }
}
