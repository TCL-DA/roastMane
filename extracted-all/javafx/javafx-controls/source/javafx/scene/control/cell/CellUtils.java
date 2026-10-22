package javafx.scene.control.cell;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

class CellUtils {
   static int TREE_VIEW_HBOX_GRAPHIC_PADDING = 3;
   private static final StringConverter<?> defaultStringConverter = new StringConverter<Object>() {
      public String toString(Object var1) {
         return var1 == null ? null : var1.toString();
      }

      public Object fromString(String var1) {
         return var1;
      }
   };
   private static final StringConverter<?> defaultTreeItemStringConverter = new StringConverter<TreeItem<?>>() {
      public String toString(TreeItem<?> var1) {
         return var1 != null && var1.getValue() != null ? var1.getValue().toString() : "";
      }

      public TreeItem<?> fromString(String var1) {
         return new TreeItem<>(var1);
      }
   };

   static <T> StringConverter<T> defaultStringConverter() {
      return (StringConverter<T>)defaultStringConverter;
   }

   static <T> StringConverter<TreeItem<T>> defaultTreeItemStringConverter() {
      return (StringConverter<TreeItem<T>>)defaultTreeItemStringConverter;
   }

   private static <T> String getItemText(Cell<T> var0, StringConverter<T> var1) {
      return var1 == null ? (var0.getItem() == null ? "" : var0.getItem().toString()) : var1.toString(var0.getItem());
   }

   static Node getGraphic(TreeItem<?> var0) {
      return var0 == null ? null : var0.getGraphic();
   }

   static <T> void updateItem(Cell<T> var0, StringConverter<T> var1, ChoiceBox<T> var2) {
      updateItem(var0, var1, null, null, var2);
   }

   static <T> void updateItem(Cell<T> var0, StringConverter<T> var1, HBox var2, Node var3, ChoiceBox<T> var4) {
      if (var0.isEmpty()) {
         var0.setText(null);
         var0.setGraphic(null);
      } else if (var0.isEditing()) {
         if (var4 != null) {
            var4.getSelectionModel().select(var0.getItem());
         }

         var0.setText(null);
         if (var3 != null) {
            var2.getChildren().setAll(new Node[]{var3, var4});
            var0.setGraphic(var2);
         } else {
            var0.setGraphic(var4);
         }
      } else {
         var0.setText(getItemText(var0, var1));
         var0.setGraphic(var3);
      }
   }

   static <T> ChoiceBox<T> createChoiceBox(Cell<T> var0, ObservableList<T> var1, ObjectProperty<StringConverter<T>> var2) {
      ChoiceBox var3 = new ChoiceBox(var1);
      var3.setMaxWidth(Double.MAX_VALUE);
      var3.converterProperty().bind(var2);
      var3.showingProperty().addListener(var2x -> {
         if (!var3.isShowing()) {
            var0.commitEdit(var3.getSelectionModel().getSelectedItem());
         }
      });
      return var3;
   }

   static <T> void updateItem(Cell<T> var0, StringConverter<T> var1, TextField var2) {
      updateItem(var0, var1, null, null, var2);
   }

   static <T> void updateItem(Cell<T> var0, StringConverter<T> var1, HBox var2, Node var3, TextField var4) {
      if (var0.isEmpty()) {
         var0.setText(null);
         var0.setGraphic(null);
      } else if (var0.isEditing()) {
         if (var4 != null) {
            var4.setText(getItemText(var0, var1));
         }

         var0.setText(null);
         if (var3 != null) {
            var2.getChildren().setAll(new Node[]{var3, var4});
            var0.setGraphic(var2);
         } else {
            var0.setGraphic(var4);
         }
      } else {
         var0.setText(getItemText(var0, var1));
         var0.setGraphic(var3);
      }
   }

   static <T> void startEdit(Cell<T> var0, StringConverter<T> var1, HBox var2, Node var3, TextField var4) {
      if (var4 != null) {
         var4.setText(getItemText(var0, var1));
      }

      var0.setText(null);
      if (var3 != null) {
         var2.getChildren().setAll(new Node[]{var3, var4});
         var0.setGraphic(var2);
      } else {
         var0.setGraphic(var4);
      }

      var4.selectAll();
      var4.requestFocus();
   }

