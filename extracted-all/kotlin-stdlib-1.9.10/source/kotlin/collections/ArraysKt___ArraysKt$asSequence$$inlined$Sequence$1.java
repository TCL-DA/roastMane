package kotlin.collections

import kotlin.jvm.internal.ArrayIteratorKt
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSequences.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Sequences.kt\nkotlin/sequences/SequencesKt__SequencesKt$Sequence$1\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,680:1\n24040#2:681\n*E\n"])
public class `ArraysKt___ArraysKt$asSequence$$inlined$Sequence$1` : Sequence<T> {
   fun `ArraysKt___ArraysKt$asSequence$$inlined$Sequence$1`(var1: Array<Any>) {
      this.$this_asSequence$inlined = var1;
   }

   public override operator fun iterator(): Iterator<Any> {
      return (java.util.Iterator<T>)ArrayIteratorKt.iterator(this.$this_asSequence$inlined);
   }
}
