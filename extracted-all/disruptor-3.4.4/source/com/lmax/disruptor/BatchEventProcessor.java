package com.lmax.disruptor;

import java.util.concurrent.atomic.AtomicInteger;

public final class BatchEventProcessor<T> implements EventProcessor {
   private static final int IDLE = 0;
   private static final int HALTED = 1;
   private static final int RUNNING = 2;
   private final AtomicInteger running = new AtomicInteger(0);
   private ExceptionHandler<? super T> exceptionHandler;
   private final DataProvider<T> dataProvider;
   private final SequenceBarrier sequenceBarrier;
   private final EventHandler<? super T> eventHandler;
   private final Sequence sequence = new Sequence(-1L);
   private final TimeoutHandler timeoutHandler;
   private final BatchStartAware batchStartAware;

   public BatchEventProcessor(DataProvider<T> dataProvider, SequenceBarrier sequenceBarrier, EventHandler<? super T> eventHandler) {
      this.dataProvider = dataProvider;
      this.sequenceBarrier = sequenceBarrier;
      this.eventHandler = eventHandler;
      if (eventHandler instanceof SequenceReportingEventHandler) {
         ((SequenceReportingEventHandler)eventHandler).setSequenceCallback(this.sequence);
      }

      this.batchStartAware = eventHandler instanceof BatchStartAware ? (BatchStartAware)eventHandler : null;
      this.timeoutHandler = eventHandler instanceof TimeoutHandler ? (TimeoutHandler)eventHandler : null;
   }

   @Override
   public Sequence getSequence() {
      return this.sequence;
   }

   @Override
   public void halt() {
      this.running.set(1);
      this.sequenceBarrier.alert();
   }

   @Override
   public boolean isRunning() {
      return this.running.get() != 0;
   }

   public void setExceptionHandler(ExceptionHandler<? super T> exceptionHandler) {
      if (null == exceptionHandler) {
         throw new NullPointerException();
      } else {
         this.exceptionHandler = exceptionHandler;
      }
   }

   @Override
   public void run() {
      if (this.running.compareAndSet(0, 2)) {
         this.sequenceBarrier.clearAlert();
         this.notifyStart();

         try {
            if (this.running.get() == 2) {
               this.processEvents();
            }
         } finally {
            this.notifyShutdown();
            this.running.set(0);
         }
      } else {
         if (this.running.get() == 2) {
            throw new IllegalStateException("Thread is already running");
         }

         this.earlyExit();
      }
   }

   private void processEvents() {
      T event = null;
      long nextSequence = this.sequence.get() + 1L;

      while (true) {
         try {
            long availableSequence = this.sequenceBarrier.waitFor(nextSequence);
            if (this.batchStartAware != null) {
               this.batchStartAware.onBatchStart(availableSequence - nextSequence + 1L);
            }

            while (nextSequence <= availableSequence) {
               event = this.dataProvider.get(nextSequence);
               this.eventHandler.onEvent(event, nextSequence, nextSequence == availableSequence);
               nextSequence++;
            }

            this.sequence.set(availableSequence);
         } catch (TimeoutException var6) {
            this.notifyTimeout(this.sequence.get());
         } catch (AlertException var7) {
            if (this.running.get() != 2) {
               return;
            }
         } catch (Throwable var8) {
            this.handleEventException(var8, nextSequence, event);
            this.sequence.set(nextSequence);
            nextSequence++;
         }
      }
   }

   private void earlyExit() {
      this.notifyStart();
      this.notifyShutdown();
   }

   private void notifyTimeout(long availableSequence) {
      try {
         if (this.timeoutHandler != null) {
            this.timeoutHandler.onTimeout(availableSequence);
         }
      } catch (Throwable var4) {
         this.handleEventException(var4, availableSequence, null);
      }
   }

   private void notifyStart() {
      if (this.eventHandler instanceof LifecycleAware) {
         try {
            ((LifecycleAware)this.eventHandler).onStart();
         } catch (Throwable var2) {
            this.handleOnStartException(var2);
         }
      }
   }

   private void notifyShutdown() {
      if (this.eventHandler instanceof LifecycleAware) {
         try {
            ((LifecycleAware)this.eventHandler).onShutdown();
         } catch (Throwable var2) {
            this.handleOnShutdownException(var2);
         }
      }
   }

   private void handleEventException(Throwable ex, long sequence, T event) {
      this.getExceptionHandler().handleEventException(ex, sequence, event);
   }

   private void handleOnStartException(Throwable ex) {
      this.getExceptionHandler().handleOnStartException(ex);
   }

   private void handleOnShutdownException(Throwable ex) {
      this.getExceptionHandler().handleOnShutdownException(ex);
   }

   private ExceptionHandler<? super T> getExceptionHandler() {
      ExceptionHandler<? super T> handler = this.exceptionHandler;
      return handler == null ? ExceptionHandlers.defaultHandler() : handler;
   }
}
