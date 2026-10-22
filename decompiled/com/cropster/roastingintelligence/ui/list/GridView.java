package com.cropster.roastingintelligence.ui.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Callback;

public final class GridView<T> extends GridPane {
   private static final int DEFAULT_NUMBER_OF_COLUMNS = 2;
   private static final String DEFAULT_STYLE_CLASS = "grid-view";
   private final ListChangeListener<T> itemsListChangeListener = this::onItemsListChanged;
   private final Map<T, GridCell<T>> itemToCellMap = new HashMap<>();
   private MapChangeListener<Object, Object> propertiesMapListener = c -> {
      if (c.wasAdded()) {
         if ("recreateKey".equals(c.getKey())) {
            this.recreate();
         }
      }
   };
   private ObjectProperty<Callback<GridView<T>, GridCell<T>>> cellFactory;
   private IntegerProperty columns;
   private ObjectProperty<ObservableList<T>> items;

   public GridView() {
      this(2);
   }

   public GridView(int numberOfColumns) {
      this(FXCollections.observableArrayList(), numberOfColumns);
   }

   public GridView(ObservableList<T> items) {
      this(items, 2);
   }

   public GridView(ObservableList<T> items, int numberOfColumns) {
      this.getStyleClass().setAll(new String[]{"grid-view"});
      this.setColumns(numberOfColumns);
      this.setItems(items);
      this.setCellFactory(gv -> new GridView.DefaultGridCell());
      this.getProperties().addListener(this.propertiesMapListener);
   }

   public ObservableList<T> getItems() {
      return this.items == null ? null : (ObservableList)this.items.get();
   }

   public ReadOnlyObjectProperty<ObservableList<T>> itemsProperty() {
      if (this.items == null) {
         this.items = new SimpleObjectProperty(this, "items");
      }

      return this.items;
   }

   public void setItems(ObservableList<T> items) {
      ObservableList<T> oldList = this.getItems();
      if (oldList != null) {
         oldList.removeListener(this.itemsListChangeListener);
         this.getChildren().clear();
         oldList.stream().filter(Predicate.not(this.itemToCellMap::containsKey)).forEach(this.itemToCellMap::remove);
      }

      ((SimpleObjectProperty)this.itemsProperty()).set(items);
      ObservableList<T> newList = this.getItems();
      if (newList != null) {
         this.createChildren(newList);
         newList.addListener(this.itemsListChangeListener);
      }
   }

   public Callback<GridView<T>, GridCell<T>> getCellFactory() {
      return this.cellFactory == null ? null : (Callback)this.cellFactory.get();
   }

   public ObjectProperty<Callback<GridView<T>, GridCell<T>>> cellFactoryProperty() {
      if (this.cellFactory == null) {
         this.cellFactory = new SimpleObjectProperty(this, "cellFactory");
         this.cellFactory.addListener(this::onCellFactoryChanged);
      }

      return this.cellFactory;
   }

   public void setCellFactory(Callback<GridView<T>, GridCell<T>> factory) {
      this.cellFactoryProperty().set(factory);
   }

   public int getColumns() {
      return this.columns == null ? 2 : this.columns.get();
   }

   public IntegerProperty columnsProperty() {
      if (this.columns == null) {
         this.columns = new SimpleIntegerProperty(this, "numberOfColumns", 2);
         this.columns.addListener(this::onColumnsChanged);
         this.onColumnsChanged(this.columns, null, this.columns.get());
      }

      return this.columns;
   }

   public void setColumns(int numberOfColumns) {
      this.columnsProperty().set(numberOfColumns);
   }

   public void refresh() {
      this.getProperties().put("recreateKey", Boolean.TRUE);
   }

   private void recreate() {
      ObservableList<T> itemsList = this.getItems();
      List<T> itemsInGrid = new ArrayList<>(itemsList);
      itemsList.clear();
      itemsList.addAll(itemsInGrid);
      this.getProperties().remove("recreateKey");
   }

   private void onCellFactoryChanged(
      ObservableValue<? extends Callback<GridView<T>, GridCell<T>>> source,
      Callback<GridView<T>, GridCell<T>> oldCellFactory,
      Callback<GridView<T>, GridCell<T>> newCellFactory
   ) {
      this.itemToCellMap.clear();
      this.getChildren().clear();
      ObservableList<T> itemsList = this.getItems();
      if (itemsList != null) {
         this.createChildren(itemsList);
      }
   }

