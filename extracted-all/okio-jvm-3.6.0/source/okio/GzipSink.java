package okio

import java.util.zip.CRC32
import java.util.zip.Deflater
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nGzipSink.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GzipSink.kt\nokio/GzipSink\n+ 2 RealBufferedSink.kt\nokio/RealBufferedSink\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,153:1\n51#2:154\n1#3:155\n86#4:156\n*S KotlinDebug\n*F\n+ 1 GzipSink.kt\nokio/GzipSink\n*L\n63#1:154\n131#1:156\n*E\n"])
class GzipSink(sink: Sink) : Sink {
   private final var closed: Boolean
   private final val crc: CRC32
   public final val deflater: Deflater
      public final get() {
         return this.deflater;
      }

   private final val deflaterSink: DeflaterSink
   private final val sink: RealBufferedSink

   init {
      this.sink = new RealBufferedSink(sink);
      this.deflater = new Deflater(-1, true);
      this.deflaterSink = new DeflaterSink(this.sink, this.deflater);
      this.crc = new CRC32();
      var var5: Buffer = this.sink.bufferField;
      this.sink.bufferField.writeShort(8075);
      var5.writeByte(8);
      var5.writeByte(0);
      var5.writeInt(0);
      var5.writeByte(0);
      var5.writeByte(0);
   }

   @Throws(java/io/IOException::class)
   public override fun write(source: Buffer, byteCount: Long) {
      if (byteCount < 0L) {
         throw new IllegalArgumentException(("byteCount < 0: " + byteCount).toString());
      } else if (byteCount != 0L) {
         this.updateCrc(source, byteCount);
         this.deflaterSink.write(source, byteCount);
      }
   }

   @Throws(java/io/IOException::class)
   public override fun flush() {
      this.deflaterSink.flush();
   }

   public override fun timeout(): Timeout {
      return this.sink.timeout();
   }

   @Throws(java/io/IOException::class)
   public override fun close() {
      if (!this.closed) {
         var thrown: java.lang.Throwable = null;

         try {
            this.deflaterSink.finishDeflate$okio();
            this.writeFooter();
         } catch (java.lang.Throwable var3) {
            thrown = var3;
         }

         try {
            this.deflater.end();
         } catch (java.lang.Throwable var5) {
            if (thrown == null) {
               thrown = var5;
            }
         }

         try {
            this.sink.close();
         } catch (java.lang.Throwable var4) {
            if (thrown == null) {
               thrown = var4;
            }
         }

         this.closed = true;
         if (thrown != null) {
            throw thrown;
         }
      }
   }

   private fun writeFooter() {
      this.sink.writeIntLe((int)this.crc.getValue());
      this.sink.writeIntLe((int)this.deflater.getBytesRead());
   }

   private fun updateCrc(buffer: Buffer, byteCount: Long) {
      var var10000: Segment = buffer.head;
      var head: Segment = var10000;
      var remaining: Long = byteCount;

      while (remaining > 0L) {
         var segmentLength: Int = (int)Math.min(remaining, (long)(head.limit - head.pos));
         this.crc.update(head.data, head.pos, segmentLength);
         remaining -= (long)segmentLength;
         var10000 = head.next;
         head = var10000;
      }
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "deflater", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_deflater")
   public fun deflater(): Deflater {
      return this.deflater;
   }
}
