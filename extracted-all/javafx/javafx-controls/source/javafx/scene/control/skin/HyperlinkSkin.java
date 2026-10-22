package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.ButtonBehavior;
import javafx.scene.control.Hyperlink;

public class HyperlinkSkin extends LabeledSkinBase<Hyperlink> {
   private final BehaviorBase<Hyperlink> behavior;

   public HyperlinkSkin(Hyperlink var1) {
      super(var1);
      this.behavior = new ButtonBehavior<>(var1);
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }
}
