package kotlin.streams.jdk8

import java.util.PrimitiveIterator.OfLong
import java.util.stream.LongStream
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSequences.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Sequences.kt\nkotlin/sequences/SequencesKt__SequencesKt$Sequence$1\n+ 2 Streams.kt\nkotlin/streams/jdk8/StreamsKt\n*L\n1#1,680:1\n47#2:681\n*E\n"])
public class `StreamsKt$asSequence$$inlined$Sequence$3` : Sequence<java.lang.Long> {
   fun `StreamsKt$asSequence$$inlined$Sequence$3`(var1: LongStream) {
      this.$this_asSequence$inlined = var1;
   }

   public override operator fun iterator(): Iterator<Any> {
      var var10000: OfLong = this.$this_asSequence$inlined.iterator();
      return var10000;
   }
}
