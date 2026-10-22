package javafx.scene.control;

import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.scene.control.Logging;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;

@DefaultProperty("items")
public class Menu extends MenuItem {
   public static final EventType<Event> ON_SHOWING = new EventType(Event.ANY, "MENU_ON_SHOWING");
   public static final EventType<Event> ON_SHOWN = new EventType(Event.ANY, "MENU_ON_SHOWN");
   public static final EventType<Event> ON_HIDING = new EventType(Event.ANY, "MENU_ON_HIDING");
   public static final EventType<Event> ON_HIDDEN = new EventType(Event.ANY, "MENU_ON_HIDDEN");
   private ReadOnlyBooleanWrapper showing;
   private ObjectProperty<EventHandler<Event>> onShowing = new ObjectPropertyBase<EventHandler<Event>>() {
      protected void invalidated() {
         Menu.this.eventHandlerManager.setEventHandler(Menu.ON_SHOWING, (EventHandler)this.get());
      }

      public Object getBean() {
         return Menu.this;
      }

      public String getName() {
         return "onShowing";
      }
   };
   private ObjectProperty<EventHandler<Event>> onShown = new ObjectPropertyBase<EventHandler<Event>>() {
      protected void invalidated() {
         Menu.this.eventHandlerManager.setEventHandler(Menu.ON_SHOWN, (EventHandler)this.get());
      }

      public Object getBean() {
         return Menu.this;
      }

      public String getName() {
         return "onShown";
      }
   };
   private ObjectProperty<EventHandler<Event>> onHiding = new ObjectPropertyBase<EventHandler<Event>>() {
      protected void invalidated() {
         Menu.this.eventHandlerManager.setEventHandler(Menu.ON_HIDING, (EventHandler)this.get());
      }

      public Object getBean() {
         return Menu.this;
      }

      public String getName() {
         return "onHiding";
      }
   };
   private ObjectProperty<EventHandler<Event>> onHidden = new ObjectPropertyBase<EventHandler<Event>>() {
      protected void invalidated() {
         Menu.this.eventHandlerManager.setEventHandler(Menu.ON_HIDDEN, (EventHandler)this.get());
      }

      public Object getBean() {
         return Menu.this;
      }

      public String getName() {
         return "onHidden";
      }
   };
   private final ObservableList<MenuItem> items = new TrackableObservableList<MenuItem>() {
      protected void onChanged(Change<MenuItem> var1) {
         while (var1.next()) {
            for (MenuItem var3 : var1.getRemoved()) {
               var3.setParentMenu(null);
               var3.setParentPopup(null);
            }

            for (MenuItem var5 : var1.getAddedSubList()) {
               if (var5.getParentMenu() != null) {
                  Logging.getControlsLogger()
                     .warning("Adding MenuItem " + var5.getText() + " that has already been added to " + var5.getParentMenu().getText());
                  var5.getParentMenu().getItems().remove(var5);
               }

               var5.setParentMenu(Menu.this);
               var5.setParentPopup(Menu.this.getParentPopup());
            }
         }

         if (Menu.this.getItems().size() == 0 && Menu.this.isShowing()) {
            Menu.this.showingPropertyImpl().set(false);
         }
      }
   };
   private static final String DEFAULT_STYLE_CLASS = "menu";
   private static final String STYLE_CLASS_SHOWING = "showing";

   public Menu() {
      this("");
   }

   public Menu(String var1) {
      this(var1, null);
   }

   public Menu(String var1, Node var2) {
      this(var1, var2, (MenuItem[])null);
   }

   public Menu(String var1, Node var2, MenuItem... var3) {
      super(var1, var2);
      this.getStyleClass().add("menu");
      if (var3 != null) {
         this.getItems().addAll(var3);
      }

      this.parentPopupProperty().addListener(var1x -> {
         for (int var2x = 0; var2x < this.getItems().size(); var2x++) {
            MenuItem var3x = (MenuItem)this.getItems().get(var2x);
            var3x.setParentPopup(this.getParentPopup());
         }
      });
   }

   private void setShowing(boolean var1) {
      if (this.getItems().size() != 0 && (!var1 || !this.isShowing())) {
         if (var1) {
            if (this.getOnMenuValidation() != null) {
               Event.fireEvent(this, new Event(MENU_VALIDATION_EVENT));

               for (MenuItem var3 : this.getItems()) {
                  if (!(var3 instanceof Menu) && var3.getOnMenuValidation() != null) {
                     Event.fireEvent(var3, new Event(MenuItem.MENU_VALIDATION_EVENT));
                  }
               }
            }

            Event.fireEvent(this, new Event(ON_SHOWING));
         } else {
            Event.fireEvent(this, new Event(ON_HIDING));
         }

         this.showingPropertyImpl().set(var1);
         Event.fireEvent(this, var1 ? new Event(ON_SHOWN) : new Event(ON_HIDDEN));
      }
   }

   public final boolean isShowing() {
      return this.showing == null ? false : this.showing.get();
   }

   public final ReadOnlyBooleanProperty showingProperty() {
      return this.showingPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyBooleanWrapper showingPropertyImpl() {
      if (this.showing == null) {
         this.showing = new ReadOnlyBooleanWrapper() {
            protected void invalidated() {
               this.get();
               if (Menu.this.isShowing()) {
                  Menu.this.getStyleClass().add("showing");
               } else {
                  Menu.this.getStyleClass().remove("showing");
               }
            }

            public Object getBean() {
               return Menu.this;
            }

            public String getName() {
               return "showing";
            }
         };
      }

      return this.showing;
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

   public final ObservableList<MenuItem> getItems() {
      return this.items;
   }

   public void show() {
      if (!this.isDisable()) {
         this.setShowing(true);
      }
   }

   public void hide() {
      if (this.isShowing()) {
         for (MenuItem var2 : this.getItems()) {
            if (var2 instanceof Menu) {
               Menu var3 = (Menu)var2;
               var3.hide();
            }
         }

         this.setShowing(false);
      }
   }

   @Override
   public <E extends Event> void addEventHandler(EventType<E> var1, EventHandler<E> var2) {
      this.eventHandlerManager.addEventHandler(var1, var2);
   }

   @Override
   public <E extends Event> void removeEventHandler(EventType<E> var1, EventHandler<E> var2) {
      this.eventHandlerManager.removeEventHandler(var1, var2);
   }

   @Override
   public EventDispatchChain buildEventDispatchChain(EventDispatchChain var1) {
      return var1.prepend(this.eventHandlerManager);
   }
}
