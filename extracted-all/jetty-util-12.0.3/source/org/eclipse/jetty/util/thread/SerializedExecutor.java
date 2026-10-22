package org.eclipse.jetty.util.thread;

import java.util.concurrent.Executor;
import java.util.function.Consumer;
import org.slf4j.LoggerFactory;

public class SerializedExecutor implements Executor {
   private final SerializedInvoker _invoker = new SerializedInvoker() {
      @Override
      protected void onError(Runnable task, Throwable t) {
         SerializedExecutor.this.onError(task, t);
      }
   };
   private final Executor _executor;

   public SerializedExecutor() {
      this(Runnable::run);
   }

   public SerializedExecutor(Executor executor) {
      this._executor = executor;
   }

   protected void onError(Runnable task, Throwable t) {
      try {
         if (task instanceof SerializedExecutor.ErrorHandlingTask) {
            ((SerializedExecutor.ErrorHandlingTask)task).accept(t);
         }
      } catch (Throwable var4) {
         if (var4 != t) {
            t.addSuppressed(var4);
         }
      }

      LoggerFactory.getLogger(task.getClass()).error("Error", t);
   }

   @Override
   public void execute(Runnable task) {
      Runnable todo = this._invoker.offer(task);
      if (todo != null) {
         this._executor.execute(todo);
      }
   }

   public void execute(Runnable... tasks) {
      Runnable todo = this._invoker.offer(tasks);
      if (todo != null) {
         this._executor.execute(todo);
      }
   }

   public interface ErrorHandlingTask extends Runnable, Consumer<Throwable> {
   }
}
