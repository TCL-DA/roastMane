package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class PredictionPanel extends HBox implements Initializable {
   @FXML
   private ToggleSwitch beanButton;
   @FXML
   private BorderPane beanPanel;
   @FXML
   private ToggleSwitch firstCrackButton;
   @FXML
   private BorderPane firstCrackPanel;
   @FXML
   private ToggleSwitch flickButton;
   @FXML
   private BorderPane flickPanel;

   public PredictionPanel() {
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.managedProperty().bind(this.visibleProperty());
      this.visibleProperty()
         .bind(this.beanPredictionVisibleProperty().or(this.firstCrackPredictionVisibleProperty()).or(this.flickPredictionVisibleProperty()));
      this.beanPredictionVisibleProperty().addListener((s, o, n) -> {
         if (n && !this.getChildren().contains(this.beanPanel)) {
            this.getChildren().add(1, this.beanPanel);
         } else if (!n) {
            this.getChildren().remove(this.beanPanel);
         }
      });
   }

   public BooleanProperty beanPredictionDisableProperty() {
      return this.beanButton.disableProperty();
   }

   public BooleanProperty beanPredictionOnProperty() {
      return this.beanButton.onProperty();
   }

   public BooleanProperty beanPredictionVisibleProperty() {
      return this.beanPanel.visibleProperty();
   }

   public BooleanProperty firstCrackPredictionDisableProperty() {
      return this.firstCrackButton.disableProperty();
   }

   public BooleanProperty firstCrackPredictionOnProperty() {
      return this.firstCrackButton.onProperty();
   }

   public BooleanProperty firstCrackPredictionVisibleProperty() {
      return this.firstCrackPanel.visibleProperty();
   }

   public BooleanProperty flickPredictionDisableProperty() {
      return this.flickButton.disableProperty();
   }

   public BooleanProperty flickPredictionOnProperty() {
      return this.flickButton.onProperty();
   }

   public BooleanProperty flickPredictionVisibleProperty() {
      return this.flickPanel.visibleProperty();
   }
}
