package okio

import java.io.IOException
import java.io.InputStream
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nJvmOkio.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JvmOkio.kt\nokio/InputStreamSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,239:1\n1#2:240\n86#3:241\n*S KotlinDebug\n*F\n+ 1 JvmOkio.kt\nokio/InputStreamSource\n*L\n92#1:241\n*E\n"])
private open class InputStreamSource(input: InputStream, timeout: Timeout) : Source {
   private final val input: InputStream
   private final val timeout: Timeout

   init {
      this.input = input;
      this.timeout = timeout;
   }

   public override fun read(sink: Buffer, byteCount: Long): Long {
      if (byteCount == 0L) {
         return 0L;
      } else if (byteCount < 0L) {
         throw new IllegalArgumentException(("byteCount < 0: " + byteCount).toString());
      } else {
         try {
            this.timeout.throwIfReached();
            var e: Segment = sink.writableSegment$okio(1);
            var var11: Int = this.input.read(e.data, e.limit, (int)Math.min(byteCount, (long)(8192 - e.limit)));
            if (var11 == -1) {
               if (e.pos == e.limit) {
                  sink.head = e.pop();
                  SegmentPool.recycle(e);
               }

               return -1L;
            } else {
               e.limit += var11;
               sink.setSize$okio(sink.size() + (long)var11);
               return (long)var11;
            }
         } catch (AssertionError var8) {
            if (Okio.isAndroidGetsocknameError(var8)) {
               throw new IOException(var8);
            } else {
               throw var8;
            }
         }
      }
   }

   public override fun close() {
      this.input.close();
   }

   public override fun timeout(): Timeout {
      return this.timeout;
   }

   public override fun toString(): String {
      return "source(" + this.input + ')';
   }
}
