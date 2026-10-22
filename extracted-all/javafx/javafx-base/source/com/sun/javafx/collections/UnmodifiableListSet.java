package com.sun.javafx.collections;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;

public final class UnmodifiableListSet<E> extends AbstractSet<E> {
   private List<E> backingList;

   public UnmodifiableListSet(List<E> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.backingList = var1;
      }
   }

   @Override
   public Iterator<E> iterator() {
      final Iterator var1 = this.backingList.iterator();
      return new Iterator<E>() {
         @Override
         public boolean hasNext() {
            return var1.hasNext();
         }

         @Override
         public E next() {
            return (E)var1.next();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public int size() {
      return this.backingList.size();
   }
}
