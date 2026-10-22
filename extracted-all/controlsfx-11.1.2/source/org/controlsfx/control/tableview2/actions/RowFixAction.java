package org.controlsfx.control.tableview2.actions;

import impl.org.controlsfx.i18n.Localization;
import javafx.scene.Node;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionCheck;
import org.controlsfx.control.tableview2.TableView2;

@ActionCheck
public class RowFixAction extends Action {
   public RowFixAction(TableView2 tableView, Integer row) {
      this(tableView, row, Localization.localize(Localization.asKey("tableview2.rowheader.menu.fixed")));
   }

   public RowFixAction(TableView2 tableView, Integer row, String name) {
      this(tableView, row, name, null);
   }

   public RowFixAction(TableView2 tableView, Integer row, String name, Node image) {
      super(name);
      this.setGraphic(image);
      if (tableView != null && row != null) {
         this.disabledProperty().bind(tableView.rowFixingEnabledProperty().not());
         tableView.getFixedRows().addListener(o -> this.setSelected(tableView.getFixedRows().contains(row)));
         this.setSelected(tableView.getFixedRows().contains(row));
         this.setEventHandler(e -> {
            if (!tableView.getFixedRows().contains(row)) {
               tableView.getFixedRows().add(row);
            } else {
               tableView.getFixedRows().remove(row);
            }
         });
      }
   }

   @Override
   public String toString() {
      return this.getText();
   }
}
