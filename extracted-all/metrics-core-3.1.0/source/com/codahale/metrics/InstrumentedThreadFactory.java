package com.codahale.metrics;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class InstrumentedThreadFactory implements ThreadFactory {
   private static final AtomicLong nameCounter = new AtomicLong();
   private final ThreadFactory delegate;
   private final Meter created;
   private final Counter running;
   private final Meter terminated;

   public InstrumentedThreadFactory(ThreadFactory delegate, MetricRegistry registry) {
      this(delegate, registry, "instrumented-thread-delegate-" + nameCounter.incrementAndGet());
   }

   public InstrumentedThreadFactory(ThreadFactory delegate, MetricRegistry registry, String name) {
      this.delegate = delegate;
      this.created = registry.meter(MetricRegistry.name(name, "created"));
      this.running = registry.counter(MetricRegistry.name(name, "running"));
      this.terminated = registry.meter(MetricRegistry.name(name, "terminated"));
   }

   @Override
   public Thread newThread(Runnable runnable) {
      Runnable wrappedRunnable = new InstrumentedThreadFactory.InstrumentedRunnable(runnable);
      Thread thread = this.delegate.newThread(wrappedRunnable);
      this.created.mark();
      return thread;
   }

   private class InstrumentedRunnable implements Runnable {
      private final Runnable task;

      InstrumentedRunnable(Runnable task) {
         this.task = task;
      }

      @Override
      public void run() {
         InstrumentedThreadFactory.this.running.inc();

         try {
            this.task.run();
         } finally {
            InstrumentedThreadFactory.this.running.dec();
            InstrumentedThreadFactory.this.terminated.mark();
         }
      }
   }
}
