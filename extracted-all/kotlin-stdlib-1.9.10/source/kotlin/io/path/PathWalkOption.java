package kotlin.io.path

import kotlin.enums.EnumEntries

@ExperimentalPathApi
@SinceKotlin(version = "1.7")
enum class PathWalkOption {
   @JvmStatic
   INCLUDE_DIRECTORIES,
   @JvmStatic
   BREADTH_FIRST,
   @JvmStatic
   FOLLOW_LINKS;


   @JvmStatic
   fun getEntries(): EnumEntries<PathWalkOption> {
      return $ENTRIES;
   }
}
