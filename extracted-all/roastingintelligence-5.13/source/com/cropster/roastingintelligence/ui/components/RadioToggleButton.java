package com.cropster.roastingintelligence.ui.components;

import javafx.scene.control.ToggleButton;

public class RadioToggleButton extends ToggleButton {
   public void fire() {
      if (this.getToggleGroup() == null || !this.isSelected()) {
         super.fire();
      }
   }
}
