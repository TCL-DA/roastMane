package com.sun.javafx.scene.control;

import javafx.scene.AccessibleAttribute;
import javafx.scene.control.TextField;

public final class FakeFocusTextField extends TextField {
   public void requestFocus() {
      if (this.getParent() != null) {
         this.getParent().requestFocus();
      }
   }

   public void setFakeFocus(boolean var1) {
      this.setFocused(var1);
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case FOCUS_ITEM:
            return this.getParent();
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }
}
