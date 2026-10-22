package org.glassfish.jersey.internal.guava;

import java.util.NoSuchElementException;

abstract class AbstractIndexedListIterator<E> extends UnmodifiableListIterator<E> {
   private final int size;
   private int position;

   AbstractIndexedListIterator(int size, int position) {
      Preconditions.checkPositionIndex(position, size);
      this.size = size;
      this.position = position;
   }

   protected abstract E get(int var1);

   @Override
   public final boolean hasNext() {
      return this.position < this.size;
   }

   @Override
   public final E next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.get(this.position++);
      }
   }

   @Override
   public final int nextIndex() {
      return this.position;
   }

   @Override
   public final boolean hasPrevious() {
      return this.position > 0;
   }

   @Override
   public final E previous() {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         return this.get(--this.position);
      }
   }

   @Override
   public final int previousIndex() {
      return this.position - 1;
   }
}
