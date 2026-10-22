package javafx.scene.control.cell;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class ChoiceBoxListCell<T> extends ListCell<T> {
   private final ObservableList<T> items;
   private ChoiceBox<T> choiceBox;
   private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty(this, "converter");

   @SafeVarargs
   public static <T> Callback<ListView<T>, ListCell<T>> forListView(T... var0) {
      return forListView(FXCollections.observableArrayList(var0));
   }

   @SafeVarargs
   public static <T> Callback<ListView<T>, ListCell<T>> forListView(StringConverter<T> var0, T... var1) {
      return forListView(var0, FXCollections.observableArrayList(var1));
   }

   public static <T> Callback<ListView<T>, ListCell<T>> forListView(ObservableList<T> var0) {
      return forListView(null, var0);
   }

   public static <T> Callback<ListView<T>, ListCell<T>> forListView(StringConverter<T> var0, ObservableList<T> var1) {
      return var2 -> new ChoiceBoxListCell(var0, var1);
   }

   public ChoiceBoxListCell() {
      this(FXCollections.observableArrayList());
   }

   @SafeVarargs
   public ChoiceBoxListCell(T... var1) {
      this(FXCollections.observableArrayList(var1));
   }

   @SafeVarargs
   public ChoiceBoxListCell(StringConverter<T> var1, T... var2) {
      this(var1, FXCollections.observableArrayList(var2));
   }

   public ChoiceBoxListCell(ObservableList<T> var1) {
      this(null, var1);
   }

   public ChoiceBoxListCell(StringConverter<T> var1, ObservableList<T> var2) {
      this.getStyleClass().add("choice-box-list-cell");
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

   public ObservableList<T> getItems() {
      return this.items;
   }

   @Override
   public void startEdit() {
      if (this.isEditable() && this.getListView().isEditable()) {
         if (this.choiceBox == null) {
            this.choiceBox = CellUtils.createChoiceBox(this, this.items, this.converterProperty());
         }

         this.choiceBox.getSelectionModel().select(this.getItem());
         super.startEdit();
         if (this.isEditing()) {
            this.setText(null);
            this.setGraphic(this.choiceBox);
         }
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
      CellUtils.updateItem(this, this.getConverter(), null, null, this.choiceBox);
   }
}
