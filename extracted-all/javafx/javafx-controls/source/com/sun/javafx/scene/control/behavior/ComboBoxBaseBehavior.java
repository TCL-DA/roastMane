package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ComboBoxBaseBehavior<T> extends BehaviorBase<ComboBoxBase<T>> {
   private final InputMap<ComboBoxBase<T>> inputMap;
   private InvalidationListener focusListener = this::focusChanged;
   private TwoLevelFocusComboBehavior tlFocus;
   private boolean keyDown;
   private boolean showPopupOnMouseRelease = true;
   private boolean mouseInsideButton = false;

   public ComboBoxBaseBehavior(ComboBoxBase<T> var1) {
      super(var1);
      this.inputMap = this.createInputMap();
      EventHandler var2 = var1x -> {
         this.showPopupOnMouseRelease = true;
         if (this.getNode().isShowing()) {
            this.hide();
         } else {
            this.show();
         }
      };
      InputMap.KeyMapping var3;
      InputMap.KeyMapping var4;
      this.addDefaultMapping(
         this.inputMap,
         new InputMap.Mapping[]{
            new InputMap.KeyMapping(KeyCode.F4, KeyEvent.KEY_RELEASED, var2),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.UP).alt(), var2),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.DOWN).alt(), var2),
            new InputMap.KeyMapping(KeyCode.SPACE, KeyEvent.KEY_PRESSED, this::keyPressed),
            new InputMap.KeyMapping(KeyCode.SPACE, KeyEvent.KEY_RELEASED, this::keyReleased),
            var3 = new InputMap.KeyMapping(KeyCode.ENTER, KeyEvent.KEY_PRESSED, this::keyPressed),
            var4 = new InputMap.KeyMapping(KeyCode.ENTER, KeyEvent.KEY_RELEASED, this::keyReleased),
            new InputMap.KeyMapping(KeyCode.ESCAPE, KeyEvent.KEY_PRESSED, this::cancelEdit),
            new InputMap.KeyMapping(KeyCode.F10, KeyEvent.KEY_PRESSED, this::forwardToParent),
            new InputMap.MouseMapping(MouseEvent.MOUSE_PRESSED, this::mousePressed),
            new InputMap.MouseMapping(MouseEvent.MOUSE_RELEASED, this::mouseReleased),
            new InputMap.MouseMapping(MouseEvent.MOUSE_ENTERED, this::mouseEntered),
            new InputMap.MouseMapping(MouseEvent.MOUSE_EXITED, this::mouseExited)
         }
      );
      var3.setAutoConsume(false);
      var4.setAutoConsume(false);
      var1.focusedProperty().addListener(this.focusListener);
      if (Utils.isTwoLevelFocus()) {
         this.tlFocus = new TwoLevelFocusComboBehavior(var1);
      }
   }

   @Override
   public void dispose() {
      if (this.tlFocus != null) {
         this.tlFocus.dispose();
      }

      this.getNode().focusedProperty().removeListener(this.focusListener);
      super.dispose();
   }

   @Override
   public InputMap<ComboBoxBase<T>> getInputMap() {
      return this.inputMap;
   }

   protected void focusChanged(Observable var1) {
      ComboBoxBase var2 = this.getNode();
      if (this.keyDown && !var2.isFocused()) {
         this.keyDown = false;
         var2.disarm();
      }
   }

   private void keyPressed(KeyEvent var1) {
      this.showPopupOnMouseRelease = true;
      if (Utils.isTwoLevelFocus()) {
         this.show();
         if (this.tlFocus != null) {
            this.tlFocus.setExternalFocus(false);
         }
      } else if (!this.getNode().isPressed() && !this.getNode().isArmed()) {
         this.keyDown = true;
         this.getNode().arm();
      }
   }

   private void keyReleased(KeyEvent var1) {
      this.showPopupOnMouseRelease = true;
      if (!Utils.isTwoLevelFocus() && this.keyDown) {
         this.keyDown = false;
         if (this.getNode().isArmed()) {
            this.getNode().disarm();
         }
      }
   }

   private void forwardToParent(KeyEvent var1) {
      if (this.getNode().getParent() != null) {
         this.getNode().getParent().fireEvent(var1);
      }
   }

   private void cancelEdit(KeyEvent var1) {
      ComboBoxBase var2 = this.getNode();
      TextField var3 = null;
      if (var2 instanceof DatePicker) {
         var3 = ((DatePicker)var2).getEditor();
      } else if (var2 instanceof ComboBox) {
         var3 = var2.isEditable() ? ((ComboBox)var2).getEditor() : null;
      }

      if (var3 != null && var3.getTextFormatter() != null) {
         var3.cancelEdit();
      } else {
         this.forwardToParent(var1);
      }
   }

   public void mousePressed(MouseEvent var1) {
      this.arm(var1);
   }

   public void mouseReleased(MouseEvent var1) {
      this.disarm();
      if (this.showPopupOnMouseRelease) {
         this.show();
      } else {
         this.showPopupOnMouseRelease = true;
         this.hide();
      }
   }

   public void mouseEntered(MouseEvent var1) {
      if (!this.getNode().isEditable()) {
         this.mouseInsideButton = true;
      } else {
         EventTarget var2 = var1.getTarget();
         this.mouseInsideButton = var2 instanceof Node && "arrow-button".equals(((Node)var2).getId());
      }

      this.arm();
   }

   public void mouseExited(MouseEvent var1) {
      this.mouseInsideButton = false;
      this.disarm();
   }

   private void arm(MouseEvent var1) {
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

   public void show() {
      if (!this.getNode().isShowing()) {
         if (this.getNode().isFocusTraversable()) {
            this.getNode().requestFocus();
         }

         this.getNode().show();
      }
   }

   public void hide() {
      if (this.getNode().isShowing()) {
         this.getNode().hide();
      }
   }

   public void onAutoHide(PopupControl var1) {
      this.hide();
      this.showPopupOnMouseRelease = this.mouseInsideButton ? !this.showPopupOnMouseRelease : true;
   }

   public void arm() {
      if (this.getNode().isPressed()) {
         this.getNode().arm();
      }
   }

   public void disarm() {
      if (!this.keyDown && this.getNode().isArmed()) {
         this.getNode().disarm();
      }
   }
}
