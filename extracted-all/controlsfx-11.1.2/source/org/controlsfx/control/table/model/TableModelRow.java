package org.controlsfx.control.table.model;

class TableModelRow<S> {
   private final int columnCount;
   private final JavaFXTableModel<S> tableModel;
   private final int row;

   TableModelRow(JavaFXTableModel<S> tableModel, int row) {
      this.row = row;
      this.tableModel = tableModel;
      this.columnCount = tableModel.getColumnCount();
   }

   public Object get(int column) {
      return column >= 0 && column < this.columnCount ? this.tableModel.getValueAt(this.row, column) : null;
   }

   @Override
   public String toString() {
      String text = "Row " + this.row + ": [ ";

      for (int col = 0; col < this.columnCount; col++) {
         text = text + this.get(col);
         if (col < this.columnCount - 1) {
            text = text + ", ";
         }
      }

      return text + " ]";
   }
}
