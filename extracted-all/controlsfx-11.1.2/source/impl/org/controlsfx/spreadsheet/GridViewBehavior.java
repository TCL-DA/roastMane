package impl.org.controlsfx.spreadsheet;

import com.sun.javafx.scene.control.behavior.TableViewBehavior;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableFocusModel;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;
import javafx.util.Pair;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

public class GridViewBehavior extends TableViewBehavior<ObservableList<SpreadsheetCell>> {
   private GridViewSkin skin;

   public GridViewBehavior(TableView<ObservableList<SpreadsheetCell>> control) {
      super(control);
   }

   void setGridViewSkin(GridViewSkin skin) {
      this.skin = skin;
   }

   protected void updateCellVerticalSelection(int delta, Runnable defaultAction) {
      TableViewSpanSelectionModel sm = (TableViewSpanSelectionModel)this.getSelectionModel();
      if (sm != null && sm.getSelectionMode() != SelectionMode.SINGLE) {
         TableFocusModel fm = this.getFocusModel();
         if (fm != null) {
            TablePositionBase focusedCell = this.getFocusedCell();
            if (this.isShiftDown && this.getAnchor() != null) {
               SpreadsheetCell cell = (SpreadsheetCell)focusedCell.getTableColumn().getCellData(focusedCell.getRow());
               sm.direction = new Pair(delta, 0);
               int newRow;
               if (delta < 0) {
                  newRow = this.skin.getFirstRow(cell, focusedCell.getRow()) + delta;
               } else {
                  newRow = focusedCell.getRow() + this.skin.spreadsheetView.getRowSpan(cell, focusedCell.getRow()) - 1 + delta;
               }

               newRow = Math.max(Math.min(this.getItemCount() - 1, newRow), 0);
               TablePositionBase<?> anchor = this.getAnchor();
               int minRow = Math.min(anchor.getRow(), newRow);
               int maxRow = Math.max(anchor.getRow(), newRow);
               int minColumn = Math.min(anchor.getColumn(), focusedCell.getColumn());
               int maxColumn = Math.max(anchor.getColumn(), focusedCell.getColumn());
               sm.clearSelection();
               if (minColumn != -1 && maxColumn != -1) {
                  sm.selectRange(
                     minRow, ((TableView)this.getNode()).getVisibleLeafColumn(minColumn), maxRow, ((TableView)this.getNode()).getVisibleLeafColumn(maxColumn)
                  );
               }

               fm.focus(newRow, focusedCell.getTableColumn());
            } else {
               int focusIndex = fm.getFocusedIndex();
               if (!sm.isSelected(focusIndex, focusedCell.getTableColumn())) {
                  sm.select(focusIndex, focusedCell.getTableColumn());
               }

               defaultAction.run();
            }
         }
      }
   }

   protected void updateCellHorizontalSelection(int delta, Runnable defaultAction) {
      TableViewSpanSelectionModel sm = (TableViewSpanSelectionModel)this.getSelectionModel();
      if (sm != null && sm.getSelectionMode() != SelectionMode.SINGLE) {
         TableFocusModel fm = this.getFocusModel();
         if (fm != null) {
            TablePositionBase focusedCell = this.getFocusedCell();
            if (focusedCell != null && focusedCell.getTableColumn() != null) {
               TableColumnBase adjacentColumn = this.getColumn(focusedCell.getTableColumn(), delta);
               if (adjacentColumn != null) {
                  int focusedCellRow = focusedCell.getRow();
                  if (this.isShiftDown && this.getAnchor() != null) {
                     SpreadsheetCell cell = (SpreadsheetCell)focusedCell.getTableColumn().getCellData(focusedCell.getRow());
                     sm.direction = new Pair(0, delta);
                     int newColumn;
                     if (delta < 0) {
                        newColumn = this.skin.spreadsheetView.getViewColumn(cell.getColumn()) + delta;
                     } else {
                        newColumn = this.skin.spreadsheetView.getViewColumn(cell.getColumn()) + this.skin.spreadsheetView.getColumnSpan(cell) - 1 + delta;
                     }

                     TablePositionBase<?> anchor = this.getAnchor();
                     int minRow = Math.min(anchor.getRow(), focusedCellRow);
                     int maxRow = Math.max(anchor.getRow(), focusedCellRow);
                     int minColumn = Math.min(anchor.getColumn(), newColumn);
                     int maxColumn = Math.max(anchor.getColumn(), newColumn);
                     sm.clearSelection();
                     if (minColumn != -1 && maxColumn != -1) {
                        sm.selectRange(
                           minRow,
                           ((TableView)this.getNode()).getVisibleLeafColumn(minColumn),
                           maxRow,
                           ((TableView)this.getNode()).getVisibleLeafColumn(maxColumn)
                        );
                     }

                     fm.focus(focusedCell.getRow(), this.getColumn(newColumn));
                  } else {
                     defaultAction.run();
                  }
               }
            }
         }
      }
   }

