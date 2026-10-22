package kotlin.text

import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.markers.KMappedMarker

@SourceDebugExtension(["SMAP\nIterables.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Iterables.kt\nkotlin/collections/CollectionsKt__IterablesKt$Iterable$1\n+ 2 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n1#1,70:1\n2475#2:71\n*E\n"])
public class `StringsKt___StringsKt$asIterable$$inlined$Iterable$1` : java.lang.Iterable<Character>, KMappedMarker {
   fun `StringsKt___StringsKt$asIterable$$inlined$Iterable$1`(var1: java.lang.CharSequence) {
      this.$this_asIterable$inlined = var1;
   }

   public override operator fun iterator(): Iterator<Any> {
      return StringsKt.iterator(this.$this_asIterable$inlined);
   }
}
