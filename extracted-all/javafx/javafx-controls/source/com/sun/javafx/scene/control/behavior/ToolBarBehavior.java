package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;

public class ToolBarBehavior extends BehaviorBase<ToolBar> {
   private final InputMap<ToolBar> toolBarInputMap = this.createInputMap();

   public ToolBarBehavior(ToolBar var1) {
      super(var1);
      this.addDefaultMapping(this.toolBarInputMap, new InputMap.Mapping[]{new InputMap.KeyMapping(new KeyBinding(KeyCode.F5).ctrl(), var1x -> {
         if (!var1.getItems().isEmpty()) {
            ((Node)var1.getItems().get(0)).requestFocus();
         }
      })});
   }

   @Override
   public InputMap<ToolBar> getInputMap() {
      return this.toolBarInputMap;
   }
}
