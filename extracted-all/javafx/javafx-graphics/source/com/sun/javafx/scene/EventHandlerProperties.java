package com.sun.javafx.scene;

import com.sun.javafx.event.EventHandlerManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.ZoomEvent;

public final class EventHandlerProperties {
   private final EventHandlerManager eventDispatcher;
   private final Object bean;
   private EventHandlerProperties.EventHandlerProperty<ContextMenuEvent> onMenuContextRequested;
   private EventHandlerProperties.EventHandlerProperty<MouseEvent> onMouseClicked;
   private EventHandlerProperties.EventHandlerProperty<MouseEvent> onMouseDragged;
   private EventHandlerProperties.EventHandlerProperty<MouseEvent> onMouseEntered;
   private EventHandlerProperties.EventHandlerProperty<MouseEvent> onMouseExited;
   private EventHandlerProperties.EventHandlerProperty<MouseEvent> onMouseMoved;
   private EventHandlerProperties.EventHandlerProperty<MouseEvent> onMousePressed;
   private EventHandlerProperties.EventHandlerProperty<MouseEvent> onMouseReleased;
   private EventHandlerProperties.EventHandlerProperty<MouseEvent> onDragDetected;
   private EventHandlerProperties.EventHandlerProperty<ScrollEvent> onScroll;
   private EventHandlerProperties.EventHandlerProperty<ScrollEvent> onScrollStarted;
   private EventHandlerProperties.EventHandlerProperty<ScrollEvent> onScrollFinished;
   private EventHandlerProperties.EventHandlerProperty<RotateEvent> onRotationStarted;
   private EventHandlerProperties.EventHandlerProperty<RotateEvent> onRotate;
   private EventHandlerProperties.EventHandlerProperty<RotateEvent> onRotationFinished;
   private EventHandlerProperties.EventHandlerProperty<ZoomEvent> onZoomStarted;
   private EventHandlerProperties.EventHandlerProperty<ZoomEvent> onZoom;
   private EventHandlerProperties.EventHandlerProperty<ZoomEvent> onZoomFinished;
   private EventHandlerProperties.EventHandlerProperty<SwipeEvent> onSwipeUp;
   private EventHandlerProperties.EventHandlerProperty<SwipeEvent> onSwipeDown;
   private EventHandlerProperties.EventHandlerProperty<SwipeEvent> onSwipeLeft;
   private EventHandlerProperties.EventHandlerProperty<SwipeEvent> onSwipeRight;
   private EventHandlerProperties.EventHandlerProperty<MouseDragEvent> onMouseDragOver;
   private EventHandlerProperties.EventHandlerProperty<MouseDragEvent> onMouseDragReleased;
   private EventHandlerProperties.EventHandlerProperty<MouseDragEvent> onMouseDragEntered;
   private EventHandlerProperties.EventHandlerProperty<MouseDragEvent> onMouseDragExited;
   private EventHandlerProperties.EventHandlerProperty<KeyEvent> onKeyPressed;
   private EventHandlerProperties.EventHandlerProperty<KeyEvent> onKeyReleased;
   private EventHandlerProperties.EventHandlerProperty<KeyEvent> onKeyTyped;
   private EventHandlerProperties.EventHandlerProperty<InputMethodEvent> onInputMethodTextChanged;
   private EventHandlerProperties.EventHandlerProperty<DragEvent> onDragEntered;
   private EventHandlerProperties.EventHandlerProperty<DragEvent> onDragExited;
   private EventHandlerProperties.EventHandlerProperty<DragEvent> onDragOver;
   private EventHandlerProperties.EventHandlerProperty<DragEvent> onDragDropped;
   private EventHandlerProperties.EventHandlerProperty<DragEvent> onDragDone;
   private EventHandlerProperties.EventHandlerProperty<TouchEvent> onTouchPressed;
   private EventHandlerProperties.EventHandlerProperty<TouchEvent> onTouchMoved;
   private EventHandlerProperties.EventHandlerProperty<TouchEvent> onTouchReleased;
   private EventHandlerProperties.EventHandlerProperty<TouchEvent> onTouchStationary;

   public EventHandlerProperties(EventHandlerManager var1, Object var2) {
      this.eventDispatcher = var1;
      this.bean = var2;
   }

   public final EventHandler<? super ContextMenuEvent> onContextMenuRequested() {
      return this.onMenuContextRequested == null ? null : (EventHandler)this.onMenuContextRequested.get();
   }

