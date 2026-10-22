package com.sun.javafx.scene.control;

import java.util.BitSet;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TablePositionBase;

public abstract class SelectedCellsMap<T extends TablePositionBase> {
   private final ObservableList<T> selectedCells = FXCollections.observableArrayList();
   private final ObservableList<T> sortedSelectedCells = new SortedList(this.selectedCells, (var0, var1x) -> {
      int var2 = var0.getRow() - var1x.getRow();
      return var2 == 0 ? var0.getColumn() - var1x.getColumn() : var2;
   });
   private final Map<Integer, BitSet> selectedCellBitSetMap;

   public SelectedCellsMap(ListChangeListener<T> var1) {
      this.sortedSelectedCells.addListener(var1);
      this.selectedCellBitSetMap = new TreeMap<>((var0, var1x) -> var0.compareTo(var1x));
   }

   public abstract boolean isCellSelectionEnabled();

   public int size() {
      return this.selectedCells.size();
   }

   public T get(int var1) {
      return (T)(var1 < 0 ? null : this.sortedSelectedCells.get(var1));
   }

   public void add(T var1) {
      int var2 = var1.getRow();
      int var3 = var1.getColumn();
      boolean var4 = false;
      BitSet var5;
      if (!this.selectedCellBitSetMap.containsKey(var2)) {
         var5 = new BitSet();
         this.selectedCellBitSetMap.put(var2, var5);
         var4 = true;
      } else {
         var5 = this.selectedCellBitSetMap.get(var2);
      }

      boolean var6 = this.isCellSelectionEnabled();
      if (var6) {
         if (var3 >= 0) {
            boolean var7 = var5.get(var3);
            if (!var7) {
               var5.set(var3);
               this.selectedCells.add(var1);
            }
         } else if (!this.selectedCells.contains(var1)) {
            this.selectedCells.add(var1);
         }
      } else if (var4) {
         if (var3 >= 0) {
            var5.set(var3);
         }

         this.selectedCells.add(var1);
      }
   }

   public void addAll(Collection<T> var1) {
      for (TablePositionBase var3 : var1) {
         int var4 = var3.getRow();
         int var5 = var3.getColumn();
         BitSet var6;
         if (!this.selectedCellBitSetMap.containsKey(var4)) {
            var6 = new BitSet();
            this.selectedCellBitSetMap.put(var4, var6);
         } else {
            var6 = this.selectedCellBitSetMap.get(var4);
         }

         if (var5 >= 0) {
            var6.set(var5);
         }
      }

      this.selectedCells.addAll(var1);
   }

   public void setAll(Collection<T> var1) {
      this.selectedCellBitSetMap.clear();

      for (TablePositionBase var3 : var1) {
         int var4 = var3.getRow();
         int var5 = var3.getColumn();
         BitSet var6;
         if (!this.selectedCellBitSetMap.containsKey(var4)) {
            var6 = new BitSet();
            this.selectedCellBitSetMap.put(var4, var6);
         } else {
            var6 = this.selectedCellBitSetMap.get(var4);
         }

         if (var5 >= 0) {
            var6.set(var5);
         }
      }

      this.selectedCells.setAll(var1);
   }

   public void remove(T var1) {
      int var2 = var1.getRow();
      int var3 = var1.getColumn();
      if (this.selectedCellBitSetMap.containsKey(var2)) {
         BitSet var4 = this.selectedCellBitSetMap.get(var2);
         if (var3 >= 0) {
            var4.clear(var3);
         }

         if (var4.isEmpty()) {
            this.selectedCellBitSetMap.remove(var2);
         }
      }

      this.selectedCells.remove(var1);
   }

   public void clear() {
      this.selectedCellBitSetMap.clear();
      this.selectedCells.clear();
   }

   public boolean isSelected(int var1, int var2) {
      if (var2 < 0) {
         return this.selectedCellBitSetMap.containsKey(var1);
      } else {
         return this.selectedCellBitSetMap.containsKey(var1) ? this.selectedCellBitSetMap.get(var1).get(var2) : false;
      }
   }

   public int indexOf(T var1) {
      return this.sortedSelectedCells.indexOf(var1);
   }

   public boolean isEmpty() {
      return this.selectedCells.isEmpty();
   }

   public ObservableList<T> getSelectedCells() {
      return this.selectedCells;
   }
}
