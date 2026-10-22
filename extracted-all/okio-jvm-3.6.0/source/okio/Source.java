package okio

import java.io.Closeable

interface Source : Closeable {
   @Throws(java/io/IOException::class)
   public abstract fun read(sink: Buffer, byteCount: Long): Long {
   }

   public abstract fun timeout(): Timeout {
   }

   @Throws(java/io/IOException::class)
   public abstract override fun close() {
   }
}
