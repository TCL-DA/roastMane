package kotlin.sequences

import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSequences.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Sequences.kt\nkotlin/sequences/SequencesKt__SequencesKt$Sequence$1\n+ 2 SequenceBuilder.kt\nkotlin/sequences/SequencesKt__SequenceBuilderKt\n*L\n1#1,680:1\n26#2:681\n*E\n"])
public class `SequencesKt__SequenceBuilderKt$sequence$$inlined$Sequence$1` : Sequence<T> {
   fun `SequencesKt__SequenceBuilderKt$sequence$$inlined$Sequence$1`(var1: Function2) {
      this.$block$inlined = var1;
   }

   public override operator fun iterator(): Iterator<Any> {
      return SequencesKt.iterator(this.$block$inlined);
   }
}
