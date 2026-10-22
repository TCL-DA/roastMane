package org.glassfish.jersey.server.internal.monitoring.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

public class UniformTimeReservoir implements TimeReservoir<Long> {
   private final long startTime;
   private final TimeUnit startTimeUnit;
   private static final int DEFAULT_SIZE = 1024;
   private static final int BITS_PER_LONG = 63;
   private final AtomicLong count = new AtomicLong();
   private final AtomicLongArray values;

   public UniformTimeReservoir(long startTime, TimeUnit startTimeUnit) {
      this(1024, startTime, startTimeUnit);
   }

   public UniformTimeReservoir(int size, long startTime, TimeUnit startTimeUnit) {
      this.startTime = startTime;
      this.startTimeUnit = startTimeUnit;
      this.values = new AtomicLongArray(size);

      for (int i = 0; i < this.values.length(); i++) {
         this.values.set(i, 0L);
      }

      this.count.set(0L);
   }

   @Override
   public int size(long time, TimeUnit timeUnit) {
      long c = this.count.get();
      return c > (long)this.values.length() ? this.values.length() : (int)c;
   }

   public void update(Long value, long time, TimeUnit timeUnit) {
      long c = this.count.incrementAndGet();
      if (c <= (long)this.values.length()) {
         this.values.set((int)c - 1, value);
      } else {
         long r = nextLong(c);
         if (r < (long)this.values.length()) {
            this.values.set((int)r, value);
         }
      }
   }

   private static long nextLong(long n) {
      long bits;
      long val;
      do {
         bits = ThreadLocalRandom.current().nextLong() & Long.MAX_VALUE;
         val = bits % n;
      } while (bits - val + (n - 1L) < 0L);

      return val;
   }

   @Override
   public UniformTimeSnapshot getSnapshot(long time, TimeUnit timeUnit) {
      int s = this.size(time, timeUnit);
      List<Long> copy = new ArrayList<>(s);

      for (int i = 0; i < s; i++) {
         copy.add(this.values.get(i));
      }

      return new UniformTimeValuesSnapshot(copy, this.startTimeUnit.convert(time, timeUnit) - this.startTime, this.startTimeUnit) {
         @Override
         public long size() {
            return UniformTimeReservoir.this.count.get();
         }
      };
   }

   @Override
   public long interval(TimeUnit timeUnit) {
      return 0L;
   }
}
