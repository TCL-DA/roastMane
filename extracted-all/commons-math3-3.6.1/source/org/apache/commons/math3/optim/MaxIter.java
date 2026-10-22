package org.apache.commons.math3.optim;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public class MaxIter implements OptimizationData {
   private final int maxIter;

   public MaxIter(int max) {
      if (max <= 0) {
         throw new NotStrictlyPositiveException(max);
      } else {
         this.maxIter = max;
      }
   }

   public int getMaxIter() {
      return this.maxIter;
   }

   public static MaxIter unlimited() {
      return new MaxIter(Integer.MAX_VALUE);
   }
}
