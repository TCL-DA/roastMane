package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.scene.control.MenuButton;
import javafx.scene.input.KeyCode;

public class MenuButtonBehavior extends MenuButtonBehaviorBase<MenuButton> {
   public MenuButtonBehavior(MenuButton var1) {
      super(var1);
      this.addDefaultMapping(new InputMap.Mapping[]{new InputMap.KeyMapping(KeyCode.SPACE, var1x -> this.openAction())});
      this.addDefaultMapping(new InputMap.Mapping[]{new InputMap.KeyMapping(KeyCode.ENTER, var1x -> this.openAction())});
   }
}
