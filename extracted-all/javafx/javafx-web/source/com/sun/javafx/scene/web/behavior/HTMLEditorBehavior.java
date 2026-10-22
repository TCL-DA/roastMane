package com.sun.javafx.scene.web.behavior;

import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.FocusTraversalInputMap;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import com.sun.javafx.scene.control.inputmap.InputMap.KeyMapping;
import com.sun.javafx.scene.control.inputmap.InputMap.Mapping;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.HTMLEditorSkin;

public class HTMLEditorBehavior extends BehaviorBase<HTMLEditor> {
   private final InputMap<HTMLEditor> inputMap = this.createInputMap();

   public HTMLEditorBehavior(HTMLEditor var1) {
      super(var1);
      this.addDefaultMapping(
         this.inputMap,
         new Mapping[]{
            new KeyMapping(new KeyBinding(KeyCode.B).shortcut(), var1x -> this.keyboardShortcuts(HTMLEditorSkin.Command.BOLD)),
            new KeyMapping(new KeyBinding(KeyCode.I).shortcut(), var1x -> this.keyboardShortcuts(HTMLEditorSkin.Command.ITALIC)),
            new KeyMapping(new KeyBinding(KeyCode.U).shortcut(), var1x -> this.keyboardShortcuts(HTMLEditorSkin.Command.UNDERLINE)),
            new KeyMapping(new KeyBinding(KeyCode.F12), var1x -> ParentHelper.getTraversalEngine((Parent)this.getNode()).selectFirst().requestFocus()),
            new KeyMapping(new KeyBinding(KeyCode.TAB).ctrl(), FocusTraversalInputMap::traverseNext),
            new KeyMapping(new KeyBinding(KeyCode.TAB).ctrl().shift(), FocusTraversalInputMap::traversePrevious)
         }
      );
   }

   public InputMap<HTMLEditor> getInputMap() {
      return this.inputMap;
   }

   private void keyboardShortcuts(HTMLEditorSkin.Command var1) {
      HTMLEditor var2 = (HTMLEditor)this.getNode();
      HTMLEditorSkin var3 = (HTMLEditorSkin)var2.getSkin();
      var3.performCommand(var1);
   }
}
