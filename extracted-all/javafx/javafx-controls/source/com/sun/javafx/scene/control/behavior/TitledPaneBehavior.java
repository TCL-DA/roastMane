package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

public class TitledPaneBehavior extends BehaviorBase<TitledPane> {
   private final TitledPane titledPane;
   private final InputMap<TitledPane> inputMap;

   public TitledPaneBehavior(TitledPane var1) {
      super(var1);
      this.titledPane = var1;
      this.inputMap = this.createInputMap();
      this.addDefaultMapping(this.inputMap, FocusTraversalInputMap.getFocusTraversalMappings());
      this.addDefaultMapping(new InputMap.Mapping[]{new InputMap.KeyMapping(KeyCode.SPACE, var1x -> {
         if (this.titledPane.isCollapsible() && this.titledPane.isFocused()) {
            this.titledPane.setExpanded(!this.titledPane.isExpanded());
            this.titledPane.requestFocus();
         }
      }), new InputMap.MouseMapping(MouseEvent.MOUSE_PRESSED, this::mousePressed)});
   }

   @Override
   public InputMap<TitledPane> getInputMap() {
      return this.inputMap;
   }

   public void mousePressed(MouseEvent var1) {
      this.getNode().requestFocus();
   }

   public void expand() {
      this.titledPane.setExpanded(true);
   }

   public void collapse() {
      this.titledPane.setExpanded(false);
   }

   public void toggle() {
      this.titledPane.setExpanded(!this.titledPane.isExpanded());
   }
}
