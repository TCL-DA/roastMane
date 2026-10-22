package impl.org.controlsfx.tableview2;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SortEvent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableFocusModel;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableViewSkinBase;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.controlsfx.control.tableview2.TableView2;

public class TableView2Skin<S> extends TableViewSkinBase<S, S, TableView<S>, TableRow<S>, TableColumn<S, ?>> {
   private static final double DEFAULT_CELL_HEIGHT;
   final Map<TableRow2<S>, Set<TableCell<S, ?>>> deportedCells = new HashMap<>();
   ObservableMap<Integer, Double> rowHeightMap = FXCollections.observableHashMap();
   protected TableView2<S> tableView;
   private final TableView2<S> parentTableView;
   protected RowHeader<S> rowHeader;
   private final ObservableSet<Integer> currentlyFixedRow = FXCollections.observableSet(new HashSet());
   private final ObservableList<Integer> selectedRows = FXCollections.observableArrayList();
   private final ObservableList<Integer> selectedColumns = FXCollections.observableArrayList();
   private double fixedRowHeight = 0.0;
   BitSet hBarValue;
   BitSet rowToLayout;
   double fixedColumnWidth;
   BooleanProperty lastRowLayout = new SimpleBooleanProperty(true);
   TableHeaderRow tableHeaderRow;
   private boolean key = false;
   private final EventHandler<KeyEvent> keyPressedEventHandler = e -> this.key = true;
   private final EventHandler<MouseEvent> mousePressedEventHandler = e -> this.key = false;
   private final List<Integer> oldSelectedColumns = new ArrayList<>();
   private final List<Integer> oldSelectedRows = new ArrayList<>();
   private final VirtualFlow<TableRow<S>> flow;
   private final IntegerProperty size = new SimpleIntegerProperty();
   private final InvalidationListener vbarValueListener = o -> this.verticalScroll();
   private final ListChangeListener<Integer> fixedRowsListener = new ListChangeListener<Integer>() {
      public void onChanged(Change<? extends Integer> c) {
         TableView2Skin.this.hBarValue.clear();

         while (c.next()) {
            if (c.wasPermutated()) {
               for (Integer fixedRow : c.getList()) {
                  TableView2Skin.this.rowToLayout.set(fixedRow, true);
               }
            } else {
               for (Integer unfixedRow : c.getRemoved()) {
                  TableView2Skin.this.rowToLayout.set(unfixedRow, false);
               }

               for (Integer fixedRow : c.getAddedSubList()) {
                  TableView2Skin.this.rowToLayout.set(fixedRow, true);
               }
            }
         }

         TableView2Skin.this.getFlow().requestLayout();
      }
   };
   private final SetChangeListener<? super Integer> currentlyFixedRowListener = arg0 -> this.computeFixedRowHeight();
   private final ListChangeListener<TableColumn> fixedColumnsListener = new ListChangeListener<TableColumn>() {
      public void onChanged(Change<? extends TableColumn> c) {
         TableView2Skin.this.hBarValue.clear();
         TableView2Skin.this.getFlow().requestLayout();
      }
   };

   public TableView2Skin(TableView2<S> tableView) {
      super(tableView);
      this.flow = this.getVirtualFlow();
      this.flow.setCellFactory(p -> this.createCell());
      this.tableView = tableView;
      if (tableView.getParent() != null && tableView.getParent() instanceof RowHeader) {
         this.parentTableView = ((RowHeader)tableView.getParent()).getParentTableView();
      } else {
         this.parentTableView = null;
      }

      tableView.rowFactoryProperty().bind(Bindings.createObjectBinding(() -> param -> new TableRow2<>(tableView), new Observable[]{tableView.skinProperty()}));
      this.getCurrentlyFixedRow().addListener(this.currentlyFixedRowListener);
      this.tableView.getFixedRows().addListener(this.fixedRowsListener);
      this.tableView.getFixedColumns().addListener(this.fixedColumnsListener);
      this.init();
      this.hBarValue = new BitSet(this.getItemCount());
      this.rowToLayout = this.initRowToLayoutBitSet();
      tableView.rowFixingEnabledProperty().addListener(o -> {
         this.rowToLayout = this.initRowToLayoutBitSet();
         tableView.refresh();
      });
      this.computeFixedRowHeight();
      EventHandler<MouseEvent> ml = event -> {
         if (tableView.getEditingCell() != null) {
            tableView.edit(-1, null);
         }

         tableView.requestFocus();
      };
      this.getFlow().getVerticalBar().addEventFilter(MouseEvent.MOUSE_PRESSED, ml);
      this.getFlow().getHorizontalBar().addEventFilter(MouseEvent.MOUSE_PRESSED, ml);
      this.registerChangeListener(tableView.fixedCellSizeProperty(), t -> {
         tableView.refresh();
         this.verticalScroll();
         this.computeFixedRowHeight();
      });
      this.registerChangeListener(tableView.columnFixingEnabledProperty(), t -> {
         tableView.refresh();
         this.verticalScroll();
         this.computeFixedRowHeight();
      });
   }

