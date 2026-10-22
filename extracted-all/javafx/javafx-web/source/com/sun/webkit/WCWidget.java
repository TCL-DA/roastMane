package com.sun.webkit;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.webkit.graphics.WCRectangle;

class WCWidget {
   private static final PlatformLogger log = PlatformLogger.getLogger(WCWidget.class.getName());
   private int x;
   private int y;
   private int width;
   private int height;
   private final WebPage page;

   WCWidget(WebPage var1) {
      this.page = var1;
   }

   WebPage getPage() {
      return this.page;
   }

   WCRectangle getBounds() {
      return new WCRectangle((float)this.x, (float)this.y, (float)this.width, (float)this.height);
   }

   void setBounds(int var1, int var2, int var3, int var4) {
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
   }

   protected void destroy() {
   }

   protected void requestFocus() {
   }

   protected void setCursor(long var1) {
   }

   protected void setVisible(boolean var1) {
   }

   private void fwkDestroy() {
      log.finer("destroy");
      this.destroy();
   }

   private void fwkSetBounds(int var1, int var2, int var3, int var4) {
      if (log.isLoggable(Level.FINER)) {
         log.finer("setBounds({0}, {1}, {2}, {3})", new Object[]{var1, var2, var3, var4});
      }

      this.setBounds(var1, var2, var3, var4);
   }

   private void fwkRequestFocus() {
      log.finer("requestFocus");
      this.requestFocus();
   }

   private void fwkSetCursor(long var1) {
      if (log.isLoggable(Level.FINER)) {
         log.finer("setCursor({0})", new Object[]{var1});
      }

      this.setCursor(var1);
   }

   private void fwkSetVisible(boolean var1) {
      if (log.isLoggable(Level.FINER)) {
         log.finer("setVisible({0})", new Object[]{var1});
      }

      this.setVisible(var1);
   }

   protected int fwkGetScreenDepth() {
      log.finer("getScreenDepth");
      WebPageClient var1 = this.page.getPageClient();
      return var1 != null ? var1.getScreenDepth() : 24;
   }

   protected WCRectangle fwkGetScreenRect(boolean var1) {
      if (log.isLoggable(Level.FINER)) {
         log.finer("getScreenRect({0})", new Object[]{var1});
      }

      WebPageClient var2 = this.page.getPageClient();
      return var2 != null ? var2.getScreenBounds(var1) : null;
   }

   private static native void initIDs();

   static {
      initIDs();
   }
}
