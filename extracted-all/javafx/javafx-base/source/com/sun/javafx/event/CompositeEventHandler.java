package com.sun.javafx.event;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;

public final class CompositeEventHandler<T extends Event> {
   private CompositeEventHandler.EventProcessorRecord<T> firstRecord;
   private CompositeEventHandler.EventProcessorRecord<T> lastRecord;
   private EventHandler<? super T> eventHandler;

   public void setEventHandler(EventHandler<? super T> var1) {
      this.eventHandler = var1;
   }

   public EventHandler<? super T> getEventHandler() {
      return this.eventHandler;
   }

   public void addEventHandler(EventHandler<? super T> var1) {
      if (this.find(var1, false) == null) {
         this.append(this.lastRecord, this.createEventHandlerRecord(var1));
      }
   }

   public void removeEventHandler(EventHandler<? super T> var1) {
      CompositeEventHandler.EventProcessorRecord var2 = this.find(var1, false);
      if (var2 != null) {
         this.remove(var2);
      }
   }

   public void addEventFilter(EventHandler<? super T> var1) {
      if (this.find(var1, true) == null) {
         this.append(this.lastRecord, this.createEventFilterRecord(var1));
      }
   }

   public void removeEventFilter(EventHandler<? super T> var1) {
      CompositeEventHandler.EventProcessorRecord var2 = this.find(var1, true);
      if (var2 != null) {
         this.remove(var2);
      }
   }

   public void dispatchBubblingEvent(Event var1) {
      Event var2 = var1;

      for (CompositeEventHandler.EventProcessorRecord var3 = this.firstRecord; var3 != null; var3 = var3.nextRecord) {
         if (var3.isDisconnected()) {
            this.remove(var3);
         } else {
            var3.handleBubblingEvent(var2);
         }
      }

      if (this.eventHandler != null) {
         this.eventHandler.handle((T)var2);
      }
   }

   public void dispatchCapturingEvent(Event var1) {
      Event var2 = var1;

      for (CompositeEventHandler.EventProcessorRecord var3 = this.firstRecord; var3 != null; var3 = var3.nextRecord) {
         if (var3.isDisconnected()) {
            this.remove(var3);
         } else {
            var3.handleCapturingEvent(var2);
         }
      }
   }

   public boolean hasFilter() {
      return this.find(true);
   }

   public boolean hasHandler() {
      return this.getEventHandler() != null ? true : this.find(false);
   }

   boolean containsHandler(EventHandler<? super T> var1) {
      return this.find(var1, false) != null;
   }

   boolean containsFilter(EventHandler<? super T> var1) {
      return this.find(var1, true) != null;
   }

   private CompositeEventHandler.EventProcessorRecord<T> createEventHandlerRecord(EventHandler<? super T> var1) {
      return (CompositeEventHandler.EventProcessorRecord<T>)(var1 instanceof WeakEventHandler
         ? new CompositeEventHandler.WeakEventHandlerRecord<>((WeakEventHandler<? super T>)var1)
         : new CompositeEventHandler.NormalEventHandlerRecord<>(var1));
   }

   private CompositeEventHandler.EventProcessorRecord<T> createEventFilterRecord(EventHandler<? super T> var1) {
      return (CompositeEventHandler.EventProcessorRecord<T>)(var1 instanceof WeakEventHandler
         ? new CompositeEventHandler.WeakEventFilterRecord<>((WeakEventHandler<? super T>)var1)
         : new CompositeEventHandler.NormalEventFilterRecord<>(var1));
   }

   private void remove(CompositeEventHandler.EventProcessorRecord<T> var1) {
      CompositeEventHandler.EventProcessorRecord var2 = var1.prevRecord;
      CompositeEventHandler.EventProcessorRecord var3 = var1.nextRecord;
      if (var2 != null) {
         var2.nextRecord = var3;
      } else {
         this.firstRecord = var3;
      }

      if (var3 != null) {
         var3.prevRecord = var2;
      } else {
         this.lastRecord = var2;
      }
   }

   private void append(CompositeEventHandler.EventProcessorRecord<T> var1, CompositeEventHandler.EventProcessorRecord<T> var2) {
      CompositeEventHandler.EventProcessorRecord var3;
      if (var1 != null) {
         var3 = var1.nextRecord;
         var1.nextRecord = var2;
      } else {
         var3 = this.firstRecord;
         this.firstRecord = var2;
      }

      if (var3 != null) {
         var3.prevRecord = var2;
      } else {
         this.lastRecord = var2;
      }

      var2.prevRecord = var1;
      var2.nextRecord = var3;
   }

