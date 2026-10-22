package nonapi.io.github.classgraph.fileslice.reader;

import io.github.classgraph.Resource;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.Arrays;
import nonapi.io.github.classgraph.fileslice.ArraySlice;
import nonapi.io.github.classgraph.fileslice.Slice;
import nonapi.io.github.classgraph.utils.StringUtils;

public class ClassfileReader implements RandomAccessReader, SequentialReader, Closeable {
   private Resource resourceToClose;
   private InputStream inflaterInputStream;
   private RandomAccessReader randomAccessReader;
   private byte[] arr;
   private int arrUsed;
   private int currIdx;
   private int classfileLengthHint = -1;
   private static final int INITIAL_BUF_SIZE = 16384;
   private static final int BUF_CHUNK_SIZE = 8184;

   public ClassfileReader(Slice slice, Resource resourceToClose) throws IOException {
      this.classfileLengthHint = (int)slice.sliceLength;
      this.resourceToClose = resourceToClose;
      if (slice.isDeflatedZipEntry) {
         this.inflaterInputStream = slice.open();
         this.arr = new byte[16384];
         this.classfileLengthHint = (int)Math.min(slice.inflatedLengthHint, 2147483639L);
      } else if (slice instanceof ArraySlice) {
         ArraySlice arraySlice = (ArraySlice)slice;
         if (arraySlice.sliceStartPos == 0L && arraySlice.sliceLength == (long)arraySlice.arr.length) {
            this.arr = arraySlice.arr;
         } else {
            this.arr = Arrays.copyOfRange(arraySlice.arr, (int)arraySlice.sliceStartPos, (int)(arraySlice.sliceStartPos + arraySlice.sliceLength));
         }

         this.arrUsed = this.arr.length;
         this.classfileLengthHint = this.arr.length;
      } else {
         this.randomAccessReader = slice.randomAccessReader();
         this.arr = new byte[16384];
         this.classfileLengthHint = (int)Math.min(slice.sliceLength, 2147483639L);
      }
   }

   public ClassfileReader(InputStream inputStream, Resource resourceToClose) throws IOException {
      this.inflaterInputStream = inputStream;
      this.arr = new byte[16384];
      this.resourceToClose = resourceToClose;
   }

   public int currPos() {
      return this.currIdx;
   }

   public byte[] buf() {
      return this.arr;
   }

   private void readTo(int targetArrUsed) throws IOException {
      int maxArrLen = this.classfileLengthHint == -1 ? 2147483639 : this.classfileLengthHint;
      if (this.inflaterInputStream == null && this.randomAccessReader == null) {
         throw new IOException("Tried to read past end of fixed array buffer");
      } else if (targetArrUsed <= 2147483639 && targetArrUsed >= 0 && this.arrUsed != maxArrLen) {
         int maxNewArrUsed = (int)Math.min(Math.max((long)targetArrUsed, (long)(this.arrUsed + 8184)), (long)maxArrLen);
         long newArrLength = (long)this.arr.length;

         while (newArrLength < (long)maxNewArrUsed) {
            newArrLength = Math.min((long)maxNewArrUsed, newArrLength * 2L);
         }

         if (newArrLength > 2147483639L) {
            throw new IOException("Hit 2GB limit while trying to grow buffer array");
         } else {
            this.arr = Arrays.copyOf(this.arr, (int)Math.min(newArrLength, (long)maxArrLen));
            int maxBytesToRead = this.arr.length - this.arrUsed;
            if (this.inflaterInputStream != null) {
               int numRead = this.inflaterInputStream.read(this.arr, this.arrUsed, maxBytesToRead);
               if (numRead > 0) {
                  this.arrUsed += numRead;
               }
            } else {
               int bytesToRead = Math.min(maxBytesToRead, maxArrLen - this.arrUsed);
               int numBytesRead = this.randomAccessReader.read((long)this.arrUsed, this.arr, this.arrUsed, bytesToRead);
               if (numBytesRead > 0) {
                  this.arrUsed += numBytesRead;
               }
            }

            if (this.arrUsed < targetArrUsed) {
               throw new IOException("Buffer underflow");
            }
         }
      } else {
         throw new IOException("Hit 2GB limit while trying to grow buffer array");
      }
   }

   public void bufferTo(int numBytes) throws IOException {
      if (numBytes > this.arrUsed) {
         this.readTo(numBytes);
      }
   }

   @Override
   public int read(long srcOffset, byte[] dstArr, int dstArrStart, int numBytes) throws IOException {
      if (numBytes == 0) {
         return 0;
      } else {
         int idx = (int)srcOffset;
         if (idx + numBytes > this.arrUsed) {
            this.readTo(idx + numBytes);
         }

         int numBytesToRead = Math.max(Math.min(numBytes, dstArr.length - dstArrStart), 0);
         if (numBytesToRead == 0) {
            return -1;
         } else {
            try {
               System.arraycopy(this.arr, idx, dstArr, dstArrStart, numBytesToRead);
               return numBytesToRead;
            } catch (IndexOutOfBoundsException var9) {
               throw new IOException("Read index out of bounds");
            }
         }
      }
   }

