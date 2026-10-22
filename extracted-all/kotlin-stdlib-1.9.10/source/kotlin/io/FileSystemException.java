package kotlin.io

import java.io.File
import java.io.IOException

open class FileSystemException(file: File, other: File?, reason: String?) : IOException(ExceptionsKt.access$constructMessage(file, other, reason)) {
   public final val file: File
   public final val other: File?
   public final val reason: String?

   init {
      this.file = file;
      this.other = other;
      this.reason = reason;
   }
}
