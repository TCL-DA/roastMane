package com.sun.marlin.stats;

public final class Histogram extends StatLong {
   static final int BUCKET = 2;
   static final int MAX = 20;
   static final int LAST = 19;
   static final int[] STEPS = new int[20];
   private final StatLong[] stats = new StatLong[20];

   static int bucket(int var0) {
      for (int var1 = 1; var1 < 20; var1++) {
         if (var0 < STEPS[var1]) {
            return var1 - 1;
         }
      }

      return 19;
   }

   public Histogram(String var1) {
      super(var1);

      for (int var2 = 0; var2 < 20; var2++) {
         this.stats[var2] = new StatLong(String.format("%5s .. %5s", STEPS[var2], var2 + 1 < 20 ? STEPS[var2 + 1] : "~"));
      }
   }

   @Override
   public void reset() {
      super.reset();

      for (int var1 = 0; var1 < 20; var1++) {
         this.stats[var1].reset();
      }
   }

   @Override
   public void add(int var1) {
      super.add(var1);
      this.stats[bucket(var1)].add(var1);
   }

   @Override
   public void add(long var1) {
      this.add((int)var1);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(2048);
      super.toString(var1).append(" { ");

      for (int var2 = 0; var2 < 20; var2++) {
         if (this.stats[var2].count != 0L) {
            var1.append("\n        ").append(this.stats[var2].toString());
         }
      }

      return var1.append(" }").toString();
   }

   static {
      STEPS[0] = 0;
      STEPS[1] = 1;

      for (int var0 = 2; var0 < 20; var0++) {
         STEPS[var0] = STEPS[var0 - 1] * 2;
      }
   }
}
