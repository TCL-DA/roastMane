package org.eclipse.jetty.util.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Graceful {
   CompletableFuture<Void> shutdown();

   boolean isShutdown();

   static CompletableFuture<Void> shutdown(Container component) {
      Logger log = LoggerFactory.getLogger(component.getClass());
      log.info("Shutdown {}", component);
      List<Graceful> gracefuls = new ArrayList<>();
      if (component instanceof Graceful) {
         gracefuls.add((Graceful)component);
      }

      gracefuls.addAll(component.getContainedBeans(Graceful.class));
      if (log.isDebugEnabled()) {
         gracefuls.forEach(g -> log.debug("Graceful {}", g));
      }

      Collections.reverse(gracefuls);
      return CompletableFuture.allOf(gracefuls.stream().map(Graceful::shutdown).toArray(CompletableFuture[]::new));
   }

   static CompletableFuture<Void> shutdown(Graceful.ThrowingRunnable runnable) {
      final AtomicReference<Thread> stopThreadReference = new AtomicReference<>();
      CompletableFuture<Void> shutdown = new CompletableFuture<Void>() {
         @Override
         public boolean cancel(boolean mayInterruptIfRunning) {
            boolean canceled = super.cancel(mayInterruptIfRunning);
            if (canceled && mayInterruptIfRunning) {
               Thread thread = stopThreadReference.get();
               if (thread != null) {
                  thread.interrupt();
               }
            }

            return canceled;
         }
      };
      Thread stopThread = new Thread(() -> {
         try {
            runnable.run();
            shutdown.complete(null);
         } catch (Throwable var3x) {
            shutdown.completeExceptionally(var3x);
         }
      });
      stopThread.setDaemon(true);
      stopThreadReference.set(stopThread);
      stopThread.start();
      return shutdown;
   }

   public abstract static class Shutdown implements Graceful {
      final Object _component;
      final AtomicReference<CompletableFuture<Void>> _done = new AtomicReference<>();

      protected Shutdown(Object component) {
         this._component = component;
      }

      @Override
      public CompletableFuture<Void> shutdown() {
         if (this._done.get() == null) {
            this._done.compareAndSet(null, new CompletableFuture<Void>() {
               @Override
               public String toString() {
                  return String.format("Shutdown<%s>@%x", Shutdown.this._component, this.hashCode());
               }
            });
         }

         CompletableFuture<Void> done = this._done.get();
         this.check();
         return done;
      }

      @Override
      public boolean isShutdown() {
         return this._done.get() != null;
      }

      public void check() {
         CompletableFuture<Void> done = this._done.get();
         if (done != null && this.isShutdownDone()) {
            done.complete(null);
         }
      }

      public void cancel() {
         CompletableFuture<Void> done = this._done.get();
         if (done != null && !done.isDone()) {
            done.cancel(true);
         }

         this._done.set(null);
      }

      public abstract boolean isShutdownDone();
   }

   @FunctionalInterface
   public interface ThrowingRunnable {
      void run() throws Exception;
   }
}
