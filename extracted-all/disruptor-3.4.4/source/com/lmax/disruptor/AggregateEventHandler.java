package com.lmax.disruptor;

public final class AggregateEventHandler<T> implements EventHandler<T>, LifecycleAware {
   private final EventHandler<T>[] eventHandlers;

   @SafeVarargs
   public AggregateEventHandler(EventHandler<T>... eventHandlers) {
      this.eventHandlers = eventHandlers;
   }

   @Override
   public void onEvent(T event, long sequence, boolean endOfBatch) throws Exception {
      for (EventHandler<T> eventHandler : this.eventHandlers) {
         eventHandler.onEvent(event, sequence, endOfBatch);
      }
   }

   @Override
   public void onStart() {
      for (EventHandler<T> eventHandler : this.eventHandlers) {
         if (eventHandler instanceof LifecycleAware) {
            ((LifecycleAware)eventHandler).onStart();
         }
      }
   }

   @Override
   public void onShutdown() {
      for (EventHandler<T> eventHandler : this.eventHandlers) {
         if (eventHandler instanceof LifecycleAware) {
            ((LifecycleAware)eventHandler).onShutdown();
         }
      }
   }
}
