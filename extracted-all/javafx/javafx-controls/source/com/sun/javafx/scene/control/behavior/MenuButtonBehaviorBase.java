package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.geometry.Side;
import javafx.scene.control.MenuButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public abstract class MenuButtonBehaviorBase<C extends MenuButton> extends ButtonBehavior<C> {
   private final InputMap<C> buttonInputMap = super.getInputMap();

   public MenuButtonBehaviorBase(C var1) {
      super((C)var1);
      this.removeMapping(MouseEvent.MOUSE_RELEASED);
      this.addDefaultMapping(new InputMap.Mapping[]{new InputMap.KeyMapping(KeyCode.ESCAPE, var1x -> this.getNode().hide())});
      this.addDefaultMapping(new InputMap.Mapping[]{new InputMap.KeyMapping(KeyCode.CANCEL, var1x -> this.getNode().hide())});
      InputMap var2 = new InputMap(var1);
      this.addDefaultMapping(var2, new InputMap.Mapping[]{new InputMap.KeyMapping(KeyCode.UP, this::overrideTraversalInput)});
      this.addDefaultMapping(var2, new InputMap.Mapping[]{new InputMap.KeyMapping(KeyCode.DOWN, this::overrideTraversalInput)});
      this.addDefaultMapping(var2, new InputMap.Mapping[]{new InputMap.KeyMapping(KeyCode.LEFT, this::overrideTraversalInput)});
      this.addDefaultMapping(var2, new InputMap.Mapping[]{new InputMap.KeyMapping(KeyCode.RIGHT, this::overrideTraversalInput)});
      this.addDefaultChildMap(this.buttonInputMap, var2);
   }

   private void overrideTraversalInput(KeyEvent var1) {
      MenuButton var2 = this.getNode();
      Side var3 = var2.getPopupSide();
      if (!var2.isShowing() && var1.getCode() == KeyCode.UP && var3 == Side.TOP
         || var1.getCode() == KeyCode.DOWN && (var3 == Side.BOTTOM || var3 == Side.TOP)
         || var1.getCode() == KeyCode.LEFT && (var3 == Side.RIGHT || var3 == Side.LEFT)
         || var1.getCode() == KeyCode.RIGHT && (var3 == Side.RIGHT || var3 == Side.LEFT)) {
         var2.show();
      }
   }

   protected void openAction() {
      if (this.getNode().isShowing()) {
         this.getNode().hide();
      } else {
         this.getNode().show();
      }
   }

   public void mousePressed(MouseEvent var1, boolean var2) {
      MenuButton var3 = this.getNode();
      if (var2) {
         if (var3.isShowing()) {
            var3.hide();
         }

         super.mousePressed(var1);
      } else {
         if (!var3.isFocused() && var3.isFocusTraversable()) {
            var3.requestFocus();
         }

         if (var3.isShowing()) {
            var3.hide();
         } else if (var1.getButton() == MouseButton.PRIMARY) {
            var3.show();
         }
      }
   }

   public void mouseReleased(MouseEvent var1, boolean var2) {
      if (var2) {
         super.mouseReleased(var1);
      } else {
         if (this.getNode().isShowing() && !this.getNode().contains(var1.getX(), var1.getY())) {
            this.getNode().hide();
         }

         this.getNode().disarm();
      }
   }
}
