package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;

public abstract class AbstractMultivariateRealDistribution implements MultivariateRealDistribution {
   protected final RandomGenerator random;
   private final int dimension;

   protected AbstractMultivariateRealDistribution(RandomGenerator rng, int n) {
      this.random = rng;
      this.dimension = n;
   }

   @Override
   public void reseedRandomGenerator(long seed) {
      this.random.setSeed(seed);
   }

   @Override
   public int getDimension() {
      return this.dimension;
   }

   @Override
   public abstract double[] sample();

   @Override
   public double[][] sample(int sampleSize) {
      if (sampleSize <= 0) {
         throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, sampleSize);
      } else {
         double[][] out = new double[sampleSize][this.dimension];

         for (int i = 0; i < sampleSize; i++) {
            out[i] = this.sample();
         }

         return out;
      }
   }
}
