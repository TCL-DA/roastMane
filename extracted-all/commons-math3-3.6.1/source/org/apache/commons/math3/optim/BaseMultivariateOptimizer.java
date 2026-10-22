package org.apache.commons.math3.optim;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;

public abstract class BaseMultivariateOptimizer<PAIR> extends BaseOptimizer<PAIR> {
   private double[] start;
   private double[] lowerBound;
   private double[] upperBound;

   protected BaseMultivariateOptimizer(ConvergenceChecker<PAIR> checker) {
      super(checker);
   }

   @Override
   public PAIR optimize(OptimizationData... optData) {
      return super.optimize(optData);
   }

   @Override
   protected void parseOptimizationData(OptimizationData... optData) {
      super.parseOptimizationData(optData);

      for (OptimizationData data : optData) {
         if (data instanceof InitialGuess) {
            this.start = ((InitialGuess)data).getInitialGuess();
         } else if (data instanceof SimpleBounds) {
            SimpleBounds bounds = (SimpleBounds)data;
            this.lowerBound = bounds.getLower();
            this.upperBound = bounds.getUpper();
         }
      }

      this.checkParameters();
   }

   public double[] getStartPoint() {
      return this.start == null ? null : (double[])this.start.clone();
   }

   public double[] getLowerBound() {
      return this.lowerBound == null ? null : (double[])this.lowerBound.clone();
   }

   public double[] getUpperBound() {
      return this.upperBound == null ? null : (double[])this.upperBound.clone();
   }

   private void checkParameters() {
      if (this.start != null) {
         int dim = this.start.length;
         if (this.lowerBound != null) {
            if (this.lowerBound.length != dim) {
               throw new DimensionMismatchException(this.lowerBound.length, dim);
            }

            for (int i = 0; i < dim; i++) {
               double v = this.start[i];
               double lo = this.lowerBound[i];
               if (v < lo) {
                  throw new NumberIsTooSmallException(v, lo, true);
               }
            }
         }

         if (this.upperBound != null) {
            if (this.upperBound.length != dim) {
               throw new DimensionMismatchException(this.upperBound.length, dim);
            }

            for (int ix = 0; ix < dim; ix++) {
               double v = this.start[ix];
               double hi = this.upperBound[ix];
               if (v > hi) {
                  throw new NumberIsTooLargeException(v, hi, true);
               }
            }
         }
      }
   }
}
