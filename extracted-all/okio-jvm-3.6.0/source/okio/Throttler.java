package okio

import java.io.IOException
import java.io.InterruptedIOException
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import org.jetbrains.annotations.NotNull

class Throttler internal constructor(allocatedUntil: Long) {
   private final var allocatedUntil: Long
   private final var bytesPerSecond: Long
   public final val condition: Condition
   public final val lock: ReentrantLock
   private final var maxByteCount: Long
   private final var waitByteCount: Long

   init {
      this.allocatedUntil = allocatedUntil;
      this.waitByteCount = 8192L;
      this.maxByteCount = 262144L;
      this.lock = new ReentrantLock();
      var var10001: Condition = this.lock.newCondition();
      this.condition = var10001;
   }

   public constructor() : this(System.nanoTime())
   @JvmOverloads
   public fun bytesPerSecond(bytesPerSecond: Long, waitByteCount: Long, maxByteCount: Long) {
      label47: {
         var var7: Lock = this.lock;
         this.lock.lock();

         try {
            if (bytesPerSecond < 0L) {
               throw new IllegalArgumentException("Failed requirement.".toString());
            }

            if (waitByteCount <= 0L) {
               throw new IllegalArgumentException("Failed requirement.".toString());
            }

            if (maxByteCount < waitByteCount) {
               throw new IllegalArgumentException("Failed requirement.".toString());
            }

            this.bytesPerSecond = bytesPerSecond;
            this.waitByteCount = waitByteCount;
            this.maxByteCount = maxByteCount;
            this.condition.signalAll();
         } catch (java.lang.Throwable var10) {
            var7.unlock();
         }

         var7.unlock();
      }
   }

   internal fun take(byteCount: Long): Long {
      if (byteCount <= 0L) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      } else {
         label54: {
            var var3: Lock = this.lock;
            this.lock.lock();

            try {
               while (true) {
                  var byteCountOrWaitNanos: Long = this.byteCountOrWaitNanos$okio(System.nanoTime(), byteCount);
                  if (byteCountOrWaitNanos >= 0L) {
                     break;
                  }

                  this.condition.awaitNanos(-byteCountOrWaitNanos);
               }
            } catch (java.lang.Throwable var11) {
               var3.unlock();
            }

            var3.unlock();
         }
      }
   }

   internal fun byteCountOrWaitNanos(now: Long, byteCount: Long): Long {
      if (this.bytesPerSecond == 0L) {
         return byteCount;
      } else {
         var idleInNanos: Long = Math.max(this.allocatedUntil - now, 0L);
         var immediateBytes: Long = this.maxByteCount - this.nanosToBytes(idleInNanos);
         if (immediateBytes >= byteCount) {
            this.allocatedUntil = now + idleInNanos + this.bytesToNanos(byteCount);
            return byteCount;
         } else if (immediateBytes >= this.waitByteCount) {
            this.allocatedUntil = now + this.bytesToNanos(this.maxByteCount);
            return immediateBytes;
         } else {
            var minByteCount: Long = Math.min(this.waitByteCount, byteCount);
            var minWaitNanos: Long = idleInNanos + this.bytesToNanos(minByteCount - this.maxByteCount);
            if (minWaitNanos == 0L) {
               this.allocatedUntil = now + this.bytesToNanos(this.maxByteCount);
               return minByteCount;
            } else {
               return -minWaitNanos;
            }
         }
      }
   }

   private fun Long.nanosToBytes(): Long {
      return `$this$nanosToBytes` * this.bytesPerSecond / 1000000000L;
   }

   private fun Long.bytesToNanos(): Long {
      return `$this$bytesToNanos` * 1000000000L / this.bytesPerSecond;
   }

   public fun source(source: Source): Source {
      return new ForwardingSource(source, this) {
         {
            super(`$source`);
            this.this$0 = `$receiver`;
         }

         @Override
         public long read(@NotNull Buffer sink, long byteCount) {
            try {
               return super.read(sink, this.this$0.take$okio(byteCount));
            } catch (InterruptedException var6) {
               Thread.currentThread().interrupt();
               throw new InterruptedIOException("interrupted");
            }
         }
      };
   }

   public fun sink(sink: Sink): Sink {
      return new ForwardingSink(sink, this) {
         {
            super(`$sink`);
            this.this$0 = `$receiver`;
         }

         @Override
         public void write(@NotNull Buffer source, long byteCount) throws IOException {
            try {
               var e: Long = byteCount;

               while (remaining > 0L) {
                  var toWrite: Long = this.this$0.take$okio(e);
                  super.write(source, toWrite);
                  e -= toWrite;
               }
            } catch (InterruptedException var8) {
               Thread.currentThread().interrupt();
               throw new InterruptedIOException("interrupted");
            }
         }
      };
   }

   @JvmOverloads
   fun bytesPerSecond(bytesPerSecond: Long, waitByteCount: Long) {
      bytesPerSecond$default(this, bytesPerSecond, waitByteCount, 0L, 4, null);
   }

   @JvmOverloads
   fun bytesPerSecond(bytesPerSecond: Long) {
      bytesPerSecond$default(this, bytesPerSecond, 0L, 0L, 6, null);
   }
}
