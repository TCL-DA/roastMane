package kotlin.collections

import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSequences.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Sequences.kt\nkotlin/sequences/SequencesKt__SequencesKt$Sequence$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,680:1\n3524#2:681\n*E\n"])
public class `CollectionsKt___CollectionsKt$asSequence$$inlined$Sequence$1` : Sequence<T> {
   fun `CollectionsKt___CollectionsKt$asSequence$$inlined$Sequence$1`(var1: java.lang.Iterable) {
      this.$this_asSequence$inlined = var1;
   }

   public override operator fun iterator(): Iterator<Any> {
      return this.$this_asSequence$inlined.iterator();
   }
}
