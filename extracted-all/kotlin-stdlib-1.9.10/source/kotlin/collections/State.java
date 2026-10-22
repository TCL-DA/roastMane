package kotlin.collections

import kotlin.enums.EnumEntries

private enum class State {
   @JvmStatic
   Ready,
   @JvmStatic
   NotReady,
   @JvmStatic
   Done,
   @JvmStatic
   Failed;


   @JvmStatic
   fun getEntries(): EnumEntries<State> {
      return $ENTRIES;
   }
}
