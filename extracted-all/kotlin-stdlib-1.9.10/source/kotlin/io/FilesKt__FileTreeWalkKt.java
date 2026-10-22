package kotlin.io

import java.io.File

// $VF: Class flags could not be determined
internal class FilesKt__FileTreeWalkKt : FilesKt__FileReadWriteKt {
   @JvmStatic
   public fun File.walk(direction: FileWalkDirection): FileTreeWalk {
      return new FileTreeWalk(`$this$walk`, direction);
   }

   @JvmStatic
   public fun File.walkTopDown(): FileTreeWalk {
      return FilesKt.walk(`$this$walkTopDown`, FileWalkDirection.TOP_DOWN);
   }

   @JvmStatic
   public fun File.walkBottomUp(): FileTreeWalk {
      return FilesKt.walk(`$this$walkBottomUp`, FileWalkDirection.BOTTOM_UP);
   }

   open fun FilesKt__FileTreeWalkKt() {
   }
}
