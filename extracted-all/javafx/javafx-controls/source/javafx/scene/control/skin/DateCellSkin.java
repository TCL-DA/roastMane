package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.DateCellBehavior;
import javafx.scene.control.DateCell;
import javafx.scene.text.Text;

public class DateCellSkin extends CellSkinBase<DateCell> {
   private final BehaviorBase<DateCell> behavior;

   public DateCellSkin(DateCell var1) {
      super(var1);
      this.behavior = new DateCellBehavior(var1);
      var1.setMaxWidth(Double.MAX_VALUE);
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   @Override
   protected void updateChildren() {
      super.updateChildren();
      Text var1 = (Text)this.getSkinnable().getProperties().get("DateCell.secondaryText");
      if (var1 != null) {
         var1.setManaged(false);
         this.getChildren().add(var1);
      }
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      super.layoutChildren(var1, var3, var5, var7);
      Text var9 = (Text)this.getSkinnable().getProperties().get("DateCell.secondaryText");
      if (var9 != null) {
         double var10 = var1 + var5 - this.rightLabelPadding() - var9.getLayoutBounds().getWidth();
         double var12 = var3 + var7 - this.bottomLabelPadding() - var9.getLayoutBounds().getHeight();
         var9.relocate(this.snapPositionX(var10), this.snapPositionY(var12));
      }
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      double var11 = super.computePrefWidth(var1, var3, var5, var7, var9);
      return this.snapSizeX(Math.max(var11, this.cellSize()));
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = super.computePrefHeight(var1, var3, var5, var7, var9);
      return this.snapSizeY(Math.max(var11, this.cellSize()));
   }

   private double cellSize() {
      double var1 = this.getCellSize();
      Text var3 = (Text)this.getSkinnable().getProperties().get("DateCell.secondaryText");
      if (var3 != null && var1 == 24.0) {
         var1 = 36.0;
      }

      return var1;
   }
}
