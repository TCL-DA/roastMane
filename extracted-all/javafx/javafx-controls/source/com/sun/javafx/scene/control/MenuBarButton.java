package com.sun.javafx.scene.control;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.skin.MenuBarSkin;

public class MenuBarButton extends MenuButton {
   public ChangeListener<Boolean> menuListener;
   public MenuBarSkin menuBarSkin;
   public Menu menu;
   private final ListChangeListener<MenuItem> itemsListener;
   private final ListChangeListener<String> styleClassListener;

   public MenuBarButton(MenuBarSkin var1, Menu var2) {
      super(var2.getText(), var2.getGraphic());
      this.menuBarSkin = var1;
      this.setAccessibleRole(AccessibleRole.MENU);
      var2.getItems().addListener(this.itemsListener = var1x -> {
         while (var1x.next()) {
            this.getItems().removeAll(var1x.getRemoved());
            this.getItems().addAll(var1x.getFrom(), var1x.getAddedSubList());
         }
      });
      var2.getStyleClass().addListener(this.styleClassListener = var2x -> {
         while (var2x.next()) {
            for (int var3 = var2x.getFrom(); var3 < var2x.getTo(); var3++) {
               this.getStyleClass().add((String)var2.getStyleClass().get(var3));
            }

            for (String var4 : var2x.getRemoved()) {
               this.getStyleClass().remove(var4);
            }
         }
      });
      this.idProperty().bind(var2.idProperty());
   }

   public MenuBarSkin getMenuBarSkin() {
      return this.menuBarSkin;
   }

   public void clearHover() {
      this.setHover(false);
   }

   public void setHover() {
      this.setHover(true);
      this.menuBarSkin.getSkinnable().notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_NODE);
   }

   public void dispose() {
      this.menu.getItems().removeListener(this.itemsListener);
      this.menu.getStyleClass().removeListener(this.styleClassListener);
      this.idProperty().unbind();
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case FOCUS_ITEM:
            return this;
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }
}
