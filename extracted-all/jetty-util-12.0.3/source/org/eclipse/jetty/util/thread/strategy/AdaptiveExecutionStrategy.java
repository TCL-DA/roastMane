package org.eclipse.jetty.util.thread.strategy;

import java.io.Closeable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.LongAdder;
import org.eclipse.jetty.util.AtomicBiInteger;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.VirtualThreads;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.thread.ExecutionStrategy;
import org.eclipse.jetty.util.thread.Invocable;
import org.eclipse.jetty.util.thread.TryExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("Adaptive execution strategy")
public class AdaptiveExecutionStrategy extends ContainerLifeCycle implements ExecutionStrategy, Runnable {
   private static final Logger LOG = LoggerFactory.getLogger(AdaptiveExecutionStrategy.class);
   private static final int IDLE = 0;
   private static final int PRODUCING = 1;
   private static final int REPRODUCING = 2;
   private final LongAdder _pcMode = new LongAdder();
   private final LongAdder _picMode = new LongAdder();
   private final LongAdder _pecMode = new LongAdder();
   private final LongAdder _epcMode = new LongAdder();
   private final ExecutionStrategy.Producer _producer;
   private final Executor _executor;
   private final TryExecutor _tryExecutor;
   private final Executor _virtualExecutor;
   private final AtomicBiInteger _state = new AtomicBiInteger();

