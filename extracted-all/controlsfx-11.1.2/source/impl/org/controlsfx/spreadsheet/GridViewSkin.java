package impl.org.controlsfx.spreadsheet;

import java.time.LocalDate;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableFocusModel;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableViewSkinBase;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.util.Callback;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class GridViewSkin
   extends TableViewSkinBase<ObservableList<SpreadsheetCell>, ObservableList<SpreadsheetCell>, TableView<ObservableList<SpreadsheetCell>>, TableRow<ObservableList<SpreadsheetCell>>, TableColumn<ObservableList<SpreadsheetCell>, ?>> {
   public static final double DEFAULT_CELL_HEIGHT = 24.0;
   private static final double DATE_CELL_MIN_WIDTH = 200.0 - Screen.getPrimary().getDpi();
   final Map<GridRow, Set<CellView>> deportedCells = new HashMap<>();
   ObservableMap<Integer, Double> rowHeightMap = FXCollections.observableHashMap();
   private GridCellEditor gridCellEditor;
   protected final SpreadsheetHandle handle;
   protected SpreadsheetView spreadsheetView;
   protected VerticalHeader verticalHeader;
   protected HorizontalPicker horizontalPickers;
   private ObservableSet<Integer> currentlyFixedRow = FXCollections.observableSet(new HashSet());
   private final ObservableList<Integer> selectedRows = FXCollections.observableArrayList();
   private final ObservableList<Integer> selectedColumns = FXCollections.observableArrayList();
   private double fixedRowHeight = 0.0;
   BitSet hBarValue;
   BitSet rowToLayout;
   RectangleSelection rectangleSelection;
   double fixedColumnWidth;
   BooleanProperty lastRowLayout = new SimpleBooleanProperty(true);
   private GridViewBehavior behavior;
   private InvalidationListener rowToLayoutListener = new InvalidationListener() {
      public void invalidated(Observable observable) {
         GridViewSkin.this.rowToLayout = GridViewSkin.this.initRowToLayoutBitSet();
      }
   };
   private final InvalidationListener vbarValueListener = new InvalidationListener() {
      public void invalidated(Observable valueModel) {
         GridViewSkin.this.verticalScroll();
      }
   };
   private final ListChangeListener<Integer> fixedRowsListener = new ListChangeListener<Integer>() {
      public void onChanged(Change<? extends Integer> c) {
         GridViewSkin.this.hBarValue.clear();

         while (c.next()) {
            if (c.wasPermutated()) {
               for (Integer fixedRow : c.getList()) {
                  GridViewSkin.this.rowToLayout.set(GridViewSkin.this.spreadsheetView.getFilteredRow(fixedRow), true);
               }
            } else {
               for (Integer unfixedRow : c.getRemoved()) {
                  GridViewSkin.this.rowToLayout.set(GridViewSkin.this.spreadsheetView.getFilteredRow(unfixedRow), false);
                  if (GridViewSkin.this.spreadsheetView.getGrid().getRows().size() > unfixedRow) {
                     for (SpreadsheetCell cell : (List)GridViewSkin.this.spreadsheetView.getGrid().getRows().get(unfixedRow)) {
                        if (GridViewSkin.this.spreadsheetView.getRowSpanFilter(cell) > 1) {
                           GridViewSkin.this.rowToLayout.set(GridViewSkin.this.spreadsheetView.getFilteredRow(unfixedRow), true);
                           break;
                        }
                     }
                  }
               }

               for (Integer fixedRow : c.getAddedSubList()) {
                  GridViewSkin.this.rowToLayout.set(GridViewSkin.this.spreadsheetView.getFilteredRow(fixedRow), true);
               }
            }
         }

         GridViewSkin.this.getFlow().requestLayout();
      }
   };
   private final SetChangeListener<? super Integer> currentlyFixedRowListener = new SetChangeListener<Integer>() {
      public void onChanged(javafx.collections.SetChangeListener.Change<? extends Integer> arg0) {
         GridViewSkin.this.computeFixedRowHeight();
      }
   };
   private final ListChangeListener<SpreadsheetColumn> fixedColumnsListener = new ListChangeListener<SpreadsheetColumn>() {
      public void onChanged(Change<? extends SpreadsheetColumn> c) {
         GridViewSkin.this.hBarValue.clear();
         GridViewSkin.this.getFlow().requestLayout();
      }
   };

   public GridViewSkin(final SpreadsheetHandle handle) {
      super(handle.getGridView());
      this.behavior = new GridViewBehavior(handle.getGridView());
      this.handle = handle;
      this.spreadsheetView = handle.getView();
      this.gridCellEditor = new GridCellEditor(handle);
      TableView<ObservableList<SpreadsheetCell>> tableView = handle.getGridView();
      tableView.setRowFactory(new Callback<TableView<ObservableList<SpreadsheetCell>>, TableRow<ObservableList<SpreadsheetCell>>>() {
         public TableRow<ObservableList<SpreadsheetCell>> call(TableView<ObservableList<SpreadsheetCell>> p) {
            return new GridRow(handle);
         }
      });
      this.getVirtualFlow().setCellFactory(param -> this.createCell());
      tableView.getStyleClass().add("cell-spreadsheet");
      this.getCurrentlyFixedRow().addListener(this.currentlyFixedRowListener);
      this.spreadsheetView.getFixedRows().addListener(this.fixedRowsListener);
      this.spreadsheetView.getFixedColumns().addListener(this.fixedColumnsListener);
      this.init();
      handle.getView().gridProperty().addListener(this.rowToLayoutListener);
      handle.getView().hiddenRowsProperty().addListener(this.rowToLayoutListener);
      handle.getView().hiddenColumnsProperty().addListener(this.rowToLayoutListener);
      this.hBarValue = new BitSet(this.getItemCount());
      this.rowToLayout = this.initRowToLayoutBitSet();
      this.computeFixedRowHeight();
      EventHandler<MouseEvent> ml = event -> {
         if (tableView.getEditingCell() != null) {
            tableView.edit(-1, null);
         }

         tableView.requestFocus();
      };
      this.getFlow().getVerticalBar().addEventFilter(MouseEvent.MOUSE_PRESSED, ml);
      this.getFlow().getHorizontalBar().addEventFilter(MouseEvent.MOUSE_PRESSED, ml);
      GridViewBehavior behavior = this.getBehavior();
      behavior.setOnFocusPreviousRow(() -> this.onFocusAboveCell());
      behavior.setOnFocusNextRow(() -> this.onFocusBelowCell());
      behavior.setOnMoveToFirstCell(() -> this.onMoveToFirstCell());
      behavior.setOnMoveToLastCell(() -> this.onMoveToLastCell());
      behavior.setOnScrollPageDown(isFocusDriven -> this.onScrollPageDown(isFocusDriven));
      behavior.setOnScrollPageUp(isFocusDriven -> this.onScrollPageUp(isFocusDriven));
      behavior.setOnSelectPreviousRow(() -> this.onSelectAboveCell());
      behavior.setOnSelectNextRow(() -> this.onSelectBelowCell());
      behavior.setOnSelectLeftCell(() -> this.onSelectLeftCell());
      behavior.setOnSelectRightCell(() -> this.onSelectRightCell());
      behavior.setOnFocusLeftCell(() -> this.onFocusLeftCell());
      behavior.setOnFocusRightCell(() -> this.onFocusRightCell());
      this.registerChangeListener(
         tableView.fixedCellSizeProperty(), var1x -> this.getFlow().setFixedCellSize(((TableView)this.getSkinnable()).getFixedCellSize())
      );
   }

   private TableRow<ObservableList<SpreadsheetCell>> createCell() {
      TableRow<ObservableList<SpreadsheetCell>> row = null;
      TableView<ObservableList<SpreadsheetCell>> tableView = (TableView<ObservableList<SpreadsheetCell>>)this.getSkinnable();
      row = (TableRow<ObservableList<SpreadsheetCell>>)tableView.getRowFactory().call(tableView);
      row.updateTableView(tableView);
      return row;
   }

   public double getRowHeight(int row) {
      if (row == -1) {
         return 24.0;
      } else {
         Double rowHeightCache = (Double)this.rowHeightMap.get(this.spreadsheetView.getModelRow(row));
         if (rowHeightCache == null) {
            double rowHeight = this.handle.getView().getGrid().getRowHeight(this.spreadsheetView.getModelRow(row));
            return rowHeight == -1.0 ? 24.0 : rowHeight;
         } else {
            return rowHeightCache;
         }
      }
   }

   public double getFixedRowHeight() {
      return this.fixedRowHeight;
   }

   public ObservableList<Integer> getSelectedRows() {
      return this.selectedRows;
   }

   public ObservableList<Integer> getSelectedColumns() {
      return this.selectedColumns;
   }

   public GridCellEditor getSpreadsheetCellEditorImpl() {
      return this.gridCellEditor;
   }

   public GridRow getRowIndexed(int index) {
      List<? extends IndexedCell> cells = (List<? extends IndexedCell>)this.getFlow().getCells();
      if (!cells.isEmpty()) {
         IndexedCell cell = cells.get(0);
         if (index >= cell.getIndex() && index - cell.getIndex() < cells.size()) {
            return (GridRow)cells.get(index - cell.getIndex());
         }
      }

      for (IndexedCell cell : this.getFlow().getFixedCells()) {
         if (cell.getIndex() == index) {
            return (GridRow)cell;
         }
      }

      return null;
   }

   public int getFirstRow(SpreadsheetCell cell, int index) {
      do {
         index--;
      } while (index >= 0 && ((ObservableList)this.spreadsheetView.getItems().get(index)).get(cell.getColumn()) == cell);

      return index + 1;
   }

   public GridRow getRow(int index) {
      return index < this.getFlow().getCells().size() ? (GridRow)this.getFlow().getCells().get(index) : null;
   }

   public final boolean containsRow(int index) {
      for (Object obj : this.getFlow().getCells()) {
         if (((GridRow)obj).getIndex() == index && ((GridRow)obj).getLayoutY() >= 0.0) {
            return true;
         }
      }

      return false;
   }

   public int getCellsSize() {
      return this.getFlow().getCells().size();
   }

   public ScrollBar getHBar() {
      return this.getFlow() != null ? this.getFlow().getHorizontalBar() : null;
   }

   public ScrollBar getVBar() {
      return this.getFlow().getVerticalBar();
   }

   public void resizeRowsToFitContent() {
      Grid grid = this.spreadsheetView.getGrid();
      int maxRows = this.handle.getView().getGrid().getRowCount();

      for (int row = 0; row < maxRows; row++) {
         if (grid.isRowResizable(row)) {
            this.resizeRowToFitContent(row);
         }
      }
   }

   public void resizeRowToFitContent(int modelRow) {
      if (!((TableView)this.getSkinnable()).getColumns().isEmpty()) {
         TableColumn<ObservableList<SpreadsheetCell>, ?> col = (TableColumn<ObservableList<SpreadsheetCell>, ?>)((TableView)this.getSkinnable())
            .getColumns()
            .get(0);
         List<?> items = this.handle.getGridView().getItems();
         if (items != null && !items.isEmpty()) {
            if (this.spreadsheetView.getGrid().isRowResizable(modelRow)) {
               Callback cellFactory = col.getCellFactory();
               if (cellFactory != null) {
                  CellView cell = (CellView)cellFactory.call(col);
                  if (cell != null) {
                     cell.getProperties().put("deferToParentPrefWidth", Boolean.TRUE);
                     double padding = 5.0;
                     Node n = cell.getSkin() == null ? null : cell.getSkin().getNode();
                     if (n instanceof Region) {
                        Region r = (Region)n;
                        padding = r.snappedTopInset() + r.snappedBottomInset();
                     }

                     double maxHeight = 0.0;
                     this.getChildren().add(cell);
                     int columnSize = ((TableView)this.getSkinnable()).getColumns().size();

                     for (int viewColumn = 0; viewColumn < columnSize; viewColumn++) {
                        TableColumn column = (TableColumn)((TableView)this.getSkinnable()).getColumns().get(viewColumn);
                        cell.updateTableColumn(column);
                        cell.updateTableView(this.handle.getGridView());
                        cell.updateIndex(modelRow);
                        SpreadsheetCell spc = (SpreadsheetCell)cell.getItem();
                        double width = column.getWidth();
                        if (spc != null && spc.getColumn() == viewColumn && spc.getColumnSpan() > 1) {
                           int max = ((TableView)this.getSkinnable()).getVisibleLeafColumns().size() - viewColumn;
                           int i = 1;

                           for (int colSpan = spc.getColumnSpan(); i < colSpan && i < max; i++) {
                              double tempWidth = this.snapSize(((TableView)this.getSkinnable()).getVisibleLeafColumn(viewColumn + i).getWidth());
                              width += tempWidth;
                           }
                        }

                        if (spc != null && spc.getColumn() == viewColumn && (cell.getText() != null && !cell.getText().isEmpty() || cell.getGraphic() != null)) {
                           cell.setWrapText(true);
                           cell.applyCss();
                           maxHeight = Math.max(maxHeight, cell.prefHeight(width));
                        }
                     }

                     this.getChildren().remove(cell);
                     this.rowHeightMap.put(modelRow, maxHeight + padding);
                     Event.fireEvent(this.spreadsheetView, new SpreadsheetView.RowHeightEvent(modelRow, maxHeight + padding));
                     this.rectangleSelection.updateRectangle();
                  }
               }
            }
         }
      }
   }

   public void resizeRowsToMaximum() {
      this.resizeRowsToFitContent();
      Grid grid = this.spreadsheetView.getGrid();
      double maxHeight = 0.0;

      for (int key : this.rowHeightMap.keySet()) {
         maxHeight = Math.max(maxHeight, (Double)this.rowHeightMap.get(key));
      }

      this.rowHeightMap.clear();
      int maxRows = this.handle.getView().getGrid().getRows().size();

      for (int modelRow = 0; modelRow < maxRows; modelRow++) {
         if (grid.isRowResizable(modelRow)) {
            Event.fireEvent(this.spreadsheetView, new SpreadsheetView.RowHeightEvent(modelRow, maxHeight));
            this.rowHeightMap.put(modelRow, maxHeight);
         }
      }

      this.rectangleSelection.updateRectangle();
   }

   public void resizeRowsToDefault() {
      this.rowHeightMap.clear();
      Grid grid = this.spreadsheetView.getGrid();

      for (GridRow row : this.getFlow().getCells()) {
         if (grid.isRowResizable(this.spreadsheetView.getModelRow(row.getIndex()))) {
            double newHeight = row.computePrefHeight(-1.0);
            if (row.getPrefHeight() != newHeight) {
               row.setRowHeight(newHeight);
               row.requestLayout();
            }
         }
      }

      this.getFlow().layoutChildren();

      for (GridRow rowx : this.getFlow().getCells()) {
         double height = this.getRowHeight(this.spreadsheetView.getModelRow(rowx.getIndex()));
         if (rowx.getHeight() != height && grid.isRowResizable(this.spreadsheetView.getModelRow(rowx.getIndex()))) {
            rowx.setRowHeight(height);
         }
      }

      this.rectangleSelection.updateRectangle();
   }

   public void resizeColumnToFitContent(TableColumn<ObservableList<SpreadsheetCell>, ?> tc, int maxRows) {
      List<?> items = this.handle.getGridView().getItems();
      if (items != null && !items.isEmpty()) {
         Callback cellFactory = tc.getCellFactory();
         if (cellFactory != null) {
            TableCell<ObservableList<SpreadsheetCell>, ?> cell = (TableCell<ObservableList<SpreadsheetCell>, ?>)cellFactory.call(tc);
            if (cell != null) {
               int indexColumn = this.handle.getGridView().getColumns().indexOf(tc);
               if (maxRows != 30 || !this.handle.isColumnWidthSet(indexColumn)) {
                  double padding = ((SpreadsheetColumn)this.spreadsheetView.getColumns().get(indexColumn)).getFilter() != null ? 0.0 : 10.0;
                  Node n = cell.getSkin() == null ? null : cell.getSkin().getNode();
                  if (n instanceof Region) {
                     Region r = (Region)n;
                     padding = r.snappedLeftInset() + r.snappedRightInset();
                  }

                  cell.getProperties().put("deferToParentPrefWidth", Boolean.TRUE);
                  ObservableList<ObservableList<SpreadsheetCell>> gridRows = this.spreadsheetView.getGrid().getRows();
                  int rows = maxRows == -1 ? items.size() : Math.min(items.size(), maxRows == 30 ? 100 : maxRows);
                  double maxWidth = 0.0;
                  boolean datePresent = false;
                  cell.updateTableColumn(tc);
                  cell.updateTableView(this.handle.getGridView());
                  if (cell.getSkin() == null) {
                     cell.setSkin(new CellViewSkin((CellView)cell));
                  }

                  for (int row = 0; row < rows; row++) {
                     cell.updateIndex(row);
                     if (cell.getText() != null && !cell.getText().isEmpty() || cell.getGraphic() != null) {
                        this.getChildren().add(cell);
                        if (((SpreadsheetCell)cell.getItem()).getItem() instanceof LocalDate) {
                           datePresent = true;
                        }

                        cell.applyCss();
                        double width = cell.prefWidth(-1.0);
                        SpreadsheetCell spc = (SpreadsheetCell)((ObservableList)gridRows.get(row)).get(indexColumn);
                        if (this.spreadsheetView.getColumnSpan(spc) > 1) {
                           for (int i = this.spreadsheetView.getViewColumn(spc.getColumn());
                              i < this.spreadsheetView.getViewColumn(spc.getColumn()) + this.spreadsheetView.getColumnSpan(spc);
                              i++
                           ) {
                              if (i != indexColumn) {
                                 width -= ((SpreadsheetColumn)this.spreadsheetView.getColumns().get(i)).getWidth();
                              }
                           }
                        }

                        maxWidth = Math.max(maxWidth, width);
                        this.getChildren().remove(cell);
                     }
                  }

                  cell.updateIndex(-1);
                  double widthMax = maxWidth + padding;
                  if (this.handle.getGridView().getColumnResizePolicy() == TableView.CONSTRAINED_RESIZE_POLICY) {
                     widthMax = Math.max(widthMax, tc.getWidth());
                  }

                  if (datePresent && widthMax < DATE_CELL_MIN_WIDTH) {
                     widthMax = DATE_CELL_MIN_WIDTH;
                  }

                  widthMax = this.snapSize(widthMax);
                  if (tc.getPrefWidth() != widthMax || tc.getWidth() == widthMax) {
                     tc.setPrefWidth(widthMax);
                  }

                  this.rectangleSelection.updateRectangle();
               }
            }
         }
      }
   }

   protected final void init() {
      this.rectangleSelection = new RectangleSelection(this, (TableViewSpanSelectionModel)this.handle.getGridView().getSelectionModel());
      this.getFlow().getVerticalBar().valueProperty().addListener(this.vbarValueListener);
      this.verticalHeader = new VerticalHeader(this.handle);
      this.getChildren().add(this.verticalHeader);
      ((HorizontalHeader)this.getTableHeaderRow()).init();
      this.verticalHeader.init(this, (HorizontalHeader)this.getTableHeaderRow());
      this.horizontalPickers = new HorizontalPicker((HorizontalHeader)this.getTableHeaderRow(), this.spreadsheetView);
      this.getChildren().add(this.horizontalPickers);
      this.getFlow().init(this.spreadsheetView);
      this.getBehavior().setGridViewSkin(this);
   }

   public GridViewBehavior getBehavior() {
      return this.behavior;
   }

   protected final ObservableSet<Integer> getCurrentlyFixedRow() {
      return this.currentlyFixedRow;
   }

   public ObservableList<TableColumn<ObservableList<SpreadsheetCell>, ?>> getColumns() {
      return this.handle.getGridView().getColumns();
   }

   public void resize(TableColumnBase<?, ?> tc, int maxRows) {
      if (tc.isResizable()) {
         int columnIndex = this.getColumns().indexOf(tc);
         TableColumn tableColumn = (TableColumn)this.getColumns().get(columnIndex);
         this.resizeColumnToFitContent(tableColumn, maxRows);
         Event.fireEvent(this.spreadsheetView, new SpreadsheetView.ColumnWidthEvent(columnIndex, tableColumn.getWidth()));
      }
   }

   protected void layoutChildren(double x, double y, double w, double h) {
      if (this.spreadsheetView != null) {
         double verticalHeaderWidth = this.verticalHeader.computeHeaderWidth();
         double horizontalPickerHeight = this.spreadsheetView.getColumnPickers().isEmpty() ? 0.0 : 16.0;
         if (!this.spreadsheetView.isShowRowHeader() && this.spreadsheetView.getRowPickers().isEmpty()) {
            x = 0.0;
         } else {
            x += verticalHeaderWidth;
            w -= verticalHeaderWidth;
         }

         y += horizontalPickerHeight;
         super.layoutChildren(x, y, w, h - horizontalPickerHeight);
         double baselineOffset = ((TableView)this.getSkinnable()).getLayoutBounds().getHeight() / 2.0;
         double tableHeaderRowHeight = 0.0;
         if (!this.spreadsheetView.getColumnPickers().isEmpty()) {
            this.layoutInArea(this.horizontalPickers, x, y - 16.0, w, tableHeaderRowHeight, baselineOffset, HPos.CENTER, VPos.CENTER);
         }

         if (this.spreadsheetView.showColumnHeaderProperty().get()) {
            double var19 = this.getTableHeaderRow().prefHeight(-1.0);
            tableHeaderRowHeight = var19 < 24.0 ? 24.0 : var19;
            this.layoutInArea(this.getTableHeaderRow(), x, y, w, tableHeaderRowHeight, baselineOffset, HPos.CENTER, VPos.CENTER);
            y += tableHeaderRowHeight;
         }

         if (this.spreadsheetView.isShowRowHeader() || !this.spreadsheetView.getRowPickers().isEmpty()) {
            this.layoutInArea(this.verticalHeader, x - verticalHeaderWidth, y - tableHeaderRowHeight, w, h, baselineOffset, HPos.CENTER, VPos.CENTER);
         }
      }
   }

   protected void onFocusAboveCell() {
      this.focusScroll();
   }

   protected void onFocusBelowCell() {
      this.focusScroll();
   }

   private int getFixedRowSize() {
      int i = 0;

      for (Integer fixedRow : this.spreadsheetView.getFixedRows()) {
         if (!this.spreadsheetView.getHiddenRows().get(fixedRow)) {
            i++;
         }
      }

      return i;
   }

   void focusScroll() {
      TableFocusModel<?, ?> fm = this.handle.getGridView().getFocusModel();
      if (fm != null) {
         int row = fm.getFocusedIndex();
         this.getFlow().scrollTo(row);
         this.scrollHorizontally();
      }
   }

   protected void onSelectAboveCell() {
      super.onSelectAboveCell();
      this.scrollHorizontally();
   }

   protected void onSelectBelowCell() {
      super.onSelectBelowCell();
      this.scrollHorizontally();
   }

   protected VirtualFlow<TableRow<ObservableList<SpreadsheetCell>>> createVirtualFlow() {
      return new GridVirtualFlow(this);
   }

   protected TableHeaderRow createTableHeaderRow() {
      return new HorizontalHeader(this);
   }

   public HorizontalHeader getHorizontalHeader() {
      return (HorizontalHeader)this.getTableHeaderRow();
   }

   public void scrollHorizontally() {
      super.scrollHorizontally();
   }

   protected void scrollHorizontally(TableColumn<ObservableList<SpreadsheetCell>, ?> col) {
      if (col != null && col.isVisible()) {
         this.fixedColumnWidth = 0.0;
         double pos = this.getFlow().getHorizontalBar().getValue();
         int index = this.getColumns().indexOf(col);
         double start = 0.0;

         for (int columnIndex = 0; columnIndex < index; columnIndex++) {
            if (!this.spreadsheetView.isColumnHidden(columnIndex)) {
               SpreadsheetColumn column = (SpreadsheetColumn)this.spreadsheetView.getColumns().get(columnIndex);
               if (column.isFixed()) {
                  this.fixedColumnWidth = this.fixedColumnWidth + column.getWidth();
               }

               start += column.getWidth();
            }
         }

         double end = start + col.getWidth();
         double headerWidth = this.handle.getView().getWidth()
            - this.snappedLeftInset()
            - this.snappedRightInset()
            - this.verticalHeader.getVerticalHeaderWidth();
         double max = this.getFlow().getHorizontalBar().getMax();
         if (start < pos + this.fixedColumnWidth && start >= 0.0 && start >= this.fixedColumnWidth) {
            double newPos = start - this.fixedColumnWidth < 0.0 ? start : start - this.fixedColumnWidth;
            this.getFlow().getHorizontalBar().setValue(newPos);
         } else if (start > pos + headerWidth) {
            double delta = !(start < 0.0) && !(end > headerWidth) ? 0.0 : start - pos - this.fixedColumnWidth;
            double newPos = pos + delta > max ? max : pos + delta;
            this.getFlow().getHorizontalBar().setValue(newPos);
         }
      }
   }

   private void verticalScroll() {
      this.verticalHeader.requestLayout();
   }

   GridVirtualFlow<?> getFlow() {
      return (GridVirtualFlow<?>)this.getVirtualFlow();
   }

   private BitSet initRowToLayoutBitSet() {
      int rowCount = this.getItemCount();
      BitSet bitSet = new BitSet(rowCount);

      for (int row = 0; row < rowCount; row++) {
         if (this.spreadsheetView.getFixedRows().contains(this.spreadsheetView.getModelRow(row))) {
            bitSet.set(row);
         } else {
            for (SpreadsheetCell cell : (List)this.handle.getGridView().getItems().get(row)) {
               if (this.spreadsheetView.getRowSpanFilter(cell) > 1) {
                  bitSet.set(row);
                  break;
               }
            }
         }
      }

      return bitSet;
   }

   public void computeFixedRowHeight() {
      this.fixedRowHeight = 0.0;

      for (int i : this.getCurrentlyFixedRow()) {
         this.fixedRowHeight = this.fixedRowHeight + this.getRowHeight(i);
      }
   }

   public final int getItemCount() {
      return ((TableView)this.getSkinnable()).getItems() == null ? 0 : ((TableView)this.getSkinnable()).getItems().size();
   }

   public void setHbarValue(double value) {
      this.setHbarValue(value, 0);
   }

   public void setHbarValue(double value, int count) {
      if (count <= 5) {
         int newCount = count + 1;
         if (this.getFlow().getScene() == null) {
            Platform.runLater(() -> this.setHbarValue(value, newCount));
         } else {
            this.getHBar().setValue(value);
         }
      }
   }
}
