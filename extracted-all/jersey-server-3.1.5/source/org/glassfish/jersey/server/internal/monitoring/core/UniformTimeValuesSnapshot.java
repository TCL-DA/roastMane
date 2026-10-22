package org.glassfish.jersey.server.internal.monitoring.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class UniformTimeValuesSnapshot extends AbstractTimeSnapshot {
   private final long[] values;

   public UniformTimeValuesSnapshot(Collection<Long> values, long timeInterval, TimeUnit timeIntervalUnit) {
      super(timeInterval, timeIntervalUnit);
      Object[] copy = values.toArray();
      this.values = new long[copy.length];

      for (int i = 0; i < copy.length; i++) {
         this.values[i] = (Long)copy[i];
      }

      Arrays.sort(this.values);
   }

   public double getValue(double quantile) {
      if (quantile < 0.0 || quantile > 1.0 || Double.isNaN(quantile)) {
         throw new IllegalArgumentException(quantile + " is not in [0..1] range");
      } else if (this.values.length == 0) {
         return 0.0;
      } else {
         double pos = quantile * (double)(this.values.length + 1);
         int index = (int)pos;
         if (index < 1) {
            return (double)this.values[0];
         } else if (index >= this.values.length) {
            return (double)this.values[this.values.length - 1];
         } else {
            double lower = (double)this.values[index - 1];
            double upper = (double)this.values[index];
            return lower + (pos - Math.floor(pos)) * (upper - lower);
         }
      }
   }

   @Override
   public long size() {
      return (long)this.values.length;
   }

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
}
