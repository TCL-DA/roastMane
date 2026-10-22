package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ButtonBarWindow extends Stage {
   private final BorderPane root = new BorderPane();
   private final HBox _buttonBar = new HBox();

   public ButtonBarWindow() {
      this.setScene(new Scene(this.root));
      this.getScene().getStylesheets().add(JavaFxController.STYLE_SHEET);
      this._buttonBar.getStyleClass().addAll(new String[]{"button-bar", "button-bar--reversable"});
      this._buttonBar.setAlignment(Pos.CENTER_RIGHT);
      this._buttonBar.setPadding(new Insets(10.0));
      this.root.setBottom(this._buttonBar);
   }

   public BorderPane getRoot() {
      return this.root;
   }

   public ButtonBarWindow withButton(String label, EventHandler<ActionEvent> handler) {
      Button button = new Button(label);
      if (this._buttonBar.getChildren().isEmpty()) {
         button.getStyleClass().add("button--primary");
      }

      button.setOnAction(handler);
      this._buttonBar.getChildren().add(button);
      return this;
   }

   public ButtonBarWindow withButton(String label, boolean isCancelButton, boolean isDefaultButton, EventHandler<ActionEvent> handler) {
      Button button = new Button(label);
      if (this._buttonBar.getChildren().isEmpty()) {
         button.getStyleClass().add("button--primary");
      }

      button.setOnAction(handler);
      button.setCancelButton(isCancelButton);
      button.setDefaultButton(isDefaultButton);
      this._buttonBar.getChildren().add(button);
      return this;
   }
}
