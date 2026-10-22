package okio

import java.io.OutputStream
import java.nio.channels.WritableByteChannel
import java.nio.charset.Charset

sealed interface BufferedSink : Sink, WritableByteChannel {
   public val buffer: Buffer

   @Deprecated(message = "moved to val: use getBuffer() instead", replaceWith = @ReplaceWith(expression = "buffer", imports = []), level = DeprecationLevel.WARNING)
   public abstract fun buffer(): Buffer {
   }

   @Throws(java/io/IOException::class)
   public abstract fun write(byteString: ByteString): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun write(byteString: ByteString, offset: Int, byteCount: Int): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun write(source: ByteArray): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun write(source: ByteArray, offset: Int, byteCount: Int): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun writeAll(source: Source): Long {
   }

   @Throws(java/io/IOException::class)
   public abstract fun write(source: Source, byteCount: Long): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun writeUtf8(string: String): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun writeUtf8(string: String, beginIndex: Int, endIndex: Int): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun writeUtf8CodePoint(codePoint: Int): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun writeString(string: String, charset: Charset): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun writeString(string: String, beginIndex: Int, endIndex: Int, charset: Charset): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun writeByte(b: Int): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun writeShort(s: Int): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun writeShortLe(s: Int): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun writeInt(i: Int): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun writeIntLe(i: Int): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun writeLong(v: Long): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun writeLongLe(v: Long): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun writeDecimalLong(v: Long): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun writeHexadecimalUnsignedLong(v: Long): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract override fun flush() {
   }

   @Throws(java/io/IOException::class)
   public abstract fun emit(): BufferedSink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun emitCompleteSegments(): BufferedSink {
   }

   public abstract fun outputStream(): OutputStream {
   }
}
