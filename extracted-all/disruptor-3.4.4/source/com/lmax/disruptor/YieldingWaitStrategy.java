package com.lmax.disruptor;

public final class YieldingWaitStrategy implements WaitStrategy {
   private static final int SPIN_TRIES = 100;

   @Override
   public long waitFor(long sequence, Sequence cursor, Sequence dependentSequence, SequenceBarrier barrier) throws AlertException, InterruptedException {
      int counter = 100;

      long availableSequence;
      while ((availableSequence = dependentSequence.get()) < sequence) {
         counter = this.applyWaitMethod(barrier, counter);
      }

      return availableSequence;
   }

   @Override
   public void signalAllWhenBlocking() {
   }

   private int applyWaitMethod(SequenceBarrier barrier, int counter) throws AlertException {
      barrier.checkAlert();
      if (0 == counter) {
         Thread.yield();
      } else {
         counter--;
      }

      return counter;
   }
}
