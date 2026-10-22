package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;

public class EmptyFileFilter extends AbstractFileFilter implements Serializable {
   public static final IOFileFilter EMPTY = new EmptyFileFilter();
   public static final IOFileFilter NOT_EMPTY = EMPTY.negate();
   private static final long serialVersionUID = 3631422087512832211L;

   protected EmptyFileFilter() {
   }

   @Override
   public boolean accept(File file) {
      if (file.isDirectory()) {
         File[] files = file.listFiles();
         return IOUtils.length((Object[])files) == 0;
      } else {
         return file.length() == 0L;
      }
   }

   @Override
   public FileVisitResult accept(Path file, BasicFileAttributes attributes) {
      try {
         if (Files.isDirectory(file)) {
            FileVisitResult var5;
            try (Stream<Path> stream = Files.list(file)) {
               var5 = toFileVisitResult(!stream.findFirst().isPresent(), file);
            }

            return var5;
         } else {
            return toFileVisitResult(Files.size(file) == 0L, file);
         }
      } catch (IOException var17) {
         return this.handle(var17);
      }
   }
}
