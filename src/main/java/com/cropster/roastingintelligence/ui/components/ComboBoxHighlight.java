package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.skin.TextFieldWithIconAndMessageSkin;
import com.cropster.roastingintelligence.ui.util.Icon;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComboBoxHighlight<T> extends ComboBox<T> {
   private static final Logger logger = LoggerFactory.getLogger(ComboBoxHighlight.class);
   private final ObjectProperty<ComboBoxHighlight.Status> highlight = new SimpleObjectProperty();

   public ComboBoxHighlight() {
      this.initialize();
   }

   public ObjectProperty<ComboBoxHighlight.Status> highlightProperty() {
      return this.highlight;
   }

   public ComboBoxHighlight.Status isHighlight() {
      return (ComboBoxHighlight.Status)this.highlight.get();
   }

   public void setHighlight(ComboBoxHighlight.Status highlight) {
      this.highlight.set(highlight);
   }

   public String getHighlightMessage() {
      TextField editor = this.getEditor();
      return editor.getSkin() instanceof TextFieldWithIconAndMessageSkin skin ? skin.getMessage() : null;
   }

   public void setHighlightMessage(String message) {
      TextField editor = this.getEditor();
      if (editor.getSkin() instanceof TextFieldWithIconAndMessageSkin skin) {
         skin.setMessage(message);
      } else {
         logger.warn("ComboBox editor skin is not the expected TextFieldWithIconAndMessageSkin");
      }
   }

   private void initialize() {
      TextField comboBoxEditor = this.getEditor();
      comboBoxEditor.setSkin(new TextFieldWithIconAndMessageSkin(comboBoxEditor));
      JavaFxUtil.addStyleIfNotPresent(this.getStyleClass(), "custom-skin");
      this.highlight.addListener((s, o, n) -> {
         ObservableList<String> styleClass = this.getStyleClass();
         styleClass.removeAll(new String[]{"validation-error", "validation-warning", "custom-skin"});
         TextField editor = this.getEditor();
         if (editor.getSkin() instanceof TextFieldWithIconAndMessageSkin skin) {
            if (n == ComboBoxHighlight.Status.ERROR) {
               JavaFxUtil.addStyleIfNotPresent(styleClass, "validation-error");
               JavaFxUtil.addStyleIfNotPresent(styleClass, "custom-skin");
               skin.setIcon(Icon.ERROR.load("icon--error"));
            } else if (n == ComboBoxHighlight.Status.WARNING) {
               JavaFxUtil.addStyleIfNotPresent(styleClass, "validation-warning");
               JavaFxUtil.addStyleIfNotPresent(styleClass, "custom-skin");
               skin.setIcon(Icon.WARNING.load("icon--warning"));
            } else {
               skin.setIcon(null);
            }
         } else {
            logger.warn("ComboBox editor skin is not the expected TextFieldWithIconAndMessageSkin");
         }
      });
   }

   public static enum Status {
      NONE,
      WARNING,
      ERROR;
   }
}
