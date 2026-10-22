package javafx.scene.input;

import javafx.beans.NamedArg;
import javafx.event.EventTarget;
import javafx.event.EventType;

public final class MouseDragEvent extends MouseEvent {
   private static final long serialVersionUID = 20121107L;
   public static final EventType<MouseDragEvent> ANY = new EventType(MouseEvent.ANY, "MOUSE-DRAG");
   public static final EventType<MouseDragEvent> MOUSE_DRAG_OVER = new EventType(ANY, "MOUSE-DRAG_OVER");
   public static final EventType<MouseDragEvent> MOUSE_DRAG_RELEASED = new EventType(ANY, "MOUSE-DRAG_RELEASED");
   public static final EventType<MouseDragEvent> MOUSE_DRAG_ENTERED_TARGET = new EventType(ANY, "MOUSE-DRAG_ENTERED_TARGET");
   public static final EventType<MouseDragEvent> MOUSE_DRAG_ENTERED = new EventType(MOUSE_DRAG_ENTERED_TARGET, "MOUSE-DRAG_ENTERED");
   public static final EventType<MouseDragEvent> MOUSE_DRAG_EXITED_TARGET = new EventType(ANY, "MOUSE-DRAG_EXITED_TARGET");
   public static final EventType<MouseDragEvent> MOUSE_DRAG_EXITED = new EventType(MOUSE_DRAG_EXITED_TARGET, "MOUSE-DRAG_EXITED");
   private final transient Object gestureSource;

   public MouseDragEvent(
      @NamedArg("source") Object var1,
      @NamedArg("target") EventTarget var2,
      @NamedArg("eventType") EventType<MouseDragEvent> var3,
      @NamedArg("x") double var4,
      @NamedArg("y") double var6,
      @NamedArg("screenX") double var8,
      @NamedArg("screenY") double var10,
      @NamedArg("button") MouseButton var12,
      @NamedArg("clickCount") int var13,
      @NamedArg("shiftDown") boolean var14,
      @NamedArg("controlDown") boolean var15,
      @NamedArg("altDown") boolean var16,
      @NamedArg("metaDown") boolean var17,
      @NamedArg("primaryButtonDown") boolean var18,
      @NamedArg("middleButtonDown") boolean var19,
      @NamedArg("secondaryButtonDown") boolean var20,
      @NamedArg("synthesized") boolean var21,
      @NamedArg("popupTrigger") boolean var22,
      @NamedArg("pickResult") PickResult var23,
      @NamedArg("gestureSource") Object var24
   ) {
      this(var1, var2, var3, var4, var6, var8, var10, var12, var13, var14, var15, var16, var17, var18, var19, var20, false, false, var21, var22, var23, var24);
   }

   public MouseDragEvent(
      @NamedArg("source") Object var1,
      @NamedArg("target") EventTarget var2,
      @NamedArg("eventType") EventType<MouseDragEvent> var3,
      @NamedArg("x") double var4,
      @NamedArg("y") double var6,
      @NamedArg("screenX") double var8,
      @NamedArg("screenY") double var10,
      @NamedArg("button") MouseButton var12,
      @NamedArg("clickCount") int var13,
      @NamedArg("shiftDown") boolean var14,
      @NamedArg("controlDown") boolean var15,
      @NamedArg("altDown") boolean var16,
      @NamedArg("metaDown") boolean var17,
      @NamedArg("primaryButtonDown") boolean var18,
      @NamedArg("middleButtonDown") boolean var19,
      @NamedArg("secondaryButtonDown") boolean var20,
      @NamedArg("backButtonDown") boolean var21,
      @NamedArg("forwardButtonDown") boolean var22,
      @NamedArg("synthesized") boolean var23,
      @NamedArg("popupTrigger") boolean var24,
      @NamedArg("pickResult") PickResult var25,
      @NamedArg("gestureSource") Object var26
   ) {
      super(var1, var2, var3, var4, var6, var8, var10, var12, var13, var14, var15, var16, var17, var18, var19, var20, var21, var22, var23, var24, false, var25);
      this.gestureSource = var26;
   }

   public MouseDragEvent(
      @NamedArg("eventType") EventType<MouseDragEvent> var1,
      @NamedArg("x") double var2,
      @NamedArg("y") double var4,
      @NamedArg("screenX") double var6,
      @NamedArg("screenY") double var8,
      @NamedArg("button") MouseButton var10,
      @NamedArg("clickCount") int var11,
      @NamedArg("shiftDown") boolean var12,
      @NamedArg("controlDown") boolean var13,
      @NamedArg("altDown") boolean var14,
      @NamedArg("metaDown") boolean var15,
      @NamedArg("primaryButtonDown") boolean var16,
      @NamedArg("middleButtonDown") boolean var17,
      @NamedArg("secondaryButtonDown") boolean var18,
      @NamedArg("synthesized") boolean var19,
      @NamedArg("popupTrigger") boolean var20,
      @NamedArg("pickResult") PickResult var21,
      @NamedArg("gestureSource") Object var22
   ) {
      this(null, null, var1, var2, var4, var6, var8, var10, var11, var12, var13, var14, var15, var16, var17, var18, var19, var20, var21, var22);
   }

   public Object getGestureSource() {
      return this.gestureSource;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("MouseDragEvent [");
      var1.append("source = ").append(this.getSource());
      var1.append(", target = ").append(this.getTarget());
      var1.append(", gestureSource = ").append(this.getGestureSource());
      var1.append(", eventType = ").append(this.getEventType());
      var1.append(", consumed = ").append(this.isConsumed());
      var1.append(", x = ").append(this.getX()).append(", y = ").append(this.getY()).append(", z = ").append(this.getZ());
      if (this.getButton() != null) {
         var1.append(", button = ").append(this.getButton());
      }

      if (this.getClickCount() > 1) {
         var1.append(", clickCount = ").append(this.getClickCount());
      }

      if (this.isPrimaryButtonDown()) {
         var1.append(", primaryButtonDown");
      }

      if (this.isMiddleButtonDown()) {
         var1.append(", middleButtonDown");
      }

      if (this.isSecondaryButtonDown()) {
         var1.append(", secondaryButtonDown");
      }

      if (this.isBackButtonDown()) {
         var1.append(", backButtonDown");
      }

      if (this.isForwardButtonDown()) {
         var1.append(", forwardButtonDown");
      }

      if (this.isShiftDown()) {
         var1.append(", shiftDown");
      }

      if (this.isControlDown()) {
         var1.append(", controlDown");
      }

      if (this.isAltDown()) {
         var1.append(", altDown");
      }

      if (this.isMetaDown()) {
         var1.append(", metaDown");
      }

      if (this.isShortcutDown()) {
         var1.append(", shortcutDown");
      }

      if (this.isSynthesized()) {
         var1.append(", synthesized");
      }

      var1.append(", pickResult = ").append(this.getPickResult());
      return var1.append("]").toString();
   }

   public MouseDragEvent copyFor(Object var1, EventTarget var2) {
      return (MouseDragEvent)super.copyFor(var1, var2);
   }

   public MouseDragEvent copyFor(Object var1, EventTarget var2, EventType<? extends MouseEvent> var3) {
      return (MouseDragEvent)super.copyFor(var1, var2, var3);
   }

   @Override
   public EventType<MouseDragEvent> getEventType() {
      return super.getEventType();
   }
}
