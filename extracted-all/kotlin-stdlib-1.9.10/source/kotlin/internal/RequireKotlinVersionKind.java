package kotlin.internal

import kotlin.enums.EnumEntries

@SinceKotlin(version = "1.2")
internal enum class RequireKotlinVersionKind {
   @JvmStatic
   LANGUAGE_VERSION,
   @JvmStatic
   COMPILER_VERSION,
   @JvmStatic
   API_VERSION;


   @JvmStatic
   fun getEntries(): EnumEntries<RequireKotlinVersionKind> {
      return $ENTRIES;
   }
}