   public ObjectProperty<EventHandler<? super ContextMenuEvent>> onContextMenuRequestedProperty() {
      if (this.onMenuContextRequested == null) {
         this.onMenuContextRequested = new EventHandlerProperties.EventHandlerProperty<>(
            this.bean, "onMenuContextRequested", ContextMenuEvent.CONTEXT_MENU_REQUESTED
         );
      }

      return this.onMenuContextRequested;
   }

   public final EventHandler<? super MouseEvent> getOnMouseClicked() {
      return this.onMouseClicked == null ? null : (EventHandler)this.onMouseClicked.get();
   }

   public ObjectProperty<EventHandler<? super MouseEvent>> onMouseClickedProperty() {
      if (this.onMouseClicked == null) {
         this.onMouseClicked = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onMouseClicked", MouseEvent.MOUSE_CLICKED);
      }

      return this.onMouseClicked;
   }

   public final EventHandler<? super MouseEvent> getOnMouseDragged() {
      return this.onMouseDragged == null ? null : (EventHandler)this.onMouseDragged.get();
   }

   public ObjectProperty<EventHandler<? super MouseEvent>> onMouseDraggedProperty() {
      if (this.onMouseDragged == null) {
         this.onMouseDragged = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onMouseDragged", MouseEvent.MOUSE_DRAGGED);
      }

      return this.onMouseDragged;
   }

   public final EventHandler<? super MouseEvent> getOnMouseEntered() {
      return this.onMouseEntered == null ? null : (EventHandler)this.onMouseEntered.get();
   }

   public ObjectProperty<EventHandler<? super MouseEvent>> onMouseEnteredProperty() {
      if (this.onMouseEntered == null) {
         this.onMouseEntered = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onMouseEntered", MouseEvent.MOUSE_ENTERED);
      }

      return this.onMouseEntered;
   }

   public final EventHandler<? super MouseEvent> getOnMouseExited() {
      return this.onMouseExited == null ? null : (EventHandler)this.onMouseExited.get();
   }

   public ObjectProperty<EventHandler<? super MouseEvent>> onMouseExitedProperty() {
      if (this.onMouseExited == null) {
         this.onMouseExited = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onMouseExited", MouseEvent.MOUSE_EXITED);
      }

      return this.onMouseExited;
   }

   public final EventHandler<? super MouseEvent> getOnMouseMoved() {
      return this.onMouseMoved == null ? null : (EventHandler)this.onMouseMoved.get();
   }

   public ObjectProperty<EventHandler<? super MouseEvent>> onMouseMovedProperty() {
      if (this.onMouseMoved == null) {
         this.onMouseMoved = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onMouseMoved", MouseEvent.MOUSE_MOVED);
      }

      return this.onMouseMoved;
   }

   public final EventHandler<? super MouseEvent> getOnMousePressed() {
      return this.onMousePressed == null ? null : (EventHandler)this.onMousePressed.get();
   }

   public ObjectProperty<EventHandler<? super MouseEvent>> onMousePressedProperty() {
      if (this.onMousePressed == null) {
         this.onMousePressed = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onMousePressed", MouseEvent.MOUSE_PRESSED);
      }

      return this.onMousePressed;
   }

   public final EventHandler<? super MouseEvent> getOnMouseReleased() {
      return this.onMouseReleased == null ? null : (EventHandler)this.onMouseReleased.get();
   }

   public ObjectProperty<EventHandler<? super MouseEvent>> onMouseReleasedProperty() {
      if (this.onMouseReleased == null) {
         this.onMouseReleased = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onMouseReleased", MouseEvent.MOUSE_RELEASED);
      }

      return this.onMouseReleased;
   }

   public final EventHandler<? super MouseEvent> getOnDragDetected() {
      return this.onDragDetected == null ? null : (EventHandler)this.onDragDetected.get();
   }

   public ObjectProperty<EventHandler<? super MouseEvent>> onDragDetectedProperty() {
      if (this.onDragDetected == null) {
         this.onDragDetected = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onDragDetected", MouseEvent.DRAG_DETECTED);
      }

      return this.onDragDetected;
   }

   public final EventHandler<? super ScrollEvent> getOnScroll() {
      return this.onScroll == null ? null : (EventHandler)this.onScroll.get();
   }

   public ObjectProperty<EventHandler<? super ScrollEvent>> onScrollProperty() {
      if (this.onScroll == null) {
         this.onScroll = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onScroll", ScrollEvent.SCROLL);
      }

      return this.onScroll;
   }

   public final EventHandler<? super ScrollEvent> getOnScrollStarted() {
      return this.onScrollStarted == null ? null : (EventHandler)this.onScrollStarted.get();
   }

