package kotlin.reflect

import kotlin.enums.EnumEntries

@SinceKotlin(version = "1.1")
enum class KVariance {
   @JvmStatic
   INVARIANT,
   @JvmStatic
   IN,
   @JvmStatic
   OUT;


   @JvmStatic
   fun getEntries(): EnumEntries<KVariance> {
      return $ENTRIES;
   }
}
