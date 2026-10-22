package kotlin.io.path

import kotlin.enums.EnumEntries

@ExperimentalPathApi
@SinceKotlin(version = "1.8")
enum class OnErrorResult {
   @JvmStatic
   SKIP_SUBTREE,
   @JvmStatic
   TERMINATE;


   @JvmStatic
   fun getEntries(): EnumEntries<OnErrorResult> {
      return $ENTRIES;
   }
}
