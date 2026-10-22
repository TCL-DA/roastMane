package okio

import javax.crypto.Cipher
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nCipherSink.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CipherSink.kt\nokio/CipherSink\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,148:1\n1#2:149\n86#3:150\n*S KotlinDebug\n*F\n+ 1 CipherSink.kt\nokio/CipherSink\n*L\n47#1:150\n*E\n"])
class CipherSink(sink: BufferedSink, cipher: Cipher) : Sink {
   private final val blockSize: Int
   public final val cipher: Cipher
   private final var closed: Boolean
   private final val sink: BufferedSink

   init {
      this.sink = sink;
      this.cipher = cipher;
      this.blockSize = this.cipher.getBlockSize();
      if (this.blockSize <= 0) {
         throw new IllegalArgumentException(("Block cipher required " + this.cipher).toString());
      }
   }

   @Throws(java/io/IOException::class)
   public override fun write(source: Buffer, byteCount: Long) {
      -SegmentedByteString.checkOffsetAndCount(source.size(), 0L, byteCount);
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         var remaining: Long = byteCount;

         while (remaining > 0L) {
            remaining -= (long)this.update(source, remaining);
         }
      }
   }

   private fun update(source: Buffer, remaining: Long): Int {
      var var10000: Segment = source.head;
      var size: Int = (int)Math.min(remaining, (long)(var10000.limit - var10000.pos));
      var var10: Buffer = this.sink.getBuffer();

      var var11: Int;
      for (outputSize = this.cipher.getOutputSize(size); outputSize > 8192; outputSize = this.cipher.getOutputSize(size)) {
         if (size <= this.blockSize) {
            var var12: BufferedSink = this.sink;
            var var10001: ByteArray = this.cipher.update(source.readByteArray(remaining));
            var12.write(var10001);
            return (int)remaining;
         }

         size -= this.blockSize;
      }

      var s: Segment = var10.writableSegment$okio(var11);
      var ciphered: Int = this.cipher.update(var10000.data, var10000.pos, size, s.data, s.limit);
      s.limit += ciphered;
      var10.setSize$okio(var10.size() + (long)ciphered);
      if (s.pos == s.limit) {
         var10.head = s.pop();
         SegmentPool.recycle(s);
      }

      this.sink.emitCompleteSegments();
      source.setSize$okio(source.size() - (long)size);
      var10000.pos += size;
      if (var10000.pos == var10000.limit) {
         source.head = var10000.pop();
         SegmentPool.recycle(var10000);
      }

      return size;
   }

   public override fun flush() {
      this.sink.flush();
   }

   public override fun timeout(): Timeout {
      return this.sink.timeout();
   }

   @Throws(java/io/IOException::class)
   public override fun close() {
      if (!this.closed) {
         this.closed = true;
         var thrown: java.lang.Throwable = this.doFinal();

         try {
            this.sink.close();
         } catch (java.lang.Throwable var3) {
            if (thrown == null) {
               thrown = var3;
            }
         }

         if (thrown != null) {
            throw thrown;
         }
      }
   }

   private fun doFinal(): Throwable? {
      var outputSize: Int = this.cipher.getOutputSize(0);
      if (outputSize == 0) {
         return null;
      } else if (outputSize > 8192) {
         try {
            var var10000: BufferedSink = this.sink;
            var var10001: ByteArray = this.cipher.doFinal();
            var10000.write(var10001);
            return null;
         } catch (java.lang.Throwable var6) {
            return var6;
         }
      } else {
         var thrown: java.lang.Throwable = null;
         var buffer: Buffer = this.sink.getBuffer();
         var s: Segment = buffer.writableSegment$okio(outputSize);

         try {
            var e: Int = this.cipher.doFinal(s.data, s.limit);
            s.limit += e;
            buffer.setSize$okio(buffer.size() + (long)e);
         } catch (java.lang.Throwable var7) {
            thrown = var7;
         }

         if (s.pos == s.limit) {
            buffer.head = s.pop();
            SegmentPool.recycle(s);
         }

         return thrown;
      }
   }
}
