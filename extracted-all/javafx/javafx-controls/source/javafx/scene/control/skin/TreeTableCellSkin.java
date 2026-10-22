package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.TreeTableCellBehavior;
import java.util.Map;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;

public class TreeTableCellSkin<S, T> extends TableCellSkinBase<TreeItem<S>, T, TreeTableCell<S, T>> {
   private final BehaviorBase<TreeTableCell<S, T>> behavior;

   public TreeTableCellSkin(TreeTableCell<S, T> var1) {
      super(var1);
      this.behavior = new TreeTableCellBehavior<>(var1);
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   @Override
   public ReadOnlyObjectProperty<TreeTableColumn<S, T>> tableColumnProperty() {
      return this.getSkinnable().tableColumnProperty();
   }

   @Override
   double leftLabelPadding() {
      double var1 = super.leftLabelPadding();
      double var3 = this.getCellSize();
      TreeTableCell var5 = this.getSkinnable();
      TreeTableColumn var6 = var5.getTableColumn();
      if (var6 == null) {
         return var1;
      } else {
         TreeTableView var7 = var5.getTreeTableView();
         if (var7 == null) {
            return var1;
         } else {
            int var8 = var7.getVisibleLeafIndex(var6);
            TreeTableColumn var9 = var7.getTreeColumn();
            if ((var9 != null || var8 == 0) && (var9 == null || var6.equals(var9))) {
               TreeTableRow var10 = var5.getTableRow();
               if (var10 == null) {
                  return var1;
               } else {
                  TreeItem var11 = var10.getTreeItem();
                  if (var11 == null) {
                     return var1;
                  } else {
                     int var12 = var7.getTreeItemLevel(var11);
                     if (!var7.isShowRoot()) {
                        var12--;
                     }

                     double var13 = 10.0;
                     if (var10.getSkin() instanceof TreeTableRowSkin) {
                        var13 = ((TreeTableRowSkin)var10.getSkin()).getIndentationPerLevel();
                     }

                     var1 += (double)var12 * var13;
                     Map var15 = TableRowSkinBase.maxDisclosureWidthMap;
                     var1 += var15.containsKey(var9) ? var15.get(var9) : 0.0;
                     Node var16 = var11.getGraphic();
                     return var1 + (var16 == null ? 0.0 : var16.prefWidth(var3));
                  }
               }
            } else {
               return var1;
            }
         }
      }
   }
}
