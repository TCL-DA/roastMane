package impl.org.controlsfx.spreadsheet;

import java.util.UUID;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.skin.TableCellSkin;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import org.controlsfx.control.spreadsheet.Filter;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

public class CellViewSkin extends TableCellSkin<ObservableList<SpreadsheetCell>, SpreadsheetCell> {
   public static final EventType<Event> FILTER_EVENT_TYPE = new EventType("FilterEventType" + UUID.randomUUID().toString());
   private static final String TOP_LEFT_CLASS = "top-left";
   private static final String TOP_RIGHT_CLASS = "top-right";
   private static final String BOTTOM_RIGHT_CLASS = "bottom-right";
   private static final String BOTTOM_LEFT_CLASS = "bottom-left";
   private static final int TRIANGLE_SIZE = 8;
   private Region topLeftRegion = null;
   private Region topRightRegion = null;
   private Region bottomRightRegion = null;
   private Region bottomLeftRegion = null;
   private MenuButton filterButton = null;
   private final EventHandler<Event> triangleEventHandler = new EventHandler<Event>() {
      public void handle(Event event) {
         ((TableCell)CellViewSkin.this.getSkinnable()).requestLayout();
      }
   };
   private final WeakEventHandler weakTriangleEventHandler = new WeakEventHandler(this.triangleEventHandler);
   private final ChangeListener<SpreadsheetCell> itemChangeListener = new ChangeListener<SpreadsheetCell>() {
      public void changed(ObservableValue<? extends SpreadsheetCell> arg0, SpreadsheetCell oldCell, SpreadsheetCell newCell) {
         if (oldCell != null) {
            oldCell.removeEventHandler(SpreadsheetCell.CORNER_EVENT_TYPE, CellViewSkin.this.weakTriangleEventHandler);
         }

         if (newCell != null) {
            newCell.addEventHandler(SpreadsheetCell.CORNER_EVENT_TYPE, CellViewSkin.this.weakTriangleEventHandler);
         }

         if (((TableCell)CellViewSkin.this.getSkinnable()).getItem() != null) {
            CellViewSkin.this.layoutTriangle();
         }
      }
   };
   private final WeakChangeListener<SpreadsheetCell> weakItemChangeListener = new WeakChangeListener(this.itemChangeListener);
   private final ChangeListener<TableColumn> columnChangeListener = new ChangeListener<TableColumn>() {
      public void changed(ObservableValue<? extends TableColumn> arg0, TableColumn oldCell, TableColumn newCell) {
         if (oldCell != null) {
            oldCell.removeEventHandler(CellViewSkin.FILTER_EVENT_TYPE, CellViewSkin.this.weakTriangleEventHandler);
         }

         if (newCell != null) {
            newCell.addEventHandler(CellViewSkin.FILTER_EVENT_TYPE, CellViewSkin.this.weakTriangleEventHandler);
         }
      }
   };
   private final WeakChangeListener<TableColumn> weakColumnChangeListener = new WeakChangeListener(this.columnChangeListener);

   public CellViewSkin(CellView tableCell) {
      super(tableCell);
      tableCell.itemProperty().addListener(this.weakItemChangeListener);
      tableCell.tableColumnProperty().addListener(this.weakColumnChangeListener);
      tableCell.getTableColumn().addEventHandler(FILTER_EVENT_TYPE, this.weakTriangleEventHandler);
      if (tableCell.getItem() != null) {
         ((SpreadsheetCell)tableCell.getItem()).addEventHandler(SpreadsheetCell.CORNER_EVENT_TYPE, this.weakTriangleEventHandler);
      }
   }

   protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      Node graphic = ((TableCell)this.getSkinnable()).getGraphic();
      if (graphic != null && graphic instanceof ImageView) {
         ImageView view = (ImageView)graphic;
         if (!((CellView)this.getSkinnable()).isOriginalCell()) {
            view.setManaged(false);
            double height = super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
            view.setManaged(true);
            return height;
         }

         if (view.getImage() != null) {
            return view.getImage().getHeight();
         }
      }

