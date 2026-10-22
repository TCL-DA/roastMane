package org.controlsfx.control;

import impl.org.controlsfx.skin.InfoOverlaySkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.image.ImageView;

public class InfoOverlay extends ControlsFXControl {
   private ObjectProperty<Node> content = new SimpleObjectProperty(this, "content");
   private StringProperty text = new SimpleStringProperty(this, "text");
   private BooleanProperty showOnHover = new SimpleBooleanProperty(this, "showOnHover", true);
   private static final String DEFAULT_STYLE_CLASS = "info-overlay";

   public InfoOverlay() {
      this((Node)null, null);
   }

   public InfoOverlay(String imageUrl, String text) {
      this(new ImageView(imageUrl), text);
   }

   public InfoOverlay(Node content, String text) {
      this.getStyleClass().setAll(new String[]{"info-overlay"});
      this.setContent(content);
      this.setText(text);
   }

   protected Skin<?> createDefaultSkin() {
      return new InfoOverlaySkin(this);
   }

   public final ObjectProperty<Node> contentProperty() {
      return this.content;
   }

   public final void setContent(Node content) {
      this.contentProperty().set(content);
   }

   public final Node getContent() {
      return (Node)this.contentProperty().get();
   }

   public final StringProperty textProperty() {
      return this.text;
   }

   public final String getText() {
      return (String)this.textProperty().get();
   }

   public final void setText(String text) {
      this.textProperty().set(text);
   }

   public final BooleanProperty showOnHoverProperty() {
      return this.showOnHover;
   }

   public final boolean isShowOnHover() {
      return this.showOnHoverProperty().get();
   }

   public final void setShowOnHover(boolean value) {
      this.showOnHoverProperty().set(value);
   }

   public String getUserAgentStylesheet() {
      return this.getUserAgentStylesheet(InfoOverlay.class, "info-overlay.css");
   }
}
