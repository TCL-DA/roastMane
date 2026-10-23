package com.cropster.roastingintelligence.ui.util;

import java.text.NumberFormat;
import javafx.scene.control.ListCell;
import org.cropster.roastingintelligence.model.Machine;

public class MachineListCell extends ListCell<Machine> {
   private NumberFormat numberFormat = NumberFormat.getNumberInstance();

   public MachineListCell() {
      this.numberFormat.setMinimumFractionDigits(0);
      this.numberFormat.setMaximumFractionDigits(2);
   }

   protected void updateItem(Machine item, boolean empty) {
      super.updateItem(item, empty);
      if (!empty && item != null) {
         String text = item.toString();
         if (item.getLocation() != null) {
            text = text + " @ " + item.getLocation().getName();
         }

         if (item.getCapacity() != null) {
            text = text
               + String.format(
                  " (%s %s)",
                  this.numberFormat.format(item.getCapacity().getAmount()),
                  org.cropster.roastingintelligence.util.Messages.m_(item.getCapacity().getUnit())
               );
         }

         this.setText(text);
      } else {
         this.setText(org.cropster.roastingintelligence.util.Messages.c_("Machine", "None"));
      }

      this.setGraphic(null);
   }
}
