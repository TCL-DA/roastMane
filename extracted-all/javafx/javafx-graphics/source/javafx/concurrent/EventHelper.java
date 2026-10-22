package javafx.concurrent;

import com.sun.javafx.event.EventHandlerManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;

class EventHelper {
   private final EventTarget target;
   private final ObjectProperty<EventHandler<WorkerStateEvent>> onReady;
   private final ObjectProperty<EventHandler<WorkerStateEvent>> onScheduled;
   private final ObjectProperty<EventHandler<WorkerStateEvent>> onRunning;
   private final ObjectProperty<EventHandler<WorkerStateEvent>> onSucceeded;
   private final ObjectProperty<EventHandler<WorkerStateEvent>> onCancelled;
   private final ObjectProperty<EventHandler<WorkerStateEvent>> onFailed;
   private EventHandlerManager internalEventDispatcher;

   final ObjectProperty<EventHandler<WorkerStateEvent>> onReadyProperty() {
      return this.onReady;
   }

   final EventHandler<WorkerStateEvent> getOnReady() {
      return (EventHandler<WorkerStateEvent>)this.onReady.get();
   }

   final void setOnReady(EventHandler<WorkerStateEvent> var1) {
      this.onReady.set(var1);
   }

   final ObjectProperty<EventHandler<WorkerStateEvent>> onScheduledProperty() {
      return this.onScheduled;
   }

   final EventHandler<WorkerStateEvent> getOnScheduled() {
      return (EventHandler<WorkerStateEvent>)this.onScheduled.get();
   }

   final void setOnScheduled(EventHandler<WorkerStateEvent> var1) {
      this.onScheduled.set(var1);
   }

   final ObjectProperty<EventHandler<WorkerStateEvent>> onRunningProperty() {
      return this.onRunning;
   }

   final EventHandler<WorkerStateEvent> getOnRunning() {
      return (EventHandler<WorkerStateEvent>)this.onRunning.get();
   }

   final void setOnRunning(EventHandler<WorkerStateEvent> var1) {
      this.onRunning.set(var1);
   }

   final ObjectProperty<EventHandler<WorkerStateEvent>> onSucceededProperty() {
      return this.onSucceeded;
   }

   final EventHandler<WorkerStateEvent> getOnSucceeded() {
      return (EventHandler<WorkerStateEvent>)this.onSucceeded.get();
   }

   final void setOnSucceeded(EventHandler<WorkerStateEvent> var1) {
      this.onSucceeded.set(var1);
   }

   final ObjectProperty<EventHandler<WorkerStateEvent>> onCancelledProperty() {
      return this.onCancelled;
   }

   final EventHandler<WorkerStateEvent> getOnCancelled() {
      return (EventHandler<WorkerStateEvent>)this.onCancelled.get();
   }

   final void setOnCancelled(EventHandler<WorkerStateEvent> var1) {
      this.onCancelled.set(var1);
   }

   final ObjectProperty<EventHandler<WorkerStateEvent>> onFailedProperty() {
      return this.onFailed;
   }

   final EventHandler<WorkerStateEvent> getOnFailed() {
      return (EventHandler<WorkerStateEvent>)this.onFailed.get();
   }

   final void setOnFailed(EventHandler<WorkerStateEvent> var1) {
      this.onFailed.set(var1);
   }

   EventHelper(EventTarget var1) {
      this.target = var1;
      this.onReady = new SimpleObjectProperty<EventHandler<WorkerStateEvent>>(var1, "onReady") {
         protected void invalidated() {
            EventHandler var1 = (EventHandler)this.get();
            EventHelper.this.setEventHandler(WorkerStateEvent.WORKER_STATE_READY, var1);
         }
      };
      this.onScheduled = new SimpleObjectProperty<EventHandler<WorkerStateEvent>>(var1, "onScheduled") {
         protected void invalidated() {
            EventHandler var1 = (EventHandler)this.get();
            EventHelper.this.setEventHandler(WorkerStateEvent.WORKER_STATE_SCHEDULED, var1);
         }
      };
      this.onRunning = new SimpleObjectProperty<EventHandler<WorkerStateEvent>>(var1, "onRunning") {
         protected void invalidated() {
            EventHandler var1 = (EventHandler)this.get();
            EventHelper.this.setEventHandler(WorkerStateEvent.WORKER_STATE_RUNNING, var1);
         }
      };
      this.onSucceeded = new SimpleObjectProperty<EventHandler<WorkerStateEvent>>(var1, "onSucceeded") {
         protected void invalidated() {
            EventHandler var1 = (EventHandler)this.get();
            EventHelper.this.setEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, var1);
         }
      };
      this.onCancelled = new SimpleObjectProperty<EventHandler<WorkerStateEvent>>(var1, "onCancelled") {
         protected void invalidated() {
            EventHandler var1 = (EventHandler)this.get();
            EventHelper.this.setEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, var1);
         }
      };
      this.onFailed = new SimpleObjectProperty<EventHandler<WorkerStateEvent>>(var1, "onFailed") {
         protected void invalidated() {
            EventHandler var1 = (EventHandler)this.get();
            EventHelper.this.setEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, var1);
         }
      };
   }

   final <T extends Event> void addEventHandler(EventType<T> var1, EventHandler<? super T> var2) {
      this.getInternalEventDispatcher().addEventHandler(var1, var2);
   }

   final <T extends Event> void removeEventHandler(EventType<T> var1, EventHandler<? super T> var2) {
      this.getInternalEventDispatcher().removeEventHandler(var1, var2);
   }

   final <T extends Event> void addEventFilter(EventType<T> var1, EventHandler<? super T> var2) {
      this.getInternalEventDispatcher().addEventFilter(var1, var2);
   }

   final <T extends Event> void removeEventFilter(EventType<T> var1, EventHandler<? super T> var2) {
      this.getInternalEventDispatcher().removeEventFilter(var1, var2);
   }

   final <T extends Event> void setEventHandler(EventType<T> var1, EventHandler<? super T> var2) {
      this.getInternalEventDispatcher().setEventHandler(var1, var2);
   }

   private EventHandlerManager getInternalEventDispatcher() {
      if (this.internalEventDispatcher == null) {
         this.internalEventDispatcher = new EventHandlerManager(this.target);
      }

      return this.internalEventDispatcher;
   }

   final void fireEvent(Event var1) {
      Event.fireEvent(this.target, var1);
   }

   EventDispatchChain buildEventDispatchChain(EventDispatchChain var1) {
      return this.internalEventDispatcher == null ? var1 : var1.append(this.getInternalEventDispatcher());
   }
}
