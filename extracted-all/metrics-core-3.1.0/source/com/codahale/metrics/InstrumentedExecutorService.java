package com.codahale.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

public class InstrumentedExecutorService implements ExecutorService {
   private static final AtomicLong nameCounter = new AtomicLong();
   private final ExecutorService delegate;
   private final Meter submitted;
   private final Counter running;
   private final Meter completed;
   private final Timer duration;

   public InstrumentedExecutorService(ExecutorService delegate, MetricRegistry registry) {
      this(delegate, registry, "instrumented-delegate-" + nameCounter.incrementAndGet());
   }

   public InstrumentedExecutorService(ExecutorService delegate, MetricRegistry registry, String name) {
      this.delegate = delegate;
      this.submitted = registry.meter(MetricRegistry.name(name, "submitted"));
      this.running = registry.counter(MetricRegistry.name(name, "running"));
      this.completed = registry.meter(MetricRegistry.name(name, "completed"));
      this.duration = registry.timer(MetricRegistry.name(name, "duration"));
   }

   @Override
   public void execute(Runnable runnable) {
      this.submitted.mark();
      this.delegate.execute(new InstrumentedExecutorService.InstrumentedRunnable(runnable));
   }

   @Override
   public Future<?> submit(Runnable runnable) {
      this.submitted.mark();
      return this.delegate.submit(new InstrumentedExecutorService.InstrumentedRunnable(runnable));
   }

   @Override
   public <T> Future<T> submit(Runnable runnable, T result) {
      this.submitted.mark();
      return this.delegate.submit(new InstrumentedExecutorService.InstrumentedRunnable(runnable), result);
   }

   @Override
   public <T> Future<T> submit(Callable<T> task) {
      this.submitted.mark();
      return this.delegate.submit(new InstrumentedExecutorService.InstrumentedCallable<>(task));
   }

   @Override
   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
      this.submitted.mark((long)tasks.size());
      Collection<? extends Callable<T>> instrumented = this.instrument(tasks);
      return this.delegate.invokeAll(instrumented);
   }

   @Override
   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
      this.submitted.mark((long)tasks.size());
      Collection<? extends Callable<T>> instrumented = this.instrument(tasks);
      return this.delegate.invokeAll(instrumented, timeout, unit);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws ExecutionException, InterruptedException {
      this.submitted.mark((long)tasks.size());
      Collection<? extends Callable<T>> instrumented = this.instrument(tasks);
      return this.delegate.invokeAny(instrumented);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
      this.submitted.mark((long)tasks.size());
      Collection<? extends Callable<T>> instrumented = this.instrument(tasks);
      return this.delegate.invokeAny(instrumented, timeout, unit);
   }

   private <T> Collection<? extends Callable<T>> instrument(Collection<? extends Callable<T>> tasks) {
      List<InstrumentedExecutorService.InstrumentedCallable<T>> instrumented = new ArrayList<>(tasks.size());

      for (Callable<T> task : tasks) {
         instrumented.add(new InstrumentedExecutorService.InstrumentedCallable<>(task));
      }

      return instrumented;
   }

   @Override
   public void shutdown() {
      this.delegate.shutdown();
   }

   @Override
   public List<Runnable> shutdownNow() {
      return this.delegate.shutdownNow();
   }

   @Override
   public boolean isShutdown() {
      return this.delegate.isShutdown();
   }

   @Override
   public boolean isTerminated() {
      return this.delegate.isTerminated();
   }

   @Override
   public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
      return this.delegate.awaitTermination(l, timeUnit);
   }

   private class InstrumentedCallable<T> implements Callable<T> {
      private final Callable<T> callable;

      InstrumentedCallable(Callable<T> callable) {
         this.callable = callable;
      }

      @Override
      public T call() throws Exception {
         InstrumentedExecutorService.this.running.inc();
         Timer.Context context = InstrumentedExecutorService.this.duration.time();

         Object var2;
         try {
            var2 = this.callable.call();
         } finally {
            context.stop();
            InstrumentedExecutorService.this.running.dec();
            InstrumentedExecutorService.this.completed.mark();
         }

         return (T)var2;
      }
   }

   private class InstrumentedRunnable implements Runnable {
      private final Runnable task;

      InstrumentedRunnable(Runnable task) {
         this.task = task;
      }

      @Override
      public void run() {
         InstrumentedExecutorService.this.running.inc();
         Timer.Context context = InstrumentedExecutorService.this.duration.time();

         try {
            this.task.run();
         } finally {
            context.stop();
            InstrumentedExecutorService.this.running.dec();
            InstrumentedExecutorService.this.completed.mark();
         }
      }
   }
}
