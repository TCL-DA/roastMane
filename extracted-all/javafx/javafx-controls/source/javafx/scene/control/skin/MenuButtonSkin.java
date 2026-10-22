package javafx.scene.control.skin;

import com.sun.javafx.scene.control.ContextMenuContent;
import com.sun.javafx.scene.control.behavior.MenuButtonBehavior;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.MenuButton;

public class MenuButtonSkin extends MenuButtonSkinBase<MenuButton> {
   static final String AUTOHIDE = "autoHide";
   private final MenuButtonBehavior behavior;

   public MenuButtonSkin(MenuButton var1) {
      super(var1);
      this.behavior = new MenuButtonBehavior(var1);
      this.popup.setOnAutoHide(var1x -> {
         MenuButton var2 = this.getSkinnable();
         if (!var2.getProperties().containsKey("autoHide")) {
            var2.getProperties().put("autoHide", Boolean.TRUE);
         }
      });
      this.popup.setOnShown(var1x -> {
         if (this.requestFocusOnFirstMenuItem) {
            this.requestFocusOnFirstMenuItem();
            this.requestFocusOnFirstMenuItem = false;
         } else {
            ContextMenuContent var2 = (ContextMenuContent)this.popup.getSkin().getNode();
            if (var2 != null) {
               var2.requestFocus();
            }
         }
      });
      if (var1.getOnAction() == null) {
         var1.setOnAction(var1x -> var1.show());
      }

      this.label.setLabelFor(var1);
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   MenuButtonBehavior getBehavior() {
      return this.behavior;
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case MNEMONIC:
            return this.label.queryAccessibleAttribute(AccessibleAttribute.MNEMONIC, new Object[0]);
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }
}
