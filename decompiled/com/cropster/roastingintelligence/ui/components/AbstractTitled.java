package com.cropster.roastingintelligence.ui.components;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public abstract class AbstractTitled extends VBox {
   private final StringProperty title;
   private final Label titleLabel;

   public AbstractTitled() {
      this.getStyleClass().add("titled");
      this.titleLabel = new Label();
      this.titleLabel.managedProperty().bind(this.titleLabel.visibleProperty());
      this.titleLabel.getStyleClass().add("title");
      this.getChildren().add(this.titleLabel);
      this.title = this.titleLabel.textProperty();
   }

   public StringProperty titleProperty() {
      return this.title;
   }

   public String getTitle() {
      return (String)this.title.get();
   }

   public void setTitle(String title) {
      this.title.set(title);
   }

   public void setTitleVisible(boolean visible) {
      this.titleLabel.setVisible(visible);
   }
}
