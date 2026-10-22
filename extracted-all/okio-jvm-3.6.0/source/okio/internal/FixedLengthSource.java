package okio.internal

import java.io.IOException
import okio.Buffer
import okio.ForwardingSource
import okio.Source

internal class FixedLengthSource(delegate: Source, size: Long, truncate: Boolean) : ForwardingSource(delegate) {
   private final var bytesReceived: Long
   private final val size: Long
   private final val truncate: Boolean

   init {
      this.size = size;
      this.truncate = truncate;
   }

   public override fun read(sink: Buffer, byteCount: Long): Long {
      var var10000: Long;
      if (this.bytesReceived > this.size) {
         var10000 = 0L;
      } else if (this.truncate) {
         var result: Long = this.size - this.bytesReceived;
         if (this.size - this.bytesReceived == 0L) {
            return -1L;
         }

         var10000 = Math.min(byteCount, result);
      } else {
         var10000 = byteCount;
      }

      var var8: Long = super.read(sink, var10000);
      if (var8 != -1L) {
         this.bytesReceived += var8;
      }

      if ((this.bytesReceived >= this.size || var8 != -1L) && this.bytesReceived <= this.size) {
         return var8;
      } else {
         if (var8 > 0L && this.bytesReceived > this.size) {
            this.truncateToSize(sink, sink.size() - (this.bytesReceived - this.size));
         }

         throw new IOException("expected " + this.size + " bytes but got " + this.bytesReceived);
      }
   }

   private fun Buffer.truncateToSize(newSize: Long) {
      var scratch: Buffer = new Buffer();
      scratch.writeAll(`$this$truncateToSize`);
      `$this$truncateToSize`.write(scratch, newSize);
      scratch.clear();
   }
}
