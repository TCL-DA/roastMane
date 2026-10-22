package javafx.scene.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;

public abstract class ComboBoxBase<T> extends Control {
   public static final EventType<Event> ON_SHOWING = new EventType(Event.ANY, "COMBO_BOX_BASE_ON_SHOWING");
   public static final EventType<Event> ON_SHOWN = new EventType(Event.ANY, "COMBO_BOX_BASE_ON_SHOWN");
   public static final EventType<Event> ON_HIDING = new EventType(Event.ANY, "COMBO_BOX_BASE_ON_HIDING");
   public static final EventType<Event> ON_HIDDEN = new EventType(Event.ANY, "COMBO_BOX_BASE_ON_HIDDEN");
   private ObjectProperty<T> value = new SimpleObjectProperty(this, "value");
   private BooleanProperty editable = new SimpleBooleanProperty(this, "editable", false) {
      protected void invalidated() {
         ComboBoxBase.this.pseudoClassStateChanged(ComboBoxBase.PSEUDO_CLASS_EDITABLE, this.get());
      }
   };
   private ReadOnlyBooleanWrapper showing;
   private StringProperty promptText = new SimpleStringProperty(this, "promptText", null) {
      protected void invalidated() {
         String var1 = this.get();
         if (var1 != null && var1.contains("\n")) {
            var1 = var1.replace("\n", "");
            this.set(var1);
         }
      }
   };
   private BooleanProperty armed = new SimpleBooleanProperty(this, "armed", false) {
      protected void invalidated() {
         ComboBoxBase.this.pseudoClassStateChanged(ComboBoxBase.PSEUDO_CLASS_ARMED, this.get());
      }
   };
   private ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
      protected void invalidated() {
         ComboBoxBase.this.setEventHandler(ActionEvent.ACTION, (EventHandler)this.get());
      }

      public Object getBean() {
         return ComboBoxBase.this;
      }

