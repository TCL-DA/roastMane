package com.sun.javafx.stage;

import com.sun.javafx.tk.FocusCause;
import javafx.stage.PopupWindow;

public class PopupWindowPeerListener extends WindowPeerListener {
   private final PopupWindow popupWindow;

   public PopupWindowPeerListener(PopupWindow var1) {
      super(var1);
      this.popupWindow = var1;
   }

   @Override
   public void changedFocused(boolean var1, FocusCause var2) {
      WindowHelper.setFocused(this.popupWindow, var1);
   }

   @Override
   public void closing() {
   }

   @Override
   public void changedLocation(float var1, float var2) {
   }

   @Override
   public void changedIconified(boolean var1) {
   }

   @Override
   public void changedMaximized(boolean var1) {
   }

   @Override
   public void changedResizable(boolean var1) {
   }

   @Override
   public void changedFullscreen(boolean var1) {
   }

   @Override
   public void focusUngrab() {
   }
}
