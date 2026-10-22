package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.util.FastMath;

public class NormalDistribution extends AbstractRealDistribution {
   public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
   private static final long serialVersionUID = 8589540077390120676L;
   private static final double SQRT2 = FastMath.sqrt(2.0);
   private final double mean;
   private final double standardDeviation;
   private final double logStandardDeviationPlusHalfLog2Pi;
   private final double solverAbsoluteAccuracy;

   public NormalDistribution() {
      this(0.0, 1.0);
   }

   public NormalDistribution(double mean, double sd) throws NotStrictlyPositiveException {
      this(mean, sd, 1.0E-9);
   }

   public NormalDistribution(double mean, double sd, double inverseCumAccuracy) throws NotStrictlyPositiveException {
      this(new Well19937c(), mean, sd, inverseCumAccuracy);
   }

   public NormalDistribution(RandomGenerator rng, double mean, double sd) throws NotStrictlyPositiveException {
      this(rng, mean, sd, 1.0E-9);
   }

   public NormalDistribution(RandomGenerator rng, double mean, double sd, double inverseCumAccuracy) throws NotStrictlyPositiveException {
      super(rng);
      if (sd <= 0.0) {
         throw new NotStrictlyPositiveException(LocalizedFormats.STANDARD_DEVIATION, sd);
      } else {
         this.mean = mean;
         this.standardDeviation = sd;
         this.logStandardDeviationPlusHalfLog2Pi = FastMath.log(sd) + 0.5 * FastMath.log(Math.PI * 2);
         this.solverAbsoluteAccuracy = inverseCumAccuracy;
      }
   }

   public double getMean() {
      return this.mean;
   }

   public double getStandardDeviation() {
      return this.standardDeviation;
   }

   @Override
   public double density(double x) {
      return FastMath.exp(this.logDensity(x));
   }

   @Override
   public double logDensity(double x) {
      double x0 = x - this.mean;
      double x1 = x0 / this.standardDeviation;
      return -0.5 * x1 * x1 - this.logStandardDeviationPlusHalfLog2Pi;
   }

   @Override
   public double cumulativeProbability(double x) {
      double dev = x - this.mean;
      if (FastMath.abs(dev) > 40.0 * this.standardDeviation) {
         return dev < 0.0 ? 0.0 : 1.0;
      } else {
         return 0.5 * Erf.erfc(-dev / (this.standardDeviation * SQRT2));
      }
   }

   @Override
   public double inverseCumulativeProbability(double p) throws OutOfRangeException {
      if (!(p < 0.0) && !(p > 1.0)) {
         return this.mean + this.standardDeviation * SQRT2 * Erf.erfInv(2.0 * p - 1.0);
      } else {
         throw new OutOfRangeException(p, 0, 1);
      }
   }

   @Deprecated
   @Override
   public double cumulativeProbability(double x0, double x1) throws NumberIsTooLargeException {
      return this.probability(x0, x1);
   }

   @Override
   public double probability(double x0, double x1) throws NumberIsTooLargeException {
      if (x0 > x1) {
         throw new NumberIsTooLargeException(LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT, x0, x1, true);
      } else {
         double denom = this.standardDeviation * SQRT2;
         double v0 = (x0 - this.mean) / denom;
         double v1 = (x1 - this.mean) / denom;
         return 0.5 * Erf.erf(v0, v1);
      }
   }

   @Override
   protected double getSolverAbsoluteAccuracy() {
      return this.solverAbsoluteAccuracy;
   }

   @Override
   public double getNumericalMean() {
      return this.getMean();
   }

   @Override
   public double getNumericalVariance() {
      double s = this.getStandardDeviation();
      return s * s;
   }

   @Override
   public double getSupportLowerBound() {
      return Double.NEGATIVE_INFINITY;
   }

   @Override
   public double getSupportUpperBound() {
      return Double.POSITIVE_INFINITY;
   }

   @Override
   public boolean isSupportLowerBoundInclusive() {
      return false;
   }

   @Override
   public boolean isSupportUpperBoundInclusive() {
      return false;
   }

   @Override
   public boolean isSupportConnected() {
      return true;
   }

   @Override
   public double sample() {
      return this.standardDeviation * this.random.nextGaussian() + this.mean;
   }
}
