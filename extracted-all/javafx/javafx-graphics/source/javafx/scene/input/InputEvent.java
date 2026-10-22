package javafx.scene.input;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public class InputEvent extends Event {
   private static final long serialVersionUID = 20121107L;
   public static final EventType<InputEvent> ANY = new EventType(Event.ANY, "INPUT");

   public InputEvent(@NamedArg("eventType") EventType<? extends InputEvent> var1) {
      super(var1);
   }

   public InputEvent(@NamedArg("source") Object var1, @NamedArg("target") EventTarget var2, @NamedArg("eventType") EventType<? extends InputEvent> var3) {
      super(var1, var2, var3);
   }

   public EventType<? extends InputEvent> getEventType() {
      return super.getEventType();
   }
}
