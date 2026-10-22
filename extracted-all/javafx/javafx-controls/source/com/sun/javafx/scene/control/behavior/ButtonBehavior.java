package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.ButtonBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ButtonBehavior<C extends ButtonBase> extends BehaviorBase<C> {
   private final InputMap<C> buttonInputMap;
   private boolean keyDown;
   private InvalidationListener focusListener = this::focusChanged;

   public ButtonBehavior(C var1) {
      super((C)var1);
      this.buttonInputMap = this.createInputMap();
      this.addDefaultMapping(this.buttonInputMap, FocusTraversalInputMap.getFocusTraversalMappings());
      this.addDefaultMapping(
         this.buttonInputMap,
         new InputMap.Mapping[]{
            new InputMap.KeyMapping(KeyCode.SPACE, KeyEvent.KEY_PRESSED, this::keyPressed),
            new InputMap.KeyMapping(KeyCode.SPACE, KeyEvent.KEY_RELEASED, this::keyReleased),
            new InputMap.MouseMapping(MouseEvent.MOUSE_PRESSED, this::mousePressed),
            new InputMap.MouseMapping(MouseEvent.MOUSE_RELEASED, this::mouseReleased),
            new InputMap.MouseMapping(MouseEvent.MOUSE_ENTERED, this::mouseEntered),
            new InputMap.MouseMapping(MouseEvent.MOUSE_EXITED, this::mouseExited),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.ENTER, KeyEvent.KEY_PRESSED), this::keyPressed, var0 -> PlatformUtil.isMac()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.ENTER, KeyEvent.KEY_RELEASED), this::keyReleased, var0 -> PlatformUtil.isMac())
         }
      );
      var1.focusedProperty().addListener(this.focusListener);
   }

   @Override
   public InputMap<C> getInputMap() {
      return this.buttonInputMap;
   }

   @Override
   public void dispose() {
      this.getNode().focusedProperty().removeListener(this.focusListener);
      super.dispose();
   }

   private void focusChanged(Observable var1) {
      ButtonBase var2 = this.getNode();
      if (this.keyDown && !var2.isFocused()) {
         this.keyDown = false;
         var2.disarm();
      }
   }

   protected void keyPressed(KeyEvent var1) {
      if (!this.getNode().isPressed() && !this.getNode().isArmed()) {
         this.keyDown = true;
         this.getNode().arm();
      }
   }

   protected void keyReleased(KeyEvent var1) {
      if (this.keyDown) {
         this.keyDown = false;
         if (this.getNode().isArmed()) {
            this.getNode().disarm();
            this.getNode().fire();
         }
      }
   }

   protected void mousePressed(MouseEvent var1) {
      if (!this.getNode().isFocused() && this.getNode().isFocusTraversable()) {
         this.getNode().requestFocus();
      }

      boolean var2 = var1.getButton() == MouseButton.PRIMARY
         && !var1.isMiddleButtonDown()
         && !var1.isSecondaryButtonDown()
         && !var1.isShiftDown()
         && !var1.isControlDown()
         && !var1.isAltDown()
         && !var1.isMetaDown();
      if (!this.getNode().isArmed() && var2) {
         this.getNode().arm();
      }
   }

   protected void mouseReleased(MouseEvent var1) {
      if (!this.keyDown && this.getNode().isArmed()) {
         this.getNode().fire();
         this.getNode().disarm();
      }
   }

   protected void mouseEntered(MouseEvent var1) {
      if (!this.keyDown && this.getNode().isPressed()) {
         this.getNode().arm();
      }
   }

   protected void mouseExited(MouseEvent var1) {
      if (!this.keyDown && this.getNode().isArmed()) {
         this.getNode().disarm();
      }
   }
}
