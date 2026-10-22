package org.apache.commons.math3.random;

import java.util.Random;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public class RandomGeneratorFactory {
   private RandomGeneratorFactory() {
   }

   public static RandomGenerator createRandomGenerator(final Random rng) {
      return new RandomGenerator() {
         @Override
         public void setSeed(int seed) {
            rng.setSeed((long)seed);
         }

         @Override
         public void setSeed(int[] seed) {
            rng.setSeed(RandomGeneratorFactory.convertToLong(seed));
         }

         @Override
         public void setSeed(long seed) {
            rng.setSeed(seed);
         }

         @Override
         public void nextBytes(byte[] bytes) {
            rng.nextBytes(bytes);
         }

         @Override
         public int nextInt() {
            return rng.nextInt();
         }

         @Override
         public int nextInt(int n) {
            if (n <= 0) {
               throw new NotStrictlyPositiveException(n);
            } else {
               return rng.nextInt(n);
            }
         }

         @Override
         public long nextLong() {
            return rng.nextLong();
         }

         @Override
         public boolean nextBoolean() {
            return rng.nextBoolean();
         }

         @Override
         public float nextFloat() {
            return rng.nextFloat();
         }

         @Override
         public double nextDouble() {
            return rng.nextDouble();
         }

         @Override
         public double nextGaussian() {
            return rng.nextGaussian();
         }
      };
   }

   public static long convertToLong(int[] seed) {
      long prime = 4294967291L;
      long combined = 0L;

      for (int s : seed) {
         combined = combined * 4294967291L + (long)s;
      }

      return combined;
   }
}
