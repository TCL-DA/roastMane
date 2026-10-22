package okio.internal

import java.util.ArrayList
import okio.Path

internal class ZipEntry(canonicalPath: Path,
   isDirectory: Boolean,
   comment: String,
   crc: Long,
   compressedSize: Long,
   size: Long,
   compressionMethod: Int,
   lastModifiedAtMillis: Long?,
   offset: Long
) {
   public final val canonicalPath: Path
   public final val children: MutableList<Path>
   public final val comment: String
   public final val compressedSize: Long
   public final val compressionMethod: Int
   public final val crc: Long
   public final val isDirectory: Boolean
   public final val lastModifiedAtMillis: Long?
   public final val offset: Long
   public final val size: Long

   init {
      this.canonicalPath = canonicalPath;
      this.isDirectory = isDirectory;
      this.comment = comment;
      this.crc = crc;
      this.compressedSize = compressedSize;
      this.size = size;
      this.compressionMethod = compressionMethod;
      this.lastModifiedAtMillis = lastModifiedAtMillis;
      this.offset = offset;
      this.children = new ArrayList<>();
   }
}
