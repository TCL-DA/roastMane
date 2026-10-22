package org.controlsfx.control;

import impl.org.controlsfx.skin.MaskerPaneSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Skin;

public class MaskerPane extends ControlsFXControl {
   private final DoubleProperty progress = new SimpleDoubleProperty(this, "progress", -1.0);
   private final ObjectProperty<Node> progressNode = new SimpleObjectProperty<Node>() {
      {
         ProgressIndicator node = new ProgressIndicator();
         node.progressProperty().bind(MaskerPane.this.progress);
         this.setValue(node);
      }

      public String getName() {
         return "progressNode";
      }

      public Object getBean() {
         return MaskerPane.this;
      }
   };
   private final BooleanProperty progressVisible = new SimpleBooleanProperty(this, "progressVisible", true);
   private final StringProperty text = new SimpleStringProperty(this, "text", "Please Wait...");

   public MaskerPane() {
      this.getStyleClass().add("masker-pane");
   }

   public final DoubleProperty progressProperty() {
      return this.progress;
   }

   public final double getProgress() {
      return this.progress.get();
   }

   public final void setProgress(double progress) {
      this.progress.set(progress);
   }

   public final ObjectProperty<Node> progressNodeProperty() {
      return this.progressNode;
   }

   public final Node getProgressNode() {
      return (Node)this.progressNode.get();
   }

   public final void setProgressNode(Node progressNode) {
      this.progressNode.set(progressNode);
   }

   public final BooleanProperty progressVisibleProperty() {
      return this.progressVisible;
   }

   public final boolean getProgressVisible() {
      return this.progressVisible.get();
   }

   public final void setProgressVisible(boolean progressVisible) {
      this.progressVisible.set(progressVisible);
   }

   public final StringProperty textProperty() {
      return this.text;
   }

   public final String getText() {
      return (String)this.text.get();
   }

   public final void setText(String text) {
      this.text.set(text);
   }

   protected Skin<?> createDefaultSkin() {
      return new MaskerPaneSkin(this);
   }

   public String getUserAgentStylesheet() {
      return this.getUserAgentStylesheet(MaskerPane.class, "maskerpane.css");
   }
}
