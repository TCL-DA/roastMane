package javafx.scene.control.cell;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class TextFieldTableCell<S, T> extends TableCell<S, T> {
   private TextField textField;
   private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty(this, "converter");

   public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
      return forTableColumn(new DefaultStringConverter());
   }

   public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(StringConverter<T> var0) {
      return var1 -> new TextFieldTableCell(var0);
   }

   public TextFieldTableCell() {
      this(null);
   }

   public TextFieldTableCell(StringConverter<T> var1) {
      this.getStyleClass().add("text-field-table-cell");
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
      if (this.isEditable() && this.getTableView().isEditable() && this.getTableColumn().isEditable()) {
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
