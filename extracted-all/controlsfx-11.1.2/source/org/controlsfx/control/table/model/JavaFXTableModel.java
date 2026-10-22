package org.controlsfx.control.table.model;

import javafx.scene.control.TableView;

interface JavaFXTableModel<T> {
   T getValueAt(int var1, int var2);

   void setValueAt(T var1, int var2, int var3);

   int getRowCount();

   int getColumnCount();

   String getColumnName(int var1);

   void sort(TableView<TableModelRow<T>> var1);
}
