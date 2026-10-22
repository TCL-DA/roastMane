package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.ListCellBehavior;
import javafx.geometry.Orientation;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class ListCellSkin<T> extends CellSkinBase<ListCell<T>> {
   private final BehaviorBase<ListCell<T>> behavior;

   public ListCellSkin(ListCell<T> var1) {
      super(var1);
      this.behavior = new ListCellBehavior<>(var1);
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      double var11 = super.computePrefWidth(var1, var3, var5, var7, var9);
      ListView var13 = this.getSkinnable().getListView();
      return var13 == null ? 0.0 : (var13.getOrientation() == Orientation.VERTICAL ? var11 : Math.max(var11, this.getCellSize()));
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = this.getFixedCellSize();
      if (var11 > 0.0) {
         return var11;
      } else {
         double var13 = this.getCellSize();
         return var13 == 24.0 ? super.computePrefHeight(var1, var3, var5, var7, var9) : var13;
      }
   }

   @Override
   protected double computeMinHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = this.getFixedCellSize();
      return var11 > 0.0 ? var11 : super.computeMinHeight(var1, var3, var5, var7, var9);
   }

   @Override
   protected double computeMaxHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = this.getFixedCellSize();
      return var11 > 0.0 ? var11 : super.computeMaxHeight(var1, var3, var5, var7, var9);
   }

   private double getFixedCellSize() {
      ListView var1 = this.getSkinnable().getListView();
      return var1 != null ? var1.getFixedCellSize() : -1.0;
   }
}