   public double getRowHeight(int row) {
      if (this.tableView.getFixedCellSize() > 0.0) {
         return this.tableView.getFixedCellSize();
      } else {
         return row >= 0 && row < this.getItemCount() && this.rowHeightMap.get(row) != null ? (Double)this.rowHeightMap.get(row) : DEFAULT_CELL_HEIGHT;
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

   public TableRow<S> getRowIndexed(int index) {
      List<? extends IndexedCell> cells = (List<? extends IndexedCell>)this.getFlow().getCells();
      if (!cells.isEmpty()) {
         IndexedCell cell = cells.get(0);
         if (index >= cell.getIndex() && index - cell.getIndex() < cells.size()) {
            return (TableRow<S>)cells.get(index - cell.getIndex());
         }
      }

      for (IndexedCell cell : this.getFlow().getFixedCells()) {
         if (cell.getIndex() == index) {
            return (TableRow<S>)cell;
         }
      }

      return null;
   }

   public int getFirstRow(TablePositionBase<?> pos, int index) {
      return index;
   }

   public TableRow<S> getRow(int index) {
      return index < this.getFlow().getCells().size() ? (TableRow)this.getFlow().getCells().get(index) : null;
   }

   public final boolean containsRow(int index) {
      for (Object obj : this.getFlow().getCells()) {
         if (((TableRow2)obj).getIndex() == index && !((TableRow2)obj).getChildrenUnmodifiable().isEmpty()) {
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

   protected final void init() {
      if (this.parentTableView == null) {
         this.rowHeader = new RowHeader<>(this.tableView);
         this.getChildren().add(this.rowHeader);
         this.rowHeader.init(this, (TableHeaderRow2)this.getTableHeaderRow());
      }

      this.getFlow().getVerticalBar().valueProperty().addListener(this.vbarValueListener);
      ((TableHeaderRow2)this.getTableHeaderRow()).init();
      this.getFlow().init();
      if (this.parentTableView == null) {
         this.tableView.addEventHandler(KeyEvent.KEY_PRESSED, new WeakEventHandler(this.keyPressedEventHandler));
         this.tableView.addEventFilter(MouseEvent.MOUSE_PRESSED, new WeakEventHandler(this.mousePressedEventHandler));
         if (this.tableView.getSelectionModel() != null) {
            this.tableView.getSelectionModel().selectedIndexProperty().addListener((obs, ov, nv) -> {
               this.updateHeaders();
               this.scrollToVisibleCell(nv, ov);
            });
            this.tableView.getSelectionModel().getSelectedCells().addListener(o -> this.updateHeaders());
            this.tableView.selectionModelProperty().addListener((observable, oldModel, newModel) -> {
               if (newModel != null) {
                  newModel.selectedIndexProperty().addListener((obs, ov, nv) -> {
                     this.updateHeaders();
                     this.scrollToVisibleCell(nv, ov);
                  });
                  newModel.getSelectedCells().addListener(o -> this.updateHeaders());
               }
            });
         }

         this.tableView.getVisibleLeafColumns().addListener(o -> this.updateHeaders());
         this.getChildren()
            .stream()
            .filter(n -> n.getStyleClass().contains("column-overlay"))
            .findFirst()
            .ifPresent(n -> n.translateXProperty().bind(this.rowHeader.rowHeaderWidthProperty()));
         Region columnReorderLine = (Region)this.getChildren().get(3);
         columnReorderLine.translateXProperty().addListener((obs, ov, nv) -> {
            if (!this.tableView.isRowHeaderVisible() && !columnReorderLine.getTransforms().isEmpty()) {
               columnReorderLine.getTransforms().clear();
            } else if (this.tableView.isRowHeaderVisible()) {
               columnReorderLine.getTransforms().setAll(new Transform[]{new Translate(this.getRowHeaderOffset(), 0.0)});
            }
         });
         columnReorderLine.boundsInParentProperty()
            .addListener((obs, ov, nv) -> columnReorderLine.setOpacity(nv.getMaxX() > this.tableView.getWidth() ? 0.0 : 1.0));
         this.getTableHeaderRow()
            .getChildren()
            .stream()
            .filter(n -> n.getStyleClass().contains("show-hide-columns-button"))
            .findFirst()
            .ifPresent(n -> n.translateXProperty().bind(this.rowHeader.rowHeaderWidthProperty().multiply(-1)));
         this.tableView.addEventHandler(SortEvent.ANY, e -> {
            if (e != null && SortUtils.SortEndedEvent.SORT_ENDED_EVENT.equals(e.getEventType())) {
               this.getFlow().rebuildFixedCells();
            }
         });
      }
   }

   protected final ObservableSet<Integer> getCurrentlyFixedRow() {
      return this.currentlyFixedRow;
   }

   public final IntegerProperty sizeProperty() {
      return this.size;
   }

   protected void layoutChildren(double x, double y, double w, double h) {
      if (this.tableView != null) {
         this.size.set(this.getItemCount());
         double width = w;
         double rowHeaderWidth = this.getRowHeaderOffset();
         if (this.tableView.isRowHeaderVisible()) {
            x += rowHeaderWidth;
            width = w - rowHeaderWidth;
         }

         super.layoutChildren(x, y, width, h);
         double baselineOffset = ((TableView)this.getSkinnable()).getLayoutBounds().getHeight() / 2.0;
         double tableHeaderRowHeight = 0.0;
         if (this.isColumnHeaderVisible()) {
            tableHeaderRowHeight = this.tableHeaderRow.prefHeight(-1.0);
            this.layoutInArea(this.getTableHeaderRow(), x, y, width, tableHeaderRowHeight, baselineOffset, HPos.CENTER, VPos.CENTER);
            y += tableHeaderRowHeight;
         }

         if (this.tableView.isRowHeaderVisible() && this.rowHeader != null) {
            this.layoutInArea(this.rowHeader, x - rowHeaderWidth, y - tableHeaderRowHeight, rowHeaderWidth, h, baselineOffset, HPos.CENTER, VPos.CENTER);
         }
      }
   }

   protected boolean isColumnHeaderVisible() {
      if (this.tableHeaderRow == null) {
         this.tableHeaderRow = this.getTableHeaderRow();
      }

      return this.tableHeaderRow.isVisible();
   }

   protected int getViewColumn(int modelColumn) {
      return (int)this.tableView.getColumns().stream().limit(modelColumn > -1 ? (long)modelColumn : 0L).filter(TableColumnBase::isVisible).count();
   }

   protected void onFocusAboveCell() {
      this.focusScroll();
   }

   protected void onFocusBelowCell() {
      this.focusScroll();
   }

   private int getFixedRowSize() {
      return this.tableView.getFixedRows().size();
   }

   void focusScroll() {
      TableFocusModel<?, ?> fm = this.tableView.getFocusModel();
      if (fm != null) {
         int row = fm.getFocusedIndex();
         if (!this.getFlow().getCells().isEmpty()
            && ((IndexedCell)this.getFlow().getCells().get(this.getFixedRowSize())).getIndex() > row
            && !this.tableView.getFixedRows().contains(row)) {
            this.flow.scrollTo(row);
         } else {
            this.flow.scrollTo(row);
         }

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

   protected VirtualFlow<TableRow<S>> createVirtualFlow() {
      return new TableView2VirtualFlow(this);
   }

   protected TableHeaderRow createTableHeaderRow() {
      return new TableHeaderRow2(this);
   }

   protected TableHeaderRow2 getTableHeaderRow2() {
      return (TableHeaderRow2)this.getTableHeaderRow();
   }

   private double getRowHeaderOffset() {
      return this.rowHeader != null ? this.rowHeader.computeHeaderWidth() : 0.0;
   }

   protected SouthTableHeaderRow getSouthHeader() {
      return this.getTableHeaderRow2().getSouthHeaderRow();
   }

   BooleanProperty getTableMenuButtonVisibleProperty() {
      return this.tableView.tableMenuButtonVisibleProperty();
   }

   public void scrollHorizontally() {
      super.scrollHorizontally();
   }

   protected void scrollHorizontally(TableColumn<S, ?> col) {
      if (col != null && col.isVisible()) {
         this.fixedColumnWidth = 0.0;
         double pos = this.getFlow().getHorizontalBar().getValue();
         int index = this.tableView.getVisibleLeafColumns().indexOf(col);
         double start = 0.0;

         for (int columnIndex = 0; columnIndex < index; columnIndex++) {
            if (((TableColumn)this.tableView.getVisibleLeafColumns().get(columnIndex)).isVisible()) {
               TableColumn<S, ?> column = (TableColumn<S, ?>)this.tableView.getVisibleLeafColumns().get(columnIndex);

               while (column.getParentColumn() != null) {
                  column = (TableColumn<S, ?>)column.getParentColumn();
               }

               if (this.tableView.isColumnFixingEnabled() && this.tableView.getFixedColumns().contains(column)) {
                  this.fixedColumnWidth = this.fixedColumnWidth + column.getWidth();
               }

               start += column.getWidth();
            }
         }

         double end = start + col.getWidth();
         double headerWidth = this.tableView.getWidth()
            - this.snappedLeftInset()
            - this.snappedRightInset()
            - (this.rowHeader != null ? this.rowHeader.getRowHeaderWidth() : 0.0);
         double max = this.getFlow().getHorizontalBar().getMax();
         if (start < pos + this.fixedColumnWidth && start >= 0.0 && start >= this.fixedColumnWidth) {
            double newPos = start - this.fixedColumnWidth < 0.0 ? start : start - this.fixedColumnWidth;
            this.getFlow().getHorizontalBar().setValue(newPos);
         } else if (start > pos + headerWidth) {
            double delta = !(start < 0.0) && !(end > headerWidth) ? 0.0 : start - pos - this.fixedColumnWidth;
            double newPos = Math.min(pos + delta, max);
            this.getFlow().getHorizontalBar().setValue(newPos);
         }
      }
   }

   private void verticalScroll() {
      if (this.rowHeader != null) {
         this.rowHeader.requestLayout();
      }
   }

   final TableView2VirtualFlow<?> getFlow() {
      return (TableView2VirtualFlow<?>)this.flow;
   }

   private BitSet initRowToLayoutBitSet() {
      int rowCount = this.getItemCount();
      BitSet bitSet = new BitSet(rowCount);

      for (int row = 0; row < rowCount; row++) {
         if (this.tableView.isRowFixingEnabled() && this.tableView.getFixedRows().contains(row)) {
            bitSet.set(row);
         }
      }

      return bitSet;
   }

   public final void computeFixedRowHeight() {
      this.fixedRowHeight = this.getCurrentlyFixedRow().stream().mapToDouble(this::getRowHeight).sum();
   }

   public TableRow<S> createCell() {
      TableRow<S> cell;
      if (((TableView)this.getSkinnable()).getRowFactory() != null) {
         cell = (TableRow<S>)((TableView)this.getSkinnable()).getRowFactory().call((TableView)this.getSkinnable());
      } else {
         cell = new TableRow();
      }

      cell.updateTableView((TableView)this.getSkinnable());
      return cell;
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
         if (this.flow.getScene() == null) {
            Platform.runLater(() -> this.setHbarValue(value, newCount));
         } else {
            this.getHBar().setValue(value);
         }
      }
   }

   private void updateHeaders() {
      ObservableList<TablePosition> selectedCells = this.tableView.getSelectionModel().getSelectedCells();
      List<Integer> columns = selectedCells.stream()
         .<Integer>map(TablePosition::getColumn)
         .filter(column -> column > -1 && this.tableView.getSelectionModel().isCellSelectionEnabled())
         .collect(Collectors.toList());
      if (!this.oldSelectedColumns.equals(columns)) {
         this.oldSelectedColumns.clear();
         this.oldSelectedColumns.addAll(columns);
         this.getSelectedColumns().setAll(columns);
      }

      List<Integer> rows = selectedCells.stream().<Integer>map(TablePositionBase::getRow).collect(Collectors.toList());
      if (!this.oldSelectedRows.equals(rows)) {
         this.oldSelectedRows.clear();
         this.oldSelectedRows.addAll(rows);
         this.getSelectedRows().setAll(rows);
      }
   }

   private void scrollToVisibleCell(Number newIndex, Number oldIndex) {
      if (this.key && newIndex != null && oldIndex != null) {
         double posFinalOffset = 0.0;
         double heightLastRow = 0.0;

         for (int j = this.getRow(0).getIndex(); j < newIndex.intValue(); j++) {
            heightLastRow = this.getRowHeight(j);
            posFinalOffset += heightLastRow;
         }

         double fixedHeight = this.getFixedRowHeight();
         if (fixedHeight > posFinalOffset) {
            this.flow.scrollTo(newIndex.intValue());
         } else if (fixedHeight > posFinalOffset - heightLastRow) {
            this.flow.scrollPixels(posFinalOffset - heightLastRow - fixedHeight);
         }
      }
   }

   static {
      double cell_size = 24.0;
      DEFAULT_CELL_HEIGHT = cell_size;
   }
}
