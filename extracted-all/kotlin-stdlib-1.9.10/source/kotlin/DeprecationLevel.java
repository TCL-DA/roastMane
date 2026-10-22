package kotlin

import kotlin.enums.EnumEntries

enum class DeprecationLevel {
   @JvmStatic
   WARNING,
   @JvmStatic
   ERROR,
   @JvmStatic
   HIDDEN;


   @JvmStatic
   fun getEntries(): EnumEntries<DeprecationLevel> {
      return $ENTRIES;
   }
}
