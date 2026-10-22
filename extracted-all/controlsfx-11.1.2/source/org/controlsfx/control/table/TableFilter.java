package org.controlsfx.control.table;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public final class TableFilter<T> {
   private final TableView<T> tableView;
   private final ObservableList<T> backingList;
   private final FilteredList<T> filteredList;
   private final ObservableList<ColumnFilter<T, ?>> columnFilters = FXCollections.observableArrayList();

   @Deprecated
   public TableFilter(TableView<T> tableView) {
      this(tableView, false);
   }

   private TableFilter(TableView<T> tableView, boolean isLazy) {
      this.tableView = tableView;
      this.backingList = tableView.getItems();
      this.filteredList = new FilteredList(new SortedList(this.backingList));
      SortedList<T> sortedControlList = new SortedList(this.filteredList);
      this.filteredList.setPredicate(v -> true);
      sortedControlList.comparatorProperty().bind(tableView.comparatorProperty());
      tableView.setItems(sortedControlList);
      this.applyForAllColumns();
      tableView.getStylesheets().add(TableFilter.class.getResource("/impl/org/controlsfx/table/tablefilter.css").toExternalForm());
      if (!isLazy) {
         this.columnFilters.forEach(ColumnFilter::initialize);
      }

      tableView.getColumns()
         .addListener(
            lc -> {
               while (lc.next()) {
                  this.columnFilters
                     .addAll(
                        lc.getAddedSubList().stream().flatMap(this::extractNestedColumns).map(c -> new ColumnFilter<>(this, c)).collect(Collectors.toList())
                     );
                  this.columnFilters
                     .removeAll(
                        lc.getRemoved()
                           .stream()
                           .flatMap(this::extractNestedColumns)
                           .flatMap(c -> this.columnFilters.stream().filter(cf -> cf.getTableColumn() == c))
                           .collect(Collectors.toList())
                     );
               }
            }
         );
   }

   public void setSearchStrategy(BiPredicate<String, String> searchStrategy) {
      this.columnFilters.forEach(cf -> cf.setSearchStrategy(searchStrategy));
   }

   public ObservableList<T> getBackingList() {
      return this.backingList;
   }

   public FilteredList<T> getFilteredList() {
      return this.filteredList;
   }

   private void applyForAllColumns() {
      this.columnFilters
         .setAll(this.tableView.getColumns().stream().flatMap(this::extractNestedColumns).map(c -> new ColumnFilter<>(this, c)).collect(Collectors.toList()));
   }

   private <S> Stream<TableColumn<T, ?>> extractNestedColumns(TableColumn<T, S> tableColumn) {
      return tableColumn.getColumns().size() == 0 ? Stream.of(tableColumn) : tableColumn.getColumns().stream().flatMap(this::extractNestedColumns);
   }

   public void selectValue(TableColumn<?, ?> column, Object value) {
      this.columnFilters.stream().filter(c -> c.getTableColumn() == column).forEach(c -> c.selectValue(value));
   }

   public void unselectValue(TableColumn<?, ?> column, Object value) {
      this.columnFilters.stream().filter(c -> c.getTableColumn() == column).forEach(c -> c.unselectValue(value));
   }

   public void selectAllValues(TableColumn<?, ?> column) {
      this.columnFilters.stream().filter(c -> c.getTableColumn() == column).forEach(ColumnFilter::selectAllValues);
   }

   public void unSelectAllValues(TableColumn<?, ?> column) {
      this.columnFilters.stream().filter(c -> c.getTableColumn() == column).forEach(ColumnFilter::unSelectAllValues);
   }

   public void executeFilter() {
      if (this.columnFilters.stream().anyMatch(ColumnFilter::isFiltered)) {
         this.filteredList.setPredicate(item -> this.columnFilters.stream().allMatch(cf -> cf.evaluate(item)));
      } else {
         this.resetFilter();
      }

      for (ColumnFilter columnFilter : this.columnFilters) {
         columnFilter.applyFilterIcon();
      }
   }

   public void resetFilter() {
      this.filteredList.setPredicate(item -> true);
   }

   public void resetAllFilters() {
      for (ColumnFilter columnFilter : this.columnFilters) {
         columnFilter.resetAllFilters();
         columnFilter.applyFilterIcon();
      }
   }

   public TableView<T> getTableView() {
      return this.tableView;
   }

   public ObservableList<ColumnFilter<T, ?>> getColumnFilters() {
      return this.columnFilters;
   }

   public Optional<ColumnFilter<T, ?>> getColumnFilter(TableColumn<T, ?> tableColumn) {
      Optional<ColumnFilter<T, ?>> result = this.columnFilters.stream().filter(f -> f.getTableColumn().equals(tableColumn)).findAny();
      result.ifPresent(ColumnFilter::initialize);
      return result;
   }

   public boolean isDirty() {
      return this.columnFilters.stream().anyMatch(ColumnFilter::isFiltered);
   }

   public static <T> TableFilter.Builder<T> forTableView(TableView<T> tableView) {
      return new TableFilter.Builder<>(tableView);
   }

   public static final class Builder<T> {
      private final TableView<T> tableView;
      private volatile boolean lazyInd = false;

      private Builder(TableView<T> tableView) {
         this.tableView = tableView;
      }

      public TableFilter.Builder<T> lazy(boolean isLazy) {
         this.lazyInd = isLazy;
         return this;
      }

      public TableFilter<T> apply() {
         return new TableFilter<>(this.tableView, this.lazyInd);
      }
   }
}
