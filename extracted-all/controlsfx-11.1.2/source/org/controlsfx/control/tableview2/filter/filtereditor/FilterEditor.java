package org.controlsfx.control.tableview2.filter.filtereditor;

import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.filter.filtermenubutton.FilterMenuButton;

public class FilterEditor<S, T> extends ComboBox<T> {
   private static final PseudoClass PSEUDO_CLASS_ERROR = PseudoClass.getPseudoClass("error");
   private final FilteredTableColumn<S, T> tableColumn;
   private final ObservableList<T> filterValues;
   private final ListChangeListener<S> itemsListener = lc -> this.loadItems();
   private final WeakListChangeListener<S> weakListChangeListener = new WeakListChangeListener(this.itemsListener);
   private final ChangeListener<String> editorListener = (obs, ov, nv) -> {
      if (this.getTableColumn() != null && this.getTableColumn().getTableView() != null) {
         ObservableList<S> items = this.getTableColumn().getTableView().getItems();
         if (items != null) {
            items.removeListener(this.weakListChangeListener);
         }

         this.updatePredicate(nv);
         if (items != null) {
            items.addListener(this.weakListChangeListener);
         }
      }
   };
   private final WeakChangeListener<String> weakEditorListener = new WeakChangeListener(this.editorListener);
   private final ChangeListener<ObservableList<S>> itemsPropertyListener = (obs, ov, nv) -> {
      if (ov != null) {
         ov.removeListener(this.weakListChangeListener);
      }

      if (nv != null) {
         nv.addListener(this.weakListChangeListener);
      }

      Platform.runLater(() -> this.loadItems());
   };
   private final WeakChangeListener<ObservableList<S>> weakItemsPropertyListener = new WeakChangeListener(this.itemsPropertyListener);
   private InvalidationListener parentListener;
   private WeakInvalidationListener weakParentListener;
   private InvalidationListener tableListener;
   private WeakInvalidationListener weakTableListener;
   private FilterMenuButton menuButton;
   private final ChangeListener<Boolean> menuButtonListener = (obs, ov, nv) -> {
      if (!nv && ov) {
         this.updatePredicate(this.getEditor().getText());
      }
   };
   private final WeakChangeListener<Boolean> weakMenuButtonListener = new WeakChangeListener(this.menuButtonListener);

   public FilterEditor(FilteredTableColumn<S, T> tableColumn) {
      this.tableColumn = tableColumn;
      this.filterValues = FXCollections.observableArrayList();
      if (this.getParent() != null) {
         this.setMenuButton();
      } else {
         this.parentListener = o -> {
            if (this.getParent() != null) {
               this.setMenuButton();
               this.parentProperty().removeListener(this.weakParentListener);
            }
         };
         this.weakParentListener = new WeakInvalidationListener(this.parentListener);
         this.parentProperty().addListener(this.weakParentListener);
      }

      this.getStyleClass().add("filter-editor");
      if (tableColumn.getTableView() != null) {
         this.addItemsListeners();
      } else {
         this.tableListener = o -> {
            if (tableColumn.getTableView() != null) {
               this.addItemsListeners();
               this.tableColumn.tableViewProperty().removeListener(this.weakTableListener);
            }
         };
         this.weakTableListener = new WeakInvalidationListener(this.tableListener);
         this.tableColumn.tableViewProperty().addListener(this.weakTableListener);
      }

      this.setItems(this.filterValues);
      this.setEditable(true);
      this.setOnAction(e -> e.consume());
      this.getEditor().textProperty().addListener(this.weakEditorListener);
   }

   private void setMenuButton() {
      this.menuButton = ((SouthFilter)this.getParent()).getMenuButton();
      this.menuButton.showingProperty().addListener(this.weakMenuButtonListener);
   }

   private void addItemsListeners() {
      TableView<S> tableView = this.tableColumn.getTableView();
      tableView.itemsProperty().addListener(this.weakItemsPropertyListener);
      tableView.getItems().addListener(this.weakListChangeListener);
      this.loadItems();
   }

   private void loadItems() {
      T selection = (T)this.getValue();
      this.filterValues.clear();
      if (this.tableColumn != null && this.tableColumn.getTableView() != null && this.tableColumn.getTableView().getItems() != null) {
         this.tableColumn
            .getTableView()
            .getItems()
            .stream()
            .filter(s -> this.tableColumn.getCellData(s) != null)
            .map(this.tableColumn::getCellData)
            .distinct()
            .forEach(this.filterValues::add);
         this.setValue(selection);
      } else {
         this.setValue(null);
      }
   }

   private void updatePredicate(String text) {
      if (text != null && !text.isEmpty()) {
         this.getTableColumn().setPredicate((Predicate<? super T>)this.menuButton.parse(text, this.getConverter()));
      } else {
         this.getTableColumn().setPredicate(null);
      }

      this.updateError();
   }

   private void updateError() {
      String text = this.getEditor().getText();
      if (text != null && !text.isEmpty()) {
         String error = this.menuButton.getErrorMessage();
         if (error != null && !error.isEmpty()) {
            this.setTooltip(new Tooltip(error));
            this.getEditor().pseudoClassStateChanged(PSEUDO_CLASS_ERROR, true);
         } else {
            this.setTooltip(null);
            this.getEditor().pseudoClassStateChanged(PSEUDO_CLASS_ERROR, false);
         }
      } else {
         this.setTooltip(null);
         this.getEditor().pseudoClassStateChanged(PSEUDO_CLASS_ERROR, false);
      }
   }

   public FilteredTableColumn<S, T> getTableColumn() {
      return this.tableColumn;
   }

   public void cancelFilter() {
      this.setValue(null);
   }

   protected double computeMaxWidth(double height) {
      return Double.MAX_VALUE;
   }
}
