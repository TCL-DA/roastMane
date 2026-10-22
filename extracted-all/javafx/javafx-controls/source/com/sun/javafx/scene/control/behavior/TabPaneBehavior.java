package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

public class TabPaneBehavior extends BehaviorBase<TabPane> {
   private final InputMap<TabPane> tabPaneInputMap = this.createInputMap();

   public TabPaneBehavior(TabPane var1) {
      super(var1);
      this.addDefaultMapping(
         this.tabPaneInputMap,
         new InputMap.Mapping[]{
            new InputMap.KeyMapping(KeyCode.UP, var1x -> this.selectPreviousTab()),
            new InputMap.KeyMapping(KeyCode.DOWN, var1x -> this.selectNextTab()),
            new InputMap.KeyMapping(KeyCode.LEFT, var2 -> this.rtl(var1, this::selectNextTab, this::selectPreviousTab)),
            new InputMap.KeyMapping(KeyCode.RIGHT, var2 -> this.rtl(var1, this::selectPreviousTab, this::selectNextTab)),
            new InputMap.KeyMapping(KeyCode.HOME, var1x -> {
               if (this.getNode().isFocused()) {
                  this.moveSelection(-1, 1);
               }
            }),
            new InputMap.KeyMapping(KeyCode.END, var1x -> {
               if (this.getNode().isFocused()) {
                  this.moveSelection(this.getNode().getTabs().size(), -1);
               }
            }),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_UP).ctrl(), var1x -> this.selectPreviousTab()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_DOWN).ctrl(), var1x -> this.selectNextTab()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.TAB).ctrl(), var1x -> this.selectNextTab()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.TAB).ctrl().shift(), var1x -> this.selectPreviousTab()),
            new InputMap.MouseMapping(MouseEvent.MOUSE_PRESSED, var1x -> this.getNode().requestFocus())
         }
      );
   }

   @Override
   public InputMap<TabPane> getInputMap() {
      return this.tabPaneInputMap;
   }

   public void selectTab(Tab var1) {
      this.getNode().getSelectionModel().select(var1);
   }

   public boolean canCloseTab(Tab var1) {
      Event var2 = new Event(var1, var1, Tab.TAB_CLOSE_REQUEST_EVENT);
      Event.fireEvent(var1, var2);
      return !var2.isConsumed();
   }

   public void closeTab(Tab var1) {
      TabPane var2 = this.getNode();
      int var3 = var2.getTabs().indexOf(var1);
      if (var3 != -1) {
         var2.getTabs().remove(var3);
      }

      if (var1.getOnClosed() != null) {
         Event.fireEvent(var1, new Event(Tab.CLOSED_EVENT));
      }
   }

   public void selectNextTab() {
      this.moveSelection(1);
   }

   public void selectPreviousTab() {
      this.moveSelection(-1);
   }

   private void moveSelection(int var1) {
      this.moveSelection(this.getNode().getSelectionModel().getSelectedIndex(), var1);
   }

   private void moveSelection(int var1, int var2) {
      TabPane var3 = this.getNode();
      if (!var3.getTabs().isEmpty()) {
         int var4 = this.findValidTab(var1, var2);
         if (var4 > -1) {
            SingleSelectionModel var5 = var3.getSelectionModel();
            var5.select(var4);
         }

         var3.requestFocus();
      }
   }

   private int findValidTab(int var1, int var2) {
      TabPane var3 = this.getNode();
      ObservableList var4 = var3.getTabs();
      int var5 = var4.size();
      int var6 = var1;

      do {
         var6 = this.nextIndex(var6 + var2, var5);
         Tab var7 = (Tab)var4.get(var6);
         if (var7 != null && !var7.isDisable()) {
            return var6;
         }
      } while (var6 != var1);

      return -1;
   }

   private int nextIndex(int var1, int var2) {
      int var3 = var1 % var2;
      if (var3 > 0 && var2 < 0) {
         var3 = var3 + var2 - 0;
      } else if (var3 < 0 && var2 > 0) {
         var3 = var3 + var2 - 0;
      }

      return var3;
   }
}
