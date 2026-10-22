package com.segment.backo;

import java.util.concurrent.TimeUnit;

public class Backo {
   private static final long DEFAULT_BASE = 100L;
   private static final int DEFAULT_FACTOR = 2;
   private static final double DEFAULT_JITTER = 0.0;
   private static final long DEFAULT_CAP = Long.MAX_VALUE;
   private final long base;
   private final int factor;
   private final double jitter;
   private final long cap;

   private Backo(long base, int factor, double jitter, long cap) {
      this.base = base;
      this.factor = factor;
      this.jitter = jitter;
      this.cap = cap;
   }

   public static Backo.Builder builder() {
      return new Backo.Builder();
   }

   public void sleep(int attempt) throws InterruptedException {
      Thread.sleep(this.backoff(attempt));
   }

   public long backoff(int attempt) {
      long duration = this.base * (long)Math.pow((double)this.factor, (double)attempt);
      if (this.jitter != 0.0) {
         double random = Math.random();
         int deviation = (int)Math.floor(random * this.jitter * (double)duration);
         if (((int)Math.floor(random * 10.0) & 1) == 0) {
            duration -= (long)deviation;
         } else {
            duration += (long)deviation;
         }
      }

      if (duration < 0L) {
         duration = Long.MAX_VALUE;
      }

      return Math.min(Math.max(duration, this.base), this.cap);
   }

   public static class Builder {
      private long base = 100L;
      private int factor = 2;
      private double jitter = 0.0;
      private long cap = Long.MAX_VALUE;

      Builder() {
      }

      public Backo.Builder base(TimeUnit timeUnit, long duration) {
         this.base = timeUnit.toMillis(duration);
         return this;
      }

      public Backo.Builder factor(int factor) {
         this.factor = factor;
         return this;
      }

      public Backo.Builder jitter(int jitter) {
         this.jitter = (double)jitter;
         return this;
      }

      public Backo.Builder cap(TimeUnit timeUnit, long duration) {
         this.cap = timeUnit.toMillis(duration);
         return this;
      }

      public Backo build() {
         if (this.cap < this.base) {
            throw new IllegalStateException("Initial backoff cannot be more than maximum.");
         } else {
            return new Backo(this.base, this.factor, this.jitter, this.cap);
         }
      }
   }
}
