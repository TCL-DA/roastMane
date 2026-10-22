package com.lmax.disruptor;

import com.lmax.disruptor.dsl.ProducerType;

public final class RingBuffer<E> extends RingBufferFields<E> implements Cursored, EventSequencer<E>, EventSink<E> {
   public static final long INITIAL_CURSOR_VALUE = -1L;
   protected long p1;
   protected long p2;
   protected long p3;
   protected long p4;
   protected long p5;
   protected long p6;
   protected long p7;

   RingBuffer(EventFactory<E> eventFactory, Sequencer sequencer) {
      super(eventFactory, sequencer);
   }

   public static <E> RingBuffer<E> createMultiProducer(EventFactory<E> factory, int bufferSize, WaitStrategy waitStrategy) {
      MultiProducerSequencer sequencer = new MultiProducerSequencer(bufferSize, waitStrategy);
      return new RingBuffer<>(factory, sequencer);
   }

   public static <E> RingBuffer<E> createMultiProducer(EventFactory<E> factory, int bufferSize) {
      return createMultiProducer(factory, bufferSize, new BlockingWaitStrategy());
   }

   public static <E> RingBuffer<E> createSingleProducer(EventFactory<E> factory, int bufferSize, WaitStrategy waitStrategy) {
      SingleProducerSequencer sequencer = new SingleProducerSequencer(bufferSize, waitStrategy);
      return new RingBuffer<>(factory, sequencer);
   }

   public static <E> RingBuffer<E> createSingleProducer(EventFactory<E> factory, int bufferSize) {
      return createSingleProducer(factory, bufferSize, new BlockingWaitStrategy());
   }

   public static <E> RingBuffer<E> create(ProducerType producerType, EventFactory<E> factory, int bufferSize, WaitStrategy waitStrategy) {
      switch (producerType) {
         case SINGLE:
            return createSingleProducer(factory, bufferSize, waitStrategy);
         case MULTI:
            return createMultiProducer(factory, bufferSize, waitStrategy);
         default:
            throw new IllegalStateException(producerType.toString());
      }
   }

   @Override
   public E get(long sequence) {
      return this.elementAt(sequence);
   }

   @Override
   public long next() {
      return this.sequencer.next();
   }

   @Override
   public long next(int n) {
      return this.sequencer.next(n);
   }

   @Override
   public long tryNext() throws InsufficientCapacityException {
      return this.sequencer.tryNext();
   }

   @Override
   public long tryNext(int n) throws InsufficientCapacityException {
      return this.sequencer.tryNext(n);
   }

   @Deprecated
   public void resetTo(long sequence) {
      this.sequencer.claim(sequence);
      this.sequencer.publish(sequence);
   }

   public E claimAndGetPreallocated(long sequence) {
      this.sequencer.claim(sequence);
      return this.get(sequence);
   }

   @Deprecated
   public boolean isPublished(long sequence) {
      return this.sequencer.isAvailable(sequence);
   }

   public void addGatingSequences(Sequence... gatingSequences) {
      this.sequencer.addGatingSequences(gatingSequences);
   }

   public long getMinimumGatingSequence() {
      return this.sequencer.getMinimumSequence();
   }

   public boolean removeGatingSequence(Sequence sequence) {
      return this.sequencer.removeGatingSequence(sequence);
   }

   public SequenceBarrier newBarrier(Sequence... sequencesToTrack) {
      return this.sequencer.newBarrier(sequencesToTrack);
   }

   public EventPoller<E> newPoller(Sequence... gatingSequences) {
      return this.sequencer.newPoller(this, gatingSequences);
   }

   @Override
   public long getCursor() {
      return this.sequencer.getCursor();
   }

   @Override
   public int getBufferSize() {
      return this.bufferSize;
   }

   @Override
   public boolean hasAvailableCapacity(int requiredCapacity) {
      return this.sequencer.hasAvailableCapacity(requiredCapacity);
   }

   @Override
   public void publishEvent(EventTranslator<E> translator) {
      long sequence = this.sequencer.next();
      this.translateAndPublish(translator, sequence);
   }

