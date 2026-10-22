package org.controlsfx.control;

import impl.org.controlsfx.skin.GridCellSkin;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Skin;

public class GridCell<T> extends IndexedCell<T> {
   private final SimpleObjectProperty<GridView<T>> gridView = new SimpleObjectProperty(this, "gridView");

   public GridCell() {
      this.getStyleClass().add("grid-cell");
      this.indexProperty().addListener(new InvalidationListener() {
         public void invalidated(Observable observable) {
            GridView<T> gridView = GridCell.this.getGridView();
            if (gridView != null) {
               if (GridCell.this.getIndex() < 0) {
                  GridCell.this.updateItem(null, true);
               } else {
                  T item = (T)gridView.getItems().get(GridCell.this.getIndex());
                  GridCell.this.updateItem(item, item == null);
               }
            }
         }
      });
   }

   protected Skin<?> createDefaultSkin() {
      return new GridCellSkin<T>(this);
   }

   public SimpleObjectProperty<GridView<T>> gridViewProperty() {
      return this.gridView;
   }

   public final void updateGridView(GridView<T> gridView) {
      this.gridView.set(gridView);
   }

   public GridView<T> getGridView() {
      return (GridView<T>)this.gridView.get();
   }
}
