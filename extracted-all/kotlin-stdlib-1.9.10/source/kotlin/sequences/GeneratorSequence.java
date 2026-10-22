package kotlin.sequences

import java.util.NoSuchElementException
import kotlin.jvm.functions.Function1
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

private class GeneratorSequence<T>(getInitialValue: () -> Any?, getNextValue: (Any) -> Any?) : Sequence<T> {
   private final val getInitialValue: () -> Any?
   private final val getNextValue: (Any) -> Any?

   init {
      this.getInitialValue = getInitialValue;
      this.getNextValue = getNextValue;
   }

   public override operator fun iterator(): Iterator<Any> {
      return new java.util.Iterator<T>(this) {
         @Nullable
         private T nextItem;
         private int nextState;

         {
            this.this$0 = `$receiver`;
            this.nextState = -2;
         }

         @Nullable
         public final T getNextItem() {
            return this.nextItem;
         }

         public final void setNextItem(@Nullable T var1) {
            this.nextItem = (T)var1;
         }

         public final int getNextState() {
            return this.nextState;
         }

         public final void setNextState(int var1) {
            this.nextState = var1;
         }

         private final void calcNext() {
            var var10001: Any;
            if (this.nextState == -2) {
               var10001 = (Function1)GeneratorSequence.access$getGetInitialValue$p(this.this$0).invoke();
            } else {
               var10001 = GeneratorSequence.access$getGetNextValue$p(this.this$0);
               var var10002: Any = this.nextItem;
               var10001 = (Function1)var10001.invoke(var10002);
            }

            this.nextItem = (T)var10001;
            this.nextState = if (this.nextItem == null) 0 else 1;
         }

         @NotNull
         @Override
         public T next() {
            if (this.nextState < 0) {
               this.calcNext();
            }

            if (this.nextState == 0) {
               throw new NoSuchElementException();
            } else {
               var var10000: Any = this.nextItem;
               this.nextState = -1;
               return (T)var10000;
            }
         }

         @Override
         public boolean hasNext() {
            if (this.nextState < 0) {
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
