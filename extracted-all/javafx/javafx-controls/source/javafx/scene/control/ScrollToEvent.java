package javafx.scene.control;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public class ScrollToEvent<T> extends Event {
   public static final EventType<ScrollToEvent> ANY = new EventType(Event.ANY, "SCROLL_TO");
   private static final EventType<ScrollToEvent<Integer>> SCROLL_TO_TOP_INDEX = new EventType(ANY, "SCROLL_TO_TOP_INDEX");
   private static final EventType<?> SCROLL_TO_COLUMN = new EventType(ANY, "SCROLL_TO_COLUMN");
   private static final long serialVersionUID = -8557345736849482516L;
   private final T scrollTarget;

   public static EventType<ScrollToEvent<Integer>> scrollToTopIndex() {
      return SCROLL_TO_TOP_INDEX;
   }

   public static <T extends TableColumnBase<?, ?>> EventType<ScrollToEvent<T>> scrollToColumn() {
      return (EventType<ScrollToEvent<T>>)SCROLL_TO_COLUMN;
   }

   public ScrollToEvent(
      @NamedArg("source") Object var1,
      @NamedArg("target") EventTarget var2,
      @NamedArg("type") EventType<ScrollToEvent<T>> var3,
      @NamedArg("scrollTarget") T var4
   ) {
      super(var1, var2, var3);

      assert var4 != null;

      this.scrollTarget = (T)var4;
   }

   public T getScrollTarget() {
      return this.scrollTarget;
   }
}
