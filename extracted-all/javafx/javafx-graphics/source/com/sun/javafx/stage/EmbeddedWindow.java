package com.sun.javafx.stage;

import com.sun.javafx.embed.HostInterface;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.Scene;
import javafx.stage.Window;

public class EmbeddedWindow extends Window {
   private HostInterface host;

   public EmbeddedWindow(HostInterface var1) {
      this.host = var1;
      EmbeddedWindowHelper.initHelper(this);
   }

   public HostInterface getHost() {
      return this.host;
   }

   @Override
   public final void setScene(Scene var1) {
      super.setScene(var1);
   }

   @Override
   public final void show() {
      super.show();
   }

   private void doVisibleChanging(boolean var1) {
      Toolkit var2 = Toolkit.getToolkit();
      if (var1 && WindowHelper.getPeer(this) == null) {
         WindowHelper.setPeer(this, var2.createTKEmbeddedStage(this.host, WindowHelper.getAccessControlContext(this)));
         WindowHelper.setPeerListener(this, new WindowPeerListener(this));
      }
   }

   static {
      EmbeddedWindowHelper.setEmbeddedWindowAccessor(new EmbeddedWindowHelper.EmbeddedWindowAccessor() {
         @Override
         public void doVisibleChanging(Window var1, boolean var2) {
            ((EmbeddedWindow)var1).doVisibleChanging(var2);
         }
      });
   }
}
