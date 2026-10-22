package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.ToggleButtonBehavior;
import javafx.scene.control.ToggleButton;

public class ToggleButtonSkin extends LabeledSkinBase<ToggleButton> {
   private final BehaviorBase<ToggleButton> behavior;

   public ToggleButtonSkin(ToggleButton var1) {
      super(var1);
      this.behavior = new ToggleButtonBehavior<>(var1);
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }
}
