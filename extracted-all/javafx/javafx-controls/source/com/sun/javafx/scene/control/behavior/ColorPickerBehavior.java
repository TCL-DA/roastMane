package com.sun.javafx.scene.control.behavior;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.PopupControl;
import javafx.scene.paint.Color;

public class ColorPickerBehavior extends ComboBoxBaseBehavior<Color> {
   public ColorPickerBehavior(ColorPicker var1) {
      super(var1);
   }

   @Override
   public void onAutoHide(PopupControl var1) {
      if (!var1.isShowing() && ((ComboBoxBase)this.getNode()).isShowing()) {
         ((ComboBoxBase)this.getNode()).hide();
      }

      if (!((ComboBoxBase)this.getNode()).isShowing()) {
         super.onAutoHide(var1);
      }
   }
}
