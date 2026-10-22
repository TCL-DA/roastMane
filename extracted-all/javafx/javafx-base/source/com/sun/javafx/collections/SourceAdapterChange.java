package com.sun.javafx.collections;

import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class SourceAdapterChange<E> extends ListChangeListener.Change<E> {
   private final ListChangeListener.Change<? extends E> change;
   private int[] perm;

   public SourceAdapterChange(ObservableList<E> var1, ListChangeListener.Change<? extends E> var2) {
      super(var1);
      this.change = var2;
   }

   @Override
   public boolean next() {
      this.perm = null;
      return this.change.next();
   }

   @Override
   public void reset() {
      this.change.reset();
   }

   @Override
   public int getTo() {
      return this.change.getTo();
   }

   @Override
   public List<E> getRemoved() {
      return (List<E>)this.change.getRemoved();
   }

   @Override
   public int getFrom() {
      return this.change.getFrom();
   }

   @Override
   public boolean wasUpdated() {
      return this.change.wasUpdated();
   }

   @Override
   protected int[] getPermutation() {
      if (this.perm == null) {
         if (this.change.wasPermutated()) {
            int var1 = this.change.getFrom();
            int var2 = this.change.getTo() - var1;
            this.perm = new int[var2];

            for (int var3 = 0; var3 < var2; var3++) {
               this.perm[var3] = this.change.getPermutation(var1 + var3);
            }
         } else {
            this.perm = new int[0];
         }
      }

      return this.perm;
   }

   @Override
   public String toString() {
      return this.change.toString();
   }
}
