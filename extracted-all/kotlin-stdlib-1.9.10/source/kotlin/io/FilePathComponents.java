package kotlin.io

import java.io.File

internal data class FilePathComponents internal constructor(root: File, segments: List<File>) {
   public final val isRooted: Boolean
      public final get() {
         var var10000: java.lang.String = this.root.getPath();
         return var10000.length() > 0;
      }

   public final val root: File
   public final val rootName: String
      public final get() {
         var var10000: java.lang.String = this.root.getPath();
         return var10000;
      }

   public final val segments: List<File>
   public final val size: Int
      public final get() {
         return this.segments.size();
      }


   init {
      this.root = root;
      this.segments = segments;
   }

   public fun subPath(beginIndex: Int, endIndex: Int): File {
      if (beginIndex >= 0 && beginIndex <= endIndex && endIndex <= this.getSize()) {
         var var10002: java.lang.Iterable = this.segments.subList(beginIndex, endIndex);
         var var10003: java.lang.String = File.separator;
         return new File(CollectionsKt.joinToString$default(var10002, var10003, null, null, 0, null, null, 62, null));
      } else {
         throw new IllegalArgumentException();
      }
   }

   public operator fun component1(): File {
      return this.root;
   }

   public operator fun component2(): List<File> {
      return this.segments;
   }

   public fun copy(root: File, segments: List<File>): FilePathComponents {
      return new FilePathComponents(root, segments);
   }

   public override fun toString(): String {
      return "FilePathComponents(root=" + this.root + ", segments=" + this.segments + ')';
   }

   public override fun hashCode(): Int {
      return this.root.hashCode() * 31 + this.segments.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is FilePathComponents) {
         return false;
      } else {
         var var2: FilePathComponents = other as FilePathComponents;
         if (!(this.root == (other as FilePathComponents).root)) {
            return false;
         } else {
            return this.segments == var2.segments;
         }
      }
   }
}
