package org.controlsfx.property.editor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.textfield.CustomTextField;

abstract class AbstractObjectField<T> extends HBox {
   private static final Image image = new Image(AbstractObjectField.class.getResource("/org/controlsfx/control/open-editor.png").toExternalForm());
   private final CustomTextField textField = new CustomTextField();
   private ObjectProperty<T> objectProperty = new SimpleObjectProperty();

   public AbstractObjectField() {
      super(1.0);
      this.textField.setEditable(false);
      this.textField.setFocusTraversable(false);
      StackPane button = new StackPane(new Node[]{new ImageView(image)});
      button.setCursor(Cursor.DEFAULT);
      button.setOnMouseReleased(e -> {
         if (MouseButton.PRIMARY == e.getButton()) {
            T result = this.edit((T)this.objectProperty.get());
            if (result != null) {
               this.objectProperty.set(result);
            }
         }
      });
      this.textField.setRight(button);
      this.getChildren().add(this.textField);
      HBox.setHgrow(this.textField, Priority.ALWAYS);
      this.objectProperty.addListener((o, oldValue, newValue) -> this.textProperty().set(this.objectToString((T)newValue)));
   }

   protected StringProperty textProperty() {
      return this.textField.textProperty();
   }

   public ObjectProperty<T> getObjectProperty() {
      return this.objectProperty;
   }

   protected String objectToString(T object) {
      return object == null ? "" : object.toString();
   }

   protected abstract Class<T> getType();

   protected abstract T edit(T var1);
}
