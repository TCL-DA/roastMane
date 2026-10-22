package org.apache.commons.math3.random;

public class SynchronizedRandomGenerator implements RandomGenerator {
   private final RandomGenerator wrapped;

   public SynchronizedRandomGenerator(RandomGenerator rng) {
      this.wrapped = rng;
   }

   @Override
   public synchronized void setSeed(int seed) {
      this.wrapped.setSeed(seed);
   }

   @Override
   public synchronized void setSeed(int[] seed) {
      this.wrapped.setSeed(seed);
   }

   @Override
   public synchronized void setSeed(long seed) {
      this.wrapped.setSeed(seed);
   }

   @Override
   public synchronized void nextBytes(byte[] bytes) {
      this.wrapped.nextBytes(bytes);
   }

   @Override
   public synchronized int nextInt() {
      return this.wrapped.nextInt();
   }

   @Override
   public synchronized int nextInt(int n) {
      return this.wrapped.nextInt(n);
   }

   @Override
   public synchronized long nextLong() {
      return this.wrapped.nextLong();
   }

   @Override
   public synchronized boolean nextBoolean() {
      return this.wrapped.nextBoolean();
   }

   @Override
   public synchronized float nextFloat() {
      return this.wrapped.nextFloat();
   }

   @Override
   public synchronized double nextDouble() {
      return this.wrapped.nextDouble();
   }

   @Override
   public synchronized double nextGaussian() {
      return this.wrapped.nextGaussian();
   }
}
