package javafx.scene.control.cell;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class TextFieldListCell<T> extends ListCell<T> {
   private TextField textField;
   private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty(this, "converter");

   public static Callback<ListView<String>, ListCell<String>> forListView() {
      return forListView(new DefaultStringConverter());
   }

   public static <T> Callback<ListView<T>, ListCell<T>> forListView(StringConverter<T> var0) {
      return var1 -> new TextFieldListCell(var0);
   }

   public TextFieldListCell() {
      this(null);
   }

   public TextFieldListCell(StringConverter<T> var1) {
      this.getStyleClass().add("text-field-list-cell");
      this.setConverter(var1);
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

   @Override
   public void startEdit() {
      if (this.isEditable() && this.getListView().isEditable()) {
         super.startEdit();
         if (this.isEditing()) {
            if (this.textField == null) {
               this.textField = CellUtils.createTextField(this, this.getConverter());
            }

            CellUtils.startEdit(this, this.getConverter(), null, null, this.textField);
         }
      }
   }

   @Override
   public void cancelEdit() {
      super.cancelEdit();
      CellUtils.cancelEdit(this, this.getConverter(), null);
   }

   @Override
   public void updateItem(T var1, boolean var2) {
      super.updateItem((T)var1, var2);
      CellUtils.updateItem(this, this.getConverter(), null, null, this.textField);
   }
}
