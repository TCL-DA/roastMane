package impl.org.controlsfx.skin;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.scene.control.skin.VirtualContainerBase;
import javafx.scene.control.skin.VirtualFlow;
import org.controlsfx.control.GridView;

public class GridViewSkin<T> extends VirtualContainerBase<GridView<T>, GridRow<T>> {
   private VirtualFlow<GridRow<T>> flow;
   private final ListChangeListener<T> gridViewItemsListener = change -> {
      this.updateItemCount();
      ((GridView)this.getSkinnable()).requestLayout();
   };
   private final WeakListChangeListener<T> weakGridViewItemsListener = new WeakListChangeListener(this.gridViewItemsListener);

   public GridViewSkin(GridView<T> control) {
      super(control);
      this.flow = this.getVirtualFlow();
      this.flow.setId("virtual-flow");
      this.flow.setPannable(false);
      this.flow.setVertical(true);
      this.flow.setFocusTraversable(((GridView)this.getSkinnable()).isFocusTraversable());
      this.flow.setCellFactory(param -> this.createCell());
      this.getChildren().add(this.flow);
      this.updateGridViewItems();
      this.updateItemCount();
      this.registerChangeListener(control.itemsProperty(), e -> this.updateGridViewItems());
      this.registerChangeListener(control.cellFactoryProperty(), e -> this.getFlow().recreateCells());
      this.registerChangeListener(control.parentProperty(), e -> {
         if (((GridView)this.getSkinnable()).getParent() != null && ((GridView)this.getSkinnable()).isVisible()) {
            ((GridView)this.getSkinnable()).requestLayout();
         }
      });
      this.registerChangeListener(control.cellHeightProperty(), e -> this.getFlow().recreateCells());
      this.registerChangeListener(control.cellWidthProperty(), e -> {
         this.updateItemCount();
         this.getFlow().recreateCells();
      });
      this.registerChangeListener(control.horizontalCellSpacingProperty(), e -> {
         this.updateItemCount();
         this.getFlow().recreateCells();
      });
      this.registerChangeListener(control.verticalCellSpacingProperty(), e -> this.getFlow().recreateCells());
      this.registerChangeListener(control.widthProperty(), e -> this.updateItemCount());
      this.registerChangeListener(control.heightProperty(), e -> this.updateItemCount());
   }

   protected VirtualFlow<GridRow<T>> createVirtualFlow() {
      return new GridViewSkin.GridVirtualFlow();
   }

   public void updateGridViewItems() {
      if (((GridView)this.getSkinnable()).getItems() != null) {
         ((GridView)this.getSkinnable()).getItems().removeListener(this.weakGridViewItemsListener);
      }

      if (((GridView)this.getSkinnable()).getItems() != null) {
         ((GridView)this.getSkinnable()).getItems().addListener(this.weakGridViewItemsListener);
      }

      this.updateItemCount();
      this.getFlow().recreateCells();
      ((GridView)this.getSkinnable()).requestLayout();
   }

   protected void layoutChildren(double x, double y, double w, double h) {
      double x1 = ((GridView)this.getSkinnable()).getInsets().getLeft();
      double y1 = ((GridView)this.getSkinnable()).getInsets().getTop();
      double w1 = ((GridView)this.getSkinnable()).getWidth()
         - (((GridView)this.getSkinnable()).getInsets().getLeft() + ((GridView)this.getSkinnable()).getInsets().getRight());
      double h1 = ((GridView)this.getSkinnable()).getHeight()
         - (((GridView)this.getSkinnable()).getInsets().getTop() + ((GridView)this.getSkinnable()).getInsets().getBottom());
      this.flow.resizeRelocate(x1, y1, w1, h1);
   }

   public int getItemCount() {
      ObservableList<?> items = ((GridView)this.getSkinnable()).getItems();
      return items == null ? 0 : (int)Math.ceil((double)items.size() / (double)this.computeMaxCellsInRow());
   }

   protected void updateItemCount() {
      if (this.flow != null) {
         int oldCount = this.flow.getCellCount();
         int newCount = this.getItemCount();
         if (newCount != oldCount) {
            this.flow.setCellCount(newCount);
            this.getFlow().rebuildCells();
         } else {
            this.getFlow().reconfigureCells();
         }

         this.updateRows(newCount);
         ((GridView)this.getSkinnable()).requestLayout();
      }
   }

   public int computeMaxCellsInRow() {
      return Math.max((int)Math.floor(this.computeRowWidth() / this.computeCellWidth()), 1);
   }

   protected double computeRowWidth() {
      return ((GridView)this.getSkinnable()).getWidth() - 18.0;
   }

   protected double computeCellWidth() {
      return ((GridView)this.getSkinnable()).cellWidthProperty().doubleValue()
         + ((GridView)this.getSkinnable()).horizontalCellSpacingProperty().doubleValue() * 2.0;
   }

   protected void updateRows(int rowCount) {
      for (int i = 0; i < rowCount; i++) {
         GridRow<T> row = (GridRow<T>)this.flow.getVisibleCell(i);
         if (row != null) {
            row.updateIndex(i);
         }
      }
   }

   protected double computeMinHeight(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return 0.0;
   }

   private GridRow<T> createCell() {
      GridRow<T> row = new GridRow<>();
      row.updateGridView((GridView<T>)this.getSkinnable());
      return row;
   }

   private GridViewSkin<T>.GridVirtualFlow getFlow() {
      return (GridViewSkin.GridVirtualFlow)this.getVirtualFlow();
   }

   private class GridVirtualFlow extends VirtualFlow<GridRow<T>> {
      public void recreateCells() {
         super.recreateCells();
      }

      public void rebuildCells() {
         super.rebuildCells();
      }

      public void reconfigureCells() {
         super.reconfigureCells();
      }
   }
}
