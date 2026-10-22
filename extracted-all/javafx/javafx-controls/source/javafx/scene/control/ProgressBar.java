package javafx.scene.control;

import javafx.css.StyleableProperty;
import javafx.geometry.Orientation;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.skin.ProgressBarSkin;

public class ProgressBar extends ProgressIndicator {
   private static final String DEFAULT_STYLE_CLASS = "progress-bar";

   public ProgressBar() {
      this(-1.0);
   }

   public ProgressBar(double var1) {
      ((StyleableProperty)this.focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
      this.setProgress(var1);
      this.getStyleClass().setAll(new String[]{"progress-bar"});
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new ProgressBarSkin(this);
   }

   @Override
   protected Boolean getInitialFocusTraversable() {
      return Boolean.FALSE;
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case ORIENTATION:
            return Orientation.HORIZONTAL;
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }
}
