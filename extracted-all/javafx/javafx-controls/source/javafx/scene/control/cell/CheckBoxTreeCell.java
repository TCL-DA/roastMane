package javafx.scene.control.cell;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class CheckBoxTreeCell<T> extends DefaultTreeCell<T> {
   private final CheckBox checkBox;
   private ObservableValue<Boolean> booleanProperty;
   private BooleanProperty indeterminateProperty;
   private ObjectProperty<StringConverter<TreeItem<T>>> converter = new SimpleObjectProperty(this, "converter");
   private ObjectProperty<Callback<TreeItem<T>, ObservableValue<Boolean>>> selectedStateCallback = new SimpleObjectProperty(this, "selectedStateCallback");

   public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView() {
      Callback var0 = var0x -> var0x instanceof CheckBoxTreeItem ? ((CheckBoxTreeItem)var0x).selectedProperty() : null;
      return forTreeView(var0, CellUtils.defaultTreeItemStringConverter());
   }

   public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView(Callback<TreeItem<T>, ObservableValue<Boolean>> var0) {
      return forTreeView(var0, CellUtils.defaultTreeItemStringConverter());
   }

   public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView(Callback<TreeItem<T>, ObservableValue<Boolean>> var0, StringConverter<TreeItem<T>> var1) {
      return var2 -> new CheckBoxTreeCell(var0, var1);
   }

   public CheckBoxTreeCell() {
      this(var0 -> var0 instanceof CheckBoxTreeItem ? ((CheckBoxTreeItem)var0).selectedProperty() : null);
   }

   public CheckBoxTreeCell(Callback<TreeItem<T>, ObservableValue<Boolean>> var1) {
      this(var1, CellUtils.defaultTreeItemStringConverter(), null);
   }

   public CheckBoxTreeCell(Callback<TreeItem<T>, ObservableValue<Boolean>> var1, StringConverter<TreeItem<T>> var2) {
      this(var1, var2, null);
   }

   private CheckBoxTreeCell(
      Callback<TreeItem<T>, ObservableValue<Boolean>> var1, StringConverter<TreeItem<T>> var2, Callback<TreeItem<T>, ObservableValue<Boolean>> var3
   ) {
      this.getStyleClass().add("check-box-tree-cell");
      this.setSelectedStateCallback(var1);
      this.setConverter(var2);
      this.checkBox = new CheckBox();
      this.checkBox.setAllowIndeterminate(false);
      this.setGraphic(null);
   }

   public final ObjectProperty<StringConverter<TreeItem<T>>> converterProperty() {
      return this.converter;
   }

   public final void setConverter(StringConverter<TreeItem<T>> var1) {
      this.converterProperty().set(var1);
   }

   public final StringConverter<TreeItem<T>> getConverter() {
      return (StringConverter<TreeItem<T>>)this.converterProperty().get();
   }

   public final ObjectProperty<Callback<TreeItem<T>, ObservableValue<Boolean>>> selectedStateCallbackProperty() {
      return this.selectedStateCallback;
   }

   public final void setSelectedStateCallback(Callback<TreeItem<T>, ObservableValue<Boolean>> var1) {
      this.selectedStateCallbackProperty().set(var1);
   }

   public final Callback<TreeItem<T>, ObservableValue<Boolean>> getSelectedStateCallback() {
      return (Callback<TreeItem<T>, ObservableValue<Boolean>>)this.selectedStateCallbackProperty().get();
   }

   @Override
   public void updateItem(T var1, boolean var2) {
      super.updateItem((T)var1, var2);
      if (var2) {
         this.setText(null);
         this.setGraphic(null);
      } else {
         StringConverter var3 = this.getConverter();
         TreeItem var4 = this.getTreeItem();
         this.setText(var3 != null ? var3.toString(var4) : (var4 == null ? "" : var4.toString()));
         this.checkBox.setGraphic(var4 == null ? null : var4.getGraphic());
         this.setGraphic(this.checkBox);
         if (this.booleanProperty != null) {
            this.checkBox.selectedProperty().unbindBidirectional((BooleanProperty)this.booleanProperty);
         }

         if (this.indeterminateProperty != null) {
            this.checkBox.indeterminateProperty().unbindBidirectional(this.indeterminateProperty);
         }

         if (var4 instanceof CheckBoxTreeItem) {
            CheckBoxTreeItem var5 = (CheckBoxTreeItem)var4;
            this.booleanProperty = var5.selectedProperty();
            this.checkBox.selectedProperty().bindBidirectional((BooleanProperty)this.booleanProperty);
            this.indeterminateProperty = var5.indeterminateProperty();
            this.checkBox.indeterminateProperty().bindBidirectional(this.indeterminateProperty);
         } else {
            Callback var6 = this.getSelectedStateCallback();
            if (var6 == null) {
               throw new NullPointerException("The CheckBoxTreeCell selectedStateCallbackProperty can not be null");
            }

            this.booleanProperty = (ObservableValue<Boolean>)var6.call(var4);
            if (this.booleanProperty != null) {
               this.checkBox.selectedProperty().bindBidirectional((BooleanProperty)this.booleanProperty);
            }
         }
      }
   }

   @Override
   void updateDisplay(T var1, boolean var2) {
   }
}
