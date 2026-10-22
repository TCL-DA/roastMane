package org.glassfish.jersey.internal.guava;

import java.util.NoSuchElementException;

abstract class AbstractSequentialIterator<T> extends UnmodifiableIterator<T> {
   private T nextOrNull;

   AbstractSequentialIterator(T firstOrNull) {
      this.nextOrNull = firstOrNull;
   }

   protected abstract T computeNext(T var1);

   @Override
   public final boolean hasNext() {
      return this.nextOrNull != null;
   }

   @Override
   public final T next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         Object var1;
         try {
            var1 = this.nextOrNull;
         } finally {
            this.nextOrNull = this.computeNext(this.nextOrNull);
         }

         return (T)var1;
      }
   }
}
