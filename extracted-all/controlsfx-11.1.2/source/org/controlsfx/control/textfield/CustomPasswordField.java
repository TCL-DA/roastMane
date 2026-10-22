package org.controlsfx.control.textfield;

import impl.org.controlsfx.skin.CustomTextFieldSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Skin;

public class CustomPasswordField extends PasswordField {
   private ObjectProperty<Node> left = new SimpleObjectProperty(this, "left");
   private ObjectProperty<Node> right = new SimpleObjectProperty(this, "right");

   public CustomPasswordField() {
      this.getStyleClass().addAll(new String[]{"custom-text-field", "custom-password-field"});
   }

   public final ObjectProperty<Node> leftProperty() {
      return this.left;
   }

   public final Node getLeft() {
      return (Node)this.left.get();
   }

   public final void setLeft(Node value) {
      this.left.set(value);
   }

   public final ObjectProperty<Node> rightProperty() {
      return this.right;
   }

   public final Node getRight() {
      return (Node)this.right.get();
   }

   public final void setRight(Node value) {
      this.right.set(value);
   }

   protected Skin<?> createDefaultSkin() {
      return new CustomTextFieldSkin(this) {
         @Override
         public ObjectProperty<Node> leftProperty() {
            return CustomPasswordField.this.leftProperty();
         }

         @Override
         public ObjectProperty<Node> rightProperty() {
            return CustomPasswordField.this.rightProperty();
         }
      };
   }

   public String getUserAgentStylesheet() {
      return CustomTextField.class.getResource("customtextfield.css").toExternalForm();
   }
}
