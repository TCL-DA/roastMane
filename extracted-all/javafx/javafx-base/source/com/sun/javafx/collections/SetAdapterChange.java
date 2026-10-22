package com.sun.javafx.collections;

import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class SetAdapterChange<E> extends SetChangeListener.Change<E> {
   private final SetChangeListener.Change<? extends E> change;

   public SetAdapterChange(ObservableSet<E> var1, SetChangeListener.Change<? extends E> var2) {
      super(var1);
      this.change = var2;
   }

   @Override
   public String toString() {
      return this.change.toString();
   }

   @Override
   public boolean wasAdded() {
      return this.change.wasAdded();
   }

   @Override
   public boolean wasRemoved() {
      return this.change.wasRemoved();
   }

   @Override
   public E getElementAdded() {
      return (E)this.change.getElementAdded();
   }

   @Override
   public E getElementRemoved() {
      return (E)this.change.getElementRemoved();
   }
}
