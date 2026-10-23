package com.cropster.roastingintelligence.ui.components.skin;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.text.TextAlignment;

public class TextFieldWithIconAndMessageSkin extends TextFieldSkin {
   private static final double DEFAULT_RIGHT_OFFSET = 0.0;
   private final Label messageLabel;
   private double rightOffset = 0.0;

   public TextFieldWithIconAndMessageSkin(TextField control) {
      this(control, null, null);
   }

   public TextFieldWithIconAndMessageSkin(TextField control, String message) {
      this(control, message, null);
   }

   public TextFieldWithIconAndMessageSkin(TextField control, String message, Node icon) {
      super(control);
      this.messageLabel = new Label();
      this.messageLabel.getStyleClass().add("text-field-message");
      this.messageLabel.setAlignment(Pos.CENTER_RIGHT);
      this.messageLabel.setTextAlignment(TextAlignment.RIGHT);
      this.messageLabel.setGraphicTextGap(4.0);
      this.messageLabel.setManaged(false);
      this.messageLabel.setMouseTransparent(true);
      this.getChildren().add(this.messageLabel);
      this.setMessage(message);
      this.setIcon(icon);
   }

   public final void setMessage(String message) {
      this.messageLabel.setText(message == null ? "" : message);
      this.updateContentDisplay();
      ((TextField)this.getSkinnable()).requestLayout();
   }

   public final String getMessage() {
      return this.messageLabel.getText();
   }

   public final void setIcon(Node icon) {
      this.messageLabel.setGraphic(icon);
      this.updateContentDisplay();
      ((TextField)this.getSkinnable()).requestLayout();
   }

   public final Node getIcon() {
      return this.messageLabel.getGraphic();
   }

   public final void setRightOffset(double offset) {
      this.rightOffset = offset;
      ((TextField)this.getSkinnable()).requestLayout();
   }

   public final double getRightOffset() {
      return this.rightOffset;
   }

   public final Label getMessageLabel() {
      return this.messageLabel;
   }

   private void updateContentDisplay() {
      boolean hasMessage = !this.messageLabel.getText().isEmpty();
      boolean hasIcon = this.messageLabel.getGraphic() != null;
      if (hasMessage && !hasIcon) {
         this.messageLabel.setContentDisplay(ContentDisplay.TEXT_ONLY);
      } else if (!hasMessage && hasIcon) {
         this.messageLabel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      } else {
         this.messageLabel.setContentDisplay(ContentDisplay.RIGHT);
      }
   }

   protected void layoutChildren(double x, double y, double w, double h) {
      super.layoutChildren(x, y, w, h);
      boolean hasMessage = !this.messageLabel.getText().isEmpty();
      boolean hasIcon = this.messageLabel.getGraphic() != null;
      if (hasMessage || hasIcon) {
         double prefLabelWidth = this.snapSizeX(this.messageLabel.prefWidth(-1.0));
         double prefLabelHeight = this.snapSizeY(this.messageLabel.prefHeight(-1.0));
         double labelWidth = Math.min(prefLabelWidth, Math.max(0.0, w - this.rightOffset));
         double labelHeight = Math.min(prefLabelHeight, Math.max(0.0, h));
         double desiredX = x + w - labelWidth - this.rightOffset;
         double maxX = x + w - labelWidth;
         double labelX = this.snapPositionX(Math.max(x, Math.min(desiredX, maxX)));
         double centeredY = y + (h - labelHeight) / 2.0;
         double maxY = y + h - labelHeight;
         double labelY = this.snapPositionY(Math.max(y, Math.min(centeredY, maxY)));
         this.messageLabel.resizeRelocate(labelX, labelY, labelWidth, labelHeight);
      }
   }
}
