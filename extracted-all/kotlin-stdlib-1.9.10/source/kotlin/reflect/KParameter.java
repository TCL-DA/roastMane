package kotlin.reflect

import kotlin.enums.EnumEntries

interface KParameter : KAnnotatedElement {
   public val index: Int
   public val isOptional: Boolean
   public val isVararg: Boolean
   public val kind: kotlin.reflect.KParameter.Kind
   public val name: String?
   public val type: KType

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
   }

   enum class Kind {
      @JvmStatic
      INSTANCE,
      @JvmStatic
      EXTENSION_RECEIVER,
      @JvmStatic
      VALUE;


      @JvmStatic
      fun getEntries(): EnumEntries<KParameter.Kind> {
         return $ENTRIES;
      }
   }
}
