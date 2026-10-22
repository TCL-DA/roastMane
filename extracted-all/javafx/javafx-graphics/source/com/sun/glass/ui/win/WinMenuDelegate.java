package com.sun.glass.ui.win;

import com.sun.glass.ui.Menu;
import com.sun.glass.ui.Pixels;
import com.sun.glass.ui.delegate.MenuDelegate;
import com.sun.glass.ui.delegate.MenuItemDelegate;

final class WinMenuDelegate extends WinMenuImpl implements MenuDelegate {
   private final Menu owner;
   private WinMenuImpl parent = null;

   public WinMenuDelegate(Menu var1) {
      this.owner = var1;
   }

   public Menu getOwner() {
      return this.owner;
   }

   @Override
   public boolean createMenu(String var1, boolean var2) {
      return this.create();
   }

   public void dispose() {
      this.destroy();
   }

   @Override
   public boolean setTitle(String var1) {
      return this.parent != null ? this.parent.setSubmenuTitle(this, var1) : true;
   }

   @Override
   public boolean setEnabled(boolean var1) {
      return this.parent != null ? this.parent.enableSubmenu(this, var1) : true;
   }

   @Override
   public boolean setPixels(Pixels var1) {
      return false;
   }

   @Override
   public boolean insert(MenuDelegate var1, int var2) {
      return this.insertSubmenu((WinMenuDelegate)var1, var2);
   }

   @Override
   public boolean insert(MenuItemDelegate var1, int var2) {
      return this.insertItem((WinMenuItemDelegate)var1, var2);
   }

   @Override
   public boolean remove(MenuDelegate var1, int var2) {
      return this.removeMenu((WinMenuDelegate)var1, var2);
   }

   @Override
   public boolean remove(MenuItemDelegate var1, int var2) {
      return this.removeItem((WinMenuItemDelegate)var1, var2);
   }

   WinMenuImpl getParent() {
      return this.parent;
   }

   void setParent(WinMenuImpl var1) {
      this.parent = var1;
   }
}
