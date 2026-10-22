package javafx.scene.control.cell;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class TextFieldTreeCell<T> extends DefaultTreeCell<T> {
   private TextField textField;
   private HBox hbox;
   private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty(this, "converter");

   public static Callback<TreeView<String>, TreeCell<String>> forTreeView() {
      return forTreeView(new DefaultStringConverter());
   }

   public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView(StringConverter<T> var0) {
      return var1 -> new TextFieldTreeCell(var0);
   }

   public TextFieldTreeCell() {
      this(null);
   }

   public TextFieldTreeCell(StringConverter<T> var1) {
      this.getStyleClass().add("text-field-tree-cell");
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
      if (this.isEditable() && this.getTreeView().isEditable()) {
         super.startEdit();
         if (this.isEditing()) {
            StringConverter var1 = this.getConverter();
            if (this.textField == null) {
               this.textField = CellUtils.createTextField(this, var1);
            }

            if (this.hbox == null) {
               this.hbox = new HBox((double)CellUtils.TREE_VIEW_HBOX_GRAPHIC_PADDING);
            }

            CellUtils.startEdit(this, var1, this.hbox, this.getTreeItemGraphic(), this.textField);
         }
      }
   }

   @Override
   public void cancelEdit() {
      super.cancelEdit();
      CellUtils.cancelEdit(this, this.getConverter(), this.getTreeItemGraphic());
   }

   @Override
   public void updateItem(T var1, boolean var2) {
      super.updateItem((T)var1, var2);
      CellUtils.updateItem(this, this.getConverter(), this.hbox, this.getTreeItemGraphic(), this.textField);
   }

   private Node getTreeItemGraphic() {
      TreeItem var1 = this.getTreeItem();
      return var1 == null ? null : var1.getGraphic();
   }
}
