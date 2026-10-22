package okio

import java.io.IOException
import java.io.InterruptedIOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull

open class AsyncTimeout : Timeout {
   @JvmStatic
   public AsyncTimeout.Companion Companion = new AsyncTimeout.Companion(null);
   @JvmStatic
   private ReentrantLock lock = new ReentrantLock();
   @JvmStatic
   private Condition condition;
   @JvmStatic
   private int TIMEOUT_WRITE_SIZE = 65536;
   @JvmStatic
   private long IDLE_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(60L);
   @JvmStatic
   private long IDLE_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(IDLE_TIMEOUT_MILLIS);
   @JvmStatic
   private AsyncTimeout head;

   private final var inQueue: Boolean
   private final var next: AsyncTimeout?
   private final var timeoutAt: Long


   public fun enter() {
      var timeoutNanos: Long = this.timeoutNanos();
      var hasDeadline: Boolean = this.hasDeadline();
      if (timeoutNanos != 0L || hasDeadline) {
         AsyncTimeout.Companion.access$scheduleTimeout(Companion, this, timeoutNanos, hasDeadline);
      }
   }

   public fun exit(): Boolean {
      return AsyncTimeout.Companion.access$cancelScheduledTimeout(Companion, this);
   }

   private fun remainingNanos(now: Long): Long {
      return this.timeoutAt - now;
   }

   protected open fun timedOut() {
   }

   public fun sink(sink: Sink): Sink {
      return new Sink(this, sink) {
         {
            this.this$0 = `$receiver`;
            this.$sink = `$sink`;
         }

         // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         @Override
         public void write(@NotNull Buffer source, long byteCount) {
            label95: {
               -SegmentedByteString.checkOffsetAndCount(source.size(), 0L, byteCount);
               var remaining: Long = byteCount;

               while (remaining > 0L) {
                  var toWrite: Long = 0L;
                  var var10000: Segment = source.head;
                  var s: Segment = var10000;

                  while (toWrite < 65536L) {
                     toWrite += (long)(s.limit - s.pos);
                     if (toWrite >= remaining) {
                        break;
                     }

                     var10000 = s.next;
                     s = var10000;
                  }

                  var var21: AsyncTimeout = this.this$0;
                  var var8: Sink = this.$sink;
                  this.this$0.enter();

                  try {
                     try {
                        ;
                     } catch (IOException var16) {
                        throw if (!var21.exit()) var16 as java.lang.Throwable else var21.access$newTimeoutException(var16) as java.lang.Throwable;
                     }
                  } catch (java.lang.Throwable var17) {
                     if (var21.exit() && false) {
                        throw var21.access$newTimeoutException(null);
                     }
                  }

                  if (var21.exit()) {
                     throw var21.access$newTimeoutException(null);
                  }
               }
            }
         }

         // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         @Override
         public void flush() {
            label38: {
               var `this_$iv`: AsyncTimeout = this.this$0;
               var var2: Sink = this.$sink;
               this.this$0.enter();

               try {
                  try {
                     ;
                  } catch (IOException var8) {
                     throw if (!`this_$iv`.exit()) var8 as java.lang.Throwable else `this_$iv`.access$newTimeoutException(var8) as java.lang.Throwable;
                  }
               } catch (java.lang.Throwable var9) {
                  if (`this_$iv`.exit() && false) {
                     throw `this_$iv`.access$newTimeoutException(null);
                  }
               }

               if (`this_$iv`.exit()) {
                  throw `this_$iv`.access$newTimeoutException(null);
               }
            }
         }

         // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         @Override
         public void close() {
            label38: {
               var `this_$iv`: AsyncTimeout = this.this$0;
               var var2: Sink = this.$sink;
               this.this$0.enter();

               try {
                  try {
                     ;
                  } catch (IOException var8) {
                     throw if (!`this_$iv`.exit()) var8 as java.lang.Throwable else `this_$iv`.access$newTimeoutException(var8) as java.lang.Throwable;
                  }
               } catch (java.lang.Throwable var9) {
                  if (`this_$iv`.exit() && false) {
                     throw `this_$iv`.access$newTimeoutException(null);
                  }
               }

               if (`this_$iv`.exit()) {
                  throw `this_$iv`.access$newTimeoutException(null);
               }
            }
         }

         @NotNull
         public AsyncTimeout timeout() {
            return this.this$0;
         }

         @NotNull
         @Override
         public java.lang.String toString() {
            return "AsyncTimeout.sink(" + this.$sink + ')';
         }
      };
   }

