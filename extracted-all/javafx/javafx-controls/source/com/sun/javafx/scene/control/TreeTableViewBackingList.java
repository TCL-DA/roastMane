package com.sun.javafx.scene.control;

import com.sun.javafx.collections.NonIterableChange.GenericAddRemoveChange;
import javafx.collections.FXCollections;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

public class TreeTableViewBackingList<T> extends ReadOnlyUnbackedObservableList<TreeItem<T>> {
   private final TreeTableView<T> treeTable;
   private int size = -1;

   public TreeTableViewBackingList(TreeTableView<T> var1) {
      this.treeTable = var1;
   }

   public void resetSize() {
      int var1 = this.size;
      this.size = -1;
      this.callObservers(new GenericAddRemoveChange(0, var1, FXCollections.emptyObservableList(), this));
   }

   public TreeItem<T> get(int var1) {
      return this.treeTable.getTreeItem(var1);
   }

   @Override
   public int size() {
      if (this.size == -1) {
         this.size = this.treeTable.getExpandedItemCount();
      }

      return this.size;
   }
}
