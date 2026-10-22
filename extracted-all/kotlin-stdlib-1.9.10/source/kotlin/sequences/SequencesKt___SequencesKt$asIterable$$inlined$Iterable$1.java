package kotlin.sequences

import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.markers.KMappedMarker

@SourceDebugExtension(["SMAP\nIterables.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Iterables.kt\nkotlin/collections/CollectionsKt__IterablesKt$Iterable$1\n+ 2 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,70:1\n2921#2:71\n*E\n"])
public class `SequencesKt___SequencesKt$asIterable$$inlined$Iterable$1` : java.lang.Iterable<T>, KMappedMarker {
   fun `SequencesKt___SequencesKt$asIterable$$inlined$Iterable$1`(var1: Sequence) {
      this.$this_asIterable$inlined = var1;
   }

   public override operator fun iterator(): Iterator<Any> {
      return this.$this_asIterable$inlined.iterator();
   }
}