   protected void focusPreviousRow() {
      this.focusVertical(true);
   }

   protected void focusNextRow() {
      this.focusVertical(false);
   }

   protected void focusLeftCell() {
      this.focusHorizontal(true);
   }

   protected void focusRightCell() {
      this.focusHorizontal(false);
   }

   protected void discontinuousSelectPreviousRow() {
      this.discontinuousSelectVertical(true);
   }

   protected void discontinuousSelectNextRow() {
      this.discontinuousSelectVertical(false);
   }

   protected void discontinuousSelectPreviousColumn() {
      this.discontinuousSelectHorizontal(true);
   }

   protected void discontinuousSelectNextColumn() {
      this.discontinuousSelectHorizontal(false);
   }

   private void focusVertical(boolean previous) {
      TableSelectionModel sm = this.getSelectionModel();
      if (sm != null && sm.getSelectionMode() != SelectionMode.SINGLE) {
         TableFocusModel fm = this.getFocusModel();
         if (fm != null) {
            TablePositionBase focusedCell = this.getFocusedCell();
            if (focusedCell != null && focusedCell.getTableColumn() != null) {
               SpreadsheetCell cell = (SpreadsheetCell)focusedCell.getTableColumn().getCellData(focusedCell.getRow());
               sm.clearAndSelect(previous ? this.findPreviousRow(focusedCell, cell) : this.findNextRow(focusedCell, cell), focusedCell.getTableColumn());
               this.skin.focusScroll();
            }
         }
      }
   }

   private void focusHorizontal(boolean previous) {
      TableSelectionModel sm = this.getSelectionModel();
      if (sm != null) {
         TableFocusModel fm = this.getFocusModel();
         if (fm != null) {
            TablePositionBase focusedCell = this.getFocusedCell();
            if (focusedCell != null && focusedCell.getTableColumn() != null) {
               SpreadsheetCell cell = (SpreadsheetCell)focusedCell.getTableColumn().getCellData(focusedCell.getRow());
               sm.clearAndSelect(
                  focusedCell.getRow(),
                  ((TableView)this.getNode())
                     .getVisibleLeafColumn(previous ? this.findPreviousColumn(focusedCell, cell) : this.findNextColumn(focusedCell, cell))
               );
               this.skin.focusScroll();
            }
         }
      }
   }

