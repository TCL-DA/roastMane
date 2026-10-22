package com.sun.javafx.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public class DirectEvent extends Event {
   private static final long serialVersionUID = 20121107L;
   public static final EventType<DirectEvent> DIRECT = new EventType<>(Event.ANY, "DIRECT");
   private final Event originalEvent;

   public DirectEvent(Event var1) {
      this(var1, null, null);
   }

   public DirectEvent(Event var1, Object var2, EventTarget var3) {
      super(var2, var3, DIRECT);
      this.originalEvent = var1;
   }

   public Event getOriginalEvent() {
      return this.originalEvent;
   }
}
