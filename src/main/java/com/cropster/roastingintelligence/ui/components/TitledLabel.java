package com.cropster.roastingintelligence.ui.components;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

public class TitledLabel extends AbstractTitled {
   private final StringProperty text;

   public TitledLabel() {
      this.getStyleClass().add("titled-label");
      Label textLabel = new Label();
      textLabel.getStyleClass().add("text");
      this.getChildren().add(textLabel);
      this.text = textLabel.textProperty();
   }

   public StringProperty textProperty() {
      return this.text;
   }

   public String getText() {
      return (String)this.text.get();
   }

   public void setText(String text) {
      this.text.set(text);
   }
}
