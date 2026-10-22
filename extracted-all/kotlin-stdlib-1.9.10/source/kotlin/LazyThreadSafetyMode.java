package kotlin

import kotlin.enums.EnumEntries

enum class LazyThreadSafetyMode {
   @JvmStatic
   SYNCHRONIZED,
   @JvmStatic
   PUBLICATION,
   @JvmStatic
   NONE;


   @JvmStatic
   fun getEntries(): EnumEntries<LazyThreadSafetyMode> {
      return $ENTRIES;
   }
}
