package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ToggleButtonBehavior<C extends ToggleButton> extends ButtonBehavior<C> {
   public ToggleButtonBehavior(C var1) {
      super((C)var1);
      ObservableList var2 = FXCollections.observableArrayList(
         new InputMap.Mapping[]{
            new InputMap.KeyMapping(KeyCode.RIGHT, var1x -> this.traverse(var1x, "ToggleNext-Right")),
            new InputMap.KeyMapping(KeyCode.LEFT, var1x -> this.traverse(var1x, "TogglePrevious-Left")),
            new InputMap.KeyMapping(KeyCode.DOWN, var1x -> this.traverse(var1x, "ToggleNext-Down")),
            new InputMap.KeyMapping(KeyCode.UP, var1x -> this.traverse(var1x, "TogglePrevious-Up"))
         }
      );

      for (InputMap.Mapping var4 : var2) {
         var4.setAutoConsume(false);
      }

      InputMap var5 = new InputMap(var1);
      var5.getMappings().addAll(var2);
      this.addDefaultChildMap(this.getInputMap(), var5);
   }

   private int nextToggleIndex(ObservableList<Toggle> var1, int var2) {
      if (var2 >= 0 && var2 < var1.size()) {
         int var4 = (var2 + 1) % var1.size();

         Toggle var3;
         while (var4 != var2 && (var3 = (Toggle)var1.get(var4)) instanceof Node && ((Node)var3).isDisabled()) {
            var4 = (var4 + 1) % var1.size();
         }

         return var4;
      } else {
         return 0;
      }
   }

   private int previousToggleIndex(ObservableList<Toggle> var1, int var2) {
      if (var2 >= 0 && var2 < var1.size()) {
         int var4 = Math.floorMod(var2 - 1, var1.size());

         Toggle var3;
         while (var4 != var2 && (var3 = (Toggle)var1.get(var4)) instanceof Node && ((Node)var3).isDisabled()) {
            var4 = Math.floorMod(var4 - 1, var1.size());
         }

         return var4;
      } else {
         return var1.size();
      }
   }

   private void traverse(KeyEvent var1, String var2) {
      ToggleButton var3 = this.getNode();
      ToggleGroup var4 = var3.getToggleGroup();
      if (var4 == null) {
         var1.consume();
      } else {
         ObservableList var5 = var4.getToggles();
         int var6 = var5.indexOf(var3);
         boolean var7 = this.traversingToNext(var2, var3.getEffectiveNodeOrientation());
         if (!Utils.isTwoLevelFocus()) {
            if (var7) {
               int var8 = this.nextToggleIndex(var5, var6);
               if (var8 != var6) {
                  Toggle var9 = (Toggle)var5.get(var8);
                  var4.selectToggle(var9);
                  ((Control)var9).requestFocus();
                  var1.consume();
               }
            } else {
               int var10 = this.previousToggleIndex(var5, var6);
               if (var10 != var6) {
                  Toggle var11 = (Toggle)var5.get(var10);
                  var4.selectToggle(var11);
                  ((Control)var11).requestFocus();
                  var1.consume();
               }
            }
         }
      }
   }

   private boolean traversingToNext(String var1, NodeOrientation var2) {
      boolean var3 = var2 == NodeOrientation.RIGHT_TO_LEFT;
      switch (var1) {
         case "ToggleNext-Right":
            return !var3;
         case "ToggleNext-Down":
            return true;
         case "TogglePrevious-Left":
            return var3;
         case "TogglePrevious-Up":
            return false;
         default:
            throw new IllegalArgumentException("Not a toggle action");
      }
   }
}
