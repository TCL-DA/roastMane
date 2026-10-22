package kotlin.io.path

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

@ExperimentalPathApi
@SinceKotlin(version = "1.7")
sealed interface FileVisitorBuilder {
   public abstract fun onPreVisitDirectory(function: (Path, BasicFileAttributes) -> FileVisitResult) {
   }

   public abstract fun onVisitFile(function: (Path, BasicFileAttributes) -> FileVisitResult) {
   }

   public abstract fun onVisitFileFailed(function: (Path, IOException) -> FileVisitResult) {
   }

   public abstract fun onPostVisitDirectory(function: (Path, IOException?) -> FileVisitResult) {
   }
}
