package kotlin.reflect

import kotlin.enums.EnumEntries

@SinceKotlin(version = "1.1")
enum class KVisibility {
   @JvmStatic
   PUBLIC,
   @JvmStatic
   PROTECTED,
   @JvmStatic
   INTERNAL,
   @JvmStatic
   PRIVATE;


   @JvmStatic
   fun getEntries(): EnumEntries<KVisibility> {
      return $ENTRIES;
   }
}
