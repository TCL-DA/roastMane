package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HorizontalDirection;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class ToggleSwitch extends HBox implements Initializable {
   @FXML
   private Label textLabel;
   @FXML
   private Pane switcher;
   private final StringProperty text = new SimpleStringProperty();
   private final BooleanProperty on = new SimpleBooleanProperty();
   private final ObjectProperty<HorizontalDirection> captionPosition = new SimpleObjectProperty<HorizontalDirection>(HorizontalDirection.LEFT) {
      protected void invalidated() {
         Node firstNode = (Node)ToggleSwitch.this.getChildren().get(0);
         if (this.get() == HorizontalDirection.LEFT && !(firstNode instanceof Label)) {
            ToggleSwitch.this.getChildren().remove(1);
            ToggleSwitch.this.getChildren().add(0, firstNode);
         } else if (this.get() == HorizontalDirection.RIGHT && firstNode instanceof Label) {
            ToggleSwitch.this.getChildren().remove(0);
            ToggleSwitch.this.getChildren().add(firstNode);
         }
      }
   };
   private ObjectProperty<javafx.scene.control.Tooltip> tooltip;

   public ToggleSwitch() {
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.switcher.setOnMouseClicked(e -> this.on.set(!this.on.get()));
      this.on.addListener((s, o, n) -> {
         if (o != n) {
            if (n) {
               this.getStyleClass().remove("toggle-switch--off");
            } else {
               this.getStyleClass().add("toggle-switch--off");
            }
         }
      });
   }

   public StringProperty textProperty() {
      return this.text;
   }

   public String getText() {
      return (String)this.text.get();
   }

   public void setText(String text) {
      this.text.set(text);
   }

   public Label getTextLabel() {
      return this.textLabel;
   }

   public BooleanProperty onProperty() {
      return this.on;
   }

   public boolean isOn() {
      return this.on.get();
   }

   public void setOn(boolean on) {
      this.on.set(on);
   }

   public ObjectProperty<HorizontalDirection> captionPositionProperty() {
      return this.captionPosition;
   }

   public HorizontalDirection isCaptionPosition() {
      return (HorizontalDirection)this.captionPosition.get();
   }

   public void setCaptionPosition(HorizontalDirection captionPosition) {
      this.captionPosition.set(captionPosition);
   }

   public final ObjectProperty<javafx.scene.control.Tooltip> tooltipProperty() {
      if (this.tooltip == null) {
         this.tooltip = new ObjectPropertyBase<javafx.scene.control.Tooltip>() {
            private javafx.scene.control.Tooltip old = null;

            protected void invalidated() {
               javafx.scene.control.Tooltip t = (javafx.scene.control.Tooltip)this.get();
               if (t != this.old) {
                  if (this.old != null) {
                     javafx.scene.control.Tooltip.uninstall(ToggleSwitch.this, this.old);
                  }

                  if (t != null) {
                     javafx.scene.control.Tooltip.install(ToggleSwitch.this, t);
                  }

                  this.old = t;
               }
            }

            public Object getBean() {
               return ToggleSwitch.this;
            }

            public String getName() {
               return "tooltip";
            }
         };
      }

      return this.tooltip;
   }

   public final javafx.scene.control.Tooltip getTooltip() {
      return this.tooltip == null ? null : (javafx.scene.control.Tooltip)this.tooltip.getValue();
   }

   public final void setTooltip(javafx.scene.control.Tooltip value) {
      this.tooltipProperty().setValue(value);
   }

   public Pane getSwitcher() {
      return this.switcher;
   }

   public void toggle() {
      this.setOn(!this.isOn());
   }
}
