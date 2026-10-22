package kotlin.io.path

import java.nio.file.FileSystemException
import java.nio.file.Path
import java.util.ArrayList

private class ExceptionsCollector(limit: Int) {
   public final val collectedExceptions: MutableList<Exception>
   private final val limit: Int
   public final var path: Path?
      internal set
   public final var totalExceptions: Int
      private set

   init {
      this.limit = limit;
      this.collectedExceptions = new ArrayList<>();
   }

   public fun enterEntry(name: Path) {
      this.path = if (this.path != null) this.path.resolve(name) else null;
   }

   public fun exitEntry(name: Path) {
      if (!(name == (if (this.path != null) this.path.getFileName() else null))) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      } else {
         this.path = if (this.path != null) this.path.getParent() else null;
      }
   }

   public fun collect(exception: Exception) {
      this.totalExceptions++;
      if (this.collectedExceptions.size() < this.limit) {
         var var4: Exception;
         if (this.path != null) {
            var var10000: java.lang.Throwable = new FileSystemException(java.lang.String.valueOf(this.path)).initCause(exception);
            var4 = var10000 as FileSystemException;
         } else {
            var4 = exception;
         }

         this.collectedExceptions.add(var4);
      }
   }

   fun ExceptionsCollector() {
      this(0, 1, null);
   }
}
