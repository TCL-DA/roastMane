package org.controlsfx.control.tableview2;

import impl.org.controlsfx.tableview2.RowHeader;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

public class FilteredTableColumn<S, T> extends TableColumn2<S, T> {
   private static final PseudoClass FILTER_ON = PseudoClass.getPseudoClass("filtered");
   private final HashMap<FilteredTableColumn.CellIdentity<S>, ChangeListener<T>> trackedCells = new HashMap<>();
   private final Button filterButton;
   private final ListChangeListener<S> backingListListener = lc -> {
      while (lc.next()) {
         if (lc.wasAdded()) {
            lc.getAddedSubList().forEach(s -> this.addBackingItem((S)s, this.getCellObservableValue(s)));
         }

         if (lc.wasRemoved()) {
            lc.getRemoved().forEach(s -> this.removeBackingItem((S)s, this.getCellObservableValue(s)));
         }
      }
   };
   private final WeakListChangeListener<S> weakListChangeListener = new WeakListChangeListener(this.backingListListener);
   private final ChangeListener<T> changeListener = (obs, ov, nv) -> Platform.runLater(() -> this.runOnFilteredTableView(FilteredTableView::filter));
   private final ChangeListener<Predicate<?>> filterListener = (obs, ov, nv) -> this.updateButton(nv != null);
   private final WeakChangeListener<Predicate<?>> weakFilterListener = new WeakChangeListener(this.filterListener);
   private final InvalidationListener tableListener = o -> this.init();
   private final WeakInvalidationListener weakTableListener = new WeakInvalidationListener(this.tableListener);
   private InvalidationListener itemsPropertyListener;
   private WeakInvalidationListener weakItemsPropertyListener;
   private InvalidationListener parentListener;
   private WeakInvalidationListener weakParentListener;
   private final BooleanProperty filterable = new SimpleBooleanProperty(this, "filterable", true);
   private final ObjectProperty<Predicate<? super T>> predicate = new SimpleObjectProperty<Predicate<? super T>>(this, "predicate", null) {
      protected void invalidated() {
         FilteredTableColumn.this.getFilteredTableView().ifPresent(FilteredTableView::filter);
      }
   };
   private final ObjectProperty<EventHandler<ActionEvent>> onFilterAction = new SimpleObjectProperty(this, "onFilterAction", null);

   public FilteredTableColumn() {
      this.tableViewProperty().addListener(this.weakTableListener);
      this.filterButton = new Button();
      this.filterButton.getStyleClass().add("filter");
      this.filterButton.onActionProperty().bind(this.onFilterActionProperty());
      this.filterButton.disableProperty().bind(this.filterableProperty().not());
      this.setGraphic(this.filterButton);
      this.updateButton(this.getPredicate() != null);
      this.predicateProperty().addListener(this.weakFilterListener);
   }

   public FilteredTableColumn(String text) {
      this();
      this.setText(text);
   }

   public final void setFilterable(boolean value) {
      this.filterable.set(value);
   }

   public final boolean isFilterable() {
      return this.filterable.get();
   }

   public final BooleanProperty filterableProperty() {
      return this.filterable;
   }

   public final void setPredicate(Predicate<? super T> value) {
      this.predicate.set(value);
   }

   public final Predicate<? super T> getPredicate() {
      return (Predicate<? super T>)this.predicate.get();
   }

   public final ObservableValue<Predicate<? super T>> predicateProperty() {
      return this.predicate;
   }

   public final ObjectProperty<EventHandler<ActionEvent>> onFilterActionProperty() {
      return this.onFilterAction;
   }

   public final void setOnFilterAction(EventHandler<ActionEvent> value) {
      this.onFilterAction.set(value);
   }

   public final EventHandler<ActionEvent> getOnFilterAction() {
      return (EventHandler<ActionEvent>)this.onFilterAction.get();
   }

   private void init() {
      this.runOnFilteredTableView(filteredTableView -> {
         this.itemsPropertyListener = o -> {
            if (filteredTableView.getItems() != null) {
               ObservableList<S> backingList = filteredTableView.getBackingList();
               if (backingList != null) {
                  backingList.forEach(s -> this.addBackingItem((S)s, this.getCellObservableValue(s)));
                  backingList.addListener(this.weakListChangeListener);
               }

               filteredTableView.itemsProperty().removeListener(this.weakItemsPropertyListener);
            }
         };
         this.weakItemsPropertyListener = new WeakInvalidationListener(this.itemsPropertyListener);
         filteredTableView.itemsProperty().addListener(this.weakItemsPropertyListener);
         if (filteredTableView.getParent() != null) {
            this.updateFilterButton(filteredTableView);
         } else {
            this.parentListener = o -> {
               if (filteredTableView.getParent() != null) {
                  this.updateFilterButton(filteredTableView);
                  filteredTableView.parentProperty().removeListener(this.weakParentListener);
               }
            };
            this.weakParentListener = new WeakInvalidationListener(this.parentListener);
            filteredTableView.parentProperty().addListener(this.weakParentListener);
         }
      });
   }

   private void updateButton(boolean value) {
      this.filterButton.pseudoClassStateChanged(FILTER_ON, value);
   }

   private void updateFilterButton(TableView<S> tableView) {
      if (tableView.getParent() instanceof RowHeader) {
         FilteredTableView<S> parentTableView = (FilteredTableView<S>)((RowHeader)tableView.getParent()).getParentTableView();
         this.updateButton(parentTableView.getPredicate() != null);
         parentTableView.predicateProperty().addListener(this.weakFilterListener);
      }
   }

   private void addBackingItem(S item, ObservableValue<T> cellValue) {
      if (cellValue != null) {
         FilteredTableColumn.CellIdentity<S> trackedCellValue = new FilteredTableColumn.CellIdentity<>(item);
         ChangeListener<T> cellListener = new WeakChangeListener(this.changeListener);
         cellValue.addListener(cellListener);
         this.trackedCells.put(trackedCellValue, cellListener);
      }
   }

   private void removeBackingItem(S item, ObservableValue<T> cellValue) {
      if (cellValue != null) {
         ChangeListener<T> listener = this.trackedCells.remove(new FilteredTableColumn.CellIdentity(item));
         cellValue.removeListener(listener);
      }
   }

   private void runOnFilteredTableView(Consumer<FilteredTableView> consumer) {
      this.getFilteredTableView().ifPresent(consumer);
   }

   private Optional<FilteredTableView<S>> getFilteredTableView() {
      TableView<S> tableView = this.getTableView();
      return tableView != null && tableView instanceof FilteredTableView ? Optional.of((FilteredTableView<S>)tableView) : Optional.empty();
   }

   private static final class CellIdentity<S> {
      private final S item;

      CellIdentity(S item) {
         this.item = item;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            FilteredTableColumn.CellIdentity<?> other = (FilteredTableColumn.CellIdentity<?>)obj;
            return Objects.equals(this.item, other.item);
         }
      }

      @Override
      public int hashCode() {
         return System.identityHashCode(this.item);
      }
   }
}
