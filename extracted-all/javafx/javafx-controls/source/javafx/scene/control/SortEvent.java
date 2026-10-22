package javafx.scene.control;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public class SortEvent<C> extends Event {
   public static final EventType<SortEvent> ANY = new EventType(Event.ANY, "SORT");
   private static final EventType<?> SORT_EVENT = new EventType(ANY, "SORT_EVENT");

   public static <C> EventType<SortEvent<C>> sortEvent() {
      return (EventType<SortEvent<C>>)SORT_EVENT;
   }

   public SortEvent(@NamedArg("source") C var1, @NamedArg("target") EventTarget var2) {
      super(var1, var2, sortEvent());
   }

   public C getSource() {
      return (C)super.getSource();
   }
}
