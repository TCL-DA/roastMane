package kotlin.sequences

import org.jetbrains.annotations.NotNull

internal class TransformingSequence<T, R>(sequence: Sequence<Any>, transformer: (Any) -> Any) : Sequence<R> {
   private final val sequence: Sequence<Any>
   private final val transformer: (Any) -> Any

   init {
      this.sequence = sequence;
      this.transformer = transformer;
   }

   public override operator fun iterator(): Iterator<Any> {
      return new java.util.Iterator<R>(this) {
         @NotNull
         private final java.util.Iterator<T> iterator;

         {
            this.this$0 = `$receiver`;
            this.iterator = TransformingSequence.access$getSequence$p(`$receiver`).iterator();
         }

         @NotNull
         public final java.util.Iterator<T> getIterator() {
            return this.iterator;
         }

         @Override
         public R next() {
            return (R)TransformingSequence.access$getTransformer$p(this.this$0).invoke(this.iterator.next());
         }

         @Override
         public boolean hasNext() {
            return this.iterator.hasNext();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
         }
      };
   }

   internal fun <E> flatten(iterator: (Any) -> Iterator<E>): Sequence<E> {
      return new FlatteningSequence<>(this.sequence, this.transformer, iterator);
   }
}