   private int findPreviousRow(TablePositionBase focusedCell, SpreadsheetCell cell) {
      ObservableList<ObservableList<SpreadsheetCell>> items = ((TableView)this.getNode()).getItems();
      if (this.isEmpty(cell)) {
         for (int row = focusedCell.getRow() - 1; row >= 0; row--) {
            SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(row)).get(focusedCell.getColumn());
            if (!this.isEmpty(temp)) {
               return row;
            }
         }
      } else if (focusedCell.getRow() - 1 >= 0
         && !this.isEmpty((SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow() - 1)).get(focusedCell.getColumn()))) {
         for (int rowx = focusedCell.getRow() - 2; rowx >= 0; rowx--) {
            SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(rowx)).get(focusedCell.getColumn());
            if (this.isEmpty(temp)) {
               return rowx + 1;
            }
         }
      } else {
         for (int rowxx = focusedCell.getRow() - 2; rowxx >= 0; rowxx--) {
            SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(rowxx)).get(focusedCell.getColumn());
            if (!this.isEmpty(temp)) {
               return rowxx;
            }
         }
      }

      return 0;
   }

   public void selectCell(int rowDiff, int columnDiff) {
      TableViewSpanSelectionModel sm = (TableViewSpanSelectionModel)this.getSelectionModel();
      if (sm != null) {
         sm.direction = new Pair(rowDiff, columnDiff);
         TableFocusModel fm = this.getFocusModel();
         if (fm != null) {
            TablePositionBase focusedCell = this.getFocusedCell();
            int currentRow = focusedCell.getRow();
            int currentColumn = this.getVisibleLeafIndex(focusedCell.getTableColumn());
            if (rowDiff >= 0 || currentRow > 0) {
               if (rowDiff <= 0 || currentRow < this.getItemCount() - 1) {
                  if (columnDiff >= 0 || currentColumn > 0) {
                     if (columnDiff <= 0 || currentColumn < this.getVisibleLeafColumns().size() - 1) {
                        if (columnDiff <= 0 || currentColumn != -1) {
                           TableColumnBase tc = focusedCell.getTableColumn();
                           tc = this.getColumn(tc, columnDiff);
                           int row = focusedCell.getRow() + rowDiff;
                           sm.clearAndSelect(row, tc);
                           this.setAnchor(row, tc);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private int findNextRow(TablePositionBase focusedCell, SpreadsheetCell cell) {
      ObservableList<ObservableList<SpreadsheetCell>> items = ((TableView)this.getNode()).getItems();
      int itemCount = this.getItemCount();
      if (this.isEmpty(cell)) {
         for (int row = focusedCell.getRow() + 1; row < itemCount; row++) {
            SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(row)).get(focusedCell.getColumn());
            if (!this.isEmpty(temp)) {
               return row;
            }
         }
      } else if (focusedCell.getRow() + 1 < itemCount
         && !this.isEmpty((SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow() + 1)).get(focusedCell.getColumn()))) {
         for (int rowx = focusedCell.getRow() + 2; rowx < this.getItemCount(); rowx++) {
            SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(rowx)).get(focusedCell.getColumn());
            if (this.isEmpty(temp)) {
               return rowx - 1;
            }
         }
      } else {
         for (int rowxx = focusedCell.getRow() + 2; rowxx < itemCount; rowxx++) {
            SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(rowxx)).get(focusedCell.getColumn());
            if (!this.isEmpty(temp)) {
               return rowxx;
            }
         }
      }

      return itemCount - 1;
   }

   private void discontinuousSelectVertical(boolean previous) {
      TableSelectionModel sm = this.getSelectionModel();
      if (sm != null) {
         TableFocusModel fm = this.getFocusModel();
         if (fm != null) {
            TablePositionBase focusedCell = this.getFocusedCell();
            if (focusedCell != null && focusedCell.getTableColumn() != null) {
               SpreadsheetCell cell = (SpreadsheetCell)focusedCell.getTableColumn().getCellData(focusedCell.getRow());
               int newRow = previous ? this.findPreviousRow(focusedCell, cell) : this.findNextRow(focusedCell, cell);
               newRow = Math.max(Math.min(this.getItemCount() - 1, newRow), 0);
               TablePositionBase<?> anchor = this.getAnchor();
               int minRow = Math.min(anchor.getRow(), newRow);
               int maxRow = Math.max(anchor.getRow(), newRow);
               int minColumn = Math.min(anchor.getColumn(), focusedCell.getColumn());
               int maxColumn = Math.max(anchor.getColumn(), focusedCell.getColumn());
               sm.clearSelection();
               if (minColumn != -1 && maxColumn != -1) {
                  sm.selectRange(
                     minRow, ((TableView)this.getNode()).getVisibleLeafColumn(minColumn), maxRow, ((TableView)this.getNode()).getVisibleLeafColumn(maxColumn)
                  );
               }

               fm.focus(newRow, focusedCell.getTableColumn());
               this.skin.focusScroll();
            }
         }
      }
   }

   private void discontinuousSelectHorizontal(boolean previous) {
      TableSelectionModel sm = this.getSelectionModel();
      if (sm != null) {
         TableFocusModel fm = this.getFocusModel();
         if (fm != null) {
            TablePositionBase focusedCell = this.getFocusedCell();
            if (focusedCell != null && focusedCell.getTableColumn() != null) {
               int columnPos = this.getVisibleLeafIndex(focusedCell.getTableColumn());
               int focusedCellRow = focusedCell.getRow();
               SpreadsheetCell cell = (SpreadsheetCell)focusedCell.getTableColumn().getCellData(focusedCell.getRow());
               int newColumn = previous ? this.findPreviousColumn(focusedCell, cell) : this.findNextColumn(focusedCell, cell);
               TablePositionBase<?> anchor = this.getAnchor();
               int minRow = Math.min(anchor.getRow(), focusedCellRow);
               int maxRow = Math.max(anchor.getRow(), focusedCellRow);
               int minColumn = Math.min(anchor.getColumn(), newColumn);
               int maxColumn = Math.max(anchor.getColumn(), newColumn);
               sm.clearSelection();
               if (minColumn != -1 && maxColumn != -1) {
                  sm.selectRange(
                     minRow, ((TableView)this.getNode()).getVisibleLeafColumn(minColumn), maxRow, ((TableView)this.getNode()).getVisibleLeafColumn(maxColumn)
                  );
               }

               fm.focus(focusedCell.getRow(), this.getColumn(newColumn));
               this.skin.focusScroll();
            }
         }
      }
   }

   private int findNextColumn(TablePositionBase focusedCell, SpreadsheetCell cell) {
      ObservableList<ObservableList<SpreadsheetCell>> items = ((TableView)this.getNode()).getItems();
      int itemCount = ((TableView)this.getNode()).getColumns().size();
      if (this.isEmpty(cell)) {
         for (int column = focusedCell.getColumn() + 1; column < itemCount; column++) {
            SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow())).get(column);
            if (!this.isEmpty(temp)) {
               return column;
            }
         }
      } else if (focusedCell.getColumn() + 1 < itemCount
         && !this.isEmpty((SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow())).get(focusedCell.getColumn() + 1))) {
         for (int columnx = focusedCell.getColumn() + 2; columnx < itemCount; columnx++) {
            SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow())).get(columnx);
            if (this.isEmpty(temp)) {
               return columnx - 1;
            }
         }
      } else {
         for (int columnxx = focusedCell.getColumn() + 2; columnxx < itemCount; columnxx++) {
            SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow())).get(columnxx);
            if (!this.isEmpty(temp)) {
               return columnxx;
            }
         }
      }

      return itemCount - 1;
   }

   private int findPreviousColumn(TablePositionBase focusedCell, SpreadsheetCell cell) {
      ObservableList<ObservableList<SpreadsheetCell>> items = ((TableView)this.getNode()).getItems();
      if (this.isEmpty(cell)) {
         for (int column = focusedCell.getColumn() - 1; column >= 0; column--) {
            SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow())).get(column);
            if (!this.isEmpty(temp)) {
               return column;
            }
         }
      } else if (focusedCell.getColumn() - 1 >= 0
         && !this.isEmpty((SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow())).get(focusedCell.getColumn() - 1))) {
         for (int columnx = focusedCell.getColumn() - 2; columnx >= 0; columnx--) {
            SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow())).get(columnx);
            if (this.isEmpty(temp)) {
               return columnx + 1;
            }
         }
      } else {
         for (int columnxx = focusedCell.getColumn() - 2; columnxx >= 0; columnxx--) {
            SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow())).get(columnxx);
            if (!this.isEmpty(temp)) {
               return columnxx;
            }
         }
      }

      return 0;
   }

   private boolean isEmpty(SpreadsheetCell cell) {
      return cell.getGraphic() == null && (cell.getItem() == null || cell.getItem() instanceof Double && ((Double)cell.getItem()).isNaN());
   }
}