   private void createChildren(ObservableList<T> itemsList) {
      Callback<GridView<T>, GridCell<T>> cf = this.getCellFactory();
      if (cf == null) {
         this.itemToCellMap.clear();
      } else {
         int columnCount = this.getColumns();

         for (int i = 0; i < itemsList.size(); i++) {
            T item = (T)itemsList.get(i);
            GridCell<T> cell = this.getGridCell(item, cf, i, columnCount);
            this.getChildren().remove(cell);
            this.getChildren().add(cell);
         }
      }
   }

   private GridCell<T> getGridCell(T item, Callback<GridView<T>, GridCell<T>> cf, int i, int columnCount) {
      GridCell<T> cell = this.itemToCellMap.computeIfAbsent(item, t -> {
         GridCell<T> c = (GridCell<T>)cf.call(this);
         c.updateGridView(this);
         c.setItem(item);
         return c;
      });
      if (cell.getIndex() != i) {
         cell.updateIndex(i);
      }

      this.updateColumnIndex(i, columnCount, cell);
      this.updateRowIndex(i, columnCount, cell);
      return cell;
   }

   private void updateRowIndex(int i, int columnCount, Node cell) {
      int newRow = i / columnCount;
      Integer rowIndex = getRowIndex(cell);
      if (rowIndex == null || rowIndex != newRow) {
         setRowIndex(cell, newRow);
      }
   }

   private void updateColumnIndex(int i, int columnCount, Node cell) {
      int newColumn = i % columnCount;
      Integer columnIndex = getColumnIndex(cell);
      if (columnIndex == null || columnIndex != newColumn) {
         setColumnIndex(cell, newColumn);
      }
   }

   private void onItemsListChanged(Change<? extends T> change) {
      Callback<GridView<T>, GridCell<T>> cf = this.getCellFactory();
      if (cf == null) {
         this.itemToCellMap.clear();
      } else {
         int columnCount = this.getColumns();

         while (change.next()) {
            if (change.wasPermutated()) {
               this.updateChildrenColumnAndRowIndexes(change.getFrom(), columnCount);
            } else if (change.wasUpdated()) {
               this.onItemsUpdated(change);
            } else if (change.wasReplaced()) {
               this.onItemsReplaced(change, cf, columnCount);
            } else if (change.wasRemoved()) {
               this.onItemsRemoved(change, columnCount);
            } else if (change.wasAdded()) {
               this.onItemsAdded(change, cf, columnCount);
            }
         }
      }
   }

   private void onItemsUpdated(Change<? extends T> change) {
      for (int i = change.getFrom(); i < change.getTo(); i++) {
         this.updateCell((T)change.getList().get(i));
      }
   }

   private void onItemsReplaced(Change<? extends T> change, Callback<GridView<T>, GridCell<T>> cf, int columnCount) {
      List<? extends T> removedList = change.getRemoved();
      List<? extends T> addedList = change.getAddedSubList();

      for (int i = 0; i < change.getRemovedSize(); i++) {
         T removedItem = (T)removedList.get(i);
         T addedItem = (T)addedList.get(i);
         if (removedItem.equals(addedItem)) {
            this.updateCell(addedItem);
         } else {
            this.itemToCellMap.remove(removedItem);
            int replaceIndex = change.getFrom() + i;
            GridCell<T> cell = this.getGridCell(addedItem, cf, replaceIndex, columnCount);
            this.getChildren().set(replaceIndex, cell);
         }
      }
   }

   private void onItemsRemoved(Change<? extends T> change, int columnCount) {
      for (T removedItem : change.getRemoved()) {
         GridCell<T> cell = this.itemToCellMap.remove(removedItem);
         this.getChildren().remove(cell);
      }

      this.updateChildrenColumnAndRowIndexes(change.getFrom(), columnCount);
   }

   private void onItemsAdded(Change<? extends T> change, Callback<GridView<T>, GridCell<T>> cf, int columnCount) {
      List<? extends T> addedList = change.getAddedSubList();

      for (int i = 0; i < change.getAddedSize(); i++) {
         T addedItem = (T)addedList.get(i);
         int addIndex = change.getFrom() + i;
         GridCell<T> cell = this.getGridCell(addedItem, cf, addIndex, columnCount);
         this.getChildren().add(addIndex, cell);
      }

      this.updateChildrenColumnAndRowIndexes(change.getTo(), columnCount);
   }