   private CompositeEventHandler.EventProcessorRecord<T> find(EventHandler<? super T> var1, boolean var2) {
      for (CompositeEventHandler.EventProcessorRecord var3 = this.firstRecord; var3 != null; var3 = var3.nextRecord) {
         if (var3.isDisconnected()) {
            this.remove(var3);
         } else if (var3.stores(var1, var2)) {
            return var3;
         }
      }

      return null;
   }

   private boolean find(boolean var1) {
      for (CompositeEventHandler.EventProcessorRecord var2 = this.firstRecord; var2 != null; var2 = var2.nextRecord) {
         if (var2.isDisconnected()) {
            this.remove(var2);
         } else if (var1 == var2.isFilter()) {
            return true;
         }
      }

      return false;
   }

   private abstract static class EventProcessorRecord<T extends Event> {
      private CompositeEventHandler.EventProcessorRecord<T> nextRecord;
      private CompositeEventHandler.EventProcessorRecord<T> prevRecord;

      public abstract boolean stores(EventHandler<? super T> var1, boolean var2);

      public abstract boolean isFilter();

      public abstract void handleBubblingEvent(T var1);

      public abstract void handleCapturingEvent(T var1);

      public abstract boolean isDisconnected();
   }

   private static final class NormalEventFilterRecord<T extends Event> extends CompositeEventHandler.EventProcessorRecord<T> {
      private final EventHandler<? super T> eventFilter;

      public NormalEventFilterRecord(EventHandler<? super T> var1) {
         this.eventFilter = var1;
      }

      @Override
      public boolean stores(EventHandler<? super T> var1, boolean var2) {
         return var2 == this.isFilter() && this.eventFilter == var1;
      }

      @Override
      public boolean isFilter() {
         return true;
      }

      @Override
      public void handleBubblingEvent(T var1) {
      }

      @Override
      public void handleCapturingEvent(T var1) {
         this.eventFilter.handle((T)var1);
      }

      @Override
      public boolean isDisconnected() {
         return false;
      }
   }

   private static final class NormalEventHandlerRecord<T extends Event> extends CompositeEventHandler.EventProcessorRecord<T> {
      private final EventHandler<? super T> eventHandler;

      public NormalEventHandlerRecord(EventHandler<? super T> var1) {
         this.eventHandler = var1;
      }

      @Override
      public boolean stores(EventHandler<? super T> var1, boolean var2) {
         return var2 == this.isFilter() && this.eventHandler == var1;
      }

      @Override
      public boolean isFilter() {
         return false;
      }

      @Override
      public void handleBubblingEvent(T var1) {
         this.eventHandler.handle((T)var1);
      }

      @Override
      public void handleCapturingEvent(T var1) {
      }

      @Override
      public boolean isDisconnected() {
         return false;
      }
   }

   private static final class WeakEventFilterRecord<T extends Event> extends CompositeEventHandler.EventProcessorRecord<T> {
      private final WeakEventHandler<? super T> weakEventFilter;

      public WeakEventFilterRecord(WeakEventHandler<? super T> var1) {
         this.weakEventFilter = var1;
      }

      @Override
      public boolean stores(EventHandler<? super T> var1, boolean var2) {
         return var2 == this.isFilter() && this.weakEventFilter == var1;
      }

      @Override
      public boolean isFilter() {
         return true;
      }

      @Override
      public void handleBubblingEvent(T var1) {
      }

      @Override
      public void handleCapturingEvent(T var1) {
         this.weakEventFilter.handle((T)var1);
      }

      @Override
      public boolean isDisconnected() {
         return this.weakEventFilter.wasGarbageCollected();
      }
   }

   private static final class WeakEventHandlerRecord<T extends Event> extends CompositeEventHandler.EventProcessorRecord<T> {
      private final WeakEventHandler<? super T> weakEventHandler;

      public WeakEventHandlerRecord(WeakEventHandler<? super T> var1) {
         this.weakEventHandler = var1;
      }

      @Override
      public boolean stores(EventHandler<? super T> var1, boolean var2) {
         return var2 == this.isFilter() && this.weakEventHandler == var1;
      }

      @Override
      public boolean isFilter() {
         return false;
      }

      @Override
      public void handleBubblingEvent(T var1) {
         this.weakEventHandler.handle((T)var1);
      }

      @Override
      public void handleCapturingEvent(T var1) {
      }

      @Override
      public boolean isDisconnected() {
         return this.weakEventHandler.wasGarbageCollected();
      }
   }
}
