package nonapi.io.github.classgraph.fileslice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.atomic.AtomicBoolean;
import nonapi.io.github.classgraph.fastzipfilereader.NestedJarHandler;
import nonapi.io.github.classgraph.fileslice.reader.RandomAccessByteBufferReader;
import nonapi.io.github.classgraph.fileslice.reader.RandomAccessFileChannelReader;
import nonapi.io.github.classgraph.fileslice.reader.RandomAccessReader;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public class FileSlice extends Slice {
   public final File file;
   public RandomAccessFile raf;
   private final long fileLength;
   private FileChannel fileChannel;
   private ByteBuffer backingByteBuffer;
   private final boolean isTopLevelFileSlice;
   private final AtomicBoolean isClosed = new AtomicBoolean();

   private FileSlice(FileSlice parentSlice, long offset, long length, boolean isDeflatedZipEntry, long inflatedLengthHint, NestedJarHandler nestedJarHandler) {
      super(parentSlice, offset, length, isDeflatedZipEntry, inflatedLengthHint, nestedJarHandler);
      this.file = parentSlice.file;
      this.raf = parentSlice.raf;
      this.fileChannel = parentSlice.fileChannel;
      this.fileLength = parentSlice.fileLength;
      this.isTopLevelFileSlice = false;
      if (parentSlice.backingByteBuffer != null) {
         this.backingByteBuffer = parentSlice.backingByteBuffer.duplicate();
         ((Buffer)this.backingByteBuffer).position((int)this.sliceStartPos);
         ((Buffer)this.backingByteBuffer).limit((int)(this.sliceStartPos + this.sliceLength));
      }
   }

   public FileSlice(File file, boolean isDeflatedZipEntry, long inflatedLengthHint, NestedJarHandler nestedJarHandler, LogNode log) throws IOException {
      super(file.length(), isDeflatedZipEntry, inflatedLengthHint, nestedJarHandler);
      FileUtils.checkCanReadAndIsFile(file);
      this.file = file;
      this.raf = new RandomAccessFile(file, "r");
      this.fileChannel = this.raf.getChannel();
      this.fileLength = file.length();
      this.isTopLevelFileSlice = true;
      if (nestedJarHandler.scanSpec.enableMemoryMapping) {
         try {
            this.backingByteBuffer = this.fileChannel.map(MapMode.READ_ONLY, 0L, this.fileLength);
         } catch (OutOfMemoryError | IOException var10) {
            System.gc();
            nestedJarHandler.runFinalizationMethod();

            try {
               this.backingByteBuffer = this.fileChannel.map(MapMode.READ_ONLY, 0L, this.fileLength);
            } catch (OutOfMemoryError | IOException var9) {
               if (log != null) {
                  log.log("File " + file + " cannot be memory mapped: " + var9 + " (using RandomAccessFile API instead)");
               }
            }
         }
      }

      nestedJarHandler.markSliceAsOpen(this);
   }

   public FileSlice(File file, NestedJarHandler nestedJarHandler, LogNode log) throws IOException {
      this(file, false, 0L, nestedJarHandler, log);
   }

   @Override
   public Slice slice(long offset, long length, boolean isDeflatedZipEntry, long inflatedLengthHint) {
      if (this.isDeflatedZipEntry) {
         throw new IllegalArgumentException("Cannot slice a deflated zip entry");
      } else {
         return new FileSlice(this, offset, length, isDeflatedZipEntry, inflatedLengthHint, this.nestedJarHandler);
      }
   }

   @Override
   public RandomAccessReader randomAccessReader() {
      return (RandomAccessReader)(this.backingByteBuffer == null
         ? new RandomAccessFileChannelReader(this.fileChannel, this.sliceStartPos, this.sliceLength)
         : new RandomAccessByteBufferReader(this.backingByteBuffer, this.sliceStartPos, this.sliceLength));
   }

   @Override
   public byte[] load() throws IOException {
      if (!this.isDeflatedZipEntry) {
         if (this.sliceLength > 2147483639L) {
            throw new IOException("File is larger than 2GB");
         } else {
            RandomAccessReader reader = this.randomAccessReader();
            byte[] content = new byte[(int)this.sliceLength];
            if (reader.read(0L, content, 0, content.length) < content.length) {
               throw new IOException("File is truncated");
            } else {
               return content;
            }
         }
      } else if (this.inflatedLengthHint > 2147483639L) {
         throw new IOException("Uncompressed size is larger than 2GB");
      } else {
         byte[] var3;
         try (InputStream inputStream = this.open()) {
            var3 = NestedJarHandler.readAllBytesAsArray(inputStream, this.inflatedLengthHint);
         }

         return var3;
      }
   }

   @Override
   public ByteBuffer read() throws IOException {
      if (this.isDeflatedZipEntry) {
         if (this.inflatedLengthHint > 2147483639L) {
            throw new IOException("Uncompressed size is larger than 2GB");
         } else {
            return ByteBuffer.wrap(this.load());
         }
      } else if (this.backingByteBuffer == null) {
         if (this.sliceLength > 2147483639L) {
            throw new IOException("File is larger than 2GB");
         } else {
            return ByteBuffer.wrap(this.load());
         }
      } else {
         return this.backingByteBuffer.duplicate();
      }
   }

   @Override
   public boolean equals(Object o) {
      return super.equals(o);
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   @Override
   public void close() {
      if (!this.isClosed.getAndSet(true)) {
         if (this.isTopLevelFileSlice && this.backingByteBuffer != null) {
            this.nestedJarHandler.closeDirectByteBuffer(this.backingByteBuffer);
         }

         this.backingByteBuffer = null;
         this.fileChannel = null;

         try {
            this.raf.close();
         } catch (IOException var2) {
         }

         this.raf = null;
         this.nestedJarHandler.markSliceAsClosed(this);
      }
   }
}
