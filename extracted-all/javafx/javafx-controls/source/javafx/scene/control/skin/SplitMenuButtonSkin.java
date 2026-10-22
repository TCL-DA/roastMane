package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.SplitMenuButtonBehavior;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.input.MouseEvent;

public class SplitMenuButtonSkin extends MenuButtonSkinBase<SplitMenuButton> {
   private final SplitMenuButtonBehavior behavior;

   public SplitMenuButtonSkin(SplitMenuButton var1) {
      super(var1);
      this.behavior = new SplitMenuButtonBehavior(var1);
      this.behaveLikeButton = true;
      this.arrowButton.addEventHandler(MouseEvent.ANY, var0 -> var0.consume());
      this.arrowButton.setOnMousePressed(var1x -> {
         this.getBehavior().mousePressed(var1x, false);
         var1x.consume();
      });
      this.arrowButton.setOnMouseReleased(var1x -> {
         this.getBehavior().mouseReleased(var1x, false);
         var1x.consume();
      });
      this.label.setLabelFor(var1);
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   SplitMenuButtonBehavior getBehavior() {
      return this.behavior;
   }
}
