package com.sun.javafx.webkit.theme;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.webkit.Invoker;
import com.sun.webkit.PopupMenu;
import com.sun.webkit.WebPage;
import com.sun.webkit.WebPageClient;
import com.sun.webkit.graphics.WCFont;
import com.sun.webkit.graphics.WCPoint;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.web.WebView;

public final class PopupMenuImpl extends PopupMenu {
   private static final PlatformLogger log = PlatformLogger.getLogger(PopupMenuImpl.class.getName());
   private final ContextMenu popupMenu = new ContextMenu();

   public PopupMenuImpl() {
      this.popupMenu.setOnHidden(var1 -> {
         log.finer("onHidden");
         Invoker.getInvoker().postOnEventThread(() -> {
            log.finer("onHidden: notifying");
            this.notifyPopupClosed();
         });
      });
      this.popupMenu.setOnAction(var1 -> {
         MenuItem var2 = (MenuItem)var1.getTarget();
         log.fine("onAction: item={0}", new Object[]{var2});
         this.notifySelectionCommited(this.popupMenu.getItems().indexOf(var2));
      });
   }

   @Override
   protected void show(WebPage var1, int var2, int var3, int var4) {
      if (log.isLoggable(Level.FINE)) {
         log.fine("show at [{0}, {1}], width={2}", new Object[]{var2, var3, var4});
      }

      this.popupMenu.setPrefWidth((double)var4);
      this.popupMenu.setPrefHeight(this.popupMenu.getHeight());
      doShow(this.popupMenu, var1, var2, var3);
   }

   @Override
   protected void hide() {
      log.fine("hiding");
      this.popupMenu.hide();
   }

   @Override
   protected void appendItem(String var1, boolean var2, boolean var3, boolean var4, int var5, int var6, WCFont var7) {
      if (log.isLoggable(Level.FINEST)) {
         log.finest(
            "itemText={0}, isLabel={1}, isSeparator={2}, isEnabled={3}, bgColor={4}, fgColor={5}, font={6}",
            new Object[]{var1, var2, var3, var4, var5, var6, var7}
         );
      }

      Object var8;
      if (var3) {
         var8 = new ContextMenuImpl.SeparatorImpl(null);
      } else {
         var8 = new MenuItem(var1);
         var8.setDisable(!var4);
      }

      var8.setMnemonicParsing(false);
      this.popupMenu.getItems().add(var8);
   }

   @Override
   protected void setSelectedItem(int var1) {
      log.finest("index={0}", new Object[]{var1});
   }

   static void doShow(ContextMenu var0, WebPage var1, int var2, int var3) {
      WebPageClient var4 = var1.getPageClient();

      assert var4 != null;

      WCPoint var5 = var4.windowToScreen(new WCPoint((float)var2, (float)var3));
      var0.show(((WebView)var4.getContainer()).getScene().getWindow(), (double)var5.getX(), (double)var5.getY());
   }
}
