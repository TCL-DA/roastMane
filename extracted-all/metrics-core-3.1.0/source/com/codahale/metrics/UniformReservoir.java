package com.codahale.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

public class UniformReservoir implements Reservoir {
   private static final int DEFAULT_SIZE = 1028;
   private static final int BITS_PER_LONG = 63;
   private final AtomicLong count = new AtomicLong();
   private final AtomicLongArray values;

   public UniformReservoir() {
      this(1028);
   }

   public UniformReservoir(int size) {
      this.values = new AtomicLongArray(size);

      for (int i = 0; i < this.values.length(); i++) {
         this.values.set(i, 0L);
      }

      this.count.set(0L);
   }

   @Override
   public int size() {
      long c = this.count.get();
      return c > (long)this.values.length() ? this.values.length() : (int)c;
   }

   @Override
   public void update(long value) {
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
   public Snapshot getSnapshot() {
      int s = this.size();
      List<Long> copy = new ArrayList<>(s);

      for (int i = 0; i < s; i++) {
         copy.add(this.values.get(i));
      }

      return new UniformSnapshot(copy);
   }
}
