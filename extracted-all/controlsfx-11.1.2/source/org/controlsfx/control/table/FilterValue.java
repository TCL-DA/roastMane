package org.controlsfx.control.table;

import java.util.Optional;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public final class FilterValue<T, R> extends HBox implements Comparable<FilterValue<T, R>> {
   private final R value;
   private final BooleanProperty isSelected = new SimpleBooleanProperty(true);
   private final BooleanProperty inScope = new SimpleBooleanProperty(true);
   private final ColumnFilter<T, R> columnFilter;
   private final InvalidationListener scopeListener;

   FilterValue(R value, ColumnFilter<T, R> columnFilter) {
      this.value = value;
      this.columnFilter = columnFilter;
      CheckBox checkBox = new CheckBox();
      Label label = new Label();
      label.setText(Optional.ofNullable(value).map(Object::toString).orElse(null));
      this.scopeListener = v -> label.textFillProperty().set(this.getInScopeProperty().get() ? Color.BLACK : Color.LIGHTGRAY);
      this.inScope.addListener(new WeakInvalidationListener(this.scopeListener));
      checkBox.selectedProperty().bindBidirectional(this.selectedProperty());
      this.getChildren().addAll(new Node[]{checkBox, label});
   }

   public R getValue() {
      return this.value;
   }

   public BooleanProperty selectedProperty() {
      return this.isSelected;
   }

   public BooleanProperty getInScopeProperty() {
      return this.inScope;
   }

   void refreshScope() {
      this.inScope.setValue(this.columnFilter.wasLastFiltered() || this.columnFilter.valueIsVisible(this.value));
   }

   @Override
   public String toString() {
      return Optional.ofNullable(this.value).map(Object::toString).orElse("");
   }

   public int compareTo(FilterValue<T, R> other) {
      return this.value != null && other.value != null && this.value instanceof Comparable && other.value instanceof Comparable
         ? ((Comparable)this.value).compareTo((R)((Comparable)other.value))
         : Optional.ofNullable(this.value).map(Object::toString).orElse("").compareTo(Optional.ofNullable(other).map(Object::toString).orElse(""));
   }
}
