package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.traversal.Direction;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.PopupControl;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class TwoLevelFocusBehavior {
   Node tlNode = null;
   PopupControl tlPopup = null;
   EventDispatcher origEventDispatcher = null;
   final EventDispatcher preemptiveEventDispatcher = (var1, var2) -> {
      if (var1 instanceof KeyEvent
         && var1.getEventType() == KeyEvent.KEY_PRESSED
         && !((KeyEvent)var1).isMetaDown()
         && !((KeyEvent)var1).isControlDown()
         && !((KeyEvent)var1).isAltDown()
         && this.isExternalFocus()) {
         EventTarget var3 = var1.getTarget();
         switch (((KeyEvent)var1).getCode()) {
            case TAB:
               if (((KeyEvent)var1).isShiftDown()) {
                  NodeHelper.traverse((Node)var3, Direction.PREVIOUS);
               } else {
                  NodeHelper.traverse((Node)var3, Direction.NEXT);
               }

               var1.consume();
               break;
            case UP:
               NodeHelper.traverse((Node)var3, Direction.UP);
               var1.consume();
               break;
            case DOWN:
               NodeHelper.traverse((Node)var3, Direction.DOWN);
               var1.consume();
               break;
            case LEFT:
               NodeHelper.traverse((Node)var3, Direction.LEFT);
               var1.consume();
               break;
            case RIGHT:
               NodeHelper.traverse((Node)var3, Direction.RIGHT);
               var1.consume();
               break;
            case ENTER:
               this.setExternalFocus(false);
               var1.consume();
               break;
            default:
               Scene var4 = this.tlNode.getScene();
               Event.fireEvent(var4, var1);
               var1.consume();
         }
      }

      return var1;
   };
   final EventDispatcher tlfEventDispatcher = (var1, var2) -> {
      if (var1 instanceof KeyEvent && this.isExternalFocus()) {
         var2 = var2.prepend(this.preemptiveEventDispatcher);
         return var2.dispatchEvent(var1);
      } else {
         return this.origEventDispatcher.dispatchEvent(var1, var2);
      }
   };
   private final EventHandler<KeyEvent> keyEventListener = var1 -> this.postDispatchTidyup(var1);
   final ChangeListener<Boolean> focusListener = (var1, var2, var3) -> {
      if (var3 && this.tlPopup != null) {
         this.setExternalFocus(false);
      } else {
         this.setExternalFocus(true);
      }
   };
   private final EventHandler<MouseEvent> mouseEventListener = var1 -> this.setExternalFocus(false);
   private boolean externalFocus = true;
   private static final PseudoClass INTERNAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("internal-focus");
   private static final PseudoClass EXTERNAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("external-focus");

   public TwoLevelFocusBehavior() {
   }

   public TwoLevelFocusBehavior(Node var1) {
      this.tlNode = var1;
      this.tlPopup = null;
      this.tlNode.addEventHandler(KeyEvent.ANY, this.keyEventListener);
      this.tlNode.addEventHandler(MouseEvent.MOUSE_PRESSED, this.mouseEventListener);
      this.tlNode.focusedProperty().addListener(this.focusListener);
      this.origEventDispatcher = this.tlNode.getEventDispatcher();
      this.tlNode.setEventDispatcher(this.tlfEventDispatcher);
   }

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

   public boolean isExternalFocus() {
      return this.externalFocus;
   }

   public void setExternalFocus(boolean var1) {
      this.externalFocus = var1;
      if (this.tlNode != null && this.tlNode instanceof Control) {
         this.tlNode.pseudoClassStateChanged(INTERNAL_PSEUDOCLASS_STATE, !var1);
         this.tlNode.pseudoClassStateChanged(EXTERNAL_PSEUDOCLASS_STATE, var1);
      } else if (this.tlPopup != null) {
         this.tlPopup.pseudoClassStateChanged(INTERNAL_PSEUDOCLASS_STATE, !var1);
         this.tlPopup.pseudoClassStateChanged(EXTERNAL_PSEUDOCLASS_STATE, var1);
      }
   }
}
