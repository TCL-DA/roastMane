package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class HintsFooterElement extends GridPane {
   @FXML
   private Label descriptionLabel;
   @FXML
   private Label elementLabel;

   public HintsFooterElement() {
      JavaFxUtil.loadComponent(this);
   }

   public HintsFooterElement(String description, String element) {
      this(description, null, element, null);
   }

   public HintsFooterElement(String description, Node elementGraphic) {
      this(description, null, null, elementGraphic);
   }

   public HintsFooterElement(String description, Node descriptionGraphic, String element, Node elementGraphic) {
      this();
      this.setDescription(description);
      this.setDescriptionGraphic(descriptionGraphic);
      this.setElement(element);
      this.setElementGraphic(elementGraphic);
   }

   public StringProperty descriptionProperty() {
      return this.descriptionLabel.textProperty();
   }

   public String getDescription() {
      return this.descriptionLabel.getText();
   }

   public void setDescription(String description) {
      this.descriptionLabel.setText(description);
   }

   public ObjectProperty<Node> descriptionGraphicProperty() {
      return this.descriptionLabel.graphicProperty();
   }

   public Node getDescriptionGraphic() {
      return this.descriptionLabel.getGraphic();
   }

   public void setDescriptionGraphic(Node graphic) {
      this.descriptionLabel.setGraphic(graphic);
   }

   public ObjectProperty<Node> elementGraphicProperty() {
      return this.elementLabel.graphicProperty();
   }

   public Node getElementGraphic() {
      return this.elementLabel.getGraphic();
   }

   public void setElementGraphic(Node graphic) {
      this.elementLabel.setGraphic(graphic);
   }

   public StringProperty elementProperty() {
      return this.elementLabel.textProperty();
   }

   public String getElement() {
      return this.elementLabel.getText();
   }

   public void setElement(String element) {
      this.elementLabel.setText(element);
   }
}
