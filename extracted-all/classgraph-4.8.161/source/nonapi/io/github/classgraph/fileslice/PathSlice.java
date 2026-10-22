package nonapi.io.github.classgraph.fileslice;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicBoolean;
import nonapi.io.github.classgraph.fastzipfilereader.NestedJarHandler;
import nonapi.io.github.classgraph.fileslice.reader.RandomAccessFileChannelReader;
import nonapi.io.github.classgraph.fileslice.reader.RandomAccessReader;
import nonapi.io.github.classgraph.utils.FileUtils;

public class PathSlice extends Slice implements Closeable {
   public final Path path;
   private final long fileLength;
   private FileChannel fileChannel;
   private final boolean isTopLevelFileSlice;
   private final AtomicBoolean isClosed = new AtomicBoolean();

   private PathSlice(PathSlice parentSlice, long offset, long length, boolean isDeflatedZipEntry, long inflatedLengthHint, NestedJarHandler nestedJarHandler) {
      super(parentSlice, offset, length, isDeflatedZipEntry, inflatedLengthHint, nestedJarHandler);
      this.path = parentSlice.path;
      this.fileChannel = parentSlice.fileChannel;
      this.fileLength = parentSlice.fileLength;
      this.isTopLevelFileSlice = false;
   }

   public PathSlice(Path path, boolean isDeflatedZipEntry, long inflatedLengthHint, NestedJarHandler nestedJarHandler) throws IOException {
      super(0L, isDeflatedZipEntry, inflatedLengthHint, nestedJarHandler);
      FileUtils.checkCanReadAndIsFile(path);
      this.path = path;
      this.fileChannel = FileChannel.open(path, StandardOpenOption.READ);
      this.fileLength = this.fileChannel.size();
      this.isTopLevelFileSlice = true;
      this.sliceLength = this.fileLength;
      nestedJarHandler.markSliceAsOpen(this);
   }

   public PathSlice(Path path, NestedJarHandler nestedJarHandler) throws IOException {
      this(path, false, 0L, nestedJarHandler);
   }

   @Override
   public Slice slice(long offset, long length, boolean isDeflatedZipEntry, long inflatedLengthHint) {
      if (this.isDeflatedZipEntry) {
         throw new IllegalArgumentException("Cannot slice a deflated zip entry");
      } else {
         return new PathSlice(this, offset, length, isDeflatedZipEntry, inflatedLengthHint, this.nestedJarHandler);
      }
   }

   @Override
   public RandomAccessReader randomAccessReader() {
      return new RandomAccessFileChannelReader(this.fileChannel, this.sliceStartPos, this.sliceLength);
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
      } else if (this.sliceLength > 2147483639L) {
         throw new IOException("File is larger than 2GB");
      } else {
         return ByteBuffer.wrap(this.load());
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
         if (this.isTopLevelFileSlice && this.fileChannel != null) {
            try {
               this.fileChannel.close();
            } catch (IOException var2) {
            }

            this.fileChannel = null;
         }

         this.fileChannel = null;
         this.nestedJarHandler.markSliceAsClosed(this);
      }
   }
}