   public ObjectProperty<EventHandler<? super ScrollEvent>> onScrollStartedProperty() {
      if (this.onScrollStarted == null) {
         this.onScrollStarted = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onScrollStarted", ScrollEvent.SCROLL_STARTED);
      }

      return this.onScrollStarted;
   }

   public final EventHandler<? super ScrollEvent> getOnScrollFinished() {
      return this.onScrollFinished == null ? null : (EventHandler)this.onScrollFinished.get();
   }

   public ObjectProperty<EventHandler<? super ScrollEvent>> onScrollFinishedProperty() {
      if (this.onScrollFinished == null) {
         this.onScrollFinished = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onScrollFinished", ScrollEvent.SCROLL_FINISHED);
      }

      return this.onScrollFinished;
   }

   public final EventHandler<? super RotateEvent> getOnRotationStarted() {
      return this.onRotationStarted == null ? null : (EventHandler)this.onRotationStarted.get();
   }

   public ObjectProperty<EventHandler<? super RotateEvent>> onRotationStartedProperty() {
      if (this.onRotationStarted == null) {
         this.onRotationStarted = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onRotationStarted", RotateEvent.ROTATION_STARTED);
      }

      return this.onRotationStarted;
   }

   public final EventHandler<? super RotateEvent> getOnRotate() {
      return this.onRotate == null ? null : (EventHandler)this.onRotate.get();
   }

   public ObjectProperty<EventHandler<? super RotateEvent>> onRotateProperty() {
      if (this.onRotate == null) {
         this.onRotate = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onRotate", RotateEvent.ROTATE);
      }

      return this.onRotate;
   }

   public final EventHandler<? super RotateEvent> getOnRotationFinished() {
      return this.onRotationFinished == null ? null : (EventHandler)this.onRotationFinished.get();
   }

   public ObjectProperty<EventHandler<? super RotateEvent>> onRotationFinishedProperty() {
      if (this.onRotationFinished == null) {
         this.onRotationFinished = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onRotationFinished", RotateEvent.ROTATION_FINISHED);
      }

      return this.onRotationFinished;
   }

   public final EventHandler<? super ZoomEvent> getOnZoomStarted() {
      return this.onZoomStarted == null ? null : (EventHandler)this.onZoomStarted.get();
   }

   public ObjectProperty<EventHandler<? super ZoomEvent>> onZoomStartedProperty() {
      if (this.onZoomStarted == null) {
         this.onZoomStarted = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onZoomStarted", ZoomEvent.ZOOM_STARTED);
      }

      return this.onZoomStarted;
   }

   public final EventHandler<? super ZoomEvent> getOnZoom() {
      return this.onZoom == null ? null : (EventHandler)this.onZoom.get();
   }

   public ObjectProperty<EventHandler<? super ZoomEvent>> onZoomProperty() {
      if (this.onZoom == null) {
         this.onZoom = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onZoom", ZoomEvent.ZOOM);
      }

      return this.onZoom;
   }

   public final EventHandler<? super ZoomEvent> getOnZoomFinished() {
      return this.onZoomFinished == null ? null : (EventHandler)this.onZoomFinished.get();
   }

   public ObjectProperty<EventHandler<? super ZoomEvent>> onZoomFinishedProperty() {
      if (this.onZoomFinished == null) {
         this.onZoomFinished = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onZoomFinished", ZoomEvent.ZOOM_FINISHED);
      }

      return this.onZoomFinished;
   }

   public final EventHandler<? super SwipeEvent> getOnSwipeUp() {
      return this.onSwipeUp == null ? null : (EventHandler)this.onSwipeUp.get();
   }

   public ObjectProperty<EventHandler<? super SwipeEvent>> onSwipeUpProperty() {
      if (this.onSwipeUp == null) {
         this.onSwipeUp = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onSwipeUp", SwipeEvent.SWIPE_UP);
      }

      return this.onSwipeUp;
   }

   public final EventHandler<? super SwipeEvent> getOnSwipeDown() {
      return this.onSwipeDown == null ? null : (EventHandler)this.onSwipeDown.get();
   }

   public ObjectProperty<EventHandler<? super SwipeEvent>> onSwipeDownProperty() {
      if (this.onSwipeDown == null) {
         this.onSwipeDown = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onSwipeDown", SwipeEvent.SWIPE_DOWN);
      }

      return this.onSwipeDown;
   }

   public final EventHandler<? super SwipeEvent> getOnSwipeLeft() {
      return this.onSwipeLeft == null ? null : (EventHandler)this.onSwipeLeft.get();
   }

   public ObjectProperty<EventHandler<? super SwipeEvent>> onSwipeLeftProperty() {
      if (this.onSwipeLeft == null) {
         this.onSwipeLeft = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onSwipeLeft", SwipeEvent.SWIPE_LEFT);
      }

      return this.onSwipeLeft;
   }

