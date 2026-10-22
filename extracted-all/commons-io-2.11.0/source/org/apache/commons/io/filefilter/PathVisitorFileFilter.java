package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.commons.io.file.NoopPathVisitor;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.io.file.PathVisitor;

public class PathVisitorFileFilter extends AbstractFileFilter {
   private final PathVisitor pathVisitor;

   public PathVisitorFileFilter(PathVisitor pathVisitor) {
      this.pathVisitor = (PathVisitor)(pathVisitor == null ? NoopPathVisitor.INSTANCE : pathVisitor);
   }

   @Override
   public boolean accept(File file) {
      try {
         Path path = file.toPath();
         return this.visitFile(path, file.exists() ? PathUtils.readBasicFileAttributes(path) : null) == FileVisitResult.CONTINUE;
      } catch (IOException var3) {
         return this.handle(var3) == FileVisitResult.CONTINUE;
      }
   }

   @Override
   public boolean accept(File dir, String name) {
      try {
         Path path = dir.toPath().resolve(name);
         return this.accept(path, PathUtils.readBasicFileAttributes(path)) == FileVisitResult.CONTINUE;
      } catch (IOException var4) {
         return this.handle(var4) == FileVisitResult.CONTINUE;
      }
   }

   @Override
   public FileVisitResult accept(Path path, BasicFileAttributes attributes) {
      try {
         return Files.isDirectory(path) ? this.pathVisitor.postVisitDirectory(path, null) : this.visitFile(path, attributes);
      } catch (IOException var4) {
         return this.handle(var4);
      }
   }

   @Override
   public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) throws IOException {
      return this.pathVisitor.visitFile(path, attributes);
   }
}
