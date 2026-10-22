package com.lmax.disruptor;

import com.lmax.disruptor.util.Util;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public abstract class AbstractSequencer implements Sequencer {
   private static final AtomicReferenceFieldUpdater<AbstractSequencer, Sequence[]> SEQUENCE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(
      AbstractSequencer.class, Sequence[].class, "gatingSequences"
   );
   protected final int bufferSize;
   protected final WaitStrategy waitStrategy;
   protected final Sequence cursor = new Sequence(-1L);
   protected volatile Sequence[] gatingSequences = new Sequence[0];

   public AbstractSequencer(int bufferSize, WaitStrategy waitStrategy) {
      if (bufferSize < 1) {
         throw new IllegalArgumentException("bufferSize must not be less than 1");
      } else if (Integer.bitCount(bufferSize) != 1) {
         throw new IllegalArgumentException("bufferSize must be a power of 2");
      } else {
         this.bufferSize = bufferSize;
         this.waitStrategy = waitStrategy;
      }
   }

   @Override
   public final long getCursor() {
      return this.cursor.get();
   }

   @Override
   public final int getBufferSize() {
      return this.bufferSize;
   }

   @Override
   public final void addGatingSequences(Sequence... gatingSequences) {
      SequenceGroups.addSequences(this, SEQUENCE_UPDATER, this, gatingSequences);
   }

   @Override
   public boolean removeGatingSequence(Sequence sequence) {
      return SequenceGroups.removeSequence(this, SEQUENCE_UPDATER, sequence);
   }

   @Override
   public long getMinimumSequence() {
      return Util.getMinimumSequence(this.gatingSequences, this.cursor.get());
   }

   @Override
   public SequenceBarrier newBarrier(Sequence... sequencesToTrack) {
      return new ProcessingSequenceBarrier(this, this.waitStrategy, this.cursor, sequencesToTrack);
   }

   @Override
   public <T> EventPoller<T> newPoller(DataProvider<T> dataProvider, Sequence... gatingSequences) {
      return EventPoller.newInstance(dataProvider, this, new Sequence(), this.cursor, gatingSequences);
   }

   @Override
   public String toString() {
      return "AbstractSequencer{waitStrategy="
         + this.waitStrategy
         + ", cursor="
         + this.cursor
         + ", gatingSequences="
         + Arrays.toString((Object[])this.gatingSequences)
         + '}';
   }
}
