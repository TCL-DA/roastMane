package org.eclipse.jetty.util.thread.strategy;

import java.util.concurrent.Executor;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.ExecutionStrategy;
import org.eclipse.jetty.util.thread.Invocable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteProduceConsume implements ExecutionStrategy, Runnable {
   private static final Logger LOG = LoggerFactory.getLogger(ExecuteProduceConsume.class);
   private final AutoLock _lock = new AutoLock();
   private final Runnable _runProduce = new ExecuteProduceConsume.RunProduce();
   private final ExecutionStrategy.Producer _producer;
   private final Executor _executor;
   private boolean _idle = true;
   private boolean _execute;
   private boolean _producing;
   private boolean _pending;

   public ExecuteProduceConsume(ExecutionStrategy.Producer producer, Executor executor) {
      this._producer = producer;
      this._executor = executor;
   }

   @Override
   public void produce() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} execute", this);
      }

      boolean produce = false;

      try (AutoLock lock = this._lock.lock()) {
         if (this._idle) {
            if (this._producing) {
               throw new IllegalStateException();
            }

            produce = this._producing = true;
            this._idle = false;
         } else {
            this._execute = true;
         }
      }

      if (produce) {
         this.produceConsume();
      }
   }

   @Override
   public void dispatch() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} spawning", this);
      }

      boolean dispatch = false;

      try (AutoLock lock = this._lock.lock()) {
         if (this._idle) {
            dispatch = true;
         } else {
            this._execute = true;
         }
      }

      if (dispatch) {
         this._executor.execute(this._runProduce);
      }
   }

   @Override
   public void run() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} run", this);
      }

      boolean produce = false;

      try (AutoLock lock = this._lock.lock()) {
         this._pending = false;
         if (!this._idle && !this._producing) {
            produce = this._producing = true;
         }
      }

      if (produce) {
         this.produceConsume();
      }
   }

   private void produceConsume() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} produce enter", this);
      }

      while (true) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} producing", this);
         }

         Runnable task = this._producer.produce();
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} produced {}", this, task);
         }

         boolean dispatch = false;

         try (AutoLock lock = this._lock.lock()) {
            this._producing = false;
            if (task == null) {
               if (this._execute) {
                  this._idle = false;
                  this._producing = true;
                  this._execute = false;
                  continue;
               }

               this._idle = true;
               break;
            }

            if (!this._pending) {
               dispatch = this._pending = Invocable.getInvocationType(task) != Invocable.InvocationType.NON_BLOCKING;
            }

            this._execute = false;
         }

         if (dispatch) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("{} dispatch", this);
            }

            this._executor.execute(this);
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("{} run {}", this, task);
         }

         task.run();
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} ran {}", this, task);
         }

         try (AutoLock lock = this._lock.lock()) {
            if (this._producing || this._idle) {
               break;
            }

            this._producing = true;
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("{} produce exit", this);
      }
   }

   public Boolean isIdle() {
      Boolean var2;
      try (AutoLock lock = this._lock.lock()) {
         var2 = this._idle;
      }

      return var2;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("EPC ");

      try (AutoLock lock = this._lock.lock()) {
         builder.append(this._idle ? "Idle/" : "");
         builder.append(this._producing ? "Prod/" : "");
         builder.append(this._pending ? "Pend/" : "");
         builder.append(this._execute ? "Exec/" : "");
      }

      builder.append(this._producer);
      return builder.toString();
   }

   private class RunProduce implements Runnable {
      @Override
      public void run() {
         ExecuteProduceConsume.this.produce();
      }
   }
}