   @Override
   public boolean tryPublishEvent(EventTranslator<E> translator) {
      try {
         long sequence = this.sequencer.tryNext();
         this.translateAndPublish(translator, sequence);
         return true;
      } catch (InsufficientCapacityException var4) {
         return false;
      }
   }

   @Override
   public <A> void publishEvent(EventTranslatorOneArg<E, A> translator, A arg0) {
      long sequence = this.sequencer.next();
      this.translateAndPublish(translator, sequence, arg0);
   }

   @Override
   public <A> boolean tryPublishEvent(EventTranslatorOneArg<E, A> translator, A arg0) {
      try {
         long sequence = this.sequencer.tryNext();
         this.translateAndPublish(translator, sequence, arg0);
         return true;
      } catch (InsufficientCapacityException var5) {
         return false;
      }
   }

   @Override
   public <A, B> void publishEvent(EventTranslatorTwoArg<E, A, B> translator, A arg0, B arg1) {
      long sequence = this.sequencer.next();
      this.translateAndPublish(translator, sequence, arg0, arg1);
   }

   @Override
   public <A, B> boolean tryPublishEvent(EventTranslatorTwoArg<E, A, B> translator, A arg0, B arg1) {
      try {
         long sequence = this.sequencer.tryNext();
         this.translateAndPublish(translator, sequence, arg0, arg1);
         return true;
      } catch (InsufficientCapacityException var6) {
         return false;
      }
   }

   @Override
   public <A, B, C> void publishEvent(EventTranslatorThreeArg<E, A, B, C> translator, A arg0, B arg1, C arg2) {
      long sequence = this.sequencer.next();
      this.translateAndPublish(translator, sequence, arg0, arg1, arg2);
   }

   @Override
   public <A, B, C> boolean tryPublishEvent(EventTranslatorThreeArg<E, A, B, C> translator, A arg0, B arg1, C arg2) {
      try {
         long sequence = this.sequencer.tryNext();
         this.translateAndPublish(translator, sequence, arg0, arg1, arg2);
         return true;
      } catch (InsufficientCapacityException var7) {
         return false;
      }
   }

   @Override
   public void publishEvent(EventTranslatorVararg<E> translator, Object... args) {
      long sequence = this.sequencer.next();
      this.translateAndPublish(translator, sequence, args);
   }

   @Override
   public boolean tryPublishEvent(EventTranslatorVararg<E> translator, Object... args) {
      try {
         long sequence = this.sequencer.tryNext();
         this.translateAndPublish(translator, sequence, args);
         return true;
      } catch (InsufficientCapacityException var5) {
         return false;
      }
   }

   @Override
   public void publishEvents(EventTranslator<E>[] translators) {
      this.publishEvents(translators, 0, translators.length);
   }

   @Override
   public void publishEvents(EventTranslator<E>[] translators, int batchStartsAt, int batchSize) {
      this.checkBounds(translators, batchStartsAt, batchSize);
      long finalSequence = this.sequencer.next(batchSize);
      this.translateAndPublishBatch(translators, batchStartsAt, batchSize, finalSequence);
   }

   @Override
   public boolean tryPublishEvents(EventTranslator<E>[] translators) {
      return this.tryPublishEvents(translators, 0, translators.length);
   }

   @Override
   public boolean tryPublishEvents(EventTranslator<E>[] translators, int batchStartsAt, int batchSize) {
      this.checkBounds(translators, batchStartsAt, batchSize);

      try {
         long finalSequence = this.sequencer.tryNext(batchSize);
         this.translateAndPublishBatch(translators, batchStartsAt, batchSize, finalSequence);
         return true;
      } catch (InsufficientCapacityException var6) {
         return false;
      }
   }

   @Override
   public <A> void publishEvents(EventTranslatorOneArg<E, A> translator, A[] arg0) {
      this.publishEvents(translator, 0, arg0.length, arg0);
   }

   @Override
   public <A> void publishEvents(EventTranslatorOneArg<E, A> translator, int batchStartsAt, int batchSize, A[] arg0) {
      this.checkBounds(arg0, batchStartsAt, batchSize);
      long finalSequence = this.sequencer.next(batchSize);
      this.translateAndPublishBatch(translator, arg0, batchStartsAt, batchSize, finalSequence);
   }

