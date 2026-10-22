package okio

private class BlackholeSink : Sink {
   public override fun write(source: Buffer, byteCount: Long) {
      source.skip(byteCount);
   }

   public override fun flush() {
   }

   public override fun timeout(): Timeout {
      return Timeout.NONE;
   }

   public override fun close() {
   }
}
