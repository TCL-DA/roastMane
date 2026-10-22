package org.glassfish.jersey.server.internal.monitoring.core;

import java.util.concurrent.TimeUnit;

public abstract class AbstractTimeSnapshot implements UniformTimeSnapshot {
   private final long timeInterval;
   private final TimeUnit timeIntervalUnit;

   protected AbstractTimeSnapshot(long timeInterval, TimeUnit timeIntervalUnit) {
      this.timeInterval = timeInterval;
      this.timeIntervalUnit = timeIntervalUnit;
   }

   @Override
   public long getTimeInterval(TimeUnit timeUnit) {
      return timeUnit.convert(this.timeInterval, this.timeIntervalUnit);
   }

   @Override
   public double getRate(TimeUnit timeUnit) {
      double rateInNanos = (double)this.size() / (double)this.getTimeInterval(TimeUnit.NANOSECONDS);
      long multiplier = TimeUnit.NANOSECONDS.convert(1L, timeUnit);
      return rateInNanos * (double)multiplier;
   }
}
