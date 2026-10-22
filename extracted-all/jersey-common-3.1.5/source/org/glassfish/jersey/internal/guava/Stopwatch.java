package org.glassfish.jersey.internal.guava;

import java.util.concurrent.TimeUnit;

public final class Stopwatch {
   private final Ticker ticker;
   private boolean isRunning;
   private long startTick;

   @Deprecated
   private Stopwatch() {
      this(Ticker.systemTicker());
   }

   @Deprecated
   private Stopwatch(Ticker ticker) {
      this.ticker = Preconditions.checkNotNull(ticker, "ticker");
   }

   public static Stopwatch createUnstarted() {
      return new Stopwatch();
   }

   private static TimeUnit chooseUnit(long nanos) {
      if (TimeUnit.DAYS.convert(nanos, TimeUnit.NANOSECONDS) > 0L) {
         return TimeUnit.DAYS;
      } else if (TimeUnit.HOURS.convert(nanos, TimeUnit.NANOSECONDS) > 0L) {
         return TimeUnit.HOURS;
      } else if (TimeUnit.MINUTES.convert(nanos, TimeUnit.NANOSECONDS) > 0L) {
         return TimeUnit.MINUTES;
      } else if (TimeUnit.SECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0L) {
         return TimeUnit.SECONDS;
      } else if (TimeUnit.MILLISECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0L) {
         return TimeUnit.MILLISECONDS;
      } else {
         return TimeUnit.MICROSECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0L ? TimeUnit.MICROSECONDS : TimeUnit.NANOSECONDS;
      }
   }

   private static String abbreviate(TimeUnit unit) {
      switch (unit) {
         case NANOSECONDS:
            return "ns";
         case MICROSECONDS:
            return "μs";
         case MILLISECONDS:
            return "ms";
         case SECONDS:
            return "s";
         case MINUTES:
            return "min";
         case HOURS:
            return "h";
         case DAYS:
            return "d";
         default:
            throw new AssertionError();
      }
   }

   public Stopwatch start() {
      Preconditions.checkState(!this.isRunning, "This stopwatch is already running.");
      this.isRunning = true;
      this.startTick = this.ticker.read();
      return this;
   }

   private long elapsedNanos() {
      return this.isRunning ? this.ticker.read() - this.startTick : 0L;
   }

   @Override
   public String toString() {
      long nanos = this.elapsedNanos();
      TimeUnit unit = chooseUnit(nanos);
      double value = (double)nanos / (double)TimeUnit.NANOSECONDS.convert(1L, unit);
      return String.format("%.4g %s", value, abbreviate(unit));
   }
}
