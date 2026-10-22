package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.skin.Utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableFocusModel;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;

public class TableViewBehavior<T> extends TableViewBehaviorBase<TableView<T>, T, TableColumn<T, ?>> {
   private final ChangeListener<TableView.TableViewSelectionModel<T>> selectionModelListener = (var1x, var2x, var3) -> {
      if (var2x != null) {
         var2x.getSelectedCells().removeListener(this.weakSelectedCellsListener);
      }

      if (var3 != null) {
         var3.getSelectedCells().addListener(this.weakSelectedCellsListener);
      }
   };
   private final WeakChangeListener<TableView.TableViewSelectionModel<T>> weakSelectionModelListener = new WeakChangeListener(this.selectionModelListener);
   private TwoLevelFocusBehavior tlFocus;

   public TableViewBehavior(TableView<T> var1) {
      super(var1);
      var1.selectionModelProperty().addListener(this.weakSelectionModelListener);
      TableView.TableViewSelectionModel var2 = var1.getSelectionModel();
      if (var2 != null) {
         var2.getSelectedCells().addListener(this.selectedCellsListener);
      }

      if (Utils.isTwoLevelFocus()) {
         this.tlFocus = new TwoLevelFocusBehavior(var1);
      }
   }

   @Override
   public void dispose() {
      if (this.tlFocus != null) {
         this.tlFocus.dispose();
      }

      super.dispose();
   }

   @Override
   protected int getItemCount() {
      return this.getNode().getItems() == null ? 0 : this.getNode().getItems().size();
   }

   @Override
   protected TableFocusModel getFocusModel() {
      return this.getNode().getFocusModel();
   }

   @Override
   protected TableSelectionModel<T> getSelectionModel() {
      return this.getNode().getSelectionModel();
   }

   @Override
   protected ObservableList<TablePosition> getSelectedCells() {
      return this.getNode().getSelectionModel().getSelectedCells();
   }

   @Override
   protected TablePositionBase getFocusedCell() {
      return this.getNode().getFocusModel().getFocusedCell();
   }

   @Override
   protected int getVisibleLeafIndex(TableColumnBase var1) {
      return this.getNode().getVisibleLeafIndex((TableColumn<T, ?>)var1);
   }

   protected TableColumn<T, ?> getVisibleLeafColumn(int var1) {
      return this.getNode().getVisibleLeafColumn(var1);
   }

   @Override
   protected boolean isControlEditable() {
      return this.getNode().isEditable();
   }

   @Override
   protected void editCell(int var1, TableColumnBase var2) {
      this.getNode().edit(var1, (TableColumn<T, ?>)var2);
   }

   @Override
   protected ObservableList<TableColumn<T, ?>> getVisibleLeafColumns() {
      return this.getNode().getVisibleLeafColumns();
   }

   @Override
   protected TablePositionBase<TableColumn<T, ?>> getTablePosition(int var1, TableColumnBase<T, ?> var2) {
      return new TablePosition<>(this.getNode(), var1, (TableColumn<T, T>)var2);
   }

   @Override
   protected void selectAllToFocus(boolean var1) {
      if (this.getNode().getEditingCell() == null) {
         super.selectAllToFocus(var1);
      }
   }
}