   public void updateCell(T item) {
      if (item != null) {
         GridCell<T> cell = this.itemToCellMap.get(item);
         if (cell != null) {
            cell.updateItem(item, false);
         }
      }
   }

   private void updateChildrenColumnAndRowIndexes(int fromIndex, int columnCount) {
      for (int i = fromIndex; i < this.getChildren().size(); i++) {
         Node cell = (Node)this.getChildren().get(i);
         this.updateColumnIndex(i, columnCount, cell);
         this.updateRowIndex(i, columnCount, cell);
      }
   }

   private void onColumnsChanged(ObservableValue<? extends Number> source, Number oldColumns, Number newColumns) {
      ObservableList<T> itemList = this.getItems();
      int itemCount = itemList == null ? 0 : itemList.size();
      int newColumnCount = newColumns == null ? 2 : newColumns.intValue();
      this.updateColumnConstraints(newColumnCount);
      int newRowCount = itemCount == 0 ? 0 : (int)Math.ceil((double)itemCount / (double)newColumnCount);
      this.updateRowConstraints(newRowCount);
      this.updateCells(newColumnCount, newRowCount, itemCount, itemList);
   }

   private void updateColumnConstraints(int newColumnCount) {
      if (newColumnCount < this.getColumnConstraints().size()) {
         for (int c = this.getColumnConstraints().size() - 1; c >= newColumnCount; c--) {
            this.getColumnConstraints().remove(c);
         }

         this.updateColumnConstraintsPercentWidth(100.0 / (double)newColumnCount);
      } else if (newColumnCount > this.getColumnConstraints().size()) {
         for (int c = this.getColumnConstraints().size(); c < newColumnCount; c++) {
            this.getColumnConstraints().add(this.createColumnConstraints());
         }

         this.updateColumnConstraintsPercentWidth(100.0 / (double)newColumnCount);
      }
   }

   private ColumnConstraints createColumnConstraints() {
      ColumnConstraints cc = new ColumnConstraints();
      cc.setHalignment(HPos.CENTER);
      cc.setHgrow(Priority.ALWAYS);
      return cc;
   }

   private void updateColumnConstraintsPercentWidth(double percentWidth) {
      this.getColumnConstraints().forEach(cc -> cc.setPercentWidth(percentWidth));
   }

   private void updateRowConstraints(int newRowCount) {
      if (newRowCount < this.getRowConstraints().size()) {
         for (int r = this.getRowConstraints().size() - 1; r >= newRowCount; r--) {
            this.getRowConstraints().remove(r);
         }
      } else if (newRowCount > this.getRowConstraints().size()) {
         for (int r = this.getRowConstraints().size(); r < newRowCount; r++) {
            this.getRowConstraints().add(this.createRowConstraints());
         }
      }
   }

   private RowConstraints createRowConstraints() {
      RowConstraints rc = new RowConstraints();
      rc.setValignment(VPos.CENTER);
      rc.setVgrow(Priority.ALWAYS);
      return rc;
   }

   private void updateCells(int newColumnCount, int newRowCount, int itemCount, ObservableList<T> itemList) {
      int i = 0;

      for (int c = 0; c < newColumnCount; c++) {
         for (int r = 0; r < newRowCount && i < itemCount; r++) {
            T item = (T)itemList.get(i);
            GridCell<T> cell = this.itemToCellMap.get(item);
            if (cell != null) {
               setColumnIndex(cell, c);
               setRowIndex(cell, r);
            }

            i++;
         }
      }

      this.requestLayout();
   }

   private static class DefaultGridCell<T> extends GridCell<T> {
      private final GridView.DefaultGridCell.DefaultGridCellLabel<T> viewer = new GridView.DefaultGridCell.DefaultGridCellLabel<>();

      DefaultGridCell() {
      }

      @Override
      protected void initViewer(T item) {
         if (item != null) {
            this.viewer.init(item);
            this.setGraphic(this.viewer);
         }
      }

      private static final class DefaultGridCellLabel<W> extends Label {
         public void init(W item) {
            this.setText(item.toString());
            this.getStyleClass().add("debug-border");
            this.setMaxHeight(Double.MAX_VALUE);
            this.setMaxWidth(Double.MAX_VALUE);
            this.setAlignment(Pos.CENTER);
         }
      }
   }
}
