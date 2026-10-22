package org.controlsfx.control.spreadsheet;

import impl.org.controlsfx.i18n.Localization;
import impl.org.controlsfx.spreadsheet.CellView;
import impl.org.controlsfx.spreadsheet.FocusModelListener;
import impl.org.controlsfx.spreadsheet.GridViewSkin;
import impl.org.controlsfx.spreadsheet.RectangleSelection;
import impl.org.controlsfx.spreadsheet.SpreadsheetGridView;
import impl.org.controlsfx.spreadsheet.SpreadsheetHandle;
import impl.org.controlsfx.spreadsheet.TableViewSpanSelectionModel;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Skin;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.KeyCombination.Modifier;
import javafx.scene.transform.Scale;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import org.controlsfx.tools.Utils;

public class SpreadsheetView extends Control {
   private static final double DEFAULT_ROW_HEADER_WIDTH = 30.0;
   private final SpreadsheetGridView cellsView;
   private SimpleObjectProperty<Grid> gridProperty = new SimpleObjectProperty();
   private DataFormat fmt;
   private final ObservableList<Integer> fixedRows = FXCollections.observableArrayList();
   private final ObservableList<SpreadsheetColumn> fixedColumns = FXCollections.observableArrayList();
   private final BooleanProperty fixingRowsAllowedProperty = new SimpleBooleanProperty(true);
   private final BooleanProperty fixingColumnsAllowedProperty = new SimpleBooleanProperty(true);
   private final BooleanProperty showColumnHeader = new SimpleBooleanProperty(true, "showColumnHeader", true);
   private final BooleanProperty showRowHeader = new SimpleBooleanProperty(true, "showRowHeader", true);
   private BitSet rowFix;
   private final ObservableMap<Integer, Picker> rowPickers = FXCollections.observableHashMap();
   private final ObservableMap<Integer, Picker> columnPickers = FXCollections.observableHashMap();
   private ObservableList<SpreadsheetColumn> columns = FXCollections.observableArrayList();
   private Map<SpreadsheetCellType<?>, SpreadsheetCellEditor> editors = new IdentityHashMap<>();
   private final SpreadsheetViewSelectionModel selectionModel;
   private final KeyCombination zoomOutChar = new KeyCharacterCombination("-", new Modifier[]{KeyCombination.SHORTCUT_DOWN});
   private final KeyCombination zoomOutKeypad = new KeyCodeCombination(KeyCode.SUBTRACT, new Modifier[]{KeyCombination.SHORTCUT_DOWN});
   private final KeyCombination zoomInChar = new KeyCharacterCombination("+", new Modifier[]{KeyCombination.SHORTCUT_DOWN});
   private final KeyCombination zoomInCharShift = new KeyCharacterCombination("+", new Modifier[]{KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN});
   private final KeyCombination zoomInKeypadAdd = new KeyCodeCombination(KeyCode.ADD, new Modifier[]{KeyCombination.SHORTCUT_DOWN});
   private final KeyCombination zoomNormalKeypad = new KeyCodeCombination(KeyCode.NUMPAD0, new Modifier[]{KeyCombination.SHORTCUT_DOWN});
   private final KeyCombination zoomNormal = new KeyCodeCombination(KeyCode.DIGIT0, new Modifier[]{KeyCombination.SHORTCUT_DOWN});
   private final KeyCombination zoomNormalShift = new KeyCodeCombination(
      KeyCode.DIGIT0, new Modifier[]{KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN}
   );
   private final DoubleProperty rowHeaderWidth = new SimpleDoubleProperty(30.0);
   private DoubleProperty zoomFactor = new SimpleDoubleProperty(1.0);
   private static final double MIN_ZOOM = 0.2;
   private static final double MAX_ZOOM = 2.0;
   private static final double STEP_ZOOM = 0.1;
   private final ObjectProperty<BitSet> hiddenRowsProperty = new SimpleObjectProperty();
   private IdentityHashMap<ObservableList<SpreadsheetCell>, Integer> identityMap;
   private final ObjectProperty<BitSet> hiddenColumnsProperty = new SimpleObjectProperty();
   private HashMap<Integer, Integer> rowMap;
   private HashMap<Integer, Integer> columnMap = new HashMap<>();
   private Integer filteredRow;
   private FilteredList<ObservableList<SpreadsheetCell>> filteredList;
   private SortedList<ObservableList<SpreadsheetCell>> sortedList;
   private CellGraphicFactory cellGraphicFactory;
   private String stylesheet;
   private final BitSet columnWidthSet = new BitSet();
   final SpreadsheetHandle handle = new SpreadsheetHandle() {
      @Override
      protected SpreadsheetView getView() {
         return SpreadsheetView.this;
      }

      @Override
      protected GridViewSkin getCellsViewSkin() {
         return SpreadsheetView.this.getCellsViewSkin();
      }

      @Override
      protected SpreadsheetGridView getGridView() {
         return SpreadsheetView.this.getCellsView();
      }

      @Override
      protected boolean isColumnWidthSet(int indexColumn) {
         return SpreadsheetView.this.columnWidthSet.get(indexColumn);
      }
   };
   private final ListChangeListener<Integer> fixedRowsListener = new ListChangeListener<Integer>() {
      public void onChanged(Change<? extends Integer> c) {
         while (c.next()) {
            if (c.wasAdded()) {
               List<? extends Integer> newRows = c.getAddedSubList();
               if (!SpreadsheetView.this.areRowsFixable(newRows)) {
                  throw new IllegalArgumentException(SpreadsheetView.this.computeReason(newRows));
               }

               FXCollections.sort(SpreadsheetView.this.fixedRows);
            }

            if (c.wasRemoved()) {
            }
         }
      }
   };
   private final ListChangeListener<SpreadsheetColumn> fixedColumnsListener = new ListChangeListener<SpreadsheetColumn>() {
      public void onChanged(Change<? extends SpreadsheetColumn> c) {
         while (c.next()) {
            if (c.wasAdded()) {
               List<? extends SpreadsheetColumn> newColumns = c.getAddedSubList();
               if (!SpreadsheetView.this.areSpreadsheetColumnsFixable(newColumns)) {
                  List<Integer> newList = new ArrayList<>();

                  for (SpreadsheetColumn column : newColumns) {
                     if (column != null) {
                        newList.add(SpreadsheetView.this.columns.indexOf(column));
                     }
                  }

                  throw new IllegalArgumentException(this.computeReason(newList));
               }
            }
         }
      }

      private String computeReason(List<Integer> list) {
         String reason = "\n This column cannot be frozen.";
         ObservableList<ObservableList<SpreadsheetCell>> rows = SpreadsheetView.this.getGrid().getRows();

         for (Integer columnIndex : list) {
            if (!SpreadsheetView.this.isColumnFixable(columnIndex)) {
               int maxSpan = 1;

               for (List<SpreadsheetCell> row : rows) {
                  SpreadsheetCell cell = row.get(columnIndex);
                  if (!list.contains(cell.getColumn())) {
                     reason = reason
                        + "The column "
                        + columnIndex
                        + " is inside a column span and the starting column "
                        + cell.getColumn()
                        + " is not frozen.\n";
                  }

                  if (cell.getColumnSpan() > maxSpan && cell.getColumn() == columnIndex) {
                     maxSpan = cell.getColumnSpan();
                  }
               }

               int count = columnIndex + maxSpan - 1;

               for (int index = columnIndex + 1; index < count; index++) {
                  if (!list.contains(index)) {
                     reason = reason
                        + "One cell on the column "
                        + columnIndex
                        + " has a column span of "
                        + maxSpan
                        + ". But the column "
                        + index
                        + " contained within that span is not frozen.\n";
                  }
               }
            }
         }

         return reason;
      }
   };
   private final ChangeListener<ContextMenu> contextMenuChangeListener = new ChangeListener<ContextMenu>() {
      public void changed(ObservableValue<? extends ContextMenu> arg0, ContextMenu oldContextMenu, ContextMenu newContextMenu) {
         if (oldContextMenu != null) {
            oldContextMenu.setOnShowing(null);
         }

         if (newContextMenu != null) {
            newContextMenu.setOnShowing(new WeakEventHandler(SpreadsheetView.this.hideContextMenuEventHandler));
         }
      }
   };
   private final EventHandler<WindowEvent> hideContextMenuEventHandler = new EventHandler<WindowEvent>() {
      public void handle(WindowEvent arg0) {
         if (SpreadsheetView.this.getEditingCell() != null) {
            Platform.runLater(() -> SpreadsheetView.this.getContextMenu().hide());
         }
      }
   };
   private final EventHandler<KeyEvent> keyPressedHandler = keyEvent -> {
      TablePosition<ObservableList<SpreadsheetCell>, ?> position = this.getSelectionModel().getFocusedCell();
      if (this.getEditingCell() == null && KeyCode.ENTER.equals(keyEvent.getCode())) {
         if (position != null) {
            if (keyEvent.isShiftDown()) {
               this.getCellsViewSkin().getBehavior().selectCell(-1, 0);
            } else {
               this.getCellsViewSkin().getBehavior().selectCell(1, 0);
            }

            keyEvent.consume();
         }

         this.getCellsViewSkin().scrollHorizontally();
      } else if (this.getEditingCell() == null && KeyCode.TAB.equals(keyEvent.getCode()) && !keyEvent.isShortcutDown()) {
         if (position != null) {
            if (keyEvent.isShiftDown()) {
               this.getSelectionModel().clearAndSelectLeftCell();
            } else {
               this.getSelectionModel().clearAndSelectRightCell();
            }
         }

         keyEvent.consume();
         this.getCellsViewSkin().scrollHorizontally();
      } else if (KeyCode.DELETE.equals(keyEvent.getCode())) {
         this.deleteSelectedCells();
      } else if (this.isEditionKey(keyEvent)) {
         this.getCellsView().edit(position.getRow(), position.getTableColumn());
      } else if (this.zoomNormalKeypad.match(keyEvent) || this.zoomNormal.match(keyEvent) || this.zoomNormalShift.match(keyEvent)) {
         this.setZoomFactor(1.0);
      } else if (this.zoomInChar.match(keyEvent) || this.zoomInKeypadAdd.match(keyEvent) || this.zoomInCharShift.match(keyEvent)) {
         this.incrementZoom();
      } else if (this.zoomOutChar.match(keyEvent) || this.zoomOutKeypad.match(keyEvent)) {
         this.decrementZoom();
      }
   };

