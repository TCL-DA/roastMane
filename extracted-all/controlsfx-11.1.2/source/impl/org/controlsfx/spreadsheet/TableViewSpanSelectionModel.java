package impl.org.controlsfx.spreadsheet;

import impl.org.controlsfx.collections.MappingChange;
import impl.org.controlsfx.collections.NonIterableChange;
import impl.org.controlsfx.collections.ReadOnlyUnbackedObservableList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.NamedArg;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import javafx.util.Pair;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class TableViewSpanSelectionModel extends TableViewSelectionModel<ObservableList<SpreadsheetCell>> {
   private boolean shift = false;
   private boolean key = false;
   private boolean drag = false;
   private MouseEvent mouseEvent;
   private boolean makeAtomic;
   private SpreadsheetGridView cellsView;
   private SpreadsheetView spreadsheetView;
   private final SelectedCellsMapTemp<TablePosition<ObservableList<SpreadsheetCell>, ?>> selectedCellsMap;
   private final ReadOnlyUnbackedObservableList<TablePosition<ObservableList<SpreadsheetCell>, ?>> selectedCellsSeq;
   private TableColumn oldTableColumn = null;
   private int oldRow = -1;
   public Pair<Integer, Integer> direction;
   private int oldColSpan = -1;
   private int oldRowSpan = -1;
   private final Timeline timer;
   private final EventHandler<ActionEvent> timerEventHandler = event -> {
      GridViewSkin skin = this.getCellsViewSkin();
      if (this.mouseEvent != null && !this.cellsView.contains(this.mouseEvent.getX(), this.mouseEvent.getY())) {
         double sceneX = this.mouseEvent.getSceneX();
         double sceneY = this.mouseEvent.getSceneY();
         double layoutX = this.cellsView.getLocalToSceneTransform().getTx();
         double layoutY = this.cellsView.getLocalToSceneTransform().getTy();
         double layoutXMax = layoutX + this.cellsView.getWidth();
         double layoutYMax = layoutY + this.cellsView.getHeight();
         if (sceneX > layoutXMax) {
            skin.getHBar().increment();
         } else if (sceneX < layoutX) {
            skin.getHBar().decrement();
         }

         if (sceneY > layoutYMax) {
            skin.getVBar().increment();
         } else if (sceneY < layoutY) {
            skin.getVBar().decrement();
         }
      }
   };
   private final EventHandler<MouseEvent> dragDoneHandler = new EventHandler<MouseEvent>() {
      public void handle(MouseEvent mouseEvent) {
         TableViewSpanSelectionModel.this.drag = false;
         TableViewSpanSelectionModel.this.timer.stop();
         TableViewSpanSelectionModel.this.spreadsheetView.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);
      }
   };
   private final EventHandler<KeyEvent> keyPressedEventHandler = keyEvent -> {
      this.key = true;
      this.shift = keyEvent.isShiftDown();
   };
   private final EventHandler<MouseEvent> mousePressedEventHandler = mouseEvent1 -> {
      this.key = false;
      this.shift = mouseEvent1.isShiftDown();
   };
   private final EventHandler<MouseEvent> onDragDetectedEventHandler = new EventHandler<MouseEvent>() {
      public void handle(MouseEvent mouseEvent) {
         TableViewSpanSelectionModel.this.cellsView.addEventHandler(MouseEvent.MOUSE_RELEASED, TableViewSpanSelectionModel.this.dragDoneHandler);
         TableViewSpanSelectionModel.this.drag = true;
         TableViewSpanSelectionModel.this.timer.setCycleCount(-1);
         TableViewSpanSelectionModel.this.timer.play();
      }
   };
   private final EventHandler<MouseEvent> onMouseDragEventHandler = e -> this.mouseEvent = e;
   private final ListChangeListener<TablePosition<ObservableList<SpreadsheetCell>, ?>> listChangeListener = this::handleSelectedCellsListChangeEvent;
   private TablePosition<ObservableList<SpreadsheetCell>, ?> old = null;

   public TableViewSpanSelectionModel(@NamedArg("spreadsheetView") SpreadsheetView spreadsheetView, @NamedArg("cellsView") SpreadsheetGridView cellsView) {
      super(cellsView);
      this.cellsView = cellsView;
      this.spreadsheetView = spreadsheetView;
      this.timer = new Timeline(new KeyFrame[]{new KeyFrame(Duration.millis(100.0), new WeakEventHandler(this.timerEventHandler), new KeyValue[0])});
      cellsView.addEventHandler(KeyEvent.KEY_PRESSED, new WeakEventHandler(this.keyPressedEventHandler));
      cellsView.addEventFilter(MouseEvent.MOUSE_PRESSED, new WeakEventHandler(this.mousePressedEventHandler));
      cellsView.setOnDragDetected(new WeakEventHandler(this.onDragDetectedEventHandler));
      cellsView.setOnMouseDragged(new WeakEventHandler(this.onMouseDragEventHandler));
      this.selectedCellsMap = new SelectedCellsMapTemp(new WeakListChangeListener(this.listChangeListener));
      this.selectedCellsSeq = new ReadOnlyUnbackedObservableList<TablePosition<ObservableList<SpreadsheetCell>, ?>>() {
         public TablePosition<ObservableList<SpreadsheetCell>, ?> get(int i) {
            return (TablePosition<ObservableList<SpreadsheetCell>, ?>)TableViewSpanSelectionModel.this.selectedCellsMap.get(i);
         }

         @Override
         public int size() {
            return TableViewSpanSelectionModel.this.selectedCellsMap.size();
         }
      };
   }

   private void handleSelectedCellsListChangeEvent(Change<? extends TablePosition<ObservableList<SpreadsheetCell>, ?>> c) {
      if (!this.makeAtomic) {
         this.selectedCellsSeq.callObservers(new MappingChange(c, MappingChange.NOOP_MAP, this.selectedCellsSeq));
         c.reset();
      }
   }

   public void select(int row, TableColumn<ObservableList<SpreadsheetCell>, ?> column) {
      if (row >= 0 && row < this.getItemCount()) {
         if (!this.isCellSelectionEnabled() || column != null) {
            TablePosition<ObservableList<SpreadsheetCell>, ?> posFinal = new TablePosition(this.getTableView(), row, column);
            int columnIndex = this.cellsView.getColumns().indexOf(posFinal.getTableColumn());
            SpreadsheetView.SpanType spanType = this.spreadsheetView.getSpanType(row, columnIndex);
            switch (spanType) {
               case ROW_SPAN_INVISIBLE:
                  if (this.old != null && !this.shift && this.old.getColumn() == posFinal.getColumn() && this.old.getRow() == posFinal.getRow() - 1) {
                     int visibleRow = FocusModelListener.getNextRowNumber(this.old, this.cellsView, this.spreadsheetView);
                     if (visibleRow < this.getItemCount()) {
                        posFinal = this.getVisibleCell(visibleRow, this.oldColSpan > 1 ? this.oldTableColumn : this.old.getTableColumn());
                        break;
                     }
                  }

                  posFinal = this.getVisibleCell(row, this.oldColSpan > 1 ? this.oldTableColumn : column);
                  break;
               case BOTH_INVISIBLE:
                  posFinal = this.getVisibleCell(row, column);
                  break;
               case COLUMN_SPAN_INVISIBLE:
                  if (this.old != null && !this.shift && this.old.getColumn() == posFinal.getColumn() - 1 && this.old.getRow() == posFinal.getRow()) {
                     posFinal = this.getVisibleCell(
                        this.oldRowSpan > 1 ? this.oldRow : this.old.getRow(),
                        FocusModelListener.getTableColumnSpan(this.old, this.cellsView, this.spreadsheetView)
                     );
                  } else {
                     posFinal = this.getVisibleCell(row, column);
                  }
               default:
                  if (this.direction != null && this.key) {
                     if ((Integer)this.direction.getKey() != 0 && this.oldColSpan > 1) {
                        posFinal = this.getVisibleCell(posFinal.getRow(), this.oldTableColumn);
                     } else if ((Integer)this.direction.getValue() != 0 && this.oldRowSpan > 1) {
                        posFinal = this.getVisibleCell(this.oldRow, posFinal.getTableColumn());
                     }
                  }
            }

            this.old = posFinal;
            if (posFinal.getTableColumn() != null) {
               if (!this.key) {
                  this.oldRow = this.old.getRow();
                  this.oldTableColumn = this.old.getTableColumn();
               } else if (this.direction != null && (Integer)this.direction.getKey() != 0) {
                  this.oldRow = this.old.getRow();
               } else if (this.direction != null && (Integer)this.direction.getValue() != 0) {
                  this.oldTableColumn = this.old.getTableColumn();
               }

               if (this.getSelectionMode() == SelectionMode.SINGLE) {
                  this.quietClearSelection();
               }

               SpreadsheetCell cell = (SpreadsheetCell)this.old.getTableColumn().getCellData(this.old.getRow());
               this.oldRowSpan = this.spreadsheetView.getRowSpan(cell, this.old.getRow());
               this.oldColSpan = this.spreadsheetView.getColumnSpan(cell);

               for (int i = this.old.getRow(); i < this.oldRowSpan + this.old.getRow(); i++) {
                  for (int j = this.spreadsheetView.getViewColumn(cell.getColumn());
                     j < this.oldColSpan + this.spreadsheetView.getViewColumn(cell.getColumn());
                     j++
                  ) {
                     posFinal = new TablePosition(this.getTableView(), i, this.getTableView().getVisibleLeafColumn(j));
                     this.selectedCellsMap.add(posFinal);
                  }
               }

               this.updateScroll(this.old);
               this.addSelectedRowsAndColumns(this.old);
               this.setSelectedIndex(this.old.getRow());
               this.setSelectedItem((ObservableList)this.getModelItem(this.old.getRow()));
               if (this.getTableView().getFocusModel() != null) {
                  this.getTableView().getFocusModel().focus(this.old.getRow(), this.old.getTableColumn());
               }
            }
         }
      }
   }

   private void updateScroll(TablePosition<ObservableList<SpreadsheetCell>, ?> posFinal) {
      if (!this.drag && this.key && this.getCellsViewSkin().getCellsSize() != 0 && !VerticalHeader.isFixedRowEmpty(this.spreadsheetView)) {
         int start = this.getCellsViewSkin().getRow(0).getIndex();
         double posFinalOffset = 0.0;

         for (int j = start; j < posFinal.getRow(); j++) {
            posFinalOffset += this.getSpreadsheetViewSkin().getRowHeight(j);
         }

         if (this.getCellsViewSkin().getFixedRowHeight() > posFinalOffset) {
            this.cellsView.scrollTo(posFinal.getRow());
         }
      }
   }

   public void clearSelection(int row, TableColumn<ObservableList<SpreadsheetCell>, ?> column) {
      TablePosition<ObservableList<SpreadsheetCell>, ?> tp = new TablePosition(this.getTableView(), row, column);
      if (tp.getRow() >= 0 && tp.getColumn() >= 0) {
         List<TablePosition<ObservableList<SpreadsheetCell>, ?>> selectedCells;
         if ((selectedCells = this.isSelectedRange(row, column, tp.getColumn())) != null) {
            for (TablePosition<ObservableList<SpreadsheetCell>, ?> cell : selectedCells) {
               this.selectedCellsMap.remove(cell);
               this.removeSelectedRowsAndColumns(cell);
               this.focus(cell.getRow());
            }
         } else {
            for (TablePosition<ObservableList<SpreadsheetCell>, ?> pos : this.getSelectedCells()) {
               if (pos.equals(tp)) {
                  this.selectedCellsMap.remove(pos);
                  this.removeSelectedRowsAndColumns(pos);
                  this.focus(row);
                  return;
               }
            }
         }
      }
   }

   public void verifySelectedCells(List<Pair<Integer, Integer>> selectedCells) {
      Set<TablePosition<ObservableList<SpreadsheetCell>, ?>> newList = new HashSet<>();
      this.clearSelection();
      int itemCount = this.getItemCount();
      int columnSize = this.getTableView().getVisibleLeafColumns().size();
      final HashSet<Integer> selectedRows = new HashSet<>();
      final HashSet<Integer> selectedColumns = new HashSet<>();
      final TablePosition<ObservableList<SpreadsheetCell>, ?> pos = null;

      for (Pair<Integer, Integer> position : selectedCells) {
         if ((Integer)position.getKey() >= 0
            && (Integer)position.getKey() < itemCount
            && (Integer)position.getValue() >= 0
            && (Integer)position.getValue() < columnSize) {
            TableColumn<ObservableList<SpreadsheetCell>, ?> column = this.getTableView().getVisibleLeafColumn((Integer)position.getValue());
            pos = this.getVisibleCell((Integer)position.getKey(), column);
            SpreadsheetCell cell = (SpreadsheetCell)column.getCellData(pos.getRow());
            if (cell != null) {
               int rowSpan = this.spreadsheetView.getRowSpan(cell, pos.getRow());
               int currentRow = pos.getRow();

               for (int i = pos.getRow(); i < rowSpan + currentRow; i++) {
                  selectedColumns.add(i);

                  for (int j = this.spreadsheetView.getViewColumn(cell.getColumn());
                     j < this.spreadsheetView.getColumnSpan(cell) + this.spreadsheetView.getViewColumn(cell.getColumn());
                     j++
                  ) {
                     selectedRows.add(j);
                     pos = new TablePosition(this.getTableView(), i, this.getTableView().getVisibleLeafColumn(j));
                     newList.add(pos);
                  }
               }
            }
         }
      }

      this.selectedCellsMap.setAll(newList);
      GridViewSkin skin = this.getSpreadsheetViewSkin();
      if (skin == null) {
         this.cellsView.skinProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable observable) {
               TableViewSpanSelectionModel.this.cellsView.skinProperty().removeListener(this);
               GridViewSkin skin = TableViewSpanSelectionModel.this.getSpreadsheetViewSkin();
               if (skin != null) {
                  TableViewSpanSelectionModel.this.updateSelectedVisuals(skin, pos, selectedRows, selectedColumns);
               }
            }
         });
      } else {
         this.updateSelectedVisuals(skin, pos, selectedRows, selectedColumns);
      }
   }

   private void updateSelectedVisuals(GridViewSkin skin, TablePosition pos, HashSet<Integer> selectedRows, HashSet<Integer> selectedColumns) {
      if (skin != null) {
         skin.getSelectedRows().addAll(selectedColumns);
         skin.getSelectedColumns().addAll(selectedRows);
      }

      if (pos != null) {
         this.getCellsViewSkin().lastRowLayout.set(true);
         this.getCellsViewSkin()
            .lastRowLayout
            .addListener(
               new InvalidationListener() {
                  public void invalidated(Observable observable) {
                     TableViewSpanSelectionModel.this.handleSelectedCellsListChangeEvent(
                        new NonIterableChange.SimpleAddChange<>(
                           0, TableViewSpanSelectionModel.this.selectedCellsMap.size(), TableViewSpanSelectionModel.this.selectedCellsSeq
                        )
                     );
                     TableViewSpanSelectionModel.this.getCellsViewSkin().lastRowLayout.removeListener(this);
                  }
               }
            );
      }
   }

   public void selectRange(
      int minRow, TableColumnBase<ObservableList<SpreadsheetCell>, ?> minColumn, int maxRow, TableColumnBase<ObservableList<SpreadsheetCell>, ?> maxColumn
   ) {
      if (this.getSelectionMode() == SelectionMode.SINGLE) {
         this.quietClearSelection();
         this.select(maxRow, maxColumn);
      } else {
         this.makeAtomic = true;
         int itemCount = this.getItemCount();
         int minColumnIndex = this.getTableView().getVisibleLeafIndex((TableColumn)minColumn);
         int maxColumnIndex = this.getTableView().getVisibleLeafIndex((TableColumn)maxColumn);
         int _minColumnIndex = Math.min(minColumnIndex, maxColumnIndex);
         int _maxColumnIndex = Math.max(minColumnIndex, maxColumnIndex);
         int _minRow = Math.min(minRow, maxRow);
         int _maxRow = Math.max(minRow, maxRow);
         HashSet<Integer> selectedRows = new HashSet<>();
         HashSet<Integer> selectedColumns = new HashSet<>();

         for (int _row = _minRow; _row <= _maxRow; _row++) {
            for (int _col = _minColumnIndex; _col <= _maxColumnIndex; _col++) {
               if (_row >= 0 && _row < itemCount) {
                  TableColumn<ObservableList<SpreadsheetCell>, ?> column = this.getTableView().getVisibleLeafColumn(_col);
                  if (column != null) {
                     TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getVisibleCell(_row, column);
                     SpreadsheetCell cell = (SpreadsheetCell)column.getCellData(pos.getRow());
                     int rowSpan = this.spreadsheetView.getRowSpan(cell, pos.getRow());
                     int currentRow = pos.getRow();

                     for (int i = currentRow; i < rowSpan + currentRow; i++) {
                        selectedRows.add(i);

                        for (int j = this.spreadsheetView.getViewColumn(cell.getColumn());
                           j < this.spreadsheetView.getColumnSpan(cell) + this.spreadsheetView.getViewColumn(cell.getColumn());
                           j++
                        ) {
                           selectedColumns.add(j);
                           pos = new TablePosition(this.getTableView(), i, this.getTableView().getVisibleLeafColumn(j));
                           this.selectedCellsMap.add(pos);
                        }
                     }
                  }
               }
            }
         }

         this.makeAtomic = false;
         this.getSpreadsheetViewSkin().getSelectedRows().addAll(selectedRows);
         this.getSpreadsheetViewSkin().getSelectedColumns().addAll(selectedColumns);
         this.setSelectedIndex(maxRow);
         this.setSelectedItem((ObservableList)this.getModelItem(maxRow));
         if (this.getTableView().getFocusModel() != null) {
            this.getTableView().getFocusModel().focus(maxRow, (TableColumn)maxColumn);
            int startChangeIndex = this.selectedCellsMap.indexOf(new TablePosition(this.getTableView(), minRow, (TableColumn)minColumn));
            int endChangeIndex = this.selectedCellsMap.getSelectedCells().size() - 1;
            if (startChangeIndex > -1 && endChangeIndex > -1) {
               int startIndex = Math.min(startChangeIndex, endChangeIndex);
               int endIndex = Math.max(startChangeIndex, endChangeIndex);
               this.handleSelectedCellsListChangeEvent(new NonIterableChange.SimpleAddChange<>(startIndex, endIndex + 1, this.selectedCellsSeq));
            }
         }
      }
   }

   public void selectAll() {
      if (this.getSelectionMode() != SelectionMode.SINGLE) {
         this.quietClearSelection();
         List<TablePosition<ObservableList<SpreadsheetCell>, ?>> indices = new ArrayList<>();
         TablePosition<ObservableList<SpreadsheetCell>, ?> tp = null;

         for (int col = 0; col < this.getTableView().getVisibleLeafColumns().size(); col++) {
            TableColumn<ObservableList<SpreadsheetCell>, ?> column = (TableColumn<ObservableList<SpreadsheetCell>, ?>)this.getTableView()
               .getVisibleLeafColumns()
               .get(col);

            for (int row = 0; row < this.getItemCount(); row++) {
               tp = new TablePosition(this.getTableView(), row, column);
               indices.add(tp);
            }
         }

         this.selectedCellsMap.setAll(indices);
         ArrayList<Integer> selectedColumns = new ArrayList<>();

         for (int col = 0; col < this.spreadsheetView.getGrid().getColumnCount(); col++) {
            selectedColumns.add(col);
         }

         ArrayList<Integer> selectedRows = new ArrayList<>();

         for (int row = 0; row < this.getItemCount(); row++) {
            selectedRows.add(row);
         }

         this.getSpreadsheetViewSkin().getSelectedRows().addAll(selectedRows);
         this.getSpreadsheetViewSkin().getSelectedColumns().addAll(selectedColumns);
         if (tp != null) {
            this.select(tp.getRow(), tp.getTableColumn());
            this.getTableView().getFocusModel().focus(0, (TableColumn)this.getTableView().getColumns().get(0));
         }
      }
   }

   public boolean isSelected(int row, TableColumn<ObservableList<SpreadsheetCell>, ?> column) {
      if (column != null && row >= 0) {
         int columnIndex = this.getTableView().getVisibleLeafIndex(column);
         if (this.getCellsViewSkin().getCellsSize() != 0) {
            TablePosition<ObservableList<SpreadsheetCell>, ?> posFinal = this.getVisibleCell(row, column);
            return this.selectedCellsMap.isSelected(posFinal.getRow(), posFinal.getColumn());
         } else {
            return this.selectedCellsMap.isSelected(row, columnIndex);
         }
      } else {
         return false;
      }
   }

   public List<TablePosition<ObservableList<SpreadsheetCell>, ?>> isSelectedRange(int row, TableColumn<ObservableList<SpreadsheetCell>, ?> column, int col) {
      if (col >= 0 && row >= 0) {
         SpreadsheetCell cell = (SpreadsheetCell)column.getCellData(row);
         int infRow = row;
         int supRow = row + this.spreadsheetView.getRowSpan(cell, row);
         int infCol = this.spreadsheetView.getViewColumn(cell.getColumn());
         int supCol = infCol + this.spreadsheetView.getColumnSpan(cell);
         List<TablePosition<ObservableList<SpreadsheetCell>, ?>> selectedCells = new ArrayList<>();

         for (TablePosition<ObservableList<SpreadsheetCell>, ?> tp : this.getSelectedCells()) {
            if (tp.getRow() >= infRow && tp.getRow() < supRow && tp.getColumn() >= infCol && tp.getColumn() < supCol) {
               selectedCells.add(tp);
            }
         }

         return selectedCells.isEmpty() ? null : selectedCells;
      } else {
         return null;
      }
   }

   private void addSelectedRowsAndColumns(TablePosition<?, ?> pos) {
      GridViewSkin skin = this.getSpreadsheetViewSkin();
      if (skin != null) {
         SpreadsheetCell cell = (SpreadsheetCell)pos.getTableColumn().getCellData(pos.getRow());
         int rowSpan = this.spreadsheetView.getRowSpan(cell, pos.getRow());

         for (int i = pos.getRow(); i < rowSpan + pos.getRow(); i++) {
            skin.getSelectedRows().add(i);

            for (int j = this.spreadsheetView.getViewColumn(cell.getColumn());
               j < this.spreadsheetView.getColumnSpan(cell) + this.spreadsheetView.getViewColumn(cell.getColumn());
               j++
            ) {
               skin.getSelectedColumns().add(j);
            }
         }
      }
   }

   private void removeSelectedRowsAndColumns(TablePosition<?, ?> pos) {
      SpreadsheetCell cell = (SpreadsheetCell)pos.getTableColumn().getCellData(pos.getRow());
      int rowSpan = this.spreadsheetView.getRowSpan(cell, pos.getRow());

      for (int i = pos.getRow(); i < rowSpan + pos.getRow(); i++) {
         this.getSpreadsheetViewSkin().getSelectedRows().remove(i);

         for (int j = this.spreadsheetView.getViewColumn(cell.getColumn());
            j < this.spreadsheetView.getColumnSpan(cell) + this.spreadsheetView.getViewColumn(cell.getColumn());
            j++
         ) {
            this.getSpreadsheetViewSkin().getSelectedColumns().remove(j);
         }
      }
   }

   public void clearAndSelect(int row, TableColumn<ObservableList<SpreadsheetCell>, ?> column) {
      this.makeAtomic = true;
      List<TablePosition<ObservableList<SpreadsheetCell>, ?>> previousSelection = new ArrayList<>(this.selectedCellsMap.getSelectedCells());
      this.clearSelection();
      this.select(row, column);
      this.makeAtomic = false;
      if (this.old != null && this.old.getColumn() >= 0) {
         TableColumn<ObservableList<SpreadsheetCell>, ?> columnFinal = this.getTableView().getVisibleLeafColumn(this.old.getColumn());
         int changeIndex = this.selectedCellsSeq.indexOf(new TablePosition(this.getTableView(), this.old.getRow(), columnFinal));
         NonIterableChange.GenericAddRemoveChange<TablePosition<ObservableList<SpreadsheetCell>, ?>> change = new NonIterableChange.GenericAddRemoveChange<>(
            changeIndex, changeIndex + 1, previousSelection, this.selectedCellsSeq
         );
         this.handleSelectedCellsListChangeEvent(change);
      }
   }

   public ObservableList<TablePosition> getSelectedCells() {
      return (ObservableList<TablePosition>)this.selectedCellsSeq;
   }

   public void selectAboveCell() {
      TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getFocusedCell();
      if (pos.getRow() == -1) {
         this.select(this.getItemCount() - 1);
      } else if (pos.getRow() > 0) {
         this.select(pos.getRow() - 1, pos.getTableColumn());
      }
   }

   public void selectBelowCell() {
      TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getFocusedCell();
      if (pos.getRow() == -1) {
         this.select(0);
      } else if (pos.getRow() < this.getItemCount() - 1) {
         this.select(pos.getRow() + 1, pos.getTableColumn());
      }
   }

   public void selectLeftCell() {
      if (this.isCellSelectionEnabled()) {
         TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getFocusedCell();
         if (pos.getColumn() - 1 >= 0) {
            this.select(pos.getRow(), this.getTableColumn(pos.getTableColumn(), -1));
         }
      }
   }

   public void selectRightCell() {
      if (this.isCellSelectionEnabled()) {
         TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getFocusedCell();
         if (pos.getColumn() + 1 < this.getTableView().getVisibleLeafColumns().size()) {
            this.select(pos.getRow(), this.getTableColumn(pos.getTableColumn(), 1));
         }
      }
   }

   public void clearSelection() {
      if (!this.makeAtomic) {
         this.setSelectedIndex(-1);
         this.setSelectedItem((ObservableList)this.getModelItem(-1));
         this.focus(-1);
      }

      this.quietClearSelection();
   }

   private void quietClearSelection() {
      this.selectedCellsMap.clear();
      GridViewSkin skin = this.getSpreadsheetViewSkin();
      if (skin != null) {
         skin.getSelectedRows().clear();
         skin.getSelectedColumns().clear();
      }
   }

   private TablePosition<ObservableList<SpreadsheetCell>, ?> getFocusedCell() {
      return this.getTableView().getFocusModel() == null ? new TablePosition(this.getTableView(), -1, null) : this.cellsView.getFocusModel().getFocusedCell();
   }

   private TableColumn<ObservableList<SpreadsheetCell>, ?> getTableColumn(TableColumn<ObservableList<SpreadsheetCell>, ?> column, int offset) {
      int columnIndex = this.getTableView().getVisibleLeafIndex(column);
      int newColumnIndex = columnIndex + offset;
      return this.getTableView().getVisibleLeafColumn(newColumnIndex);
   }

   private GridViewSkin getSpreadsheetViewSkin() {
      return this.getCellsViewSkin();
   }

   private TablePosition<ObservableList<SpreadsheetCell>, ?> getVisibleCell(int row, TableColumn<ObservableList<SpreadsheetCell>, ?> column) {
      int modelColumn = this.cellsView.getColumns().indexOf(column);
      SpreadsheetView.SpanType spanType = this.spreadsheetView.getSpanType(row, modelColumn);
      switch (spanType) {
         case ROW_SPAN_INVISIBLE:
         case BOTH_INVISIBLE:
         case COLUMN_SPAN_INVISIBLE:
         default:
            SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)this.cellsView.getItems().get(row)).get(modelColumn);
            int firstRow = this.getCellsViewSkin() == null ? -1 : this.getCellsViewSkin().getFirstRow(cell, row);
            if (this.getCellsViewSkin() != null && (this.getCellsViewSkin().getCellsSize() == 0 || this.getNonFixedRow(0).getIndex() > firstRow)) {
               GridRow gridRow = this.getNonFixedRow(0);
               return new TablePosition(
                  this.cellsView,
                  gridRow == null ? row : gridRow.getIndex(),
                  this.cellsView.getVisibleLeafColumn(this.spreadsheetView.getViewColumn(cell.getColumn()))
               );
            }

            return new TablePosition(this.cellsView, firstRow, this.cellsView.getVisibleLeafColumn(this.spreadsheetView.getViewColumn(cell.getColumn())));
         case NORMAL_CELL:
         case ROW_VISIBLE:
            return new TablePosition(this.cellsView, row, column);
      }
   }

   final GridViewSkin getCellsViewSkin() {
      return (GridViewSkin)this.cellsView.getSkin();
   }

   private GridRow getNonFixedRow(int index) {
      return this.getCellsViewSkin().getRow(index);
   }
}
