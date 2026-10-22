package okhttp3.internal.ws

import java.io.Closeable
import java.util.zip.Inflater
import okio.Buffer
import okio.InflaterSource
import okio.Source

class MessageInflater(noContextTakeover: Boolean) : Closeable {
   private final val deflatedBytes: Buffer
   private final val inflater: Inflater
   private final val inflaterSource: InflaterSource
   private final val noContextTakeover: Boolean

   init {
      this.noContextTakeover = noContextTakeover;
      this.deflatedBytes = new Buffer();
      this.inflater = new Inflater(true);
      this.inflaterSource = new InflaterSource(this.deflatedBytes as Source, this.inflater);
   }

   @Throws(java/io/IOException::class)
   public fun inflate(buffer: Buffer) {
      if (this.deflatedBytes.size() != 0L) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      } else {
         if (this.noContextTakeover) {
            this.inflater.reset();
         }

         this.deflatedBytes.writeAll(buffer as Source);
         this.deflatedBytes.writeInt(65535);
         var totalBytesToRead: Long = this.inflater.getBytesRead() + this.deflatedBytes.size();

         do {
            this.inflaterSource.readOrInflate(buffer, java.lang.Long.MAX_VALUE);
         } while (this.inflater.getBytesRead() < totalBytesToRead);
      }
   }

   @Throws(java/io/IOException::class)
   public override fun close() {
      this.inflaterSource.close();
   }
}
