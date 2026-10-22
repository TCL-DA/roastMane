package kotlin.sequences

import java.util.NoSuchElementException
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

internal class TakeWhileSequence<T>(sequence: Sequence<Any>, predicate: (Any) -> Boolean) : Sequence<T> {
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
         private int nextState;
         @Nullable
         private T nextItem;

         {
            this.this$0 = `$receiver`;
            this.iterator = TakeWhileSequence.access$getSequence$p(`$receiver`).iterator();
            this.nextState = -1;
         }

         @NotNull
         public final java.util.Iterator<T> getIterator() {
            return this.iterator;
         }

         public final int getNextState() {
            return this.nextState;
         }

         public final void setNextState(int var1) {
            this.nextState = var1;
         }

         @Nullable
         public final T getNextItem() {
            return this.nextItem;
         }

         public final void setNextItem(@Nullable T var1) {
            this.nextItem = (T)var1;
         }

         private final void calcNext() {
            if (this.iterator.hasNext()) {
               var item: Any = this.iterator.next();
               if (TakeWhileSequence.access$getPredicate$p(this.this$0).invoke(item) as java.lang.Boolean) {
                  this.nextState = 1;
                  this.nextItem = (T)item;
                  return;
               }
            }

            this.nextState = 0;
         }

         @Override
         public T next() {
            if (this.nextState == -1) {
               this.calcNext();
            }

            if (this.nextState == 0) {
               throw new NoSuchElementException();
            } else {
               var result: Any = this.nextItem;
               this.nextItem = null;
               this.nextState = -1;
               return (T)result;
            }
         }

         @Override
         public boolean hasNext() {
            if (this.nextState == -1) {
               this.calcNext();
            }

            return this.nextState == 1;
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
         }
      };
   }
}
