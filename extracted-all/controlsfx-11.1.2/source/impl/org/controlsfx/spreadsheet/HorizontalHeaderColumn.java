package impl.org.controlsfx.spreadsheet;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.input.MouseEvent;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class HorizontalHeaderColumn extends NestedTableColumnHeader {
   public HorizontalHeaderColumn(TableColumnBase<?, ?> tc) {
      super(tc);
      this.widthProperty().addListener(observable -> {
         ((GridViewSkin)this.getTableSkin()).hBarValue.clear();
         ((GridViewSkin)this.getTableSkin()).rectangleSelection.updateRectangle();
      });
   }

   protected TableColumnHeader createTableColumnHeader(final TableColumnBase col) {
      if (col != null && !col.getColumns().isEmpty()) {
         return new HorizontalHeaderColumn(col);
      } else {
         TableColumnHeader columnHeader = new TableColumnHeader(col);
         columnHeader.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent mouseEvent) {
               if (mouseEvent.getClickCount() == 2 && mouseEvent.isPrimaryButtonDown()) {
                  ((GridViewSkin)HorizontalHeaderColumn.this.getTableSkin()).resize(col, -1);
               }
            }
         });
         return columnHeader;
      }
   }

   protected void layoutChildren() {
      super.layoutChildren();
      this.layoutFixedColumns();
   }

   public void layoutFixedColumns() {
      SpreadsheetHandle handle = ((GridViewSkin)this.getTableSkin()).handle;
      SpreadsheetView spreadsheetView = handle.getView();
      if (handle.getCellsViewSkin() != null && !this.getChildren().isEmpty()) {
         double hbarValue = handle.getCellsViewSkin().getHBar().getValue();
         int labelHeight = (int)((Node)this.getChildren().get(0)).prefHeight(-1.0);
         double fixedColumnWidth = 0.0;
         double x = this.snappedLeftInset();
         int max = this.getColumnHeaders().size();
         max = max > handle.getGridView().getVisibleLeafColumns().size() ? handle.getGridView().getVisibleLeafColumns().size() : max;
         max = max > spreadsheetView.getColumns().size() ? spreadsheetView.getColumns().size() : max;

         for (int j = 0; j < max; j++) {
            TableColumnHeader n = (TableColumnHeader)this.getColumnHeaders().get(j);
            double prefWidth = this.snapSize(n.prefWidth(-1.0));
            n.setPrefHeight(24.0);
            if (((SpreadsheetColumn)spreadsheetView.getColumns().get(spreadsheetView.getModelColumn(j))).isFixed()) {
               double tableCellX = 0.0;
               if (hbarValue + fixedColumnWidth > x) {
                  tableCellX = Math.abs(hbarValue - x + fixedColumnWidth);
                  n.toFront();
                  fixedColumnWidth += prefWidth;
               }

               n.relocate(x + tableCellX, (double)labelHeight + this.snappedTopInset());
            }

            x += prefWidth;
         }
      }
   }
}
