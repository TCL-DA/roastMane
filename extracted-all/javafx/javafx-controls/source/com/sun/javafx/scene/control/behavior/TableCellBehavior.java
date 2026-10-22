package com.sun.javafx.scene.control.behavior;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableView;

public class TableCellBehavior<S, T> extends TableCellBehaviorBase<S, T, TableColumn<S, ?>, TableCell<S, T>> {
   public TableCellBehavior(TableCell<S, T> var1) {
      super(var1);
   }

   protected TableView<S> getCellContainer() {
      return this.getNode().getTableView();
   }

   protected TableColumn<S, T> getTableColumn() {
      return this.getNode().getTableColumn();
   }

   @Override
   protected int getItemCount() {
      return this.getCellContainer().getItems().size();
   }

   protected TableView.TableViewSelectionModel<S> getSelectionModel() {
      return this.getCellContainer().getSelectionModel();
   }

   protected TableView.TableViewFocusModel<S> getFocusModel() {
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
      return this.getCellContainer().getVisibleLeafIndex((TableColumn<S, ?>)var1);
   }

   @Override
   protected void focus(int var1, TableColumnBase var2) {
      this.getFocusModel().focus(var1, (TableColumn<S, ?>)var2);
   }

   protected void edit(TableCell<S, T> var1) {
      if (var1 == null) {
         this.getCellContainer().edit(-1, null);
      } else {
         this.getCellContainer().edit(var1.getIndex(), var1.getTableColumn());
      }
   }
}
