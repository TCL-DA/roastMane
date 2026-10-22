package com.sun.javafx.menu;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;

public interface MenuBase extends MenuItemBase {
   boolean isShowing();

   ReadOnlyBooleanProperty showingProperty();

   ObjectProperty<EventHandler<Event>> onShowingProperty();

   void setOnShowing(EventHandler<Event> var1);

   EventHandler<Event> getOnShowing();

   ObjectProperty<EventHandler<Event>> onShownProperty();

   void setOnShown(EventHandler<Event> var1);

   EventHandler<Event> getOnShown();

   ObjectProperty<EventHandler<Event>> onHidingProperty();

   void setOnHiding(EventHandler<Event> var1);

   EventHandler<Event> getOnHiding();

   ObjectProperty<EventHandler<Event>> onHiddenProperty();

   void setOnHidden(EventHandler<Event> var1);

   EventHandler<Event> getOnHidden();

   ObservableList<MenuItemBase> getItemsBase();

   void show();

   void hide();
}
