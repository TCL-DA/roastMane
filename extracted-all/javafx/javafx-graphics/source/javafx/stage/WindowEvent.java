package javafx.stage;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public class WindowEvent extends Event {
   private static final long serialVersionUID = 20121107L;
   public static final EventType<WindowEvent> ANY = new EventType(Event.ANY, "WINDOW");
   public static final EventType<WindowEvent> WINDOW_SHOWING = new EventType(ANY, "WINDOW_SHOWING");
   public static final EventType<WindowEvent> WINDOW_SHOWN = new EventType(ANY, "WINDOW_SHOWN");
   public static final EventType<WindowEvent> WINDOW_HIDING = new EventType(ANY, "WINDOW_HIDING");
   public static final EventType<WindowEvent> WINDOW_HIDDEN = new EventType(ANY, "WINDOW_HIDDEN");
   public static final EventType<WindowEvent> WINDOW_CLOSE_REQUEST = new EventType(ANY, "WINDOW_CLOSE_REQUEST");

   public WindowEvent(@NamedArg("source") Window var1, @NamedArg("eventType") EventType<? extends Event> var2) {
      super(var1, var1, var2);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("WindowEvent [");
      var1.append("source = ").append(this.getSource());
      var1.append(", target = ").append(this.getTarget());
      var1.append(", eventType = ").append(this.getEventType());
      var1.append(", consumed = ").append(this.isConsumed());
      return var1.append("]").toString();
   }

   public WindowEvent copyFor(Object var1, EventTarget var2) {
      return (WindowEvent)super.copyFor(var1, var2);
   }

   public WindowEvent copyFor(Object var1, EventTarget var2, EventType<WindowEvent> var3) {
      WindowEvent var4 = this.copyFor(var1, var2);
      var4.eventType = var3;
      return var4;
   }

   public EventType<WindowEvent> getEventType() {
      return super.getEventType();
   }
}
