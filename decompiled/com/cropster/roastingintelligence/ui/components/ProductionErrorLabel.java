package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class ProductionErrorLabel extends BorderPane {
   @FXML
   private Label messageLabel;

   public ProductionErrorLabel(String text) {
      JavaFxUtil.loadComponent(this);
      this.setText(text);
   }

   public StringProperty textProperty() {
      return this.messageLabel.textProperty();
   }

   public String getText() {
      return this.messageLabel.getText();
   }

   public void setText(String text) {
      this.messageLabel.setText(text);
   }
}
