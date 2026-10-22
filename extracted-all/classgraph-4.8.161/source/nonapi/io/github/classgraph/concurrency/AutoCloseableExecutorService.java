package nonapi.io.github.classgraph.concurrency;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AutoCloseableExecutorService extends ThreadPoolExecutor implements AutoCloseable {
   public final InterruptionChecker interruptionChecker = new InterruptionChecker();

   public AutoCloseableExecutorService(int numThreads) {
      super(numThreads, numThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new SimpleThreadFactory("ClassGraph-worker-", true));
   }

   @Override
   public void afterExecute(Runnable runnable, Throwable throwable) {
      super.afterExecute(runnable, throwable);
      if (throwable != null) {
         this.interruptionChecker.setExecutionException(new ExecutionException("Uncaught exception", throwable));
         this.interruptionChecker.interrupt();
      } else if (runnable instanceof Future) {
         try {
            ((Future)runnable).get();
         } catch (InterruptedException | CancellationException var4) {
            this.interruptionChecker.interrupt();
         } catch (ExecutionException var5) {
            this.interruptionChecker.setExecutionException(var5);
            this.interruptionChecker.interrupt();
         }
      }
   }

   @Override
   public void close() {
      try {
         this.shutdown();
      } catch (SecurityException var5) {
      }

      boolean terminated = false;

      try {
         terminated = this.awaitTermination(2500L, TimeUnit.MILLISECONDS);
      } catch (InterruptedException var4) {
         this.interruptionChecker.interrupt();
      }

      if (!terminated) {
         try {
            this.shutdownNow();
         } catch (SecurityException var3) {
            throw new RuntimeException(
               "Could not shut down ExecutorService -- need java.lang.RuntimePermission(\"modifyThread\"), or the security manager's checkAccess method denies access",
               var3
            );
         }
      }
   }
}
