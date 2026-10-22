package impl.org.controlsfx.tableview2;

import javafx.beans.NamedArg;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.control.SortEvent;

public class SortUtils {
   public static class SortEndedEvent<C> extends SortEvent<C> {
      public static final EventType<?> SORT_ENDED_EVENT = new EventType(SortEvent.ANY, "SORT_ENDED_EVENT");

      public SortEndedEvent(@NamedArg("source") C source, @NamedArg("target") EventTarget target) {
         super(source, target);
         this.eventType = SORT_ENDED_EVENT;
      }
   }

   public static class SortStartedEvent<C> extends SortEvent<C> {
      public static final EventType<?> SORT_STARTED_EVENT = new EventType(SortEvent.ANY, "SORT_STARTED_EVENT");

      public SortStartedEvent(@NamedArg("source") C source, @NamedArg("target") EventTarget target) {
         super(source, target);
         this.eventType = SORT_STARTED_EVENT;
      }
   }
}
