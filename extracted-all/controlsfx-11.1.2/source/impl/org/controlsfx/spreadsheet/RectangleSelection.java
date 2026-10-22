package impl.org.controlsfx.spreadsheet;

import java.util.List;
import java.util.TreeSet;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.spreadsheet.ClipboardCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class RectangleSelection extends Rectangle {
   private final GridViewSkin skin;
   private final TableViewSpanSelectionModel sm;
   private final RectangleSelection.SelectionRange selectionRange;
   private final InvalidationListener selectedCellListener;
   private final InvalidationListener layoutListener = observable -> this.updateRectangle();
   private final EventHandler<MouseEvent> mouseDraggedListener = new EventHandler<MouseEvent>() {
      public void handle(MouseEvent event) {
         RectangleSelection.this.skin.getVBar().valueProperty().removeListener(RectangleSelection.this.layoutListener);
         RectangleSelection.this.setVisible(false);
         RectangleSelection.this.skin.getVBar().addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
               RectangleSelection.this.skin.getVBar().removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
               RectangleSelection.this.skin.getVBar().valueProperty().addListener(RectangleSelection.this.layoutListener);
               RectangleSelection.this.updateRectangle();
            }
         });
      }
   };

   public RectangleSelection(GridViewSkin skin, TableViewSpanSelectionModel sm) {
      this.skin = skin;
      this.sm = sm;
      this.getStyleClass().add("selection-rectangle");
      this.setMouseTransparent(true);
      this.selectionRange = new RectangleSelection.SelectionRange();
      this.selectedCellListener = observable -> {
         skin.getHorizontalHeader().clearSelectedColumns();
         skin.verticalHeader.clearSelectedRows();
         this.selectionRange.fill(sm.getSelectedCells(), skin.spreadsheetView);
         this.updateRectangle();
      };
      skin.getVBar().valueProperty().addListener(this.layoutListener);
      skin.getVBar().addEventFilter(MouseEvent.MOUSE_DRAGGED, this.mouseDraggedListener);
      skin.spreadsheetView.hiddenRowsProperty().addListener(this.selectedCellListener);
      skin.spreadsheetView.hiddenColumnsProperty().addListener(this.selectedCellListener);
      skin.getHBar().valueProperty().addListener(this.layoutListener);
      sm.getSelectedCells().addListener(this.selectedCellListener);
   }

   public final void updateRectangle() {
      if (!this.sm.getSelectedCells().isEmpty()
         && !this.skin.getSelectedRows().isEmpty()
         && !this.skin.getSelectedColumns().isEmpty()
         && this.selectionRange.range != null) {
         IndexedCell topRowCell = this.skin.getFlow().getTopRow();
         if (topRowCell != null) {
            int topRow = topRowCell.getIndex();
            IndexedCell bottomRowCell = (IndexedCell)this.skin.getFlow().getCells().get(this.skin.getFlow().getCells().size() - 1);
            if (bottomRowCell != null) {
               int bottomRow = bottomRowCell.getIndex();
               int minRow = this.selectionRange.range.getTop();
               if (minRow > bottomRow) {
                  this.setVisible(false);
               } else {
                  minRow = Math.max(minRow, topRow);
                  int maxRow = this.selectionRange.range.getBottom();
                  if (maxRow < topRow) {
                     this.setVisible(false);
                  } else {
                     maxRow = Math.min(maxRow, bottomRow);
                     int minColumn = this.selectionRange.range.getLeft();
                     int maxColumn = this.selectionRange.range.getRight();
                     GridRow gridMinRow = this.skin.getRowIndexed(minRow);
                     if (gridMinRow == null) {
                        this.setVisible(false);
                     } else if (maxRow < this.skin.getItemCount()
                        && maxColumn < ((TableView)this.skin.getSkinnable()).getVisibleLeafColumns().size()
                        && minColumn >= 0) {
                        SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)((TableView)this.skin.getSkinnable()).getItems().get(maxRow))
                           .get(this.skin.spreadsheetView.getModelColumn(maxColumn));
                        this.handleHorizontalPositioning(
                           minColumn, this.skin.spreadsheetView.getViewColumn(cell.getColumn()) + this.skin.spreadsheetView.getColumnSpan(cell) - 1
                        );
                        if (this.getX() + this.getWidth() < 0.0) {
                           this.setVisible(false);
                        } else {
                           GridRow gridMaxRow = this.skin.getRowIndexed(maxRow);
                           if (gridMaxRow == null) {
                              this.setVisible(false);
                           } else {
                              this.setVisible(true);
                              this.handleVerticalPositioning(minRow, maxRow, gridMinRow, gridMaxRow);
                           }
                        }
                     } else {
                        this.setVisible(false);
                     }
                  }
               }
            }
         }
      } else {
         this.setVisible(false);
      }
   }

   private void handleVerticalPositioning(int minRow, int maxRow, GridRow gridMinRow, GridRow gridMaxRow) {
      double height = 0.0;

      for (int i = maxRow; i <= maxRow; i++) {
         height += this.skin.getRowHeight(i);
      }

      if (!this.skin.getCurrentlyFixedRow().contains(minRow)) {
         this.yProperty().unbind();
         if (gridMinRow.getLayoutY() < this.skin.getFixedRowHeight()) {
            this.setY(this.skin.getFixedRowHeight());
         } else {
            this.yProperty().bind(gridMinRow.layoutYProperty());
         }
      } else {
         this.yProperty().bind(gridMinRow.layoutYProperty().add(gridMinRow.verticalShift));
      }

      this.heightProperty().bind(gridMaxRow.layoutYProperty().add(gridMaxRow.verticalShift).subtract(this.yProperty()).add(height));
   }

   private void handleHorizontalPositioning(int minColumn, int maxColumn) {
      double x = 0.0;
      List<TableColumn<ObservableList<SpreadsheetCell>, ?>> visibleColumns = this.skin.handle.getGridView().getVisibleLeafColumns();
      List<TableColumn<ObservableList<SpreadsheetCell>, ?>> allColumns = this.skin.handle.getGridView().getColumns();
      List<SpreadsheetColumn> columns = this.skin.spreadsheetView.getColumns();
      if (visibleColumns.size() > minColumn && visibleColumns.size() > maxColumn) {
         for (int i = 0; i < minColumn; i++) {
            x += this.snapSize(visibleColumns.get(i).getWidth());
         }

         x -= this.skin.getHBar().getValue();
         double width = 0.0;

         for (int i = minColumn; i <= maxColumn; i++) {
            width += this.snapSize(visibleColumns.get(i).getWidth());
         }

         int col = this.skin.spreadsheetView.getModelColumn(minColumn);
         if (col < columns.size() && !this.skin.spreadsheetView.getFixedColumns().contains(columns.get(col))) {
            if (x < this.skin.fixedColumnWidth) {
               width -= this.skin.fixedColumnWidth - x;
               x = this.skin.fixedColumnWidth;
            }
         } else if (x + width < this.skin.fixedColumnWidth) {
            x = 0.0;
            width = 0.0;

            for (SpreadsheetColumn column : this.skin.spreadsheetView.getFixedColumns()) {
               int indexColumn = this.skin.spreadsheetView.getViewColumn(columns.indexOf(column));
               if (indexColumn < minColumn && indexColumn != minColumn) {
                  x += this.snapSize(column.getWidth());
               }

               if (indexColumn >= minColumn && indexColumn <= maxColumn) {
                  width += this.snapSize(column.getWidth());
               }
            }
         } else if (x < this.skin.fixedColumnWidth) {
            double tempX = 0.0;

            for (SpreadsheetColumn column : this.skin.spreadsheetView.getFixedColumns()) {
               int indexColumnx = this.skin.spreadsheetView.getViewColumn(columns.indexOf(column));
               if (indexColumnx < minColumn && indexColumnx != minColumn) {
                  tempX += this.snapSize(column.getWidth());
               }
            }

            width -= tempX - x;
            x = tempX;
         }

         this.setX(x);
         this.setWidth(width);
      }
   }

   private double snapSize(double value) {
      return Math.ceil(value);
   }

   public static class GridRange {
      private final int top;
      private final int bottom;
      private final int left;
      private final int right;

      public GridRange(int top, int bottom, int left, int right) {
         this.top = top;
         this.bottom = bottom;
         this.left = left;
         this.right = right;
      }

      public int getTop() {
         return this.top;
      }

      public int getBottom() {
         return this.bottom;
      }

      public int getLeft() {
         return this.left;
      }

      public int getRight() {
         return this.right;
      }
   }

   public static class SelectionRange {
      private final TreeSet<Long> set = new TreeSet<>();
      private RectangleSelection.GridRange range;

      public void fill(List<TablePosition> list) {
         this.set.clear();

         for (TablePosition pos : list) {
            long key = key(pos.getRow(), pos.getColumn());
            this.set.add(key);
         }

         this.computeRange();
      }

      public void fill(List<TablePosition> list, SpreadsheetView spv) {
         this.set.clear();
         this.range = null;

         for (TablePosition pos : list) {
            long key = key(pos.getRow(), pos.getColumn());
            this.set.add(key);
            if (!spv.getGrid().isCellDisplaySelection(spv.getModelRow(pos.getRow()), spv.getModelColumn(pos.getColumn()))) {
               return;
            }
         }

         this.computeRange();
      }

      public void fillClipboardRange(List<ClipboardCell> list) {
         this.set.clear();
         this.range = null;

         for (ClipboardCell pos : list) {
            this.set.add(key(pos.getRow(), pos.getColumn()));
         }

         this.computeRange();
      }

      public RectangleSelection.GridRange getRange() {
         return this.range;
      }

      public static Long key(int row, int column) {
         return (long)row << 32 | (long)column;
      }

      private int getRow(Long l) {
         return (int)(l >> 32);
      }

      private int getColumn(Long l) {
         return (int)(l & -1L);
      }

      private void computeRange() {
         this.range = null;

         while (!this.set.isEmpty()) {
            if (this.range != null) {
               this.range = null;
               return;
            }

            long first = this.set.first();
            this.set.remove(first);
            int row = this.getRow(first);
            int column = this.getColumn(first);

            while (this.set.contains(key(row, column + 1))) {
               this.set.remove(key(row, ++column));
            }

            boolean flag = true;

            while (flag) {
               row++;

               for (int col = this.getColumn(first); col <= column; col++) {
                  if (!this.set.contains(key(row, col))) {
                     flag = false;
                     break;
                  }
               }

               if (flag) {
                  for (int colx = this.getColumn(first); colx <= column; colx++) {
                     this.set.remove(key(row, colx));
                  }
               } else {
                  row--;
               }
            }

            this.range = new RectangleSelection.GridRange(this.getRow(first), row, this.getColumn(first), column);
         }
      }
   }
}
