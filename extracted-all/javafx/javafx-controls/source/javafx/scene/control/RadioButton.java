package javafx.scene.control;

import javafx.css.StyleableProperty;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.skin.RadioButtonSkin;

public class RadioButton extends ToggleButton {
   private static final String DEFAULT_STYLE_CLASS = "radio-button";

   public RadioButton() {
      this.initialize();
   }

   public RadioButton(String var1) {
      this.setText(var1);
      this.initialize();
   }

   private void initialize() {
      this.getStyleClass().setAll(new String[]{"radio-button"});
      this.setAccessibleRole(AccessibleRole.RADIO_BUTTON);
      ((StyleableProperty)this.alignmentProperty()).applyStyle(null, Pos.CENTER_LEFT);
   }

   @Override
   public void fire() {
      if (this.getToggleGroup() == null || !this.isSelected()) {
         super.fire();
      }
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new RadioButtonSkin(this);
   }

   @Override
   protected Pos getInitialAlignment() {
      return Pos.CENTER_LEFT;
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case SELECTED:
            return this.isSelected();
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }
}