   @Override
   public int read(long srcOffset, ByteBuffer dstBuf, int dstBufStart, int numBytes) throws IOException {
      if (numBytes == 0) {
         return 0;
      } else {
         int idx = (int)srcOffset;
         if (idx + numBytes > this.arrUsed) {
            this.readTo(idx + numBytes);
         }

         int numBytesToRead = Math.max(Math.min(numBytes, dstBuf.capacity() - dstBufStart), 0);
         if (numBytesToRead == 0) {
            return -1;
         } else {
            try {
               ((Buffer)dstBuf).position(dstBufStart);
               ((Buffer)dstBuf).limit(dstBufStart + numBytesToRead);
               dstBuf.put(this.arr, idx, numBytesToRead);
               return numBytesToRead;
            } catch (IndexOutOfBoundsException | ReadOnlyBufferException | BufferUnderflowException var9) {
               throw new IOException("Read index out of bounds");
            }
         }
      }
   }

   @Override
   public byte readByte(long offset) throws IOException {
      int idx = (int)offset;
      if (idx + 1 > this.arrUsed) {
         this.readTo(idx + 1);
      }

      return this.arr[idx];
   }

   @Override
   public int readUnsignedByte(long offset) throws IOException {
      int idx = (int)offset;
      if (idx + 1 > this.arrUsed) {
         this.readTo(idx + 1);
      }

      return this.arr[idx] & 0xFF;
   }

   @Override
   public short readShort(long offset) throws IOException {
      return (short)this.readUnsignedShort(offset);
   }

   @Override
   public int readUnsignedShort(long offset) throws IOException {
      int idx = (int)offset;
      if (idx + 2 > this.arrUsed) {
         this.readTo(idx + 2);
      }

      return (this.arr[idx] & 0xFF) << 8 | this.arr[idx + 1] & 0xFF;
   }

   @Override
   public int readInt(long offset) throws IOException {
      int idx = (int)offset;
      if (idx + 4 > this.arrUsed) {
         this.readTo(idx + 4);
      }

      return (this.arr[idx] & 0xFF) << 24 | (this.arr[idx + 1] & 0xFF) << 16 | (this.arr[idx + 2] & 0xFF) << 8 | this.arr[idx + 3] & 0xFF;
   }

   @Override
   public long readUnsignedInt(long offset) throws IOException {
      return (long)this.readInt(offset) & 4294967295L;
   }

   @Override
   public long readLong(long offset) throws IOException {
      int idx = (int)offset;
      if (idx + 8 > this.arrUsed) {
         this.readTo(idx + 8);
      }

      return ((long)this.arr[idx] & 255L) << 56
         | ((long)this.arr[idx + 1] & 255L) << 48
         | ((long)this.arr[idx + 2] & 255L) << 40
         | ((long)this.arr[idx + 3] & 255L) << 32
         | ((long)this.arr[idx + 4] & 255L) << 24
         | ((long)this.arr[idx + 5] & 255L) << 16
         | ((long)this.arr[idx + 6] & 255L) << 8
         | (long)this.arr[idx + 7] & 255L;
   }

   @Override
   public byte readByte() throws IOException {
      byte val = this.readByte((long)this.currIdx);
      this.currIdx++;
      return val;
   }

   @Override
   public int readUnsignedByte() throws IOException {
      int val = this.readUnsignedByte((long)this.currIdx);
      this.currIdx++;
      return val;
   }

   @Override
   public short readShort() throws IOException {
      short val = this.readShort((long)this.currIdx);
      this.currIdx += 2;
      return val;
   }

   @Override
   public int readUnsignedShort() throws IOException {
      int val = this.readUnsignedShort((long)this.currIdx);
      this.currIdx += 2;
      return val;
   }

   @Override
   public int readInt() throws IOException {
      int val = this.readInt((long)this.currIdx);
      this.currIdx += 4;
      return val;
   }

   @Override
   public long readUnsignedInt() throws IOException {
      long val = this.readUnsignedInt((long)this.currIdx);
      this.currIdx += 4;
      return val;
   }

   @Override
   public long readLong() throws IOException {
      long val = this.readLong((long)this.currIdx);
      this.currIdx += 8;
      return val;
   }

   @Override
   public void skip(int bytesToSkip) throws IOException {
      if (bytesToSkip < 0) {
         throw new IllegalArgumentException("Tried to skip a negative number of bytes");
      } else {
         int idx = this.currIdx;
         if (idx + bytesToSkip > this.arrUsed) {
            this.readTo(idx + bytesToSkip);
         }

         this.currIdx += bytesToSkip;
      }
   }

   @Override
   public String readString(long offset, int numBytes, boolean replaceSlashWithDot, boolean stripLSemicolon) throws IOException {
      int idx = (int)offset;
      if (idx + numBytes > this.arrUsed) {
         this.readTo(idx + numBytes);
      }

      return StringUtils.readString(this.arr, idx, numBytes, replaceSlashWithDot, stripLSemicolon);
   }

   @Override
   public String readString(int numBytes, boolean replaceSlashWithDot, boolean stripLSemicolon) throws IOException {
      String val = StringUtils.readString(this.arr, this.currIdx, numBytes, replaceSlashWithDot, stripLSemicolon);
      this.currIdx += numBytes;
      return val;
   }

   @Override
   public String readString(long offset, int numBytes) throws IOException {
      return this.readString(offset, numBytes, false, false);
   }

   @Override
   public String readString(int numBytes) throws IOException {
      return this.readString(numBytes, false, false);
   }

   @Override
   public void close() {
      try {
         if (this.inflaterInputStream != null) {
            this.inflaterInputStream.close();
            this.inflaterInputStream = null;
         }

         if (this.resourceToClose != null) {
            this.resourceToClose.close();
            this.resourceToClose = null;
         }
      } catch (Exception var2) {
      }
   }
}
