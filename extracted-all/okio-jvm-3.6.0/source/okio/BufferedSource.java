package okio

import java.io.InputStream
import java.nio.channels.ReadableByteChannel
import java.nio.charset.Charset

sealed interface BufferedSource : Source, ReadableByteChannel {
   public val buffer: Buffer

   @Deprecated(message = "moved to val: use getBuffer() instead", replaceWith = @ReplaceWith(expression = "buffer", imports = []), level = DeprecationLevel.WARNING)
   public abstract fun buffer(): Buffer {
   }

   @Throws(java/io/IOException::class)
   public abstract fun exhausted(): Boolean {
   }

   @Throws(java/io/IOException::class)
   public abstract fun require(byteCount: Long) {
   }

   @Throws(java/io/IOException::class)
   public abstract fun request(byteCount: Long): Boolean {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readByte(): Byte {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readShort(): Short {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readShortLe(): Short {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readInt(): Int {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readIntLe(): Int {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readLong(): Long {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readLongLe(): Long {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readDecimalLong(): Long {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readHexadecimalUnsignedLong(): Long {
   }

   @Throws(java/io/IOException::class)
   public abstract fun skip(byteCount: Long) {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readByteString(): ByteString {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readByteString(byteCount: Long): ByteString {
   }

   @Throws(java/io/IOException::class)
   public abstract fun select(options: Options): Int {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readByteArray(): ByteArray {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readByteArray(byteCount: Long): ByteArray {
   }

   @Throws(java/io/IOException::class)
   public abstract fun read(sink: ByteArray): Int {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readFully(sink: ByteArray) {
   }

   @Throws(java/io/IOException::class)
   public abstract fun read(sink: ByteArray, offset: Int, byteCount: Int): Int {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readFully(sink: Buffer, byteCount: Long) {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readAll(sink: Sink): Long {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readUtf8(): String {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readUtf8(byteCount: Long): String {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readUtf8Line(): String? {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readUtf8LineStrict(): String {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readUtf8LineStrict(limit: Long): String {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readUtf8CodePoint(): Int {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readString(charset: Charset): String {
   }

   @Throws(java/io/IOException::class)
   public abstract fun readString(byteCount: Long, charset: Charset): String {
   }

   @Throws(java/io/IOException::class)
   public abstract fun indexOf(b: Byte): Long {
   }

   @Throws(java/io/IOException::class)
   public abstract fun indexOf(b: Byte, fromIndex: Long): Long {
   }

   @Throws(java/io/IOException::class)
   public abstract fun indexOf(b: Byte, fromIndex: Long, toIndex: Long): Long {
   }

   @Throws(java/io/IOException::class)
   public abstract fun indexOf(bytes: ByteString): Long {
   }

   @Throws(java/io/IOException::class)
   public abstract fun indexOf(bytes: ByteString, fromIndex: Long): Long {
   }

   @Throws(java/io/IOException::class)
   public abstract fun indexOfElement(targetBytes: ByteString): Long {
   }

   @Throws(java/io/IOException::class)
   public abstract fun indexOfElement(targetBytes: ByteString, fromIndex: Long): Long {
   }

   @Throws(java/io/IOException::class)
   public abstract fun rangeEquals(offset: Long, bytes: ByteString): Boolean {
   }

   @Throws(java/io/IOException::class)
   public abstract fun rangeEquals(offset: Long, bytes: ByteString, bytesOffset: Int, byteCount: Int): Boolean {
   }

   public abstract fun peek(): BufferedSource {
   }

   public abstract fun inputStream(): InputStream {
   }
}
