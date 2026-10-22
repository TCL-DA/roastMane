package com.sun.javafx.fxml.builder.web;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.DepthTest;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Effect;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.transform.Transform;

@Deprecated
public abstract class NodeBuilder<B extends NodeBuilder<B>> {
   BitSet __set = new BitSet();
   private BlendMode blendMode;
   private boolean cache;
   private CacheHint cacheHint;
   private Node clip;
   private Cursor cursor;
   private DepthTest depthTest;
   private boolean disable;
   private Effect effect;
   private EventDispatcher eventDispatcher;
   private boolean focusTraversable;
   private String id;
   private InputMethodRequests inputMethodRequests;
   private double layoutX;
   private double layoutY;
   private boolean managed;
   private boolean mouseTransparent;
   private EventHandler<? super ContextMenuEvent> onContextMenuRequested;
   private EventHandler<? super MouseEvent> onDragDetected;
   private EventHandler<? super DragEvent> onDragDone;
   private EventHandler<? super DragEvent> onDragDropped;
   private EventHandler<? super DragEvent> onDragEntered;
   private EventHandler<? super DragEvent> onDragExited;
   private EventHandler<? super DragEvent> onDragOver;
   private EventHandler<? super InputMethodEvent> onInputMethodTextChanged;
   private EventHandler<? super KeyEvent> onKeyPressed;
   private EventHandler<? super KeyEvent> onKeyReleased;
   private EventHandler<? super KeyEvent> onKeyTyped;
   private EventHandler<? super MouseEvent> onMouseClicked;
   private EventHandler<? super MouseDragEvent> onMouseDragEntered;
   private EventHandler<? super MouseDragEvent> onMouseDragExited;
   private EventHandler<? super MouseEvent> onMouseDragged;
   private EventHandler<? super MouseDragEvent> onMouseDragOver;
   private EventHandler<? super MouseDragEvent> onMouseDragReleased;
   private EventHandler<? super MouseEvent> onMouseEntered;
   private EventHandler<? super MouseEvent> onMouseExited;
   private EventHandler<? super MouseEvent> onMouseMoved;
   private EventHandler<? super MouseEvent> onMousePressed;
   private EventHandler<? super MouseEvent> onMouseReleased;
   private EventHandler<? super RotateEvent> onRotate;
   private EventHandler<? super RotateEvent> onRotationFinished;
   private EventHandler<? super RotateEvent> onRotationStarted;
   private EventHandler<? super ScrollEvent> onScroll;
   private EventHandler<? super ScrollEvent> onScrollFinished;
   private EventHandler<? super ScrollEvent> onScrollStarted;
   private EventHandler<? super SwipeEvent> onSwipeDown;
   private EventHandler<? super SwipeEvent> onSwipeLeft;
   private EventHandler<? super SwipeEvent> onSwipeRight;
   private EventHandler<? super SwipeEvent> onSwipeUp;
   private EventHandler<? super TouchEvent> onTouchMoved;
   private EventHandler<? super TouchEvent> onTouchPressed;
   private EventHandler<? super TouchEvent> onTouchReleased;
   private EventHandler<? super TouchEvent> onTouchStationary;
   private EventHandler<? super ZoomEvent> onZoom;
   private EventHandler<? super ZoomEvent> onZoomFinished;
   private EventHandler<? super ZoomEvent> onZoomStarted;
   private double opacity;
   private boolean pickOnBounds;
   private double rotate;
   private Point3D rotationAxis;
   private double scaleX;
   private double scaleY;
   private double scaleZ;
   private String style;
   private Collection<? extends String> styleClass;
   private Collection<? extends Transform> transforms;
   private double translateX;
   private double translateY;
   private double translateZ;
   private Object userData;
   private boolean visible;

   protected NodeBuilder() {
   }

   private void __set(int var1) {
      this.__set.set(var1);
   }

