package org.controlsfx.control.tableview2.actions;

import impl.org.controlsfx.i18n.Localization;
import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionCheck;
import org.controlsfx.control.tableview2.TableView2;

@ActionCheck
public class ColumnFixAction extends Action {
   private InvalidationListener fixedColumnsListener;

   public ColumnFixAction(TableColumn column) {
      this(column, Localization.localize(Localization.asKey("tableview2.column.menu.fixed")));
   }

   public ColumnFixAction(TableColumn column, String name) {
      this(column, name, null);
   }

   public ColumnFixAction(TableColumn column, String name, Node image) {
      super(name);
      this.setGraphic(image);
      if (column != null) {
         this.fixedColumnsListener = o -> this.setSelected(this.isFixedColumn(column));
         TableView tableView = column.getTableView();
         if (tableView != null && tableView instanceof TableView2) {
            this.initialize(column, (TableView2)tableView);
         }

         column.tableViewProperty().addListener((o, ov, nv) -> {
            if (ov != null && ov instanceof TableView2) {
               this.reset((TableView2)ov);
            }

            if (nv != null && nv instanceof TableView2) {
               this.initialize(column, (TableView2)nv);
            }
         });
      }
   }

   @Override
   public String toString() {
      return this.getText();
   }

   private void initialize(TableColumn column, TableView2 tableView) {
      tableView.getFixedColumns().addListener(this.fixedColumnsListener);
      this.disabledProperty().bind(tableView.columnFixingEnabledProperty().not().or(column.parentColumnProperty().isNotNull()));
      this.setSelected(this.isFixedColumn(column));
      this.setEventHandler(e -> {
         if (!tableView.getFixedColumns().contains(column)) {
            tableView.getFixedColumns().add(column);
         } else {
            tableView.getFixedColumns().remove(column);
         }
      });
   }

   private void reset(TableView2 tableView) {
      tableView.getFixedColumns().removeListener(this.fixedColumnsListener);
      this.disabledProperty().unbind();
      this.setSelected(false);
      this.setEventHandler(null);
   }

   private boolean isFixedColumn(TableColumn column) {
      while (column.getParentColumn() != null) {
         column = (TableColumn)column.getParentColumn();
      }

      TableView tableView = column.getTableView();
      return tableView != null && tableView instanceof TableView2 && ((TableView2)tableView).getFixedColumns().contains(column);
   }
}
