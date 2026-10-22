package okhttp3.internal.concurrent

import java.util.ArrayList
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.Util

@SourceDebugExtension(["SMAP\nTaskRunner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TaskRunner.kt\nokhttp3/internal/concurrent/TaskRunner\n+ 2 Util.kt\nokhttp3/internal/Util\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,314:1\n608#2,4:315\n608#2,4:319\n615#2,4:323\n608#2,4:327\n608#2,4:331\n1#3:335\n*S KotlinDebug\n*F\n+ 1 TaskRunner.kt\nokhttp3/internal/concurrent/TaskRunner\n*L\n79#1:315,4\n97#1:319,4\n108#1:323,4\n126#1:327,4\n152#1:331,4\n*E\n"])
class TaskRunner(backend: okhttp3.internal.concurrent.TaskRunner.Backend) {
   @JvmStatic
   public TaskRunner.Companion Companion = new TaskRunner.Companion(null);
   @JvmField
   @JvmStatic
   public TaskRunner INSTANCE = new TaskRunner(new TaskRunner.RealBackend(Util.threadFactory(Util.okHttpName + " TaskRunner", true)));
   @JvmStatic
   private Logger logger;

   public final val backend: okhttp3.internal.concurrent.TaskRunner.Backend
   private final val busyQueues: MutableList<TaskQueue>
   private final var coordinatorWaiting: Boolean
   private final var coordinatorWakeUpAt: Long
   private final var nextQueueName: Int
   private final val readyQueues: MutableList<TaskQueue>
   private final val runnable: Runnable

