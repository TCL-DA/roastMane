package kotlin.sequences

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt

// $VF: Class flags could not be determined
internal class SequencesKt__SequenceBuilderKt {
   private const val State_Done: Int = 4
   private const val State_Failed: Int = 5
   private const val State_ManyNotReady: Int = 1
   private const val State_ManyReady: Int = 2
   private const val State_NotReady: Int = 0
   private const val State_Ready: Int = 3

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun <T> sequence(block: (SequenceScope<T>, Continuation<Unit>) -> Any?): Sequence<T> {
      return new SequencesKt__SequenceBuilderKt$sequence$$inlined$Sequence$1(block);
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun <T> iterator(block: (SequenceScope<T>, Continuation<Unit>) -> Any?): Iterator<T> {
      var iterator: SequenceBuilderIterator = new SequenceBuilderIterator();
      iterator.setNextStep(IntrinsicsKt.createCoroutineUnintercepted(block, iterator, iterator));
      return iterator;
   }

   open fun SequencesKt__SequenceBuilderKt() {
   }
}
