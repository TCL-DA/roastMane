package kotlin.coroutines.intrinsics

import kotlin.enums.EnumEntries

@SinceKotlin(version = "1.3")
@PublishedApi
internal enum class CoroutineSingletons {
   @JvmStatic
   COROUTINE_SUSPENDED,
   @JvmStatic
   UNDECIDED,
   @JvmStatic
   RESUMED;


   @JvmStatic
   fun getEntries(): EnumEntries<CoroutineSingletons> {
      return $ENTRIES;
   }
}
