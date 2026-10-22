package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.util.FastMath;

public class NakagamiDistribution extends AbstractRealDistribution {
   public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
   private static final long serialVersionUID = 20141003L;
   private final double mu;
   private final double omega;
   private final double inverseAbsoluteAccuracy;

   public NakagamiDistribution(double mu, double omega) {
      this(mu, omega, 1.0E-9);
   }

   public NakagamiDistribution(double mu, double omega, double inverseAbsoluteAccuracy) {
      this(new Well19937c(), mu, omega, inverseAbsoluteAccuracy);
   }

   public NakagamiDistribution(RandomGenerator rng, double mu, double omega, double inverseAbsoluteAccuracy) {
      super(rng);
      if (mu < 0.5) {
         throw new NumberIsTooSmallException(mu, 0.5, true);
      } else if (omega <= 0.0) {
         throw new NotStrictlyPositiveException(LocalizedFormats.NOT_POSITIVE_SCALE, omega);
      } else {
         this.mu = mu;
         this.omega = omega;
         this.inverseAbsoluteAccuracy = inverseAbsoluteAccuracy;
      }
   }

   public double getShape() {
      return this.mu;
   }

   public double getScale() {
      return this.omega;
   }

   @Override
   protected double getSolverAbsoluteAccuracy() {
      return this.inverseAbsoluteAccuracy;
   }

   @Override
   public double density(double x) {
      return x <= 0.0
         ? 0.0
         : 2.0
            * FastMath.pow(this.mu, this.mu)
            / (Gamma.gamma(this.mu) * FastMath.pow(this.omega, this.mu))
            * FastMath.pow(x, 2.0 * this.mu - 1.0)
            * FastMath.exp(-this.mu * x * x / this.omega);
   }

   @Override
   public double cumulativeProbability(double x) {
      return Gamma.regularizedGammaP(this.mu, this.mu * x * x / this.omega);
   }

   @Override
   public double getNumericalMean() {
      return Gamma.gamma(this.mu + 0.5) / Gamma.gamma(this.mu) * FastMath.sqrt(this.omega / this.mu);
   }

   @Override
   public double getNumericalVariance() {
      double v = Gamma.gamma(this.mu + 0.5) / Gamma.gamma(this.mu);
      return this.omega * (1.0 - 1.0 / this.mu * v * v);
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
