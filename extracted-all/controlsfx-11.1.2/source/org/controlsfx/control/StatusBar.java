package org.controlsfx.control;

import impl.org.controlsfx.i18n.Localization;
import impl.org.controlsfx.skin.StatusBarSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Skin;

public class StatusBar extends ControlsFXControl {
   private final StringProperty text = new SimpleStringProperty(this, "text", Localization.localize(Localization.asKey("statusbar.ok")));
   private final ObjectProperty<Node> graphic = new SimpleObjectProperty(this, "graphic");
   private final StringProperty styleTextProperty = new SimpleStringProperty();
   private final ObservableList<Node> leftItems = FXCollections.observableArrayList();
   private final ObservableList<Node> rightItems = FXCollections.observableArrayList();
   private final DoubleProperty progress = new SimpleDoubleProperty(this, "progress");

   public StatusBar() {
      this.getStyleClass().add("status-bar");
   }

   protected Skin<?> createDefaultSkin() {
      return new StatusBarSkin(this);
   }

   public String getUserAgentStylesheet() {
      return this.getUserAgentStylesheet(StatusBar.class, "statusbar.css");
   }

   public final StringProperty textProperty() {
      return this.text;
   }

   public final void setText(String text) {
      this.textProperty().set(text);
   }

   public final String getText() {
      return (String)this.textProperty().get();
   }

   public final ObjectProperty<Node> graphicProperty() {
      return this.graphic;
   }

   public final Node getGraphic() {
      return (Node)this.graphicProperty().get();
   }

   public final void setGraphic(Node node) {
      this.graphicProperty().set(node);
   }

   public void setStyleText(String style) {
      this.styleTextProperty.set(style);
   }

   public String getStyleText() {
      return (String)this.styleTextProperty.get();
   }

   public final StringProperty styleTextProperty() {
      return this.styleTextProperty;
   }

   public final ObservableList<Node> getLeftItems() {
      return this.leftItems;
   }

   public final ObservableList<Node> getRightItems() {
      return this.rightItems;
   }

   public final DoubleProperty progressProperty() {
      return this.progress;
   }

   public final void setProgress(double progress) {
      this.progressProperty().set(progress);
   }

   public final double getProgress() {
      return this.progressProperty().get();
   }
}
