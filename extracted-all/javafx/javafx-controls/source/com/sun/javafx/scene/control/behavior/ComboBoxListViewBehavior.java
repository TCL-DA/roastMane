package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.KeyCode;

public class ComboBoxListViewBehavior<T> extends ComboBoxBaseBehavior<T> {
   public ComboBoxListViewBehavior(ComboBox<T> var1) {
      super(var1);
      InputMap var2 = new InputMap(var1);
      var2.getMappings()
         .addAll(
            new InputMap.Mapping[]{
               new InputMap.KeyMapping(KeyCode.UP, var1x -> this.selectPrevious()), new InputMap.KeyMapping(KeyCode.DOWN, var1x -> this.selectNext())
            }
         );
      this.addDefaultChildMap(this.getInputMap(), var2);
   }

   private ComboBox<T> getComboBox() {
      return (ComboBox<T>)this.getNode();
   }

   private void selectPrevious() {
      SingleSelectionModel var1 = this.getComboBox().getSelectionModel();
      if (var1 != null) {
         var1.selectPrevious();
      }
   }

   private void selectNext() {
      SingleSelectionModel var1 = this.getComboBox().getSelectionModel();
      if (var1 != null) {
         var1.selectNext();
      }
   }
}