   @Override
   public <A> boolean tryPublishEvents(EventTranslatorOneArg<E, A> translator, A[] arg0) {
      return this.tryPublishEvents(translator, 0, arg0.length, arg0);
   }

   @Override
   public <A> boolean tryPublishEvents(EventTranslatorOneArg<E, A> translator, int batchStartsAt, int batchSize, A[] arg0) {
      this.checkBounds(arg0, batchStartsAt, batchSize);

      try {
         long finalSequence = this.sequencer.tryNext(batchSize);
         this.translateAndPublishBatch(translator, arg0, batchStartsAt, batchSize, finalSequence);
         return true;
      } catch (InsufficientCapacityException var7) {
         return false;
      }
   }

   @Override
   public <A, B> void publishEvents(EventTranslatorTwoArg<E, A, B> translator, A[] arg0, B[] arg1) {
      this.publishEvents(translator, 0, arg0.length, arg0, arg1);
   }

   @Override
   public <A, B> void publishEvents(EventTranslatorTwoArg<E, A, B> translator, int batchStartsAt, int batchSize, A[] arg0, B[] arg1) {
      this.checkBounds(arg0, arg1, batchStartsAt, batchSize);
      long finalSequence = this.sequencer.next(batchSize);
      this.translateAndPublishBatch(translator, arg0, arg1, batchStartsAt, batchSize, finalSequence);
   }

   @Override
   public <A, B> boolean tryPublishEvents(EventTranslatorTwoArg<E, A, B> translator, A[] arg0, B[] arg1) {
      return this.tryPublishEvents(translator, 0, arg0.length, arg0, arg1);
   }

   @Override
   public <A, B> boolean tryPublishEvents(EventTranslatorTwoArg<E, A, B> translator, int batchStartsAt, int batchSize, A[] arg0, B[] arg1) {
      this.checkBounds(arg0, arg1, batchStartsAt, batchSize);

      try {
         long finalSequence = this.sequencer.tryNext(batchSize);
         this.translateAndPublishBatch(translator, arg0, arg1, batchStartsAt, batchSize, finalSequence);
         return true;
      } catch (InsufficientCapacityException var8) {
         return false;
      }
   }

   @Override
   public <A, B, C> void publishEvents(EventTranslatorThreeArg<E, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2) {
      this.publishEvents(translator, 0, arg0.length, arg0, arg1, arg2);
   }

   @Override
   public <A, B, C> void publishEvents(EventTranslatorThreeArg<E, A, B, C> translator, int batchStartsAt, int batchSize, A[] arg0, B[] arg1, C[] arg2) {
      this.checkBounds(arg0, arg1, arg2, batchStartsAt, batchSize);
      long finalSequence = this.sequencer.next(batchSize);
      this.translateAndPublishBatch(translator, arg0, arg1, arg2, batchStartsAt, batchSize, finalSequence);
   }

   @Override
   public <A, B, C> boolean tryPublishEvents(EventTranslatorThreeArg<E, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2) {
      return this.tryPublishEvents(translator, 0, arg0.length, arg0, arg1, arg2);
   }

   @Override
   public <A, B, C> boolean tryPublishEvents(EventTranslatorThreeArg<E, A, B, C> translator, int batchStartsAt, int batchSize, A[] arg0, B[] arg1, C[] arg2) {
      this.checkBounds(arg0, arg1, arg2, batchStartsAt, batchSize);

      try {
         long finalSequence = this.sequencer.tryNext(batchSize);
         this.translateAndPublishBatch(translator, arg0, arg1, arg2, batchStartsAt, batchSize, finalSequence);
         return true;
      } catch (InsufficientCapacityException var9) {
         return false;
      }
   }

   @Override
   public void publishEvents(EventTranslatorVararg<E> translator, Object[]... args) {
      this.publishEvents(translator, 0, args.length, args);
   }

