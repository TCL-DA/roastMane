package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;

public class MagicNumberFileFilter extends AbstractFileFilter implements Serializable {
   private static final long serialVersionUID = -547733176983104172L;
   private final byte[] magicNumbers;
   private final long byteOffset;

   public MagicNumberFileFilter(byte[] magicNumber) {
      this(magicNumber, 0L);
   }

   public MagicNumberFileFilter(byte[] magicNumber, long offset) {
      if (magicNumber == null) {
         throw new IllegalArgumentException("The magic number cannot be null");
      } else if (magicNumber.length == 0) {
         throw new IllegalArgumentException("The magic number must contain at least one byte");
      } else if (offset < 0L) {
         throw new IllegalArgumentException("The offset cannot be negative");
      } else {
         this.magicNumbers = IOUtils.byteArray(magicNumber.length);
         System.arraycopy(magicNumber, 0, this.magicNumbers, 0, magicNumber.length);
         this.byteOffset = offset;
      }
   }

   public MagicNumberFileFilter(String magicNumber) {
      this(magicNumber, 0L);
   }

   public MagicNumberFileFilter(String magicNumber, long offset) {
      if (magicNumber == null) {
         throw new IllegalArgumentException("The magic number cannot be null");
      } else if (magicNumber.isEmpty()) {
         throw new IllegalArgumentException("The magic number must contain at least one byte");
      } else if (offset < 0L) {
         throw new IllegalArgumentException("The offset cannot be negative");
      } else {
         this.magicNumbers = magicNumber.getBytes(Charset.defaultCharset());
         this.byteOffset = offset;
      }
   }

   @Override
   public boolean accept(File file) {
      if (file != null && file.isFile() && file.canRead()) {
         try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            byte[] fileBytes = IOUtils.byteArray(this.magicNumbers.length);
            randomAccessFile.seek(this.byteOffset);
            int read = randomAccessFile.read(fileBytes);
            if (read != this.magicNumbers.length) {
               return false;
            }

            return Arrays.equals(this.magicNumbers, fileBytes);
         } catch (IOException var19) {
         }
      }

      return false;
   }

   @Override
   public FileVisitResult accept(Path file, BasicFileAttributes attributes) {
      if (file != null && Files.isRegularFile(file) && Files.isReadable(file)) {
         try (FileChannel fileChannel = FileChannel.open(file)) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(this.magicNumbers.length);
            int read = fileChannel.read(byteBuffer);
            if (read != this.magicNumbers.length) {
               return FileVisitResult.TERMINATE;
            }

            return toFileVisitResult(Arrays.equals(this.magicNumbers, byteBuffer.array()), file);
         } catch (IOException var20) {
         }
      }

      return FileVisitResult.TERMINATE;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder(super.toString());
      builder.append("(");
      builder.append(new String(this.magicNumbers, Charset.defaultCharset()));
      builder.append(",");
      builder.append(this.byteOffset);
      builder.append(")");
      return builder.toString();
   }
}
