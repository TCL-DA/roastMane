package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.FastMath;

public class GeometricDistribution extends AbstractIntegerDistribution {
   private static final long serialVersionUID = 20130507L;
   private final double probabilityOfSuccess;
   private final double logProbabilityOfSuccess;
   private final double log1mProbabilityOfSuccess;

   public GeometricDistribution(double p) {
      this(new Well19937c(), p);
   }

   public GeometricDistribution(RandomGenerator rng, double p) {
      super(rng);
      if (!(p <= 0.0) && !(p > 1.0)) {
         this.probabilityOfSuccess = p;
         this.logProbabilityOfSuccess = FastMath.log(p);
         this.log1mProbabilityOfSuccess = FastMath.log1p(-p);
      } else {
         throw new OutOfRangeException(LocalizedFormats.OUT_OF_RANGE_LEFT, p, 0, 1);
      }
   }

   public double getProbabilityOfSuccess() {
      return this.probabilityOfSuccess;
   }

   @Override
   public double probability(int x) {
      return x < 0 ? 0.0 : FastMath.exp(this.log1mProbabilityOfSuccess * (double)x) * this.probabilityOfSuccess;
   }

   @Override
   public double logProbability(int x) {
      return x < 0 ? Double.NEGATIVE_INFINITY : (double)x * this.log1mProbabilityOfSuccess + this.logProbabilityOfSuccess;
   }

   @Override
   public double cumulativeProbability(int x) {
      return x < 0 ? 0.0 : -FastMath.expm1(this.log1mProbabilityOfSuccess * (double)(x + 1));
   }

   @Override
   public double getNumericalMean() {
      return (1.0 - this.probabilityOfSuccess) / this.probabilityOfSuccess;
   }

   @Override
   public double getNumericalVariance() {
      return (1.0 - this.probabilityOfSuccess) / (this.probabilityOfSuccess * this.probabilityOfSuccess);
   }

   @Override
   public int getSupportLowerBound() {
      return 0;
   }

   @Override
   public int getSupportUpperBound() {
      return Integer.MAX_VALUE;
   }

   @Override
   public boolean isSupportConnected() {
      return true;
   }

   @Override
   public int inverseCumulativeProbability(double p) throws OutOfRangeException {
      if (p < 0.0 || p > 1.0) {
         throw new OutOfRangeException(p, 0, 1);
      } else if (p == 1.0) {
         return Integer.MAX_VALUE;
      } else {
         return p == 0.0 ? 0 : Math.max(0, (int)Math.ceil(FastMath.log1p(-p) / this.log1mProbabilityOfSuccess - 1.0));
      }
   }
}