   public final EventHandler<? super SwipeEvent> getOnSwipeRight() {
      return this.onSwipeRight == null ? null : (EventHandler)this.onSwipeRight.get();
   }

   public ObjectProperty<EventHandler<? super SwipeEvent>> onSwipeRightProperty() {
      if (this.onSwipeRight == null) {
         this.onSwipeRight = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onSwipeRight", SwipeEvent.SWIPE_RIGHT);
      }

      return this.onSwipeRight;
   }

   public final EventHandler<? super MouseDragEvent> getOnMouseDragOver() {
      return this.onMouseDragOver == null ? null : (EventHandler)this.onMouseDragOver.get();
   }

   public ObjectProperty<EventHandler<? super MouseDragEvent>> onMouseDragOverProperty() {
      if (this.onMouseDragOver == null) {
         this.onMouseDragOver = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onMouseDragOver", MouseDragEvent.MOUSE_DRAG_OVER);
      }

      return this.onMouseDragOver;
   }

   public final EventHandler<? super MouseDragEvent> getOnMouseDragReleased() {
      return this.onMouseDragReleased == null ? null : (EventHandler)this.onMouseDragReleased.get();
   }

   public ObjectProperty<EventHandler<? super MouseDragEvent>> onMouseDragReleasedProperty() {
      if (this.onMouseDragReleased == null) {
         this.onMouseDragReleased = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onMouseDragReleased", MouseDragEvent.MOUSE_DRAG_RELEASED);
      }

      return this.onMouseDragReleased;
   }

   public final EventHandler<? super MouseDragEvent> getOnMouseDragEntered() {
      return this.onMouseDragEntered == null ? null : (EventHandler)this.onMouseDragEntered.get();
   }

   public ObjectProperty<EventHandler<? super MouseDragEvent>> onMouseDragEnteredProperty() {
      if (this.onMouseDragEntered == null) {
         this.onMouseDragEntered = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onMouseDragEntered", MouseDragEvent.MOUSE_DRAG_ENTERED);
      }

      return this.onMouseDragEntered;
   }

   public final EventHandler<? super MouseDragEvent> getOnMouseDragExited() {
      return this.onMouseDragExited == null ? null : (EventHandler)this.onMouseDragExited.get();
   }

   public ObjectProperty<EventHandler<? super MouseDragEvent>> onMouseDragExitedProperty() {
      if (this.onMouseDragExited == null) {
         this.onMouseDragExited = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onMouseDragExited", MouseDragEvent.MOUSE_DRAG_EXITED);
      }

      return this.onMouseDragExited;
   }

   public final EventHandler<? super KeyEvent> getOnKeyPressed() {
      return this.onKeyPressed == null ? null : (EventHandler)this.onKeyPressed.get();
   }

   public ObjectProperty<EventHandler<? super KeyEvent>> onKeyPressedProperty() {
      if (this.onKeyPressed == null) {
         this.onKeyPressed = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onKeyPressed", KeyEvent.KEY_PRESSED);
      }

      return this.onKeyPressed;
   }

   public final EventHandler<? super KeyEvent> getOnKeyReleased() {
      return this.onKeyReleased == null ? null : (EventHandler)this.onKeyReleased.get();
   }

   public ObjectProperty<EventHandler<? super KeyEvent>> onKeyReleasedProperty() {
      if (this.onKeyReleased == null) {
         this.onKeyReleased = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onKeyReleased", KeyEvent.KEY_RELEASED);
      }

      return this.onKeyReleased;
   }

   public final EventHandler<? super KeyEvent> getOnKeyTyped() {
      return this.onKeyTyped == null ? null : (EventHandler)this.onKeyTyped.get();
   }

   public ObjectProperty<EventHandler<? super KeyEvent>> onKeyTypedProperty() {
      if (this.onKeyTyped == null) {
         this.onKeyTyped = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onKeyTyped", KeyEvent.KEY_TYPED);
      }

      return this.onKeyTyped;
   }

   public final EventHandler<? super InputMethodEvent> getOnInputMethodTextChanged() {
      return this.onInputMethodTextChanged == null ? null : (EventHandler)this.onInputMethodTextChanged.get();
   }

   public ObjectProperty<EventHandler<? super InputMethodEvent>> onInputMethodTextChangedProperty() {
      if (this.onInputMethodTextChanged == null) {
         this.onInputMethodTextChanged = new EventHandlerProperties.EventHandlerProperty<>(
            this.bean, "onInputMethodTextChanged", InputMethodEvent.INPUT_METHOD_TEXT_CHANGED
         );
      }

      return this.onInputMethodTextChanged;
   }

