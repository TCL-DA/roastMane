package org.eclipse.jetty.util.thread;

import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializedInvoker {
   private static final Logger LOG = LoggerFactory.getLogger(SerializedInvoker.class);
   private final AtomicReference<SerializedInvoker.Link> _tail = new AtomicReference<>();

   public Runnable offer(Runnable task) {
      if (task == null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Offering task null, skipping it in {}", this);
         }

         return null;
      } else {
         if (SerializedInvoker.NamedRunnable.LOG.isDebugEnabled() && !(task instanceof SerializedInvoker.NamedRunnable)) {
            task = new SerializedInvoker.NamedRunnable(task, this.deriveTaskName(task));
         }

         SerializedInvoker.Link link = new SerializedInvoker.Link(task);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Offering link {} of {}", link, this);
         }

         SerializedInvoker.Link penultimate = this._tail.getAndSet(link);
         if (penultimate == null) {
            return link;
         } else {
            penultimate._next.lazySet(link);
            return null;
         }
      }
   }

   protected String deriveTaskName(Runnable task) {
      StackTraceElement[] stackTrace = new Exception().getStackTrace();

      for (StackTraceElement stackTraceElement : stackTrace) {
         String className = stackTraceElement.getClassName();
         if (!className.equals(SerializedInvoker.class.getName()) && !className.equals(this.getClass().getName())) {
            return "Queued at " + stackTraceElement;
         }
      }

      return task.toString();
   }

   public Runnable offer(Runnable... tasks) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Offering {} tasks in {}", tasks.length, this);
      }

      Runnable runnable = null;

      for (Runnable task : tasks) {
         if (runnable == null) {
            runnable = this.offer(task);
         } else {
            this.offer(task);
         }
      }

      return runnable;
   }

   public void run(Runnable task) {
      Runnable todo = this.offer(task);
      if (todo != null) {
         todo.run();
      } else if (LOG.isDebugEnabled()) {
         LOG.debug("Queued link in {}", this);
      }
   }

   public void run(Runnable... tasks) {
      Runnable todo = this.offer(tasks);
      if (todo != null) {
         todo.run();
      } else if (LOG.isDebugEnabled()) {
         LOG.debug("Queued links in {}", this);
      }
   }

   @Override
   public String toString() {
      return String.format("%s@%x{tail=%s}", this.getClass().getSimpleName(), this.hashCode(), this._tail);
   }

   protected void onError(Runnable task, Throwable t) {
      LOG.warn("Serialized invocation error", t);
   }

   private class Link implements Runnable, Invocable {
      private final Runnable _task;
      private final AtomicReference<SerializedInvoker.Link> _next = new AtomicReference<>();

      public Link(Runnable task) {
         this._task = task;
      }

      @Override
      public Invocable.InvocationType getInvocationType() {
         return Invocable.InvocationType.BLOCKING;
      }

      SerializedInvoker.Link next() {
         if (SerializedInvoker.this._tail.compareAndSet(this, null)) {
            return null;
         } else {
            while (true) {
               SerializedInvoker.Link next = this._next.get();
               if (next != null) {
                  return next;
               }

               Thread.onSpinWait();
            }
         }
      }

      @Override
      public void run() {
         SerializedInvoker.Link link = this;

         while (link != null) {
            if (SerializedInvoker.LOG.isDebugEnabled()) {
               SerializedInvoker.LOG.debug("Running link {} of {}", link, SerializedInvoker.this);
            }

            try {
               link._task.run();
            } catch (Throwable var3) {
               if (SerializedInvoker.LOG.isDebugEnabled()) {
                  SerializedInvoker.LOG.debug("Failed while running link {} of {}", new Object[]{link, SerializedInvoker.this, var3});
               }

               SerializedInvoker.this.onError(link._task, var3);
            }

            link = link.next();
            if (link == null && SerializedInvoker.LOG.isDebugEnabled()) {
               SerializedInvoker.LOG.debug("Next link is null, execution is over in {}", SerializedInvoker.this);
            }
         }
      }

      @Override
      public String toString() {
         return String.format("%s@%x{%s -> %s}", this.getClass().getSimpleName(), this.hashCode(), this._task, this._next);
      }
   }

   private static record NamedRunnable(Runnable delegate, String name) implements Runnable {
      private static final Logger LOG = LoggerFactory.getLogger(SerializedInvoker.NamedRunnable.class);

      @Override
      public void run() {
         this.delegate.run();
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
