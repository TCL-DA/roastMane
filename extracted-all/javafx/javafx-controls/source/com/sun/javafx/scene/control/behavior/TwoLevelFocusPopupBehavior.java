package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.traversal.Direction;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PopupControl;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class TwoLevelFocusPopupBehavior extends TwoLevelFocusBehavior {
   final EventDispatcher preemptiveEventDispatcher = (var1x, var2) -> {
      if (var1x instanceof KeyEvent
         && var1x.getEventType() == KeyEvent.KEY_PRESSED
         && !((KeyEvent)var1x).isMetaDown()
         && !((KeyEvent)var1x).isControlDown()
         && !((KeyEvent)var1x).isAltDown()
         && this.isExternalFocus()) {
         EventTarget var3 = var1x.getTarget();
         switch (((KeyEvent)var1x).getCode()) {
            case TAB:
               if (((KeyEvent)var1x).isShiftDown()) {
                  NodeHelper.traverse((Node)var3, Direction.PREVIOUS);
               } else {
                  NodeHelper.traverse((Node)var3, Direction.NEXT);
               }

               var1x.consume();
               break;
            case UP:
               NodeHelper.traverse((Node)var3, Direction.UP);
               var1x.consume();
               break;
            case DOWN:
               NodeHelper.traverse((Node)var3, Direction.DOWN);
               var1x.consume();
               break;
            case LEFT:
               NodeHelper.traverse((Node)var3, Direction.LEFT);
               var1x.consume();
               break;
            case RIGHT:
               NodeHelper.traverse((Node)var3, Direction.RIGHT);
               var1x.consume();
               break;
            case ENTER:
               this.setExternalFocus(false);
               var1x.consume();
               break;
            default:
               Scene var4 = this.tlNode.getScene();
               Event.fireEvent(var4, var1x);
               var1x.consume();
         }
      }

      return var1x;
   };
   final EventDispatcher preemptivePopupEventDispatcher = (var1x, var2) -> {
      if (var1x instanceof KeyEvent
         && var1x.getEventType() == KeyEvent.KEY_PRESSED
         && !((KeyEvent)var1x).isMetaDown()
         && !((KeyEvent)var1x).isControlDown()
         && !((KeyEvent)var1x).isAltDown()
         && !this.isExternalFocus()) {
         EventTarget var3 = var1x.getTarget();
         switch (((KeyEvent)var1x).getCode()) {
            case TAB:
            case ENTER:
               var1x.consume();
            case UP:
            case DOWN:
               break;
            case LEFT:
               if (var3 instanceof Node) {
                  NodeHelper.traverse((Node)var3, Direction.LEFT);
                  var1x.consume();
               } else if (var3 instanceof Scene) {
                  Node var7 = ((Scene)var3).getFocusOwner();
                  if (var7 != null) {
                     NodeHelper.traverse(var7, Direction.LEFT);
                     var1x.consume();
                  }
               }
               break;
            case RIGHT:
               if (var3 instanceof Node) {
                  NodeHelper.traverse((Node)var3, Direction.RIGHT);
                  var1x.consume();
               } else if (var3 instanceof Scene) {
                  Node var6 = ((Scene)var3).getFocusOwner();
                  if (var6 != null) {
                     NodeHelper.traverse(var6, Direction.RIGHT);
                     var1x.consume();
                  }
               }
               break;
            default:
               Object var4 = null;
               if (this.tlNode != null) {
                  var4 = this.tlNode.getScene();
                  Event.fireEvent((EventTarget)var4, var1x);
               }

               var1x.consume();
         }
      }

      return var1x;
   };
   final EventDispatcher tlfEventDispatcher = (var1x, var2) -> {
      if (var1x instanceof KeyEvent) {
         if (this.isExternalFocus()) {
            var2 = var2.prepend(this.preemptiveEventDispatcher);
            return var2.dispatchEvent(var1x);
         } else {
            var2 = var2.prepend(this.preemptivePopupEventDispatcher);
            var2 = var2.prepend(this.origEventDispatcher);
            return var2.dispatchEvent(var1x);
         }
      } else {
         return this.origEventDispatcher.dispatchEvent(var1x, var2);
      }
   };
   private final EventHandler<KeyEvent> keyEventListener = var1x -> this.postDispatchTidyup(var1x);
   final ChangeListener<Boolean> focusListener = (var0, var1x, var2) -> {
   };
   private final EventHandler<MouseEvent> mouseEventListener = var1x -> this.setExternalFocus(false);

   public TwoLevelFocusPopupBehavior(PopupControl var1) {
      this.tlPopup = var1;
      this.setExternalFocus(false);
      this.tlPopup.addEventHandler(KeyEvent.ANY, this.keyEventListener);
      this.tlPopup.addEventHandler(MouseEvent.MOUSE_PRESSED, this.mouseEventListener);
      this.tlPopup.focusedProperty().addListener(this.focusListener);
      this.origEventDispatcher = this.tlPopup.getEventDispatcher();
      this.tlPopup.setEventDispatcher(this.tlfEventDispatcher);
   }

   public TwoLevelFocusPopupBehavior(Node var1) {
      this.tlNode = var1;
      this.setExternalFocus(false);
      this.tlNode.addEventHandler(KeyEvent.ANY, this.keyEventListener);
      this.tlNode.addEventHandler(MouseEvent.MOUSE_PRESSED, this.mouseEventListener);
      this.tlNode.focusedProperty().addListener(this.focusListener);
      this.origEventDispatcher = this.tlNode.getEventDispatcher();
      this.tlNode.setEventDispatcher(this.tlfEventDispatcher);
   }

   @Override
   public void dispose() {
      this.tlNode.removeEventHandler(KeyEvent.ANY, this.keyEventListener);
      this.tlNode.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.mouseEventListener);
      this.tlNode.focusedProperty().removeListener(this.focusListener);
      this.tlNode.setEventDispatcher(this.origEventDispatcher);
   }

   private Event postDispatchTidyup(Event var1) {
      if (var1 instanceof KeyEvent
         && var1.getEventType() == KeyEvent.KEY_PRESSED
         && !this.isExternalFocus()
         && !((KeyEvent)var1).isMetaDown()
         && !((KeyEvent)var1).isControlDown()
         && !((KeyEvent)var1).isAltDown()) {
         switch (((KeyEvent)var1).getCode()) {
            case TAB:
            case UP:
            case DOWN:
            case LEFT:
            case RIGHT:
               var1.consume();
               break;
            case ENTER:
               this.setExternalFocus(true);
               var1.consume();
         }
      }

      return var1;
   }
}
