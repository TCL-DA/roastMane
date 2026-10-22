package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.scene.control.Pagination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

public class PaginationBehavior extends BehaviorBase<Pagination> {
   private final InputMap<Pagination> paginationInputMap = this.createInputMap();

   public PaginationBehavior(Pagination var1) {
      super(var1);
      this.addDefaultMapping(
         this.paginationInputMap,
         new InputMap.Mapping[]{
            new InputMap.KeyMapping(KeyCode.LEFT, var2 -> this.rtl(var1, this::right, this::left)),
            new InputMap.KeyMapping(KeyCode.RIGHT, var2 -> this.rtl(var1, this::left, this::right)),
            new InputMap.MouseMapping(MouseEvent.MOUSE_PRESSED, this::mousePressed)
         }
      );
   }

   @Override
   public InputMap<Pagination> getInputMap() {
      return this.paginationInputMap;
   }

   public void mousePressed(MouseEvent var1) {
      this.getNode().requestFocus();
   }

   private void left() {
      this.movePage(-1);
   }

   private void right() {
      this.movePage(1);
   }

   private void movePage(int var1) {
      Pagination var2 = this.getNode();
      int var3 = var2.getCurrentPageIndex();
      var2.setCurrentPageIndex(var3 + var1);
   }
}