   public AdaptiveExecutionStrategy(ExecutionStrategy.Producer producer, Executor executor) {
      this._producer = producer;
      this._executor = executor;
      this._tryExecutor = TryExecutor.asTryExecutor(executor);
      this._virtualExecutor = VirtualThreads.getVirtualThreadsExecutor(this._executor);
      this.addBean(this._producer);
      this.addBean(this._tryExecutor);
      this.addBean(this._virtualExecutor);
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} created", this);
      }
   }

   @Override
   public void dispatch() {
      boolean execute = false;

      label30:
      while (true) {
         long biState = this._state.get();
         int state = AtomicBiInteger.getLo(biState);
         int pending = AtomicBiInteger.getHi(biState);
         switch (state) {
            case 0:
               if (pending > 0) {
                  break label30;
               }

               if (!this._state.compareAndSet(biState, pending + 1, state)) {
                  break;
               }

               execute = true;
               break label30;
            case 1:
               if (!this._state.compareAndSet(biState, pending, 2)) {
                  break;
               }
            default:
               break label30;
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("{} dispatch {}", this, execute);
      }

      if (execute) {
         this._executor.execute(this);
      }
   }

   @Override
   public void produce() {
      this.tryProduce(false);
   }

   @Override
   public void run() {
      this.tryProduce(true);
   }

   private void tryProduce(boolean wasPending) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} tryProduce {}", this, wasPending);
      }

      while (true) {
         long biState = this._state.get();
         int state = AtomicBiInteger.getLo(biState);
         int pending = AtomicBiInteger.getHi(biState);
         if (wasPending) {
            pending--;
         }

         switch (state) {
            case 0:
               if (this._state.compareAndSet(biState, pending, 1)) {
                  boolean nonBlocking = Invocable.isNonBlockingInvocation();

                  label56:
                  while (this.isRunning()) {
                     try {
                        Runnable task = this.produceTask();
                        if (task != null) {
                           if (!this.consumeTask(task, this.selectSubStrategy(task, nonBlocking))) {
                              return;
                           }
                        } else {
                           while (true) {
                              long biStatex = this._state.get();
                              int statex = AtomicBiInteger.getLo(biStatex);
                              int pendingx = AtomicBiInteger.getHi(biStatex);
                              switch (statex) {
                                 case 1:
                                    if (this._state.compareAndSet(biStatex, pendingx, 0)) {
                                       return;
                                    }
                                    break;
                                 case 2:
                                    if (this._state.compareAndSet(biStatex, pendingx, 1)) {
                                       continue label56;
                                    }
                                    break;
                                 default:
                                    throw new IllegalStateException(this.toString(biStatex));
                              }
                           }
                        }
                     } catch (Throwable var8) {
                        LOG.warn("Unable to produce", var8);
                     }
                  }

                  return;
               }
               break;
            case 1:
               if (this._state.compareAndSet(biState, pending, 2)) {
                  return;
               }
               break;
            case 2:
               if (this._state.compareAndSet(biState, pending, state)) {
                  return;
               }
               break;
            default:
               throw new IllegalStateException(this.toString(biState));
         }
      }
   }

   private AdaptiveExecutionStrategy.SubStrategy selectSubStrategy(Runnable task, boolean nonBlocking) {
      Invocable.InvocationType taskType = Invocable.getInvocationType(task);
      switch (taskType) {
         case NON_BLOCKING:
            return AdaptiveExecutionStrategy.SubStrategy.PRODUCE_CONSUME;
         case EITHER:
            if (nonBlocking) {
               return AdaptiveExecutionStrategy.SubStrategy.PRODUCE_CONSUME;
            } else {
               boolean tryExecuted = false;

               while (true) {
                  long biState = this._state.get();
                  int state = AtomicBiInteger.getLo(biState);
                  int pending = AtomicBiInteger.getHi(biState);
                  if (tryExecuted || pending <= 0 && this._tryExecutor.tryExecute(this)) {
                     tryExecuted = true;
                     pending++;
                  }

                  if (pending > 0) {
                     if (this._state.compareAndSet(biState, pending, 0)) {
                        return AdaptiveExecutionStrategy.SubStrategy.EXECUTE_PRODUCE_CONSUME;
                     }
                  } else if (this._state.compareAndSet(biState, pending, state)) {
                     return AdaptiveExecutionStrategy.SubStrategy.PRODUCE_INVOKE_CONSUME;
                  }
               }
            }
         case BLOCKING:
            if (!nonBlocking) {
               boolean tryExecuted = false;

               while (true) {
                  long biStatex = this._state.get();
                  int statex = AtomicBiInteger.getLo(biStatex);
                  int pendingx = AtomicBiInteger.getHi(biStatex);
                  if (tryExecuted || pendingx <= 0 && this._tryExecutor.tryExecute(this)) {
                     tryExecuted = true;
                     pendingx++;
                  }

                  if (pendingx > 0) {
                     if (this._state.compareAndSet(biStatex, pendingx, 0)) {
                        return AdaptiveExecutionStrategy.SubStrategy.EXECUTE_PRODUCE_CONSUME;
                     }
                  } else if (this._state.compareAndSet(biStatex, pendingx, statex)) {
                     break;
                  }
               }
            }

            return AdaptiveExecutionStrategy.SubStrategy.PRODUCE_EXECUTE_CONSUME;
         default:
            throw new IllegalStateException(String.format("taskType=%s %s", taskType, this));
      }
   }

   private boolean consumeTask(Runnable task, AdaptiveExecutionStrategy.SubStrategy subStrategy) {
      if (LOG.isDebugEnabled()) {
         LOG.debug(
            "consumeTask ss={}/{}/{} t={} {}", new Object[]{subStrategy, Invocable.isNonBlockingInvocation(), Invocable.getInvocationType(task), task, this}
         );
      }

      switch (subStrategy) {
         case PRODUCE_CONSUME:
            this._pcMode.increment();
            this.runTask(task);
            return true;
         case PRODUCE_INVOKE_CONSUME:
            this._picMode.increment();
            this.invokeAsNonBlocking(task);
            return true;
         case PRODUCE_EXECUTE_CONSUME:
            this._pecMode.increment();
            this.execute(task);
            return true;
         case EXECUTE_PRODUCE_CONSUME:
            this._epcMode.increment();
            this.runTask(task);

            long biState;
            int pending;
            do {
               biState = this._state.get();
               int state = AtomicBiInteger.getLo(biState);
               pending = AtomicBiInteger.getHi(biState);
               if (state != 0) {
                  return false;
               }
            } while (!this._state.compareAndSet(biState, pending, 1));

            return true;
         default:
            throw new IllegalStateException(String.format("ss=%s %s", subStrategy, this));
      }
   }

   private void runTask(Runnable task) {
      try {
         task.run();
      } catch (Throwable var3) {
         LOG.warn("Task run failed", var3);
      }
   }

   private void invokeAsNonBlocking(Runnable task) {
      try {
         Invocable.invokeNonBlocking(task);
      } catch (Throwable var3) {
         LOG.warn("Task invoke failed", var3);
      }
   }

   private Runnable produceTask() {
      try {
         return this._producer.produce();
      } catch (Throwable var2) {
         LOG.warn("Task produce failed", var2);
         return null;
      }
   }

   private void execute(Runnable task) {
      try {
         Executor executor = this._virtualExecutor;
         if (executor == null) {
            executor = this._executor;
         }

         executor.execute(task);
      } catch (RejectedExecutionException var3) {
         if (this.isRunning()) {
            LOG.warn("Execute failed", var3);
         } else {
            LOG.trace("IGNORED", var3);
         }

         if (task instanceof Closeable) {
            IO.close((Closeable)task);
         }
      }
   }

   @ManagedAttribute(
      value = "whether this execution strategy uses virtual threads",
      readonly = true
   )
   public boolean isUseVirtualThreads() {
      return this._virtualExecutor != null;
   }

   @ManagedAttribute(
      value = "number of tasks consumed with PC mode",
      readonly = true
   )
   public long getPCTasksConsumed() {
      return this._pcMode.longValue();
   }

   @ManagedAttribute(
      value = "number of tasks executed with PIC mode",
      readonly = true
   )
   public long getPICTasksExecuted() {
      return this._picMode.longValue();
   }

   @ManagedAttribute(
      value = "number of tasks executed with PEC mode",
      readonly = true
   )
   public long getPECTasksExecuted() {
      return this._pecMode.longValue();
   }

   @ManagedAttribute(
      value = "number of tasks consumed with EPC mode",
      readonly = true
   )
   public long getEPCTasksConsumed() {
      return this._epcMode.longValue();
   }

   @ManagedAttribute(
      value = "whether this execution strategy is idle",
      readonly = true
   )
   public boolean isIdle() {
      return this._state.getLo() == 0;
   }

   @ManagedOperation(
      value = "resets the task counts",
      impact = "ACTION"
   )
   public void reset() {
      this._pcMode.reset();
      this._epcMode.reset();
      this._pecMode.reset();
      this._picMode.reset();
   }

   @Override
   public String toString() {
      return this.toString(this._state.get());
   }

   public String toString(long biState) {
      StringBuilder builder = new StringBuilder();
      this.getString(builder);
      this.getState(builder, biState);
      return builder.toString();
   }

   private void getString(StringBuilder builder) {
      builder.append(this.getClass().getSimpleName());
      builder.append('@');
      builder.append(Integer.toHexString(this.hashCode()));
      builder.append('/');
      builder.append(this._producer);
      builder.append('/');
   }

   private void getState(StringBuilder builder, long biState) {
      int state = AtomicBiInteger.getLo(biState);
      int pending = AtomicBiInteger.getHi(biState);

      builder.append(switch (state) {
         case 0 -> "IDLE";
         case 1 -> "PRODUCING";
         case 2 -> "REPRODUCING";
         default -> "UNKNOWN(%d)".formatted(state);
      });
      builder.append("/p=");
      builder.append(pending);
      builder.append('/');
      builder.append(this._tryExecutor);
      builder.append("[pc=");
      builder.append(this.getPCTasksConsumed());
      builder.append(",pic=");
      builder.append(this.getPICTasksExecuted());
      builder.append(",pec=");
      builder.append(this.getPECTasksExecuted());
      builder.append(",epc=");
      builder.append(this.getEPCTasksConsumed());
      builder.append("]");
      builder.append("@");
      builder.append(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now()));
   }

   private static enum SubStrategy {
      PRODUCE_CONSUME,
      PRODUCE_INVOKE_CONSUME,
      PRODUCE_EXECUTE_CONSUME,
      EXECUTE_PRODUCE_CONSUME;
   }
}
