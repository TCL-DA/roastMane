package com.sun.javafx.scene.control.behavior;

import javafx.collections.ObservableList;
import javafx.scene.control.TableFocusModel;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseButton;

public class TreeTableRowBehavior<T> extends TableRowBehaviorBase<TreeTableRow<T>> {
   public TreeTableRowBehavior(TreeTableRow<T> var1) {
      super(var1);
   }

   @Override
   protected TableSelectionModel<TreeItem<T>> getSelectionModel() {
      return this.getCellContainer().getSelectionModel();
   }

   protected TableFocusModel<TreeItem<T>, ?> getFocusModel() {
      return this.getCellContainer().getFocusModel();
   }

   protected TreeTableView<T> getCellContainer() {
      return this.getNode().getTreeTableView();
   }

   @Override
   protected TablePositionBase<?> getFocusedCell() {
      return this.getCellContainer().getFocusModel().getFocusedCell();
   }

   @Override
   protected ObservableList getVisibleLeafColumns() {
      return this.getCellContainer().getVisibleLeafColumns();
   }

   protected void edit(TreeTableRow<T> var1) {
   }

   @Override
   protected void handleClicks(MouseButton var1, int var2, boolean var3) {
      TreeItem var4 = this.getNode().getTreeItem();
      if (var1 == MouseButton.PRIMARY) {
         if (var2 == 1 && var3) {
            this.edit(this.getNode());
         } else if (var2 == 1) {
            this.edit(null);
         } else if (var2 == 2 && var4.isLeaf()) {
            this.edit(this.getNode());
         } else if (var2 % 2 == 0) {
            var4.setExpanded(!var4.isExpanded());
         }
      }
   }
}
