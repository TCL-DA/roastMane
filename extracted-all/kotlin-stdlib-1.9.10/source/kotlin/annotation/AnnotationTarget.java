package kotlin.annotation

import kotlin.enums.EnumEntries

enum class AnnotationTarget {
   @JvmStatic
   CLASS,
   @JvmStatic
   ANNOTATION_CLASS,
   @JvmStatic
   TYPE_PARAMETER,
   @JvmStatic
   PROPERTY,
   @JvmStatic
   FIELD,
   @JvmStatic
   LOCAL_VARIABLE,
   @JvmStatic
   VALUE_PARAMETER,
   @JvmStatic
   CONSTRUCTOR,
   @JvmStatic
   FUNCTION,
   @JvmStatic
   PROPERTY_GETTER,
   @JvmStatic
   PROPERTY_SETTER,
   @JvmStatic
   TYPE,
   @JvmStatic
   EXPRESSION,
   @JvmStatic
   FILE,
   @SinceKotlin(version = "1.1")
   @JvmStatic
   TYPEALIAS;


   @JvmStatic
   fun getEntries(): EnumEntries<AnnotationTarget> {
      return $ENTRIES;
   }
}
