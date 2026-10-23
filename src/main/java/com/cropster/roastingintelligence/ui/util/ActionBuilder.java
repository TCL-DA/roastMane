package com.cropster.roastingintelligence.ui.util;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.control.action.ActionUtils.ActionTextBehavior;
import org.controlsfx.tools.Duplicatable;
import org.girod.javafx.svgimage.SVGContent;
import org.girod.javafx.svgimage.SVGImage;

public class ActionBuilder {
   public static final String ID_PROPERTY = "id";
   public static final String STYLE_CLASS_PROPERTY = "style-class";
   public static final String PROPERTIES_LISTER_PROPERTY = "properties-listener";
   private final Consumer<ActionEvent> eventHandler;
   private final String text;
   private KeyCombination accelerator = null;
   private boolean disabled = false;
   private boolean disableOnCallback = false;
   private Node graphics = null;
   private String id = null;
   private String longText = null;
   private boolean selected = false;
   private String style = null;
   private String[] classes = null;

   public static Button buttonFrom(Action action, ActionTextBehavior textBehavior) {
      return configureButton(action, new Button(), textBehavior);
   }

   public static Button configureButton(final Action action, Button button, ActionTextBehavior textBehavior) {
      ActionUtils.configureButton(action, button, textBehavior);
      button.setId((String)action.getProperties().get("id"));
      button.graphicProperty().unbind();
      button.graphicProperty().bind(new ObjectBinding<Node>() {
         {
            this.bind(new Observable[]{action.graphicProperty()});
         }

         protected Node computeValue() {
            return ActionBuilder.copyNode((Node)action.graphicProperty().get());
         }

         public void removeListener(InvalidationListener listener) {
            super.removeListener(listener);
            this.unbind(new Observable[]{action.graphicProperty()});
         }
      });
      Optional.ofNullable((String[])action.getProperties().get("style-class")).ifPresent(styleClass -> button.getStyleClass().addAll(styleClass));
      if (!action.getProperties().containsKey("properties-listener")) {
         MapChangeListener<Object, Object> listener = change -> {
            if ("id".equals(change.getKey())) {
               if (change.wasRemoved() && change.wasAdded()) {
                  if (Objects.equals(button.getId(), change.getValueRemoved())) {
                     button.setId((String)change.getValueAdded());
                  }
               } else if (change.wasRemoved()) {
                  if (Objects.equals(button.getId(), change.getValueRemoved())) {
                     button.setId(null);
                  }
               } else if (StringUtils.isBlank(button.getId())) {
                  button.setId((String)change.getValueAdded());
               }
            } else if ("style-class".equals(change.getKey())) {
               if (change.wasRemoved() && change.wasAdded()) {
                  String[] sClass = (String[])change.getValueRemoved();
                  if (sClass != null) {
                     button.getStyleClass().removeAll(sClass);
                  }

                  sClass = (String[])change.getValueAdded();
                  if (sClass != null) {
                     button.getStyleClass().addAll(sClass);
                  }
               } else if (change.wasRemoved()) {
                  String[] sClassx = (String[])change.getValueRemoved();
                  if (sClassx != null) {
                     button.getStyleClass().removeAll(sClassx);
                  }
               } else {
                  String[] sClassx = (String[])change.getValueAdded();
                  if (sClassx != null) {
                     button.getStyleClass().addAll(sClassx);
                  }
               }
            }
         };
         action.getProperties().addListener(listener);
         action.getProperties().put("properties-listener", listener);
      }

      return button;
   }

   public static MenuItem configureMenuItem(final Action action, MenuItem menuItem) {
      ActionUtils.configureMenuItem(action, menuItem);
      menuItem.setId((String)action.getProperties().get("id"));
      menuItem.graphicProperty().unbind();
      menuItem.graphicProperty().bind(new ObjectBinding<Node>() {
         {
            this.bind(new Observable[]{action.graphicProperty()});
         }

         protected Node computeValue() {
            return ActionBuilder.copyNode((Node)action.graphicProperty().get());
         }

         public void removeListener(InvalidationListener listener) {
            super.removeListener(listener);
            this.unbind(new Observable[]{action.graphicProperty()});
         }
      });
      Optional.ofNullable((String[])action.getProperties().get("style-class")).ifPresent(styleClass -> menuItem.getStyleClass().addAll(styleClass));
      if (!action.getProperties().containsKey("properties-listener")) {
         MapChangeListener<Object, Object> listener = change -> {
            if ("id".equals(change.getKey())) {
               if (change.wasRemoved() && change.wasAdded()) {
                  if (Objects.equals(menuItem.getId(), change.getValueRemoved())) {
                     menuItem.setId((String)change.getValueAdded());
                  }
               } else if (change.wasRemoved()) {
                  if (Objects.equals(menuItem.getId(), change.getValueRemoved())) {
                     menuItem.setId(null);
                  }
               } else if (StringUtils.isBlank(menuItem.getId())) {
                  menuItem.setId((String)change.getValueAdded());
               }
            } else if ("style-class".equals(change.getKey())) {
               if (change.wasRemoved() && change.wasAdded()) {
                  String[] sClass = (String[])change.getValueRemoved();
                  if (sClass != null) {
                     menuItem.getStyleClass().removeAll(sClass);
                  }

                  sClass = (String[])change.getValueAdded();
                  if (sClass != null) {
                     menuItem.getStyleClass().addAll(sClass);
                  }
               } else if (change.wasRemoved()) {
                  String[] sClassx = (String[])change.getValueRemoved();
                  if (sClassx != null) {
                     menuItem.getStyleClass().removeAll(sClassx);
                  }
               } else {
                  String[] sClassx = (String[])change.getValueAdded();
                  if (sClassx != null) {
                     menuItem.getStyleClass().addAll(sClassx);
                  }
               }
            }
         };
         action.getProperties().addListener(listener);
         action.getProperties().put("properties-listener", listener);
      }

      return menuItem;
   }

