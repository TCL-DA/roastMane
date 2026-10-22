package com.sun.javafx.scene.control.behavior;

import java.time.LocalDate;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PopupControl;

public class DatePickerBehavior extends ComboBoxBaseBehavior<LocalDate> {
   public DatePickerBehavior(DatePicker var1) {
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
