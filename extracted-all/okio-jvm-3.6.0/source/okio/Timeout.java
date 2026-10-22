package okio

import java.io.InterruptedIOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nTimeout.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Timeout.kt\nokio/Timeout\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,316:1\n1#2:317\n*E\n"])
open class Timeout {
   @JvmStatic
   public Timeout.Companion Companion = new Timeout.Companion(null);
   @JvmField
   @JvmStatic
   public Timeout NONE = new Timeout() {
      @NotNull
      @Override
      public Timeout timeout(long timeout, @NotNull TimeUnit unit) {
         return this;
      }

      @NotNull
      @Override
      public Timeout deadlineNanoTime(long deadlineNanoTime) {
         return this;
      }

      @Override
      public void throwIfReached() {
      }
   };

   private final var deadlineNanoTime: Long
   private final var hasDeadline: Boolean
   private final var timeoutNanos: Long


   public open fun timeout(timeout: Long, unit: TimeUnit): Timeout {
      if (timeout < 0L) {
         throw new IllegalArgumentException(("timeout < 0: " + timeout).toString());
      } else {
         this.timeoutNanos = unit.toNanos(timeout);
         return this;
      }
   }

   public open fun timeoutNanos(): Long {
      return this.timeoutNanos;
   }

   public open fun hasDeadline(): Boolean {
      return this.hasDeadline;
   }

   public open fun deadlineNanoTime(): Long {
      if (!this.hasDeadline) {
         throw new IllegalStateException("No deadline".toString());
      } else {
         return this.deadlineNanoTime;
      }
   }

   public open fun deadlineNanoTime(deadlineNanoTime: Long): Timeout {
      this.hasDeadline = true;
      this.deadlineNanoTime = deadlineNanoTime;
      return this;
   }

   public fun deadline(duration: Long, unit: TimeUnit): Timeout {
      if (duration <= 0L) {
         throw new IllegalArgumentException(("duration <= 0: " + duration).toString());
      } else {
         return this.deadlineNanoTime(System.nanoTime() + unit.toNanos(duration));
      }
   }

   public open fun clearTimeout(): Timeout {
      this.timeoutNanos = 0L;
      return this;
   }

   public open fun clearDeadline(): Timeout {
      this.hasDeadline = false;
      return this;
   }

   @Throws(java/io/IOException::class)
   public open fun throwIfReached() {
      if (Thread.currentThread().isInterrupted()) {
         throw new InterruptedIOException("interrupted");
      } else if (this.hasDeadline && this.deadlineNanoTime - System.nanoTime() <= 0L) {
         throw new InterruptedIOException("deadline reached");
      }
   }

   @Throws(java/io/InterruptedIOException::class)
   public fun awaitSignal(condition: Condition) {
      try {
         var e: Boolean = this.hasDeadline();
         var timeoutNanos: Long = this.timeoutNanos();
         if (!e && timeoutNanos == 0L) {
            condition.await();
         } else {
            var start: Long = System.nanoTime();
            var var10000: Long = if (e && timeoutNanos != 0L)
               Math.min(timeoutNanos, this.deadlineNanoTime() - start)
               else
               (if (e) this.deadlineNanoTime() - start else timeoutNanos);
            var var12: Long = 0L;
            if (var10000 > 0L) {
               condition.await(var10000, TimeUnit.NANOSECONDS);
               var12 = System.nanoTime() - start;
            }

            if (var12 >= var10000) {
               throw new InterruptedIOException("timeout");
            }
         }
      } catch (InterruptedException var11) {
         Thread.currentThread().interrupt();
         throw new InterruptedIOException("interrupted");
      }
   }

   @Throws(java/io/InterruptedIOException::class)
   public fun waitUntilNotified(monitor: Any) {
      try {
         var e: Boolean = this.hasDeadline();
         var timeoutNanos: Long = this.timeoutNanos();
         if (!e && timeoutNanos == 0L) {
            monitor.wait();
         } else {
            var start: Long = System.nanoTime();
            var var10000: Long = if (e && timeoutNanos != 0L)
               Math.min(timeoutNanos, this.deadlineNanoTime() - start)
               else
               (if (e) this.deadlineNanoTime() - start else timeoutNanos);
            var var14: Long = 0L;
            if (var10000 > 0L) {
               monitor.wait(var10000 / 1000000L, (int)(var10000 - var10000 / 1000000L * 1000000L));
               var14 = System.nanoTime() - start;
            }

            if (var14 >= var10000) {
               throw new InterruptedIOException("timeout");
            }
         }
      } catch (InterruptedException var13) {
         Thread.currentThread().interrupt();
         throw new InterruptedIOException("interrupted");
      }
   }

   public inline fun <T> intersectWith(other: Timeout, block: () -> T): T {
      var originalTimeout: Long = this.timeoutNanos();
      this.timeout(Companion.minTimeout(other.timeoutNanos(), this.timeoutNanos()), TimeUnit.NANOSECONDS);
      label40:
      if (!this.hasDeadline()) {
         if (other.hasDeadline()) {
            this.deadlineNanoTime(other.deadlineNanoTime());
         }

         try {
            var var15: Any = block.invoke();
         } catch (java.lang.Throwable var9) {
            InlineMarker.finallyStart(1);
            this.timeout(originalTimeout, TimeUnit.NANOSECONDS);
            if (other.hasDeadline()) {
               this.clearDeadline();
            }

            InlineMarker.finallyEnd(1);
         }

         InlineMarker.finallyStart(1);
         this.timeout(originalTimeout, TimeUnit.NANOSECONDS);
         if (other.hasDeadline()) {
            this.clearDeadline();
         }

         InlineMarker.finallyEnd(1);
      } else {
         label100: {
            var originalDeadline: Long = this.deadlineNanoTime();
            if (other.hasDeadline()) {
               this.deadlineNanoTime(Math.min(this.deadlineNanoTime(), other.deadlineNanoTime()));
            }

            try {
               var var8: Any = block.invoke();
            } catch (java.lang.Throwable var10) {
               InlineMarker.finallyStart(1);
               this.timeout(originalTimeout, TimeUnit.NANOSECONDS);
               if (other.hasDeadline()) {
                  this.deadlineNanoTime(originalDeadline);
               }

               InlineMarker.finallyEnd(1);
            }

            InlineMarker.finallyStart(1);
            this.timeout(originalTimeout, TimeUnit.NANOSECONDS);
            if (other.hasDeadline()) {
               this.deadlineNanoTime(originalDeadline);
            }

            InlineMarker.finallyEnd(1);
         }
      }
   }

   companion object Companion private constructor() {
      public final val NONE: Timeout


      public fun minTimeout(aNanos: Long, bNanos: Long): Long {
         return if (aNanos == 0L) bNanos else (if (bNanos == 0L) aNanos else (if (aNanos < bNanos) aNanos else bNanos));
      }
   }
}
