package com.sun.javafx.scene.control;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.ListChangeListener.Change;

public abstract class SelectedItemsReadOnlyObservableList<E> extends ObservableListBase<E> {
   private final ObservableList<Integer> selectedIndices;
   private final Supplier<Integer> modelSizeSupplier;
   private final List<WeakReference<E>> itemsRefList;

   public SelectedItemsReadOnlyObservableList(ObservableList<Integer> var1, Supplier<Integer> var2) {
      this.modelSizeSupplier = var2;
      this.selectedIndices = var1;
      this.itemsRefList = new ArrayList<>();
      var1.addListener(var2x -> {
         int var3 = 0;
         this.beginChange();

         while (var2x.next()) {
            if (var2x.wasReplaced()) {
               List var8 = this.getRemovedElements(var2x, var3);
               List var10 = this.getAddedElements(var2x);
               if (!var8.equals(var10)) {
                  this.nextReplace(var2x.getFrom(), var2x.getTo(), var8);
               }
            } else if (var2x.wasAdded()) {
               this.nextAdd(var2x.getFrom(), var2x.getTo());
            } else if (var2x.wasRemoved()) {
               int var7 = var2x.getRemovedSize();
               if (var7 == 1) {
                  this.nextRemove(var2x.getFrom(), this.getRemovedModelItem(var3 + var2x.getFrom()));
               } else {
                  this.nextRemove(var2x.getFrom(), this.getRemovedElements(var2x, var3));
               }

               var3 += var7;
            } else if (!var2x.wasPermutated()) {
               if (var2x.wasUpdated()) {
                  for (int var6 = var2x.getFrom(); var6 < var2x.getTo(); var6++) {
                     this.nextUpdate(var6);
                  }
               }
            } else {
               int[] var4 = new int[this.size()];

               for (int var5 = 0; var5 < this.size(); var5++) {
                  var4[var5] = var2x.getPermutation(var5);
               }

               this.nextPermutation(var2x.getFrom(), var2x.getTo(), var4);
            }
         }

         this.itemsRefList.clear();

         for (int var11 : var1) {
            this.itemsRefList.add(new WeakReference<>(this.getModelItem(var11)));
         }

         this.endChange();
      });
   }

   protected abstract E getModelItem(int var1);

   public E get(int var1) {
      int var2 = (Integer)this.selectedIndices.get(var1);
      return this.getModelItem(var2);
   }

   public int size() {
      return this.selectedIndices.size();
   }

   private E _getModelItem(int var1) {
      return var1 >= this.modelSizeSupplier.get() ? this.getRemovedModelItem(var1) : this.getModelItem(var1);
   }

   private E getRemovedModelItem(int var1) {
      return var1 >= 0 && var1 < this.itemsRefList.size() ? this.itemsRefList.get(var1).get() : null;
   }

   private List<E> getRemovedElements(Change<? extends Integer> var1, int var2) {
      ArrayList var3 = new ArrayList(var1.getRemovedSize());
      int var4 = var1.getFrom();
      int var5 = var4;

      for (int var6 = var4 + var1.getRemovedSize(); var5 < var6; var5++) {
         var3.add(this.getRemovedModelItem(var5 + var2));
      }

      return var3;
   }

   private List<E> getAddedElements(Change<? extends Integer> var1) {
      ArrayList var2 = new ArrayList(var1.getAddedSize());

      for (int var4 : var1.getAddedSubList()) {
         var2.add(this._getModelItem(var4));
      }

      return var2;
   }
}
