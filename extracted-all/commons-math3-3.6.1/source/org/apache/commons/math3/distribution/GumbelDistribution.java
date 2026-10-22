package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.FastMath;

public class GumbelDistribution extends AbstractRealDistribution {
   private static final long serialVersionUID = 20141003L;
   private static final double EULER = 0.5778636748954609;
   private final double mu;
   private final double beta;

   public GumbelDistribution(double mu, double beta) {
      this(new Well19937c(), mu, beta);
   }

   public GumbelDistribution(RandomGenerator rng, double mu, double beta) {
      super(rng);
      if (beta <= 0.0) {
         throw new NotStrictlyPositiveException(LocalizedFormats.SCALE, beta);
      } else {
         this.beta = beta;
         this.mu = mu;
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
      double z = (x - this.mu) / this.beta;
      double t = FastMath.exp(-z);
      return FastMath.exp(-z - t) / this.beta;
   }

   @Override
   public double cumulativeProbability(double x) {
      double z = (x - this.mu) / this.beta;
      return FastMath.exp(-FastMath.exp(-z));
   }

   @Override
   public double inverseCumulativeProbability(double p) throws OutOfRangeException {
      if (p < 0.0 || p > 1.0) {
         throw new OutOfRangeException(p, 0.0, 1.0);
      } else if (p == 0.0) {
         return Double.NEGATIVE_INFINITY;
      } else {
         return p == 1.0 ? Double.POSITIVE_INFINITY : this.mu - FastMath.log(-FastMath.log(p)) * this.beta;
      }
   }

   @Override
   public double getNumericalMean() {
      return this.mu + 0.5778636748954609 * this.beta;
   }

   @Override
   public double getNumericalVariance() {
      return 1.6449340668482264 * this.beta * this.beta;
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
