package com.sun.javafx.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.event.Event;
import javafx.event.EventDispatcher;
import javafx.event.EventType;

public class EventRedirector extends BasicEventDispatcher {
   private final EventDispatchChainImpl eventDispatchChain;
   private final List<EventDispatcher> eventDispatchers = new CopyOnWriteArrayList<>();
   private final Object eventSource;

   public EventRedirector(Object var1) {
      this.eventDispatchChain = new EventDispatchChainImpl();
      this.eventSource = var1;
   }

   protected void handleRedirectedEvent(Object var1, Event var2) {
   }

   public final void addEventDispatcher(EventDispatcher var1) {
      this.eventDispatchers.add(var1);
   }

   public final void removeEventDispatcher(EventDispatcher var1) {
      this.eventDispatchers.remove(var1);
   }

   @Override
   public final Event dispatchCapturingEvent(Event var1) {
      EventType var2 = var1.getEventType();
      if (var2 == DirectEvent.DIRECT) {
         var1 = ((DirectEvent)var1).getOriginalEvent();
      } else {
         this.redirectEvent(var1);
         if (var2 == RedirectedEvent.REDIRECTED) {
            this.handleRedirectedEvent(var1.getSource(), ((RedirectedEvent)var1).getOriginalEvent());
         }
      }

      return var1;
   }

   private void redirectEvent(Event var1) {
      if (!this.eventDispatchers.isEmpty()) {
         RedirectedEvent var2 = var1.getEventType() == RedirectedEvent.REDIRECTED ? (RedirectedEvent)var1 : new RedirectedEvent(var1, this.eventSource, null);

         for (EventDispatcher var4 : this.eventDispatchers) {
            this.eventDispatchChain.reset();
            var4.dispatchEvent(var2, this.eventDispatchChain);
         }
      }
   }
}