   static <T> void cancelEdit(Cell<T> var0, StringConverter<T> var1, Node var2) {
      var0.setText(getItemText(var0, var1));
      var0.setGraphic(var2);
   }

   static <T> TextField createTextField(Cell<T> var0, StringConverter<T> var1) {
      TextField var2 = new TextField(getItemText(var0, var1));
      var2.setOnAction(
         var3 -> {
            if (var1 == null) {
               throw new IllegalStateException(
                  "Attempting to convert text input into Object, but provided StringConverter is null. Be sure to set a StringConverter in your cell factory."
               );
            } else {
               var0.commitEdit(var1.fromString(var2.getText()));
               var3.consume();
            }
         }
      );
      var2.setOnKeyReleased(var1x -> {
         if (var1x.getCode() == KeyCode.ESCAPE) {
            var0.cancelEdit();
            var1x.consume();
         }
      });
      return var2;
   }

   static <T> void updateItem(Cell<T> var0, StringConverter<T> var1, ComboBox<T> var2) {
      updateItem(var0, var1, null, null, var2);
   }

   static <T> void updateItem(Cell<T> var0, StringConverter<T> var1, HBox var2, Node var3, ComboBox<T> var4) {
      if (var0.isEmpty()) {
         var0.setText(null);
         var0.setGraphic(null);
      } else if (var0.isEditing()) {
         if (var4 != null) {
            var4.getSelectionModel().select(var0.getItem());
         }

         var0.setText(null);
         if (var3 != null) {
            var2.getChildren().setAll(new Node[]{var3, var4});
            var0.setGraphic(var2);
         } else {
            var0.setGraphic(var4);
         }
      } else {
         var0.setText(getItemText(var0, var1));
         var0.setGraphic(var3);
      }
   }

   static <T> ComboBox<T> createComboBox(final Cell<T> var0, ObservableList<T> var1, ObjectProperty<StringConverter<T>> var2) {
      final ComboBox var3 = new ComboBox(var1);
      var3.converterProperty().bind(var2);
      var3.setMaxWidth(Double.MAX_VALUE);
      var3.addEventFilter(KeyEvent.KEY_RELEASED, var2x -> {
         if (var2x.getCode() == KeyCode.ENTER) {
            tryComboBoxCommit(var3, var0);
         } else if (var2x.getCode() == KeyCode.ESCAPE) {
            var0.cancelEdit();
         }
      });
      var3.getEditor().focusedProperty().addListener(var2x -> {
         if (!var3.isFocused()) {
            tryComboBoxCommit(var3, var0);
         }
      });
      boolean var4 = listenToComboBoxSkin(var3, var0);
      if (!var4) {
         var3.skinProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable var1) {
               boolean var2 = CellUtils.listenToComboBoxSkin(var3, var0);
               if (var2) {
                  var3.skinProperty().removeListener(this);
               }
            }
         });
      }

      return var3;
   }

   private static <T> void tryComboBoxCommit(ComboBox<T> var0, Cell<T> var1) {
      StringConverter var2 = var0.getConverter();
      if (var0.isEditable() && var2 != null) {
         Object var3 = var2.fromString(var0.getEditor().getText());
         var1.commitEdit(var3);
      } else {
         var1.commitEdit(var0.getValue());
      }
   }

   private static <T> boolean listenToComboBoxSkin(ComboBox<T> var0, Cell<T> var1) {
      Skin var2 = var0.getSkin();
      if (var2 != null && var2 instanceof ComboBoxListViewSkin) {
         ComboBoxListViewSkin var3 = (ComboBoxListViewSkin)var2;
         Node var4 = var3.getPopupContent();
         if (var4 != null && var4 instanceof ListView) {
            var4.addEventHandler(MouseEvent.MOUSE_RELEASED, var2x -> var1.commitEdit(var0.getValue()));
            return true;
         }
      }

      return false;
   }
}
