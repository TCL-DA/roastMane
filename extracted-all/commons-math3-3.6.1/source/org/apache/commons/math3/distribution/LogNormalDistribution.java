package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.util.FastMath;

public class LogNormalDistribution extends AbstractRealDistribution {
   public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
   private static final long serialVersionUID = 20120112L;
   private static final double SQRT2PI = FastMath.sqrt(Math.PI * 2);
   private static final double SQRT2 = FastMath.sqrt(2.0);
   private final double scale;
   private final double shape;
   private final double logShapePlusHalfLog2Pi;
   private final double solverAbsoluteAccuracy;

   public LogNormalDistribution() {
      this(0.0, 1.0);
   }

   public LogNormalDistribution(double scale, double shape) throws NotStrictlyPositiveException {
      this(scale, shape, 1.0E-9);
   }

   public LogNormalDistribution(double scale, double shape, double inverseCumAccuracy) throws NotStrictlyPositiveException {
      this(new Well19937c(), scale, shape, inverseCumAccuracy);
   }

   public LogNormalDistribution(RandomGenerator rng, double scale, double shape) throws NotStrictlyPositiveException {
      this(rng, scale, shape, 1.0E-9);
   }

   public LogNormalDistribution(RandomGenerator rng, double scale, double shape, double inverseCumAccuracy) throws NotStrictlyPositiveException {
      super(rng);
      if (shape <= 0.0) {
         throw new NotStrictlyPositiveException(LocalizedFormats.SHAPE, shape);
      } else {
         this.scale = scale;
         this.shape = shape;
         this.logShapePlusHalfLog2Pi = FastMath.log(shape) + 0.5 * FastMath.log(Math.PI * 2);
         this.solverAbsoluteAccuracy = inverseCumAccuracy;
      }
   }

   public double getScale() {
      return this.scale;
   }

   public double getShape() {
      return this.shape;
   }

   @Override
   public double density(double x) {
      if (x <= 0.0) {
         return 0.0;
      } else {
         double x0 = FastMath.log(x) - this.scale;
         double x1 = x0 / this.shape;
         return FastMath.exp(-0.5 * x1 * x1) / (this.shape * SQRT2PI * x);
      }
   }

   @Override
   public double logDensity(double x) {
      if (x <= 0.0) {
         return Double.NEGATIVE_INFINITY;
      } else {
         double logX = FastMath.log(x);
         double x0 = logX - this.scale;
         double x1 = x0 / this.shape;
         return -0.5 * x1 * x1 - (this.logShapePlusHalfLog2Pi + logX);
      }
   }

   @Override
   public double cumulativeProbability(double x) {
      if (x <= 0.0) {
         return 0.0;
      } else {
         double dev = FastMath.log(x) - this.scale;
         if (FastMath.abs(dev) > 40.0 * this.shape) {
            return dev < 0.0 ? 0.0 : 1.0;
         } else {
            return 0.5 + 0.5 * Erf.erf(dev / (this.shape * SQRT2));
         }
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
      } else if (!(x0 <= 0.0) && !(x1 <= 0.0)) {
         double denom = this.shape * SQRT2;
         double v0 = (FastMath.log(x0) - this.scale) / denom;
         double v1 = (FastMath.log(x1) - this.scale) / denom;
         return 0.5 * Erf.erf(v0, v1);
      } else {
         return super.probability(x0, x1);
      }
   }

   @Override
   protected double getSolverAbsoluteAccuracy() {
      return this.solverAbsoluteAccuracy;
   }

   @Override
   public double getNumericalMean() {
      double s = this.shape;
      return FastMath.exp(this.scale + s * s / 2.0);
   }

   @Override
   public double getNumericalVariance() {
      double s = this.shape;
      double ss = s * s;
      return FastMath.expm1(ss) * FastMath.exp(2.0 * this.scale + ss);
   }

   @Override
   public double getSupportLowerBound() {
      return 0.0;
   }

   @Override
   public double getSupportUpperBound() {
      return Double.POSITIVE_INFINITY;
   }

   @Override
   public boolean isSupportLowerBoundInclusive() {
      return true;
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
      double n = this.random.nextGaussian();
      return FastMath.exp(this.scale + this.shape * n);
   }
}
