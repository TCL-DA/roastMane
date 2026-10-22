package kotlin.sequences

import org.jetbrains.annotations.NotNull

internal class IndexingSequence<T>(sequence: Sequence<Any>) : Sequence<IndexedValue<? extends T>> {
   private final val sequence: Sequence<Any>

   init {
      this.sequence = sequence;
   }

   public override operator fun iterator(): Iterator<IndexedValue<Any>> {
      return (java.util.Iterator<IndexedValue<T>>)(new java.util.Iterator<IndexedValue<? extends T>>(this) {
         @NotNull
         private final java.util.Iterator<T> iterator;
         private int index;

         {
            this.iterator = IndexingSequence.access$getSequence$p(`$receiver`).iterator();
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

         @NotNull
         public IndexedValue<T> next() {
            var var10000: IndexedValue = new IndexedValue;
            var var1: Int = this.index++;
            if (var1 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            var10000./* $VF: Unable to resugar constructor */<init>(var1, this.iterator.next());
            return var10000;
         }

         @Override
         public boolean hasNext() {
            return this.iterator.hasNext();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
         }
      });
   }
}
