package impl.org.controlsfx.spreadsheet;

import org.controlsfx.control.spreadsheet.SpreadsheetView;

public abstract class SpreadsheetHandle {
   protected abstract SpreadsheetView getView();

   protected abstract SpreadsheetGridView getGridView();

   protected abstract GridViewSkin getCellsViewSkin();

   protected abstract boolean isColumnWidthSet(int var1);
}
