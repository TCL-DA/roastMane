package org.controlsfx.control.table.model;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

class TableModelValueFactory<S, T> implements Callback<CellDataFeatures<TableModelRow<S>, T>, ObservableValue<T>> {
   private final JavaFXTableModel<S> _tableModel;
   private final int _columnIndex;

   public TableModelValueFactory(JavaFXTableModel<S> tableModel, int columnIndex) {
      this._tableModel = tableModel;
      this._columnIndex = columnIndex;
   }

   public ObservableValue<T> call(CellDataFeatures<TableModelRow<S>, T> cdf) {
      TableModelRow<S> row = (TableModelRow<S>)cdf.getValue();
      T valueAt = (T)row.get(this._columnIndex);
      return (ObservableValue<T>)(valueAt instanceof ObservableValue ? (ObservableValue)valueAt : new ReadOnlyObjectWrapper(valueAt));
   }
}
