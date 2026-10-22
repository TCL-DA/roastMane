package org.controlsfx.control.table.model;

import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

class TableModelTableView<S> extends TableView<TableModelRow<S>> {
   public TableModelTableView(final JavaFXTableModel<S> tableModel) {
      this.setItems(new ReadOnlyUnbackedObservableList<TableModelRow<S>>() {
         public TableModelRow<S> get(int row) {
            return row >= 0 && row < tableModel.getRowCount() ? new TableModelRow<>(tableModel, row) : null;
         }

         public int size() {
            return tableModel.getRowCount();
         }
      });
      this.setSortPolicy(table -> {
         tableModel.sort(table);
         return true;
      });

      for (int i = 0; i < tableModel.getColumnCount(); i++) {
         TableColumn<TableModelRow<S>, ?> column = new TableColumn(tableModel.getColumnName(i));
         column.setCellValueFactory(new TableModelValueFactory<>(tableModel, i));
         this.getColumns().add(column);
      }
   }
}
