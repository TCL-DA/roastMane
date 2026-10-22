package org.glassfish.jersey.spi;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.internal.guava.ThreadFactoryBuilder;
import org.glassfish.jersey.internal.util.ExtendedLogger;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.process.JerseyProcessingUncaughtExceptionHandler;

public abstract class AbstractThreadPoolProvider<E extends ThreadPoolExecutor> implements AutoCloseable {
   private static final ExtendedLogger LOGGER = new ExtendedLogger(Logger.getLogger(AbstractThreadPoolProvider.class.getName()), Level.FINEST);
   public static final int DEFAULT_TERMINATION_TIMEOUT = 5000;
   private final String name;
   private final AtomicBoolean closed = new AtomicBoolean(false);
   private final LazyValue<E> lazyExecutorServiceProvider = Values.lazy(
      (Value<E>)(() -> this.createExecutor(this.getCorePoolSize(), this.createThreadFactory(), this.getRejectedExecutionHandler()))
   );

   protected AbstractThreadPoolProvider(String name) {
      this.name = name;
   }

   protected final E getExecutor() {
      if (this.isClosed()) {
         throw new IllegalStateException(LocalizationMessages.THREAD_POOL_EXECUTOR_PROVIDER_CLOSED());
      } else {
         return this.lazyExecutorServiceProvider.get();
      }
   }

   protected abstract E createExecutor(int var1, ThreadFactory var2, RejectedExecutionHandler var3);

   protected int getTerminationTimeout() {
      return 5000;
   }

   protected int getCorePoolSize() {
      return Runtime.getRuntime().availableProcessors();
   }

   protected RejectedExecutionHandler getRejectedExecutionHandler() {
      return (r, executor) -> {
      };
   }

   protected ThreadFactory getBackingThreadFactory() {
      return null;
   }

   private ThreadFactory createThreadFactory() {
      ThreadFactoryBuilder factoryBuilder = new ThreadFactoryBuilder()
         .setNameFormat(this.name + "-%d")
         .setUncaughtExceptionHandler(new JerseyProcessingUncaughtExceptionHandler());
      ThreadFactory backingThreadFactory = this.getBackingThreadFactory();
      if (backingThreadFactory != null) {
         factoryBuilder.setThreadFactory(backingThreadFactory);
      }

      return factoryBuilder.build();
   }

   public final boolean isClosed() {
      return this.closed.get();
   }

   protected void onClose() {
   }

   @Override
   public final void close() {
      if (this.closed.compareAndSet(false, true)) {
         try {
            this.onClose();
         } finally {
            if (this.lazyExecutorServiceProvider.isInitialized()) {
               AccessController.doPrivileged(
                  shutdownExecutor(this.name, this.lazyExecutorServiceProvider.get(), this.getTerminationTimeout(), TimeUnit.MILLISECONDS)
               );
            }
         }
      }
   }

   private static PrivilegedAction<?> shutdownExecutor(
      String executorName, ExecutorService executorService, int terminationTimeout, TimeUnit terminationTimeUnit
   ) {
      return () -> {
         if (!executorService.isShutdown()) {
            executorService.shutdown();
         }

         if (executorService.isTerminated()) {
            return null;
         } else {
            boolean terminated = false;
            boolean interrupted = false;

            try {
               terminated = executorService.awaitTermination((long)terminationTimeout, terminationTimeUnit);
            } catch (InterruptedException var13) {
               if (LOGGER.isDebugLoggable()) {
                  LOGGER.log(LOGGER.getDebugLevel(), "Interrupted while waiting for thread pool executor " + executorName + " to shutdown.", (Throwable)var13);
               }

               interrupted = true;
            }

            try {
               if (!terminated) {
                  List<Runnable> cancelledTasks = executorService.shutdownNow();

                  for (Runnable cancelledTask : cancelledTasks) {
                     if (cancelledTask instanceof Future) {
                        ((Future)cancelledTask).cancel(true);
                     }
                  }

                  if (LOGGER.isDebugLoggable()) {
                     LOGGER.debugLog("Thread pool executor {0} forced-shut down. List of cancelled tasks: {1}", executorName, cancelledTasks);
                  }
               }
            } finally {
               if (interrupted) {
                  Thread.currentThread().interrupt();
               }
            }

            return null;
         }
      };
   }
}
