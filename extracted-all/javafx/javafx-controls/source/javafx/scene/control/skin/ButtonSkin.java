package javafx.scene.control.skin;

import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.ButtonBehavior;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination.Modifier;

public class ButtonSkin extends LabeledSkinBase<Button> {
   private KeyCodeCombination defaultAcceleratorKeyCodeCombination;
   private KeyCodeCombination cancelAcceleratorKeyCodeCombination;
   private final BehaviorBase<Button> behavior;
   Runnable defaultButtonRunnable = () -> {
      if (this.getSkinnable().getScene() != null && NodeHelper.isTreeVisible(this.getSkinnable()) && !this.getSkinnable().isDisabled()) {
         this.getSkinnable().fire();
      }
   };
   Runnable cancelButtonRunnable = () -> {
      if (this.getSkinnable().getScene() != null && NodeHelper.isTreeVisible(this.getSkinnable()) && !this.getSkinnable().isDisabled()) {
         this.getSkinnable().fire();
      }
   };
   ChangeListener<Scene> sceneChangeListener = (var1x, var2, var3) -> {
      if (var2 != null) {
         if (this.getSkinnable().isDefaultButton()) {
            this.setDefaultButton(var2, false);
         }

         if (this.getSkinnable().isCancelButton()) {
            this.setCancelButton(var2, false);
         }
      }

      if (var3 != null) {
         if (this.getSkinnable().isDefaultButton()) {
            this.setDefaultButton(var3, true);
         }

         if (this.getSkinnable().isCancelButton()) {
            this.setCancelButton(var3, true);
         }
      }
   };
   WeakChangeListener<Scene> weakSceneChangeListener = new WeakChangeListener(this.sceneChangeListener);

   public ButtonSkin(Button var1) {
      super(var1);
      this.behavior = new ButtonBehavior<>(var1);
      this.registerChangeListener(var1.defaultButtonProperty(), var1x -> this.setDefaultButton(this.getSkinnable().isDefaultButton()));
      this.registerChangeListener(var1.cancelButtonProperty(), var1x -> this.setCancelButton(this.getSkinnable().isCancelButton()));
      this.registerChangeListener(var1.focusedProperty(), var1x -> {
         if (!this.getSkinnable().isFocused()) {
            ContextMenu var2 = this.getSkinnable().getContextMenu();
            if (var2 != null && var2.isShowing()) {
               var2.hide();
               Utils.removeMnemonics(var2, this.getSkinnable().getScene());
            }
         }
      });
      this.registerChangeListener(var1.parentProperty(), var1x -> {
         if (this.getSkinnable().getParent() == null && this.getSkinnable().getScene() != null) {
            if (this.getSkinnable().isDefaultButton()) {
               this.getSkinnable().getScene().getAccelerators().remove(this.defaultAcceleratorKeyCodeCombination);
            }

            if (this.getSkinnable().isCancelButton()) {
               this.getSkinnable().getScene().getAccelerators().remove(this.cancelAcceleratorKeyCodeCombination);
            }
         }
      });
      var1.sceneProperty().addListener(this.weakSceneChangeListener);
      if (this.getSkinnable().isDefaultButton()) {
         this.setDefaultButton(true);
      }

      if (this.getSkinnable().isCancelButton()) {
         this.setCancelButton(true);
      }
   }

   @Override
   public void dispose() {
      if (this.getSkinnable() != null) {
         if (this.getSkinnable().isDefaultButton()) {
            this.setDefaultButton(false);
         }

         if (this.getSkinnable().isCancelButton()) {
            this.setCancelButton(false);
         }

         this.getSkinnable().sceneProperty().removeListener(this.weakSceneChangeListener);
         super.dispose();
         if (this.behavior != null) {
            this.behavior.dispose();
         }
      }
   }

   private void setDefaultButton(boolean var1) {
      this.setDefaultButton(this.getSkinnable().getScene(), var1);
   }

   private void setDefaultButton(Scene var1, boolean var2) {
      if (var1 != null) {
         KeyCode var3 = KeyCode.ENTER;
         this.defaultAcceleratorKeyCodeCombination = new KeyCodeCombination(var3, new Modifier[0]);
         Runnable var4 = (Runnable)var1.getAccelerators().get(this.defaultAcceleratorKeyCodeCombination);
         if (!var2) {
            if (this.defaultButtonRunnable.equals(var4)) {
               var1.getAccelerators().remove(this.defaultAcceleratorKeyCodeCombination);
            }
         } else if (!this.defaultButtonRunnable.equals(var4)) {
            var1.getAccelerators().remove(this.defaultAcceleratorKeyCodeCombination);
            var1.getAccelerators().put(this.defaultAcceleratorKeyCodeCombination, this.defaultButtonRunnable);
         }
      }
   }

   private void setCancelButton(boolean var1) {
      this.setCancelButton(this.getSkinnable().getScene(), var1);
   }

   private void setCancelButton(Scene var1, boolean var2) {
      if (var1 != null) {
         KeyCode var3 = KeyCode.ESCAPE;
         this.cancelAcceleratorKeyCodeCombination = new KeyCodeCombination(var3, new Modifier[0]);
         Runnable var4 = (Runnable)var1.getAccelerators().get(this.cancelAcceleratorKeyCodeCombination);
         if (!var2) {
            if (this.cancelButtonRunnable.equals(var4)) {
               var1.getAccelerators().remove(this.cancelAcceleratorKeyCodeCombination);
            }
         } else if (!this.cancelButtonRunnable.equals(var4)) {
            var1.getAccelerators().remove(this.cancelAcceleratorKeyCodeCombination);
            var1.getAccelerators().put(this.cancelAcceleratorKeyCodeCombination, this.cancelButtonRunnable);
         }
      }
   }
}
