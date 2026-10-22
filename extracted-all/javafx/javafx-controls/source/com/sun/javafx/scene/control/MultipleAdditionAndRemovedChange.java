package com.sun.javafx.scene.control;

import java.util.Collections;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;

public class MultipleAdditionAndRemovedChange<E> extends Change<E> {
   private static final int[] EMPTY_PERM = new int[0];
   private boolean invalid = true;
   private final List<E> addedElements;
   private final List<E> removedElements;
   private boolean iteratingThroughAdded = true;
   private boolean returnedRemovedElements = false;
   private int addedIndex = 0;
   private int from;
   private int to;

   public MultipleAdditionAndRemovedChange(List<E> var1, List<E> var2, ObservableList<E> var3) {
      super(var3);
      this.addedElements = var1;
      this.removedElements = var2;
   }

   public boolean next() {
      if (this.invalid) {
         this.invalid = false;
      }

      if (this.addedIndex >= this.addedElements.size()) {
         if (!this.returnedRemovedElements) {
            this.returnedRemovedElements = true;
            this.iteratingThroughAdded = false;
            this.from = 0;
            this.to = 0;
            return !this.removedElements.isEmpty();
         } else {
            return false;
         }
      } else {
         this.from = this.getList().indexOf(this.addedElements.get(this.addedIndex));
         this.to = this.from + 1;
         this.addedIndex++;

         for (int var1 = 0; var1 + this.addedIndex < this.addedElements.size(); var1++) {
            Object var2 = this.addedElements.get(var1 + this.addedIndex);
            if (var2 != this.getList().get(this.from + var1)) {
               this.to = this.from + 1 + var1;
               this.addedIndex += var1;
               break;
            }
         }

         return true;
      }
   }

   public void reset() {
      this.invalid = true;
      this.from = 0;
      this.to = 0;
      this.addedIndex = 0;
      this.iteratingThroughAdded = true;
      this.returnedRemovedElements = false;
   }

   public int getFrom() {
      this.checkState();
      return this.from;
   }

   public int getTo() {
      this.checkState();
      return this.to;
   }

   public List<E> getRemoved() {
      return this.iteratingThroughAdded ? Collections.emptyList() : this.removedElements;
   }

   protected int[] getPermutation() {
      return EMPTY_PERM;
   }

   public boolean wasAdded() {
      return this.iteratingThroughAdded && !this.addedElements.isEmpty();
   }

   public boolean wasRemoved() {
      return !this.iteratingThroughAdded && !this.removedElements.isEmpty();
   }

   private void checkState() {
      if (this.invalid) {
         throw new IllegalStateException("Invalid Change state: next() must be called before inspecting the Change.");
      }
   }
}
