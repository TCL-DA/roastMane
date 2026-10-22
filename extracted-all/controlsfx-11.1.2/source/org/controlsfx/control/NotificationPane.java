package org.controlsfx.control;

import impl.org.controlsfx.skin.NotificationPaneSkin;
import java.util.UUID;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import org.controlsfx.control.action.Action;

public class NotificationPane extends ControlsFXControl {
   public static final String STYLE_CLASS_DARK = "dark";
   public static final EventType<Event> ON_SHOWING = newEventType("NOTIFICATION_PANE_ON_SHOWING");
   public static final EventType<Event> ON_SHOWN = newEventType("NOTIFICATION_PANE_ON_SHOWN");
   public static final EventType<Event> ON_HIDING = newEventType("NOTIFICATION_PANE_ON_HIDING");
   public static final EventType<Event> ON_HIDDEN = newEventType("NOTIFICATION_PANE_ON_HIDDEN");
   private ObjectProperty<Node> content = new SimpleObjectProperty(this, "content");
   private StringProperty text = new SimpleStringProperty(this, "text");
   private ObjectProperty<Node> graphic = new SimpleObjectProperty(this, "graphic");
   private ReadOnlyBooleanWrapper showing = new ReadOnlyBooleanWrapper(this, "showing");
   private BooleanProperty showFromTop = new SimpleBooleanProperty(this, "showFromTop", true) {
      protected void invalidated() {
         NotificationPane.this.updateStyleClasses();
      }
   };
   private ObjectProperty<EventHandler<Event>> onShowing = new SimpleObjectProperty<EventHandler<Event>>(this, "onShowing") {
      protected void invalidated() {
         NotificationPane.this.setEventHandler(NotificationPane.ON_SHOWING, (EventHandler)this.get());
      }
   };
   private ObjectProperty<EventHandler<Event>> onShown = new SimpleObjectProperty<EventHandler<Event>>(this, "onShown") {
      protected void invalidated() {
         NotificationPane.this.setEventHandler(NotificationPane.ON_SHOWN, (EventHandler)this.get());
      }
   };
   private ObjectProperty<EventHandler<Event>> onHiding = new SimpleObjectProperty<EventHandler<Event>>(this, "onHiding") {
      protected void invalidated() {
         NotificationPane.this.setEventHandler(NotificationPane.ON_HIDING, (EventHandler)this.get());
      }
   };
   private ObjectProperty<EventHandler<Event>> onHidden = new SimpleObjectProperty<EventHandler<Event>>(this, "onHidden") {
      protected void invalidated() {
         NotificationPane.this.setEventHandler(NotificationPane.ON_HIDDEN, (EventHandler)this.get());
      }
   };
   private BooleanProperty closeButtonVisible = new SimpleBooleanProperty(this, "closeButtonVisible", true);
   private final ObservableList<Action> actions = FXCollections.observableArrayList();
   private static final String DEFAULT_STYLE_CLASS = "notification-pane";

   private static EventType<Event> newEventType(String name) {
      return new EventType(Event.ANY, name + UUID.randomUUID().toString());
   }

   public NotificationPane() {
      this(null);
   }

   public NotificationPane(Node content) {
      this.getStyleClass().add("notification-pane");
      this.setContent(content);
      this.updateStyleClasses();
   }

   protected Skin<?> createDefaultSkin() {
      return new NotificationPaneSkin(this);
   }

   public String getUserAgentStylesheet() {
      return this.getUserAgentStylesheet(NotificationPane.class, "notificationpane.css");
   }

   public final ObjectProperty<Node> contentProperty() {
      return this.content;
   }

   public final void setContent(Node value) {
      this.content.set(value);
   }

   public final Node getContent() {
      return (Node)this.content.get();
   }

   public final StringProperty textProperty() {
      return this.text;
   }

   public final void setText(String value) {
      this.text.set(value);
   }

   public final String getText() {
      return (String)this.text.get();
   }

   public final ObjectProperty<Node> graphicProperty() {
      return this.graphic;
   }

   public final void setGraphic(Node value) {
      this.graphic.set(value);
   }

