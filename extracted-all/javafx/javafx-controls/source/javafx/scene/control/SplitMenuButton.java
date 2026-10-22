package javafx.scene.control;

import javafx.event.ActionEvent;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.skin.SplitMenuButtonSkin;

public class SplitMenuButton extends MenuButton {
   private static final String DEFAULT_STYLE_CLASS = "split-menu-button";

   public SplitMenuButton() {
      this((MenuItem[])null);
   }

   public SplitMenuButton(MenuItem... var1) {
      if (var1 != null) {
         this.getItems().addAll(var1);
      }

      this.getStyleClass().setAll(new String[]{"split-menu-button"});
      this.setAccessibleRole(AccessibleRole.SPLIT_MENU_BUTTON);
      this.setMnemonicParsing(true);
   }

   @Override
   public void fire() {
      if (!this.isDisabled()) {
         this.fireEvent(new ActionEvent());
      }
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new SplitMenuButtonSkin(this);
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case EXPANDED:
            return this.isShowing();
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   @Override
   public void executeAccessibleAction(AccessibleAction var1, Object... var2) {
      switch (var1) {
         case FIRE:
            this.fire();
            break;
         case EXPAND:
            this.show();
            break;
         case COLLAPSE:
            this.hide();
            break;
         default:
            super.executeAccessibleAction(var1);
      }
   }
}
