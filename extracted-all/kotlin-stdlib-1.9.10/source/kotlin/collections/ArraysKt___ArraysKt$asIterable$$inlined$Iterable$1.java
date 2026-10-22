package kotlin.collections

import kotlin.jvm.internal.ArrayIteratorKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.markers.KMappedMarker

@SourceDebugExtension(["SMAP\nIterables.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Iterables.kt\nkotlin/collections/CollectionsKt__IterablesKt$Iterable$1\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,70:1\n23966#2:71\n*E\n"])
public class `ArraysKt___ArraysKt$asIterable$$inlined$Iterable$1` : java.lang.Iterable<T>, KMappedMarker {
   fun `ArraysKt___ArraysKt$asIterable$$inlined$Iterable$1`(var1: Array<Any>) {
      this.$this_asIterable$inlined = var1;
   }

   public override operator fun iterator(): Iterator<Any> {
      return (java.util.Iterator<T>)ArrayIteratorKt.iterator(this.$this_asIterable$inlined);
   }
}
