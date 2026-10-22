package kotlin.text

import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Lambda
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nRegex.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Regex.kt\nkotlin/text/RegexKt$fromInt$1$1\n*L\n1#1,396:1\n*E\n"])
// $VF: Class flags could not be determined
internal class `Regex$special$$inlined$fromInt$1` : Lambda, Function1<RegexOption, java.lang.Boolean> {
   fun `Regex$special$$inlined$fromInt$1`(`$value`: Int) {
      super(1);
      this.$value = `$value`;
   }

   // QF: local property
internal fun <reified T> `<anonymous>`(it: T): Boolean where T : FlagEnum, T : Enum<T> {
      return (this.$value and (it as FlagEnum).getMask()) == (it as FlagEnum).getValue();
   }
}
