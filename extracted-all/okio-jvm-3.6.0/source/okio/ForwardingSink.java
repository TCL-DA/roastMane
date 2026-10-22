package okio

abstract class ForwardingSink : Sink {
   public final val delegate: Sink

   open fun ForwardingSink(delegate: Sink) {
      this.delegate = delegate;
   }

   @Throws(java/io/IOException::class)
   public override fun write(source: Buffer, byteCount: Long) {
      this.delegate.write(source, byteCount);
   }

   @Throws(java/io/IOException::class)
   public override fun flush() {
      this.delegate.flush();
   }

   public override fun timeout(): Timeout {
      return this.delegate.timeout();
   }

   @Throws(java/io/IOException::class)
   public override fun close() {
      this.delegate.close();
   }

   public override fun toString(): String {
      return this.getClass().getSimpleName() + '(' + this.delegate + ')';
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "delegate", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_delegate")
   public fun delegate(): Sink {
      return this.delegate;
   }
}
