package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.TableCellBehavior;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class TableCellSkin<S, T> extends TableCellSkinBase<S, T, TableCell<S, T>> {
   private final BehaviorBase<TableCell<S, T>> behavior;

   public TableCellSkin(TableCell<S, T> var1) {
      super(var1);
      this.behavior = new TableCellBehavior<>(var1);
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   @Override
   public ReadOnlyObjectProperty<TableColumn<S, T>> tableColumnProperty() {
      return this.getSkinnable().tableColumnProperty();
   }
}