   public fun source(source: Source): Source {
      return new Source(this, source) {
         {
            this.this$0 = `$receiver`;
            this.$source = `$source`;
         }

         // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         @Override
         public long read(@NotNull Buffer sink, long byteCount) {
            label38: {
               var `this_$iv`: AsyncTimeout = this.this$0;
               var var5: Source = this.$source;
               this.this$0.enter();

               try {
                  try {
                     ;
                  } catch (IOException var14) {
                     throw if (!`this_$iv`.exit()) var14 as java.lang.Throwable else `this_$iv`.access$newTimeoutException(var14) as java.lang.Throwable;
                  }
               } catch (java.lang.Throwable var15) {
                  if (`this_$iv`.exit() && false) {
                     throw `this_$iv`.access$newTimeoutException(null);
                  }
               }

               if (`this_$iv`.exit()) {
                  throw `this_$iv`.access$newTimeoutException(null);
               }
            }
         }

         // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         @Override
         public void close() {
            label38: {
               var `this_$iv`: AsyncTimeout = this.this$0;
               var var2: Source = this.$source;
               this.this$0.enter();

               try {
                  try {
                     ;
                  } catch (IOException var8) {
                     throw if (!`this_$iv`.exit()) var8 as java.lang.Throwable else `this_$iv`.access$newTimeoutException(var8) as java.lang.Throwable;
                  }
               } catch (java.lang.Throwable var9) {
                  if (`this_$iv`.exit() && false) {
                     throw `this_$iv`.access$newTimeoutException(null);
                  }
               }

               if (`this_$iv`.exit()) {
                  throw `this_$iv`.access$newTimeoutException(null);
               }
            }
         }

         @NotNull
         public AsyncTimeout timeout() {
            return this.this$0;
         }

         @NotNull
         @Override
         public java.lang.String toString() {
            return "AsyncTimeout.source(" + this.$source + ')';
         }
      };
   }

   public inline fun <T> withTimeout(block: () -> T): T {
      label37: {
         this.enter();

         try {
            try {
               ;
            } catch (IOException var7) {
               throw if (!this.exit()) var7 as java.lang.Throwable else this.access$newTimeoutException(var7) as java.lang.Throwable;
            }
         } catch (java.lang.Throwable var8) {
            InlineMarker.finallyStart(1);
            if (this.exit() && false) {
               throw this.access$newTimeoutException(null);
            }

            InlineMarker.finallyEnd(1);
         }

         InlineMarker.finallyStart(1);
         if (this.exit()) {
            throw this.access$newTimeoutException(null);
         } else {
            InlineMarker.finallyEnd(1);
            var var12: Any;
            return (T)var12;
         }
      }
   }

   @PublishedApi
   internal fun `access$newTimeoutException`(cause: IOException?): IOException {
      return this.newTimeoutException(cause);
   }

   protected open fun newTimeoutException(cause: IOException?): IOException {
      var e: InterruptedIOException = new InterruptedIOException("timeout");
      if (cause != null) {
         e.initCause(cause);
      }

      return e;
   }

   @JvmStatic
   fun {
      var var10000: Condition = lock.newCondition();
      condition = var10000;
   }