   public void applyTo(Node var1) {
      BitSet var2 = this.__set;
      int var3 = -1;

      while ((var3 = var2.nextSetBit(var3 + 1)) >= 0) {
         switch (var3) {
            case 0:
               var1.setBlendMode(this.blendMode);
               break;
            case 1:
               var1.setCache(this.cache);
               break;
            case 2:
               var1.setCacheHint(this.cacheHint);
               break;
            case 3:
               var1.setClip(this.clip);
               break;
            case 4:
               var1.setCursor(this.cursor);
               break;
            case 5:
               var1.setDepthTest(this.depthTest);
               break;
            case 6:
               var1.setDisable(this.disable);
               break;
            case 7:
               var1.setEffect(this.effect);
               break;
            case 8:
               var1.setEventDispatcher(this.eventDispatcher);
               break;
            case 9:
               var1.setFocusTraversable(this.focusTraversable);
               break;
            case 10:
               var1.setId(this.id);
               break;
            case 11:
               var1.setInputMethodRequests(this.inputMethodRequests);
               break;
            case 12:
               var1.setLayoutX(this.layoutX);
               break;
            case 13:
               var1.setLayoutY(this.layoutY);
               break;
            case 14:
               var1.setManaged(this.managed);
               break;
            case 15:
               var1.setMouseTransparent(this.mouseTransparent);
               break;
            case 16:
               var1.setOnContextMenuRequested(this.onContextMenuRequested);
               break;
            case 17:
               var1.setOnDragDetected(this.onDragDetected);
               break;
            case 18:
               var1.setOnDragDone(this.onDragDone);
               break;
            case 19:
               var1.setOnDragDropped(this.onDragDropped);
               break;
            case 20:
               var1.setOnDragEntered(this.onDragEntered);
               break;
            case 21:
               var1.setOnDragExited(this.onDragExited);
               break;
            case 22:
               var1.setOnDragOver(this.onDragOver);
               break;
            case 23:
               var1.setOnInputMethodTextChanged(this.onInputMethodTextChanged);
               break;
            case 24:
               var1.setOnKeyPressed(this.onKeyPressed);
               break;
            case 25:
               var1.setOnKeyReleased(this.onKeyReleased);
               break;
            case 26:
               var1.setOnKeyTyped(this.onKeyTyped);
               break;
            case 27:
               var1.setOnMouseClicked(this.onMouseClicked);
               break;
            case 28:
               var1.setOnMouseDragEntered(this.onMouseDragEntered);
               break;
            case 29:
               var1.setOnMouseDragExited(this.onMouseDragExited);
               break;
            case 30:
               var1.setOnMouseDragged(this.onMouseDragged);
               break;
            case 31:
               var1.setOnMouseDragOver(this.onMouseDragOver);
               break;
            case 32:
               var1.setOnMouseDragReleased(this.onMouseDragReleased);
               break;
            case 33:
               var1.setOnMouseEntered(this.onMouseEntered);
               break;
            case 34:
               var1.setOnMouseExited(this.onMouseExited);
               break;
            case 35:
               var1.setOnMouseMoved(this.onMouseMoved);
               break;
            case 36:
               var1.setOnMousePressed(this.onMousePressed);
               break;
            case 37:
               var1.setOnMouseReleased(this.onMouseReleased);
               break;
            case 38:
               var1.setOnRotate(this.onRotate);
               break;
            case 39:
               var1.setOnRotationFinished(this.onRotationFinished);
               break;
            case 40:
               var1.setOnRotationStarted(this.onRotationStarted);
               break;
            case 41:
               var1.setOnScroll(this.onScroll);
               break;
            case 42:
               var1.setOnScrollFinished(this.onScrollFinished);
               break;
            case 43:
               var1.setOnScrollStarted(this.onScrollStarted);
               break;
            case 44:
               var1.setOnSwipeDown(this.onSwipeDown);
               break;
            case 45:
               var1.setOnSwipeLeft(this.onSwipeLeft);
               break;
            case 46:
               var1.setOnSwipeRight(this.onSwipeRight);
               break;
            case 47:
               var1.setOnSwipeUp(this.onSwipeUp);
               break;
            case 48:
               var1.setOnTouchMoved(this.onTouchMoved);
               break;
            case 49:
               var1.setOnTouchPressed(this.onTouchPressed);
               break;
            case 50:
               var1.setOnTouchReleased(this.onTouchReleased);
               break;
            case 51:
               var1.setOnTouchStationary(this.onTouchStationary);
               break;
            case 52:
               var1.setOnZoom(this.onZoom);
               break;
            case 53:
               var1.setOnZoomFinished(this.onZoomFinished);
               break;
            case 54:
               var1.setOnZoomStarted(this.onZoomStarted);
               break;
            case 55:
               var1.setOpacity(this.opacity);
               break;
            case 56:
               var1.setPickOnBounds(this.pickOnBounds);
               break;
            case 57:
               var1.setRotate(this.rotate);
               break;
            case 58:
               var1.setRotationAxis(this.rotationAxis);
               break;
            case 59:
               var1.setScaleX(this.scaleX);
               break;
            case 60:
               var1.setScaleY(this.scaleY);
               break;
            case 61:
               var1.setScaleZ(this.scaleZ);
               break;
            case 62:
               var1.setStyle(this.style);
               break;
            case 63:
               var1.getStyleClass().addAll(this.styleClass);
               break;
            case 64:
               var1.getTransforms().addAll(this.transforms);
               break;
            case 65:
               var1.setTranslateX(this.translateX);
               break;
            case 66:
               var1.setTranslateY(this.translateY);
               break;
            case 67:
               var1.setTranslateZ(this.translateZ);
               break;
            case 68:
               var1.setUserData(this.userData);
               break;
            case 69:
               var1.setVisible(this.visible);
         }
      }
   }