   final GridViewSkin getCellsViewSkin() {
      return (GridViewSkin)this.cellsView.getSkin();
   }

   final SpreadsheetGridView getCellsView() {
      return this.cellsView;
   }

   public String getUserAgentStylesheet() {
      if (this.stylesheet == null) {
         this.stylesheet = SpreadsheetView.class.getResource("spreadsheet.css").toExternalForm();
      }

      return this.stylesheet;
   }

   void columnWidthSet(int indexColumn) {
      this.columnWidthSet.set(indexColumn);
   }

   public SpreadsheetView() {
      this(getSampleGrid());

      for (SpreadsheetColumn column : this.getColumns()) {
         column.setPrefWidth(100.0);
      }
   }

   public void setCellGraphicFactory(CellGraphicFactory cellGraphicFactory) {
      this.cellGraphicFactory = cellGraphicFactory;
   }

   public CellGraphicFactory getCellGraphicFactory() {
      return this.cellGraphicFactory;
   }

   public SpreadsheetView(Grid grid) {
      this.addEventHandler(SpreadsheetView.RowHeightEvent.ROW_HEIGHT_CHANGE, event -> {
         if (this.getFixedRows().contains(this.getModelRow(event.getRow())) && this.getCellsViewSkin() != null) {
            this.getCellsViewSkin().computeFixedRowHeight();
         }
      });
      this.hiddenRowsProperty.addListener(new InvalidationListener() {
         public void invalidated(Observable observable) {
            SpreadsheetView.this.computeRowMap();
            SpreadsheetView.this.initRowFix(SpreadsheetView.this.getGrid());
         }
      });
      this.hiddenColumnsProperty.addListener(new InvalidationListener() {
         public void invalidated(Observable observable) {
            SpreadsheetView.this.computeColumnMap();
            SpreadsheetView.this.initRowFix(SpreadsheetView.this.getGrid());
         }
      });
      this.getStyleClass().add("SpreadsheetView");
      this.setSkin(new Skin<SpreadsheetView>() {
         public Node getNode() {
            return SpreadsheetView.this.getCellsView();
         }

         public SpreadsheetView getSkinnable() {
            return SpreadsheetView.this;
         }

         public void dispose() {
         }
      });
      this.cellsView = new SpreadsheetGridView(this.handle);
      this.getChildren().add(this.cellsView);
      TableViewSpanSelectionModel tableViewSpanSelectionModel = new TableViewSpanSelectionModel(this, this.cellsView);
      this.cellsView.setSelectionModel(tableViewSpanSelectionModel);
      tableViewSpanSelectionModel.setCellSelectionEnabled(true);
      tableViewSpanSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);
      this.selectionModel = new SpreadsheetViewSelectionModel(this, tableViewSpanSelectionModel);
      this.cellsView.getFocusModel().focusedCellProperty().addListener(new FocusModelListener(this, this.cellsView));
      this.cellsView.addEventFilter(KeyEvent.KEY_PRESSED, this.keyPressedHandler);
      this.contextMenuProperty().addListener(new WeakChangeListener(this.contextMenuChangeListener));
      CellView.getValue(() -> this.setContextMenu(this.getSpreadsheetViewContextMenu()));
      this.setGrid(grid);
      this.setEditable(true);
      this.fixedRows.addListener(this.fixedRowsListener);
      this.fixedColumns.addListener(this.fixedColumnsListener);
      final Scale scale = new Scale(1.0, 1.0);
      this.getTransforms().add(scale);
      this.zoomFactor.addListener(new ChangeListener<Number>() {
         public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            scale.setX(newValue.doubleValue());
            scale.setY(newValue.doubleValue());
            SpreadsheetView.this.requestLayout();
         }
      });
      this.addEventFilter(ScrollEvent.ANY, event -> {
         if (event.isShortcutDown()) {
            if (event.getTextDeltaY() > 0.0) {
               this.incrementZoom();
            } else {
               this.decrementZoom();
            }

            event.consume();
         }
      });
   }

   protected void layoutChildren() {
      super.layoutChildren();
      Pos pos = Pos.TOP_LEFT;
      double width = this.getWidth();
      double height = this.getHeight();
      double top = this.getInsets().getTop();
      double right = this.getInsets().getRight();
      double left = this.getInsets().getLeft();
      double bottom = this.getInsets().getBottom();
      double contentWidth = (width - left - right) / this.zoomFactor.get();
      double contentHeight = (height - top - bottom) / this.zoomFactor.get();
      this.layoutInArea((Node)this.getChildren().get(0), left, top, contentWidth, contentHeight, 0.0, null, pos.getHpos(), pos.getVpos());
   }

   public boolean isRowHidden(int row) {
      return ((BitSet)this.hiddenRowsProperty.get()).get(row);
   }

   public BitSet getHiddenRows() {
      return (BitSet)this.hiddenRowsProperty.get();
   }

   public final ObjectProperty<BitSet> hiddenRowsProperty() {
      return this.hiddenRowsProperty;
   }

   public void setHiddenRows(BitSet hiddenRows) {
      BitSet bitSet = new BitSet(hiddenRows.size());
      bitSet.or(hiddenRows);
      this.hiddenRowsProperty.setValue(bitSet);
      this.requestLayout();
   }

   public void setHiddenColumns(BitSet hiddenColumns) {
      BitSet bitSet = new BitSet(hiddenColumns.size());
      bitSet.or(hiddenColumns);
      this.hiddenColumnsProperty.setValue(bitSet);
      this.requestLayout();
   }

   public boolean isColumnHidden(int column) {
      return ((BitSet)this.hiddenColumnsProperty.get()).get(column);
   }

   public BitSet getHiddenColumns() {
      return (BitSet)this.hiddenColumnsProperty.get();
   }

   public final ObjectProperty<BitSet> hiddenColumnsProperty() {
      return this.hiddenColumnsProperty;
   }

   public int getFilteredRow() {
      return this.filteredRow == null ? -1 : this.filteredRow;
   }

   public void setFilteredRow(Integer row) {
      if (row != null && row <= this.getGrid().getRowCount()) {
         this.filteredRow = row;
      } else {
         this.filteredRow = null;
      }
   }

   public void hideRow(int row) {
      if (!this.getHiddenRows().get(row)) {
         this.getHiddenRows().set(row, true);
         BitSet bitSet = new BitSet(this.getHiddenRows().size());
         bitSet.or(this.getHiddenRows());
         this.setHiddenRows(bitSet);
      }
   }

   public void hideColumn(SpreadsheetColumn column) {
      int indexColumn = this.getColumns().indexOf(column);
      if (!this.getHiddenColumns().get(indexColumn)) {
         this.getHiddenColumns().set(indexColumn, true);
         BitSet bitSet = new BitSet(this.getHiddenColumns().size());
         bitSet.or(this.getHiddenColumns());
         this.setHiddenColumns(bitSet);
      }
   }

   private void computeRowMap() {
      if (this.getHiddenRows().isEmpty()) {
         this.filteredList.setPredicate(null);
      } else {
         this.filteredList.setPredicate(new Predicate<ObservableList<SpreadsheetCell>>() {
            public boolean test(ObservableList<SpreadsheetCell> t) {
               int index = SpreadsheetView.this.identityMap.get(t);
               return !SpreadsheetView.this.getHiddenRows().get(index) || index == SpreadsheetView.this.getFilteredRow();
            }
         });
      }

      int rowCount = this.getGrid().getRowCount();
      this.rowMap = new HashMap<>(rowCount);
      int visibleRow = 0;

      for (int i = 0; i < rowCount; i++) {
         if (!this.getHiddenRows().get(i)) {
            this.rowMap.put(i, visibleRow++);
         } else {
            this.rowMap.put(i, visibleRow);
         }
      }
   }

   private void computeColumnMap() {
      int columnCount = this.getGrid().getColumnCount();
      this.columnMap = new HashMap<>(columnCount);
      CellView.getValue(() -> {
         int columnSize = this.getColumns().size();
         int totalColumn = this.getGrid().getColumnCount();
         int visibleColumn = 0;

         for (int i = 0; i < totalColumn; i++) {
            if (!this.getHiddenColumns().get(i)) {
               if (i < columnSize) {
                  ((SpreadsheetColumn)this.getColumns().get(i)).column.setVisible(true);
               }

               this.columnMap.put(i, visibleColumn++);
            } else {
               if (i < columnSize) {
                  ((SpreadsheetColumn)this.getColumns().get(i)).column.setVisible(false);
               }

               this.columnMap.put(i, visibleColumn);
            }
         }
      });
   }

   public void showRow(int row) {
      if (this.getHiddenRows().get(row)) {
         this.getHiddenRows().set(row, false);
         BitSet bitSet = new BitSet(this.getHiddenRows().size());
         bitSet.or(this.getHiddenRows());
         this.setHiddenRows(bitSet);
      }
   }

   public void showColumn(SpreadsheetColumn column) {
      int indexColumn = this.getColumns().indexOf(column);
      if (this.getHiddenColumns().get(indexColumn)) {
         this.getHiddenColumns().set(indexColumn, false);
         BitSet bitSet = new BitSet(this.getHiddenColumns().size());
         bitSet.or(this.getHiddenColumns());
         this.setHiddenColumns(bitSet);
      }
   }

   public int getFilteredRow(int modelRow) {
      try {
         return this.rowMap.get(modelRow);
      } catch (NullPointerException var3) {
         return modelRow;
      }
   }

   public int getViewColumn(int modelColumn) {
      try {
         return this.columnMap.get(modelColumn);
      } catch (NullPointerException var3) {
         return modelColumn;
      }
   }

   public int getModelColumn(int viewColumn) {
      try {
         return this.cellsView.getColumns().indexOf(this.cellsView.getVisibleLeafColumn(viewColumn));
      } catch (NullPointerException var3) {
         return viewColumn;
      }
   }

   public int getViewRow(int modelRow) {
      modelRow = this.getFilteredRow(modelRow);
      if (this.getComparator() != null) {
         modelRow = this.getViewIndex(modelRow);
      }

      return modelRow;
   }

   private int getViewIndex(int sourceIndex) {
      return this.sortedList.getViewIndex(sourceIndex);
   }

   public int getModelRow(int viewRow) {
      if (viewRow >= 0 && viewRow < this.sortedList.size()) {
         try {
            return this.getFilteredSourceIndex(this.sortedList.getSourceIndex(viewRow));
         } catch (IndexOutOfBoundsException | NullPointerException var3) {
            return viewRow;
         }
      } else {
         return viewRow;
      }
   }

   public int getFilteredSourceIndex(int viewRow) {
      if (viewRow >= 0 && viewRow < this.filteredList.size()) {
         try {
            return this.filteredList.getSourceIndex(viewRow);
         } catch (IndexOutOfBoundsException | NullPointerException var3) {
            return viewRow;
         }
      } else {
         return viewRow;
      }
   }

   public int getRowSpan(SpreadsheetCell cell, int index) {
      int rowSpan = 0;

      do {
         rowSpan++;
         index++;
      } while (
         index < this.sortedList.size()
            && cell.getColumn() < this.getGrid().getColumnCount()
            && ((ObservableList)this.sortedList.get(index)).get(cell.getColumn()) == cell
      );

      return rowSpan;
   }

   public int getReverseRowSpan(SpreadsheetCell cell, int index) {
      int rowSpan = 0;

      do {
         rowSpan++;
         index--;
      } while (index >= 0 && cell.getColumn() < this.getGrid().getColumnCount() && ((ObservableList)this.sortedList.get(index)).get(cell.getColumn()) == cell);

      return rowSpan;
   }

   public int getRowSpanFilter(SpreadsheetCell cell) {
      int rowSpan = cell.getRowSpan();

      for (int i = cell.getRow(); i < cell.getRow() + cell.getRowSpan(); i++) {
         rowSpan -= this.getHiddenRows().get(i) ? 1 : 0;
      }

      return rowSpan;
   }

   public ObservableList<ObservableList<SpreadsheetCell>> getItems() {
      return this.cellsView.getItems();
   }

   public int getColumnSpan(SpreadsheetCell cell) {
      int colSpan = cell.getColumnSpan();

      for (int i = cell.getColumn(); i < cell.getColumn() + cell.getColumnSpan(); i++) {
         colSpan -= this.getHiddenColumns().get(i) ? 1 : 0;
      }

      return colSpan;
   }

   public final Double getZoomFactor() {
      return this.zoomFactor.get();
   }

   public final void setZoomFactor(Double zoomFactor) {
      this.zoomFactor.set(zoomFactor);
   }

   public final DoubleProperty zoomFactorProperty() {
      return this.zoomFactor;
   }

   public void incrementZoom() {
      double newZoom = this.zoomFactor.getValue() + 0.1;
      newZoom *= 10.0;
      newZoom = Math.floor((double)((float)newZoom));
      newZoom /= 10.0;
      this.zoomFactor.setValue(newZoom > 2.0 ? 2.0 : newZoom);
   }

   public void decrementZoom() {
      double newZoom = this.zoomFactor.getValue() - 0.1;
      newZoom *= 10.0;
      newZoom = Math.ceil((double)((float)newZoom));
      newZoom /= 10.0;
      this.zoomFactor.setValue(newZoom < 0.2 ? 0.2 : newZoom);
   }

   public void edit(int row, SpreadsheetColumn column) {
      this.cellsView.edit(row, column == null ? null : column.column);
   }

   public Comparator getComparator() {
      return this.sortedList == null ? null : this.sortedList.getComparator();
   }

   public ObjectProperty<Comparator<? super ObservableList<SpreadsheetCell>>> comparatorProperty() {
      return this.sortedList.comparatorProperty();
   }

   public void setComparator(Comparator<ObservableList<SpreadsheetCell>> comparator) {
      this.sortedList.setComparator(comparator);
      this.computeRowMap();
      this.requestLayout();
   }

   public final void setGrid(Grid grid) {
      if (grid != null) {
         this.filteredList = new FilteredList(grid.getRows());
         this.sortedList = new SortedList(this.filteredList);
         this.gridProperty.set(grid);
         this.setHiddenRows(new BitSet(this.filteredList.getSource().size()));
         this.setHiddenColumns(new BitSet(grid.getColumnCount()));
         this.initRowFix(grid);
         List<Integer> newFixedRows = new ArrayList<>();

         for (Integer rowFixed : this.getFixedRows()) {
            if (this.isRowFixable(rowFixed)) {
               newFixedRows.add(rowFixed);
            }
         }

         this.getFixedRows().setAll(newFixedRows);
         List<Integer> columnsFixed = new ArrayList<>();

         for (SpreadsheetColumn column : this.getFixedColumns()) {
            columnsFixed.add(this.getColumns().indexOf(column));
         }

         this.getFixedColumns().clear();
         List<Double> widthColumns = new ArrayList<>();

         for (SpreadsheetColumn column : this.columns) {
            widthColumns.add(column.getWidth());
         }

         Pair<Integer, Integer> focusedPair = null;
         TablePosition focusedCell = this.cellsView.getFocusModel().getFocusedCell();
         if (focusedCell != null && focusedCell.getRow() != -1 && focusedCell.getColumn() != -1) {
            focusedPair = new Pair(focusedCell.getRow(), focusedCell.getColumn());
         }

         Pair<Integer, Integer> finalPair = focusedPair;
         if (grid.getRows() != null) {
            this.cellsView.setItems(this.sortedList);
            this.computeRowMap();
            int columnCount = grid.getColumnCount();
            this.columns.clear();

            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
               SpreadsheetColumn spreadsheetColumn = new SpreadsheetColumn(this.getTableColumn(grid, columnIndex), this, columnIndex, grid);
               if (widthColumns.size() > columnIndex) {
                  spreadsheetColumn.setPrefWidth(widthColumns.get(columnIndex));
               }

               this.columns.add(spreadsheetColumn);
               if (columnsFixed.contains(columnIndex) && spreadsheetColumn.isColumnFixable()) {
                  spreadsheetColumn.setFixed(true);
               }
            }
         }

         List<Pair<Integer, Integer>> selectedCells = new ArrayList<>();

         for (TablePosition position : this.getSelectionModel().getSelectedCells()) {
            selectedCells.add(new Pair(position.getRow(), position.getColumn()));
         }

         Runnable runnable = () -> {
            if (this.cellsView.getColumns().size() > grid.getColumnCount()) {
               this.cellsView.getColumns().remove(grid.getColumnCount(), this.cellsView.getColumns().size());
            } else if (this.cellsView.getColumns().size() < grid.getColumnCount()) {
               for (int i = this.cellsView.getColumns().size(); i < grid.getColumnCount(); i++) {
                  this.cellsView.getColumns().add(((SpreadsheetColumn)this.columns.get(i)).column);
               }
            }

            ((TableViewSpanSelectionModel)this.cellsView.getSelectionModel()).verifySelectedCells(selectedCells);
            if (finalPair != null
               && (Integer)finalPair.getKey() < this.getGrid().getRowCount()
               && (Integer)finalPair.getValue() < this.getGrid().getColumnCount()) {
               this.cellsView.getFocusModel().focus((Integer)finalPair.getKey(), (TableColumn)this.cellsView.getColumns().get((Integer)finalPair.getValue()));
            }
         };
         if (Platform.isFxApplicationThread()) {
            runnable.run();
         } else {
            try {
               FutureTask future = new FutureTask(runnable, null);
               Platform.runLater(future);
               future.get();
            } catch (ExecutionException | InterruptedException var11) {
               Logger.getLogger(SpreadsheetView.class.getName()).log(Level.SEVERE, null, var11);
            }
         }
      }
   }

   public TablePosition<ObservableList<SpreadsheetCell>, ?> getEditingCell() {
      return this.cellsView.getEditingCell();
   }

   public ReadOnlyObjectProperty<TablePosition<ObservableList<SpreadsheetCell>, ?>> editingCellProperty() {
      return this.cellsView.editingCellProperty();
   }

   public final ObservableList<SpreadsheetColumn> getColumns() {
      return this.columns;
   }

   public final Grid getGrid() {
      return (Grid)this.gridProperty.get();
   }

   public final ReadOnlyObjectProperty<Grid> gridProperty() {
      return this.gridProperty;
   }

   public ObservableList<Integer> getFixedRows() {
      return this.fixedRows;
   }

   public boolean isRowFixable(int row) {
      return row >= 0 && row < this.rowFix.size() && this.isFixingRowsAllowed() ? this.rowFix.get(row) : false;
   }

   public boolean areRowsFixable(List<? extends Integer> list) {
      if (list != null && !list.isEmpty() && this.isFixingRowsAllowed()) {
         Grid grid = this.getGrid();
         int rowCount = grid.getRowCount();
         ObservableList<ObservableList<SpreadsheetCell>> rows = grid.getRows();

         for (Integer row : list) {
            if (row == null || row < 0 || row >= rowCount) {
               return false;
            }

            if (!this.isRowFixable(row)) {
               int maxSpan = 1;

               for (SpreadsheetCell cell : (List)rows.get(row)) {
                  if (!list.contains(cell.getRow())) {
                     return false;
                  }

                  if (this.getRowSpan(cell, row) > maxSpan && cell.getRow() == row) {
                     maxSpan = cell.getRowSpan();
                  }
               }

               int count = row + maxSpan - 1;

               for (int index = row + 1; index <= count; index++) {
                  if (!list.contains(index)) {
                     return false;
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean isFixingRowsAllowed() {
      return this.fixingRowsAllowedProperty.get();
   }

   public void setFixingRowsAllowed(boolean b) {
      this.fixingRowsAllowedProperty.set(b);
   }

   public ReadOnlyBooleanProperty fixingRowsAllowedProperty() {
      return this.fixingRowsAllowedProperty;
   }

   public ObservableList<SpreadsheetColumn> getFixedColumns() {
      return this.fixedColumns;
   }

   public boolean isColumnFixable(int columnIndex) {
      return columnIndex >= 0 && columnIndex < this.getColumns().size() && this.isFixingColumnsAllowed()
         ? ((SpreadsheetColumn)this.getColumns().get(columnIndex)).isColumnFixable()
         : false;
   }

   public boolean areSpreadsheetColumnsFixable(List<? extends SpreadsheetColumn> list) {
      List<Integer> newList = new ArrayList<>();

      for (SpreadsheetColumn column : list) {
         if (column != null) {
            newList.add(this.columns.indexOf(column));
         }
      }

      return this.areColumnsFixable(newList);
   }

   public boolean areColumnsFixable(List<? extends Integer> list) {
      if (list != null && !list.isEmpty() && this.isFixingRowsAllowed()) {
         Grid grid = this.getGrid();
         int columnCount = grid.getColumnCount();
         ObservableList<ObservableList<SpreadsheetCell>> rows = grid.getRows();

         for (Integer columnIndex : list) {
            if (columnIndex == null || columnIndex < 0 || columnIndex >= columnCount) {
               return false;
            }

            if (!this.isColumnFixable(columnIndex)) {
               int maxSpan = 1;

               for (List<SpreadsheetCell> row : rows) {
                  SpreadsheetCell cell = row.get(columnIndex);
                  if (!list.contains(cell.getColumn())) {
                     return false;
                  }

                  if (cell.getColumnSpan() > maxSpan && cell.getColumn() == columnIndex) {
                     maxSpan = cell.getColumnSpan();
                  }
               }

               int count = columnIndex + maxSpan - 1;

               for (int index = columnIndex + 1; index <= count; index++) {
                  if (!list.contains(index)) {
                     return false;
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean isFixingColumnsAllowed() {
      return this.fixingColumnsAllowedProperty.get();
   }

   public void setFixingColumnsAllowed(boolean b) {
      this.fixingColumnsAllowedProperty.set(b);
   }

   public ReadOnlyBooleanProperty fixingColumnsAllowedProperty() {
      return this.fixingColumnsAllowedProperty;
   }

   public final void setShowColumnHeader(boolean b) {
      this.showColumnHeader.setValue(b);
   }

   public final boolean isShowColumnHeader() {
      return this.showColumnHeader.get();
   }

   public final BooleanProperty showColumnHeaderProperty() {
      return this.showColumnHeader;
   }

   public final void setShowRowHeader(boolean b) {
      this.showRowHeader.setValue(b);
   }

   public final boolean isShowRowHeader() {
      return this.showRowHeader.get();
   }

   public final BooleanProperty showRowHeaderProperty() {
      return this.showRowHeader;
   }

   public final DoubleProperty rowHeaderWidthProperty() {
      return this.rowHeaderWidth;
   }

   public final void setRowHeaderWidth(double value) {
      this.rowHeaderWidth.setValue(value);
   }

   public final double getRowHeaderWidth() {
      return this.rowHeaderWidth.get();
   }

   public ObservableMap<Integer, Picker> getRowPickers() {
      return this.rowPickers;
   }

   public ObservableMap<Integer, Picker> getColumnPickers() {
      return this.columnPickers;
   }

   public void resizeRowsToFitContent() {
      if (this.getCellsViewSkin() != null) {
         this.getCellsViewSkin().resizeRowsToFitContent();
      }
   }

   public void resizeRowsToMaximum() {
      if (this.getCellsViewSkin() != null) {
         this.getCellsViewSkin().resizeRowsToMaximum();
      }
   }

   public void resizeRowsToDefault() {
      if (this.getCellsViewSkin() != null) {
         this.getCellsViewSkin().resizeRowsToDefault();
      }
   }

   public double getRowHeight(int row) {
      return this.getCellsViewSkin() == null ? this.getGrid().getRowHeight(row) : this.getCellsViewSkin().getRowHeight(row);
   }

   public SpreadsheetViewSelectionModel getSelectionModel() {
      return this.selectionModel;
   }

   public void scrollToRow(int row) {
      this.cellsView.scrollTo(row);
   }

   public void setVBarValue(double value) {
      if (this.getCellsViewSkin() == null) {
         Platform.runLater(() -> this.setVBarValue(value));
      } else {
         this.getCellsViewSkin().getVBar().setValue(value);
      }
   }

   public void setHBarValue(double value) {
      this.setHBarValue(value, 0);
   }

   private void setHBarValue(double value, int attempt) {
      if (attempt <= 10) {
         if (this.getCellsViewSkin() == null) {
            Platform.runLater(() -> this.setHBarValue(value, newAttempt));
         } else {
            this.getCellsViewSkin().setHbarValue(value);
         }
      }
   }

   public double getVBarValue() {
      return this.getCellsViewSkin() != null && this.getCellsViewSkin().getVBar() != null ? this.getCellsViewSkin().getVBar().getValue() : 0.0;
   }

   public double getHBarValue() {
      return this.getCellsViewSkin() != null && this.getCellsViewSkin().getHBar() != null ? this.getCellsViewSkin().getHBar().getValue() : 0.0;
   }

   public void scrollToColumn(SpreadsheetColumn column) {
      this.cellsView.scrollToColumn(column.column);
   }

   public void scrollToColumnIndex(int modelColumn) {
      this.cellsView.scrollToColumnIndex(modelColumn);
   }

   public final Optional<SpreadsheetCellEditor> getEditor(SpreadsheetCellType<?> cellType) {
      if (cellType == null) {
         return Optional.empty();
      } else {
         SpreadsheetCellEditor cellEditor = this.editors.get(cellType);
         if (cellEditor == null) {
            cellEditor = cellType.createEditor(this);
            if (cellEditor == null) {
               return Optional.empty();
            }

            this.editors.put(cellType, cellEditor);
         }

         return Optional.of(cellEditor);
      }
   }

   public final void setEditable(boolean b) {
      this.cellsView.setEditable(b);
   }

   public final boolean isEditable() {
      return this.cellsView.isEditable();
   }

   public final BooleanProperty editableProperty() {
      return this.cellsView.editableProperty();
   }

   public final ObjectProperty<Node> placeholderProperty() {
      return this.cellsView.placeholderProperty();
   }

   public final void setPlaceholder(Node placeholder) {
      this.cellsView.setPlaceholder(placeholder);
   }

   public final Node getPlaceholder() {
      return this.cellsView.getPlaceholder();
   }

   public void copyClipboard() {
      this.checkFormat();
      ArrayList<ClipboardCell> list = new ArrayList<>();
      ObservableList<TablePosition> posList = this.getSelectionModel().getSelectedCells();
      Set<SpreadsheetCell> treatedCells = new HashSet<>();

      for (TablePosition<?, ?> p : posList) {
         SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)this.getGrid().getRows().get(this.getModelRow(p.getRow())))
            .get(this.getModelColumn(p.getColumn()));
         if (!treatedCells.contains(cell)) {
            treatedCells.add(cell);

            for (int row = 0; row < this.getRowSpan(cell, p.getRow()); row++) {
               for (int col = 0; col < this.getColumnSpan(cell); col++) {
                  list.add(new ClipboardCell(p.getRow() + row, p.getColumn() + col, cell));
               }
            }
         }
      }

      ClipboardContent content = new ClipboardContent();
      content.put(this.fmt, list);
      Clipboard.getSystemClipboard().setContent(content);
   }

   private void pasteOneValue(ClipboardCell change) {
      for (TablePosition position : this.getSelectionModel().getSelectedCells()) {
         this.tryPasteCell(this.getModelRow(position.getRow()), this.getModelColumn(position.getColumn()), change);
      }
   }

   private void tryPasteCell(int row, int column, ClipboardCell change) {
      SpreadsheetView.SpanType type = this.getSpanType(row, column);
      if (type == SpreadsheetView.SpanType.NORMAL_CELL || type == SpreadsheetView.SpanType.ROW_VISIBLE) {
         SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)this.getGrid().getRows().get(row)).get(column);
         Object value = change.getHtmlVersion() == null ? change.getValue() : change.getHtmlVersion();
         value = cell.isCellGraphic() ? value : change.getValue();
         boolean succeed = cell.getCellType().match(value, cell.getOptionsForEditor());
         if (succeed) {
            this.getGrid().setCellValue(cell.getRow(), cell.getColumn(), cell.getCellType().convertValue(value));
         }
      }
   }

   private void pasteMixedValues(ArrayList<ClipboardCell> list) {
      RectangleSelection.SelectionRange sourceSelectionRange = new RectangleSelection.SelectionRange();
      sourceSelectionRange.fillClipboardRange(list);
      if (sourceSelectionRange.getRange() != null) {
         RectangleSelection.SelectionRange targetSelectionRange = new RectangleSelection.SelectionRange();
         targetSelectionRange.fill(this.cellsView.getSelectionModel().getSelectedCells());
         if (targetSelectionRange.getRange() != null) {
            RectangleSelection.GridRange sourceRange = sourceSelectionRange.getRange();
            RectangleSelection.GridRange targetRange = targetSelectionRange.getRange();
            int sourceRowGap = sourceRange.getBottom() - sourceRange.getTop() + 1;
            int targetRowGap = targetRange.getBottom() - targetRange.getTop() + 1;
            int sourceColumnGap = sourceRange.getRight() - sourceRange.getLeft() + 1;
            int targetColumnGap = targetRange.getRight() - targetRange.getLeft() + 1;
            int offsetRow = targetRange.getTop() - sourceRange.getTop();
            int offsetCol = targetRange.getLeft() - sourceRange.getLeft();
            if ((sourceRowGap == targetRowGap || targetRowGap == 1) && targetColumnGap % sourceColumnGap == 0) {
               for (ClipboardCell change : list) {
                  int row = this.getModelRow(change.getRow() + offsetRow);
                  int column = change.getColumn() + offsetCol;

                  do {
                     int modelColumn = this.getModelColumn(column);
                     if (row < this.getGrid().getRowCount() && modelColumn < this.getGrid().getColumnCount() && row >= 0 && column >= 0) {
                        this.tryPasteCell(row, modelColumn, change);
                     }
                  } while ((column += sourceColumnGap) > targetRange.getRight());
               }
            } else if ((sourceColumnGap == targetColumnGap || targetColumnGap == 1) && targetRowGap % sourceRowGap == 0) {
               for (ClipboardCell change : list) {
                  int row = change.getRow() + offsetRow;
                  int column = this.getModelColumn(change.getColumn() + offsetCol);

                  do {
                     int modelRow = this.getModelRow(row);
                     if (modelRow < this.getGrid().getRowCount() && column < this.getGrid().getColumnCount() && row >= 0 && column >= 0) {
                        this.tryPasteCell(modelRow, column, change);
                     }
                  } while ((row += sourceRowGap) > targetRange.getBottom());
               }
            }
         }
      }
   }

   private void pasteSeveralValues(ArrayList<ClipboardCell> list) {
      int minRow = this.getGrid().getRowCount();
      int minCol = this.getGrid().getColumnCount();
      int maxRow = 0;
      int maxCol = 0;

      for (ClipboardCell p : list) {
         int tempcol = p.getColumn();
         int temprow = p.getRow();
         if (tempcol < minCol) {
            minCol = tempcol;
         }

         if (tempcol > maxCol) {
            maxCol = tempcol;
         }

         if (temprow < minRow) {
            minRow = temprow;
         }

         if (temprow > maxRow) {
            maxRow = temprow;
         }
      }

      TablePosition<?, ?> p = this.cellsView.getFocusModel().getFocusedCell();
      int offsetRow = p.getRow() - minRow;
      int offsetCol = p.getColumn() - minCol;
      int rowCount = this.getGrid().getRowCount();
      int columnCount = this.getGrid().getColumnCount();

      for (ClipboardCell change : list) {
         int row = this.getModelRow(change.getRow() + offsetRow);
         int column = this.getModelColumn(change.getColumn() + offsetCol);
         if (row < rowCount && column < columnCount && row >= 0 && column >= 0) {
            this.tryPasteCell(row, column, change);
         }
      }
   }

   public void pasteClipboard() {
      List<TablePosition> selectedCells = this.cellsView.getSelectionModel().getSelectedCells();
      if (this.isEditable() && !selectedCells.isEmpty()) {
         this.checkFormat();
         Clipboard clipboard = Clipboard.getSystemClipboard();
         if (clipboard.getContent(this.fmt) != null) {
            ArrayList<ClipboardCell> list = (ArrayList<ClipboardCell>)clipboard.getContent(this.fmt);
            if (list.size() == 1) {
               this.pasteOneValue(list.get(0));
            } else if (selectedCells.size() > 1) {
               this.pasteMixedValues(list);
            } else {
               this.pasteSeveralValues(list);
            }
         } else if (clipboard.hasString()) {
         }
      }
   }

   public ContextMenu getSpreadsheetViewContextMenu() {
      ContextMenu contextMenu = new ContextMenu();
      MenuItem copyItem = new MenuItem(Localization.localize(Localization.asKey("spreadsheet.view.menu.copy")));
      copyItem.setGraphic(new ImageView(new Image(SpreadsheetView.class.getResourceAsStream("copySpreadsheetView.png"))));
      copyItem.setAccelerator(new KeyCodeCombination(KeyCode.C, new Modifier[]{KeyCombination.SHORTCUT_DOWN}));
      copyItem.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent e) {
            SpreadsheetView.this.copyClipboard();
         }
      });
      MenuItem pasteItem = new MenuItem(Localization.localize(Localization.asKey("spreadsheet.view.menu.paste")));
      pasteItem.setGraphic(new ImageView(new Image(SpreadsheetView.class.getResourceAsStream("pasteSpreadsheetView.png"))));
      pasteItem.setAccelerator(new KeyCodeCombination(KeyCode.V, new Modifier[]{KeyCombination.SHORTCUT_DOWN}));
      pasteItem.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent e) {
            SpreadsheetView.this.pasteClipboard();
         }
      });
      Menu cornerMenu = new Menu(Localization.localize(Localization.asKey("spreadsheet.view.menu.comment")));
      cornerMenu.setGraphic(new ImageView(new Image(SpreadsheetView.class.getResourceAsStream("comment.png"))));
      MenuItem topLeftItem = new MenuItem(Localization.localize(Localization.asKey("spreadsheet.view.menu.comment.top-left")));
      topLeftItem.setOnAction(
         new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
               TablePosition<ObservableList<SpreadsheetCell>, ?> pos = SpreadsheetView.this.cellsView.getFocusModel().getFocusedCell();
               SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)SpreadsheetView.this.getGrid()
                     .getRows()
                     .get(SpreadsheetView.this.getModelRow(pos.getRow())))
                  .get(SpreadsheetView.this.getModelColumn(pos.getColumn()));
               cell.activateCorner(SpreadsheetCell.CornerPosition.TOP_LEFT);
            }
         }
      );
      MenuItem topRightItem = new MenuItem(Localization.localize(Localization.asKey("spreadsheet.view.menu.comment.top-right")));
      topRightItem.setOnAction(
         new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
               TablePosition<ObservableList<SpreadsheetCell>, ?> pos = SpreadsheetView.this.cellsView.getFocusModel().getFocusedCell();
               SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)SpreadsheetView.this.getGrid()
                     .getRows()
                     .get(SpreadsheetView.this.getModelRow(pos.getRow())))
                  .get(SpreadsheetView.this.getModelColumn(pos.getColumn()));
               cell.activateCorner(SpreadsheetCell.CornerPosition.TOP_RIGHT);
            }
         }
      );
      MenuItem bottomRightItem = new MenuItem(Localization.localize(Localization.asKey("spreadsheet.view.menu.comment.bottom-right")));
      bottomRightItem.setOnAction(
         new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
               TablePosition<ObservableList<SpreadsheetCell>, ?> pos = SpreadsheetView.this.cellsView.getFocusModel().getFocusedCell();
               SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)SpreadsheetView.this.getGrid()
                     .getRows()
                     .get(SpreadsheetView.this.getModelRow(pos.getRow())))
                  .get(SpreadsheetView.this.getModelColumn(pos.getColumn()));
               cell.activateCorner(SpreadsheetCell.CornerPosition.BOTTOM_RIGHT);
            }
         }
      );
      MenuItem bottomLeftItem = new MenuItem(Localization.localize(Localization.asKey("spreadsheet.view.menu.comment.bottom-left")));
      bottomLeftItem.setOnAction(
         new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
               TablePosition<ObservableList<SpreadsheetCell>, ?> pos = SpreadsheetView.this.cellsView.getFocusModel().getFocusedCell();
               SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)SpreadsheetView.this.getGrid()
                     .getRows()
                     .get(SpreadsheetView.this.getModelRow(pos.getRow())))
                  .get(SpreadsheetView.this.getModelColumn(pos.getColumn()));
               cell.activateCorner(SpreadsheetCell.CornerPosition.BOTTOM_LEFT);
            }
         }
      );
      cornerMenu.getItems().addAll(new MenuItem[]{topLeftItem, topRightItem, bottomRightItem, bottomLeftItem});
      contextMenu.getItems().addAll(new MenuItem[]{copyItem, pasteItem, cornerMenu});
      return contextMenu;
   }

   public void deleteSelectedCells() {
      for (TablePosition<ObservableList<SpreadsheetCell>, ?> position : this.getSelectionModel().getSelectedCells()) {
         this.getGrid().setCellValue(this.getModelRow(position.getRow()), this.getModelColumn(position.getColumn()), null);
      }
   }

   public SpreadsheetView.SpanType getSpanType(int rowIndex, int modelColumn) {
      if (this.getGrid() == null) {
         return SpreadsheetView.SpanType.NORMAL_CELL;
      } else if (rowIndex >= 0 && modelColumn >= 0 && rowIndex < this.getItems().size() && modelColumn < this.getGrid().getColumnCount()) {
         SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)this.getCellsView().getItems().get(rowIndex)).get(modelColumn);
         int cellColumn = this.getHiddenColumns().nextClearBit(cell.getColumn());
         int cellRowSpan = this.getRowSpanFilter(cell);
         if (cellColumn == modelColumn && cellRowSpan == 1) {
            return SpreadsheetView.SpanType.NORMAL_CELL;
         } else {
            int cellColumnSpan = this.getColumnSpan(cell);
            GridViewSkin skin = this.getCellsViewSkin();
            boolean containsRowMinusOne = skin == null ? true : skin.containsRow(rowIndex - 1);
            boolean containsSameCellMinusOne = rowIndex > 0
               ? ((ObservableList)this.getCellsView().getItems().get(rowIndex - 1)).get(modelColumn) == cell
               : false;
            if (containsRowMinusOne && cellColumnSpan > 1 && cellColumn != modelColumn && cellRowSpan > 1 && containsSameCellMinusOne) {
               return SpreadsheetView.SpanType.BOTH_INVISIBLE;
            } else if (cellRowSpan > 1 && cellColumn == modelColumn) {
               return containsSameCellMinusOne && containsRowMinusOne ? SpreadsheetView.SpanType.ROW_SPAN_INVISIBLE : SpreadsheetView.SpanType.ROW_VISIBLE;
            } else if (cellColumnSpan > 1 && (!containsSameCellMinusOne || !containsRowMinusOne)) {
               return cellColumn == modelColumn ? SpreadsheetView.SpanType.NORMAL_CELL : SpreadsheetView.SpanType.COLUMN_SPAN_INVISIBLE;
            } else {
               return SpreadsheetView.SpanType.NORMAL_CELL;
            }
         }
      } else {
         return SpreadsheetView.SpanType.NORMAL_CELL;
      }
   }

   private TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell> getTableColumn(Grid grid, int columnIndex) {
      String columnHeader = grid.getColumnHeaders().size() > columnIndex
         ? (String)grid.getColumnHeaders().get(columnIndex)
         : Utils.getExcelLetterFromNumber(columnIndex);
      TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell> column;
      if (columnIndex < this.cellsView.getColumns().size()) {
         column = (TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell>)this.cellsView.getColumns().get(columnIndex);
         column.setText(columnHeader);
      } else {
         column = new TableColumn(columnHeader);
         column.setEditable(true);
         column.setSortable(false);
         column.setReorderable(false);
         column.setCellValueFactory(
            p -> columnIndex >= ((ObservableList)p.getValue()).size()
                  ? null
                  : new ReadOnlyObjectWrapper((SpreadsheetCell)((ObservableList)p.getValue()).get(columnIndex))
         );
         column.setCellFactory(p -> new CellView(this.handle));
      }

      return column;
   }

   private static Grid getSampleGrid() {
      GridBase gridBase = new GridBase(100, 15);
      ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();

      for (int row = 0; row < gridBase.getRowCount(); row++) {
         ObservableList<SpreadsheetCell> currentRow = FXCollections.observableArrayList();

         for (int column = 0; column < gridBase.getColumnCount(); column++) {
            currentRow.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1, "toto"));
         }

         rows.add(currentRow);
      }

      gridBase.setRows(rows);
      return gridBase;
   }

   private void initRowFix(Grid grid) {
      ObservableList<ObservableList<SpreadsheetCell>> rows = grid.getRows();
      int rowSize = rows.size();
      this.rowFix = new BitSet(rowSize);
      this.identityMap = new IdentityHashMap<>(rowSize);

      label23:
      for (int r = 0; r < rowSize; r++) {
         ObservableList<SpreadsheetCell> row = (ObservableList<SpreadsheetCell>)rows.get(r);
         this.identityMap.put(row, r);

         for (SpreadsheetCell cell : row) {
            if (this.getRowSpanFilter(cell) > 1) {
               continue label23;
            }
         }

         this.rowFix.set(r);
      }
   }

   private void verifyGrid(Grid grid) {
      this.verifyColumnSpan(grid);
   }

   private void verifyColumnSpan(Grid grid) {
      for (int i = 0; i < grid.getRows().size(); i++) {
         ObservableList<SpreadsheetCell> row = (ObservableList<SpreadsheetCell>)grid.getRows().get(i);
         int count = 0;

         for (int j = 0; j < row.size(); j++) {
            if (((SpreadsheetCell)row.get(j)).getColumnSpan() == 1) {
               count++;
            } else {
               if (((SpreadsheetCell)row.get(j)).getColumnSpan() <= 1) {
                  throw new IllegalStateException("\n At row " + i + " and column " + j + ": this cell has a negative columnSpan");
               }

               count++;
               SpreadsheetCell currentCell = (SpreadsheetCell)row.get(j);

               for (int k = j + 1; k < currentCell.getColumn() + currentCell.getColumnSpan(); k++) {
                  if (!((SpreadsheetCell)row.get(k)).equals(currentCell)) {
                     throw new IllegalStateException(
                        "\n At row "
                           + i
                           + " and column "
                           + j
                           + ": this cell is in the range of a columnSpan but is different. \nEvery cell in a range of a ColumnSpan must be of the same instance."
                     );
                  }

                  count++;
                  j++;
               }
            }
         }

         if (count != grid.getColumnCount()) {
            throw new IllegalStateException("The row" + i + " has a number of cells different of the columnCount declared in the grid.");
         }
      }
   }

   private void checkFormat() {
      if ((this.fmt = DataFormat.lookupMimeType("SpreadsheetView")) == null) {
         this.fmt = new DataFormat(new String[]{"SpreadsheetView"});
      }
   }

   private String computeReason(List<? extends Integer> list) {
      String reason = "\n A row cannot be frozen. \n";

      for (Integer row : list) {
         if (!this.isRowFixable(row)) {
            int maxSpan = 1;

            for (SpreadsheetCell cell : (List)this.getGrid().getRows().get(row)) {
               if (!list.contains(cell.getRow())) {
                  reason = reason + "The row " + row + " is inside a row span and the starting row " + cell.getRow() + " is not frozen.\n";
               }

               if (cell.getRowSpan() > maxSpan && cell.getRow() == row) {
                  maxSpan = cell.getRowSpan();
               }
            }

            int count = row + maxSpan - 1;

            for (int index = row + 1; index < count; index++) {
               if (!list.contains(index)) {
                  reason = reason
                     + "One cell on the row "
                     + row
                     + " has a row span of "
                     + maxSpan
                     + ". But the row "
                     + index
                     + " contained within that span is not frozen.\n";
               }
            }
         }
      }

      return reason;
   }

   private boolean isEditionKey(KeyEvent keyEvent) {
      return !keyEvent.isShortcutDown()
         && !keyEvent.getCode().isNavigationKey()
         && !keyEvent.getCode().isFunctionKey()
         && !keyEvent.getCode().isModifierKey()
         && !keyEvent.getCode().isMediaKey()
         && keyEvent.getCode() != KeyCode.ESCAPE;
   }

   public static class ColumnWidthEvent extends Event {
      public static final EventType<SpreadsheetView.ColumnWidthEvent> COLUMN_WIDTH_CHANGE = new EventType(
         Event.ANY, "ColumnWidthChange" + UUID.randomUUID().toString()
      );
      private final int column;
      private final double width;

      public ColumnWidthEvent(int column, double width) {
         super(COLUMN_WIDTH_CHANGE);
         this.column = column;
         this.width = width;
      }

      public int getColumn() {
         return this.column;
      }

      public double getWidth() {
         return this.width;
      }
   }

   public static class RowHeightEvent extends Event {
      public static final EventType<SpreadsheetView.RowHeightEvent> ROW_HEIGHT_CHANGE = new EventType(
         Event.ANY, "RowHeightChange" + UUID.randomUUID().toString()
      );
      private final int modelRow;
      private final double height;

      public RowHeightEvent(int row, double height) {
         super(ROW_HEIGHT_CHANGE);
         this.modelRow = row;
         this.height = height;
      }

      public int getRow() {
         return this.modelRow;
      }

      public double getHeight() {
         return this.height;
      }
   }

   public static enum SpanType {
      NORMAL_CELL,
      COLUMN_SPAN_INVISIBLE,
      ROW_SPAN_INVISIBLE,
      ROW_VISIBLE,
      BOTH_INVISIBLE;
   }
}
