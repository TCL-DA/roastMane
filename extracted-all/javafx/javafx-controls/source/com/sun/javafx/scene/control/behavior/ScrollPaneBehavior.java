package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.inputmap.InputMap;
import java.util.Optional;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.skin.ScrollPaneSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

public class ScrollPaneBehavior extends BehaviorBase<ScrollPane> {
   private final InputMap<ScrollPane> inputMap = this.createInputMap();

   public ScrollPaneBehavior(ScrollPane var1) {
      super(var1);
      this.addDefaultMapping(
         this.inputMap,
         new InputMap.Mapping[]{
            new InputMap.KeyMapping(KeyCode.LEFT, var2 -> this.rtl(var1, this::horizontalUnitIncrement, this::horizontalUnitDecrement)),
            new InputMap.KeyMapping(KeyCode.RIGHT, var2 -> this.rtl(var1, this::horizontalUnitDecrement, this::horizontalUnitIncrement)),
            new InputMap.KeyMapping(KeyCode.UP, var1x -> this.verticalUnitDecrement()),
            new InputMap.KeyMapping(KeyCode.DOWN, var1x -> this.verticalUnitIncrement()),
            new InputMap.KeyMapping(KeyCode.PAGE_UP, var1x -> this.verticalPageDecrement()),
            new InputMap.KeyMapping(KeyCode.PAGE_DOWN, var1x -> this.verticalPageIncrement()),
            new InputMap.KeyMapping(KeyCode.SPACE, var1x -> this.verticalPageIncrement()),
            new InputMap.KeyMapping(KeyCode.HOME, var1x -> this.verticalHome()),
            new InputMap.KeyMapping(KeyCode.END, var1x -> this.verticalEnd()),
            new InputMap.MouseMapping(MouseEvent.MOUSE_PRESSED, this::mousePressed)
         }
      );
   }

   @Override
   public InputMap<ScrollPane> getInputMap() {
      return this.inputMap;
   }

   public void horizontalUnitIncrement() {
      this.getHorizontalScrollBar().ifPresent(ScrollBar::increment);
   }

   public void horizontalUnitDecrement() {
      this.getHorizontalScrollBar().ifPresent(ScrollBar::decrement);
   }

   public void verticalUnitIncrement() {
      this.getVerticalScrollBar().ifPresent(ScrollBar::increment);
   }

   void verticalUnitDecrement() {
      this.getVerticalScrollBar().ifPresent(ScrollBar::decrement);
   }

   void horizontalPageIncrement() {
      this.getHorizontalScrollBar().ifPresent(ScrollBar::increment);
   }

   void horizontalPageDecrement() {
      this.getHorizontalScrollBar().ifPresent(ScrollBar::decrement);
   }

   void verticalPageIncrement() {
      this.getVerticalScrollBar().ifPresent(ScrollBar::increment);
   }

   void verticalPageDecrement() {
      this.getVerticalScrollBar().ifPresent(ScrollBar::decrement);
   }

   void verticalHome() {
      ScrollPane var1 = this.getNode();
      var1.setHvalue(var1.getHmin());
      var1.setVvalue(var1.getVmin());
   }

   void verticalEnd() {
      ScrollPane var1 = this.getNode();
      var1.setHvalue(var1.getHmax());
      var1.setVvalue(var1.getVmax());
   }

   private Optional<ScrollBar> getVerticalScrollBar() {
      return Optional.ofNullable(((ScrollPaneSkin)this.getNode().getSkin()).getVerticalScrollBar());
   }

   private Optional<ScrollBar> getHorizontalScrollBar() {
      return Optional.ofNullable(((ScrollPaneSkin)this.getNode().getSkin()).getHorizontalScrollBar());
   }

   public void mousePressed(MouseEvent var1) {
      this.getNode().requestFocus();
   }
}
