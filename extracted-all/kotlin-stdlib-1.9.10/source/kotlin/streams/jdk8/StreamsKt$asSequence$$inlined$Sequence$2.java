package kotlin.streams.jdk8

import java.util.PrimitiveIterator.OfInt
import java.util.stream.IntStream
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSequences.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Sequences.kt\nkotlin/sequences/SequencesKt__SequencesKt$Sequence$1\n+ 2 Streams.kt\nkotlin/streams/jdk8/StreamsKt\n*L\n1#1,680:1\n39#2:681\n*E\n"])
public class `StreamsKt$asSequence$$inlined$Sequence$2` : Sequence<Integer> {
   fun `StreamsKt$asSequence$$inlined$Sequence$2`(var1: IntStream) {
      this.$this_asSequence$inlined = var1;
   }

   public override operator fun iterator(): Iterator<Any> {
      var var10000: OfInt = this.$this_asSequence$inlined.iterator();
      return var10000;
   }
}
