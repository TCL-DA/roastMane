package org.apache.commons.math3.random;

import java.util.Random;

public class JDKRandomGenerator extends Random implements RandomGenerator {
   private static final long serialVersionUID = -7745277476784028798L;

   public JDKRandomGenerator() {
   }

   public JDKRandomGenerator(int seed) {
      this.setSeed(seed);
   }

   @Override
   public void setSeed(int seed) {
      this.setSeed((long)seed);
   }

   @Override
   public void setSeed(int[] seed) {
      this.setSeed(RandomGeneratorFactory.convertToLong(seed));
   }
}
