package okio

import javax.crypto.Cipher
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nCipherSource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CipherSource.kt\nokio/CipherSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,120:1\n1#2:121\n*E\n"])
class CipherSource(source: BufferedSource, cipher: Cipher) : Source {
   private final val blockSize: Int
   private final val buffer: Buffer
   public final val cipher: Cipher
   private final var closed: Boolean
   private final var final: Boolean
   private final val source: BufferedSource

   init {
      this.source = source;
      this.cipher = cipher;
      this.blockSize = this.cipher.getBlockSize();
      this.buffer = new Buffer();
      if (this.blockSize <= 0) {
         throw new IllegalArgumentException(("Block cipher required " + this.cipher).toString());
      }
   }

   @Throws(java/io/IOException::class)
   public override fun read(sink: Buffer, byteCount: Long): Long {
      if (byteCount < 0L) {
         throw new IllegalArgumentException(("byteCount < 0: " + byteCount).toString());
      } else if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else if (byteCount == 0L) {
         return 0L;
      } else {
         this.refill();
         return this.buffer.read(sink, byteCount);
      }
   }

   private fun refill() {
      while (this.buffer.size() == 0L && !this.final) {
         if (this.source.exhausted()) {
            this.final = true;
            this.doFinal();
            break;
         }

         this.update();
      }
   }

   private fun update() {
      var var10000: Segment = this.source.getBuffer().head;
      var size: Int = var10000.limit - var10000.pos;

      var outputSize: Int;
      for (outputSize = this.cipher.getOutputSize(var10000.limit - var10000.pos); outputSize > 8192; outputSize = this.cipher.getOutputSize(size)) {
         if (size <= this.blockSize) {
            this.final = true;
            var var7: Buffer = this.buffer;
            var var10001: ByteArray = this.cipher.doFinal(this.source.readByteArray());
            var7.write(var10001);
            return;
         }

         size -= this.blockSize;
      }

      var s: Segment = this.buffer.writableSegment$okio(outputSize);
      var ciphered: Int = this.cipher.update(var10000.data, var10000.pos, size, s.data, s.pos);
      this.source.skip((long)size);
      s.limit += ciphered;
      this.buffer.setSize$okio(this.buffer.size() + (long)ciphered);
      if (s.pos == s.limit) {
         this.buffer.head = s.pop();
         SegmentPool.recycle(s);
      }
   }

   private fun doFinal() {
      var outputSize: Int = this.cipher.getOutputSize(0);
      if (outputSize != 0) {
         var s: Segment = this.buffer.writableSegment$okio(outputSize);
         var ciphered: Int = this.cipher.doFinal(s.data, s.pos);
         s.limit += ciphered;
         this.buffer.setSize$okio(this.buffer.size() + (long)ciphered);
         if (s.pos == s.limit) {
            this.buffer.head = s.pop();
            SegmentPool.recycle(s);
         }
      }
   }

   public override fun timeout(): Timeout {
      return this.source.timeout();
   }

   @Throws(java/io/IOException::class)
   public override fun close() {
      this.closed = true;
      this.source.close();
   }
}
