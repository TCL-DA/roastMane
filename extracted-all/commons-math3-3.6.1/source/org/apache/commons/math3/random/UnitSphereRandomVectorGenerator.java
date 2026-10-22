package org.apache.commons.math3.random;

import org.apache.commons.math3.util.FastMath;

public class UnitSphereRandomVectorGenerator implements RandomVectorGenerator {
   private final RandomGenerator rand;
   private final int dimension;

   public UnitSphereRandomVectorGenerator(int dimension, RandomGenerator rand) {
      this.dimension = dimension;
      this.rand = rand;
   }

   public UnitSphereRandomVectorGenerator(int dimension) {
      this(dimension, new MersenneTwister());
   }

   @Override
   public double[] nextVector() {
      double[] v = new double[this.dimension];
      double normSq = 0.0;

      for (int i = 0; i < this.dimension; i++) {
         double comp = this.rand.nextGaussian();
         v[i] = comp;
         normSq += comp * comp;
      }

      double f = 1.0 / FastMath.sqrt(normSq);

      for (int i = 0; i < this.dimension; i++) {
         v[i] *= f;
      }

      return v;
   }
}
