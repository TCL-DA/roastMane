package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class SliderButton extends HBox {
   @FXML
   private Button button;
   private final int value;

   public SliderButton(int value) {
      JavaFxUtil.loadComponent(this);
      this.value = value;
   }

   public Button getButton() {
      return this.button;
   }

   public int getValue() {
      return this.value;
   }
}
