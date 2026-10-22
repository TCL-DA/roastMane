package nonapi.io.github.classgraph.fileslice;

import io.github.classgraph.Resource;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import nonapi.io.github.classgraph.fastzipfilereader.NestedJarHandler;
import nonapi.io.github.classgraph.fileslice.reader.RandomAccessReader;

public abstract class Slice implements Closeable {
   protected final NestedJarHandler nestedJarHandler;
   protected final Slice parentSlice;
   public final long sliceStartPos;
   public long sliceLength;
   public final boolean isDeflatedZipEntry;
   public final long inflatedLengthHint;
   private int hashCode;

   protected Slice(Slice parentSlice, long offset, long length, boolean isDeflatedZipEntry, long inflatedLengthHint, NestedJarHandler nestedJarHandler) {
      this.parentSlice = parentSlice;
      long parentSliceStartPos = parentSlice == null ? 0L : parentSlice.sliceStartPos;
      this.sliceStartPos = parentSliceStartPos + offset;
      this.sliceLength = length;
      this.isDeflatedZipEntry = isDeflatedZipEntry;
      this.inflatedLengthHint = inflatedLengthHint;
      this.nestedJarHandler = nestedJarHandler;
      if (this.sliceStartPos < 0L) {
         throw new IllegalArgumentException("Invalid startPos");
      } else if (length < 0L) {
         throw new IllegalArgumentException("Invalid length");
      } else if (parentSlice != null
         && (this.sliceStartPos < parentSliceStartPos || this.sliceStartPos + length > parentSliceStartPos + parentSlice.sliceLength)) {
         throw new IllegalArgumentException("Child slice is not completely contained within parent slice");
      }
   }

   protected Slice(long length, boolean isDeflatedZipEntry, long inflatedLengthHint, NestedJarHandler nestedJarHandler) {
      this(null, 0L, length, isDeflatedZipEntry, inflatedLengthHint, nestedJarHandler);
   }

   public abstract Slice slice(long var1, long var3, boolean var5, long var6);

   public InputStream open() throws IOException {
      return this.open(null);
   }

   public InputStream open(final Resource resourceToClose) throws IOException {
      InputStream rawInputStream = new InputStream() {
         RandomAccessReader randomAccessReader = Slice.this.randomAccessReader();
         private long currOff;
         private long markOff;
         private final byte[] byteBuf = new byte[1];
         private final AtomicBoolean closed = new AtomicBoolean();

         @Override
         public int read() throws IOException {
            if (this.closed.get()) {
               throw new IOException("Already closed");
            } else {
               return this.read(this.byteBuf, 0, 1);
            }
         }

         @Override
         public int read(byte[] buf, int off, int len) throws IOException {
            if (this.closed.get()) {
               throw new IOException("Already closed");
            } else if (len == 0) {
               return 0;
            } else {
               int numBytesToRead = Math.min(len, this.available());
               if (numBytesToRead < 1) {
                  return -1;
               } else {
                  int numBytesRead = this.randomAccessReader.read(this.currOff, buf, off, numBytesToRead);
                  if (numBytesRead > 0) {
                     this.currOff += (long)numBytesRead;
                  }

                  return numBytesRead;
               }
            }
         }

         @Override
         public long skip(long n) throws IOException {
            if (this.closed.get()) {
               throw new IOException("Already closed");
            } else {
               long newOff = Math.min(this.currOff + n, Slice.this.sliceLength);
               long skipped = newOff - this.currOff;
               this.currOff = newOff;
               return skipped;
            }
         }

         @Override
         public int available() {
            return (int)Math.min(Math.max(Slice.this.sliceLength - this.currOff, 0L), 2147483639L);
         }

         @Override
         public synchronized void mark(int readlimit) {
            this.markOff = this.currOff;
         }

         @Override
         public synchronized void reset() {
            this.currOff = this.markOff;
         }

         @Override
         public boolean markSupported() {
            return true;
         }

         @Override
         public void close() {
            if (resourceToClose != null) {
               try {
                  resourceToClose.close();
               } catch (Exception var2) {
               }
            }

            this.closed.getAndSet(true);
         }
      };
      return this.isDeflatedZipEntry ? this.nestedJarHandler.openInflaterInputStream(rawInputStream) : rawInputStream;
   }

   public abstract RandomAccessReader randomAccessReader();

   public abstract byte[] load() throws IOException;

   public String loadAsString() throws IOException {
      return new String(this.load(), StandardCharsets.UTF_8);
   }

   public ByteBuffer read() throws IOException {
      return ByteBuffer.wrap(this.load());
   }

   @Override
   public void close() throws IOException {
   }

   @Override
   public int hashCode() {
      if (this.hashCode == 0) {
         this.hashCode = (this.parentSlice == null ? 1 : this.parentSlice.hashCode()) ^ (int)this.sliceStartPos * 7 ^ (int)this.sliceLength * 15;
         if (this.hashCode == 0) {
            this.hashCode = 1;
         }
      }

      return this.hashCode;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Slice)) {
         return false;
      } else {
         Slice other = (Slice)o;
         return this.parentSlice == other.parentSlice && this.sliceStartPos == other.sliceStartPos && this.sliceLength == other.sliceLength;
      }
   }
}
