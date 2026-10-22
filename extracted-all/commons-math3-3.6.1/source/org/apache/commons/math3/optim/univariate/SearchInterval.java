package org.apache.commons.math3.optim.univariate;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.optim.OptimizationData;

public class SearchInterval implements OptimizationData {
   private final double lower;
   private final double upper;
   private final double start;

   public SearchInterval(double lo, double hi, double init) {
      if (lo >= hi) {
         throw new NumberIsTooLargeException(lo, hi, false);
      } else if (!(init < lo) && !(init > hi)) {
         this.lower = lo;
         this.upper = hi;
         this.start = init;
      } else {
         throw new OutOfRangeException(init, lo, hi);
      }
   }

   public SearchInterval(double lo, double hi) {
      this(lo, hi, 0.5 * (lo + hi));
   }

   public double getMin() {
      return this.lower;
   }

   public double getMax() {
      return this.upper;
   }

   public double getStartValue() {
      return this.start;
   }
}
