package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.util.Icon;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class DragTargetLabel extends HBox {
   private final Label nameLabel;
   private final StringProperty name = new SimpleStringProperty();
   private final StringProperty alias = new SimpleStringProperty();
   private final StringProperty text;
   private final ObjectProperty<EventHandler<ActionEvent>> closeAction;

   public DragTargetLabel() {
      this.setAlignment(Pos.CENTER);
      this.getStyleClass().add("dragtarget");
      this.getStyleClass().add("dragtarget-label");
      this.nameLabel = new Label();
      this.nameLabel.setMinWidth(Double.NEGATIVE_INFINITY);
      this.nameLabel.getStyleClass().add("dragtarget-label__name");
      this.getChildren().add(this.nameLabel);
      Label textLabel = new Label();
      textLabel.getStyleClass().add("dragtarget-label__text");
      this.text = textLabel.textProperty();
      this.getChildren().add(textLabel);
      Label spacer = new Label();
      spacer.setMaxWidth(Double.MAX_VALUE);
      spacer.managedProperty().bind(spacer.visibleProperty());
      spacer.visibleProperty().bind(this.text.isEmpty().not());
      HBox.setHgrow(spacer, Priority.ALWAYS);
      this.getChildren().add(spacer);
      Button closeButton = new Button();
      closeButton.setGraphic(Icon.CLOSE.load("icon--link"));
      closeButton.getStyleClass().add("button--transparent");
      closeButton.managedProperty().bind(closeButton.visibleProperty());
      closeButton.visibleProperty().bind(this.text.isEmpty().not());
      this.closeAction = closeButton.onActionProperty();
      this.getChildren().add(closeButton);
      this.text.isEmpty().addListener((s, o, n) -> this.setAlignment(n ? Pos.CENTER : Pos.CENTER_LEFT));
      this.name.addListener((s, o, n) -> {
         if (this.alias.get() == null) {
            this.nameLabel.textProperty().bind(this.name);
         }
      });
      this.alias.addListener((s, o, n) -> {
         if (n != null) {
            this.nameLabel.textProperty().bind(this.alias);
         } else {
            this.nameLabel.textProperty().bind(this.name);
         }
      });
   }

   public StringProperty nameProperty() {
      return this.name;
   }

   public String getName() {
      return (String)this.name.get();
   }

   public void setName(String name) {
      this.name.set(name);
   }

   public StringProperty aliasProperty() {
      return this.name;
   }

   public String getAlias() {
      return (String)this.alias.get();
   }

   public void setAlias(String alias) {
      this.alias.set(alias);
   }

   public StringProperty textPropery() {
      return this.text;
   }

   public String getText() {
      return (String)this.text.get();
   }

   public void setText(String text) {
      this.text.set(text);
   }

   public ObjectProperty<EventHandler<ActionEvent>> onCloseActionProperty() {
      return this.closeAction;
   }

   public EventHandler<ActionEvent> getOnCloseAction() {
      return (EventHandler<ActionEvent>)this.closeAction.get();
   }

   public void setOnCloseAction(EventHandler<ActionEvent> closeAction) {
      this.closeAction.set(closeAction);
   }

   public void reset() {
      if (!this.getStyleClass().contains("dragtarget-label--empty")) {
         this.getStyleClass().add("dragtarget-label--empty");
      }

      this.setText(null);
   }
}
