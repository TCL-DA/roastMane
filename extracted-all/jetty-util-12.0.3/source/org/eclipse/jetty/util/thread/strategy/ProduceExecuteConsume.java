package org.eclipse.jetty.util.thread.strategy;

import java.util.concurrent.Executor;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.ExecutionStrategy;
import org.eclipse.jetty.util.thread.Invocable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProduceExecuteConsume implements ExecutionStrategy {
   private static final Logger LOG = LoggerFactory.getLogger(ProduceExecuteConsume.class);
   private final AutoLock _lock = new AutoLock();
   private final ExecutionStrategy.Producer _producer;
   private final Executor _executor;
   private ProduceExecuteConsume.State _state = ProduceExecuteConsume.State.IDLE;

   public ProduceExecuteConsume(ExecutionStrategy.Producer producer, Executor executor) {
      this._producer = producer;
      this._executor = executor;
   }

   @Override
   public void produce() {
      try (AutoLock lock = this._lock.lock()) {
         switch (this._state) {
            case IDLE:
               this._state = ProduceExecuteConsume.State.PRODUCE;
               break;
            case PRODUCE:
            case EXECUTE:
               this._state = ProduceExecuteConsume.State.EXECUTE;
               return;
            default:
               throw new IllegalStateException(this._state.toString());
         }
      }

      while (true) {
         Runnable task = this._producer.produce();
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} produced {}", this._producer, task);
         }

         if (task == null) {
            try (AutoLock lock = this._lock.lock()) {
               switch (this._state) {
                  case IDLE:
                     throw new IllegalStateException();
                  case PRODUCE:
                     this._state = ProduceExecuteConsume.State.IDLE;
                     return;
                  case EXECUTE:
                     this._state = ProduceExecuteConsume.State.PRODUCE;
                     break;
                  default:
                     throw new IllegalStateException(this._state.toString());
               }
            }
         } else if (Invocable.getInvocationType(task) == Invocable.InvocationType.NON_BLOCKING) {
            task.run();
         } else {
            this._executor.execute(task);
         }
      }
   }

   @Override
   public void dispatch() {
      this._executor.execute(this::produce);
   }

   private static enum State {
      IDLE,
      PRODUCE,
      EXECUTE;
   }
}
