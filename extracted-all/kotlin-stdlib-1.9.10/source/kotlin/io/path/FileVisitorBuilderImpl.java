package kotlin.io.path

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

@ExperimentalPathApi
internal class FileVisitorBuilderImpl : FileVisitorBuilder {
   private final var isBuilt: Boolean
   private final var onPostVisitDirectory: ((Path, IOException?) -> FileVisitResult)?
   private final var onPreVisitDirectory: ((Path, BasicFileAttributes) -> FileVisitResult)?
   private final var onVisitFile: ((Path, BasicFileAttributes) -> FileVisitResult)?
   private final var onVisitFileFailed: ((Path, IOException) -> FileVisitResult)?


   public override fun onPreVisitDirectory(function: (Path, BasicFileAttributes) -> FileVisitResult) {
      this.checkIsNotBuilt();
      this.checkNotDefined(this.onPreVisitDirectory, "onPreVisitDirectory");
      this.onPreVisitDirectory = function;
   }

   public override fun onVisitFile(function: (Path, BasicFileAttributes) -> FileVisitResult) {
      this.checkIsNotBuilt();
      this.checkNotDefined(this.onVisitFile, "onVisitFile");
      this.onVisitFile = function;
   }

   public override fun onVisitFileFailed(function: (Path, IOException) -> FileVisitResult) {
      this.checkIsNotBuilt();
      this.checkNotDefined(this.onVisitFileFailed, "onVisitFileFailed");
      this.onVisitFileFailed = function;
   }

   public override fun onPostVisitDirectory(function: (Path, IOException?) -> FileVisitResult) {
      this.checkIsNotBuilt();
      this.checkNotDefined(this.onPostVisitDirectory, "onPostVisitDirectory");
      this.onPostVisitDirectory = function;
   }

   public fun build(): FileVisitor<Path> {
      this.checkIsNotBuilt();
      this.isBuilt = true;
      return new FileVisitorImpl(this.onPreVisitDirectory, this.onVisitFile, this.onVisitFileFailed, this.onPostVisitDirectory);
   }

   private fun checkIsNotBuilt() {
      if (this.isBuilt) {
         throw new IllegalStateException("This builder was already built");
      }
   }

   private fun checkNotDefined(function: Any?, name: String) {
      if (function != null) {
         throw new IllegalStateException(name + " was already defined");
      }
   }
}
