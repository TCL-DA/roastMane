package impl.org.controlsfx.tableview2;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.skin.CellSkinBase;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.controlsfx.control.tableview2.TableView2;

public class TableRow2Skin<S> extends CellSkinBase<TableRow<S>> {
   private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
   private static final PseudoClass LEFT_CELL = PseudoClass.getPseudoClass("left");
   private static final PseudoClass RIGHT_CELL = PseudoClass.getPseudoClass("right");
   private static final PseudoClass SINGLE_CELL = PseudoClass.getPseudoClass("single");
   private static final PseudoClass FIXED_CELL = PseudoClass.getPseudoClass("fixed");
   private final TableView2<S> tableView;
   private final TableView2Skin<S> skin;
   private Reference<HashMap<TableColumnBase, TableCell<S, ?>>> cellsMap;
   private final List<TableCell<S, ?>> cells = new ArrayList<>();
   private final TableView2<S> parentTableView;

   public TableRow2Skin(TableView2<S> tableView, TableRow<S> tableRow) {
      super(tableRow);
      this.tableView = tableView;
      ((TableRow)this.getSkinnable()).setPickOnBounds(false);
      this.registerChangeListener(tableRow.itemProperty(), t -> this.requestCellUpdate());
      this.registerChangeListener(tableRow.indexProperty(), t -> {
         if (((TableRow)this.getSkinnable()).isEmpty()) {
            this.requestCellUpdate();
         }
      });
      if (tableView.getParent() != null && tableView.getParent() instanceof RowHeader) {
         this.parentTableView = ((RowHeader)tableView.getParent()).getParentTableView();
         this.skin = (TableView2Skin<S>)this.parentTableView.getSkin();
      } else {
         this.parentTableView = null;
         this.skin = (TableView2Skin<S>)tableView.getSkin();
      }
   }

   private void requestCellUpdate() {
      ((TableRow)this.getSkinnable()).requestLayout();
      int newIndex = ((TableRow)this.getSkinnable()).getIndex();
      int i = 0;

      for (int max = this.cells.size(); i < max; i++) {
         this.cells.get(i).updateIndex(newIndex);
      }
   }