   public B blendMode(BlendMode var1) {
      this.blendMode = var1;
      this.__set(0);
      return (B)this;
   }

   public B cache(boolean var1) {
      this.cache = var1;
      this.__set(1);
      return (B)this;
   }

   public B cacheHint(CacheHint var1) {
      this.cacheHint = var1;
      this.__set(2);
      return (B)this;
   }

   public B clip(Node var1) {
      this.clip = var1;
      this.__set(3);
      return (B)this;
   }

   public B cursor(Cursor var1) {
      this.cursor = var1;
      this.__set(4);
      return (B)this;
   }

   public B depthTest(DepthTest var1) {
      this.depthTest = var1;
      this.__set(5);
      return (B)this;
   }

   public B disable(boolean var1) {
      this.disable = var1;
      this.__set(6);
      return (B)this;
   }

   public B effect(Effect var1) {
      this.effect = var1;
      this.__set(7);
      return (B)this;
   }

   public B eventDispatcher(EventDispatcher var1) {
      this.eventDispatcher = var1;
      this.__set(8);
      return (B)this;
   }

   public B focusTraversable(boolean var1) {
      this.focusTraversable = var1;
      this.__set(9);
      return (B)this;
   }

   public B id(String var1) {
      this.id = var1;
      this.__set(10);
      return (B)this;
   }

   public B inputMethodRequests(InputMethodRequests var1) {
      this.inputMethodRequests = var1;
      this.__set(11);
      return (B)this;
   }

   public B layoutX(double var1) {
      this.layoutX = var1;
      this.__set(12);
      return (B)this;
   }

   public B layoutY(double var1) {
      this.layoutY = var1;
      this.__set(13);
      return (B)this;
   }

   public B managed(boolean var1) {
      this.managed = var1;
      this.__set(14);
      return (B)this;
   }

   public B mouseTransparent(boolean var1) {
      this.mouseTransparent = var1;
      this.__set(15);
      return (B)this;
   }

   public B onContextMenuRequested(EventHandler<? super ContextMenuEvent> var1) {
      this.onContextMenuRequested = var1;
      this.__set(16);
      return (B)this;
   }

   public B onDragDetected(EventHandler<? super MouseEvent> var1) {
      this.onDragDetected = var1;
      this.__set(17);
      return (B)this;
   }

   public B onDragDone(EventHandler<? super DragEvent> var1) {
      this.onDragDone = var1;
      this.__set(18);
      return (B)this;
   }

