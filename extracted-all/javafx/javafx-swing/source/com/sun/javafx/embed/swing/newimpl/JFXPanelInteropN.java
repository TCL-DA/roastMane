package com.sun.javafx.embed.swing.newimpl;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.Window;
import javafx.embed.swing.JFXPanel;
import jdk.swing.interop.SwingInterOpUtils;

public class JFXPanelInteropN {
   public void postEvent(JFXPanel var1, AWTEvent var2) {
      SwingInterOpUtils.postEvent(var1, var2);
   }

   public boolean isUngrabEvent(AWTEvent var1) {
      return SwingInterOpUtils.isUngrabEvent(var1);
   }

   public long getMask() {
      return -2147483632L;
   }

   public void grab(Toolkit var1, Window var2) {
      SwingInterOpUtils.grab(var1, var2);
   }

   public void ungrab(Toolkit var1, Window var2) {
      SwingInterOpUtils.ungrab(var1, var2);
   }
}
