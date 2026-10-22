package kotlin.sequences

import java.util.concurrent.atomic.AtomicReference

internal class ConstrainedOnceSequence<T>(sequence: Sequence<Any>) : Sequence<T> {
   private final val sequenceRef: AtomicReference<Sequence<Any>>

   init {
      this.sequenceRef = new AtomicReference<>(sequence);
   }

   public override operator fun iterator(): Iterator<Any> {
      var var10000: Sequence = this.sequenceRef.getAndSet(null);
      if (var10000 == null) {
         throw new IllegalStateException("This sequence can be consumed only once.");
      } else {
         return var10000.iterator();
      }
   }
}
