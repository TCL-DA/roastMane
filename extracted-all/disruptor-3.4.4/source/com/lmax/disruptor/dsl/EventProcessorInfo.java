package com.lmax.disruptor.dsl;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import java.util.concurrent.Executor;

class EventProcessorInfo<T> implements ConsumerInfo {
   private final EventProcessor eventprocessor;
   private final EventHandler<? super T> handler;
   private final SequenceBarrier barrier;
   private boolean endOfChain = true;

   EventProcessorInfo(EventProcessor eventprocessor, EventHandler<? super T> handler, SequenceBarrier barrier) {
      this.eventprocessor = eventprocessor;
      this.handler = handler;
      this.barrier = barrier;
   }

   public EventProcessor getEventProcessor() {
      return this.eventprocessor;
   }

   @Override
   public Sequence[] getSequences() {
      return new Sequence[]{this.eventprocessor.getSequence()};
   }

   public EventHandler<? super T> getHandler() {
      return this.handler;
   }

   @Override
   public SequenceBarrier getBarrier() {
      return this.barrier;
   }

   @Override
   public boolean isEndOfChain() {
      return this.endOfChain;
   }

   @Override
   public void start(Executor executor) {
      executor.execute(this.eventprocessor);
   }

   @Override
   public void halt() {
      this.eventprocessor.halt();
   }

   @Override
   public void markAsUsedInBarrier() {
      this.endOfChain = false;
   }

   @Override
   public boolean isRunning() {
      return this.eventprocessor.isRunning();
   }
}
