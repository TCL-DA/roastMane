package com.lmax.disruptor;

import com.lmax.disruptor.util.Util;
import java.util.concurrent.locks.LockSupport;

public final class SingleProducerSequencer extends SingleProducerSequencerFields {
   protected long p1;
   protected long p2;
   protected long p3;
   protected long p4;
   protected long p5;
   protected long p6;
   protected long p7;

   public SingleProducerSequencer(int bufferSize, WaitStrategy waitStrategy) {
      super(bufferSize, waitStrategy);
   }

   @Override
   public boolean hasAvailableCapacity(int requiredCapacity) {
      return this.hasAvailableCapacity(requiredCapacity, false);
   }

   private boolean hasAvailableCapacity(int requiredCapacity, boolean doStore) {
      long nextValue = this.nextValue;
      long wrapPoint = nextValue + (long)requiredCapacity - (long)this.bufferSize;
      long cachedGatingSequence = this.cachedValue;
      if (wrapPoint > cachedGatingSequence || cachedGatingSequence > nextValue) {
         if (doStore) {
            this.cursor.setVolatile(nextValue);
         }

         long minSequence = Util.getMinimumSequence(this.gatingSequences, nextValue);
         this.cachedValue = minSequence;
         if (wrapPoint > minSequence) {
            return false;
         }
      }

      return true;
   }

   @Override
   public long next() {
      return this.next(1);
   }

   @Override
   public long next(int n) {
      if (n < 1) {
         throw new IllegalArgumentException("n must be > 0");
      } else {
         long nextValue = this.nextValue;
         long nextSequence = nextValue + (long)n;
         long wrapPoint = nextSequence - (long)this.bufferSize;
         long cachedGatingSequence = this.cachedValue;
         if (wrapPoint > cachedGatingSequence || cachedGatingSequence > nextValue) {
            this.cursor.setVolatile(nextValue);

            long minSequence;
            while (wrapPoint > (minSequence = Util.getMinimumSequence(this.gatingSequences, nextValue))) {
               LockSupport.parkNanos(1L);
            }

            this.cachedValue = minSequence;
         }

         this.nextValue = nextSequence;
         return nextSequence;
      }
   }

   @Override
   public long tryNext() throws InsufficientCapacityException {
      return this.tryNext(1);
   }

   @Override
   public long tryNext(int n) throws InsufficientCapacityException {
      if (n < 1) {
         throw new IllegalArgumentException("n must be > 0");
      } else if (!this.hasAvailableCapacity(n, true)) {
         throw InsufficientCapacityException.INSTANCE;
      } else {
         return this.nextValue += (long)n;
      }
   }

   @Override
   public long remainingCapacity() {
      long nextValue = this.nextValue;
      long consumed = Util.getMinimumSequence(this.gatingSequences, nextValue);
      return (long)this.getBufferSize() - (nextValue - consumed);
   }

   @Override
   public void claim(long sequence) {
      this.nextValue = sequence;
   }

   @Override
   public void publish(long sequence) {
      this.cursor.set(sequence);
      this.waitStrategy.signalAllWhenBlocking();
   }

   @Override
   public void publish(long lo, long hi) {
      this.publish(hi);
   }

   @Override
   public boolean isAvailable(long sequence) {
      return sequence <= this.cursor.get();
   }

   @Override
   public long getHighestPublishedSequence(long lowerBound, long availableSequence) {
      return availableSequence;
   }
}
