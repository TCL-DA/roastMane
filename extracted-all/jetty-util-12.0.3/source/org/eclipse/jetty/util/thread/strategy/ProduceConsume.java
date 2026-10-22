package org.eclipse.jetty.util.thread.strategy;

import java.util.concurrent.Executor;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.ExecutionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProduceConsume implements ExecutionStrategy, Runnable {
   private static final Logger LOG = LoggerFactory.getLogger(ProduceConsume.class);
   private final AutoLock _lock = new AutoLock();
   private final ExecutionStrategy.Producer _producer;
   private final Executor _executor;
   private ProduceConsume.State _state = ProduceConsume.State.IDLE;

   public ProduceConsume(ExecutionStrategy.Producer producer, Executor executor) {
      this._producer = producer;
      this._executor = executor;
   }

   @Override
   public void produce() {
      try (AutoLock lock = this._lock.lock()) {
         switch (this._state) {
            case IDLE:
               this._state = ProduceConsume.State.PRODUCE;
               break;
            case PRODUCE:
            case EXECUTE:
               this._state = ProduceConsume.State.EXECUTE;
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
                     this._state = ProduceConsume.State.IDLE;
                     return;
                  case EXECUTE:
                     this._state = ProduceConsume.State.PRODUCE;
                     break;
                  default:
                     throw new IllegalStateException(this._state.toString());
               }
            }
         } else {
            task.run();
         }
      }
   }

   @Override
   public void dispatch() {
      this._executor.execute(this);
   }

   @Override
   public void run() {
      this.produce();
   }

   private static enum State {
      IDLE,
      PRODUCE,
      EXECUTE;
   }
}
