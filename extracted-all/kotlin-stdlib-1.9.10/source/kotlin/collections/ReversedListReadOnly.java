package kotlin.collections

import org.jetbrains.annotations.NotNull

private open class ReversedListReadOnly<T>(delegate: List<Any>) : AbstractList<T> {
   private final val delegate: List<Any>
   public open val size: Int
      public open get() {
         return this.delegate.size();
      }


   init {
      this.delegate = delegate;
   }

   public override operator fun get(index: Int): Any {
      return this.delegate.get(CollectionsKt__ReversedViewsKt.access$reverseElementIndex(this, index));
   }

   public override operator fun iterator(): Iterator<Any> {
      return this.listIterator(0);
   }

   public override fun listIterator(): ListIterator<Any> {
      return this.listIterator(0);
   }

   public override fun listIterator(index: Int): ListIterator<Any> {
      return new java.util.ListIterator<T>(this, index) {
         @NotNull
         private final java.util.ListIterator<T> delegateIterator;

         {
            this.this$0 = `$receiver`;
            this.delegateIterator = ReversedListReadOnly.access$getDelegate$p(`$receiver`)
               .listIterator(CollectionsKt__ReversedViewsKt.access$reversePositionIndex(`$receiver`, `$index`));
         }

         @NotNull
         public final java.util.ListIterator<T> getDelegateIterator() {
            return this.delegateIterator;
         }

         @Override
         public boolean hasNext() {
            return this.delegateIterator.hasPrevious();
         }

         @Override
         public boolean hasPrevious() {
            return this.delegateIterator.hasNext();
         }

         @Override
         public T next() {
            return this.delegateIterator.previous();
         }

         @Override
         public int nextIndex() {
            return CollectionsKt__ReversedViewsKt.access$reverseIteratorIndex(this.this$0, this.delegateIterator.previousIndex());
         }

         @Override
         public T previous() {
            return this.delegateIterator.next();
         }

         @Override
         public int previousIndex() {
            return CollectionsKt__ReversedViewsKt.access$reverseIteratorIndex(this.this$0, this.delegateIterator.nextIndex());
         }

         @Override
         public void add(T element) {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
         }

         @Override
         public void set(T element) {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
         }
      };
   }
}
