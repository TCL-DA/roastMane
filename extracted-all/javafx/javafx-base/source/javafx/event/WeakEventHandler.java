package javafx.event;

import java.lang.ref.WeakReference;
import javafx.beans.NamedArg;

public final class WeakEventHandler<T extends Event> implements EventHandler<T> {
   private final WeakReference<EventHandler<T>> weakRef;

   public WeakEventHandler(@NamedArg("eventHandler") EventHandler<T> var1) {
      this.weakRef = new WeakReference<>(var1);
   }

   public boolean wasGarbageCollected() {
      return this.weakRef.get() == null;
   }

   @Override
   public void handle(T var1) {
      EventHandler var2 = this.weakRef.get();
      if (var2 != null) {
         var2.handle(var1);
      }
   }

   void clear() {
      this.weakRef.clear();
   }
}
