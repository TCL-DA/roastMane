package kotlin.sequences

import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

internal class DropWhileSequence<T>(sequence: Sequence<Any>, predicate: (Any) -> Boolean) : Sequence<T> {
   private final val predicate: (Any) -> Boolean
   private final val sequence: Sequence<Any>

   init {
      this.sequence = sequence;
      this.predicate = predicate;
   }

   public override operator fun iterator(): Iterator<Any> {
      return new java.util.Iterator<T>(this) {
         @NotNull
         private final java.util.Iterator<T> iterator;
         private int dropState;
         @Nullable
         private T nextItem;

         {
            this.this$0 = `$receiver`;
            this.iterator = DropWhileSequence.access$getSequence$p(`$receiver`).iterator();
            this.dropState = -1;
         }

         @NotNull
         public final java.util.Iterator<T> getIterator() {
            return this.iterator;
         }

         public final int getDropState() {
            return this.dropState;
         }

         public final void setDropState(int var1) {
            this.dropState = var1;
         }

         @Nullable
         public final T getNextItem() {
            return this.nextItem;
         }

         public final void setNextItem(@Nullable T var1) {
            this.nextItem = (T)var1;
         }

         private final void drop() {
            while (this.iterator.hasNext()) {
               var item: Any = this.iterator.next();
               if (!DropWhileSequence.access$getPredicate$p(this.this$0).invoke(item) as java.lang.Boolean) {
                  this.nextItem = (T)item;
                  this.dropState = 1;
                  return;
               }
            }

            this.dropState = 0;
         }

         @Override
         public T next() {
            if (this.dropState == -1) {
               this.drop();
            }

            if (this.dropState == 1) {
               var result: Any = this.nextItem;
               this.nextItem = null;
               this.dropState = 0;
               return (T)result;
            } else {
               return this.iterator.next();
            }
         }

         @Override
         public boolean hasNext() {
            if (this.dropState == -1) {
               this.drop();
            }

            return this.dropState == 1 || this.iterator.hasNext();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
         }
      };
   }
}
