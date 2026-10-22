package impl.org.controlsfx.spreadsheet;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.Skin;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class SpreadsheetGridView extends TableView<ObservableList<SpreadsheetCell>> {
   private final SpreadsheetHandle handle;

   public SpreadsheetGridView(SpreadsheetHandle handle) {
      this.handle = handle;
   }

   public String getUserAgentStylesheet() {
      return this.handle.getView().getUserAgentStylesheet();
   }

   protected Skin<?> createDefaultSkin() {
      return new GridViewSkin(this.handle);
   }

   public GridViewSkin getGridViewSkin() {
      return this.handle.getCellsViewSkin();
   }

   public boolean resizeColumn(TableColumn<ObservableList<SpreadsheetCell>, ?> tc, double delta) {
      boolean returnedValue = super.resizeColumn(tc, delta);
      if (returnedValue) {
         Event.fireEvent(this.handle.getView(), new SpreadsheetView.ColumnWidthEvent(this.getColumns().indexOf(tc), tc.getWidth()));
      }

      return returnedValue;
   }
}
