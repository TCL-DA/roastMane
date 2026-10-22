package org.controlsfx.control.spreadsheet;

import impl.org.controlsfx.spreadsheet.TableViewSpanSelectionModel;
import java.util.Arrays;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TablePosition;
import javafx.util.Pair;

public class SpreadsheetViewSelectionModel {
   private final TableViewSpanSelectionModel selectionModel;
   private final SpreadsheetView spv;

   SpreadsheetViewSelectionModel(SpreadsheetView spv, TableViewSpanSelectionModel selectionModel) {
      this.spv = spv;
      this.selectionModel = selectionModel;
   }

   public final void clearAndSelect(int row, SpreadsheetColumn column) {
      this.selectionModel.clearAndSelect(row, column.column);
   }

   public final void clearAndSelect(SpreadsheetCell cell) {
      if (!this.spv.isRowHidden(cell.getRow()) && !this.spv.isColumnHidden(cell.getColumn())) {
         int row = this.spv.getViewRow(cell.getRow());
         if (row != -1) {
            this.selectionModel.clearAndSelect(row, ((SpreadsheetColumn)this.spv.getColumns().get(cell.getColumn())).column);
         }
      }
   }

   private void clearAndSelectView(int row, SpreadsheetColumn column) {
      this.selectionModel.clearAndSelect(row, column.column);
   }

   public final void select(int row, SpreadsheetColumn column) {
      this.selectionModel.select(this.spv.getFilteredRow(row), column.column);
   }

   public final void clearSelection() {
      this.selectionModel.clearSelection();
   }

   public final ObservableList<TablePosition> getSelectedCells() {
      return this.selectionModel.getSelectedCells();
   }

   public final void selectAll() {
      this.selectionModel.selectAll();
   }

   public final TablePosition getFocusedCell() {
      return this.selectionModel.getTableView().getFocusModel().getFocusedCell();
   }

   public final void focus(int row, SpreadsheetColumn column) {
      this.selectionModel.getTableView().getFocusModel().focus(row, column.column);
   }

   public final void setSelectionMode(SelectionMode value) {
      this.selectionModel.setSelectionMode(value);
   }

   public SelectionMode getSelectionMode() {
      return this.selectionModel.getSelectionMode();
   }

   public void selectCells(List<Pair<Integer, Integer>> selectedCells) {
      this.selectionModel.verifySelectedCells(selectedCells);
   }

   public void selectCells(Pair<Integer, Integer>... selectedCells) {
      this.selectionModel.verifySelectedCells(Arrays.asList(selectedCells));
   }

   public void selectRange(int minRow, SpreadsheetColumn minColumn, int maxRow, SpreadsheetColumn maxColumn) {
      this.selectionModel.selectRange(this.spv.getFilteredRow(minRow), minColumn.column, this.spv.getFilteredRow(maxRow), maxColumn.column);
   }

   public void clearAndSelectLeftCell() {
      TablePosition<ObservableList<SpreadsheetCell>, ?> position = this.getFocusedCell();
      int row = position.getRow();
      int column = position.getColumn();
      if (--column < 0) {
         if (row == 0) {
            column++;
         } else {
            column = this.selectionModel.getTableView().getVisibleLeafColumns().size() - 1;
            row--;
            this.selectionModel.direction = new Pair(-1, -1);
         }

         this.clearAndSelectView(row, (SpreadsheetColumn)this.spv.getColumns().get(this.spv.getModelColumn(column)));
      } else {
         this.spv.getCellsViewSkin().getBehavior().selectCell(0, -1);
      }
   }

   public void clearAndSelectRightCell() {
      TablePosition<ObservableList<SpreadsheetCell>, ?> position = this.getFocusedCell();
      int row = position.getRow();
      int column = position.getColumn();
      if (++column >= this.selectionModel.getTableView().getVisibleLeafColumns().size()) {
         if (row == this.spv.getGrid().getRowCount() - 1) {
            column--;
         } else {
            this.selectionModel.direction = new Pair(1, 1);
            column = 0;
            row++;
         }

         this.clearAndSelectView(row, (SpreadsheetColumn)this.spv.getColumns().get(this.spv.getModelColumn(column)));
      } else {
         this.spv.getCellsViewSkin().getBehavior().selectCell(0, 1);
      }
   }
}
