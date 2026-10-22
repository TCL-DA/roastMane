package org.controlsfx.control;

import impl.org.controlsfx.skin.HyperlinkLabelSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.control.Skin;

public class HyperlinkLabel extends ControlsFXControl implements EventTarget {
   private final StringProperty text = new SimpleStringProperty(this, "text");
   private ObjectProperty<EventHandler<ActionEvent>> onAction;
   private static final String DEFAULT_STYLE_CLASS = "hyperlink-label";

   public HyperlinkLabel() {
      this(null);
   }

   public HyperlinkLabel(String text) {
      this.setText(text);
      this.getStyleClass().add("hyperlink-label");
   }

   protected Skin<?> createDefaultSkin() {
      return new HyperlinkLabelSkin(this);
   }

   public final StringProperty textProperty() {
      return this.text;
   }

   public final String getText() {
      return (String)this.text.get();
   }

   public final void setText(String value) {
      this.text.set(value);
   }

   public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
      if (this.onAction == null) {
         this.onAction = new SimpleObjectProperty<EventHandler<ActionEvent>>(this, "onAction") {
            protected void invalidated() {
               HyperlinkLabel.this.setEventHandler(ActionEvent.ACTION, (EventHandler)this.get());
            }
         };
      }

      return this.onAction;
   }

   public final void setOnAction(EventHandler<ActionEvent> value) {
      this.onActionProperty().set(value);
   }

   public final EventHandler<ActionEvent> getOnAction() {
      return this.onAction == null ? null : (EventHandler)this.onAction.get();
   }
}
