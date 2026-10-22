package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.TableRowBehavior;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class TableRowSkin<T> extends TableRowSkinBase<T, TableRow<T>, TableCell<T, ?>> {
   private TableViewSkin<T> tableViewSkin;
   private final BehaviorBase<TableRow<T>> behavior;

   public TableRowSkin(TableRow<T> var1) {
      super(var1);
      this.behavior = new TableRowBehavior<>(var1);
      this.updateTableViewSkin();
      this.registerChangeListener(var1.tableViewProperty(), var1x -> {
         this.updateTableViewSkin();
         int var2 = 0;

         for (int var3 = this.cells.size(); var2 < var3; var2++) {
            Node var4 = (Node)this.cells.get(var2);
            if (var4 instanceof TableCell) {
               ((TableCell)var4).updateTableView(this.getSkinnable().getTableView());
            }
         }
      });
      this.setupTreeTableViewListeners();
   }

   private void setupTreeTableViewListeners() {
      TableView var1 = this.getSkinnable().getTableView();
      if (var1 == null) {
         this.getSkinnable().tableViewProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable var1) {
               TableRowSkin.this.getSkinnable().tableViewProperty().removeListener(this);
               TableRowSkin.this.setupTreeTableViewListeners();
            }
         });
      } else {
         DoubleProperty var2 = var1.fixedCellSizeProperty();
         if (var2 != null) {
            this.registerChangeListener(var2, var2x -> {
               this.fixedCellSize = var2.get();
               this.fixedCellSizeEnabled = this.fixedCellSize > 0.0;
            });
            this.fixedCellSize = var2.get();
            this.fixedCellSizeEnabled = this.fixedCellSize > 0.0;
            this.registerChangeListener(this.getVirtualFlow().widthProperty(), var1x -> var1.requestLayout());
         }
      }
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   @Override
   protected Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case SELECTED_ITEMS:
            ArrayList var9 = new ArrayList();
            int var11 = this.getSkinnable().getIndex();
            Iterator var13 = this.getTableView().getSelectionModel().getSelectedCells().iterator();
            if (var13.hasNext()) {
               TablePosition var6 = (TablePosition)var13.next();
               if (var6.getRow() == var11) {
                  TableColumn var7 = var6.getTableColumn();
                  if (var7 == null) {
                     var7 = this.getTableView().getVisibleLeafColumn(0);
                  }

                  TableCell var8 = this.cellsMap.get(var7).get();
                  if (var8 != null) {
                     var9.add(var8);
                  }
               }

               return FXCollections.observableArrayList(var9);
            }
         case CELL_AT_ROW_COLUMN:
            int var10 = (Integer)var2[1];
            TableColumn var12 = this.getTableView().getVisibleLeafColumn(var10);
            if (this.cellsMap.containsKey(var12)) {
               return this.cellsMap.get(var12).get();
            }

            return null;
         case FOCUS_ITEM:
            TableView.TableViewFocusModel var3 = this.getTableView().getFocusModel();
            TablePosition var4 = var3.getFocusedCell();
            TableColumn var5 = var4.getTableColumn();
            if (var5 == null) {
               var5 = this.getTableView().getVisibleLeafColumn(0);
            }

            if (this.cellsMap.containsKey(var5)) {
               return this.cellsMap.get(var5).get();
            }

            return null;
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   protected TableCell<T, ?> createCell(TableColumnBase var1) {
      TableColumn var2 = (TableColumn)var1;
      TableCell var3 = (TableCell)var2.getCellFactory().call(var2);
      var3.updateTableColumn(var2);
      var3.updateTableView(var2.getTableView());
      var3.updateTableRow(this.getSkinnable());
      return var3;
   }

   @Override
   protected ObservableList<TableColumn<T, ?>> getVisibleLeafColumns() {
      return this.getTableView() == null ? FXCollections.emptyObservableList() : this.getTableView().getVisibleLeafColumns();
   }

   protected void updateCell(TableCell<T, ?> var1, TableRow<T> var2) {
      var1.updateTableRow(var2);
   }

   protected TableColumn<T, ?> getTableColumn(TableCell<T, ?> var1) {
      return var1.getTableColumn();
   }

   private TableView<T> getTableView() {
      return this.getSkinnable().getTableView();
   }

   private void updateTableViewSkin() {
      TableView var1 = this.getSkinnable().getTableView();
      if (var1 != null && var1.getSkin() instanceof TableViewSkin) {
         this.tableViewSkin = (TableViewSkin<T>)var1.getSkin();
      }
   }
}
