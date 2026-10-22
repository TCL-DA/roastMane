package okio

abstract class ForwardingSource : Source {
   public final val delegate: Source

   open fun ForwardingSource(delegate: Source) {
      this.delegate = delegate;
   }

   @Throws(java/io/IOException::class)
   public override fun read(sink: Buffer, byteCount: Long): Long {
      return this.delegate.read(sink, byteCount);
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
   public fun delegate(): Source {
      return this.delegate;
   }
}
