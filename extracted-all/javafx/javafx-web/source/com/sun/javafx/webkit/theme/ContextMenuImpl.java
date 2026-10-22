package com.sun.javafx.webkit.theme;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.webkit.ContextMenu;
import com.sun.webkit.ContextMenuItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;

public final class ContextMenuImpl extends ContextMenu {
   private static final PlatformLogger log = PlatformLogger.getLogger(ContextMenuImpl.class.getName());
   private final ObservableList<ContextMenuItem> items = FXCollections.observableArrayList();

   @Override
   protected void show(ContextMenu.ShowContext var1, int var2, int var3) {
      if (log.isLoggable(Level.FINE)) {
         log.fine("show at [{0}, {1}]", new Object[]{var2, var3});
      }

      javafx.scene.control.ContextMenu var4 = new javafx.scene.control.ContextMenu();
      var4.setOnAction(var1x -> {
         MenuItem var2x = (MenuItem)var1x.getTarget();
         log.fine("onAction: item={0}", new Object[]{var2x});
         var1.notifyItemSelected(((ContextMenuImpl.MenuItemPeer)var2x).getItemPeer().getAction());
      });
      var4.getItems().addAll(this.fillMenu());
      PopupMenuImpl.doShow(var4, var1.getPage(), var2, var3);
   }

   @Override
   protected void appendItem(ContextMenuItem var1) {
      this.insertItem(var1, this.items.size());
   }

   @Override
   protected void insertItem(ContextMenuItem var1, int var2) {
      if (log.isLoggable(Level.FINE)) {
         log.fine("item={0}, index={1}", new Object[]{var1, var2});
      }

      if (var1 != null) {
         this.items.remove(var1);
         if (this.items.size() == 0) {
            this.items.add(var1);
         } else {
            this.items.add(var2, var1);
         }
      }
   }

   @Override
   protected int getItemCount() {
      return this.items.size();
   }

   private MenuItem createMenuItem(ContextMenuItem var1) {
      log.fine("item={0}", new Object[]{var1});
      if (var1.getType() == 2) {
         ContextMenuImpl.MenuImpl var4 = new ContextMenuImpl.MenuImpl(var1.getTitle());
         if (var1.getSubmenu() != null) {
            var4.getItems().addAll(((ContextMenuImpl)var1.getSubmenu()).fillMenu());
         }

         return var4;
      } else if (var1.getType() == 0) {
         Object var2 = null;
         if (var1.isChecked()) {
            var2 = new ContextMenuImpl.CheckMenuItemImpl(var1);
         } else {
            var2 = new ContextMenuImpl.MenuItemImpl(var1);
         }

         var2.setDisable(!var1.isEnabled());
         return (MenuItem)var2;
      } else if (var1.getType() == 1) {
         return new ContextMenuImpl.SeparatorImpl(var1);
      } else {
         throw new IllegalArgumentException("unexpected item type");
      }
   }

   private ObservableList<MenuItem> fillMenu() {
      ObservableList var1 = FXCollections.observableArrayList();

      for (ContextMenuItem var3 : this.items) {
         var1.add(this.createMenuItem(var3));
      }

      return var1;
   }

   private static final class CheckMenuItemImpl extends CheckMenuItem implements ContextMenuImpl.MenuItemPeer {
      private final ContextMenuItem itemPeer;

      private CheckMenuItemImpl(ContextMenuItem var1) {
         this.itemPeer = var1;
      }

      @Override
      public ContextMenuItem getItemPeer() {
         return this.itemPeer;
      }
   }

   private static final class MenuImpl extends Menu {
      private MenuImpl(String var1) {
         super(var1);
      }
   }

   private static final class MenuItemImpl extends MenuItem implements ContextMenuImpl.MenuItemPeer {
      private final ContextMenuItem itemPeer;

      private MenuItemImpl(ContextMenuItem var1) {
         super(var1.getTitle());
         this.itemPeer = var1;
      }

      @Override
      public ContextMenuItem getItemPeer() {
         return this.itemPeer;
      }
   }

   private interface MenuItemPeer {
      ContextMenuItem getItemPeer();
   }

   static final class SeparatorImpl extends MenuItem implements ContextMenuImpl.MenuItemPeer {
      private final ContextMenuItem itemPeer;

      SeparatorImpl(ContextMenuItem var1) {
         this.itemPeer = var1;
         this.setGraphic(new Separator());
         this.setDisable(true);
      }

      @Override
      public ContextMenuItem getItemPeer() {
         return this.itemPeer;
      }
   }
}
