package okhttp3.internal.ws

import java.io.Closeable
import java.util.zip.Deflater
import okio.Buffer
import okio.ByteString
import okio.DeflaterSink
import okio.Sink
import okio.Buffer.UnsafeCursor

class MessageDeflater(noContextTakeover: Boolean) : Closeable {
   private final val deflatedBytes: Buffer
   private final val deflater: Deflater
   private final val deflaterSink: DeflaterSink
   private final val noContextTakeover: Boolean

   init {
      this.noContextTakeover = noContextTakeover;
      this.deflatedBytes = new Buffer();
      this.deflater = new Deflater(-1, true);
      this.deflaterSink = new DeflaterSink(this.deflatedBytes as Sink, this.deflater);
   }

   @Throws(java/io/IOException::class)
   public fun deflate(buffer: Buffer) {
      if (this.deflatedBytes.size() != 0L) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      } else {
         label57: {
            if (this.noContextTakeover) {
               this.deflater.reset();
            }

            this.deflaterSink.write(buffer, buffer.size());
            this.deflaterSink.flush();
            if (this.endsWith(this.deflatedBytes, MessageDeflaterKt.access$getEMPTY_DEFLATE_BLOCK$p())) {
               var newSize: Long = this.deflatedBytes.size() - (long)4;
               var var4: Closeable = Buffer.readAndWriteUnsafe$default(this.deflatedBytes, null, 1, null) as Closeable;
               var var5: java.lang.Throwable = null;

               try {
                  try {
                     var var13: Long = (var4 as UnsafeCursor).resizeBuffer(newSize);
                  } catch (java.lang.Throwable var9) {
                     var5 = var9;
                     throw var9;
                  }
               } catch (java.lang.Throwable var10) {
                  CloseableKt.closeFinally(var4, var5);
               }

               CloseableKt.closeFinally(var4, null);
            } else {
               this.deflatedBytes.writeByte(0);
            }

            buffer.write(this.deflatedBytes, this.deflatedBytes.size());
         }
      }
   }

   @Throws(java/io/IOException::class)
   public override fun close() {
      this.deflaterSink.close();
   }

   private fun Buffer.endsWith(suffix: ByteString): Boolean {
      return `$this$endsWith`.rangeEquals(`$this$endsWith`.size() - (long)suffix.size(), suffix);
   }
}
