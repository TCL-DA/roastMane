package com.sun.javafx.collections;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ImmutableObservableList<E> extends AbstractList<E> implements ObservableList<E> {
   private final E[] elements;

   public ImmutableObservableList(E... var1) {
      this.elements = (E[])(var1 != null && var1.length != 0 ? Arrays.copyOf(var1, var1.length) : null);
   }

   @Override
   public void addListener(InvalidationListener var1) {
   }

   @Override
   public void removeListener(InvalidationListener var1) {
   }

   @Override
   public void addListener(ListChangeListener<? super E> var1) {
   }

   @Override
   public void removeListener(ListChangeListener<? super E> var1) {
   }

   @Override
   public boolean addAll(E... var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean setAll(E... var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean setAll(Collection<? extends E> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(E... var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(E... var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void remove(int var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public E get(int var1) {
      if (var1 >= 0 && var1 < this.size()) {
         return this.elements[var1];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public int size() {
      return this.elements == null ? 0 : this.elements.length;
   }
}
