package org.apache.commons.math3.optimization;

import org.apache.commons.math3.util.Precision;

@Deprecated
public abstract class AbstractConvergenceChecker<PAIR> implements ConvergenceChecker<PAIR> {
   @Deprecated
   private static final double DEFAULT_RELATIVE_THRESHOLD = 100.0 * Precision.EPSILON;
   @Deprecated
   private static final double DEFAULT_ABSOLUTE_THRESHOLD = 100.0 * Precision.SAFE_MIN;
   private final double relativeThreshold;
   private final double absoluteThreshold;

   @Deprecated
   public AbstractConvergenceChecker() {
      this.relativeThreshold = DEFAULT_RELATIVE_THRESHOLD;
      this.absoluteThreshold = DEFAULT_ABSOLUTE_THRESHOLD;
   }

   public AbstractConvergenceChecker(double relativeThreshold, double absoluteThreshold) {
      this.relativeThreshold = relativeThreshold;
      this.absoluteThreshold = absoluteThreshold;
   }

   public double getRelativeThreshold() {
      return this.relativeThreshold;
   }

   public double getAbsoluteThreshold() {
      return this.absoluteThreshold;
   }

   @Override
   public abstract boolean converged(int var1, PAIR var2, PAIR var3);
}
