package impl.org.controlsfx.spreadsheet;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.CellSkinBase;
import javafx.scene.layout.BorderStroke;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class GridRowSkin extends CellSkinBase<TableRow<ObservableList<SpreadsheetCell>>> {
   private final SpreadsheetHandle handle;
   private final SpreadsheetView spreadsheetView;
   private Reference<HashMap<TableColumnBase, CellView>> cellsMap;
   private final List<CellView> cells = new ArrayList<>();

   public GridRowSkin(SpreadsheetHandle handle, TableRow<ObservableList<SpreadsheetCell>> gridRow) {
      super(gridRow);
      this.handle = handle;
      this.spreadsheetView = handle.getView();
      ((TableRow)this.getSkinnable()).setPickOnBounds(false);
      gridRow.itemProperty().addListener(o -> this.requestCellUpdate());
      this.registerChangeListener(gridRow.indexProperty(), e -> {
         if (((TableRow)this.getSkinnable()).isEmpty()) {
            this.requestCellUpdate();
         }
      });
   }

   private void requestCellUpdate() {
      ((TableRow)this.getSkinnable()).requestLayout();
      int newIndex = ((TableRow)this.getSkinnable()).getIndex();
      this.getChildren().clear();
      int i = 0;

      for (int max = this.cells.size(); i < max; i++) {
         this.cells.get(i).updateIndex(newIndex);
      }
   }

   protected void layoutChildren(double x, double y, double w, double h) {
      ObservableList<? extends TableColumnBase<?, ?>> visibleLeafColumns = this.handle.getGridView().getVisibleLeafColumns();
      if (visibleLeafColumns.isEmpty()) {
         super.layoutChildren(x, y, w, h);
      } else {
         GridRow control = (GridRow)this.getSkinnable();
         SpreadsheetGridView gridView = this.handle.getGridView();
         int index = control.getIndex();
         if (index >= 0 && index < gridView.getItems().size()) {
            List<SpreadsheetCell> row = (List<SpreadsheetCell>)((TableRow)this.getSkinnable()).getItem();
            List<SpreadsheetColumn> columns = this.spreadsheetView.getColumns();
            ObservableList<TableColumn<ObservableList<SpreadsheetCell>, ?>> tableViewColumns = gridView.getColumns();
            if (columns.size() == tableViewColumns.size()) {
               ((TableRow)this.getSkinnable()).setVisible(true);
               double verticalPadding = this.snappedTopInset() + this.snappedBottomInset();
               double horizontalPadding = this.snappedLeftInset() + this.snappedRightInset();
               double controlHeight = this.getTableRowHeight(index);
               double customHeight = controlHeight == -1.0 ? 24.0 : controlHeight;
               GridViewSkin skin = this.handle.getCellsViewSkin();
               skin.hBarValue.set(index, true);
               double headerWidth = gridView.getWidth();
               double hbarValue = skin.getHBar().getValue();
               ((GridRow)this.getSkinnable()).verticalShift.setValue(this.getFixedRowShift(index));
               double fixedColumnWidth = 0.0;
               List<CellView> fixedCells = new ArrayList<>();
               this.putCellsInCache();
               boolean firstVisibleCell = false;
               CellView lastCell = null;
               boolean rowHeightChange = false;

               for (int indexColumn = 0; indexColumn < columns.size(); indexColumn++) {
                  if (((TableColumn)((TableView)skin.getSkinnable()).getColumns().get(indexColumn)).isVisible()) {
                     double width = this.snapSize(columns.get(indexColumn).getWidth()) - this.snapSize(horizontalPadding);
                     if (row.size() <= indexColumn) {
                        break;
                     }

                     SpreadsheetCell spreadsheetCell = row.get(indexColumn);
                     int columnSpan = this.spreadsheetView.getColumnSpan(spreadsheetCell);
                     boolean isVisible = !this.isInvisible(x, width, hbarValue, headerWidth, columnSpan);
                     boolean isFixed = columns.get(indexColumn).isFixed();
                     if (isFixed) {
                        isVisible = true;
                     }

                     if (!isVisible) {
                        if (firstVisibleCell) {
                           break;
                        }

                        x += width;
                     } else {
                        CellView tableCell = this.getCell((TableColumnBase)gridView.getColumns().get(indexColumn));
                        this.cells.add(0, tableCell);
                        tableCell.setManaged(true);
                        double tableCellX = 0.0;
                        boolean increaseFixedWidth = false;
                        int viewColumn = this.spreadsheetView.getViewColumn(spreadsheetCell.getColumn());
                        SpreadsheetView.SpanType spanType = null;
                        if (isFixed) {
                           spanType = this.spreadsheetView.getSpanType(index, indexColumn);
                           if (spanType != SpreadsheetView.SpanType.COLUMN_SPAN_INVISIBLE && hbarValue + fixedColumnWidth > x) {
                              increaseFixedWidth = true;
                              tableCellX = Math.abs(hbarValue - x + fixedColumnWidth);
                              fixedColumnWidth += width;
                              fixedCells.add(tableCell);
                           }
                        }

                        if (isVisible) {
                           spanType = spanType == null ? this.spreadsheetView.getSpanType(index, indexColumn) : spanType;
                           switch (spanType) {
                              case ROW_SPAN_INVISIBLE:
                              case BOTH_INVISIBLE:
                                 fixedCells.remove(tableCell);
                                 this.getChildren().remove(tableCell);
                                 x += width;
                                 continue;
                              case COLUMN_SPAN_INVISIBLE:
                                 fixedCells.remove(tableCell);
                                 this.getChildren().remove(tableCell);
                                 continue;
                              case ROW_VISIBLE:
                              case NORMAL_CELL:
                                 if (tableCell.getIndex() != index) {
                                    tableCell.updateIndex(index);
                                 } else {
                                    tableCell.updateItem(spreadsheetCell, false);
                                 }

                                 if (!tableCell.isEditing()
                                    && tableCell.getParent() != this.getSkinnable()
                                    && (!spreadsheetCell.isCellGraphic() || !skin.rowToLayout.get(index) || !fixedCells.contains(tableCell))) {
                                    this.getChildren().add(0, tableCell);
                                 }
                              default:
                                 if (columnSpan > 1) {
                                    int max = ((TableView)skin.getSkinnable()).getVisibleLeafColumns().size() - viewColumn;

                                    for (int i = 1; i < columnSpan && i < max; i++) {
                                       double tempWidth = this.snapSize(((TableView)skin.getSkinnable()).getVisibleLeafColumn(viewColumn + i).getWidth());
                                       width += tempWidth;
                                       if (increaseFixedWidth) {
                                          fixedColumnWidth += tempWidth;
                                       }
                                    }
                                 }

                                 if (controlHeight == -1.0 && !tableCell.isEditing()) {
                                    double tempHeight = tableCell.prefHeight(width) + tableCell.snappedTopInset() + tableCell.snappedBottomInset();
                                    if (tempHeight > customHeight) {
                                       rowHeightChange = true;
                                       skin.rowHeightMap.put(spreadsheetCell.getRow(), tempHeight);

                                       for (CellView cell : this.cells) {
                                          cell.resize(cell.getWidth(), cell.getHeight() + (tempHeight - customHeight));
                                       }

                                       customHeight = tempHeight;
                                       skin.getFlow().layoutChildren();
                                    }
                                 }

                                 double height = this.snapSize(customHeight) - this.snapSize(verticalPadding);
                                 if (spreadsheetCell.getRowSpan() > 1) {
                                    int rowSpan = this.spreadsheetView.getRowSpan(spreadsheetCell, index);
                                    height = 0.0;
                                    int reverseRowSpan = this.spreadsheetView.getReverseRowSpan(spreadsheetCell, index);
                                    int newIndex = index - reverseRowSpan;
                                    int maxRow = newIndex + reverseRowSpan + rowSpan;

                                    for (int ix = newIndex + 1; ix < maxRow; ix++) {
                                       height += this.snapSize(skin.getRowHeight(ix));
                                    }
                                 }

                                 boolean needToBeShifted = false;
                                 if (lastCell != null && !this.hasRightBorder(lastCell) && !this.hasLeftBorder(tableCell)) {
                                    tableCell.resize(width + 1.0, height);
                                    needToBeShifted = true;
                                 } else {
                                    tableCell.resize(width, height);
                                 }

                                 lastCell = tableCell;
                                 double spaceBetweenTopAndMe = 0.0;
                                 if (spreadsheetCell.getRowSpan() > 1) {
                                    int reverseRowSpan = this.spreadsheetView.getReverseRowSpan(spreadsheetCell, index);
                                    int newIndex = index - reverseRowSpan;

                                    for (int p = newIndex + 1; p < index; p++) {
                                       spaceBetweenTopAndMe += skin.getRowHeight(p);
                                    }
                                 }

                                 tableCell.relocate(
                                    x + tableCellX + (double)(needToBeShifted ? -1 : 0),
                                    this.snappedTopInset() - spaceBetweenTopAndMe + ((GridRow)this.getSkinnable()).verticalShift.get()
                                 );
                           }
                        } else {
                           this.getChildren().remove(tableCell);
                        }

                        x += width;
                     }
                  }
               }

               skin.fixedColumnWidth = fixedColumnWidth;
               this.handleFixedCell(fixedCells, index);
               this.removeUselessCell(index);
               if (this.handle.getCellsViewSkin().lastRowLayout.get()) {
                  this.handle.getCellsViewSkin().lastRowLayout.setValue(false);
               }

               if (rowHeightChange && this.spreadsheetView.getFixedRows().contains(this.spreadsheetView.getModelRow(index))) {
                  skin.computeFixedRowHeight();
               }
            }
         } else {
            this.getChildren().clear();
            this.putCellsInCache();
         }
      }
   }

   private boolean hasRightBorder(CellView tableCell) {
      return tableCell.getBorder() != null
         && !tableCell.getBorder().isEmpty()
         && ((BorderStroke)tableCell.getBorder().getStrokes().get(0)).getWidths().getRight() > 0.0;
   }

   private boolean hasLeftBorder(CellView tableCell) {
      return tableCell.getBorder() != null
         && !tableCell.getBorder().isEmpty()
         && ((BorderStroke)tableCell.getBorder().getStrokes().get(0)).getWidths().getLeft() > 0.0;
   }

   private void removeUselessCell(int index) {
      this.getChildren().removeIf(t -> !(t instanceof CellView) ? false : !this.cells.contains(t) && ((CellView)t).getIndex() == index);
   }

   private void removeDeportedCells() {
      GridViewSkin skin = this.handle.getCellsViewSkin();

      for (Entry<GridRow, Set<CellView>> entry : skin.deportedCells.entrySet()) {
         ArrayList<CellView> toRemove = new ArrayList<>();

         for (CellView cell : entry.getValue()) {
            if (!cell.isEditing() && cell.getTableRow() == this.getSkinnable() && entry.getKey() != this.getSkinnable()) {
               entry.getKey().removeCell(cell);
               toRemove.add(cell);
            }
         }

         entry.getValue().removeAll(toRemove);
      }
   }

   private void handleFixedCell(List<CellView> fixedCells, int index) {
      this.removeDeportedCells();
      if (!fixedCells.isEmpty()) {
         GridViewSkin skin = this.handle.getCellsViewSkin();
         if (skin.rowToLayout.get(index)) {
            GridRow topRow = skin.getFlow().getTopRow();
            if (topRow != null) {
               for (CellView cell : fixedCells) {
                  if (!cell.isEditing()) {
                     topRow.removeCell(cell);
                     topRow.addCell(cell);
                  }

                  double originalLayoutY = ((TableRow)this.getSkinnable()).getLayoutY() + cell.getLayoutY();
                  if (skin.deportedCells.containsKey(topRow)) {
                     skin.deportedCells.get(topRow).add(cell);
                  } else {
                     Set<CellView> temp = new HashSet<>();
                     temp.add(cell);
                     skin.deportedCells.put(topRow, temp);
                  }

                  cell.relocate(cell.getLayoutX(), originalLayoutY - topRow.getLayoutY());
               }
            }
         } else {
            for (CellView cell : fixedCells) {
               cell.toFront();
            }
         }
      }
   }

   private HashMap<TableColumnBase, CellView> getCellsMap() {
      if (this.cellsMap != null && this.cellsMap.get() != null) {
         return this.cellsMap.get();
      } else {
         HashMap<TableColumnBase, CellView> map = new HashMap<>();
         this.cellsMap = new WeakReference<>(map);
         return map;
      }
   }

   private void putCellsInCache() {
      for (CellView cell : this.cells) {
         this.getCellsMap().put(cell.getTableColumn(), cell);
      }

      this.cells.clear();
   }

   private CellView getCell(TableColumnBase tcb) {
      TableColumn tableColumn = (TableColumn)tcb;
      if (this.getCellsMap().containsKey(tableColumn)) {
         return this.getCellsMap().remove(tableColumn);
      } else {
         CellView cell = (CellView)tableColumn.getCellFactory().call(tableColumn);
         cell.updateTableColumn(tableColumn);
         cell.updateTableView(tableColumn.getTableView());
         cell.updateTableRow((TableRow)this.getSkinnable());
         return cell;
      }
   }

   private double getFixedRowShift(int index) {
      double tableCellY = 0.0;
      int positionY = this.spreadsheetView.getFixedRows().indexOf(this.spreadsheetView.getFilteredSourceIndex(index));
      double space = 0.0;

      for (int o = 0; o < positionY; o++) {
         if (!this.spreadsheetView.isRowHidden(o)) {
            space += this.handle.getCellsViewSkin().getRowHeight((Integer)this.spreadsheetView.getFixedRows().get(o));
         }
      }

      if (positionY != -1 && ((TableRow)this.getSkinnable()).getLocalToParentTransform().getTy() <= space) {
         tableCellY = space - ((TableRow)this.getSkinnable()).getLocalToParentTransform().getTy();
         this.handle.getCellsViewSkin().getCurrentlyFixedRow().add(index);
      } else {
         this.handle.getCellsViewSkin().getCurrentlyFixedRow().remove(index);
      }

      return tableCellY;
   }

   private double getTableRowHeight(int row) {
      Double rowHeightCache = (Double)this.handle.getCellsViewSkin().rowHeightMap.get(this.spreadsheetView.getModelRow(row));
      return rowHeightCache == null ? this.handle.getView().getGrid().getRowHeight(this.spreadsheetView.getModelRow(row)) : rowHeightCache;
   }

   private boolean isInvisible(double x, double width, double hbarValue, double headerWidth, int columnSpan) {
      return x + width < hbarValue && columnSpan == 1 || x > hbarValue + headerWidth;
   }

   protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      double prefWidth = 0.0;
      List<? extends TableColumnBase> visibleLeafColumns = this.handle.getGridView().getVisibleLeafColumns();
      int i = 0;

      for (int max = visibleLeafColumns.size(); i < max; i++) {
         prefWidth += visibleLeafColumns.get(i).getWidth();
      }

      return prefWidth;
   }

   protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return ((TableRow)this.getSkinnable()).getPrefHeight();
   }

   protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return ((TableRow)this.getSkinnable()).getPrefHeight();
   }

   protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return super.computeMaxHeight(width, topInset, rightInset, bottomInset, leftInset);
   }
}
