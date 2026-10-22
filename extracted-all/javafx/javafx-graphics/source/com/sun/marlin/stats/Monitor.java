package com.sun.marlin.stats;

public final class Monitor extends StatLong {
   private static final long INVALID = -1L;
   private long start = -1L;

   public Monitor(String var1) {
      super(var1);
   }

   public void start() {
      this.start = System.nanoTime();
   }

   public void stop() {
      long var1 = System.nanoTime() - this.start;
      if (this.start != -1L && var1 > 0L) {
         this.add(var1);
      }

      this.start = -1L;
   }
}
