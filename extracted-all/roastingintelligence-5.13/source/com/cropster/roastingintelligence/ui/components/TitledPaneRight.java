package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.components.skin.TitledPaneRightSkin;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.TitledPane;

public class TitledPaneRight extends TitledPane {
   public TitledPaneRight() {
   }

   public TitledPaneRight(String title, Node content) {
      super(title, content);
   }

   public double computeMaxHeight() {
      return this.computeMaxHeight(this.getWidth());
   }

   protected Skin<?> createDefaultSkin() {
      return new TitledPaneRightSkin(this);
   }
}
