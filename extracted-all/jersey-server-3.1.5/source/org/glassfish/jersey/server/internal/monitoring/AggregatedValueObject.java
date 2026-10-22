package org.glassfish.jersey.server.internal.monitoring;

import java.util.Collection;
import java.util.LinkedList;

class AggregatedValueObject {
   private final long max;
   private final long min;
   private final double mean;
   private final long count;

   private AggregatedValueObject(long max, long min, double mean, long count) {
      this.max = max;
      this.min = min;
      this.mean = mean;
      this.count = count;
   }

   public static AggregatedValueObject createFromValues(Collection<Long> values) {
      if (values.isEmpty()) {
         throw new IllegalArgumentException("The values collection must not be empty");
      } else {
         long max = Long.MIN_VALUE;
         long min = Long.MAX_VALUE;
         long sum = 0L;

         for (Long value : values) {
            max = Math.max(max, value);
            min = Math.min(min, value);
            sum += value;
         }

         return new AggregatedValueObject(max, min, (double)sum / (double)values.size(), (long)values.size());
      }
   }

   public static AggregatedValueObject createFromMultiValues(Collection<? extends Collection<Long>> values) {
      Collection<Long> mergedCollection = new LinkedList<>();

      for (Collection<Long> collection : values) {
         mergedCollection.addAll(collection);
      }

      return createFromValues(mergedCollection);
   }

   public long getMax() {
      return this.max;
   }

   public long getMin() {
      return this.min;
   }

   public double getMean() {
      return this.mean;
   }

   public long getCount() {
      return this.count;
   }
}
