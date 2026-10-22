package com.cropster.roastingintelligence.ui.components;

import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class DragAndDropPlaceholder extends VBox {
   public DragAndDropPlaceholder() {
      Region upperSpacer = new Region();
      upperSpacer.getStyleClass().add("channel-configuration-panel__placeholder-spacer");
      this.getChildren().add(upperSpacer);
      Region placeholder = new Region();
      placeholder.getStyleClass().add("channel-configuration-panel__placeholder");
      this.getChildren().add(placeholder);
      Region lowerSpacer = new Region();
      lowerSpacer.getStyleClass().add("channel-configuration-panel__placeholder-spacer");
      this.getChildren().add(lowerSpacer);
   }
}
