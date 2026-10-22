package okhttp3.internal.concurrent

import java.util.Arrays
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.jvm.internal.InlineMarker

internal inline fun taskLog(task: Task, queue: TaskQueue, messageBlock: () -> String) {
   if (TaskRunner.Companion.getLogger().isLoggable(Level.FINE)) {
      access$log(task, queue, messageBlock.invoke() as java.lang.String);
   }
}

internal inline fun <T> logElapsed(task: Task, queue: TaskQueue, block: () -> T): T {
   label31: {
      var startNs: Long = -1L;
      var loggingEnabled: Boolean = TaskRunner.Companion.getLogger().isLoggable(Level.FINE);
      if (loggingEnabled) {
         startNs = queue.getTaskRunner$okhttp().getBackend().nanoTime();
         access$log(task, queue, "starting");
      }

      try {
         ;
      } catch (java.lang.Throwable var12) {
         InlineMarker.finallyStart(1);
         if (loggingEnabled) {
            var elapsedNs: Long = queue.getTaskRunner$okhttp().getBackend().nanoTime() - startNs;
            if (false) {
               access$log(task, queue, "finished run in " + formatDuration(elapsedNs));
            } else {
               access$log(task, queue, "failed a run in " + formatDuration(elapsedNs));
            }
         }

         InlineMarker.finallyEnd(1);
      }

      InlineMarker.finallyStart(1);
      if (loggingEnabled) {
         access$log(task, queue, "finished run in " + formatDuration(queue.getTaskRunner$okhttp().getBackend().nanoTime() - startNs));
      }

      InlineMarker.finallyEnd(1);
      var var15: Any;
      return (T)var15;
   }
}

private fun log(task: Task, queue: TaskQueue, message: String) {
   var var10000: Logger = TaskRunner.Companion.getLogger();
   var var10001: StringBuilder = new StringBuilder().append(queue.getName$okhttp()).append(' ');
   var var4: Array<Any> = new Object[]{message};
   var var10002: java.lang.String = java.lang.String.format("%-22s", Arrays.copyOf(var4, var4.length));
   var10000.fine(var10001.append(var10002).append(": ").append(task.getName()).toString());
}

public fun formatDuration(ns: Long): String {
   var var4: Array<Any> = new Object[]{
      if (ns <= -999500000L)
         (ns - (long)500000000) / (long)1000000000 + " s "
         else
         (
            if (ns <= -999500L)
               (ns - (long)500000) / (long)1000000 + " ms"
               else
               (
                  if (ns <= 0L)
                     (ns - (long)500) / (long)1000 + " µs"
                     else
                     (
                        if (ns < 999500L)
                           (ns + (long)500) / (long)1000 + " µs"
                           else
                           (if (ns < 999500000L) (ns + (long)500000) / (long)1000000 + " ms" else (ns + (long)500000000) / (long)1000000000 + " s ")
                     )
               )
         )
   };
   var var10000: java.lang.String = java.lang.String.format("%6s", Arrays.copyOf(var4, var4.length));
   return var10000;
}

@JvmSynthetic
fun `access$log`(task: Task, queue: TaskQueue, message: java.lang.String) {
   log(task, queue, message);
}

