package org.apache.commons.math3.distribution;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class ChiSquaredDistribution extends AbstractRealDistribution {
   public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
   private static final long serialVersionUID = -8352658048349159782L;
   private final GammaDistribution gamma;
   private final double solverAbsoluteAccuracy;

   public ChiSquaredDistribution(double degreesOfFreedom) {
      this(degreesOfFreedom, 1.0E-9);
   }

   public ChiSquaredDistribution(double degreesOfFreedom, double inverseCumAccuracy) {
      this(new Well19937c(), degreesOfFreedom, inverseCumAccuracy);
   }

   public ChiSquaredDistribution(RandomGenerator rng, double degreesOfFreedom) {
      this(rng, degreesOfFreedom, 1.0E-9);
   }

   public ChiSquaredDistribution(RandomGenerator rng, double degreesOfFreedom, double inverseCumAccuracy) {
      super(rng);
      this.gamma = new GammaDistribution(degreesOfFreedom / 2.0, 2.0);
      this.solverAbsoluteAccuracy = inverseCumAccuracy;
   }

   public double getDegreesOfFreedom() {
      return this.gamma.getShape() * 2.0;
   }

   @Override
   public double density(double x) {
      return this.gamma.density(x);
   }

   @Override
   public double logDensity(double x) {
      return this.gamma.logDensity(x);
   }

   @Override
   public double cumulativeProbability(double x) {
      return this.gamma.cumulativeProbability(x);
   }

   @Override
   protected double getSolverAbsoluteAccuracy() {
      return this.solverAbsoluteAccuracy;
   }

   @Override
   public double getNumericalMean() {
      return this.getDegreesOfFreedom();
   }

   @Override
   public double getNumericalVariance() {
      return 2.0 * this.getDegreesOfFreedom();
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
}
