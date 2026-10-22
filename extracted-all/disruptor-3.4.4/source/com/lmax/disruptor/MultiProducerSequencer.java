package com.lmax.disruptor;

import com.lmax.disruptor.util.Util;
import java.util.concurrent.locks.LockSupport;
import sun.misc.Unsafe;

public final class MultiProducerSequencer extends AbstractSequencer {
   private static final Unsafe UNSAFE = Util.getUnsafe();
   private static final long BASE = (long)UNSAFE.arrayBaseOffset(int[].class);
   private static final long SCALE = (long)UNSAFE.arrayIndexScale(int[].class);
   private final Sequence gatingSequenceCache = new Sequence(-1L);
   private final int[] availableBuffer;
   private final int indexMask;
   private final int indexShift;

   public MultiProducerSequencer(int bufferSize, WaitStrategy waitStrategy) {
      super(bufferSize, waitStrategy);
      this.availableBuffer = new int[bufferSize];
      this.indexMask = bufferSize - 1;
      this.indexShift = Util.log2(bufferSize);
      this.initialiseAvailableBuffer();
   }

   @Override
   public boolean hasAvailableCapacity(int requiredCapacity) {
      return this.hasAvailableCapacity(this.gatingSequences, requiredCapacity, this.cursor.get());
   }

   private boolean hasAvailableCapacity(Sequence[] gatingSequences, int requiredCapacity, long cursorValue) {
      long wrapPoint = cursorValue + (long)requiredCapacity - (long)this.bufferSize;
      long cachedGatingSequence = this.gatingSequenceCache.get();
      if (wrapPoint > cachedGatingSequence || cachedGatingSequence > cursorValue) {
         long minSequence = Util.getMinimumSequence(gatingSequences, cursorValue);
         this.gatingSequenceCache.set(minSequence);
         if (wrapPoint > minSequence) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void claim(long sequence) {
      this.cursor.set(sequence);
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
         while (true) {
            long current = this.cursor.get();
            long next = current + (long)n;
            long wrapPoint = next - (long)this.bufferSize;
            long cachedGatingSequence = this.gatingSequenceCache.get();
            if (wrapPoint <= cachedGatingSequence && cachedGatingSequence <= current) {
               if (this.cursor.compareAndSet(current, next)) {
                  return next;
               }
            } else {
               long gatingSequence = Util.getMinimumSequence(this.gatingSequences, current);
               if (wrapPoint > gatingSequence) {
                  LockSupport.parkNanos(1L);
               } else {
                  this.gatingSequenceCache.set(gatingSequence);
               }
            }
         }
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
      } else {
         long current;
         long next;
         do {
            current = this.cursor.get();
            next = current + (long)n;
            if (!this.hasAvailableCapacity(this.gatingSequences, n, current)) {
               throw InsufficientCapacityException.INSTANCE;
            }
         } while (!this.cursor.compareAndSet(current, next));

         return next;
      }
   }

   @Override
   public long remainingCapacity() {
      long consumed = Util.getMinimumSequence(this.gatingSequences, this.cursor.get());
      long produced = this.cursor.get();
      return (long)this.getBufferSize() - (produced - consumed);
   }

   private void initialiseAvailableBuffer() {
      for (int i = this.availableBuffer.length - 1; i != 0; i--) {
         this.setAvailableBufferValue(i, -1);
      }

      this.setAvailableBufferValue(0, -1);
   }

   @Override
   public void publish(long sequence) {
      this.setAvailable(sequence);
      this.waitStrategy.signalAllWhenBlocking();
   }

   @Override
   public void publish(long lo, long hi) {
      for (long l = lo; l <= hi; l++) {
         this.setAvailable(l);
      }

      this.waitStrategy.signalAllWhenBlocking();
   }

   private void setAvailable(long sequence) {
      this.setAvailableBufferValue(this.calculateIndex(sequence), this.calculateAvailabilityFlag(sequence));
   }

   private void setAvailableBufferValue(int index, int flag) {
      long bufferAddress = (long)index * SCALE + BASE;
      UNSAFE.putOrderedInt(this.availableBuffer, bufferAddress, flag);
   }

   @Override
   public boolean isAvailable(long sequence) {
      int index = this.calculateIndex(sequence);
      int flag = this.calculateAvailabilityFlag(sequence);
      long bufferAddress = (long)index * SCALE + BASE;
      return UNSAFE.getIntVolatile(this.availableBuffer, bufferAddress) == flag;
   }

   @Override
   public long getHighestPublishedSequence(long lowerBound, long availableSequence) {
      for (long sequence = lowerBound; sequence <= availableSequence; sequence++) {
         if (!this.isAvailable(sequence)) {
            return sequence - 1L;
         }
      }

      return availableSequence;
   }

   private int calculateAvailabilityFlag(long sequence) {
      return (int)(sequence >>> this.indexShift);
   }

   private int calculateIndex(long sequence) {
      return (int)sequence & this.indexMask;
   }
}
