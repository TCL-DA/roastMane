package javafx.scene.web;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventType;

public final class WebEvent<T> extends Event {
   public static final EventType<WebEvent> ANY = new EventType(Event.ANY, "WEB");
   public static final EventType<WebEvent> RESIZED = new EventType(ANY, "WEB_RESIZED");
   public static final EventType<WebEvent> STATUS_CHANGED = new EventType(ANY, "WEB_STATUS_CHANGED");
   public static final EventType<WebEvent> VISIBILITY_CHANGED = new EventType(ANY, "WEB_VISIBILITY_CHANGED");
   public static final EventType<WebEvent> ALERT = new EventType(ANY, "WEB_ALERT");
   private final T data;

   public WebEvent(@NamedArg("source") Object var1, @NamedArg("type") EventType<WebEvent> var2, @NamedArg("data") T var3) {
      super(var1, null, var2);
      this.data = (T)var3;
   }

   public T getData() {
      return this.data;
   }

   public String toString() {
      return String.format("WebEvent [source = %s, eventType = %s, data = %s]", this.getSource(), this.getEventType(), this.getData());
   }
}
