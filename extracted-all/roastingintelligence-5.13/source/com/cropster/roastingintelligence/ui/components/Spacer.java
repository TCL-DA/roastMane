package com.cropster.roastingintelligence.ui.components;

import javafx.scene.layout.Region;

public class Spacer extends Region {
   public Spacer() {
      this.getStyleClass().add("spacer");
      this.managedProperty().bind(this.visibleProperty());
   }
}
