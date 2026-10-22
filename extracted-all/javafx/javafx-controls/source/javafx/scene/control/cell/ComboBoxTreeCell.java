package javafx.scene.control.cell;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class ComboBoxTreeCell<T> extends DefaultTreeCell<T> {
   private final ObservableList<T> items;
   private ComboBox<T> comboBox;
   private HBox hbox;
   private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty(this, "converter");
   private BooleanProperty comboBoxEditable = new SimpleBooleanProperty(this, "comboBoxEditable");

   @SafeVarargs
   public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView(T... var0) {
      return forTreeView(FXCollections.observableArrayList(var0));
   }

   public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView(ObservableList<T> var0) {
      return forTreeView(null, var0);
   }

   @SafeVarargs
   public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView(StringConverter<T> var0, T... var1) {
      return forTreeView(var0, FXCollections.observableArrayList(var1));
   }

   public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView(StringConverter<T> var0, ObservableList<T> var1) {
      return var2 -> new ComboBoxTreeCell(var0, var1);
   }

   public ComboBoxTreeCell() {
      this(FXCollections.observableArrayList());
   }

   @SafeVarargs
   public ComboBoxTreeCell(T... var1) {
      this(FXCollections.observableArrayList(var1));
   }

   @SafeVarargs
   public ComboBoxTreeCell(StringConverter<T> var1, T... var2) {
      this(var1, FXCollections.observableArrayList(var2));
   }

   public ComboBoxTreeCell(ObservableList<T> var1) {
      this(null, var1);
   }

   public ComboBoxTreeCell(StringConverter<T> var1, ObservableList<T> var2) {
      this.getStyleClass().add("combo-box-tree-cell");
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
      if (this.isEditable() && this.getTreeView().isEditable()) {
         TreeItem var1 = this.getTreeItem();
         if (var1 != null) {
            if (this.comboBox == null) {
               this.comboBox = CellUtils.createComboBox(this, this.items, this.converterProperty());
               this.comboBox.editableProperty().bind(this.comboBoxEditableProperty());
            }

            if (this.hbox == null) {
               this.hbox = new HBox((double)CellUtils.TREE_VIEW_HBOX_GRAPHIC_PADDING);
            }

            this.comboBox.getSelectionModel().select((T)var1.getValue());
            super.startEdit();
            if (this.isEditing()) {
               this.setText(null);
               Node var2 = CellUtils.getGraphic(var1);
               if (var2 != null) {
                  this.hbox.getChildren().setAll(new Node[]{var2, this.comboBox});
                  this.setGraphic(this.hbox);
               } else {
                  this.setGraphic(this.comboBox);
               }
            }
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
      Node var3 = CellUtils.getGraphic(this.getTreeItem());
      CellUtils.updateItem(this, this.getConverter(), this.hbox, var3, this.comboBox);
   }
}
