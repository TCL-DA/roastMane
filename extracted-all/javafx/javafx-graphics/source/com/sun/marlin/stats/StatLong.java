package com.sun.marlin.stats;

public class StatLong {
   public final String name;
   public long count = 0L;
   public long sum = 0L;
   public long min = 2147483647L;
   public long max = -2147483648L;

   public StatLong(String var1) {
      this.name = var1;
   }

   public void reset() {
      this.count = 0L;
      this.sum = 0L;
      this.min = 2147483647L;
      this.max = -2147483648L;
   }

   public void add(int var1) {
      this.count++;
      this.sum += (long)var1;
      if ((long)var1 < this.min) {
         this.min = (long)var1;
      }

      if ((long)var1 > this.max) {
         this.max = (long)var1;
      }
   }

   public void add(long var1) {
      this.count++;
      this.sum += var1;
      if (var1 < this.min) {
         this.min = var1;
      }

      if (var1 > this.max) {
         this.max = var1;
      }
   }

   @Override
   public String toString() {
      return this.toString(new StringBuilder(128)).toString();
   }

   public final StringBuilder toString(StringBuilder var1) {
      var1.append(this.name).append('[').append(this.count);
      var1.append("] sum: ").append(this.sum).append(" avg: ");
      var1.append(trimTo3Digits((double)this.sum / (double)this.count));
      var1.append(" [").append(this.min).append(" | ").append(this.max).append("]");
      return var1;
   }

   public static double trimTo3Digits(double var0) {
      return (double)((long)(1000.0 * var0)) / 1000.0;
   }
}
