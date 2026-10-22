package impl.org.controlsfx.skin;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Skin;
import org.controlsfx.control.GridView;

class GridRow<T> extends IndexedCell<T> {
   private final SimpleObjectProperty<GridView<T>> gridView = new SimpleObjectProperty(this, "gridView");

   public GridRow() {
      this.getStyleClass().add("grid-row");
   }

   protected Skin<?> createDefaultSkin() {
      return new GridRowSkin<T>(this);
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

   public void updateIndex(int i) {
      super.updateIndex(i);
      GridRowSkin<?> skin = (GridRowSkin<?>)this.getSkin();
      if (skin != null) {
         skin.updateCells();
      }

      this.updateItem(null, this.getIndex() == -1);
   }
}
