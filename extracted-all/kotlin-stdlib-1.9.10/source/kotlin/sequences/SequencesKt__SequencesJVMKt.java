package kotlin.sequences

import java.util.Enumeration
import kotlin.internal.InlineOnly

// $VF: Class flags could not be determined
internal class SequencesKt__SequencesJVMKt : SequencesKt__SequenceBuilderKt {
   @InlineOnly
   @JvmStatic
   public inline fun <T> Enumeration<T>.asSequence(): Sequence<T> {
      return SequencesKt.asSequence(CollectionsKt.iterator(`$this$asSequence`));
   }

   open fun SequencesKt__SequencesJVMKt() {
   }
}