   public final Node getGraphic() {
      return (Node)this.graphic.get();
   }

   public final ReadOnlyBooleanProperty showingProperty() {
      return this.showing.getReadOnlyProperty();
   }

   private final void setShowing(boolean value) {
      this.showing.set(value);
   }

   public final boolean isShowing() {
      return this.showing.get();
   }

   public final BooleanProperty showFromTopProperty() {
      return this.showFromTop;
   }

   public final void setShowFromTop(boolean value) {
      this.showFromTop.set(value);
   }

   public final boolean isShowFromTop() {
      return this.showFromTop.get();
   }

   public final ObjectProperty<EventHandler<Event>> onShowingProperty() {
      return this.onShowing;
   }

   public final void setOnShowing(EventHandler<Event> value) {
      this.onShowingProperty().set(value);
   }

   public final EventHandler<Event> getOnShowing() {
      return (EventHandler<Event>)this.onShowingProperty().get();
   }

   public final ObjectProperty<EventHandler<Event>> onShownProperty() {
      return this.onShown;
   }

   public final void setOnShown(EventHandler<Event> value) {
      this.onShownProperty().set(value);
   }

   public final EventHandler<Event> getOnShown() {
      return (EventHandler<Event>)this.onShownProperty().get();
   }

   public final ObjectProperty<EventHandler<Event>> onHidingProperty() {
      return this.onHiding;
   }

   public final void setOnHiding(EventHandler<Event> value) {
      this.onHidingProperty().set(value);
   }

   public final EventHandler<Event> getOnHiding() {
      return (EventHandler<Event>)this.onHidingProperty().get();
   }

   public final ObjectProperty<EventHandler<Event>> onHiddenProperty() {
      return this.onHidden;
   }

   public final void setOnHidden(EventHandler<Event> value) {
      this.onHiddenProperty().set(value);
   }

   public final EventHandler<Event> getOnHidden() {
      return (EventHandler<Event>)this.onHiddenProperty().get();
   }

   public final BooleanProperty closeButtonVisibleProperty() {
      return this.closeButtonVisible;
   }

   public final void setCloseButtonVisible(boolean value) {
      this.closeButtonVisible.set(value);
   }

   public final boolean isCloseButtonVisible() {
      return this.closeButtonVisible.get();
   }

   public final ObservableList<Action> getActions() {
      return this.actions;
   }

   public void show() {
      this.setShowing(true);
   }

   public void show(final String text) {
      this.hideAndThen(new Runnable() {
         @Override
         public void run() {
            NotificationPane.this.setText(text);
            NotificationPane.this.setShowing(true);
         }
      });
   }

   public void show(final String text, final Node graphic) {
      this.hideAndThen(new Runnable() {
         @Override
         public void run() {
            NotificationPane.this.setText(text);
            NotificationPane.this.setGraphic(graphic);
            NotificationPane.this.setShowing(true);
         }
      });
   }

   public void show(final String text, final Node graphic, final Action... actions) {
      this.hideAndThen(new Runnable() {
         @Override
         public void run() {
            NotificationPane.this.setText(text);
            NotificationPane.this.setGraphic(graphic);
            if (actions == null) {
               NotificationPane.this.getActions().clear();
            } else {
               for (Action action : actions) {
                  if (action != null) {
                     NotificationPane.this.getActions().add(action);
                  }
               }
            }

            NotificationPane.this.setShowing(true);
         }
      });
   }

   public void hide() {
      this.setShowing(false);
   }

   private void updateStyleClasses() {
      this.getStyleClass().removeAll(new String[]{"top", "bottom"});
      this.getStyleClass().add(this.isShowFromTop() ? "top" : "bottom");
   }

   private void hideAndThen(final Runnable r) {
      if (this.isShowing()) {
         EventHandler<Event> eventHandler = new EventHandler<Event>() {
            public void handle(Event e) {
               r.run();
               NotificationPane.this.removeEventHandler(NotificationPane.ON_HIDDEN, this);
            }
         };
         this.addEventHandler(ON_HIDDEN, eventHandler);
         this.hide();
      } else {
         r.run();
      }
   }
}
