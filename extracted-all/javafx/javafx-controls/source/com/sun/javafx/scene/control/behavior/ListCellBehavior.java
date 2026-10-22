package com.sun.javafx.scene.control.behavior;

import javafx.scene.control.FocusModel;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;

public class ListCellBehavior<T> extends CellBehaviorBase<ListCell<T>> {
   public ListCellBehavior(ListCell<T> var1) {
      super(var1);
   }

   @Override
   protected MultipleSelectionModel<T> getSelectionModel() {
      return this.getCellContainer().getSelectionModel();
   }

   @Override
   protected FocusModel<T> getFocusModel() {
      return this.getCellContainer().getFocusModel();
   }

   protected ListView<T> getCellContainer() {
      return this.getNode().getListView();
   }

   protected void edit(ListCell<T> var1) {
      int var2 = var1 == null ? -1 : var1.getIndex();
      this.getCellContainer().edit(var2);
   }
}
