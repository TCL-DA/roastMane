package kotlin.sequences

import java.util.NoSuchElementException
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

internal class FlatteningSequence<T, R, E>(sequence: Sequence<Any>, transformer: (Any) -> Any, iterator: (Any) -> Iterator<Any>) : Sequence<E> {
   private final val iterator: (Any) -> Iterator<Any>
   private final val sequence: Sequence<Any>
   private final val transformer: (Any) -> Any

   init {
      this.sequence = sequence;
      this.transformer = transformer;
      this.iterator = iterator;
   }

   public override operator fun iterator(): Iterator<Any> {
      return new java.util.Iterator<E>(this) {
         @NotNull
         private final java.util.Iterator<T> iterator;
         @Nullable
         private java.util.Iterator<? extends E> itemIterator;

         {
            this.this$0 = `$receiver`;
            this.iterator = FlatteningSequence.access$getSequence$p(`$receiver`).iterator();
         }

         @NotNull
         public final java.util.Iterator<T> getIterator() {
            return this.iterator;
         }

         @Nullable
         public final java.util.Iterator<E> getItemIterator() {
            return (java.util.Iterator<E>)this.itemIterator;
         }

         public final void setItemIterator(@Nullable java.util.Iterator<? extends E> var1) {
            this.itemIterator = var1;
         }

         @Override
         public E next() {
            if (!this.ensureItemIterator()) {
               throw new NoSuchElementException();
            } else {
               var var10000: java.util.Iterator = this.itemIterator;
               return (E)var10000.next();
            }
         }

         @Override
         public boolean hasNext() {
            return this.ensureItemIterator();
         }

         private final boolean ensureItemIterator() {
            if (this.itemIterator != null && !this.itemIterator.hasNext()) {
               this.itemIterator = null;
            }

            while (this.itemIterator == null) {
               if (!this.iterator.hasNext()) {
                  return false;
               }

               var nextItemIterator: java.util.Iterator = FlatteningSequence.access$getIterator$p(this.this$0)
                  .invoke(FlatteningSequence.access$getTransformer$p(this.this$0).invoke(this.iterator.next())) as java.util.Iterator;
               if (nextItemIterator.hasNext()) {
                  this.itemIterator = nextItemIterator;
                  return true;
               }
            }

            return true;
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
         }
      };
   }
}
