package kotlin.text

import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSequences.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Sequences.kt\nkotlin/sequences/SequencesKt__SequencesKt$Sequence$1\n+ 2 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n1#1,680:1\n2483#2:681\n*E\n"])
public class `StringsKt___StringsKt$asSequence$$inlined$Sequence$1` : Sequence<Character> {
   fun `StringsKt___StringsKt$asSequence$$inlined$Sequence$1`(var1: java.lang.CharSequence) {
      this.$this_asSequence$inlined = var1;
   }

   public override operator fun iterator(): Iterator<Any> {
      return StringsKt.iterator(this.$this_asSequence$inlined);
   }
}
