package javafx.scene.control.skin;

import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TableColumnBase;
import javafx.scene.shape.Rectangle;

public abstract class TableCellSkinBase<S, T, C extends IndexedCell<T>> extends CellSkinBase<C> {
   boolean isDeferToParentForPrefWidth = false;
   private InvalidationListener columnWidthListener = var1x -> this.getSkinnable().requestLayout();
   private WeakInvalidationListener weakColumnWidthListener = new WeakInvalidationListener(this.columnWidthListener);

   public TableCellSkinBase(C var1) {
      super((C)var1);
      Rectangle var2 = new Rectangle();
      var2.widthProperty().bind(var1.widthProperty());
      var2.heightProperty().bind(var1.heightProperty());
      this.getSkinnable().setClip(var2);
      TableColumnBase var3 = this.getTableColumn();
      if (var3 != null) {
         var3.widthProperty().addListener(this.weakColumnWidthListener);
      }

      if (var1.getProperties().containsKey("deferToParentPrefWidth")) {
         this.isDeferToParentForPrefWidth = true;
      }
   }

   public abstract ReadOnlyObjectProperty<? extends TableColumnBase<S, T>> tableColumnProperty();

   public final TableColumnBase<S, T> getTableColumn() {
      return (TableColumnBase<S, T>)this.tableColumnProperty().get();
   }

   @Override
   public void dispose() {
      if (this.getSkinnable() != null) {
         TableColumnBase var1 = this.getTableColumn();
         if (var1 != null) {
            var1.widthProperty().removeListener(this.weakColumnWidthListener);
         }

         super.dispose();
      }
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      this.layoutLabelInArea(var1, var3, var5, var7 - this.getSkinnable().getPadding().getBottom());
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      if (this.isDeferToParentForPrefWidth) {
         return super.computePrefWidth(var1, var3, var5, var7, var9);
      } else {
         TableColumnBase var11 = this.getTableColumn();
         return var11 == null ? 0.0 : this.snapSizeX(var11.getWidth());
      }
   }
}
