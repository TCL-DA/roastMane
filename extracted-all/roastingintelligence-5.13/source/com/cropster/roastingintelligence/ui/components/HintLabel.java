package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.Icon;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class HintLabel extends Label {
   public HintLabel() {
      this.setGraphic(Icon.INFO.load());
      this.setAlignment(Pos.CENTER_LEFT);
      this.setMaxWidth(Double.POSITIVE_INFINITY);
      this.getStyleClass().add("hint-label");
      Tooltip tooltip = new Tooltip();
      tooltip.setMaxWidth(330.0);
      tooltip.setWrapText(true);
      tooltip.setShowDuration(Duration.seconds(30.0));
      JavaFxUtil.configureTooltip(tooltip);
      this.setTooltip(tooltip);
   }

   public HintLabel(String text) {
      super(text);
      this.setGraphic(Icon.INFO.load());
      this.setAlignment(Pos.CENTER_LEFT);
      this.setMaxWidth(Double.POSITIVE_INFINITY);
      this.getStyleClass().add("hint-label");
      Tooltip tooltip = new Tooltip();
      tooltip.setMaxWidth(330.0);
      tooltip.setWrapText(true);
      tooltip.setShowDuration(Duration.seconds(30.0));
      JavaFxUtil.configureTooltip(tooltip);
      this.setTooltip(tooltip);
   }
}
