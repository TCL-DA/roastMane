package impl.org.controlsfx.skin;

import javafx.scene.Node;
import javafx.scene.control.skin.CellSkinBase;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

public class GridRowSkin<T> extends CellSkinBase<GridRow<T>> {
   public GridRowSkin(GridRow<T> control) {
      super(control);
      this.getChildren().clear();
      this.updateCells();
      this.registerChangeListener(((GridRow)this.getSkinnable()).widthProperty(), e -> this.updateCells());
      this.registerChangeListener(((GridRow)this.getSkinnable()).heightProperty(), e -> this.updateCells());
   }

   public GridCell<T> getCellAtIndex(int index) {
      return index < this.getChildren().size() ? (GridCell)this.getChildren().get(index) : null;
   }

   public void updateCells() {
      int rowIndex = ((GridRow)this.getSkinnable()).getIndex();
      if (rowIndex >= 0) {
         GridView<T> gridView = ((GridRow)this.getSkinnable()).getGridView();
         GridViewSkin<?> gridViewSkin = (GridViewSkin<?>)gridView.getSkin();
         if (gridViewSkin == null) {
            return;
         }

         int maxCellsInRow = gridViewSkin.computeMaxCellsInRow();
         int totalCellsInGrid = gridView.getItems().size();
         int startCellIndex = rowIndex * maxCellsInRow;
         int endCellIndex = startCellIndex + maxCellsInRow - 1;
         int cacheIndex = 0;

         for (int cellIndex = startCellIndex; cellIndex <= endCellIndex && cellIndex < totalCellsInGrid; cacheIndex++) {
            GridCell<T> cell = this.getCellAtIndex(cacheIndex);
            if (cell == null) {
               cell = this.createCell();
               this.getChildren().add(cell);
            }

            cell.updateIndex(-1);
            cell.updateIndex(cellIndex);
            cellIndex++;
         }

         this.getChildren().remove(cacheIndex, this.getChildren().size());
      }
   }

   private GridCell<T> createCell() {
      GridView<T> gridView = (GridView<T>)((GridRow)this.getSkinnable()).gridViewProperty().get();
      GridCell<T> cell;
      if (gridView.getCellFactory() != null) {
         cell = (GridCell<T>)gridView.getCellFactory().call(gridView);
      } else {
         cell = this.createDefaultCellImpl();
      }

      cell.updateGridView(gridView);
      return cell;
   }

   private GridCell<T> createDefaultCellImpl() {
      return new GridCell<T>() {
         protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
               this.setText("");
            } else {
               this.setText(item.toString());
            }
         }
      };
   }

   protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
   }

   protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return Double.MAX_VALUE;
   }

   protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      GridView<T> gv = (GridView<T>)((GridRow)this.getSkinnable()).gridViewProperty().get();
      return gv.getCellHeight() + gv.getVerticalCellSpacing() * 2.0;
   }

   protected void layoutChildren(double x, double y, double w, double h) {
      double cellWidth = ((GridView)((GridRow)this.getSkinnable()).gridViewProperty().get()).getCellWidth();
      double cellHeight = ((GridView)((GridRow)this.getSkinnable()).gridViewProperty().get()).getCellHeight();
      double horizontalCellSpacing = ((GridView)((GridRow)this.getSkinnable()).gridViewProperty().get()).getHorizontalCellSpacing();
      double verticalCellSpacing = ((GridView)((GridRow)this.getSkinnable()).gridViewProperty().get()).getVerticalCellSpacing();
      double xPos = 0.0;
      double yPos = 0.0;

      for (Node child : this.getChildren()) {
         child.relocate(xPos + horizontalCellSpacing, yPos + verticalCellSpacing);
         child.resize(cellWidth, cellHeight);
         xPos = xPos + horizontalCellSpacing + cellWidth + horizontalCellSpacing;
      }
   }
}