   public final EventHandler<? super DragEvent> getOnDragEntered() {
      return this.onDragEntered == null ? null : (EventHandler)this.onDragEntered.get();
   }

   public ObjectProperty<EventHandler<? super DragEvent>> onDragEnteredProperty() {
      if (this.onDragEntered == null) {
         this.onDragEntered = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onDragEntered", DragEvent.DRAG_ENTERED);
      }

      return this.onDragEntered;
   }

   public final EventHandler<? super DragEvent> getOnDragExited() {
      return this.onDragExited == null ? null : (EventHandler)this.onDragExited.get();
   }

   public ObjectProperty<EventHandler<? super DragEvent>> onDragExitedProperty() {
      if (this.onDragExited == null) {
         this.onDragExited = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onDragExited", DragEvent.DRAG_EXITED);
      }

      return this.onDragExited;
   }

   public final EventHandler<? super DragEvent> getOnDragOver() {
      return this.onDragOver == null ? null : (EventHandler)this.onDragOver.get();
   }

   public ObjectProperty<EventHandler<? super DragEvent>> onDragOverProperty() {
      if (this.onDragOver == null) {
         this.onDragOver = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onDragOver", DragEvent.DRAG_OVER);
      }

      return this.onDragOver;
   }

   public final EventHandler<? super DragEvent> getOnDragDropped() {
      return this.onDragDropped == null ? null : (EventHandler)this.onDragDropped.get();
   }

   public ObjectProperty<EventHandler<? super DragEvent>> onDragDroppedProperty() {
      if (this.onDragDropped == null) {
         this.onDragDropped = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onDragDropped", DragEvent.DRAG_DROPPED);
      }

      return this.onDragDropped;
   }

   public final EventHandler<? super DragEvent> getOnDragDone() {
      return this.onDragDone == null ? null : (EventHandler)this.onDragDone.get();
   }

   public ObjectProperty<EventHandler<? super DragEvent>> onDragDoneProperty() {
      if (this.onDragDone == null) {
         this.onDragDone = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onDragDone", DragEvent.DRAG_DONE);
      }

      return this.onDragDone;
   }

   public final EventHandler<? super TouchEvent> getOnTouchPressed() {
      return this.onTouchPressed == null ? null : (EventHandler)this.onTouchPressed.get();
   }

   public ObjectProperty<EventHandler<? super TouchEvent>> onTouchPressedProperty() {
      if (this.onTouchPressed == null) {
         this.onTouchPressed = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onTouchPressed", TouchEvent.TOUCH_PRESSED);
      }

      return this.onTouchPressed;
   }

   public final EventHandler<? super TouchEvent> getOnTouchMoved() {
      return this.onTouchMoved == null ? null : (EventHandler)this.onTouchMoved.get();
   }

   public ObjectProperty<EventHandler<? super TouchEvent>> onTouchMovedProperty() {
      if (this.onTouchMoved == null) {
         this.onTouchMoved = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onTouchMoved", TouchEvent.TOUCH_MOVED);
      }

      return this.onTouchMoved;
   }

   public final EventHandler<? super TouchEvent> getOnTouchReleased() {
      return this.onTouchReleased == null ? null : (EventHandler)this.onTouchReleased.get();
   }

   public ObjectProperty<EventHandler<? super TouchEvent>> onTouchReleasedProperty() {
      if (this.onTouchReleased == null) {
         this.onTouchReleased = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onTouchReleased", TouchEvent.TOUCH_RELEASED);
      }

      return this.onTouchReleased;
   }

   public final EventHandler<? super TouchEvent> getOnTouchStationary() {
      return this.onTouchStationary == null ? null : (EventHandler)this.onTouchStationary.get();
   }

   public ObjectProperty<EventHandler<? super TouchEvent>> onTouchStationaryProperty() {
      if (this.onTouchStationary == null) {
         this.onTouchStationary = new EventHandlerProperties.EventHandlerProperty<>(this.bean, "onTouchStationary", TouchEvent.TOUCH_STATIONARY);
      }

      return this.onTouchStationary;
   }

   private final class EventHandlerProperty<T extends Event> extends SimpleObjectProperty<EventHandler<? super T>> {
      private final EventType<T> eventType;

      public EventHandlerProperty(Object var2, String var3, EventType<T> var4) {
         super(var2, var3);
         this.eventType = var4;
      }

      protected void invalidated() {
         EventHandlerProperties.this.eventDispatcher.setEventHandler(this.eventType, (EventHandler)this.get());
      }
   }
}
