package okio

import java.nio.ByteBuffer
import java.nio.channels.FileChannel

internal class NioFileSystemFileHandle(readWrite: Boolean, fileChannel: FileChannel) : FileHandle(readWrite) {
   private final val fileChannel: FileChannel

   init {
      this.fileChannel = fileChannel;
   }

   @Synchronized
   protected override fun protectedResize(size: Long) {
      var currentSize: Long = this.size();
      var delta: Long = size - currentSize;
      if (size - currentSize > 0L) {
         this.protectedWrite(currentSize, new byte[(int)delta], 0, (int)delta);
      } else {
         this.fileChannel.truncate(size);
      }
   }

   @Synchronized
   protected override fun protectedSize(): Long {
      return this.fileChannel.size();
   }

   @Synchronized
   protected override fun protectedRead(fileOffset: Long, array: ByteArray, arrayOffset: Int, byteCount: Int): Int {
      this.fileChannel.position(fileOffset);
      var byteBuffer: ByteBuffer = ByteBuffer.wrap(array, arrayOffset, byteCount);
      var bytesRead: Int = 0;

      while (bytesRead < byteCount) {
         var readResult: Int = this.fileChannel.read(byteBuffer);
         if (readResult == -1) {
            if (bytesRead == 0) {
               return -1;
            }
            break;
         }

         bytesRead += readResult;
      }

      return bytesRead;
   }

   @Synchronized
   protected override fun protectedWrite(fileOffset: Long, array: ByteArray, arrayOffset: Int, byteCount: Int) {
      this.fileChannel.position(fileOffset);
      this.fileChannel.write(ByteBuffer.wrap(array, arrayOffset, byteCount));
   }

   @Synchronized
   protected override fun protectedFlush() {
      this.fileChannel.force(true);
   }

   @Synchronized
   protected override fun protectedClose() {
      this.fileChannel.close();
   }
}
