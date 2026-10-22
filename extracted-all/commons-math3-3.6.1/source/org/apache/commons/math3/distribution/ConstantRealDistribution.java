package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.OutOfRangeException;

public class ConstantRealDistribution extends AbstractRealDistribution {
   private static final long serialVersionUID = -4157745166772046273L;
   private final double value;

   public ConstantRealDistribution(double value) {
      super(null);
      this.value = value;
   }

   @Override
   public double density(double x) {
      return x == this.value ? 1.0 : 0.0;
   }

   @Override
   public double cumulativeProbability(double x) {
      return x < this.value ? 0.0 : 1.0;
   }

   @Override
   public double inverseCumulativeProbability(double p) throws OutOfRangeException {
      if (!(p < 0.0) && !(p > 1.0)) {
         return this.value;
      } else {
         throw new OutOfRangeException(p, 0, 1);
      }
   }

   @Override
   public double getNumericalMean() {
      return this.value;
   }

   @Override
   public double getNumericalVariance() {
      return 0.0;
   }

   @Override
   public double getSupportLowerBound() {
      return this.value;
   }

   @Override
   public double getSupportUpperBound() {
      return this.value;
   }

   @Override
   public boolean isSupportLowerBoundInclusive() {
      return true;
   }

   @Override
   public boolean isSupportUpperBoundInclusive() {
      return true;
   }

   @Override
   public boolean isSupportConnected() {
      return true;
   }

   @Override
   public double sample() {
      return this.value;
   }

   @Override
   public void reseedRandomGenerator(long seed) {
   }
}
