package okio

import java.io.RandomAccessFile

internal class JvmFileHandle(readWrite: Boolean, randomAccessFile: RandomAccessFile) : FileHandle(readWrite) {
   private final val randomAccessFile: RandomAccessFile

   init {
      this.randomAccessFile = randomAccessFile;
   }

   @Synchronized
   protected override fun protectedResize(size: Long) {
      var currentSize: Long = this.size();
      var delta: Long = size - currentSize;
      if (size - currentSize > 0L) {
         this.protectedWrite(currentSize, new byte[(int)delta], 0, (int)delta);
      } else {
         this.randomAccessFile.setLength(size);
      }
   }

   @Synchronized
   protected override fun protectedSize(): Long {
      return this.randomAccessFile.length();
   }

   @Synchronized
   protected override fun protectedRead(fileOffset: Long, array: ByteArray, arrayOffset: Int, byteCount: Int): Int {
      this.randomAccessFile.seek(fileOffset);
      var bytesRead: Int = 0;

      while (bytesRead < byteCount) {
         var readResult: Int = this.randomAccessFile.read(array, arrayOffset, byteCount - bytesRead);
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
      this.randomAccessFile.seek(fileOffset);
      this.randomAccessFile.write(array, arrayOffset, byteCount);
   }

   @Synchronized
   protected override fun protectedFlush() {
      this.randomAccessFile.getFD().sync();
   }

   @Synchronized
   protected override fun protectedClose() {
      this.randomAccessFile.close();
   }
}