      public String getName() {
         return "onAction";
      }
   };
   private ObjectProperty<EventHandler<Event>> onShowing = new ObjectPropertyBase<EventHandler<Event>>() {
      protected void invalidated() {
         ComboBoxBase.this.setEventHandler(ComboBoxBase.ON_SHOWING, (EventHandler)this.get());
      }

      public Object getBean() {
         return ComboBoxBase.this;
      }

      public String getName() {
         return "onShowing";
      }
   };
   private ObjectProperty<EventHandler<Event>> onShown = new ObjectPropertyBase<EventHandler<Event>>() {
      protected void invalidated() {
         ComboBoxBase.this.setEventHandler(ComboBoxBase.ON_SHOWN, (EventHandler)this.get());
      }

      public Object getBean() {
         return ComboBoxBase.this;
      }

      public String getName() {
         return "onShown";
      }
   };
   private ObjectProperty<EventHandler<Event>> onHiding = new ObjectPropertyBase<EventHandler<Event>>() {
      protected void invalidated() {
         ComboBoxBase.this.setEventHandler(ComboBoxBase.ON_HIDING, (EventHandler)this.get());
      }

      public Object getBean() {
         return ComboBoxBase.this;
      }

      public String getName() {
         return "onHiding";
      }
   };
   private ObjectProperty<EventHandler<Event>> onHidden = new ObjectPropertyBase<EventHandler<Event>>() {
      protected void invalidated() {
         ComboBoxBase.this.setEventHandler(ComboBoxBase.ON_HIDDEN, (EventHandler)this.get());
      }

      public Object getBean() {
         return ComboBoxBase.this;
      }

      public String getName() {
         return "onHidden";
      }
   };
   private static final String DEFAULT_STYLE_CLASS = "combo-box-base";
   private static final PseudoClass PSEUDO_CLASS_EDITABLE = PseudoClass.getPseudoClass("editable");
   private static final PseudoClass PSEUDO_CLASS_SHOWING = PseudoClass.getPseudoClass("showing");
   private static final PseudoClass PSEUDO_CLASS_ARMED = PseudoClass.getPseudoClass("armed");

   public ComboBoxBase() {
      this.getStyleClass().add("combo-box-base");
      this.getProperties().addListener(var1 -> {
         if (var1.wasAdded() && var1.getKey() == "FOCUSED") {
            this.setFocused((Boolean)var1.getValueAdded());
            this.getProperties().remove("FOCUSED");
         }
      });
   }

   public ObjectProperty<T> valueProperty() {
      return this.value;
   }

   public final void setValue(T var1) {
      this.valueProperty().set(var1);
   }

   public final T getValue() {
      return (T)this.valueProperty().get();
   }

   public BooleanProperty editableProperty() {
      return this.editable;
   }

   public final void setEditable(boolean var1) {
      this.editableProperty().set(var1);
   }

   public final boolean isEditable() {
      return this.editableProperty().get();
   }

   public ReadOnlyBooleanProperty showingProperty() {
      return this.showingPropertyImpl().getReadOnlyProperty();
   }

   public final boolean isShowing() {
      return this.showingPropertyImpl().get();
   }

   private void setShowing(boolean var1) {
      Event.fireEvent(this, var1 ? new Event(ON_SHOWING) : new Event(ON_HIDING));
      this.showingPropertyImpl().set(var1);
      Event.fireEvent(this, var1 ? new Event(ON_SHOWN) : new Event(ON_HIDDEN));
   }

   private ReadOnlyBooleanWrapper showingPropertyImpl() {
      if (this.showing == null) {
         this.showing = new ReadOnlyBooleanWrapper(false) {
            protected void invalidated() {
               ComboBoxBase.this.pseudoClassStateChanged(ComboBoxBase.PSEUDO_CLASS_SHOWING, this.get());
               ComboBoxBase.this.notifyAccessibleAttributeChanged(AccessibleAttribute.EXPANDED);
            }

            public Object getBean() {
               return ComboBoxBase.this;
            }

            public String getName() {
               return "showing";
            }
         };
      }

      return this.showing;
   }

   public final StringProperty promptTextProperty() {
      return this.promptText;
   }

   public final String getPromptText() {
      return (String)this.promptText.get();
   }

   public final void setPromptText(String var1) {
      this.promptText.set(var1);
   }

   public BooleanProperty armedProperty() {
      return this.armed;
   }

   private final void setArmed(boolean var1) {
      this.armedProperty().set(var1);
   }

   public final boolean isArmed() {
      return this.armedProperty().get();
   }

   public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
      return this.onAction;
   }

   public final void setOnAction(EventHandler<ActionEvent> var1) {
      this.onActionProperty().set(var1);
   }

   public final EventHandler<ActionEvent> getOnAction() {
      return (EventHandler<ActionEvent>)this.onActionProperty().get();
   }

   public final ObjectProperty<EventHandler<Event>> onShowingProperty() {
      return this.onShowing;
   }

   public final void setOnShowing(EventHandler<Event> var1) {
      this.onShowingProperty().set(var1);
   }

   public final EventHandler<Event> getOnShowing() {
      return (EventHandler<Event>)this.onShowingProperty().get();
   }

   public final ObjectProperty<EventHandler<Event>> onShownProperty() {
      return this.onShown;
   }

   public final void setOnShown(EventHandler<Event> var1) {
      this.onShownProperty().set(var1);
   }

   public final EventHandler<Event> getOnShown() {
      return (EventHandler<Event>)this.onShownProperty().get();
   }

   public final ObjectProperty<EventHandler<Event>> onHidingProperty() {
      return this.onHiding;
   }

   public final void setOnHiding(EventHandler<Event> var1) {
      this.onHidingProperty().set(var1);
   }

   public final EventHandler<Event> getOnHiding() {
      return (EventHandler<Event>)this.onHidingProperty().get();
   }

   public final ObjectProperty<EventHandler<Event>> onHiddenProperty() {
      return this.onHidden;
   }

   public final void setOnHidden(EventHandler<Event> var1) {
      this.onHiddenProperty().set(var1);
   }

   public final EventHandler<Event> getOnHidden() {
      return (EventHandler<Event>)this.onHiddenProperty().get();
   }

   public void show() {
      if (!this.isDisabled()) {
         this.setShowing(true);
      }
   }

   public void hide() {
      if (this.isShowing()) {
         this.setShowing(false);
      }
   }

   public void arm() {
      if (!this.armedProperty().isBound()) {
         this.setArmed(true);
      }
   }

   public void disarm() {
      if (!this.armedProperty().isBound()) {
         this.setArmed(false);
      }
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case EXPANDED:
            return this.isShowing();
         case EDITABLE:
            return this.isEditable();
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   @Override
   public void executeAccessibleAction(AccessibleAction var1, Object... var2) {
      switch (var1) {
         case EXPAND:
            this.show();
            break;
         case COLLAPSE:
            this.hide();
            break;
         default:
            super.executeAccessibleAction(var1);
      }
   }
}
