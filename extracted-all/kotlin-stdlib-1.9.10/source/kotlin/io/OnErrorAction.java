package kotlin.io

import kotlin.enums.EnumEntries

enum class OnErrorAction {
   @JvmStatic
   SKIP,
   @JvmStatic
   TERMINATE;


   @JvmStatic
   fun getEntries(): EnumEntries<OnErrorAction> {
      return $ENTRIES;
   }
}
