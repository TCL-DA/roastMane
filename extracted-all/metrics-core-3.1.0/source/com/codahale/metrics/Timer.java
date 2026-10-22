package com.codahale.metrics;

import java.io.Closeable;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Timer implements Metered, Sampling {
   private final Meter meter;
   private final Histogram histogram;
   private final Clock clock;

   public Timer() {
      this(new ExponentiallyDecayingReservoir());
   }

   public Timer(Reservoir reservoir) {
      this(reservoir, Clock.defaultClock());
   }

   public Timer(Reservoir reservoir, Clock clock) {
      this.meter = new Meter(clock);
      this.clock = clock;
      this.histogram = new Histogram(reservoir);
   }

   public void update(long duration, TimeUnit unit) {
      this.update(unit.toNanos(duration));
   }

   public <T> T time(Callable<T> event) throws Exception {
      long startTime = this.clock.getTick();

      Object var4;
      try {
         var4 = event.call();
      } finally {
         this.update(this.clock.getTick() - startTime);
      }

      return (T)var4;
   }

   public Timer.Context time() {
      return new Timer.Context(this, this.clock);
   }

   @Override
   public long getCount() {
      return this.histogram.getCount();
   }

   @Override
   public double getFifteenMinuteRate() {
      return this.meter.getFifteenMinuteRate();
   }

   @Override
   public double getFiveMinuteRate() {
      return this.meter.getFiveMinuteRate();
   }

   @Override
   public double getMeanRate() {
      return this.meter.getMeanRate();
   }

   @Override
   public double getOneMinuteRate() {
      return this.meter.getOneMinuteRate();
   }

   @Override
   public Snapshot getSnapshot() {
      return this.histogram.getSnapshot();
   }

   private void update(long duration) {
      if (duration >= 0L) {
         this.histogram.update(duration);
         this.meter.mark();
      }
   }

   public static class Context implements Closeable {
      private final Timer timer;
      private final Clock clock;
      private final long startTime;

      private Context(Timer timer, Clock clock) {
         this.timer = timer;
         this.clock = clock;
         this.startTime = clock.getTick();
      }

      public long stop() {
         long elapsed = this.clock.getTick() - this.startTime;
         this.timer.update(elapsed, TimeUnit.NANOSECONDS);
         return elapsed;
      }

      @Override
      public void close() {
         this.stop();
      }
   }
}
