package org.controlsfx.validation;

import javafx.event.Event;
import javafx.event.EventType;

public class ValidateEvent extends Event {
   private static final long serialVersionUID = 1L;
   public static final EventType<Event> EVENT_TYPE = new EventType("VALIDATE_EVENT");

   public ValidateEvent() {
      super(EVENT_TYPE);
   }
}
