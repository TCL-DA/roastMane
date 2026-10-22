package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.DatePickerContent;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.traversal.Direction;
import java.time.temporal.ChronoUnit;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.DateCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class DateCellBehavior extends BehaviorBase<DateCell> {
   private final InputMap<DateCell> inputMap = this.createInputMap();

   public DateCellBehavior(DateCell var1) {
      super(var1);
      this.addDefaultMapping(
         this.inputMap,
         new InputMap.Mapping[]{
            new InputMap.KeyMapping(KeyCode.UP, var2 -> this.traverse(var1, Direction.UP)),
            new InputMap.KeyMapping(KeyCode.DOWN, var2 -> this.traverse(var1, Direction.DOWN)),
            new InputMap.KeyMapping(KeyCode.LEFT, var2 -> this.traverse(var1, Direction.LEFT)),
            new InputMap.KeyMapping(KeyCode.RIGHT, var2 -> this.traverse(var1, Direction.RIGHT)),
            new InputMap.KeyMapping(KeyCode.ENTER, KeyEvent.KEY_RELEASED, var1x -> this.selectDate()),
            new InputMap.KeyMapping(KeyCode.SPACE, KeyEvent.KEY_RELEASED, var1x -> this.selectDate())
         }
      );
   }

   @Override
   public InputMap<DateCell> getInputMap() {
      return this.inputMap;
   }

   private void selectDate() {
      DateCell var1 = this.getNode();
      DatePickerContent var2 = this.findDatePickerContent(var1);
      var2.selectDayCell(var1);
   }

   public void traverse(DateCell var1, Direction var2) {
      boolean var3 = var1.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
      DatePickerContent var4 = this.findDatePickerContent(var1);
      if (var4 != null) {
         switch (var2) {
            case UP:
               var4.goToDayCell(var1, -1, ChronoUnit.WEEKS, true);
               break;
            case DOWN:
               var4.goToDayCell(var1, 1, ChronoUnit.WEEKS, true);
               break;
            case LEFT:
               var4.goToDayCell(var1, var3 ? 1 : -1, ChronoUnit.DAYS, true);
               break;
            case RIGHT:
               var4.goToDayCell(var1, var3 ? -1 : 1, ChronoUnit.DAYS, true);
         }
      }
   }

   protected DatePickerContent findDatePickerContent(Node var1) {
      Parent var2;
      while ((var2 = var1.getParent()) != null && !(var2 instanceof DatePickerContent)) {
      }

      return (DatePickerContent)var2;
   }
}
