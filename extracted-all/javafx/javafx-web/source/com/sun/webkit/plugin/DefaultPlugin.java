package com.sun.webkit.plugin;

import com.sun.prism.paint.Color;
import com.sun.webkit.graphics.WCGraphicsContext;
import java.io.IOError;
import java.net.URL;

final class DefaultPlugin implements Plugin {
   private int x = 0;
   private int y = 0;
   private int w = 0;
   private int h = 0;

   private void init(String var1) {
   }

   DefaultPlugin(URL var1, String var2, String[] var3, String[] var4) {
      this.init("Default Plugin for: " + (null == var1 ? "(null)" : var1.toExternalForm()));
   }

   @Override
   public void paint(WCGraphicsContext var1, int var2, int var3, int var4, int var5) {
      var1.fillRect((float)this.x, (float)this.y, (float)this.w, (float)this.h, new Color(0.6666667F, 1.0F, 1.0F, 0.06666667F));
   }

   @Override
   public void activate(Object var1, PluginListener var2) {
   }

   @Override
   public void destroy() {
   }

   @Override
   public void setVisible(boolean var1) {
   }

   @Override
   public void setEnabled(boolean var1) {
   }

   @Override
   public void setBounds(int var1, int var2, int var3, int var4) {
      this.x = var1;
      this.y = var2;
      this.w = var3;
      this.h = var4;
   }

   @Override
   public Object invoke(String var1, String var2, Object[] var3) throws IOError {
      return null;
   }

   @Override
   public boolean handleMouseEvent(
      String var1, int var2, int var3, int var4, int var5, int var6, boolean var7, boolean var8, boolean var9, boolean var10, boolean var11, long var12
   ) {
      return false;
   }

   @Override
   public void requestFocus() {
   }

   @Override
   public void setNativeContainerBounds(int var1, int var2, int var3, int var4) {
   }
}