   public B onDragDropped(EventHandler<? super DragEvent> var1) {
      this.onDragDropped = var1;
      this.__set(19);
      return (B)this;
   }

   public B onDragEntered(EventHandler<? super DragEvent> var1) {
      this.onDragEntered = var1;
      this.__set(20);
      return (B)this;
   }

   public B onDragExited(EventHandler<? super DragEvent> var1) {
      this.onDragExited = var1;
      this.__set(21);
      return (B)this;
   }

   public B onDragOver(EventHandler<? super DragEvent> var1) {
      this.onDragOver = var1;
      this.__set(22);
      return (B)this;
   }

   public B onInputMethodTextChanged(EventHandler<? super InputMethodEvent> var1) {
      this.onInputMethodTextChanged = var1;
      this.__set(23);
      return (B)this;
   }

   public B onKeyPressed(EventHandler<? super KeyEvent> var1) {
      this.onKeyPressed = var1;
      this.__set(24);
      return (B)this;
   }

   public B onKeyReleased(EventHandler<? super KeyEvent> var1) {
      this.onKeyReleased = var1;
      this.__set(25);
      return (B)this;
   }

   public B onKeyTyped(EventHandler<? super KeyEvent> var1) {
      this.onKeyTyped = var1;
      this.__set(26);
      return (B)this;
   }

   public B onMouseClicked(EventHandler<? super MouseEvent> var1) {
      this.onMouseClicked = var1;
      this.__set(27);
      return (B)this;
   }

   public B onMouseDragEntered(EventHandler<? super MouseDragEvent> var1) {
      this.onMouseDragEntered = var1;
      this.__set(28);
      return (B)this;
   }

   public B onMouseDragExited(EventHandler<? super MouseDragEvent> var1) {
      this.onMouseDragExited = var1;
      this.__set(29);
      return (B)this;
   }

   public B onMouseDragged(EventHandler<? super MouseEvent> var1) {
      this.onMouseDragged = var1;
      this.__set(30);
      return (B)this;
   }

   public B onMouseDragOver(EventHandler<? super MouseDragEvent> var1) {
      this.onMouseDragOver = var1;
      this.__set(31);
      return (B)this;
   }

   public B onMouseDragReleased(EventHandler<? super MouseDragEvent> var1) {
      this.onMouseDragReleased = var1;
      this.__set(32);
      return (B)this;
   }

   public B onMouseEntered(EventHandler<? super MouseEvent> var1) {
      this.onMouseEntered = var1;
      this.__set(33);
      return (B)this;
   }

   public B onMouseExited(EventHandler<? super MouseEvent> var1) {
      this.onMouseExited = var1;
      this.__set(34);
      return (B)this;
   }

   public B onMouseMoved(EventHandler<? super MouseEvent> var1) {
      this.onMouseMoved = var1;
      this.__set(35);
      return (B)this;
   }

   public B onMousePressed(EventHandler<? super MouseEvent> var1) {
      this.onMousePressed = var1;
      this.__set(36);
      return (B)this;
   }

   public B onMouseReleased(EventHandler<? super MouseEvent> var1) {
      this.onMouseReleased = var1;
      this.__set(37);
      return (B)this;
   }

   public B onRotate(EventHandler<? super RotateEvent> var1) {
      this.onRotate = var1;
      this.__set(38);
      return (B)this;
   }

   public B onRotationFinished(EventHandler<? super RotateEvent> var1) {
      this.onRotationFinished = var1;
      this.__set(39);
      return (B)this;
   }

   public B onRotationStarted(EventHandler<? super RotateEvent> var1) {
      this.onRotationStarted = var1;
      this.__set(40);
      return (B)this;
   }

   public B onScroll(EventHandler<? super ScrollEvent> var1) {
      this.onScroll = var1;
      this.__set(41);
      return (B)this;
   }

   public B onScrollFinished(EventHandler<? super ScrollEvent> var1) {
      this.onScrollFinished = var1;
      this.__set(42);
      return (B)this;
   }

