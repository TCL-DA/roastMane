package kotlin.collections

import org.jetbrains.annotations.NotNull

private class ReversedList<T>(delegate: MutableList<Any>) : AbstractMutableList<T> {
   private final val delegate: MutableList<Any>
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

   public override fun clear() {
      this.delegate.clear();
   }

   public override fun removeAt(index: Int): Any {
      return this.delegate.remove(CollectionsKt__ReversedViewsKt.access$reverseElementIndex(this, index));
   }

   public override operator fun set(index: Int, element: Any): Any {
      return this.delegate.set(CollectionsKt__ReversedViewsKt.access$reverseElementIndex(this, index), (T)element);
   }

   public override fun add(index: Int, element: Any) {
      this.delegate.add(CollectionsKt__ReversedViewsKt.access$reversePositionIndex(this, index), (T)element);
   }

   public override operator fun iterator(): MutableIterator<Any> {
      return this.listIterator(0);
   }

   public override fun listIterator(): MutableListIterator<Any> {
      return this.listIterator(0);
   }

   public override fun listIterator(index: Int): MutableListIterator<Any> {
      return new java.util.ListIterator<T>(this, index) {
         @NotNull
         private final java.util.ListIterator<T> delegateIterator;

         {
            this.this$0 = `$receiver`;
            this.delegateIterator = ReversedList.access$getDelegate$p(`$receiver`)
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
            this.delegateIterator.add((T)element);
            this.delegateIterator.previous();
         }

         @Override
         public void remove() {
            this.delegateIterator.remove();
         }

         @Override
         public void set(T element) {
            this.delegateIterator.set((T)element);
         }
      };
   }
}
