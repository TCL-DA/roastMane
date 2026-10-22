package okhttp3.internal.concurrent

import java.util.ArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.RejectedExecutionException
import java.util.logging.Level
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.Util

@SourceDebugExtension(["SMAP\nTaskQueue.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TaskQueue.kt\nokhttp3/internal/concurrent/TaskQueue\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 TaskLogger.kt\nokhttp3/internal/concurrent/TaskLoggerKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 5 Util.kt\nokhttp3/internal/Util\n*L\n1#1,218:1\n1#2:219\n25#3,4:220\n25#3,4:224\n25#3,4:228\n25#3,4:232\n25#3,4:251\n350#4,7:236\n615#5,4:243\n615#5,4:247\n*S KotlinDebug\n*F\n+ 1 TaskQueue.kt\nokhttp3/internal/concurrent/TaskQueue\n*L\n65#1:220,4\n68#1:224,4\n153#1:228,4\n159#1:232,4\n208#1:251,4\n165#1:236,7\n179#1:243,4\n189#1:247,4\n*E\n"])
class TaskQueue internal constructor(taskRunner: TaskRunner, name: String) {
   internal final var activeTask: Task?
   internal final var cancelActiveTask: Boolean
   internal final val futureTasks: MutableList<Task>
   internal final val name: String
   public final val scheduledTasks: List<Task>
      public final get() {
         synchronized (this.taskRunner) {
            return CollectionsKt.toList(this.futureTasks);
         }
      }

   internal final var shutdown: Boolean
   internal final val taskRunner: TaskRunner

   init {
      this.taskRunner = taskRunner;
      this.name = name;
      this.futureTasks = new ArrayList<>();
   }

   public fun schedule(task: Task, delayNanos: Long) {
      var var4: TaskRunner;
      label42: {
         var4 = this.taskRunner;
         synchronized (this.taskRunner) {
            if (this.shutdown) {
               if (!task.getCancelable()) {
                  if (TaskRunner.Companion.getLogger().isLoggable(Level.FINE)) {
                     TaskLoggerKt.access$log(task, this, "schedule failed (queue is shutdown)");
                  }

                  throw new RejectedExecutionException();
               }

               if (TaskRunner.Companion.getLogger().isLoggable(Level.FINE)) {
                  TaskLoggerKt.access$log(task, this, "schedule canceled (queue is shutdown)");
               }
               break label42;
            }

            if (this.scheduleAndDecide$okhttp(task, delayNanos, false)) {
               this.taskRunner.kickCoordinator$okhttp(this);
            }
         }

         // $VF: monitorexit
         return;
      }

      // $VF: monitorexit
   }

   public inline fun schedule(name: String, delayNanos: Long, crossinline block: () -> Long) {
      this.schedule(new Task(name, block) {
         {
            super(`$name`, false, 2, null);
            this.$block = `$block`;
         }

         @Override
         public long runOnce() {
            return (this.$block.invoke() as java.lang.Number).longValue();
         }
      }, delayNanos);
   }

   public inline fun execute(name: String, delayNanos: Long, cancelable: Boolean, crossinline block: () -> Unit) {
      this.schedule(new Task(name, cancelable, block) {
         {
            super(`$name`, `$cancelable`);
            this.$block = `$block`;
         }

         @Override
         public long runOnce() {
            this.$block.invoke();
            return -1L;
         }
      }, delayNanos);
   }

   public fun idleLatch(): CountDownLatch {
      label54: {
         var var1: TaskRunner = this.taskRunner;
         synchronized (this.taskRunner){} // $VF: monitorenter 

         label51: {
            label50: {
               label49: {
                  try {
                     if (this.activeTask == null && this.futureTasks.isEmpty()) {
                        new CountDownLatch(0);
                        break label51;
                     }

                     if (this.activeTask is TaskQueue.AwaitIdleTask) {
                        var var8: CountDownLatch = (this.activeTask as TaskQueue.AwaitIdleTask).getLatch();
                        break label50;
                     }

                     for (Task futureTask : this.futureTasks) {
                        if (futureTask is TaskQueue.AwaitIdleTask) {
                           var var7: CountDownLatch = (futureTask as TaskQueue.AwaitIdleTask).getLatch();
                           break label49;
                        }
                     }

                     var var12: TaskQueue.AwaitIdleTask = new TaskQueue.AwaitIdleTask();
                     if (this.scheduleAndDecide$okhttp(var12, 0L, false)) {
                        this.taskRunner.kickCoordinator$okhttp(this);
                     }

                     var var6: CountDownLatch = var12.getLatch();
                  } catch (java.lang.Throwable var10) {
                     // $VF: monitorexit
                  }

                  // $VF: monitorexit
               }

               // $VF: monitorexit
            }

            // $VF: monitorexit
         }

         // $VF: monitorexit
      }
   }

