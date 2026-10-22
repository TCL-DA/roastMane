package org.apache.commons.math3.random;

import org.apache.commons.math3.util.FastMath;

public class UniformRandomGenerator implements NormalizedRandomGenerator {
   private static final double SQRT3 = FastMath.sqrt(3.0);
   private final RandomGenerator generator;

   public UniformRandomGenerator(RandomGenerator generator) {
      this.generator = generator;
   }

   @Override
   public double nextNormalizedDouble() {
      return SQRT3 * (2.0 * this.generator.nextDouble() - 1.0);
   }
}
