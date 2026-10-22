package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.util.FastMath;

public class GammaDistribution extends AbstractRealDistribution {
   public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
   private static final long serialVersionUID = 20120524L;
   private final double shape;
   private final double scale;
   private final double shiftedShape;
   private final double densityPrefactor1;
   private final double logDensityPrefactor1;
   private final double densityPrefactor2;
   private final double logDensityPrefactor2;
   private final double minY;
   private final double maxLogY;
   private final double solverAbsoluteAccuracy;

   public GammaDistribution(double shape, double scale) throws NotStrictlyPositiveException {
      this(shape, scale, 1.0E-9);
   }

   public GammaDistribution(double shape, double scale, double inverseCumAccuracy) throws NotStrictlyPositiveException {
      this(new Well19937c(), shape, scale, inverseCumAccuracy);
   }

   public GammaDistribution(RandomGenerator rng, double shape, double scale) throws NotStrictlyPositiveException {
      this(rng, shape, scale, 1.0E-9);
   }

   public GammaDistribution(RandomGenerator rng, double shape, double scale, double inverseCumAccuracy) throws NotStrictlyPositiveException {
      super(rng);
      if (shape <= 0.0) {
         throw new NotStrictlyPositiveException(LocalizedFormats.SHAPE, shape);
      } else if (scale <= 0.0) {
         throw new NotStrictlyPositiveException(LocalizedFormats.SCALE, scale);
      } else {
         this.shape = shape;
         this.scale = scale;
         this.solverAbsoluteAccuracy = inverseCumAccuracy;
         this.shiftedShape = shape + 4.7421875 + 0.5;
         double aux = Math.E / ((Math.PI * 2) * this.shiftedShape);
         this.densityPrefactor2 = shape * FastMath.sqrt(aux) / Gamma.lanczos(shape);
         this.logDensityPrefactor2 = FastMath.log(shape) + 0.5 * FastMath.log(aux) - FastMath.log(Gamma.lanczos(shape));
         this.densityPrefactor1 = this.densityPrefactor2 / scale * FastMath.pow(this.shiftedShape, -shape) * FastMath.exp(shape + 4.7421875);
         this.logDensityPrefactor1 = this.logDensityPrefactor2 - FastMath.log(scale) - FastMath.log(this.shiftedShape) * shape + shape + 4.7421875;
         this.minY = shape + 4.7421875 - FastMath.log(Double.MAX_VALUE);
         this.maxLogY = FastMath.log(Double.MAX_VALUE) / (shape - 1.0);
      }
   }

   @Deprecated
   public double getAlpha() {
      return this.shape;
   }

   public double getShape() {
      return this.shape;
   }

   @Deprecated
   public double getBeta() {
      return this.scale;
   }

   public double getScale() {
      return this.scale;
   }

   @Override
   public double density(double x) {
      if (x < 0.0) {
         return 0.0;
      } else {
         double y = x / this.scale;
         if (!(y <= this.minY) && !(FastMath.log(y) >= this.maxLogY)) {
            return this.densityPrefactor1 * FastMath.exp(-y) * FastMath.pow(y, this.shape - 1.0);
         } else {
            double aux1 = (y - this.shiftedShape) / this.shiftedShape;
            double aux2 = this.shape * (FastMath.log1p(aux1) - aux1);
            double aux3 = -y * 5.2421875 / this.shiftedShape + 4.7421875 + aux2;
            return this.densityPrefactor2 / x * FastMath.exp(aux3);
         }
      }
   }

   @Override
   public double logDensity(double x) {
      if (x < 0.0) {
         return Double.NEGATIVE_INFINITY;
      } else {
         double y = x / this.scale;
         if (!(y <= this.minY) && !(FastMath.log(y) >= this.maxLogY)) {
            return this.logDensityPrefactor1 - y + FastMath.log(y) * (this.shape - 1.0);
         } else {
            double aux1 = (y - this.shiftedShape) / this.shiftedShape;
            double aux2 = this.shape * (FastMath.log1p(aux1) - aux1);
            double aux3 = -y * 5.2421875 / this.shiftedShape + 4.7421875 + aux2;
            return this.logDensityPrefactor2 - FastMath.log(x) + aux3;
         }
      }
   }

   @Override
   public double cumulativeProbability(double x) {
      double ret;
      if (x <= 0.0) {
         ret = 0.0;
      } else {
         ret = Gamma.regularizedGammaP(this.shape, x / this.scale);
      }

      return ret;
   }

   @Override
   protected double getSolverAbsoluteAccuracy() {
      return this.solverAbsoluteAccuracy;
   }

   @Override
   public double getNumericalMean() {
      return this.shape * this.scale;
   }

   @Override
   public double getNumericalVariance() {
      return this.shape * this.scale * this.scale;
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
      if (this.shape < 1.0) {
         while (true) {
            double u = this.random.nextDouble();
            double bGS = 1.0 + this.shape / Math.E;
            double p = bGS * u;
            if (p <= 1.0) {
               double x = FastMath.pow(p, 1.0 / this.shape);
               double u2 = this.random.nextDouble();
               if (!(u2 > FastMath.exp(-x))) {
                  return this.scale * x;
               }
            } else {
               double x = -1.0 * FastMath.log((bGS - p) / this.shape);
               double u2 = this.random.nextDouble();
               if (!(u2 > FastMath.pow(x, this.shape - 1.0))) {
                  return this.scale * x;
               }
            }
         }
      } else {
         double d = this.shape - 0.3333333333333333;
         double c = 1.0 / (3.0 * FastMath.sqrt(d));

         while (true) {
            double x = this.random.nextGaussian();
            double v = (1.0 + c * x) * (1.0 + c * x) * (1.0 + c * x);
            if (!(v <= 0.0)) {
               double x2 = x * x;
               double u = this.random.nextDouble();
               if (u < 1.0 - 0.0331 * x2 * x2) {
                  return this.scale * d * v;
               }

               if (FastMath.log(u) < 0.5 * x2 + d * (1.0 - v + FastMath.log(v))) {
                  return this.scale * d * v;
               }
            }
         }
      }
   }
}
