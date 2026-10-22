package com.sun.javafx.scene.control.behavior;

import javafx.scene.Node;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseButton;

public class TreeTableCellBehavior<S, T> extends TableCellBehaviorBase<TreeItem<S>, T, TreeTableColumn<S, ?>, TreeTableCell<S, T>> {
   public TreeTableCellBehavior(TreeTableCell<S, T> var1) {
      super(var1);
   }

   protected TreeTableView<S> getCellContainer() {
      return this.getNode().getTreeTableView();
   }

   protected TreeTableColumn<S, T> getTableColumn() {
      return this.getNode().getTableColumn();
   }

   @Override
   protected int getItemCount() {
      return this.getCellContainer().getExpandedItemCount();
   }

   protected TreeTableView.TreeTableViewSelectionModel<S> getSelectionModel() {
      return this.getCellContainer().getSelectionModel();
   }

   protected TreeTableView.TreeTableViewFocusModel<S> getFocusModel() {
      return this.getCellContainer().getFocusModel();
   }

   @Override
   protected TablePositionBase getFocusedCell() {
      return this.getCellContainer().getFocusModel().getFocusedCell();
   }

   @Override
   protected boolean isTableRowSelected() {
      return this.getNode().getTableRow().isSelected();
   }

   @Override
   protected int getVisibleLeafIndex(TableColumnBase var1) {
      return this.getCellContainer().getVisibleLeafIndex((TreeTableColumn<S, ?>)var1);
   }

   @Override
   protected void focus(int var1, TableColumnBase var2) {
      this.getFocusModel().focus(var1, (TreeTableColumn<S, ?>)var2);
   }

   protected void edit(TreeTableCell<S, T> var1) {
      if (var1 == null) {
         this.getCellContainer().edit(-1, null);
      } else {
         this.getCellContainer().edit(var1.getIndex(), var1.getTableColumn());
      }
   }

   @Override
   protected boolean handleDisclosureNode(double var1, double var3) {
      TreeItem var5 = this.getNode().getTableRow().getTreeItem();
      TreeTableView var6 = this.getNode().getTreeTableView();
      TreeTableColumn var7 = this.getTableColumn();
      TreeTableColumn var8 = var6.getTreeColumn() == null ? var6.getVisibleLeafColumn(0) : var6.getTreeColumn();
      if (var7 == var8) {
         Node var9 = this.getNode().getTableRow().getDisclosureNode();
         if (var9 != null && var9.isVisible()) {
            double var10 = 0.0;

            for (TreeTableColumn var13 : var6.getVisibleLeafColumns()) {
               if (var13 == var8) {
                  break;
               }

               var10 += var13.getWidth();
            }

            double var14 = var9.getBoundsInParent().getMaxX();
            if (var1 < var14 - var10) {
               if (var5 != null) {
                  var5.setExpanded(!var5.isExpanded());
               }

               return true;
            }
         }
      }

      return false;
   }

   @Override
   protected void handleClicks(MouseButton var1, int var2, boolean var3) {
      TreeItem var4 = this.getNode().getTableRow().getTreeItem();
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
