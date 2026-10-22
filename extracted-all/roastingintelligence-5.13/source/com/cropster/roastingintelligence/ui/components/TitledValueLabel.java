package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class TitledValueLabel extends BorderPane {
   @FXML
   private Label titleLabel;
   @FXML
   private Label valueLabel;

   public TitledValueLabel() {
      this("--", "--");
   }

   public TitledValueLabel(String title) {
      this(title, "--");
   }

   public TitledValueLabel(String title, String value) {
      JavaFxUtil.loadComponent(this);
      this.titleLabel.setText(title);
      this.valueLabel.setText(value);
   }

   public StringProperty titleProperty() {
      return this.titleLabel.textProperty();
   }

   public String getTitle() {
      return this.titleLabel.getText();
   }

   public void setTitle(String title) {
      this.titleLabel.setText(title);
   }

   public StringProperty valueProperty() {
      return this.valueLabel.textProperty();
   }

   public String getValue() {
      return this.valueLabel.getText();
   }

   public void setValue(String value) {
      this.valueLabel.setText(value);
   }
}
