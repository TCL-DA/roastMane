package com.codahale.metrics;

public class Counter implements Metric, Counting {
   private final LongAdder count = new LongAdder();

   public void inc() {
      this.inc(1L);
   }

   public void inc(long n) {
      this.count.add(n);
   }

   public void dec() {
      this.dec(1L);
   }

   public void dec(long n) {
      this.count.add(-n);
   }

   @Override
   public long getCount() {
      return this.count.sum();
   }
}
