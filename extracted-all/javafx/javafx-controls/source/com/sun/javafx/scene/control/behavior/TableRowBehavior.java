package com.sun.javafx.scene.control.behavior;

import javafx.collections.ObservableList;
import javafx.scene.control.FocusModel;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;

public class TableRowBehavior<T> extends TableRowBehaviorBase<TableRow<T>> {
   public TableRowBehavior(TableRow<T> var1) {
      super(var1);
   }

   @Override
   protected TableSelectionModel<T> getSelectionModel() {
      return this.getCellContainer().getSelectionModel();
   }

   @Override
   protected TablePositionBase<?> getFocusedCell() {
      return this.getCellContainer().getFocusModel().getFocusedCell();
   }

   @Override
   protected FocusModel<T> getFocusModel() {
      return this.getCellContainer().getFocusModel();
   }

   @Override
   protected ObservableList getVisibleLeafColumns() {
      return this.getCellContainer().getVisibleLeafColumns();
   }

   protected TableView<T> getCellContainer() {
      return this.getNode().getTableView();
   }

   protected void edit(TableRow<T> var1) {
   }
}
