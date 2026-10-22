package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.FastMath;

public class LaplaceDistribution extends AbstractRealDistribution {
   private static final long serialVersionUID = 20141003L;
   private final double mu;
   private final double beta;

   public LaplaceDistribution(double mu, double beta) {
      this(new Well19937c(), mu, beta);
   }

   public LaplaceDistribution(RandomGenerator rng, double mu, double beta) {
      super(rng);
      if (beta <= 0.0) {
         throw new NotStrictlyPositiveException(LocalizedFormats.NOT_POSITIVE_SCALE, beta);
      } else {
         this.mu = mu;
         this.beta = beta;
      }
   }

   public double getLocation() {
      return this.mu;
   }

   public double getScale() {
      return this.beta;
   }

   @Override
   public double density(double x) {
      return FastMath.exp(-FastMath.abs(x - this.mu) / this.beta) / (2.0 * this.beta);
   }

   @Override
   public double cumulativeProbability(double x) {
      return x <= this.mu ? FastMath.exp((x - this.mu) / this.beta) / 2.0 : 1.0 - FastMath.exp((this.mu - x) / this.beta) / 2.0;
   }

   @Override
   public double inverseCumulativeProbability(double p) throws OutOfRangeException {
      if (p < 0.0 || p > 1.0) {
         throw new OutOfRangeException(p, 0.0, 1.0);
      } else if (p == 0.0) {
         return Double.NEGATIVE_INFINITY;
      } else if (p == 1.0) {
         return Double.POSITIVE_INFINITY;
      } else {
         double x = p > 0.5 ? -Math.log(2.0 - 2.0 * p) : Math.log(2.0 * p);
         return this.mu + this.beta * x;
      }
   }

   @Override
   public double getNumericalMean() {
      return this.mu;
   }

   @Override
   public double getNumericalVariance() {
      return 2.0 * this.beta * this.beta;
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
}
