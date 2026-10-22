package kotlin.io.path

import kotlin.enums.EnumEntries

@ExperimentalPathApi
@SinceKotlin(version = "1.8")
enum class CopyActionResult {
   @JvmStatic
   CONTINUE,
   @JvmStatic
   SKIP_SUBTREE,
   @JvmStatic
   TERMINATE;


   @JvmStatic
   fun getEntries(): EnumEntries<CopyActionResult> {
      return $ENTRIES;
   }
}
