package org.controlsfx.control.tableview2.event;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public class FilterEvent<C> extends Event {
   public static final EventType<FilterEvent> ANY = new EventType(Event.ANY, "FILTER");
   private static final EventType<?> FILTER_EVENT = new EventType(ANY, "FILTER_EVENT");

   public static <C> EventType<FilterEvent<C>> filterEvent() {
      return (EventType<FilterEvent<C>>)FILTER_EVENT;
   }

   public FilterEvent(@NamedArg("source") C source, @NamedArg("target") EventTarget target) {
      super(source, target, filterEvent());
   }

   public C getSource() {
      return (C)super.getSource();
   }
}
