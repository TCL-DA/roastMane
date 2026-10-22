package nonapi.io.github.classgraph.concurrency;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class InterruptionChecker {
   private final AtomicBoolean interrupted = new AtomicBoolean(false);
   private final AtomicReference<ExecutionException> thrownExecutionException = new AtomicReference<>();

   public void interrupt() {
      this.interrupted.set(true);
      Thread.currentThread().interrupt();
   }

   public void setExecutionException(ExecutionException executionException) {
      if (executionException != null && this.thrownExecutionException.get() == null) {
         this.thrownExecutionException.compareAndSet(null, executionException);
      }
   }

   public ExecutionException getExecutionException() {
      return this.thrownExecutionException.get();
   }

   public static Throwable getCause(Throwable throwable) {
      Throwable cause = throwable;

      while (cause instanceof ExecutionException) {
         cause = cause.getCause();
      }

      return (Throwable)(cause != null ? cause : new ExecutionException("ExecutionException with unknown cause", null));
   }

   public boolean checkAndReturn() {
      if (this.interrupted.get()) {
         this.interrupt();
         return true;
      } else if (Thread.currentThread().isInterrupted()) {
         this.interrupted.set(true);
         return true;
      } else {
         return false;
      }
   }

   public void check() throws InterruptedException, ExecutionException {
      ExecutionException executionException = this.getExecutionException();
      if (executionException != null) {
         throw executionException;
      } else if (this.checkAndReturn()) {
         throw new InterruptedException();
      }
   }
}
