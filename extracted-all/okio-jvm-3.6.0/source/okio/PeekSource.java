package okio

import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nPeekSource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PeekSource.kt\nokio/PeekSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,74:1\n1#2:75\n*E\n"])
internal class PeekSource(upstream: BufferedSource) : Source {
   private final val buffer: Buffer
   private final var closed: Boolean
   private final var expectedPos: Int
   private final var expectedSegment: Segment?
   private final var pos: Long
   private final val upstream: BufferedSource

   init {
      this.upstream = upstream;
      this.buffer = this.upstream.getBuffer();
      this.expectedSegment = this.buffer.head;
      this.expectedPos = if (this.buffer.head != null) this.buffer.head.pos else -1;
   }

   public override fun read(sink: Buffer, byteCount: Long): Long {
      if (byteCount < 0L) {
         throw new IllegalArgumentException(("byteCount < 0: " + byteCount).toString());
      } else if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         var var11: Int;
         label65: {
            label47:
            if (this.expectedSegment != null) {
               if (this.expectedSegment === this.buffer.head) {
                  var11 = this.expectedPos;
                  var var10001: Segment = this.buffer.head;
                  if (var11 == var10001.pos) {
                     break label47;
                  }
               }

               var11 = 0;
               break label65;
            }

            var11 = 1;
         }

         if (!var11) {
            throw new IllegalStateException("Peek source is invalid because upstream source was used".toString());
         } else if (byteCount == 0L) {
            return 0L;
         } else if (!this.upstream.request(this.pos + 1L)) {
            return -1L;
         } else {
            if (this.expectedSegment == null && this.buffer.head != null) {
               this.expectedSegment = this.buffer.head;
               var var12: Segment = this.buffer.head;
               this.expectedPos = var12.pos;
            }

            var toCopy: Long = Math.min(byteCount, this.buffer.size() - this.pos);
            this.buffer.copyTo(sink, this.pos, toCopy);
            this.pos += toCopy;
            return toCopy;
         }
      }
   }

   public override fun timeout(): Timeout {
      return this.upstream.timeout();
   }

   public override fun close() {
      this.closed = true;
   }
}
