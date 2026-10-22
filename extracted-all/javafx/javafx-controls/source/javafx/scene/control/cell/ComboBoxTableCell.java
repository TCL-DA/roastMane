package javafx.scene.control.cell;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class ComboBoxTableCell<S, T> extends TableCell<S, T> {
   private final ObservableList<T> items;
   private ComboBox<T> comboBox;
   private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty(this, "converter");
   private BooleanProperty comboBoxEditable = new SimpleBooleanProperty(this, "comboBoxEditable");

   @SafeVarargs
   public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(T... var0) {
      return forTableColumn(null, (T[])var0);
   }

   @SafeVarargs
   public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(StringConverter<T> var0, T... var1) {
      return forTableColumn(var0, FXCollections.observableArrayList(var1));
   }

   public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(ObservableList<T> var0) {
      return forTableColumn(null, var0);
   }

   public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(StringConverter<T> var0, ObservableList<T> var1) {
      return var2 -> new ComboBoxTableCell(var0, var1);
   }

   public ComboBoxTableCell() {
      this(FXCollections.observableArrayList());
   }

   @SafeVarargs
   public ComboBoxTableCell(T... var1) {
      this(FXCollections.observableArrayList(var1));
   }

   @SafeVarargs
   public ComboBoxTableCell(StringConverter<T> var1, T... var2) {
      this(var1, FXCollections.observableArrayList(var2));
   }

   public ComboBoxTableCell(ObservableList<T> var1) {
      this(null, var1);
   }

   public ComboBoxTableCell(StringConverter<T> var1, ObservableList<T> var2) {
      this.getStyleClass().add("combo-box-table-cell");
      this.items = var2;
      this.setConverter(var1 != null ? var1 : CellUtils.defaultStringConverter());
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

   public final BooleanProperty comboBoxEditableProperty() {
      return this.comboBoxEditable;
   }

   public final void setComboBoxEditable(boolean var1) {
      this.comboBoxEditableProperty().set(var1);
   }

   public final boolean isComboBoxEditable() {
      return this.comboBoxEditableProperty().get();
   }

   public ObservableList<T> getItems() {
      return this.items;
   }

   @Override
   public void startEdit() {
      if (this.isEditable() && this.getTableView().isEditable() && this.getTableColumn().isEditable()) {
         if (this.comboBox == null) {
            this.comboBox = CellUtils.createComboBox(this, this.items, this.converterProperty());
            this.comboBox.editableProperty().bind(this.comboBoxEditableProperty());
         }

         this.comboBox.getSelectionModel().select(this.getItem());
         super.startEdit();
         this.setText(null);
         this.setGraphic(this.comboBox);
      }
   }

   @Override
   public void cancelEdit() {
      super.cancelEdit();
      this.setText(this.getConverter().toString(this.getItem()));
      this.setGraphic(null);
   }

   @Override
   public void updateItem(T var1, boolean var2) {
      super.updateItem((T)var1, var2);
      CellUtils.updateItem(this, this.getConverter(), null, null, this.comboBox);
   }
}
