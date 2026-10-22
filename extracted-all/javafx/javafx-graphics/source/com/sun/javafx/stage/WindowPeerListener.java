package com.sun.javafx.stage;

import com.sun.javafx.tk.FocusCause;
import com.sun.javafx.tk.TKStageListener;
import javafx.event.Event;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class WindowPeerListener implements TKStageListener {
   private final Window window;

   public WindowPeerListener(Window var1) {
      this.window = var1;
   }

   @Override
   public void changedLocation(float var1, float var2) {
      WindowHelper.notifyLocationChanged(this.window, (double)var1, (double)var2);
   }

   @Override
   public void changedSize(float var1, float var2) {
      WindowHelper.notifySizeChanged(this.window, (double)var1, (double)var2);
   }

   @Override
   public void changedScale(float var1, float var2) {
      WindowHelper.notifyScaleChanged(this.window, (double)var1, (double)var2);
   }

   @Override
   public void changedFocused(boolean var1, FocusCause var2) {
      WindowHelper.setFocused(this.window, var1);
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
   public void changedAlwaysOnTop(boolean var1) {
   }

   @Override
   public void changedScreen(Object var1, Object var2) {
      WindowHelper.getWindowAccessor().notifyScreenChanged(this.window, var1, var2);
   }

   @Override
   public void closing() {
      Event.fireEvent(this.window, new WindowEvent(this.window, WindowEvent.WINDOW_CLOSE_REQUEST));
   }

   @Override
   public void closed() {
      if (this.window.isShowing()) {
         this.window.hide();
      }
   }

   @Override
   public void focusUngrab() {
      Event.fireEvent(this.window, new FocusUngrabEvent());
   }
}
