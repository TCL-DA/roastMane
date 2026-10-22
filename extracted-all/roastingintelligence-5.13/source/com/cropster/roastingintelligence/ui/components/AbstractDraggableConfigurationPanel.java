package com.cropster.roastingintelligence.ui.components;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public abstract class AbstractDraggableConfigurationPanel extends GridPane implements Initializable {
   public abstract Node getDragRegion();

   public abstract void reset();

   public abstract BooleanProperty expandedProperty();

   public boolean isExpanded() {
      return this.expandedProperty().get();
   }

   public void setExpanded(boolean expanded) {
      this.expandedProperty().set(expanded);
   }
}