   @Override
   public void publishEvents(EventTranslatorVararg<E> translator, int batchStartsAt, int batchSize, Object[]... args) {
      this.checkBounds(batchStartsAt, batchSize, args);
      long finalSequence = this.sequencer.next(batchSize);
      this.translateAndPublishBatch(translator, batchStartsAt, batchSize, finalSequence, args);
   }

   @Override
   public boolean tryPublishEvents(EventTranslatorVararg<E> translator, Object[]... args) {
      return this.tryPublishEvents(translator, 0, args.length, args);
   }

   @Override
   public boolean tryPublishEvents(EventTranslatorVararg<E> translator, int batchStartsAt, int batchSize, Object[]... args) {
      this.checkBounds(args, batchStartsAt, batchSize);

      try {
         long finalSequence = this.sequencer.tryNext(batchSize);
         this.translateAndPublishBatch(translator, batchStartsAt, batchSize, finalSequence, args);
         return true;
      } catch (InsufficientCapacityException var7) {
         return false;
      }
   }

   @Override
   public void publish(long sequence) {
      this.sequencer.publish(sequence);
   }

   @Override
   public void publish(long lo, long hi) {
      this.sequencer.publish(lo, hi);
   }

   @Override
   public long remainingCapacity() {
      return this.sequencer.remainingCapacity();
   }

   private void checkBounds(EventTranslator<E>[] translators, int batchStartsAt, int batchSize) {
      this.checkBatchSizing(batchStartsAt, batchSize);
      this.batchOverRuns(translators, batchStartsAt, batchSize);
   }

   private void checkBatchSizing(int batchStartsAt, int batchSize) {
      if (batchStartsAt < 0 || batchSize < 0) {
         throw new IllegalArgumentException(
            "Both batchStartsAt and batchSize must be positive but got: batchStartsAt " + batchStartsAt + " and batchSize " + batchSize
         );
      } else if (batchSize > this.bufferSize) {
         throw new IllegalArgumentException("The ring buffer cannot accommodate " + batchSize + " it only has space for " + this.bufferSize + " entities.");
      }
   }

   private <A> void checkBounds(A[] arg0, int batchStartsAt, int batchSize) {
      this.checkBatchSizing(batchStartsAt, batchSize);
      this.batchOverRuns(arg0, batchStartsAt, batchSize);
   }

   private <A, B> void checkBounds(A[] arg0, B[] arg1, int batchStartsAt, int batchSize) {
      this.checkBatchSizing(batchStartsAt, batchSize);
      this.batchOverRuns(arg0, batchStartsAt, batchSize);
      this.batchOverRuns(arg1, batchStartsAt, batchSize);
   }

   private <A, B, C> void checkBounds(A[] arg0, B[] arg1, C[] arg2, int batchStartsAt, int batchSize) {
      this.checkBatchSizing(batchStartsAt, batchSize);
      this.batchOverRuns(arg0, batchStartsAt, batchSize);
      this.batchOverRuns(arg1, batchStartsAt, batchSize);
      this.batchOverRuns(arg2, batchStartsAt, batchSize);
   }

   private void checkBounds(int batchStartsAt, int batchSize, Object[][] args) {
      this.checkBatchSizing(batchStartsAt, batchSize);
      this.batchOverRuns(args, batchStartsAt, batchSize);
   }

   private <A> void batchOverRuns(A[] arg0, int batchStartsAt, int batchSize) {
      if (batchStartsAt + batchSize > arg0.length) {
         throw new IllegalArgumentException(
            "A batchSize of: "
               + batchSize
               + " with batchStatsAt of: "
               + batchStartsAt
               + " will overrun the available number of arguments: "
               + (arg0.length - batchStartsAt)
         );
      }
   }

   private void translateAndPublish(EventTranslator<E> translator, long sequence) {
      try {
         translator.translateTo(this.get(sequence), sequence);
      } finally {
         this.sequencer.publish(sequence);
      }
   }

   private <A> void translateAndPublish(EventTranslatorOneArg<E, A> translator, long sequence, A arg0) {
      try {
         translator.translateTo(this.get(sequence), sequence, arg0);
      } finally {
         this.sequencer.publish(sequence);
      }
   }

