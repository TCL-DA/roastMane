package okio

import java.io.OutputStream
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nJvmOkio.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JvmOkio.kt\nokio/OutputStreamSink\n+ 2 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,239:1\n86#2:240\n*S KotlinDebug\n*F\n+ 1 JvmOkio.kt\nokio/OutputStreamSink\n*L\n55#1:240\n*E\n"])
private class OutputStreamSink(out: OutputStream, timeout: Timeout) : Sink {
   private final val out: OutputStream
   private final val timeout: Timeout

   init {
      this.out = out;
      this.timeout = timeout;
   }

   public override fun write(source: Buffer, byteCount: Long) {
      -SegmentedByteString.checkOffsetAndCount(source.size(), 0L, byteCount);
      var remaining: Long = byteCount;

      while (remaining > 0L) {
         this.timeout.throwIfReached();
         var var10000: Segment = source.head;
         var toCopy: Int = (int)Math.min(remaining, (long)(var10000.limit - var10000.pos));
         this.out.write(var10000.data, var10000.pos, toCopy);
         var10000.pos += toCopy;
         remaining -= (long)toCopy;
         source.setSize$okio(source.size() - (long)toCopy);
         if (var10000.pos == var10000.limit) {
            source.head = var10000.pop();
            SegmentPool.recycle(var10000);
         }
      }
   }

   public override fun flush() {
      this.out.flush();
   }

   public override fun close() {
      this.out.close();
   }

   public override fun timeout(): Timeout {
      return this.timeout;
   }

   public override fun toString(): String {
      return "sink(" + this.out + ')';
   }
}