   internal fun scheduleAndDecide(task: Task, delayNanos: Long, recurrence: Boolean): Boolean {
      task.initQueue$okhttp(this);
      var now: Long = this.taskRunner.getBackend().nanoTime();
      var executeNanoTime: Long = now + delayNanos;
      var existingIndex: Int = this.futureTasks.indexOf(task);
      if (existingIndex != -1) {
         if (task.getNextExecuteNanoTime$okhttp() <= executeNanoTime) {
            if (TaskRunner.Companion.getLogger().isLoggable(Level.FINE)) {
               TaskLoggerKt.access$log(task, this, "already scheduled");
            }

            return false;
         }

         this.futureTasks.remove(existingIndex);
      }

      task.setNextExecuteNanoTime$okhttp(executeNanoTime);
      if (TaskRunner.Companion.getLogger().isLoggable(Level.FINE)) {
         TaskLoggerKt.access$log(
            task,
            this,
            if (recurrence)
               "run again after " + TaskLoggerKt.formatDuration(executeNanoTime - now)
               else
               "scheduled after " + TaskLoggerKt.formatDuration(executeNanoTime - now)
         );
      }

      var `index$iv`: Int = 0;
      var var14: java.util.Iterator = this.futureTasks.iterator();

      var var10000: Int;
      while (true) {
         if (!var14.hasNext()) {
            var10000 = -1;
            break;
         }

         if ((var14.next() as Task).getNextExecuteNanoTime$okhttp() - now > delayNanos) {
            var10000 = `index$iv`;
            break;
         }

         `index$iv`++;
      }

      var var21: Int = var10000;
      if (var10000 == -1) {
         var21 = this.futureTasks.size();
      }

      this.futureTasks.add(var21, task);
      return var21 == 0;
   }

   public fun cancelAll() {
      if (Util.assertionsEnabled && Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + this);
      } else {
         synchronized (this.taskRunner) {
            if (this.cancelAllAndDecide$okhttp()) {
               this.taskRunner.kickCoordinator$okhttp(this);
            }
         }
      }
   }

   public fun shutdown() {
      if (Util.assertionsEnabled && Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + this);
      } else {
         synchronized (this.taskRunner) {
            this.shutdown = true;
            if (this.cancelAllAndDecide$okhttp()) {
               this.taskRunner.kickCoordinator$okhttp(this);
            }
         }
      }
   }

   internal fun cancelAllAndDecide(): Boolean {
      if (this.activeTask != null) {
         var var10000: Task = this.activeTask;
         if (var10000.getCancelable()) {
            this.cancelActiveTask = true;
         }
      }

      var tasksCanceled: Boolean = false;

      for (int i = this.futureTasks.size() - 1; -1 < i; i--) {
         if (this.futureTasks.get(i).getCancelable()) {
            var `task$iv`: Task = this.futureTasks.get(i);
            if (TaskRunner.Companion.getLogger().isLoggable(Level.FINE)) {
               TaskLoggerKt.access$log(`task$iv`, this, "canceled");
            }

            tasksCanceled = true;
            this.futureTasks.remove(i);
         }
      }

      return tasksCanceled;
   }

   public override fun toString(): String {
      return this.name;
   }

   private class AwaitIdleTask : Task(Util.okHttpName + " awaitIdle", false) {
      public final val latch: CountDownLatch = new CountDownLatch(1)


      public override fun runOnce(): Long {
         this.latch.countDown();
         return -1L;
      }
   }
}
