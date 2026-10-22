package org.glassfish.jersey.internal.guava;

import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

final class ExecutionList {
   private static final Logger log = Logger.getLogger(ExecutionList.class.getName());
   private ExecutionList.RunnableExecutorPair runnables;
   private boolean executed;

   public ExecutionList() {
   }

   private static void executeListener(Runnable runnable, Executor executor) {
      try {
         executor.execute(runnable);
      } catch (RuntimeException var3) {
         log.log(Level.SEVERE, "RuntimeException while executing runnable " + runnable + " with executor " + executor, (Throwable)var3);
      }
   }

   public void add(Runnable runnable, Executor executor) {
      Preconditions.checkNotNull(runnable, "Runnable was null.");
      Preconditions.checkNotNull(executor, "Executor was null.");
      synchronized (this) {
         if (!this.executed) {
            this.runnables = new ExecutionList.RunnableExecutorPair(runnable, executor, this.runnables);
            return;
         }
      }

      executeListener(runnable, executor);
   }

   public void execute() {
      ExecutionList.RunnableExecutorPair list;
      synchronized (this) {
         if (this.executed) {
            return;
         }

         this.executed = true;
         list = this.runnables;
         this.runnables = null;
      }

      ExecutionList.RunnableExecutorPair reversedList = null;

      while (list != null) {
         ExecutionList.RunnableExecutorPair tmp = list;
         list = list.next;
         tmp.next = reversedList;
         reversedList = tmp;
      }

      while (reversedList != null) {
         executeListener(reversedList.runnable, reversedList.executor);
         reversedList = reversedList.next;
      }
   }

   private static final class RunnableExecutorPair {
      final Runnable runnable;
      final Executor executor;
      ExecutionList.RunnableExecutorPair next;

      RunnableExecutorPair(Runnable runnable, Executor executor, ExecutionList.RunnableExecutorPair next) {
         this.runnable = runnable;
         this.executor = executor;
         this.next = next;
      }
   }
}
