package com.cropster.roastingintelligence.ui.components;

import javafx.beans.NamedArg;
import javafx.scene.control.Label;

public class DragSourceLabel extends Label {
   private final RoastLabel.RoastLabelComponent component;

   public DragSourceLabel(@NamedArg("component") RoastLabel.RoastLabelComponent component) {
      this.getStyleClass().add("dragable-label");
      this.setText(component.getLabel());
      this.component = component;
   }

   public RoastLabel.RoastLabelComponent getComponent() {
      return this.component;
   }
}
