package kotlin.collections

import kotlin.jvm.internal.ArrayIteratorsKt
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSequences.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Sequences.kt\nkotlin/sequences/SequencesKt__SequencesKt$Sequence$1\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,680:1\n24110#2:681\n*E\n"])
public class `ArraysKt___ArraysKt$asSequence$$inlined$Sequence$8` : Sequence<java.lang.Boolean> {
   fun `ArraysKt___ArraysKt$asSequence$$inlined$Sequence$8`(var1: BooleanArray) {
      this.$this_asSequence$inlined = var1;
   }

   public override operator fun iterator(): Iterator<Any> {
      return ArrayIteratorsKt.iterator(this.$this_asSequence$inlined);
   }
}
