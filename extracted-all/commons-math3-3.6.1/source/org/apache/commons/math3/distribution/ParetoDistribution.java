package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.FastMath;

public class ParetoDistribution extends AbstractRealDistribution {
   public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
   private static final long serialVersionUID = 20130424L;
   private final double scale;
   private final double shape;
   private final double solverAbsoluteAccuracy;

   public ParetoDistribution() {
      this(1.0, 1.0);
   }

   public ParetoDistribution(double scale, double shape) throws NotStrictlyPositiveException {
      this(scale, shape, 1.0E-9);
   }

   public ParetoDistribution(double scale, double shape, double inverseCumAccuracy) throws NotStrictlyPositiveException {
      this(new Well19937c(), scale, shape, inverseCumAccuracy);
   }

   public ParetoDistribution(RandomGenerator rng, double scale, double shape) throws NotStrictlyPositiveException {
      this(rng, scale, shape, 1.0E-9);
   }

   public ParetoDistribution(RandomGenerator rng, double scale, double shape, double inverseCumAccuracy) throws NotStrictlyPositiveException {
      super(rng);
      if (scale <= 0.0) {
         throw new NotStrictlyPositiveException(LocalizedFormats.SCALE, scale);
      } else if (shape <= 0.0) {
         throw new NotStrictlyPositiveException(LocalizedFormats.SHAPE, shape);
      } else {
         this.scale = scale;
         this.shape = shape;
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
      return x < this.scale ? 0.0 : FastMath.pow(this.scale, this.shape) / FastMath.pow(x, this.shape + 1.0) * this.shape;
   }

   @Override
   public double logDensity(double x) {
      return x < this.scale
         ? Double.NEGATIVE_INFINITY
         : FastMath.log(this.scale) * this.shape - FastMath.log(x) * (this.shape + 1.0) + FastMath.log(this.shape);
   }

   @Override
   public double cumulativeProbability(double x) {
      return x <= this.scale ? 0.0 : 1.0 - FastMath.pow(this.scale / x, this.shape);
   }

   @Deprecated
   @Override
   public double cumulativeProbability(double x0, double x1) throws NumberIsTooLargeException {
      return this.probability(x0, x1);
   }

   @Override
   protected double getSolverAbsoluteAccuracy() {
      return this.solverAbsoluteAccuracy;
   }

   @Override
   public double getNumericalMean() {
      return this.shape <= 1.0 ? Double.POSITIVE_INFINITY : this.shape * this.scale / (this.shape - 1.0);
   }

   @Override
   public double getNumericalVariance() {
      if (this.shape <= 2.0) {
         return Double.POSITIVE_INFINITY;
      } else {
         double s = this.shape - 1.0;
         return this.scale * this.scale * this.shape / (s * s) / (this.shape - 2.0);
      }
   }

   @Override
   public double getSupportLowerBound() {
      return this.scale;
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
      double n = this.random.nextDouble();
      return this.scale / FastMath.pow(n, 1.0 / this.shape);
   }
}
