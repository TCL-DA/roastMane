package impl.org.controlsfx.spreadsheet;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewFocusModel;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class FocusModelListener implements ChangeListener<TablePosition<ObservableList<SpreadsheetCell>, ?>> {
   private final TableViewFocusModel<ObservableList<SpreadsheetCell>> tfm;
   private final SpreadsheetGridView cellsView;
   private final SpreadsheetView spreadsheetView;

   public FocusModelListener(SpreadsheetView spreadsheetView, SpreadsheetGridView cellsView) {
      this.tfm = cellsView.getFocusModel();
      this.spreadsheetView = spreadsheetView;
      this.cellsView = cellsView;
   }

   public void changed(
      ObservableValue<? extends TablePosition<ObservableList<SpreadsheetCell>, ?>> ov,
      TablePosition<ObservableList<SpreadsheetCell>, ?> oldPosition,
      TablePosition<ObservableList<SpreadsheetCell>, ?> newPosition
   ) {
      int columnIndex = -1;
      if (newPosition != null && newPosition.getTableColumn() != null) {
         columnIndex = this.cellsView.getColumns().indexOf(newPosition.getTableColumn());
      }

      SpreadsheetView.SpanType spanType = this.spreadsheetView.getSpanType(newPosition.getRow(), columnIndex);
      switch (spanType) {
         case ROW_SPAN_INVISIBLE:
            if (!this.spreadsheetView.isPressed() && oldPosition.getColumn() == newPosition.getColumn() && oldPosition.getRow() == newPosition.getRow() - 1) {
               Platform.runLater(() -> this.tfm.focus(getNextRowNumber(oldPosition, this.cellsView, this.spreadsheetView), oldPosition.getTableColumn()));
            } else {
               Platform.runLater(() -> this.tfm.focus(newPosition.getRow() - 1, newPosition.getTableColumn()));
            }
            break;
         case BOTH_INVISIBLE:
            Platform.runLater(() -> this.tfm.focus(newPosition.getRow() - 1, (TableColumn)this.cellsView.getColumns().get(newPosition.getColumn() - 1)));
            break;
         case COLUMN_SPAN_INVISIBLE:
            if (!this.spreadsheetView.isPressed() && oldPosition.getColumn() == newPosition.getColumn() - 1 && oldPosition.getRow() == newPosition.getRow()) {
               Platform.runLater(() -> this.tfm.focus(oldPosition.getRow(), getTableColumnSpan(oldPosition, this.cellsView, this.spreadsheetView)));
            } else {
               Platform.runLater(() -> this.tfm.focus(newPosition.getRow(), this.cellsView.getVisibleLeafColumn(newPosition.getColumn() - 1)));
            }
      }
   }

   static TableColumn<ObservableList<SpreadsheetCell>, ?> getTableColumnSpan(TablePosition<?, ?> pos, SpreadsheetGridView cellsView, SpreadsheetView spv) {
      return cellsView.getVisibleLeafColumn(
         pos.getColumn()
            + spv.getColumnSpan(
               (SpreadsheetCell)((ObservableList)cellsView.getItems().get(pos.getRow())).get(cellsView.getColumns().indexOf(pos.getTableColumn()))
            )
      );
   }

   public static int getNextRowNumber(TablePosition<?, ?> pos, TableView<ObservableList<SpreadsheetCell>> cellsView, SpreadsheetView spv) {
      return spv.getRowSpan(
            (SpreadsheetCell)((ObservableList)cellsView.getItems().get(pos.getRow())).get(cellsView.getColumns().indexOf(pos.getTableColumn())), pos.getRow()
         )
         + pos.getRow();
   }
}
