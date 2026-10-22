package nonapi.io.github.classgraph.fileslice.reader;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import nonapi.io.github.classgraph.utils.StringUtils;

public class RandomAccessByteBufferReader implements RandomAccessReader {
   private final ByteBuffer byteBuffer;
   private final int sliceStartPos;
   private final int sliceLength;

   public RandomAccessByteBufferReader(ByteBuffer byteBuffer, long sliceStartPos, long sliceLength) {
      this.byteBuffer = byteBuffer.duplicate();
      this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
      this.sliceStartPos = (int)sliceStartPos;
      this.sliceLength = (int)sliceLength;
      ((Buffer)this.byteBuffer).position(this.sliceStartPos);
      ((Buffer)this.byteBuffer).limit(this.sliceStartPos + this.sliceLength);
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
               int srcStart = (int)srcOffset;
               ((Buffer)this.byteBuffer).position(this.sliceStartPos + srcStart);
               this.byteBuffer.get(dstArr, dstArrStart, numBytesToRead);
               ((Buffer)this.byteBuffer).position(this.sliceStartPos);
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
               ((Buffer)this.byteBuffer).position(srcStart);
               ((Buffer)dstBuf).position(dstBufStart);
               ((Buffer)dstBuf).limit(dstBufStart + numBytesToRead);
               dstBuf.put(this.byteBuffer);
               ((Buffer)this.byteBuffer).limit(this.sliceStartPos + this.sliceLength);
               ((Buffer)this.byteBuffer).position(this.sliceStartPos);
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
      int idx = (int)((long)this.sliceStartPos + offset);
      return this.byteBuffer.get(idx);
   }

   @Override
   public int readUnsignedByte(long offset) throws IOException {
      int idx = (int)((long)this.sliceStartPos + offset);
      return this.byteBuffer.get(idx) & 0xFF;
   }

   @Override
   public int readUnsignedShort(long offset) throws IOException {
      int idx = (int)((long)this.sliceStartPos + offset);
      return this.byteBuffer.getShort(idx) & 0xFF;
   }

   @Override
   public short readShort(long offset) throws IOException {
      return (short)this.readUnsignedShort(offset);
   }

   @Override
   public int readInt(long offset) throws IOException {
      int idx = (int)((long)this.sliceStartPos + offset);
      return this.byteBuffer.getInt(idx);
   }

   @Override
   public long readUnsignedInt(long offset) throws IOException {
      return (long)this.readInt(offset) & 4294967295L;
   }

   @Override
   public long readLong(long offset) throws IOException {
      int idx = (int)((long)this.sliceStartPos + offset);
      return this.byteBuffer.getLong(idx);
   }

   @Override
   public String readString(long offset, int numBytes, boolean replaceSlashWithDot, boolean stripLSemicolon) throws IOException {
      int idx = (int)((long)this.sliceStartPos + offset);
      byte[] arr = new byte[numBytes];
      if (this.read(offset, arr, 0, numBytes) < numBytes) {
         throw new IOException("Premature EOF while reading string");
      } else {
         return StringUtils.readString(arr, idx, numBytes, replaceSlashWithDot, stripLSemicolon);
      }
   }

   @Override
   public String readString(long offset, int numBytes) throws IOException {
      return this.readString(offset, numBytes, false, false);
   }
}