      return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
   }

   protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      Filter filter = ((CellView)this.getSkinnable()).getFilter();
      double cellWidth = super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
      if (filter != null) {
         double filterWidth = filter.getMenuButton().getWidth();
         filterWidth = filterWidth == 0.0 ? 23.0 : filterWidth;
         Pos alignement = ((TableCell)this.getSkinnable()).getAlignment();
         switch (alignement) {
            case BASELINE_LEFT:
            case BOTTOM_LEFT:
            case CENTER_LEFT:
            case TOP_LEFT:
               return cellWidth + filterWidth;
            case BASELINE_CENTER:
            case BOTTOM_CENTER:
            case TOP_CENTER:
            case CENTER:
               return cellWidth + 2.0 * filterWidth;
            default:
               return cellWidth + 10.0;
         }
      } else {
         return cellWidth;
      }
   }

   protected void layoutChildren(double x, double y, double w, double h) {
      super.layoutChildren(x, y, w, h);
      if (((TableCell)this.getSkinnable()).getItem() != null) {
         this.layoutTriangle();
         this.handleFilter(x, y, w, h);
      }
   }

   private void layoutTriangle() {
      SpreadsheetCell cell = (SpreadsheetCell)((TableCell)this.getSkinnable()).getItem();
      this.handleTopLeft(cell);
      this.handleTopRight(cell);
      this.handleBottomLeft(cell);
      this.handleBottomRight(cell);
      ((TableCell)this.getSkinnable()).requestLayout();
   }

   private void handleFilter(double x, double y, double w, double h) {
      Filter filter = ((CellView)this.getSkinnable()).getFilter();
      if (filter != null) {
         if (filter.getMenuButton().isShowing()) {
            return;
         }

         this.removeMenuButton();
         this.filterButton = filter.getMenuButton();
         if (!this.getChildren().contains(this.filterButton)) {
            this.getChildren().add(this.filterButton);
         }

         this.layoutInArea(this.filterButton, x + this.snappedRightInset(), y + this.snappedBottomInset(), w, h, 0.0, new Insets(0.0), HPos.RIGHT, VPos.BOTTOM);
      } else if (this.filterButton != null) {
         this.removeMenuButton();
      }
   }

   private void removeMenuButton() {
      if (this.filterButton != null && this.getChildren().contains(this.filterButton)) {
         this.getChildren().remove(this.filterButton);
         this.filterButton = null;
      }
   }

   private void handleTopLeft(SpreadsheetCell cell) {
      if (cell.isCornerActivated(SpreadsheetCell.CornerPosition.TOP_LEFT)) {
         if (this.topLeftRegion == null) {
            this.topLeftRegion = getRegion(SpreadsheetCell.CornerPosition.TOP_LEFT);
         }

         this.getChildren().remove(this.topLeftRegion);
         this.getChildren().add(this.topLeftRegion);
         this.topLeftRegion.relocate(0.0, 0.0);
      } else if (this.topLeftRegion != null) {
         this.getChildren().remove(this.topLeftRegion);
         this.topLeftRegion = null;
      }
   }

   private void handleTopRight(SpreadsheetCell cell) {
      if (cell.isCornerActivated(SpreadsheetCell.CornerPosition.TOP_RIGHT)) {
         if (this.topRightRegion == null) {
            this.topRightRegion = getRegion(SpreadsheetCell.CornerPosition.TOP_RIGHT);
         }

         this.getChildren().remove(this.topRightRegion);
         this.getChildren().add(this.topRightRegion);
         this.topRightRegion.relocate(((TableCell)this.getSkinnable()).getWidth() - 8.0, 0.0);
      } else if (this.topRightRegion != null) {
         this.getChildren().remove(this.topRightRegion);
         this.topRightRegion = null;
      }
   }

   private void handleBottomRight(SpreadsheetCell cell) {
      if (cell.isCornerActivated(SpreadsheetCell.CornerPosition.BOTTOM_RIGHT)) {
         if (this.bottomRightRegion == null) {
            this.bottomRightRegion = getRegion(SpreadsheetCell.CornerPosition.BOTTOM_RIGHT);
         }

         this.getChildren().remove(this.bottomRightRegion);
         this.getChildren().add(this.bottomRightRegion);
         this.bottomRightRegion.relocate(((TableCell)this.getSkinnable()).getWidth() - 8.0, ((TableCell)this.getSkinnable()).getHeight() - 8.0);
      } else if (this.bottomRightRegion != null) {
         this.getChildren().remove(this.bottomRightRegion);
         this.bottomRightRegion = null;
      }
   }

   private void handleBottomLeft(SpreadsheetCell cell) {
      if (cell.isCornerActivated(SpreadsheetCell.CornerPosition.BOTTOM_LEFT)) {
         if (this.bottomLeftRegion == null) {
            this.bottomLeftRegion = getRegion(SpreadsheetCell.CornerPosition.BOTTOM_LEFT);
         }

         this.getChildren().remove(this.bottomLeftRegion);
         this.getChildren().add(this.bottomLeftRegion);
         this.bottomLeftRegion.relocate(0.0, ((TableCell)this.getSkinnable()).getHeight() - 8.0);
      } else if (this.bottomLeftRegion != null) {
         this.getChildren().remove(this.bottomLeftRegion);
         this.bottomLeftRegion = null;
      }
   }

   private static Region getRegion(SpreadsheetCell.CornerPosition position) {
      Region region = new Region();
      region.resize(8.0, 8.0);
      region.getStyleClass().add("cell-corner");
      switch (position) {
         case TOP_LEFT:
            region.getStyleClass().add("top-left");
            break;
         case TOP_RIGHT:
            region.getStyleClass().add("top-right");
            break;
         case BOTTOM_RIGHT:
            region.getStyleClass().add("bottom-right");
            break;
         case BOTTOM_LEFT:
            region.getStyleClass().add("bottom-left");
      }

      return region;
   }
}
