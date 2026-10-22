package org.controlsfx.control.tableview2;

import impl.org.controlsfx.tableview2.FilteredColumnPredicate;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.controlsfx.control.tableview2.event.FilterEvent;
import org.controlsfx.control.tableview2.filter.filtereditor.FilterEditor;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

public class FilteredTableView<S> extends TableView2<S> {
   private ObservableList<S> backingList;
   public static final Callback<FilteredTableView, Boolean> DEFAULT_FILTER_POLICY = table -> {
      try {
         ObservableList items = table.getItems();
         FilteredList filteredList = null;
         if (items instanceof FilteredList) {
            filteredList = (FilteredList)items;
         } else if (items instanceof SortedList) {
            SortedList sortedList = (SortedList)items;
            if (sortedList.getSource() instanceof FilteredList) {
               filteredList = (FilteredList)sortedList.getSource();
            }
         }

         if (filteredList != null) {
            if (table.getBackingList() == null) {
               table.setBackingList(filteredList.getSource());
            }

            boolean predicatesBound = filteredList.predicateProperty().isEqualTo(table.predicateProperty()).get();
            if (!predicatesBound) {
               String s = "FilteredTableView items list is a FilteredList, but the FilteredList predicate should be bound to the FilteredTableView predicate for filtering to be enabled (e.g. filteredList.predicateProperty().bind(tableView.predicateProperty());).";
               Logger.getLogger(FilteredTableView.class.getName()).log(Level.WARNING, s);
            }

            return predicatesBound;
         } else if (items != null && !items.isEmpty()) {
            String s = "FilteredTableView items list is not a FilteredList. Filtering options are not available unless the list is wrapped with a FilteredList. FilteredTableView.configureForFiltering(tableView, items); is called";
            Logger.getLogger(FilteredTableView.class.getName()).log(Level.WARNING, s);
            configureForFiltering(table, items);
            return true;
         } else {
            return true;
         }
      } catch (Exception var5) {
         return false;
      }
   };
   private ReadOnlyObjectWrapper<Predicate<S>> predicate;
   private ObjectProperty<Callback<TableView<S>, Boolean>> filterPolicy;
   private ObjectProperty<EventHandler<FilterEvent<TableView<S>>>> onFilter;

   public FilteredTableView() {
   }

   public FilteredTableView(ObservableList<S> items) {
      super(items);
      this.backingList = items;
   }

   public static <S> void configureForFiltering(FilteredTableView<S> tableView, ObservableList<S> items) {
      tableView.setBackingList(items);
      FilteredList<S> filteredData = new FilteredList(items);
      filteredData.predicateProperty().bind(tableView.predicateProperty());
      SortedList<S> sortedData = new SortedList(filteredData);
      sortedData.comparatorProperty().bind(tableView.comparatorProperty());
      tableView.setItems(sortedData);
   }

   public void setBackingList(ObservableList<S> backingList) {
      this.backingList = backingList;
   }

   private void setPredicate(Predicate<S> value) {
      this.predicatePropertyImpl().set(value);
   }

   public final Predicate<S> getPredicate() {
      return this.predicate == null ? null : (Predicate)this.predicate.get();
   }

   public final ReadOnlyObjectProperty<Predicate<S>> predicateProperty() {
      return this.predicatePropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyObjectWrapper<Predicate<S>> predicatePropertyImpl() {
      if (this.predicate == null) {
         this.predicate = new ReadOnlyObjectWrapper<Predicate<S>>(this, "predicate") {
            protected void invalidated() {
               if (this.get() == null) {
                  FilteredTableView.this.resetColumnsFilter();
               }
            }
         };
      }

      return this.predicate;
   }

   public final void setFilterPolicy(Callback<TableView<S>, Boolean> callback) {
      this.filterPolicyProperty().set(callback);
   }

   public final Callback<TableView<S>, Boolean> getFilterPolicy() {
      return this.filterPolicy == null ? DEFAULT_FILTER_POLICY : (Callback)this.filterPolicy.get();
   }

   public final ObjectProperty<Callback<TableView<S>, Boolean>> filterPolicyProperty() {
      if (this.filterPolicy == null) {
         this.filterPolicy = new SimpleObjectProperty<Callback<TableView<S>, Boolean>>(this, "filterPolicy", DEFAULT_FILTER_POLICY) {
            protected void invalidated() {
               FilteredTableView.this.filter();
            }
         };
      }

      return this.filterPolicy;
   }

   public final void setOnFilter(EventHandler<FilterEvent<TableView<S>>> value) {
      this.onFilterProperty().set(value);
   }

   public final EventHandler<FilterEvent<TableView<S>>> getOnFilter() {
      return this.onFilter != null ? (EventHandler)this.onFilter.get() : null;
   }

   public final ObjectProperty<EventHandler<FilterEvent<TableView<S>>>> onFilterProperty() {
      if (this.onFilter == null) {
         this.onFilter = new ObjectPropertyBase<EventHandler<FilterEvent<TableView<S>>>>() {
            protected void invalidated() {
               EventType<FilterEvent<TableView<S>>> eventType = FilterEvent.filterEvent();
               EventHandler<FilterEvent<TableView<S>>> eventHandler = (EventHandler<FilterEvent<TableView<S>>>)this.get();
               FilteredTableView.this.setEventHandler(eventType, eventHandler);
            }

            public Object getBean() {
               return FilteredTableView.this;
            }

            public String getName() {
               return "onFilter";
            }
         };
      }

      return this.onFilter;
   }

   public void resetFilter() {
      this.setPredicate(null);
      this.resetColumnsFilter();
   }

   public void filter() {
      Predicate<S> oldPredicate = this.getPredicate();
      boolean filterExists = this.getVisibleLeafColumns()
         .stream()
         .filter(FilteredTableColumn.class::isInstance)
         .map(FilteredTableColumn.class::cast)
         .filter(FilteredTableColumn::isFilterable)
         .noneMatch(f -> f.getPredicate() != null);
      this.setPredicate(filterExists ? null : new FilteredColumnPredicate<>(this.getVisibleLeafColumns()));
      FilterEvent<TableView<S>> filterEvent = new FilterEvent<>(this, this);
      this.fireEvent(filterEvent);
      if (filterEvent.isConsumed()) {
         this.setPredicate(oldPredicate);
      } else {
         Callback<TableView<S>, Boolean> filterPolicy = this.getFilterPolicy();
         if (filterPolicy != null) {
            boolean success = (Boolean)filterPolicy.call(this);
            if (!success) {
               this.setPredicate(oldPredicate);
            }
         }
      }
   }

   ObservableList<S> getBackingList() {
      return this.backingList;
   }

   private void resetColumnsFilter() {
      this.getVisibleLeafColumns()
         .stream()
         .filter(FilteredTableColumn.class::isInstance)
         .map(FilteredTableColumn.class::cast)
         .filter(FilteredTableColumn::isFilterable)
         .peek(c -> c.setPredicate(null))
         .map(f -> f.getSouthNode())
         .filter(Objects::nonNull)
         .filter(SouthFilter.class::isInstance)
         .map(f -> ((SouthFilter)f).getFilterEditor())
         .forEach(FilterEditor::cancelFilter);
   }
}
