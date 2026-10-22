package org.controlsfx.control.table;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class ColumnFilter<T, R> {
   private final TableFilter<T> tableFilter;
   private final TableColumn<T, R> tableColumn;
   private final ObservableList<FilterValue<T, R>> filterValues;
   private final DupeCounter<R> filterValuesDupeCounter = new DupeCounter(false);
   private final DupeCounter<R> visibleValuesDupeCounter = new DupeCounter(false);
   private final HashSet<R> unselectedValues = new HashSet<>();
   private final HashMap<ColumnFilter.CellIdentity<T>, ChangeListener<R>> trackedCells = new HashMap<>();
   private static final Image filterIcon = new Image(ColumnFilter.class.getResource("/impl/org/controlsfx/table/filter.png").toExternalForm());
   private boolean bumpedWidth = false;
   private boolean lastFilter = false;
   private boolean isDirty = false;
   private BiPredicate<String, String> searchStrategy = (inputString, subjectString) -> subjectString.toLowerCase().contains(inputString.toLowerCase());
   private volatile FilterPanel filterPanel;
   private ImageView imageView;
   private boolean initialized = false;
   private final ListChangeListener<T> backingListListener = lc -> {
      while (lc.next()) {
         if (lc.wasAdded()) {
            lc.getAddedSubList().forEach(t -> this.addBackingItem((T)t, this.getTableColumn().getCellObservableValue(t)));
         }

         if (lc.wasRemoved()) {
            lc.getRemoved().forEach(t -> this.removeBackingItem((T)t, this.getTableColumn().getCellObservableValue(t)));
         }
      }
   };
   private final ListChangeListener<T> itemsListener = lc -> {
      while (lc.next()) {
         if (lc.wasAdded()) {
            lc.getAddedSubList().stream().<ObservableValue<R>>map(this.getTableColumn()::getCellObservableValue).forEach(this::addVisibleItem);
         }

         if (lc.wasRemoved()) {
            lc.getRemoved().stream().<ObservableValue<R>>map(this.getTableColumn()::getCellObservableValue).forEach(this::removeVisibleItem);
         }
      }
   };
   private final ChangeListener<R> changeListener = (observable, oldValue, newValue) -> {
      if (this.filterValuesDupeCounter.add((R)newValue) == 1) {
         this.getFilterValues().add(new FilterValue<>(newValue, this));
      }

      this.removeValue((R)oldValue);
   };
   private final ListChangeListener<FilterValue<T, R>> filterValueListChangeListener = lc -> {
      while (lc.next()) {
         if (lc.wasRemoved()) {
            lc.getRemoved().stream().filter(v -> !v.selectedProperty().get()).forEach(this.unselectedValues::remove);
         }

         if (lc.wasUpdated()) {
            int from = lc.getFrom();
            int to = lc.getTo();
            lc.getList().subList(from, to).forEach(v -> {
               this.isDirty = true;
               boolean value = v.selectedProperty().getValue();
               if (!value) {
                  this.unselectedValues.add((R)v.getValue());
               } else {
                  this.unselectedValues.remove(v.getValue());
               }
            });
         }
      }
   };

   public ColumnFilter(TableFilter<T> tableFilter, TableColumn<T, R> tableColumn) {
      this.tableFilter = tableFilter;
      this.tableColumn = tableColumn;
      this.filterValues = FXCollections.observableArrayList(cb -> new Observable[]{cb.selectedProperty()});
      this.attachContextMenu();
   }

   void setFilterPanel(FilterPanel filterPanel) {
      this.filterPanel = filterPanel;
   }

   FilterPanel getFilterPanel() {
      return this.filterPanel;
   }

   void applyFilterIcon() {
      if (this.hasUnselections()) {
         this.tableColumn.setGraphic(this.imageView);
         if (!this.bumpedWidth) {
            this.tableColumn.setPrefWidth(this.tableColumn.getWidth() + 20.0);
            this.bumpedWidth = true;
         }
      } else {
         this.tableColumn.setGraphic(null);
      }
   }

   public void initialize() {
      if (!this.initialized) {
         this.initializeListeners();
         this.initializeValues();
         this.initialized = true;
      }
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public void selectValue(Object value) {
      if (!this.isInitialized()) {
         this.initialize();
      }

      this.filterPanel.selectValue(value);
   }

   public void unselectValue(Object value) {
      if (!this.isInitialized()) {
         this.initialize();
      }

      this.filterPanel.unSelectValue(value);
   }

   public void selectAllValues() {
      if (!this.isInitialized()) {
         this.initialize();
      }

      this.filterPanel.selectAllValues();
   }

   public void unSelectAllValues() {
      if (!this.isInitialized()) {
         this.initialize();
      }

      this.filterPanel.unSelectAllValues();
   }

   boolean wasLastFiltered() {
      return this.lastFilter;
   }

   boolean hasUnselections() {
      return this.unselectedValues.size() != 0;
   }

   public void setSearchStrategy(BiPredicate<String, String> searchStrategy) {
      this.searchStrategy = searchStrategy;
   }

   public BiPredicate<String, String> getSearchStrategy() {
      return this.searchStrategy;
   }

   public boolean isFiltered() {
      return this.isDirty || this.unselectedValues.size() > 0;
   }

   public boolean valueIsVisible(R value) {
      return this.visibleValuesDupeCounter.get(value) > 0;
   }

   public void applyFilter() {
      this.tableFilter.executeFilter();
      this.lastFilter = true;
      this.tableFilter.getColumnFilters().stream().filter(c -> !c.equals(this)).forEach(c -> c.lastFilter = false);
      this.tableFilter.getColumnFilters().stream().flatMap(c -> c.filterValues.stream()).forEach(FilterValue::refreshScope);
      this.isDirty = false;
   }

   public ObservableList<FilterValue<T, R>> getFilterValues() {
      return this.filterValues;
   }

   public TableColumn<T, R> getTableColumn() {
      return this.tableColumn;
   }

   public TableFilter<T> getTableFilter() {
      return this.tableFilter;
   }

   void resetAllFilters() {
      this.tableFilter.getColumnFilters().stream().flatMap(c -> c.filterValues.stream()).forEach(fv -> fv.selectedProperty().set(true));
      this.tableFilter.resetFilter();
      this.tableFilter.getColumnFilters().forEach(c -> c.lastFilter = false);
      this.tableFilter.getColumnFilters().stream().flatMap(c -> c.filterValues.stream()).forEach(FilterValue::refreshScope);
      this.isDirty = false;
   }

   boolean evaluate(T item) {
      ObservableValue<R> value = this.tableColumn.getCellObservableValue(item);
      return this.unselectedValues.size() == 0 || !this.unselectedValues.contains(value.getValue());
   }

   private void initializeValues() {
      this.imageView = new ImageView(filterIcon);
      this.imageView.setFitHeight(15.0);
      this.imageView.setPreserveRatio(true);
      this.tableFilter.getBackingList().forEach(t -> this.addBackingItem((T)t, this.tableColumn.getCellObservableValue(t)));
      this.tableFilter.getTableView().getItems().stream().<ObservableValue<R>>map(this.tableColumn::getCellObservableValue).forEach(this::addVisibleItem);
   }

   private void addBackingItem(T item, ObservableValue<R> cellValue) {
      if (cellValue != null) {
         if (this.filterValuesDupeCounter.add((R)cellValue.getValue()) == 1) {
            this.filterValues.add(new FilterValue<>(cellValue.getValue(), this));
         }

         ColumnFilter.CellIdentity<T> trackedCellValue = new ColumnFilter.CellIdentity<>(item);
         ChangeListener<R> cellListener = new WeakChangeListener(this.changeListener);
         cellValue.addListener(cellListener);
         this.trackedCells.put(trackedCellValue, cellListener);
      }
   }

   private void removeBackingItem(T item, ObservableValue<R> cellValue) {
      if (cellValue != null) {
         this.removeValue((R)cellValue.getValue());
         ChangeListener<R> listener = this.trackedCells.get(new ColumnFilter.CellIdentity(item));
         if (listener != null) {
            cellValue.removeListener(listener);
         }

         this.trackedCells.remove(new ColumnFilter.CellIdentity(item));
      }
   }

   private void removeValue(R value) {
      boolean removedLastDuplicate = this.filterValuesDupeCounter.remove(value) == 0;
      if (removedLastDuplicate) {
         Optional<FilterValue<T, R>> existingFilterValue = this.getFilterValues().stream().filter(fv -> Objects.equals(fv.getValue(), value)).findAny();
         existingFilterValue.ifPresent(trFilterValue -> this.getFilterValues().remove(trFilterValue));
      }
   }

   private void addVisibleItem(ObservableValue<R> cellValue) {
      if (cellValue != null) {
         this.visibleValuesDupeCounter.add((R)cellValue.getValue());
      }
   }

   private void removeVisibleItem(ObservableValue<R> cellValue) {
      if (cellValue != null) {
         this.visibleValuesDupeCounter.remove((R)cellValue.getValue());
      }
   }

   private void initializeListeners() {
      this.tableFilter.getBackingList().addListener(new WeakListChangeListener(this.backingListListener));
      this.tableFilter.getTableView().getItems().addListener(new WeakListChangeListener(this.itemsListener));
      this.filterValues.addListener(new WeakListChangeListener(this.filterValueListChangeListener));
   }

   private void attachContextMenu() {
      ContextMenu contextMenu = new ContextMenu();
      CustomMenuItem item = FilterPanel.getInMenuItem(this, contextMenu);
      contextMenu.getStyleClass().add("column-filter");
      contextMenu.getItems().add(item);
      this.tableColumn.setContextMenu(contextMenu);
      contextMenu.setOnShowing(ae -> this.initialize());
   }

   private static final class CellIdentity<T> {
      private final T item;

      CellIdentity(T item) {
         this.item = item;
      }

      @Override
      public boolean equals(Object other) {
         return this.item == ((ColumnFilter.CellIdentity)other).item;
      }

      @Override
      public int hashCode() {
         return System.identityHashCode(this.item);
      }
   }
}
