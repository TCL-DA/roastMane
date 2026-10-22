package org.controlsfx.control.table;

import impl.org.controlsfx.ReflectionUtils;
import impl.org.controlsfx.i18n.Localization;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public final class FilterPanel<T, R> extends VBox {
   private final ColumnFilter<T, R> columnFilter;
   private final FilteredList<FilterValue> filterList;
   private final TextField searchBox = new TextField();
   private final ListView<FilterValue> checkListView;
   private final Collection<InvalidationListener> columnHeadersChangeListeners = new ArrayList<>();
   private final ChangeListener<Skin<?>> skinListener = (w, o, n) -> {
      this.columnHeadersChangeListeners.clear();
      if (n instanceof TableViewSkin) {
         TableViewSkin<?> skin = (TableViewSkin<?>)n;
         checkChangeContextMenu(skin, this.getColumnFilter().getTableColumn(), this);
      }
   };

   void selectAllValues() {
      this.checkListView.getItems().forEach(item -> item.selectedProperty().set(true));
   }

   void unSelectAllValues() {
      this.checkListView.getItems().forEach(item -> item.selectedProperty().set(false));
   }

   void selectValue(Object value) {
      this.checkListView
         .getItems()
         .stream()
         .filter(item -> item.getValue() == null && value == null || item.getValue() != null && value != null && item.getValue().equals(value))
         .forEach(item -> item.selectedProperty().set(true));
   }

   void unSelectValue(Object value) {
      this.checkListView
         .getItems()
         .stream()
         .filter(item -> item.getValue() == null && value == null || item.getValue() != null && value != null && item.getValue().equals(value))
         .forEach(item -> item.selectedProperty().set(false));
   }

   FilterPanel(ColumnFilter<T, R> columnFilter, ContextMenu contextMenu) {
      columnFilter.setFilterPanel(this);
      this.columnFilter = columnFilter;
      this.getStyleClass().add("filter-panel");
      this.setPadding(new Insets(3.0));
      this.searchBox.setPromptText(Localization.getString("filterpanel.search.field"));
      this.getChildren().add(this.searchBox);
      this.filterList = new FilteredList(new SortedList(columnFilter.getFilterValues()), t -> true);
      this.checkListView = new ListView();
      this.checkListView.setItems(new SortedList(this.filterList, FilterValue::compareTo));
      this.getChildren().add(this.checkListView);
      HBox buttonBox = new HBox();
      Button applyBttn = new Button(Localization.getString("filterpanel.apply.button"));
      HBox.setHgrow(applyBttn, Priority.ALWAYS);
      applyBttn.setOnAction(e -> {
         if (columnFilter.getTableFilter().isDirty()) {
            columnFilter.applyFilter();
         }

         contextMenu.hide();
      });
      buttonBox.getChildren().add(applyBttn);
      Button unselectAllButton = new Button(Localization.getString("filterpanel.none.button"));
      HBox.setHgrow(unselectAllButton, Priority.ALWAYS);
      unselectAllButton.setOnAction(e -> columnFilter.getFilterValues().forEach(v -> v.selectedProperty().set(false)));
      buttonBox.getChildren().add(unselectAllButton);
      Button selectAllButton = new Button(Localization.getString("filterpanel.all.button"));
      HBox.setHgrow(selectAllButton, Priority.ALWAYS);
      selectAllButton.setOnAction(e -> columnFilter.getFilterValues().forEach(v -> v.selectedProperty().set(true)));
      buttonBox.getChildren().add(selectAllButton);
      Button clearAllButton = new Button(Localization.getString("filterpanel.resetall.button"));
      HBox.setHgrow(clearAllButton, Priority.ALWAYS);
      clearAllButton.setOnAction(e -> {
         columnFilter.resetAllFilters();
         columnFilter.getTableFilter().getColumnFilters().forEach(cf -> cf.getTableColumn().setGraphic(null));
         contextMenu.hide();
      });
      buttonBox.getChildren().add(clearAllButton);
      buttonBox.setAlignment(Pos.BASELINE_CENTER);
      this.getChildren().add(buttonBox);
   }

   public void resetSearchFilter() {
      this.filterList.setPredicate(t -> true);
      this.searchBox.clear();
   }

   public static <T, R> CustomMenuItem getInMenuItem(ColumnFilter<T, R> columnFilter, ContextMenu contextMenu) {
      FilterPanel<T, R> filterPanel = new FilterPanel<>(columnFilter, contextMenu);
      CustomMenuItem menuItem = new CustomMenuItem();
      filterPanel.initializeListeners();
      menuItem.contentProperty().set(filterPanel);
      columnFilter.getTableFilter().getTableView().skinProperty().addListener(new WeakChangeListener(filterPanel.skinListener));
      menuItem.setHideOnClick(false);
      return menuItem;
   }

   private void initializeListeners() {
      this.searchBox
         .textProperty()
         .addListener(
            l -> {
               this.filterList
                  .setPredicate(
                     val -> this.searchBox.getText().isEmpty()
                           || this.columnFilter
                              .getSearchStrategy()
                              .test(this.searchBox.getText(), Optional.ofNullable(val.getValue()).map(Object::toString).orElse(""))
                  );
               this.columnFilter
                  .getFilterValues()
                  .stream()
                  .filter(
                     s -> !this.columnFilter
                           .getSearchStrategy()
                           .test(this.searchBox.getText(), Optional.ofNullable(s.getValue()).map(Object::toString).orElse(""))
                  )
                  .collect(Collectors.toList())
                  .forEach(s -> s.selectedProperty().set(false));
               this.columnFilter
                  .getFilterValues()
                  .stream()
                  .filter(
                     s -> this.columnFilter
                           .getSearchStrategy()
                           .test(this.searchBox.getText(), Optional.ofNullable(s.getValue()).map(Object::toString).orElse(""))
                  )
                  .collect(Collectors.toList())
                  .forEach(s -> s.selectedProperty().set(true));
            }
         );
   }

   private static void checkChangeContextMenu(TableViewSkin<?> skin, TableColumn<?, ?> column, FilterPanel filterPanel) {
      ReflectionUtils.getTableHeaderRowFrom(skin).ifPresent(tableHeaderRow -> ReflectionUtils.getRootHeaderFrom(tableHeaderRow).ifPresent(header -> {
            InvalidationListener listener = filterPanel.getOrCreateChangeListener(header, column);
            header.getColumnHeaders().addListener(new WeakInvalidationListener(listener));
            changeContextMenu(header, column);
         }));
   }

   private InvalidationListener getOrCreateChangeListener(NestedTableColumnHeader header, TableColumn<?, ?> column) {
      InvalidationListener listener = obs -> changeContextMenu(header, column);
      this.columnHeadersChangeListeners.add(listener);
      return listener;
   }

   private static void changeContextMenu(NestedTableColumnHeader header, TableColumn<?, ?> column) {
      TableColumnHeader headerSkin = scan(column, header);
      if (headerSkin != null) {
         headerSkin.setOnContextMenuRequested(ev -> {
            ContextMenu cMenu = column.getContextMenu();
            if (cMenu != null) {
               cMenu.show(headerSkin, Side.BOTTOM, 5.0, 5.0);
            }

            ev.consume();
         });
      }
   }

   private static TableColumnHeader scan(TableColumn<?, ?> search, TableColumnHeader header) {
      if (search.equals(header.getTableColumn())) {
         return header;
      } else {
         if (header instanceof NestedTableColumnHeader) {
            NestedTableColumnHeader parent = (NestedTableColumnHeader)header;

            for (int i = 0; i < parent.getColumnHeaders().size(); i++) {
               TableColumnHeader result = scan(search, (TableColumnHeader)parent.getColumnHeaders().get(i));
               if (result != null) {
                  return result;
               }
            }
         }

         return null;
      }
   }

   public ColumnFilter<T, R> getColumnFilter() {
      return this.columnFilter;
   }
}
