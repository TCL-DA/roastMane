package kotlin.io

import kotlin.enums.EnumEntries

enum class FileWalkDirection {
   @JvmStatic
   TOP_DOWN,
   @JvmStatic
   BOTTOM_UP;


   @JvmStatic
   fun getEntries(): EnumEntries<FileWalkDirection> {
      return $ENTRIES;
   }
}