   public static MenuItem menuItemFrom(Action action) {
      return configureMenuItem(action, new MenuItem());
   }

   public static Action update(Action action, Node graphics, KeyCombination accelerator) {
      Objects.requireNonNull(action);
      action.setAccelerator(accelerator);
      action.setGraphic(graphics);
      return action;
   }

   public static Action update(Action action, Node graphics, String longText, KeyCombination accelerator) {
      Objects.requireNonNull(action);
      action.setAccelerator(accelerator);
      action.setGraphic(graphics);
      action.setLongText(longText);
      return action;
   }

   public static Action update(Action action, Node graphics, KeyCombination accelerator, boolean disabled, boolean selected) {
      Objects.requireNonNull(action);
      action.setAccelerator(accelerator);
      action.setDisabled(disabled);
      action.setGraphic(graphics);
      action.setSelected(selected);
      return action;
   }

   public static Action update(Action action, Node graphics, String longText, KeyCombination accelerator, boolean disabled, boolean selected) {
      Objects.requireNonNull(action);
      action.setAccelerator(accelerator);
      action.setDisabled(disabled);
      action.setGraphic(graphics);
      action.setLongText(longText);
      action.setSelected(selected);
      return action;
   }

   public static Action update(Action action, Node graphics, String longText, KeyCombination accelerator, boolean disabled, boolean selected, String style) {
      Objects.requireNonNull(action);
      action.setAccelerator(accelerator);
      action.setDisabled(disabled);
      action.setGraphic(graphics);
      action.setLongText(longText);
      action.setSelected(selected);
      action.setStyle(style);
      return action;
   }

   private static Node copyNode(Node node) {
      if (node instanceof ImageView imageView) {
         return new ImageView(imageView.getImage());
      } else if (node instanceof Duplicatable<?> duplicatable) {
         return (Node)duplicatable.duplicate();
      } else if (node instanceof SVGImage svgImage) {
         SVGContent svgContent = svgImage.getSVGContent();
         return svgContent.isFromURL() ? JavaFxUtil.loadSvg(svgContent.url) : new SVGImage(svgContent);
      } else {
         return null;
      }
   }

   public ActionBuilder(String text) {
      this(text, (Consumer<ActionEvent>)null);
   }

   public ActionBuilder(Runnable callback) {
      this(null, e -> callback.run());
   }

   public ActionBuilder(Consumer<ActionEvent> eventHandler) {
      this(null, eventHandler);
   }

   public ActionBuilder(String text, Runnable callback) {
      this(text, e -> callback.run());
   }

   public ActionBuilder(String text, Consumer<ActionEvent> eventHandler) {
      this.text = text;
      this.eventHandler = eventHandler;
   }

   public ActionBuilder accelerator(KeyCombination accelerator) {
      this.accelerator = accelerator;
      return this;
   }

   public Action build() {
      Action action = new Action(this.text, this.eventHandler) {
         protected void setEventHandler(Consumer<ActionEvent> eventHandler) {
            Consumer<ActionEvent> handler = eventHandler;
            if (ActionBuilder.this.disableOnCallback && eventHandler != null) {
               handler = e -> {
                  this.setDisabled(true);
                  eventHandler.accept(e);
               };
            }

            super.setEventHandler(handler);
         }
      };
      action.setAccelerator(this.accelerator);
      action.setDisabled(this.disabled);
      action.setGraphic(this.graphics);
      action.setLongText(this.longText);
      action.setSelected(this.selected);
      action.setStyle(this.style);
      action.getProperties().put("style-class", this.classes);
      if (this.id != null) {
         action.getProperties().put("id", this.id);
      }

      if (this.classes != null) {
         action.getProperties().put("style-class", this.classes);
      }

      return action;
   }

   public ActionBuilder disabled(boolean disabled) {
      this.disabled = disabled;
      return this;
   }

   public ActionBuilder disableOnCallback(boolean disableOnCallback) {
      this.disableOnCallback = disableOnCallback;
      return this;
   }

   public ActionBuilder graphics(Node graphics) {
      this.graphics = graphics;
      return this;
   }

   public ActionBuilder id(String id) {
      this.id = id;
      return this;
   }

   public ActionBuilder longText(String longText) {
      this.longText = longText;
      return this;
   }

   public ActionBuilder selected(boolean selected) {
      this.selected = selected;
      return this;
   }

   public ActionBuilder style(String style) {
      this.style = style;
      return this;
   }

   public ActionBuilder styleClass(String... classes) {
      this.classes = classes;
      return this;
   }
}
