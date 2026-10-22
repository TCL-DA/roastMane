package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class UniformRealDistribution extends AbstractRealDistribution {
   @Deprecated
   public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
   private static final long serialVersionUID = 20120109L;
   private final double lower;
   private final double upper;

   public UniformRealDistribution() {
      this(0.0, 1.0);
   }

   public UniformRealDistribution(double lower, double upper) throws NumberIsTooLargeException {
      this(new Well19937c(), lower, upper);
   }

   @Deprecated
   public UniformRealDistribution(double lower, double upper, double inverseCumAccuracy) throws NumberIsTooLargeException {
      this(new Well19937c(), lower, upper);
   }

   @Deprecated
   public UniformRealDistribution(RandomGenerator rng, double lower, double upper, double inverseCumAccuracy) {
      this(rng, lower, upper);
   }

   public UniformRealDistribution(RandomGenerator rng, double lower, double upper) throws NumberIsTooLargeException {
      super(rng);
      if (lower >= upper) {
         throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, lower, upper, false);
      } else {
         this.lower = lower;
         this.upper = upper;
      }
   }

   @Override
   public double density(double x) {
      return !(x < this.lower) && !(x > this.upper) ? 1.0 / (this.upper - this.lower) : 0.0;
   }

   @Override
   public double cumulativeProbability(double x) {
      if (x <= this.lower) {
         return 0.0;
      } else {
         return x >= this.upper ? 1.0 : (x - this.lower) / (this.upper - this.lower);
      }
   }

   @Override
   public double inverseCumulativeProbability(double p) throws OutOfRangeException {
      if (!(p < 0.0) && !(p > 1.0)) {
         return p * (this.upper - this.lower) + this.lower;
      } else {
         throw new OutOfRangeException(p, 0, 1);
      }
   }

   @Override
   public double getNumericalMean() {
      return 0.5 * (this.lower + this.upper);
   }

   @Override
   public double getNumericalVariance() {
      double ul = this.upper - this.lower;
      return ul * ul / 12.0;
   }

   @Override
   public double getSupportLowerBound() {
      return this.lower;
   }

   @Override
   public double getSupportUpperBound() {
      return this.upper;
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
      double u = this.random.nextDouble();
      return u * this.upper + (1.0 - u) * this.lower;
   }
}
