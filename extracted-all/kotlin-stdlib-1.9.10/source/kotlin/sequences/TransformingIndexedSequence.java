package kotlin.sequences

import kotlin.jvm.functions.Function2
import org.jetbrains.annotations.NotNull

internal class TransformingIndexedSequence<T, R>(sequence: Sequence<Any>, transformer: (Int, Any) -> Any) : Sequence<R> {
   private final val sequence: Sequence<Any>
   private final val transformer: (Int, Any) -> Any

   init {
      this.sequence = sequence;
      this.transformer = transformer;
   }

   public override operator fun iterator(): Iterator<Any> {
      return new java.util.Iterator<R>(this) {
         @NotNull
         private final java.util.Iterator<T> iterator;
         private int index;

         {
            this.this$0 = `$receiver`;
            this.iterator = TransformingIndexedSequence.access$getSequence$p(`$receiver`).iterator();
         }

         @NotNull
         public final java.util.Iterator<T> getIterator() {
            return this.iterator;
         }

         public final int getIndex() {
            return this.index;
         }

         public final void setIndex(int var1) {
            this.index = var1;
         }

         @Override
         public R next() {
            var var10000: Function2 = TransformingIndexedSequence.access$getTransformer$p(this.this$0);
            var var1: Int = this.index++;
            if (var1 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            return (R)var10000.invoke(var1, this.iterator.next());
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
}
