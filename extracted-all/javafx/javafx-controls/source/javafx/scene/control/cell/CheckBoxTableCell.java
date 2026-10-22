package javafx.scene.control.cell;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class CheckBoxTableCell<S, T> extends TableCell<S, T> {
   private final CheckBox checkBox;
   private boolean showLabel;
   private ObservableValue<Boolean> booleanProperty;
   private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<StringConverter<T>>(this, "converter") {
      protected void invalidated() {
         CheckBoxTableCell.this.updateShowLabel();
      }
   };
   private ObjectProperty<Callback<Integer, ObservableValue<Boolean>>> selectedStateCallback = new SimpleObjectProperty(this, "selectedStateCallback");

   public static <S> Callback<TableColumn<S, Boolean>, TableCell<S, Boolean>> forTableColumn(TableColumn<S, Boolean> var0) {
      return forTableColumn(null, null);
   }

   public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(Callback<Integer, ObservableValue<Boolean>> var0) {
      return forTableColumn(var0, null);
   }

   public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(Callback<Integer, ObservableValue<Boolean>> var0, boolean var1) {
      StringConverter var2 = !var1 ? null : CellUtils.defaultStringConverter();
      return forTableColumn(var0, var2);
   }

   public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(Callback<Integer, ObservableValue<Boolean>> var0, StringConverter<T> var1) {
      return var2 -> new CheckBoxTableCell(var0, var1);
   }

   public CheckBoxTableCell() {
      this(null, null);
   }

   public CheckBoxTableCell(Callback<Integer, ObservableValue<Boolean>> var1) {
      this(var1, null);
   }

   public CheckBoxTableCell(Callback<Integer, ObservableValue<Boolean>> var1, StringConverter<T> var2) {
      this.getStyleClass().add("check-box-table-cell");
      this.checkBox = new CheckBox();
      this.setGraphic(null);
      this.setSelectedStateCallback(var1);
      this.setConverter(var2);
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

   public final ObjectProperty<Callback<Integer, ObservableValue<Boolean>>> selectedStateCallbackProperty() {
      return this.selectedStateCallback;
   }

   public final void setSelectedStateCallback(Callback<Integer, ObservableValue<Boolean>> var1) {
      this.selectedStateCallbackProperty().set(var1);
   }

   public final Callback<Integer, ObservableValue<Boolean>> getSelectedStateCallback() {
      return (Callback<Integer, ObservableValue<Boolean>>)this.selectedStateCallbackProperty().get();
   }

   @Override
   public void updateItem(T var1, boolean var2) {
      super.updateItem((T)var1, var2);
      if (var2) {
         this.setText(null);
         this.setGraphic(null);
      } else {
         StringConverter var3 = this.getConverter();
         if (this.showLabel) {
            this.setText(var3.toString(var1));
         }

         this.setGraphic(this.checkBox);
         if (this.booleanProperty instanceof BooleanProperty) {
            this.checkBox.selectedProperty().unbindBidirectional((BooleanProperty)this.booleanProperty);
         }

         ObservableValue var4 = this.getSelectedProperty();
         if (var4 instanceof BooleanProperty) {
            this.booleanProperty = var4;
            this.checkBox.selectedProperty().bindBidirectional((BooleanProperty)this.booleanProperty);
         }

         this.checkBox
            .disableProperty()
            .bind(Bindings.not(this.getTableView().editableProperty().and(this.getTableColumn().editableProperty()).and(this.editableProperty())));
      }
   }

   private void updateShowLabel() {
      this.showLabel = this.converter != null;
      this.checkBox.setAlignment(this.showLabel ? Pos.CENTER_LEFT : Pos.CENTER);
   }

   private ObservableValue<?> getSelectedProperty() {
      return this.getSelectedStateCallback() != null
         ? (ObservableValue)this.getSelectedStateCallback().call(this.getIndex())
         : this.getTableColumn().getCellObservableValue(this.getIndex());
   }
}
