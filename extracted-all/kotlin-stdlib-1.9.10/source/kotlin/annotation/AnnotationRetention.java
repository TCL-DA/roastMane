package kotlin.annotation

import kotlin.enums.EnumEntries

enum class AnnotationRetention {
   @JvmStatic
   SOURCE,
   @JvmStatic
   BINARY,
   @JvmStatic
   RUNTIME;


   @JvmStatic
   fun getEntries(): EnumEntries<AnnotationRetention> {
      return $ENTRIES;
   }
}
