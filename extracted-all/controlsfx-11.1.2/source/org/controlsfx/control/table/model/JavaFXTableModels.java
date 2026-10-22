package org.controlsfx.control.table.model;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.SortType;
import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

class JavaFXTableModels {
   public static <S> JavaFXTableModel<S> wrap(final TableModel tableModel) {
      return new JavaFXTableModel<S>() {
         final TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);

         @Override
         public S getValueAt(int rowIndex, int columnIndex) {
            return (S)tableModel.getValueAt(this.sorter.convertRowIndexToView(rowIndex), columnIndex);
         }

         @Override
         public void setValueAt(S value, int rowIndex, int columnIndex) {
            tableModel.setValueAt(value, rowIndex, columnIndex);
         }

         @Override
         public int getRowCount() {
            return tableModel.getRowCount();
         }

         @Override
         public int getColumnCount() {
            return tableModel.getColumnCount();
         }

         @Override
         public String getColumnName(int columnIndex) {
            return tableModel.getColumnName(columnIndex);
         }

         @Override
         public void sort(TableView<TableModelRow<S>> table) {
            List<SortKey> sortKeys = new ArrayList<>();

            for (TableColumn<TableModelRow<S>, ?> column : table.getSortOrder()) {
               int columnIndex = table.getVisibleLeafIndex(column);
               SortType sortType = column.getSortType();
               SortOrder sortOrder = sortType == SortType.ASCENDING
                  ? SortOrder.ASCENDING
                  : (sortType == SortType.DESCENDING ? SortOrder.DESCENDING : SortOrder.UNSORTED);
               SortKey sortKey = new SortKey(columnIndex, sortOrder);
               sortKeys.add(sortKey);
               this.sorter.setComparator(columnIndex, column.getComparator());
            }

            this.sorter.setSortKeys(sortKeys);
            this.sorter.sort();
         }
      };
   }
}