   protected void layoutChildren(double x, double y, double w, double h) {
      ObservableList<? extends TableColumnBase<?, ?>> visibleLeafColumns = this.tableView.getVisibleLeafColumns();
      if (visibleLeafColumns.isEmpty()) {
         super.layoutChildren(x, y, w, h);
      } else {
         TableRow2<S> control = (TableRow2<S>)this.getSkinnable();
         int index = control.getIndex();
         if (index < 0) {
            this.getChildren().clear();
            this.putCellsInCache();
         } else {
            this.getChildren().removeIf(n -> n.getId() != null && n.getId().equals("pane-fixed-cell"));
            Object o = control.getProperties().get("fixed");
            boolean fixedRow = Boolean.TRUE.equals(o);
            List<? extends TableColumn<S, ?>> columns = this.tableView.getVisibleLeafColumns();
            ((TableRow)this.getSkinnable()).setVisible(true);
            double height = 0.0;
            double verticalPadding = this.snappedTopInset() + this.snappedBottomInset();
            double horizontalPadding = this.snappedLeftInset() + this.snappedRightInset();
            double customHeight = this.skin.getRowHeight(control.getIndex());
            this.skin.hBarValue.set(index, true);
            double headerWidth = this.tableView.getWidth();
            double hbarValue = this.parentTableView != null ? 0.0 : this.skin.getHBar().getValue();
            control.verticalShift.setValue(this.tableView.isRowFixingEnabled() ? this.getFixedRowShift(index) : 0.0);
            double fixedColumnWidth = 0.0;
            List<TableCell<S, ?>> fixedCells = new ArrayList<>();
            this.putCellsInCache();
            boolean firstVisibleCell = false;
            TableCell<S, ?> lastCell = null;
            boolean rowHeightChange = false;
            boolean isFirstColumn = false;

            for (int indexColumn = 0; indexColumn < columns.size(); indexColumn++) {
               TableColumn<S, ?> column = (TableColumn<S, ?>)columns.get(indexColumn);
               if (column.isVisible()) {
                  TablePosition<S, ?> pos = new TablePosition(this.tableView, index, column);
                  double width = this.snapSizeX(column.getWidth()) - this.snapSizeX(horizontalPadding);
                  int columnSpan = this.tableView.getColumnSpan(pos);
                  boolean isVisible = !this.isInvisible(x, width, hbarValue, headerWidth, columnSpan);
                  TableColumn<?, ?> rootColumn = column;

                  while (rootColumn.getParentColumn() != null) {
                     rootColumn = (TableColumn<?, ?>)rootColumn.getParentColumn();
                  }

                  if (this.tableView.isColumnFixingEnabled() && this.tableView.getFixedColumns().contains(rootColumn)) {
                     isVisible = true;
                  }

                  if (!isVisible) {
                     TableCell<S, ?> cell = this.getCellsMap().remove(column);
                     if (cell != null) {
                        this.getChildren().remove(cell);
                     }

                     if (firstVisibleCell) {
                        break;
                     }

                     x += width;
                  } else {
                     TableCell<S, ?> tableCell = this.getCell(column);
                     if (!isFirstColumn) {
                        tableCell.pseudoClassStateChanged(LEFT_CELL, true);
                        isFirstColumn = true;
                     }

                     this.cells.add(0, tableCell);
                     tableCell.setManaged(true);
                     double tableCellX = 0.0;
                     boolean increaseFixedWidth = false;
                     if (this.tableView.isColumnFixingEnabled() && this.tableView.getFixedColumns().contains(column) && hbarValue + fixedColumnWidth > x) {
                        increaseFixedWidth = true;
                        tableCellX = Math.abs(hbarValue - x + fixedColumnWidth);
                        fixedColumnWidth += width;
                        fixedCells.add(tableCell);
                     }

                     TableView2.SpanType spanType = this.tableView.getSpanType(index, indexColumn);
                     switch (spanType) {
                        case ROW_SPAN_INVISIBLE:
                        case BOTH_INVISIBLE:
                           fixedCells.remove(tableCell);
                           this.getChildren().remove(tableCell);
                           x += width;
                           break;
                        case COLUMN_SPAN_INVISIBLE:
                           fixedCells.remove(tableCell);
                           this.getChildren().remove(tableCell);
                           break;
                        case ROW_VISIBLE:
                        case NORMAL_CELL:
                           if (tableCell.getIndex() != index) {
                              tableCell.updateIndex(index);
                           } else {
                              tableCell.updateIndex(index);
                              tableCell.requestLayout();
                           }

                           if (!tableCell.isEditing() && tableCell.getParent() != this.getSkinnable()) {
                              this.getChildren().add(0, tableCell);
                           }
                        default:
                           if (this.tableView.getSelectionModel() != null && this.tableView.getSelectionModel().isCellSelectionEnabled()) {
                              boolean select = !this.tableView
                                 .getSelectionModel()
                                 .getSelectedCells()
                                 .stream()
                                 .noneMatch(cellx -> cellx.getRow() == index && cellx.getColumn() == indexColumn);
                              tableCell.pseudoClassStateChanged(SELECTED, select);
                           }

                           if (columnSpan > 1) {
                              int viewColumn = this.skin.getViewColumn(indexColumn);
                              int max = this.tableView.getVisibleLeafColumns().size() - viewColumn;
                              int i = 1;

                              for (int colSpan = columnSpan; i < colSpan && i < max; i++) {
                                 double tempWidth = this.snapSize(this.tableView.getVisibleLeafColumn(viewColumn + i).getWidth());
                                 width += tempWidth;
                                 if (increaseFixedWidth) {
                                    fixedColumnWidth += tempWidth;
                                 }
                              }
                           }

                           if (!tableCell.isEditing()) {
                              double tempHeight = tableCell.prefHeight(width) + tableCell.snappedTopInset() + tableCell.snappedBottomInset();
                              if (this.parentTableView != null && control.getIndex() < this.skin.getItemCount()) {
                                 tempHeight = (Double)this.skin.rowHeightMap.getOrDefault(control.getIndex(), tempHeight);
                              }

                              if (tempHeight > customHeight) {
                                 rowHeightChange = true;
                                 this.skin.rowHeightMap.put(control.getIndex(), tempHeight);

                                 for (TableCell<S, ?> cellx : this.cells) {
                                    cellx.resize(cellx.getWidth(), cellx.getHeight() + (tempHeight - customHeight));
                                 }

                                 customHeight = tempHeight;
                                 if (this.parentTableView != null) {
                                    ((TableView2Skin)this.tableView.getSkin()).getFlow().layoutChildren();
                                 } else {
                                    this.skin.getFlow().layoutChildren();
                                 }
                              }
                           }

                           height = this.snapSize(customHeight) - this.snapSize(verticalPadding);
                           int rowSpan = this.tableView.getRowSpan(pos, index);
                           if (rowSpan > 1) {
                              height = 0.0;
                              int maxRow = index + rowSpan;

                              for (int i = index; i < maxRow; i++) {
                                 height += this.snapSize(this.skin.getRowHeight(i));
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
                           tableCell.relocate(
                              x + tableCellX + (double)(needToBeShifted ? -1 : 0), this.snappedTopInset() - spaceBetweenTopAndMe + control.verticalShift.get()
                           );
                           x += width;
                     }
                  }
               }
            }

            double paneWidth = this.tableView.getWidth() - x - this.tableView.snappedLeftInset() - this.tableView.snappedRightInset();
            if (this.tableView.isRowHeaderVisible()) {
               paneWidth -= this.tableView.getRowHeaderWidth();
            }

            if (this.skin.getVBar().isVisible()) {
               paneWidth -= this.skin.getVBar().getWidth();
            }

            int paneBorderInset = 1;
            boolean paneBorderVisible = paneWidth > 1.0;
            boolean singleColumn = columns.size() == 1;
            if (fixedRow && paneWidth >= 0.0 && this.parentTableView == null) {
               Pane pane = new Pane();
               pane.setId("pane-fixed-cell");
               pane.getStyleClass().add("tableview2-cell");
               pane.pseudoClassStateChanged(RIGHT_CELL, paneBorderVisible);
               pane.resizeRelocate(x, this.snappedTopInset() + control.verticalShift.get(), paneWidth, height);
               this.getChildren().add(pane);
            }

            if (lastCell != null) {
               lastCell.pseudoClassStateChanged(RIGHT_CELL, !singleColumn && !paneBorderVisible);
               lastCell.pseudoClassStateChanged(SINGLE_CELL, singleColumn && !paneBorderVisible);
               if (this.parentTableView != null) {
                  lastCell.pseudoClassStateChanged(FIXED_CELL, fixedRow && control.getIndex() < this.skin.getItemCount());
               }
            }

            this.skin.fixedColumnWidth = fixedColumnWidth;
            this.handleFixedCell(fixedCells, index);
            this.removeUselessCell(index);
            if (this.skin.lastRowLayout.get()) {
               this.skin.lastRowLayout.setValue(false);
            }

            if (rowHeightChange && this.tableView.getFixedRows().contains(index)) {
               this.skin.computeFixedRowHeight();
            }
         }
      }
   }

   private boolean hasRightBorder(TableCell<S, ?> tableCell) {
      return tableCell.getBorder() != null
         && !tableCell.getBorder().isEmpty()
         && ((BorderStroke)tableCell.getBorder().getStrokes().get(0)).getWidths().getRight() > 0.0;
   }

   private boolean hasLeftBorder(TableCell<S, ?> tableCell) {
      return tableCell.getBorder() != null
         && !tableCell.getBorder().isEmpty()
         && ((BorderStroke)tableCell.getBorder().getStrokes().get(0)).getWidths().getLeft() > 0.0;
   }

   private void removeUselessCell(int index) {
      this.getChildren().removeIf(t -> !(t instanceof TableCell) ? false : !this.cells.contains((TableCell)t) && ((TableCell)t).getIndex() == index);
   }

   private void removeDeportedCells() {
      for (Entry<TableRow2<S>, Set<TableCell<S, ?>>> entry : this.skin.deportedCells.entrySet()) {
         ArrayList<TableCell<S, ?>> toRemove = new ArrayList<>();

         for (TableCell<S, ?> cell : entry.getValue()) {
            if (!cell.isEditing() && cell.getTableRow() == this.getSkinnable() && entry.getKey() != this.getSkinnable()) {
               entry.getKey().removeCell(cell);
               toRemove.add(cell);
            }
         }

         entry.getValue().removeAll(toRemove);
      }
   }

   private void handleFixedCell(List<TableCell<S, ?>> fixedCells, int index) {
      this.removeDeportedCells();
      if (!fixedCells.isEmpty()) {
         if (this.skin.rowToLayout.get(index)) {
            TableRow<S> tableRow = (TableRow<S>)this.getSkinnable();
            if (tableRow != null && tableRow instanceof TableRow2) {
               TableRow2<S> tableRow2 = (TableRow2<S>)tableRow;

               for (TableCell<S, ?> cell : fixedCells) {
                  if (!cell.isEditing()) {
                     tableRow2.removeCell(cell);
                     tableRow2.addCell(cell);
                  }

                  double originalLayoutY = ((TableRow)this.getSkinnable()).getLayoutY() + cell.getLayoutY();
                  if (this.skin.deportedCells.containsKey(tableRow2)) {
                     this.skin.deportedCells.get(tableRow2).add(cell);
                  } else {
                     Set<TableCell<S, ?>> temp = new HashSet<>();
                     temp.add(cell);
                     this.skin.deportedCells.put(tableRow2, temp);
                  }

                  cell.relocate(cell.getLayoutX(), originalLayoutY - tableRow.getLayoutY());
               }
            }
         } else {
            for (TableCell<S, ?> cell : fixedCells) {
               cell.toFront();
            }
         }
      }
   }

   private HashMap<TableColumnBase, TableCell<S, ?>> getCellsMap() {
      if (this.cellsMap != null && this.cellsMap.get() != null) {
         return this.cellsMap.get();
      } else {
         HashMap<TableColumnBase, TableCell<S, ?>> map = new HashMap<>();
         this.cellsMap = new WeakReference<>(map);
         return map;
      }
   }

   private void putCellsInCache() {
      for (TableCell<S, ?> cell : this.cells) {
         this.getCellsMap().put(cell.getTableColumn(), cell);
      }

      this.cells.clear();
   }

   private TableCell<S, ?> getCell(TableColumnBase tcb) {
      TableColumn tableColumn = (TableColumn)tcb;
      TableCell<S, ?> cell;
      if (this.getCellsMap().containsKey(tableColumn)) {
         cell = this.getCellsMap().remove(tableColumn);
      } else {
         Callback cellFactory = tableColumn.getCellFactory();
         if (cellFactory == null) {
            cellFactory = TableColumn.DEFAULT_CELL_FACTORY;
         }

         cell = (TableCell<S, ?>)cellFactory.call(tableColumn);
         if (!cell.getStyleClass().contains("tableview2-cell")) {
            cell.getStyleClass().add("tableview2-cell");
         }

         cell.updateTableColumn(tableColumn);
         cell.updateTableView(tableColumn.getTableView());
         cell.updateTableRow((TableRow)this.getSkinnable());
         if (this.parentTableView != null) {
            cell.setOnContextMenuRequested(e -> {
               BiFunction<Integer, S, ContextMenu> cmFactory = this.parentTableView.getRowHeaderContextMenuFactory();
               if (this.tableView.getItems() != null && cmFactory != null) {
                  ContextMenu contextMenu = cmFactory.apply(((TableRow)this.getSkinnable()).getIndex(), (S)((TableRow)this.getSkinnable()).getItem());
                  contextMenu.show(this.tableView.getScene().getWindow(), e.getScreenX(), e.getScreenY());
               }
            });
         }
      }

      cell.pseudoClassStateChanged(LEFT_CELL, false);
      cell.pseudoClassStateChanged(RIGHT_CELL, false);
      cell.pseudoClassStateChanged(SINGLE_CELL, false);
      return cell;
   }

   private double getFixedRowShift(int index) {
      double tableCellY = 0.0;
      int positionY = this.tableView.getFixedRows().indexOf(index);
      double space = 0.0;

      for (int o = 0; o < positionY; o++) {
         space += this.skin.getRowHeight((Integer)this.tableView.getFixedRows().get(o));
      }

      if (positionY != -1 && ((TableRow)this.getSkinnable()).getLocalToParentTransform().getTy() <= space) {
         tableCellY = space - ((TableRow)this.getSkinnable()).getLocalToParentTransform().getTy();
         this.skin.getCurrentlyFixedRow().add(index);
      } else {
         this.skin.getCurrentlyFixedRow().remove(index);
      }

      return tableCellY;
   }

   private boolean isInvisible(double x, double width, double hbarValue, double headerWidth, int columnSpan) {
      return x + width < hbarValue && columnSpan == 1 || x > hbarValue + headerWidth;
   }

   protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      double prefWidth = 0.0;
      List<? extends TableColumnBase> visibleLeafColumns = this.tableView.getVisibleLeafColumns();
      int i = 0;

      for (int max = visibleLeafColumns.size(); i < max; i++) {
         prefWidth += visibleLeafColumns.get(i).getWidth();
      }

      return prefWidth;
   }

   protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.parentTableView != null
         ? this.skin.getRow(((TableRow)this.getSkinnable()).getIndex()).getPrefHeight()
         : ((TableRow)this.getSkinnable()).getPrefHeight();
   }

   protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.parentTableView != null
         ? this.skin.getRow(((TableRow)this.getSkinnable()).getIndex()).getPrefHeight()
         : ((TableRow)this.getSkinnable()).getPrefHeight();
   }

   protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return super.computeMaxHeight(width, topInset, rightInset, bottomInset, leftInset);
   }
}
