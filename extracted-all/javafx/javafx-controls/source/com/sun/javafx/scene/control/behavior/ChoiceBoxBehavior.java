package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ChoiceBoxBehavior<T> extends BehaviorBase<ChoiceBox<T>> {
   private final InputMap<ChoiceBox<T>> choiceBoxInputMap = this.createInputMap();
   private TwoLevelFocusComboBehavior tlFocus;

   public ChoiceBoxBehavior(ChoiceBox<T> var1) {
      super(var1);
      this.addDefaultMapping(
         this.choiceBoxInputMap,
         new InputMap.Mapping[]{
            new InputMap.KeyMapping(KeyCode.SPACE, KeyEvent.KEY_PRESSED, this::keyPressed),
            new InputMap.KeyMapping(KeyCode.SPACE, KeyEvent.KEY_RELEASED, this::keyReleased),
            new InputMap.KeyMapping(KeyCode.ESCAPE, KeyEvent.KEY_RELEASED, var1x -> this.cancel()),
            new InputMap.KeyMapping(KeyCode.DOWN, KeyEvent.KEY_RELEASED, var1x -> this.showPopup()),
            new InputMap.KeyMapping(KeyCode.CANCEL, KeyEvent.KEY_RELEASED, var1x -> this.cancel()),
            new InputMap.MouseMapping(MouseEvent.MOUSE_PRESSED, this::mousePressed),
            new InputMap.MouseMapping(MouseEvent.MOUSE_RELEASED, this::mouseReleased)
         }
      );
      InputMap var2 = new InputMap(var1);
      var2.setInterceptor(var0 -> !Utils.isTwoLevelFocus());
      var2.getMappings()
         .addAll(
            new InputMap.Mapping[]{
               new InputMap.KeyMapping(KeyCode.ENTER, KeyEvent.KEY_PRESSED, this::keyPressed),
               new InputMap.KeyMapping(KeyCode.ENTER, KeyEvent.KEY_RELEASED, this::keyReleased)
            }
         );
      this.addDefaultChildMap(this.choiceBoxInputMap, var2);
      if (Utils.isTwoLevelFocus()) {
         this.tlFocus = new TwoLevelFocusComboBehavior(var1);
      }
   }

   @Override
   public InputMap<ChoiceBox<T>> getInputMap() {
      return this.choiceBoxInputMap;
   }

   @Override
   public void dispose() {
      if (this.tlFocus != null) {
         this.tlFocus.dispose();
      }

      super.dispose();
   }

   public void select(int var1) {
      SingleSelectionModel var2 = this.getNode().getSelectionModel();
      if (var2 != null) {
         var2.select(var1);
      }
   }

   public void close() {
      this.getNode().hide();
   }

   public void showPopup() {
      this.getNode().show();
   }

   public void mousePressed(MouseEvent var1) {
      ChoiceBox var2 = this.getNode();
      if (var2.isFocusTraversable()) {
         var2.requestFocus();
      }
   }

   public void mouseReleased(MouseEvent var1) {
      ChoiceBox var2 = this.getNode();
      if (var2.isShowing() || !var2.contains(var1.getX(), var1.getY())) {
         var2.hide();
      } else if (var1.getButton() == MouseButton.PRIMARY) {
         var2.show();
      }
   }

   private void keyPressed(KeyEvent var1) {
      ChoiceBox var2 = this.getNode();
      if (!var2.isShowing()) {
         var2.show();
      }
   }

   private void keyReleased(KeyEvent var1) {
   }

   public void cancel() {
      ChoiceBox var1 = this.getNode();
      var1.hide();
   }
}
