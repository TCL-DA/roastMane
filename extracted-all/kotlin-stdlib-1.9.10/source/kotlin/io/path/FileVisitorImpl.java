package kotlin.io.path

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

private class FileVisitorImpl(onPreVisitDirectory: ((Path, BasicFileAttributes) -> FileVisitResult)?,
      onVisitFile: ((Path, BasicFileAttributes) -> FileVisitResult)?,
      onVisitFileFailed: ((Path, IOException) -> FileVisitResult)?,
      onPostVisitDirectory: ((Path, IOException?) -> FileVisitResult)?
   )
   : SimpleFileVisitor<Path> {
   private final val onPostVisitDirectory: ((Path, IOException?) -> FileVisitResult)?
   private final val onPreVisitDirectory: ((Path, BasicFileAttributes) -> FileVisitResult)?
   private final val onVisitFile: ((Path, BasicFileAttributes) -> FileVisitResult)?
   private final val onVisitFileFailed: ((Path, IOException) -> FileVisitResult)?

   init {
      this.onPreVisitDirectory = onPreVisitDirectory;
      this.onVisitFile = onVisitFile;
      this.onVisitFileFailed = onVisitFileFailed;
      this.onPostVisitDirectory = onPostVisitDirectory;
   }

   public open fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
      if (this.onPreVisitDirectory != null) {
         var var10000: FileVisitResult = this.onPreVisitDirectory.invoke(dir, attrs);
         if (var10000 != null) {
            return var10000;
         }
      }

      var var3: FileVisitResult = super.preVisitDirectory(dir, attrs);
      return var3;
   }

   public open fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
      if (this.onVisitFile != null) {
         var var10000: FileVisitResult = this.onVisitFile.invoke(file, attrs);
         if (var10000 != null) {
            return var10000;
         }
      }

      var var3: FileVisitResult = super.visitFile(file, attrs);
      return var3;
   }

   public open fun visitFileFailed(file: Path, exc: IOException): FileVisitResult {
      if (this.onVisitFileFailed != null) {
         var var10000: FileVisitResult = this.onVisitFileFailed.invoke(file, exc);
         if (var10000 != null) {
            return var10000;
         }
      }

      var var3: FileVisitResult = super.visitFileFailed(file, exc);
      return var3;
   }

   public open fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
      if (this.onPostVisitDirectory != null) {
         var var10000: FileVisitResult = this.onPostVisitDirectory.invoke(dir, exc);
         if (var10000 != null) {
            return var10000;
         }
      }

      var var3: FileVisitResult = super.postVisitDirectory(dir, exc);
      return var3;
   }
}
