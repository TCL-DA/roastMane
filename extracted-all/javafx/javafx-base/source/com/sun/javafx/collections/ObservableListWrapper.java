package com.sun.javafx.collections;

import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;
import javafx.util.Callback;

public class ObservableListWrapper<E> extends ModifiableObservableListBase<E> implements ObservableList<E>, SortableList<E>, RandomAccess {
   private final List<E> backingList;
   private final ElementObserver elementObserver;
   private SortHelper helper;

   public ObservableListWrapper(List<E> var1) {
      this.backingList = var1;
      this.elementObserver = null;
   }

   public ObservableListWrapper(List<E> var1, Callback<E, Observable[]> var2) {
      this.backingList = var1;
      this.elementObserver = new ElementObserver<>(var2, new Callback<E, InvalidationListener>() {
         public InvalidationListener call(final E var1) {
            return new InvalidationListener() {
               @Override
               public void invalidated(Observable var1x) {
                  ObservableListWrapper.this.beginChange();
                  int var2 = 0;

                  for (int var3 = ObservableListWrapper.this.size(); var2 < var3; var2++) {
                     if (ObservableListWrapper.this.get(var2) == var1) {
                        ObservableListWrapper.this.nextUpdate(var2);
                     }
                  }

                  ObservableListWrapper.this.endChange();
               }
            };
         }
      }, this);
      int var3 = this.backingList.size();

      for (int var4 = 0; var4 < var3; var4++) {
         this.elementObserver.attachListener(this.backingList.get(var4));
      }
   }

   @Override
   public E get(int var1) {
      return this.backingList.get(var1);
   }

   @Override
   public int size() {
      return this.backingList.size();
   }

   @Override
   protected void doAdd(int var1, E var2) {
      if (this.elementObserver != null) {
         this.elementObserver.attachListener(var2);
      }

      this.backingList.add(var1, (E)var2);
   }

   @Override
   protected E doSet(int var1, E var2) {
      Object var3 = this.backingList.set(var1, (E)var2);
      if (this.elementObserver != null) {
         this.elementObserver.detachListener(var3);
         this.elementObserver.attachListener(var2);
      }

      return (E)var3;
   }

   @Override
   protected E doRemove(int var1) {
      Object var2 = this.backingList.remove(var1);
      if (this.elementObserver != null) {
         this.elementObserver.detachListener(var2);
      }

      return (E)var2;
   }

   @Override
   public int indexOf(Object var1) {
      return this.backingList.indexOf(var1);
   }

   @Override
   public int lastIndexOf(Object var1) {
      return this.backingList.lastIndexOf(var1);
   }

   @Override
   public boolean contains(Object var1) {
      return this.backingList.contains(var1);
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      return this.backingList.containsAll(var1);
   }

   @Override
   public void clear() {
      if (this.elementObserver != null) {
         int var1 = this.size();

         for (int var2 = 0; var2 < var1; var2++) {
            this.elementObserver.detachListener(this.get(var2));
         }
      }

      if (this.hasListeners()) {
         this.beginChange();
         this.nextRemove(0, this);
      }

      this.backingList.clear();
      this.modCount++;
      if (this.hasListeners()) {
         this.endChange();
      }
   }

   @Override
   public void remove(int var1, int var2) {
      this.beginChange();

      for (int var3 = var1; var3 < var2; var3++) {
         this.remove(var1);
      }

      this.endChange();
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      this.beginChange();
      BitSet var2 = new BitSet(var1.size());

      for (int var3 = 0; var3 < this.size(); var3++) {
         if (var1.contains(this.get(var3))) {
            var2.set(var3);
         }
      }

      if (!var2.isEmpty()) {
         int var4 = this.size();

         while ((var4 = var2.previousSetBit(var4 - 1)) >= 0) {
            this.remove(var4);
         }
      }

      this.endChange();
      return !var2.isEmpty();
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      this.beginChange();
      BitSet var2 = new BitSet(var1.size());

      for (int var3 = 0; var3 < this.size(); var3++) {
         if (!var1.contains(this.get(var3))) {
            var2.set(var3);
         }
      }

      if (!var2.isEmpty()) {
         int var4 = this.size();

         while ((var4 = var2.previousSetBit(var4 - 1)) >= 0) {
            this.remove(var4);
         }
      }

      this.endChange();
      return !var2.isEmpty();
   }

   @Override
   public void sort() {
      if (!this.backingList.isEmpty()) {
         int[] var1 = this.getSortHelper().sort(this.backingList);
         this.fireChange(new NonIterableChange.SimplePermutationChange<>(0, this.size(), var1, this));
      }
   }

   @Override
   public void sort(Comparator<? super E> var1) {
      if (!this.backingList.isEmpty()) {
         int[] var2 = this.getSortHelper().sort(this.backingList, var1);
         this.fireChange(new NonIterableChange.SimplePermutationChange<>(0, this.size(), var2, this));
      }
   }

   private SortHelper getSortHelper() {
      if (this.helper == null) {
         this.helper = new SortHelper();
      }

      return this.helper;
   }
}
