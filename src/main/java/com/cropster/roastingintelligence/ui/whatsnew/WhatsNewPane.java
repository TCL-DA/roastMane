package com.cropster.roastingintelligence.ui.whatsnew;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.text.MessageFormat;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextFlow;

public class WhatsNewPane extends BorderPane {
   private static final String RECCOMENDATION_STYLE = "<span style=\"-fx-stroke: -fx-color-text-disabled; -fx-stroke-width: 0.222; \">{0}</span>";
   @FXML
   private Button actionButton;
   @FXML
   private Label bannerStatus;
   @FXML
   private Label bannerTitle;
   @FXML
   private BorderPane buttonsContainer;
   @FXML
   private Button closeButton;
   @FXML
   private TextFlow contentFlow;
   @FXML
   private CheckBox gotitCheckbox;
   @FXML
   private BorderPane headerContainer;
   @FXML
   private BorderPane imageContainer;
   @FXML
   private GridPane navigationButtonsContainer;
   @FXML
   private Button nextButton;
   @FXML
   private BorderPane notificationContainer;
   @FXML
   private Label notificationLabel;
   @FXML
   private Button previousButton;
   @FXML
   private TextFlow reccomendationTextFlow;
   @FXML
   private Label titleLabel;

   public WhatsNewPane() {
      JavaFxUtil.loadComponent(this);
      this.reccomendationTextFlow
         .getChildren()
         .addAll(
            JavaFxUtil.parseHtmlToTextNodes(
               MessageFormat.format(
                  "<span style=\"-fx-stroke: -fx-color-text-disabled; -fx-stroke-width: 0.222; \">{0}</span>",
                  org.cropster.roastingintelligence.util.Messages.m_("<b>Note:</b> You can access this window at any time via the “Help” menu")
               )
            )
         );
   }

   public Button getActionButton() {
      return this.actionButton;
   }

   public Label getBannerStatus() {
      return this.bannerStatus;
   }

   public Label getBannerTitle() {
      return this.bannerTitle;
   }

   public BorderPane getButtonsContainer() {
      return this.buttonsContainer;
   }

   public Button getCloseButton() {
      return this.closeButton;
   }

   public TextFlow getContentFlow() {
      return this.contentFlow;
   }

   public CheckBox getGotitCheckbox() {
      return this.gotitCheckbox;
   }

   public BorderPane getImageContainer() {
      return this.imageContainer;
   }

   public Button getNextButton() {
      return this.nextButton;
   }

   public BorderPane getNotificationContainer() {
      return this.notificationContainer;
   }

   public Label getNotificationLabel() {
      return this.notificationLabel;
   }

   public Button getPreviousButton() {
      return this.previousButton;
   }

   public TextFlow getReccomendationTextFlow() {
      return this.reccomendationTextFlow;
   }

   public Label getTitleLabel() {
      return this.titleLabel;
   }
}
