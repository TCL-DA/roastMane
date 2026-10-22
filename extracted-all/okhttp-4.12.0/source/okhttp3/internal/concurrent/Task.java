package okhttp3.internal.concurrent

import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nTask.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Task.kt\nokhttp3/internal/concurrent/Task\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,71:1\n1#2:72\n*E\n"])
abstract class Task {
   public final val cancelable: Boolean
   public final val name: String
   internal final var nextExecuteNanoTime: Long
   internal final var queue: TaskQueue?

   open fun Task(name: java.lang.String, cancelable: Boolean) {
      this.name = name;
      this.cancelable = cancelable;
      this.nextExecuteNanoTime = -1L;
   }

   public abstract fun runOnce(): Long {
   }

   internal fun initQueue(queue: TaskQueue) {
      if (this.queue != queue) {
         if (this.queue != null) {
            throw new IllegalStateException("task is in multiple queues".toString());
         } else {
            this.queue = queue;
         }
      }
   }

   public override fun toString(): String {
      return this.name;
   }
}
