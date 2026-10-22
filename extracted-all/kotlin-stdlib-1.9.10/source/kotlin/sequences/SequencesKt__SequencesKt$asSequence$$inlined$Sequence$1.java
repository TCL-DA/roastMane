package kotlin.sequences

import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSequences.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Sequences.kt\nkotlin/sequences/SequencesKt__SequencesKt$Sequence$1\n+ 2 Sequences.kt\nkotlin/sequences/SequencesKt__SequencesKt\n*L\n1#1,680:1\n30#2:681\n*E\n"])
public class `SequencesKt__SequencesKt$asSequence$$inlined$Sequence$1` : Sequence<T> {
   fun `SequencesKt__SequencesKt$asSequence$$inlined$Sequence$1`(var1: java.util.Iterator) {
      this.$this_asSequence$inlined = var1;
   }

   public override operator fun iterator(): Iterator<Any> {
      return this.$this_asSequence$inlined;
   }
}