   private <A, B> void translateAndPublish(EventTranslatorTwoArg<E, A, B> translator, long sequence, A arg0, B arg1) {
      try {
         translator.translateTo(this.get(sequence), sequence, arg0, arg1);
      } finally {
         this.sequencer.publish(sequence);
      }
   }

   private <A, B, C> void translateAndPublish(EventTranslatorThreeArg<E, A, B, C> translator, long sequence, A arg0, B arg1, C arg2) {
      try {
         translator.translateTo(this.get(sequence), sequence, arg0, arg1, arg2);
      } finally {
         this.sequencer.publish(sequence);
      }
   }

   private void translateAndPublish(EventTranslatorVararg<E> translator, long sequence, Object... args) {
      try {
         translator.translateTo(this.get(sequence), sequence, args);
      } finally {
         this.sequencer.publish(sequence);
      }
   }

   private void translateAndPublishBatch(EventTranslator<E>[] translators, int batchStartsAt, int batchSize, long finalSequence) {
      long initialSequence = finalSequence - (long)(batchSize - 1);

      try {
         long sequence = initialSequence;
         int batchEndsAt = batchStartsAt + batchSize;

         for (int i = batchStartsAt; i < batchEndsAt; i++) {
            EventTranslator<E> translator = translators[i];
            translator.translateTo(this.get(sequence), sequence++);
         }
      } finally {
         this.sequencer.publish(initialSequence, finalSequence);
      }
   }

   private <A> void translateAndPublishBatch(EventTranslatorOneArg<E, A> translator, A[] arg0, int batchStartsAt, int batchSize, long finalSequence) {
      long initialSequence = finalSequence - (long)(batchSize - 1);

      try {
         long sequence = initialSequence;
         int batchEndsAt = batchStartsAt + batchSize;

         for (int i = batchStartsAt; i < batchEndsAt; i++) {
            translator.translateTo(this.get(sequence), sequence++, arg0[i]);
         }
      } finally {
         this.sequencer.publish(initialSequence, finalSequence);
      }
   }

   private <A, B> void translateAndPublishBatch(
      EventTranslatorTwoArg<E, A, B> translator, A[] arg0, B[] arg1, int batchStartsAt, int batchSize, long finalSequence
   ) {
      long initialSequence = finalSequence - (long)(batchSize - 1);

      try {
         long sequence = initialSequence;
         int batchEndsAt = batchStartsAt + batchSize;

         for (int i = batchStartsAt; i < batchEndsAt; i++) {
            translator.translateTo(this.get(sequence), sequence++, arg0[i], arg1[i]);
         }
      } finally {
         this.sequencer.publish(initialSequence, finalSequence);
      }
   }

   private <A, B, C> void translateAndPublishBatch(
      EventTranslatorThreeArg<E, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2, int batchStartsAt, int batchSize, long finalSequence
   ) {
      long initialSequence = finalSequence - (long)(batchSize - 1);

      try {
         long sequence = initialSequence;
         int batchEndsAt = batchStartsAt + batchSize;

         for (int i = batchStartsAt; i < batchEndsAt; i++) {
            translator.translateTo(this.get(sequence), sequence++, arg0[i], arg1[i], arg2[i]);
         }
      } finally {
         this.sequencer.publish(initialSequence, finalSequence);
      }
   }

   private void translateAndPublishBatch(EventTranslatorVararg<E> translator, int batchStartsAt, int batchSize, long finalSequence, Object[][] args) {
      long initialSequence = finalSequence - (long)(batchSize - 1);

      try {
         long sequence = initialSequence;
         int batchEndsAt = batchStartsAt + batchSize;

         for (int i = batchStartsAt; i < batchEndsAt; i++) {
            translator.translateTo(this.get(sequence), sequence++, args[i]);
         }
      } finally {
         this.sequencer.publish(initialSequence, finalSequence);
      }
   }

   @Override
   public String toString() {
      return "RingBuffer{bufferSize=" + this.bufferSize + ", sequencer=" + this.sequencer + "}";
   }
}
