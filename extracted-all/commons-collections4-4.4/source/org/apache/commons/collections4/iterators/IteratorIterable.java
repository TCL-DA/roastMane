package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import org.apache.commons.collections4.ResettableIterator;

public class IteratorIterable<E> implements Iterable<E> {
   private final Iterator<? extends E> iterator;
   private final Iterator<E> typeSafeIterator;

   private static <E> Iterator<E> createTypesafeIterator(final Iterator<? extends E> iterator) {
      return new Iterator<E>() {
         @Override
         public boolean hasNext() {
            return iterator.hasNext();
         }

         @Override
         public E next() {
            return (E)iterator.next();
         }

         @Override
         public void remove() {
            iterator.remove();
         }
      };
   }

   public IteratorIterable(Iterator<? extends E> iterator) {
      this(iterator, false);
   }

   public IteratorIterable(Iterator<? extends E> iterator, boolean multipleUse) {
      if (multipleUse && !(iterator instanceof ResettableIterator)) {
         this.iterator = new ListIteratorWrapper<>(iterator);
      } else {
         this.iterator = iterator;
      }

      this.typeSafeIterator = createTypesafeIterator(this.iterator);
   }

   @Override
   public Iterator<E> iterator() {
      if (this.iterator instanceof ResettableIterator) {
         ((ResettableIterator)this.iterator).reset();
      }

      return this.typeSafeIterator;
   }
}
