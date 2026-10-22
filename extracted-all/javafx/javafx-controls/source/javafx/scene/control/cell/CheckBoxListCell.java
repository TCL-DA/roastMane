package javafx.scene.control.cell;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class CheckBoxListCell<T> extends ListCell<T> {
   private final CheckBox checkBox;
   private ObservableValue<Boolean> booleanProperty;
   private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty(this, "converter");
   private ObjectProperty<Callback<T, ObservableValue<Boolean>>> selectedStateCallback = new SimpleObjectProperty(this, "selectedStateCallback");

   public static <T> Callback<ListView<T>, ListCell<T>> forListView(Callback<T, ObservableValue<Boolean>> var0) {
      return forListView(var0, CellUtils.defaultStringConverter());
   }

   public static <T> Callback<ListView<T>, ListCell<T>> forListView(Callback<T, ObservableValue<Boolean>> var0, StringConverter<T> var1) {
      return var2 -> new CheckBoxListCell(var0, var1);
   }

   public CheckBoxListCell() {
      this(null);
   }

   public CheckBoxListCell(Callback<T, ObservableValue<Boolean>> var1) {
      this(var1, CellUtils.defaultStringConverter());
   }

   public CheckBoxListCell(Callback<T, ObservableValue<Boolean>> var1, StringConverter<T> var2) {
      this.getStyleClass().add("check-box-list-cell");
      this.setSelectedStateCallback(var1);
      this.setConverter(var2);
      this.checkBox = new CheckBox();
      this.setAlignment(Pos.CENTER_LEFT);
      this.setContentDisplay(ContentDisplay.LEFT);
      this.setGraphic(null);
   }

   public final ObjectProperty<StringConverter<T>> converterProperty() {
      return this.converter;
   }

   public final void setConverter(StringConverter<T> var1) {
      this.converterProperty().set(var1);
   }

   public final StringConverter<T> getConverter() {
      return (StringConverter<T>)this.converterProperty().get();
   }

   public final ObjectProperty<Callback<T, ObservableValue<Boolean>>> selectedStateCallbackProperty() {
      return this.selectedStateCallback;
   }

   public final void setSelectedStateCallback(Callback<T, ObservableValue<Boolean>> var1) {
      this.selectedStateCallbackProperty().set(var1);
   }

   public final Callback<T, ObservableValue<Boolean>> getSelectedStateCallback() {
      return (Callback<T, ObservableValue<Boolean>>)this.selectedStateCallbackProperty().get();
   }

   @Override
   public void updateItem(T var1, boolean var2) {
      super.updateItem((T)var1, var2);
      if (!var2) {
         StringConverter var3 = this.getConverter();
         Callback var4 = this.getSelectedStateCallback();
         if (var4 == null) {
            throw new NullPointerException("The CheckBoxListCell selectedStateCallbackProperty can not be null");
         }

         this.setGraphic(this.checkBox);
         this.setText(var3 != null ? var3.toString(var1) : (var1 == null ? "" : var1.toString()));
         if (this.booleanProperty != null) {
            this.checkBox.selectedProperty().unbindBidirectional((BooleanProperty)this.booleanProperty);
         }

         this.booleanProperty = (ObservableValue<Boolean>)var4.call(var1);
         if (this.booleanProperty != null) {
            this.checkBox.selectedProperty().bindBidirectional((BooleanProperty)this.booleanProperty);
         }
      } else {
         this.setGraphic(null);
         this.setText(null);
      }
   }
}
