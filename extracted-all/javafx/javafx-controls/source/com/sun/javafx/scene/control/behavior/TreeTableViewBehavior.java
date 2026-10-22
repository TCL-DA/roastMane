package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableFocusModel;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

public class TreeTableViewBehavior<T> extends TableViewBehaviorBase<TreeTableView<T>, TreeItem<T>, TreeTableColumn<T, ?>> {
   private final ChangeListener<TreeTableView.TreeTableViewSelectionModel<T>> selectionModelListener = (var1x, var2x, var3) -> {
      if (var2x != null) {
         var2x.getSelectedCells().removeListener(this.weakSelectedCellsListener);
      }

      if (var3 != null) {
         var3.getSelectedCells().addListener(this.weakSelectedCellsListener);
      }
   };
   private final WeakChangeListener<TreeTableView.TreeTableViewSelectionModel<T>> weakSelectionModelListener = new WeakChangeListener(
      this.selectionModelListener
   );

   public TreeTableViewBehavior(TreeTableView<T> var1) {
      super(var1);
      InputMap var2 = new InputMap(var1);
      var2.getMappings()
         .addAll(
            new InputMap.Mapping[]{
               new InputMap.KeyMapping(KeyCode.LEFT, var2x -> this.rtl(var1, this::expandRow, this::collapseRow)),
               new InputMap.KeyMapping(KeyCode.KP_LEFT, var2x -> this.rtl(var1, this::expandRow, this::collapseRow)),
               new InputMap.KeyMapping(KeyCode.RIGHT, var2x -> this.rtl(var1, this::collapseRow, this::expandRow)),
               new InputMap.KeyMapping(KeyCode.KP_RIGHT, var2x -> this.rtl(var1, this::collapseRow, this::expandRow)),
               new InputMap.KeyMapping(KeyCode.MULTIPLY, var1x -> this.expandAll()),
               new InputMap.KeyMapping(KeyCode.ADD, var1x -> this.expandRow()),
               new InputMap.KeyMapping(KeyCode.SUBTRACT, var1x -> this.collapseRow())
            }
         );
      this.addDefaultChildMap(this.getInputMap(), var2);
      var1.selectionModelProperty().addListener(this.weakSelectionModelListener);
      if (this.getSelectionModel() != null) {
         var1.getSelectionModel().getSelectedCells().addListener(this.selectedCellsListener);
      }
   }

   @Override
   protected int getItemCount() {
      return this.getNode().getExpandedItemCount();
   }

   @Override
   protected TableFocusModel getFocusModel() {
      return this.getNode().getFocusModel();
   }

   @Override
   protected TableSelectionModel<TreeItem<T>> getSelectionModel() {
      return this.getNode().getSelectionModel();
   }

   @Override
   protected ObservableList<TreeTablePosition<T, ?>> getSelectedCells() {
      return this.getNode().getSelectionModel().getSelectedCells();
   }

   @Override
   protected TablePositionBase getFocusedCell() {
      return this.getNode().getFocusModel().getFocusedCell();
   }

   @Override
   protected int getVisibleLeafIndex(TableColumnBase var1) {
      return this.getNode().getVisibleLeafIndex((TreeTableColumn<T, ?>)var1);
   }

   protected TreeTableColumn getVisibleLeafColumn(int var1) {
      return this.getNode().getVisibleLeafColumn(var1);
   }

   @Override
   protected boolean isControlEditable() {
      return this.getNode().isEditable();
   }

   @Override
   protected void editCell(int var1, TableColumnBase var2) {
      this.getNode().edit(var1, (TreeTableColumn<T, ?>)var2);
   }

   @Override
   protected ObservableList<TreeTableColumn<T, ?>> getVisibleLeafColumns() {
      return this.getNode().getVisibleLeafColumns();
   }

   @Override
   protected TablePositionBase<TreeTableColumn<T, ?>> getTablePosition(int var1, TableColumnBase<TreeItem<T>, ?> var2) {
      return new TreeTablePosition<>(this.getNode(), var1, (TreeTableColumn<T, T>)var2);
   }

   @Override
   protected void selectAllToFocus(boolean var1) {
      if (this.getNode().getEditingCell() == null) {
         super.selectAllToFocus(var1);
      }
   }

   private void rightArrowPressed() {
      if (this.getNode().getSelectionModel().isCellSelectionEnabled()) {
         if (this.isRTL()) {
            this.selectLeftCell();
         } else {
            this.selectRightCell();
         }
      } else {
         this.expandRow();
      }
   }

   private void leftArrowPressed() {
      if (this.getNode().getSelectionModel().isCellSelectionEnabled()) {
         if (this.isRTL()) {
            this.selectRightCell();
         } else {
            this.selectLeftCell();
         }
      } else {
         this.collapseRow();
      }
   }

   private void expandRow() {
      Callback var1 = var1x -> this.getNode().getRow(var1x);
      TreeViewBehavior.expandRow(this.getNode().getSelectionModel(), var1);
   }

   private void expandAll() {
      TreeViewBehavior.expandAll(this.getNode().getRoot());
   }

   private void collapseRow() {
      TreeTableView var1 = this.getNode();
      TreeViewBehavior.collapseRow(var1.getSelectionModel(), var1.getRoot(), var1.isShowRoot());
   }
}