   public B onScrollStarted(EventHandler<? super ScrollEvent> var1) {
      this.onScrollStarted = var1;
      this.__set(43);
      return (B)this;
   }

   public B onSwipeDown(EventHandler<? super SwipeEvent> var1) {
      this.onSwipeDown = var1;
      this.__set(44);
      return (B)this;
   }

   public B onSwipeLeft(EventHandler<? super SwipeEvent> var1) {
      this.onSwipeLeft = var1;
      this.__set(45);
      return (B)this;
   }

   public B onSwipeRight(EventHandler<? super SwipeEvent> var1) {
      this.onSwipeRight = var1;
      this.__set(46);
      return (B)this;
   }

   public B onSwipeUp(EventHandler<? super SwipeEvent> var1) {
      this.onSwipeUp = var1;
      this.__set(47);
      return (B)this;
   }

   public B onTouchMoved(EventHandler<? super TouchEvent> var1) {
      this.onTouchMoved = var1;
      this.__set(48);
      return (B)this;
   }

   public B onTouchPressed(EventHandler<? super TouchEvent> var1) {
      this.onTouchPressed = var1;
      this.__set(49);
      return (B)this;
   }

   public B onTouchReleased(EventHandler<? super TouchEvent> var1) {
      this.onTouchReleased = var1;
      this.__set(50);
      return (B)this;
   }

   public B onTouchStationary(EventHandler<? super TouchEvent> var1) {
      this.onTouchStationary = var1;
      this.__set(51);
      return (B)this;
   }

   public B onZoom(EventHandler<? super ZoomEvent> var1) {
      this.onZoom = var1;
      this.__set(52);
      return (B)this;
   }

   public B onZoomFinished(EventHandler<? super ZoomEvent> var1) {
      this.onZoomFinished = var1;
      this.__set(53);
      return (B)this;
   }

   public B onZoomStarted(EventHandler<? super ZoomEvent> var1) {
      this.onZoomStarted = var1;
      this.__set(54);
      return (B)this;
   }

   public B opacity(double var1) {
      this.opacity = var1;
      this.__set(55);
      return (B)this;
   }

   public B pickOnBounds(boolean var1) {
      this.pickOnBounds = var1;
      this.__set(56);
      return (B)this;
   }

   public B rotate(double var1) {
      this.rotate = var1;
      this.__set(57);
      return (B)this;
   }

   public B rotationAxis(Point3D var1) {
      this.rotationAxis = var1;
      this.__set(58);
      return (B)this;
   }

   public B scaleX(double var1) {
      this.scaleX = var1;
      this.__set(59);
      return (B)this;
   }

   public B scaleY(double var1) {
      this.scaleY = var1;
      this.__set(60);
      return (B)this;
   }

   public B scaleZ(double var1) {
      this.scaleZ = var1;
      this.__set(61);
      return (B)this;
   }

   public B style(String var1) {
      this.style = var1;
      this.__set(62);
      return (B)this;
   }

   public B styleClass(Collection<? extends String> var1) {
      this.styleClass = var1;
      this.__set(63);
      return (B)this;
   }

   public B styleClass(String... var1) {
      return this.styleClass(Arrays.asList(var1));
   }

   public B transforms(Collection<? extends Transform> var1) {
      this.transforms = var1;
      this.__set(64);
      return (B)this;
   }

   public B transforms(Transform... var1) {
      return this.transforms(Arrays.asList(var1));
   }

   public B translateX(double var1) {
      this.translateX = var1;
      this.__set(65);
      return (B)this;
   }

   public B translateY(double var1) {
      this.translateY = var1;
      this.__set(66);
      return (B)this;
   }

   public B translateZ(double var1) {
      this.translateZ = var1;
      this.__set(67);
      return (B)this;
   }

   public B userData(Object var1) {
      this.userData = var1;
      this.__set(68);
      return (B)this;
   }

   public B visible(boolean var1) {
      this.visible = var1;
      this.__set(69);
      return (B)this;
   }
}
