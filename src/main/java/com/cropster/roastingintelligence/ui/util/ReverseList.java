package com.cropster.roastingintelligence.ui.util;

import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.transformation.TransformationList;

public class ReverseList<E> extends TransformationList<E, E> {
   public ReverseList(ObservableList<? extends E> source) {
      super(source);
   }

   public E get(int index) {
      return (E)this.getSource().get(this.getSourceIndex(index));
   }

   public int getSourceIndex(int index) {
      return this.getSource().size() - index - 1;
   }

   public int getViewIndex(int index) {
      return this.getSource().size() - index - 1;
   }

   public int size() {
      return this.getSource().size();
   }

   protected void sourceChanged(Change<? extends E> c) {
      this.beginChange();

      while (c.next()) {
         if (c.wasPermutated()) {
            int index = 0;
            int[] permutations = new int[c.getTo() - c.getFrom()];

            for (int i = c.getTo() - 1; i >= c.getFrom(); i--) {
               permutations[index++] = this.getViewIndex(c.getPermutation(i));
            }

            this.nextPermutation(this.getViewIndex(c.getTo() - 1), this.getViewIndex(c.getFrom() + 1), permutations);
         } else if (c.wasUpdated()) {
            for (int i = c.getTo() - 1; i >= c.getFrom(); i--) {
               this.nextUpdate(this.getViewIndex(i));
            }
         } else if (c.wasReplaced()) {
            this.nextReplace(this.getViewIndex(c.getTo() - 1), this.getViewIndex(c.getFrom() + 1), c.getRemoved());
         } else if (c.wasAdded()) {
            this.nextAdd(this.getViewIndex(c.getTo() - 1), this.getViewIndex(c.getFrom() + 1));
         } else if (c.wasRemoved()) {
            this.nextRemove(this.getViewIndex(c.getTo() - 1), c.getRemoved());
         }
      }

      this.endChange();
   }
}