   init {
      this.backend = backend;
      this.nextQueueName = 10000;
      this.busyQueues = new ArrayList<>();
      this.readyQueues = new ArrayList<>();
      this.runnable = new Runnable(this) {
         {
            this.this$0 = `$receiver`;
         }

         @Override
         public void run() {
            // $VF: Couldn't be decompiled
            // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
            // java.lang.IndexOutOfBoundsException: Index -1 out of bounds for length 0
            //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
            //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
            //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
            //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
            //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
            //   at org.jetbrains.java.decompiler.modules.decompiler.stats.SynchronizedStatement.initExprents(SynchronizedStatement.java:97)
            //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:189)
            //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
            //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
            //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
            //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
            //
            // Bytecode:
            // 000: nop
            // 001: aload 0
            // 002: getfield okhttp3/internal/concurrent/TaskRunner$runnable$1.this$0 Lokhttp3/internal/concurrent/TaskRunner;
            // 005: astore 3
            // 006: aload 0
            // 007: getfield okhttp3/internal/concurrent/TaskRunner$runnable$1.this$0 Lokhttp3/internal/concurrent/TaskRunner;
            // 00a: astore 4
            // 00c: aload 3
            // 00d: astore 5
            // 00f: aload 5
            // 011: monitorenter
            // 012: nop
            // 013: bipush 0
            // 014: istore 6
            // 016: aload 4
            // 018: invokevirtual okhttp3/internal/concurrent/TaskRunner.awaitTaskToRun ()Lokhttp3/internal/concurrent/Task;
            // 01b: astore 6
            // 01d: aload 5
            // 01f: monitorexit
            // 020: aload 6
            // 022: goto 02d
            // 025: astore 6
            // 027: aload 5
            // 029: monitorexit
            // 02a: aload 6
            // 02c: athrow
            // 02d: dup
            // 02e: ifnonnull 033
            // 031: pop
            // 032: return
            // 033: astore 1
            // 034: aload 1
            // 035: invokevirtual okhttp3/internal/concurrent/Task.getQueue$okhttp ()Lokhttp3/internal/concurrent/TaskQueue;
            // 038: dup
            // 039: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
            // 03c: astore 2
            // 03d: aload 0
            // 03e: getfield okhttp3/internal/concurrent/TaskRunner$runnable$1.this$0 Lokhttp3/internal/concurrent/TaskRunner;
            // 041: astore 3
            // 042: bipush 0
            // 043: istore 4
            // 045: ldc2_w -1
            // 048: lstore 5
            // 04a: getstatic okhttp3/internal/concurrent/TaskRunner.Companion Lokhttp3/internal/concurrent/TaskRunner$Companion;
            // 04d: invokevirtual okhttp3/internal/concurrent/TaskRunner$Companion.getLogger ()Ljava/util/logging/Logger;
            // 050: getstatic java/util/logging/Level.FINE Ljava/util/logging/Level;
            // 053: invokevirtual java/util/logging/Logger.isLoggable (Ljava/util/logging/Level;)Z
            // 056: istore 7
            // 058: iload 7
            // 05a: ifeq 072
            // 05d: aload 2
            // 05e: invokevirtual okhttp3/internal/concurrent/TaskQueue.getTaskRunner$okhttp ()Lokhttp3/internal/concurrent/TaskRunner;
            // 061: invokevirtual okhttp3/internal/concurrent/TaskRunner.getBackend ()Lokhttp3/internal/concurrent/TaskRunner$Backend;
            // 064: invokeinterface okhttp3/internal/concurrent/TaskRunner$Backend.nanoTime ()J 1
            // 069: lstore 5
            // 06b: aload 1
            // 06c: aload 2
            // 06d: ldc "starting"
            // 06f: invokestatic okhttp3/internal/concurrent/TaskLoggerKt.access$log (Lokhttp3/internal/concurrent/Task;Lokhttp3/internal/concurrent/TaskQueue;Ljava/lang/String;)V
            // 072: bipush 0
            // 073: istore 8
            // 075: nop
            // 076: bipush 0
            // 077: istore 9
            // 079: bipush 0
            // 07a: istore 10
            // 07c: nop
            // 07d: aload 3
            // 07e: aload 1
            // 07f: invokestatic okhttp3/internal/concurrent/TaskRunner.access$runTask (Lokhttp3/internal/concurrent/TaskRunner;Lokhttp3/internal/concurrent/Task;)V
            // 082: bipush 1
            // 083: istore 10
            // 085: nop
            // 086: goto 09b
            // 089: astore 11
            // 08b: aload 3
            // 08c: invokevirtual okhttp3/internal/concurrent/TaskRunner.getBackend ()Lokhttp3/internal/concurrent/TaskRunner$Backend;
            // 08f: aload 0
            // 090: checkcast java/lang/Runnable
            // 093: invokeinterface okhttp3/internal/concurrent/TaskRunner$Backend.execute (Ljava/lang/Runnable;)V 2
            // 098: aload 11
            // 09a: athrow
            // 09b: nop
            // 09c: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
            // 09f: astore 9
            // 0a1: bipush 1
            // 0a2: istore 8
            // 0a4: aload 9
            // 0a6: astore 10
            // 0a8: iload 7
            // 0aa: ifeq 0db
            // 0ad: aload 2
            // 0ae: invokevirtual okhttp3/internal/concurrent/TaskQueue.getTaskRunner$okhttp ()Lokhttp3/internal/concurrent/TaskRunner;
            // 0b1: invokevirtual okhttp3/internal/concurrent/TaskRunner.getBackend ()Lokhttp3/internal/concurrent/TaskRunner$Backend;
            // 0b4: invokeinterface okhttp3/internal/concurrent/TaskRunner$Backend.nanoTime ()J 1
            // 0b9: lload 5
            // 0bb: lsub
            // 0bc: lstore 12
            // 0be: nop
            // 0bf: aload 1
            // 0c0: aload 2
            // 0c1: new java/lang/StringBuilder
            // 0c4: dup
            // 0c5: invokespecial java/lang/StringBuilder.<init> ()V
            // 0c8: ldc "finished run in "
            // 0ca: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
            // 0cd: lload 12
            // 0cf: invokestatic okhttp3/internal/concurrent/TaskLoggerKt.formatDuration (J)Ljava/lang/String;
            // 0d2: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
            // 0d5: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
            // 0d8: invokestatic okhttp3/internal/concurrent/TaskLoggerKt.access$log (Lokhttp3/internal/concurrent/Task;Lokhttp3/internal/concurrent/TaskQueue;Ljava/lang/String;)V
            // 0db: goto 000
            // 0de: astore 9
            // 0e0: iload 7
            // 0e2: ifeq 136
            // 0e5: aload 2
            // 0e6: invokevirtual okhttp3/internal/concurrent/TaskQueue.getTaskRunner$okhttp ()Lokhttp3/internal/concurrent/TaskRunner;
            // 0e9: invokevirtual okhttp3/internal/concurrent/TaskRunner.getBackend ()Lokhttp3/internal/concurrent/TaskRunner$Backend;
            // 0ec: invokeinterface okhttp3/internal/concurrent/TaskRunner$Backend.nanoTime ()J 1
            // 0f1: lload 5
            // 0f3: lsub
            // 0f4: lstore 14
            // 0f6: iload 8
            // 0f8: ifeq 11a
            // 0fb: aload 1
            // 0fc: aload 2
            // 0fd: new java/lang/StringBuilder
            // 100: dup
            // 101: invokespecial java/lang/StringBuilder.<init> ()V
            // 104: ldc "finished run in "
            // 106: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
            // 109: lload 14
            // 10b: invokestatic okhttp3/internal/concurrent/TaskLoggerKt.formatDuration (J)Ljava/lang/String;
            // 10e: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
            // 111: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
            // 114: invokestatic okhttp3/internal/concurrent/TaskLoggerKt.access$log (Lokhttp3/internal/concurrent/Task;Lokhttp3/internal/concurrent/TaskQueue;Ljava/lang/String;)V
            // 117: goto 136
            // 11a: aload 1
            // 11b: aload 2
            // 11c: new java/lang/StringBuilder
            // 11f: dup
            // 120: invokespecial java/lang/StringBuilder.<init> ()V
            // 123: ldc "failed a run in "
            // 125: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
            // 128: lload 14
            // 12a: invokestatic okhttp3/internal/concurrent/TaskLoggerKt.formatDuration (J)Ljava/lang/String;
            // 12d: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
            // 130: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
            // 133: invokestatic okhttp3/internal/concurrent/TaskLoggerKt.access$log (Lokhttp3/internal/concurrent/Task;Lokhttp3/internal/concurrent/TaskQueue;Ljava/lang/String;)V
            // 136: aload 9
            // 138: athrow
         }
      };
   }

