package com.sun.javafx.scene.control;

import com.sun.javafx.collections.ObservableListWrapper;
import com.sun.javafx.collections.NonIterableChange.SimplePermutationChange;
import java.util.List;
import java.util.ListIterator;
import javafx.scene.control.Tab;

public class TabObservableList<E> extends ObservableListWrapper<E> {
   private final List<E> tabList;

   public TabObservableList(List<E> var1) {
      super(var1);
      this.tabList = var1;
   }

   public void reorder(Tab var1, Tab var2) {
      if (this.tabList.contains(var1) && this.tabList.contains(var2) && var1 != var2) {
         Object[] var3 = this.tabList.toArray();
         int var4 = this.tabList.indexOf(var1);
         int var5 = this.tabList.indexOf(var2);
         if (var4 != -1 && var5 != -1) {
            int var6 = (var5 - var4) / Math.abs(var5 - var4);

            for (int var7 = var4; var7 != var5; var7 += var6) {
               var3[var7] = var3[var7 + var6];
            }

            var3[var5] = var1;
            ListIterator var13 = this.tabList.listIterator();

            for (int var8 = 0; var8 < this.tabList.size(); var8++) {
               var13.next();
               var13.set(var3[var8]);
            }

            var1.getTabPane().getSelectionModel().select(var1);
            int var14 = Math.abs(var5 - var4) + 1;
            int[] var9 = new int[var14];
            int var10 = var6 > 0 ? var4 : var5;
            int var11 = var6 < 0 ? var4 : var5;

            for (int var12 = 0; var12 < var14; var12++) {
               var9[var12] = var12 + var10;
            }

            this.fireChange(new SimplePermutationChange(var10, var11 + 1, var9, this));
         }
      }
   }
}