   @SourceDebugExtension(["SMAP\nAsyncTimeout.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AsyncTimeout.kt\nokio/AsyncTimeout$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,331:1\n1#2:332\n*E\n"])
   companion object Companion private constructor() {
      private final val IDLE_TIMEOUT_MILLIS: Long
      private final val IDLE_TIMEOUT_NANOS: Long
      private const val TIMEOUT_WRITE_SIZE: Int
      public final val condition: Condition
      private final var head: AsyncTimeout?
      public final val lock: ReentrantLock


      private fun scheduleTimeout(node: AsyncTimeout, timeoutNanos: Long, hasDeadline: Boolean) {
         label65: {
            var var5: Lock = AsyncTimeout.Companion.getLock();
            var5.lock();

            try {
               if (AsyncTimeout.access$getInQueue$p(node)) {
                  throw new IllegalStateException("Unbalanced enter/exit".toString());
               }

               AsyncTimeout.access$setInQueue$p(node, true);
               if (AsyncTimeout.access$getHead$cp() == null) {
                  AsyncTimeout.access$setHead$cp(new AsyncTimeout());
                  new AsyncTimeout.Watchdog().start();
               }

               var now: Long = System.nanoTime();
               if (timeoutNanos != 0L && hasDeadline) {
                  AsyncTimeout.access$setTimeoutAt$p(node, now + Math.min(timeoutNanos, node.deadlineNanoTime() - now));
               } else if (timeoutNanos != 0L) {
                  AsyncTimeout.access$setTimeoutAt$p(node, now + timeoutNanos);
               } else {
                  if (!hasDeadline) {
                     throw new AssertionError();
                  }

                  AsyncTimeout.access$setTimeoutAt$p(node, node.deadlineNanoTime());
               }

               var remainingNanos: Long = AsyncTimeout.access$remainingNanos(node, now);
               var var10000: AsyncTimeout = AsyncTimeout.access$getHead$cp();
               var prev: AsyncTimeout = var10000;

               while (AsyncTimeout.access$getNext$p(prev) != null) {
                  var var10001: AsyncTimeout = AsyncTimeout.access$getNext$p(prev);
                  if (remainingNanos < AsyncTimeout.access$remainingNanos(var10001, now)) {
                     break;
                  }

                  var10000 = AsyncTimeout.access$getNext$p(prev);
                  prev = var10000;
               }

               AsyncTimeout.access$setNext$p(node, AsyncTimeout.access$getNext$p(prev));
               AsyncTimeout.access$setNext$p(prev, node);
               if (prev === AsyncTimeout.access$getHead$cp()) {
                  AsyncTimeout.Companion.getCondition().signal();
               }
            } catch (java.lang.Throwable var13) {
               var5.unlock();
            }

            var5.unlock();
         }
      }

      private fun cancelScheduledTimeout(node: AsyncTimeout): Boolean {
         label40: {
            var var2: Lock = AsyncTimeout.Companion.getLock();
            var2.lock();

            label37: {
               label36: {
                  try {
                     if (!AsyncTimeout.access$getInQueue$p(node)) {
                        break label37;
                     }

                     AsyncTimeout.access$setInQueue$p(node, false);

                     for (AsyncTimeout prev = AsyncTimeout.access$getHead$cp(); prev != null; prev = AsyncTimeout.access$getNext$p(prev)) {
                        if (AsyncTimeout.access$getNext$p(prev) === node) {
                           AsyncTimeout.access$setNext$p(prev, AsyncTimeout.access$getNext$p(node));
                           AsyncTimeout.access$setNext$p(node, null);
                           break label36;
                        }
                     }
                  } catch (java.lang.Throwable var8) {
                     var2.unlock();
                  }

                  var2.unlock();
               }

               var2.unlock();
            }

            var2.unlock();
         }
      }

      @Throws(java/lang/InterruptedException::class)
      internal fun awaitTimeout(): AsyncTimeout? {
         var var10000: AsyncTimeout = AsyncTimeout.access$getHead$cp();
         var node: AsyncTimeout = AsyncTimeout.access$getNext$p(var10000);
         if (node != null) {
            var var4: Long = AsyncTimeout.access$remainingNanos(node, System.nanoTime());
            if (var4 > 0L) {
               this.getCondition().await(var4, TimeUnit.NANOSECONDS);
               return null;
            } else {
               var10000 = AsyncTimeout.access$getHead$cp();
               AsyncTimeout.access$setNext$p(var10000, AsyncTimeout.access$getNext$p(node));
               AsyncTimeout.access$setNext$p(node, null);
               return node;
            }
         } else {
            var waitNanos: Long = System.nanoTime();
            this.getCondition().await(AsyncTimeout.access$getIDLE_TIMEOUT_MILLIS$cp(), TimeUnit.MILLISECONDS);
            var10000 = AsyncTimeout.access$getHead$cp();
            return if (AsyncTimeout.access$getNext$p(var10000) == null && System.nanoTime() - waitNanos >= AsyncTimeout.access$getIDLE_TIMEOUT_NANOS$cp())
               AsyncTimeout.access$getHead$cp()
               else
               null;
         }
      }
   }

   private class Watchdog internal constructor() : Thread("Okio Watchdog") {
      init {
         this.setDaemon(true);
      }

      public override fun run() {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.IndexOutOfBoundsException: Index -1 out of bounds for length 0
         //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
         //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
         //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
         //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
         //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
         //   at org.jetbrains.java.decompiler.util.collections.ListStack.pop(ListStack.java:31)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.<init>(InvocationExprent.java:161)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processBlock(ExprProcessor.java:494)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:134)
         //
         // Bytecode:
         // 00: nop
         // 01: nop
         // 02: aconst_null
         // 03: astore 1
         // 04: getstatic okio/AsyncTimeout.Companion Lokio/AsyncTimeout$Companion;
         // 07: invokevirtual okio/AsyncTimeout$Companion.getLock ()Ljava/util/concurrent/locks/ReentrantLock;
         // 0a: checkcast java/util/concurrent/locks/Lock
         // 0d: astore 2
         // 0e: aload 2
         // 0f: invokeinterface java/util/concurrent/locks/Lock.lock ()V 1
         // 14: nop
         // 15: bipush 0
         // 16: istore 3
         // 17: getstatic okio/AsyncTimeout.Companion Lokio/AsyncTimeout$Companion;
         // 1a: invokevirtual okio/AsyncTimeout$Companion.awaitTimeout$okio ()Lokio/AsyncTimeout;
         // 1d: astore 1
         // 1e: aload 1
         // 1f: invokestatic okio/AsyncTimeout.access$getHead$cp ()Lokio/AsyncTimeout;
         // 22: if_acmpne 35
         // 25: getstatic okio/AsyncTimeout.Companion Lokio/AsyncTimeout$Companion;
         // 28: pop
         // 29: aconst_null
         // 2a: invokestatic okio/AsyncTimeout.access$setHead$cp (Lokio/AsyncTimeout;)V
         // 2d: nop
         // 2e: aload 2
         // 2f: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
         // 34: return
         // 35: nop
         // 36: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
         // 39: astore 3
         // 3a: aload 2
         // 3b: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
         // 40: goto 4c
         // 43: astore 3
         // 44: aload 2
         // 45: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
         // 4a: aload 3
         // 4b: athrow
         // 4c: aload 1
         // 4d: dup
         // 4e: ifnull 57
         // 51: invokevirtual okio/AsyncTimeout.timedOut ()V
         // 54: goto 00
         // 57: pop
         // 58: goto 00
         // 5b: astore 1
         // 5c: goto 00
      }
   }
}
