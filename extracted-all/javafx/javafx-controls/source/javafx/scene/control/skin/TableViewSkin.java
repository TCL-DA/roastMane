package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.TableViewBehavior;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

public class TableViewSkin<T> extends TableViewSkinBase<T, T, TableView<T>, TableRow<T>, TableColumn<T, ?>> {
   private final TableViewBehavior<T> behavior;

   public TableViewSkin(TableView<T> var1) {
      super(var1);
      this.behavior = new TableViewBehavior<>(var1);
      this.flow.setFixedCellSize(var1.getFixedCellSize());
      this.flow.setCellFactory(var1x -> this.createCell());
      EventHandler var2 = var1x -> {
         if (var1.getEditingCell() != null) {
            var1.edit(-1, null);
         }

         if (var1.isFocusTraversable()) {
            var1.requestFocus();
         }
      };
      this.flow.getVbar().addEventFilter(MouseEvent.MOUSE_PRESSED, var2);
      this.flow.getHbar().addEventFilter(MouseEvent.MOUSE_PRESSED, var2);
      this.behavior.setOnFocusPreviousRow(() -> this.onFocusAboveCell());
      this.behavior.setOnFocusNextRow(() -> this.onFocusBelowCell());
      this.behavior.setOnMoveToFirstCell(() -> this.onMoveToFirstCell());
      this.behavior.setOnMoveToLastCell(() -> this.onMoveToLastCell());
      this.behavior.setOnScrollPageDown(var1x -> this.onScrollPageDown(var1x));
      this.behavior.setOnScrollPageUp(var1x -> this.onScrollPageUp(var1x));
      this.behavior.setOnSelectPreviousRow(() -> this.onSelectAboveCell());
      this.behavior.setOnSelectNextRow(() -> this.onSelectBelowCell());
      this.behavior.setOnSelectLeftCell(() -> this.onSelectLeftCell());
      this.behavior.setOnSelectRightCell(() -> this.onSelectRightCell());
      this.behavior.setOnFocusLeftCell(() -> this.onFocusLeftCell());
      this.behavior.setOnFocusRightCell(() -> this.onFocusRightCell());
      this.registerChangeListener(var1.fixedCellSizeProperty(), var1x -> this.flow.setFixedCellSize(this.getSkinnable().getFixedCellSize()));
      this.updateItemCount();
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case SELECTED_ITEMS:
            ArrayList var3 = new ArrayList();
            TableView.TableViewSelectionModel var4 = this.getSkinnable().getSelectionModel();

            for (TablePosition var6 : var4.getSelectedCells()) {
               TableRow var7 = this.flow.getPrivateCell(var6.getRow());
               if (var7 != null) {
                  var3.add(var7);
               }
            }

            return FXCollections.observableArrayList(var3);
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   @Override
   protected void executeAccessibleAction(AccessibleAction var1, Object... var2) {
      switch (var1) {
         case SHOW_ITEM:
            Node var8 = (Node)var2[0];
            if (var8 instanceof TableCell) {
               TableCell var9 = (TableCell)var8;
               this.flow.scrollTo(var9.getIndex());
            }
            break;
         case SET_SELECTED_ITEMS:
            ObservableList var3 = (ObservableList)var2[0];
            if (var3 != null) {
               TableView.TableViewSelectionModel var4 = this.getSkinnable().getSelectionModel();
               if (var4 != null) {
                  var4.clearSelection();

                  for (Node var6 : var3) {
                     if (var6 instanceof TableCell) {
                        TableCell var7 = (TableCell)var6;
                        var4.select(var7.getIndex(), var7.getTableColumn());
                     }
                  }
               }
            }
            break;
         default:
            super.executeAccessibleAction(var1, var2);
      }
   }

   private TableRow<T> createCell() {
      TableView var2 = this.getSkinnable();
      TableRow var1;
      if (var2.getRowFactory() != null) {
         var1 = (TableRow)var2.getRowFactory().call(var2);
      } else {
         var1 = new TableRow();
      }

      var1.updateTableView(var2);
      return var1;
   }

   @Override
   protected int getItemCount() {
      TableView var1 = this.getSkinnable();
      return var1.getItems() == null ? 0 : var1.getItems().size();
   }

   @Override
   void horizontalScroll() {
      super.horizontalScroll();
      if (this.getSkinnable().getFixedCellSize() > 0.0) {
         this.flow.requestCellLayout();
      }
   }
}
