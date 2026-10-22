package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class FlickPredictionPanel extends HBox {
   @FXML
   private VBox flickPredictionMessage;
   @FXML
   private Button dismissButton;
   @FXML
   private VBox flickParagraph;
   @FXML
   private Label flickTendencyLabel;
   @FXML
   private Label flickTendencyIcon;
   @FXML
   private Region rightPadding;
   @FXML
   private Region topPadding;

   public FlickPredictionPanel() {
      JavaFxUtil.loadComponent(this);
   }

   public Button getDismissButton() {
      return this.dismissButton;
   }

   public VBox getFlickParagraph() {
      return this.flickParagraph;
   }

   public Label getIconLabel() {
      return this.flickTendencyIcon;
   }

   public VBox getFlickPredictionMessage() {
      return this.flickPredictionMessage;
   }

   public Label getFlickTendencyLabel() {
      return this.flickTendencyLabel;
   }

   public Label getFlickTendencyIcon() {
      return this.flickTendencyIcon;
   }

   public Region getRightPadding() {
      return this.rightPadding;
   }

   public Region getTopPadding() {
      return this.topPadding;
   }
}
