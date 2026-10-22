package com.codahale.metrics;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;

public class UniformSnapshot extends Snapshot {
   private static final Charset UTF_8 = Charset.forName("UTF-8");
   private final long[] values;

   public UniformSnapshot(Collection<Long> values) {
      Object[] copy = values.toArray();
      this.values = new long[copy.length];

      for (int i = 0; i < copy.length; i++) {
         this.values[i] = (Long)copy[i];
      }

      Arrays.sort(this.values);
   }

   public UniformSnapshot(long[] values) {
      this.values = Arrays.copyOf(values, values.length);
      Arrays.sort(this.values);
   }

   @Override
   public double getValue(double quantile) {
      if (quantile < 0.0 || quantile > 1.0) {
         throw new IllegalArgumentException(quantile + " is not in [0..1]");
      } else if (this.values.length == 0) {
         return 0.0;
      } else {
         double pos = quantile * (double)(this.values.length + 1);
         if (pos < 1.0) {
            return (double)this.values[0];
         } else if (pos >= (double)this.values.length) {
            return (double)this.values[this.values.length - 1];
         } else {
            double lower = (double)this.values[(int)pos - 1];
            double upper = (double)this.values[(int)pos];
            return lower + (pos - Math.floor(pos)) * (upper - lower);
         }
      }
   }

   @Override
   public int size() {
      return this.values.length;
   }

   @Override
   public long[] getValues() {
      return Arrays.copyOf(this.values, this.values.length);
   }

   @Override
   public long getMax() {
      return this.values.length == 0 ? 0L : this.values[this.values.length - 1];
   }

   @Override
   public long getMin() {
      return this.values.length == 0 ? 0L : this.values[0];
   }

   @Override
   public double getMean() {
      if (this.values.length == 0) {
         return 0.0;
      } else {
         double sum = 0.0;

         for (long value : this.values) {
            sum += (double)value;
         }

         return sum / (double)this.values.length;
      }
   }

   @Override
   public double getStdDev() {
      if (this.values.length <= 1) {
         return 0.0;
      } else {
         double mean = this.getMean();
         double sum = 0.0;

         for (long value : this.values) {
            double diff = (double)value - mean;
            sum += diff * diff;
         }

         double variance = sum / (double)(this.values.length - 1);
         return Math.sqrt(variance);
      }
   }

   @Override
   public void dump(OutputStream output) {
      PrintWriter out = new PrintWriter(new OutputStreamWriter(output, UTF_8));

      try {
         for (long value : this.values) {
            out.printf("%d%n", value);
         }
      } finally {
         out.close();
      }
   }
}
