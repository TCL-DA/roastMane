package org.controlsfx.control.spreadsheet;

import impl.org.controlsfx.i18n.Localization;
import impl.org.controlsfx.spreadsheet.CellView;
import impl.org.controlsfx.spreadsheet.CellViewSkin;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.WindowEvent;
import org.controlsfx.tools.Utils;

public final class SpreadsheetColumn {
   private final SpreadsheetView spreadsheetView;
   final TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell> column;
   private final boolean canFix;
   private final Integer indexColumn;
   private MenuItem fixItem;
   private final ObjectProperty<Filter> filterProperty = new SimpleObjectProperty();

   SpreadsheetColumn(
      final TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell> column,
      final SpreadsheetView spreadsheetView,
      final Integer indexColumn,
      final Grid grid
   ) {
      this.spreadsheetView = spreadsheetView;
      this.column = column;
      column.setMinWidth(0.0);
      this.indexColumn = indexColumn;
      this.canFix = this.initCanFix(grid);
      CellView.getValue(() -> column.setContextMenu(this.getColumnContextMenu()));
      spreadsheetView.fixingColumnsAllowedProperty().addListener(new ChangeListener<Boolean>() {
         public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            CellView.getValue(() -> column.setContextMenu(SpreadsheetColumn.this.getColumnContextMenu()));
         }
      });
      grid.getColumnHeaders().addListener(new InvalidationListener() {
         public void invalidated(Observable arg0) {
            List<String> columnsHeader = spreadsheetView.getGrid().getColumnHeaders();
            if (columnsHeader.size() <= indexColumn) {
               SpreadsheetColumn.this.setText(Utils.getExcelLetterFromNumber(indexColumn));
            } else if (!columnsHeader.get(indexColumn).equals(SpreadsheetColumn.this.getText())) {
               SpreadsheetColumn.this.setText(columnsHeader.get(indexColumn));
            }
         }
      });
      grid.getRows().addListener(new InvalidationListener() {
         public void invalidated(Observable arg0) {
            SpreadsheetColumn.this.initCanFix(grid);
         }
      });
      this.filterProperty
         .addListener(
            new ChangeListener<Filter>() {
               public void changed(ObservableValue<? extends Filter> observable, Filter oldFilter, Filter newFilter) {
                  if (newFilter != null) {
                     if (spreadsheetView.getFilteredRow() == -1) {
                        SpreadsheetColumn.this.setFilter(null);
                        return;
                     }

                     SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)spreadsheetView.getGrid().getRows().get(spreadsheetView.getFilteredRow()))
                        .get(indexColumn);
                     if (cell.getColumnSpan() > 1) {
                        SpreadsheetColumn.this.setFilter(null);
                        return;
                     }
                  }

                  Event.fireEvent(column, new Event(CellViewSkin.FILTER_EVENT_TYPE));
               }
            }
         );
   }

   public boolean isFixed() {
      return this.spreadsheetView.getFixedColumns().contains(this);
   }

   public void setFixed(boolean fixed) {
      if (fixed) {
         this.spreadsheetView.getFixedColumns().add(this);
      } else {
         this.spreadsheetView.getFixedColumns().removeAll(new SpreadsheetColumn[]{this});
      }
   }

   public void setPrefWidth(double width) {
      width = Math.ceil(width);
      if (this.column.getPrefWidth() != width || this.column.getWidth() == width) {
         this.column.setPrefWidth(width);
      }

      this.spreadsheetView.columnWidthSet(this.indexColumn);
   }

   public double getWidth() {
      return this.column.getWidth();
   }

   public final ReadOnlyDoubleProperty widthProperty() {
      return this.column.widthProperty();
   }

   public final void setMinWidth(double value) {
      this.column.setMinWidth(value);
   }

   public final double getMinWidth() {
      return this.column.getMinWidth();
   }

   public final DoubleProperty minWidthProperty() {
      return this.column.minWidthProperty();
   }

   public final DoubleProperty maxWidthProperty() {
      return this.column.maxWidthProperty();
   }

   public final void setMaxWidth(double value) {
      this.column.setMaxWidth(value);
   }

   public final double getMaxWidth() {
      return this.column.getMaxWidth();
   }

   public void setResizable(boolean b) {
      this.column.setResizable(b);
   }

   public void fitColumn() {
      if (this.column.isResizable() && this.spreadsheetView.getCellsViewSkin() != null) {
         this.spreadsheetView.getCellsViewSkin().resize(this.column, 100);
      }
   }

   public boolean isColumnFixable() {
      return this.canFix && this.spreadsheetView.isFixingColumnsAllowed();
   }

   public void setFilter(Filter filter) {
      this.filterProperty.setValue(filter);
   }

   public Filter getFilter() {
      return (Filter)this.filterProperty.get();
   }

   public ObjectProperty filterProperty() {
      return this.filterProperty;
   }

   private void setText(String text) {
      this.column.setText(text);
   }

   private String getText() {
      return this.column.getText();
   }

   private ContextMenu getColumnContextMenu() {
      if (this.isColumnFixable()) {
         ContextMenu contextMenu = new ContextMenu();
         this.fixItem = new MenuItem(Localization.localize(Localization.asKey("spreadsheet.column.menu.fix")));
         contextMenu.setOnShowing(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
               if (!SpreadsheetColumn.this.isFixed()) {
                  SpreadsheetColumn.this.fixItem.setText(Localization.localize(Localization.asKey("spreadsheet.column.menu.fix")));
               } else {
                  SpreadsheetColumn.this.fixItem.setText(Localization.localize(Localization.asKey("spreadsheet.column.menu.unfix")));
               }
            }
         });
         this.fixItem.setGraphic(new ImageView(new Image(this.getClass().getResourceAsStream("pinSpreadsheetView.png"))));
         this.fixItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
               if (!SpreadsheetColumn.this.isFixed()) {
                  SpreadsheetColumn.this.setFixed(true);
               } else {
                  SpreadsheetColumn.this.setFixed(false);
               }
            }
         });
         contextMenu.getItems().addAll(new MenuItem[]{this.fixItem});
         return contextMenu;
      } else {
         return new ContextMenu();
      }
   }

   private boolean initCanFix(Grid grid) {
      for (ObservableList<SpreadsheetCell> row : grid.getRows()) {
         int columnSpan = ((SpreadsheetCell)row.get(this.indexColumn)).getColumnSpan();
         if (columnSpan > 1) {
            return false;
         }
      }

      return true;
   }
}
