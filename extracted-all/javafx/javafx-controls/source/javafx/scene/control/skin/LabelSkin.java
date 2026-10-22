package javafx.scene.control.skin;

import javafx.scene.control.Label;

public class LabelSkin extends LabeledSkinBase<Label> {
   public LabelSkin(Label var1) {
      super(var1);
      this.consumeMouseEvents(false);
      this.registerChangeListener(var1.labelForProperty(), var1x -> this.mnemonicTargetChanged());
   }
}