   internal fun kickCoordinator(taskQueue: TaskQueue) {
      if (Util.assertionsEnabled && !Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + this);
      } else {
         if (taskQueue.getActiveTask$okhttp() == null) {
            if (!taskQueue.getFutureTasks$okhttp().isEmpty()) {
               Util.addIfAbsent(this.readyQueues, taskQueue);
            } else {
               this.readyQueues.remove(taskQueue);
            }
         }

         if (this.coordinatorWaiting) {
            this.backend.coordinatorNotify(this);
         } else {
            this.backend.execute(this.runnable);
         }
      }
   }

   private fun beforeRun(task: Task) {
      if (Util.assertionsEnabled && !Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + this);
      } else {
         task.setNextExecuteNanoTime$okhttp(-1L);
         var var10000: TaskQueue = task.getQueue$okhttp();
         var10000.getFutureTasks$okhttp().remove(task);
         this.readyQueues.remove(var10000);
         var10000.setActiveTask$okhttp(task);
         this.busyQueues.add(var10000);
      }
   }

   private fun runTask(task: Task) {
      if (Util.assertionsEnabled && Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + this);
      } else {
         label58: {
            var currentThread: Thread = Thread.currentThread();
            var var14: java.lang.String = currentThread.getName();
            currentThread.setName(task.getName());
            var var17: Long = -1L;

            try {
               var17 = task.runOnce();
            } catch (java.lang.Throwable var11) {
               synchronized (this) {
                  this.afterRun(task, var17);
               }

               currentThread.setName(var14);
            }

            synchronized (this) {
               this.afterRun(task, var17);
            }

            currentThread.setName(var14);
         }
      }
   }

   private fun afterRun(task: Task, delayNanos: Long) {
      if (Util.assertionsEnabled && !Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + this);
      } else {
         var var10000: TaskQueue = task.getQueue$okhttp();
         if (var10000.getActiveTask$okhttp() != task) {
            throw new IllegalStateException("Check failed.".toString());
         } else {
            var var7: Boolean = var10000.getCancelActiveTask$okhttp();
            var10000.setCancelActiveTask$okhttp(false);
            var10000.setActiveTask$okhttp(null);
            this.busyQueues.remove(var10000);
            if (delayNanos != -1L && !var7 && !var10000.getShutdown$okhttp()) {
               var10000.scheduleAndDecide$okhttp(task, delayNanos, true);
            }

            if (!var10000.getFutureTasks$okhttp().isEmpty()) {
               this.readyQueues.add(var10000);
            }
         }
      }
   }

   public fun awaitTaskToRun(): Task? {
      if (Util.assertionsEnabled && !Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + this);
      } else {
         label129: {
            while (!this.readyQueues.isEmpty()) {
               var now: Long = this.backend.nanoTime();
               var minDelayNanos: Long = java.lang.Long.MAX_VALUE;
               var readyTask: Task = null;
               var multipleReadyTasks: Boolean = false;

               for (TaskQueue queue : this.readyQueues) {
                  var candidate: Task = queue.getFutureTasks$okhttp().get(0);
                  var candidateDelay: Long = Math.max(0L, candidate.getNextExecuteNanoTime$okhttp() - now);
                  if (candidateDelay > 0L) {
                     minDelayNanos = Math.min(candidateDelay, minDelayNanos);
                  } else {
                     if (readyTask != null) {
                        multipleReadyTasks = true;
                        break;
                     }

                     readyTask = candidate;
                  }
               }

               if (readyTask != null) {
                  this.beforeRun(readyTask);
                  if (multipleReadyTasks || !this.coordinatorWaiting && !this.readyQueues.isEmpty()) {
                     this.backend.execute(this.runnable);
                  }

                  return readyTask;
               }

               if (this.coordinatorWaiting) {
                  if (minDelayNanos < this.coordinatorWakeUpAt - now) {
                     this.backend.coordinatorNotify(this);
                  }

                  return null;
               }

               this.coordinatorWaiting = true;
               this.coordinatorWakeUpAt = now + minDelayNanos;

               label76: {
                  try {
                     try {
                        this.backend.coordinatorWait(this, minDelayNanos);
                        break label76;
                     } catch (InterruptedException var12) {
                        this.cancelAll();
                     }
                  } catch (java.lang.Throwable var13) {
                     this.coordinatorWaiting = false;
                  }

                  this.coordinatorWaiting = false;
                  continue;
               }

               this.coordinatorWaiting = false;
            }

            return null;
         }
      }
   }

   public fun newQueue(): TaskQueue {
      var var10000: Int;
      synchronized (this) {
         var10000 = this.nextQueueName++;
      }

      return new TaskQueue(this, "" + 'Q' + var10000);
   }

   public fun activeQueues(): List<TaskQueue> {
      synchronized (this) {
         return CollectionsKt.plus(this.busyQueues, this.readyQueues);
      }
   }

   public fun cancelAll() {
      for (int i = this.busyQueues.size() - 1; -1 < i; i--) {
         this.busyQueues.get(i).cancelAllAndDecide$okhttp();
      }

      for (int i = this.readyQueues.size() - 1; -1 < i; i--) {
         var queue: TaskQueue = this.readyQueues.get(var3);
         queue.cancelAllAndDecide$okhttp();
         if (queue.getFutureTasks$okhttp().isEmpty()) {
            this.readyQueues.remove(var3);
         }
      }
   }

   @JvmStatic
   fun {
      var var10000: Logger = Logger.getLogger(TaskRunner.class.getName());
      logger = var10000;
   }

   interface Backend {
      public abstract fun beforeTask(taskRunner: TaskRunner) {
      }

      public abstract fun nanoTime(): Long {
      }

      public abstract fun coordinatorNotify(taskRunner: TaskRunner) {
      }

      public abstract fun coordinatorWait(taskRunner: TaskRunner, nanos: Long) {
      }

      public abstract fun execute(runnable: Runnable) {
      }
   }

   companion object Companion private constructor() {
      public final val INSTANCE: TaskRunner
      public final val logger: Logger

   }

   @SourceDebugExtension(["SMAP\nTaskRunner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TaskRunner.kt\nokhttp3/internal/concurrent/TaskRunner$RealBackend\n+ 2 Util.kt\nokhttp3/internal/Util\n*L\n1#1,314:1\n560#2:315\n*S KotlinDebug\n*F\n+ 1 TaskRunner.kt\nokhttp3/internal/concurrent/TaskRunner$RealBackend\n*L\n281#1:315\n*E\n"])
   class RealBackend(threadFactory: ThreadFactory) : TaskRunner.Backend {
      private final val executor: ThreadPoolExecutor

      init {
         this.executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), threadFactory);
      }

      public override fun beforeTask(taskRunner: TaskRunner) {
      }

      public override fun nanoTime(): Long {
         return System.nanoTime();
      }

      public override fun coordinatorNotify(taskRunner: TaskRunner) {
         taskRunner.notify();
      }

      @Throws(java/lang/InterruptedException::class)
      public override fun coordinatorWait(taskRunner: TaskRunner, nanos: Long) {
         var ms: Long = nanos / 1000000L;
         var ns: Long = nanos - nanos / 1000000L * 1000000L;
         if (ms > 0L || nanos > 0L) {
            taskRunner.wait(ms, (int)ns);
         }
      }

      public override fun execute(runnable: Runnable) {
         this.executor.execute(runnable);
      }

      public fun shutdown() {
         this.executor.shutdown();
      }
   }
}
