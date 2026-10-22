package nonapi.io.github.classgraph.fileslice.reader;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import nonapi.io.github.classgraph.utils.StringUtils;

public class RandomAccessArrayReader implements RandomAccessReader {
   private final byte[] arr;
   private final int sliceStartPos;
   private final int sliceLength;

   public RandomAccessArrayReader(byte[] arr, int sliceStartPos, int sliceLength) {
      this.arr = arr;
      this.sliceStartPos = sliceStartPos;
      this.sliceLength = sliceLength;
   }

   @Override
   public int read(long srcOffset, byte[] dstArr, int dstArrStart, int numBytes) throws IOException {
      if (numBytes == 0) {
         return 0;
      } else if (srcOffset >= 0L && numBytes >= 0 && (long)numBytes <= (long)this.sliceLength - srcOffset) {
         try {
            int numBytesToRead = Math.max(Math.min(numBytes, dstArr.length - dstArrStart), 0);
            if (numBytesToRead == 0) {
               return -1;
            } else {
               int srcStart = (int)((long)this.sliceStartPos + srcOffset);
               System.arraycopy(this.arr, srcStart, dstArr, dstArrStart, numBytesToRead);
               return numBytesToRead;
            }
         } catch (IndexOutOfBoundsException var8) {
            throw new IOException("Read index out of bounds");
         }
      } else {
         throw new IOException("Read index out of bounds");
      }
   }

   @Override
   public int read(long srcOffset, ByteBuffer dstBuf, int dstBufStart, int numBytes) throws IOException {
      if (numBytes == 0) {
         return 0;
      } else if (srcOffset >= 0L && numBytes >= 0 && (long)numBytes <= (long)this.sliceLength - srcOffset) {
         try {
            int numBytesToRead = Math.max(Math.min(numBytes, dstBuf.capacity() - dstBufStart), 0);
            if (numBytesToRead == 0) {
               return -1;
            } else {
               int srcStart = (int)((long)this.sliceStartPos + srcOffset);
               ((Buffer)dstBuf).position(dstBufStart);
               ((Buffer)dstBuf).limit(dstBufStart + numBytesToRead);
               dstBuf.put(this.arr, srcStart, numBytesToRead);
               return numBytesToRead;
            }
         } catch (IndexOutOfBoundsException | ReadOnlyBufferException | BufferUnderflowException var8) {
            throw new IOException("Read index out of bounds");
         }
      } else {
         throw new IOException("Read index out of bounds");
      }
   }

   @Override
   public byte readByte(long offset) throws IOException {
      int idx = this.sliceStartPos + (int)offset;
      return this.arr[idx];
   }

   @Override
   public int readUnsignedByte(long offset) throws IOException {
      int idx = this.sliceStartPos + (int)offset;
      return this.arr[idx] & 0xFF;
   }

   @Override
   public short readShort(long offset) throws IOException {
      return (short)this.readUnsignedShort(offset);
   }

   @Override
   public int readUnsignedShort(long offset) throws IOException {
      int idx = this.sliceStartPos + (int)offset;
      return (this.arr[idx + 1] & 0xFF) << 8 | this.arr[idx] & 0xFF;
   }

   @Override
   public int readInt(long offset) throws IOException {
      int idx = this.sliceStartPos + (int)offset;
      return (this.arr[idx + 3] & 0xFF) << 24 | (this.arr[idx + 2] & 0xFF) << 16 | (this.arr[idx + 1] & 0xFF) << 8 | this.arr[idx] & 0xFF;
   }

   @Override
   public long readUnsignedInt(long offset) throws IOException {
      return (long)this.readInt(offset) & 4294967295L;
   }

   @Override
   public long readLong(long offset) throws IOException {
      int idx = this.sliceStartPos + (int)offset;
      return ((long)this.arr[idx + 7] & 255L) << 56
         | ((long)this.arr[idx + 6] & 255L) << 48
         | ((long)this.arr[idx + 5] & 255L) << 40
         | ((long)this.arr[idx + 4] & 255L) << 32
         | ((long)this.arr[idx + 3] & 255L) << 24
         | ((long)this.arr[idx + 2] & 255L) << 16
         | ((long)this.arr[idx + 1] & 255L) << 8
         | (long)this.arr[idx] & 255L;
   }

   @Override
   public String readString(long offset, int numBytes, boolean replaceSlashWithDot, boolean stripLSemicolon) throws IOException {
      int idx = this.sliceStartPos + (int)offset;
      return StringUtils.readString(this.arr, idx, numBytes, replaceSlashWithDot, stripLSemicolon);
   }

   @Override
   public String readString(long offset, int numBytes) throws IOException {
      return this.readString(offset, numBytes, false, false);
   }
}
