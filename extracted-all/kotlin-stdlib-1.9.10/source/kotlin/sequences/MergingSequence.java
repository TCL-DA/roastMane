package kotlin.sequences

import org.jetbrains.annotations.NotNull

internal class MergingSequence<T1, T2, V>(sequence1: Sequence<Any>, sequence2: Sequence<Any>, transform: (Any, Any) -> Any) : Sequence<V> {
   private final val sequence1: Sequence<Any>
   private final val sequence2: Sequence<Any>
   private final val transform: (Any, Any) -> Any

   init {
      this.sequence1 = sequence1;
      this.sequence2 = sequence2;
      this.transform = transform;
   }

   public override operator fun iterator(): Iterator<Any> {
      return new java.util.Iterator<V>(this) {
         @NotNull
         private final java.util.Iterator<T1> iterator1;
         @NotNull
         private final java.util.Iterator<T2> iterator2;

         {
            this.this$0 = `$receiver`;
            this.iterator1 = MergingSequence.access$getSequence1$p(`$receiver`).iterator();
            this.iterator2 = MergingSequence.access$getSequence2$p(`$receiver`).iterator();
         }

         @NotNull
         public final java.util.Iterator<T1> getIterator1() {
            return this.iterator1;
         }

         @NotNull
         public final java.util.Iterator<T2> getIterator2() {
            return this.iterator2;
         }

         @Override
         public V next() {
            return (V)MergingSequence.access$getTransform$p(this.this$0).invoke(this.iterator1.next(), this.iterator2.next());
         }

         @Override
         public boolean hasNext() {
            return this.iterator1.hasNext() && this.iterator2.hasNext();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
         }
      };
   }
}
