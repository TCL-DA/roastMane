package okhttp3.internal.cache2

import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel
import okio.Buffer

internal class FileOperator(fileChannel: FileChannel) {
   private final val fileChannel: FileChannel

   init {
      this.fileChannel = fileChannel;
   }

   @Throws(java/io/IOException::class)
   public fun write(pos: Long, source: Buffer, byteCount: Long) {
      if (byteCount >= 0L && byteCount <= source.size()) {
         var mutablePos: Long = pos;
         var mutableByteCount: Long = byteCount;

         while (mutableByteCount > 0L) {
            var bytesWritten: Long = this.fileChannel.transferFrom(source as ReadableByteChannel, mutablePos, mutableByteCount);
            mutablePos += bytesWritten;
            mutableByteCount -= bytesWritten;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public fun read(pos: Long, sink: Buffer, byteCount: Long) {
      if (byteCount < 0L) {
         throw new IndexOutOfBoundsException();
      } else {
         var mutablePos: Long = pos;
         var mutableByteCount: Long = byteCount;

         while (mutableByteCount > 0L) {
            var bytesRead: Long = this.fileChannel.transferTo(mutablePos, mutableByteCount, sink as WritableByteChannel);
            mutablePos += bytesRead;
            mutableByteCount -= bytesRead;
         }
      }
   }
}
