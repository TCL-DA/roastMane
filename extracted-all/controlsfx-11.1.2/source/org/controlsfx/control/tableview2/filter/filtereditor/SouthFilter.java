package org.controlsfx.control.tableview2.filter.filtereditor;

import impl.org.controlsfx.tableview2.filter.filtermenubutton.DefaultFilterMenuButtonFactory;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.filter.filtermenubutton.FilterMenuButton;

public class SouthFilter<S, T> extends HBox {
   private final FilterEditor<S, T> filterEditor;
   private FilterMenuButton menuButton;
   private final FilteredTableColumn<S, T> tableColumn;
   private final Class<T> clazz;
   private final InvalidationListener buttonListener = o -> {
      this.getChildren().remove(this.menuButton);
      this.menuButton = this.getFilterMenuButtonFactory();
      this.getChildren().add(this.menuButton);
   };
   private SimpleObjectProperty<FilterMenuButton> filterMenuButtonFactory;

   public SouthFilter(FilteredTableColumn<S, T> tableColumn, Class<T> clazz) {
      this.tableColumn = tableColumn;
      this.clazz = clazz;
      this.filterEditor = new FilterEditor<>(tableColumn);
      HBox.setHgrow(this.filterEditor, Priority.ALWAYS);
      this.menuButton = this.getFilterMenuButtonFactory();
      this.filterMenuButtonFactoryProperty().addListener(new WeakInvalidationListener(this.buttonListener));
      this.getStyleClass().add("south-filter");
      this.getChildren().addAll(new Node[]{this.menuButton, this.filterEditor});
   }

   public FilterEditor<S, T> getFilterEditor() {
      return this.filterEditor;
   }

   public FilteredTableColumn<S, T> getTableColumn() {
      return this.tableColumn;
   }

   public FilterMenuButton getMenuButton() {
      return this.menuButton;
   }

   public final SimpleObjectProperty<FilterMenuButton> filterMenuButtonFactoryProperty() {
      if (this.filterMenuButtonFactory == null) {
         this.filterMenuButtonFactory = new SimpleObjectProperty(this, "filterMenuButtonFactory", DefaultFilterMenuButtonFactory.forClass(this.clazz));
      }

      return this.filterMenuButtonFactory;
   }

   public final FilterMenuButton getFilterMenuButtonFactory() {
      return (FilterMenuButton)this.filterMenuButtonFactoryProperty().get();
   }

   public final void setFilterMenuButtonFactory(FilterMenuButton value) {
      this.filterMenuButtonFactoryProperty().set(value);
   }
}
