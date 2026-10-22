package okio

import java.util.concurrent.TimeUnit

open class ForwardingTimeout(delegate: Timeout) : Timeout {
   public final var delegate: Timeout
      internal set

   init {
      this.delegate = delegate;
   }

   public fun setDelegate(delegate: Timeout): ForwardingTimeout {
      this.delegate = delegate;
      return this;
   }

   public override fun timeout(timeout: Long, unit: TimeUnit): Timeout {
      return this.delegate.timeout(timeout, unit);
   }

   public override fun timeoutNanos(): Long {
      return this.delegate.timeoutNanos();
   }

   public override fun hasDeadline(): Boolean {
      return this.delegate.hasDeadline();
   }

   public override fun deadlineNanoTime(): Long {
      return this.delegate.deadlineNanoTime();
   }

   public override fun deadlineNanoTime(deadlineNanoTime: Long): Timeout {
      return this.delegate.deadlineNanoTime(deadlineNanoTime);
   }

   public override fun clearTimeout(): Timeout {
      return this.delegate.clearTimeout();
   }

   public override fun clearDeadline(): Timeout {
      return this.delegate.clearDeadline();
   }

   @Throws(java/io/IOException::class)
   public override fun throwIfReached() {
      this.delegate.throwIfReached();
   }
}
