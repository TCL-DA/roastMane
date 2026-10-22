package okio

import java.util.ArrayList
import kotlin.reflect.KClass
import kotlin.reflect.KClasses

class FileMetadata(isRegularFile: Boolean,
   isDirectory: Boolean,
   symlinkTarget: Path?,
   size: Long?,
   createdAtMillis: Long?,
   lastModifiedAtMillis: Long?,
   lastAccessedAtMillis: Long?,
   extras: Map<KClass<*>, Any>
) {
   public final val createdAtMillis: Long?
   public final val extras: Map<KClass<*>, Any>
   public final val isDirectory: Boolean
   public final val isRegularFile: Boolean
   public final val lastAccessedAtMillis: Long?
   public final val lastModifiedAtMillis: Long?
   public final val size: Long?
   public final val symlinkTarget: Path?

   init {
      this.isRegularFile = isRegularFile;
      this.isDirectory = isDirectory;
      this.symlinkTarget = symlinkTarget;
      this.size = size;
      this.createdAtMillis = createdAtMillis;
      this.lastModifiedAtMillis = lastModifiedAtMillis;
      this.lastAccessedAtMillis = lastAccessedAtMillis;
      this.extras = MapsKt.toMap(extras);
   }

   public fun <T : Any> extra(type: KClass<out T>): T? {
      var var10000: Any = this.extras.get(type);
      return (T)(if (var10000 == null) null else KClasses.cast(type, var10000));
   }

   public fun copy(
      isRegularFile: Boolean,
      isDirectory: Boolean,
      symlinkTarget: Path?,
      size: Long?,
      createdAtMillis: Long?,
      lastModifiedAtMillis: Long?,
      lastAccessedAtMillis: Long?,
      extras: Map<KClass<*>, Any>
   ): FileMetadata {
      return new FileMetadata(isRegularFile, isDirectory, symlinkTarget, size, createdAtMillis, lastModifiedAtMillis, lastAccessedAtMillis, extras);
   }

   public override fun toString(): String {
      var fields: java.util.List = new ArrayList();
      if (this.isRegularFile) {
         fields.add("isRegularFile");
      }

      if (this.isDirectory) {
         fields.add("isDirectory");
      }

      if (this.size != null) {
         fields.add("byteCount=" + this.size);
      }

      if (this.createdAtMillis != null) {
         fields.add("createdAt=" + this.createdAtMillis);
      }

      if (this.lastModifiedAtMillis != null) {
         fields.add("lastModifiedAt=" + this.lastModifiedAtMillis);
      }

      if (this.lastAccessedAtMillis != null) {
         fields.add("lastAccessedAt=" + this.lastAccessedAtMillis);
      }

      if (!this.extras.isEmpty()) {
         fields.add("extras=" + this.extras);
      }

      return CollectionsKt.joinToString$default(fields, ", ", "FileMetadata(", ")", 0, null, null, 56, null);
   }

   fun FileMetadata() {
      this(false, false, null, null, null, null, null, null, 255, null);
   }
}
