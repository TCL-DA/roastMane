package impl.org.controlsfx.spreadsheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import javafx.beans.binding.When;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableRow;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

final class GridVirtualFlow<T extends IndexedCell<?>> extends VirtualFlow<T> {
   private static final Comparator<GridRow> ROWCMP = new Comparator<GridRow>() {
      public int compare(GridRow firstRow, GridRow secondRow) {
         return secondRow.getIndex() - firstRow.getIndex();
      }
   };
   private SpreadsheetView spreadSheetView;
   private final GridViewSkin gridViewSkin;
   private final ArrayList<T> myFixedCells = new ArrayList<>();
   public final List<Node> sheetChildren;
   private StackPane corner;
   private Scale scale;
   private final ChangeListener<Number> hBarValueChangeListener = new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
         GridVirtualFlow.this.gridViewSkin.hBarValue.clear();
      }
   };

   public GridVirtualFlow(GridViewSkin gridViewSkin) {
      this.gridViewSkin = gridViewSkin;
      ChangeListener<Number> listenerY = new ChangeListener<Number>() {
         public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
            GridVirtualFlow.this.layoutTotal();
         }
      };
      this.getVbar().valueProperty().addListener(listenerY);
      this.getHbar().valueProperty().addListener(this.hBarValueChangeListener);
      this.widthProperty().addListener(this.hBarValueChangeListener);
      this.sheetChildren = this.findSheetChildren();
      this.findCorner();
      this.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
         if (event.getTarget().getClass() == GridRow.class) {
            this.spreadSheetView.getSelectionModel().clearSelection();
         }
      });
   }

   public void init(SpreadsheetView spv) {
      this.getHbar().maxProperty().addListener(new ChangeListener<Number>() {
         public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            GridVirtualFlow.this.getHbar().setBlockIncrement(GridVirtualFlow.this.getWidth());
            GridVirtualFlow.this.getHbar().setUnitIncrement(newValue.doubleValue() / 20.0);
         }
      });
      this.scale = new Scale(1.0 / spv.getZoomFactor(), 1.0 / spv.getZoomFactor());
      this.scale.setPivotX(this.getHbar().getWidth() / 2.0);
      this.getHbar().getTransforms().add(this.scale);
      this.getVbar().getTransforms().add(this.scale);
      this.corner.getTransforms().add(this.scale);
      this.spreadSheetView = spv;
      this.spreadSheetView.zoomFactorProperty().addListener((observable, oldValue, newValue) -> {
         this.scale.setX(1.0 / newValue.doubleValue());
         this.scale.setY(1.0 / newValue.doubleValue());
      });
      Rectangle rec = new Rectangle();
      rec.widthProperty().bind(this.widthProperty().subtract(new When(this.getVbar().visibleProperty()).then(this.getVbar().widthProperty()).otherwise(0)));
      rec.heightProperty().bind(this.heightProperty().subtract(new When(this.getHbar().visibleProperty()).then(this.getHbar().heightProperty()).otherwise(0)));
      this.gridViewSkin.rectangleSelection.setClip(rec);
      this.getChildren().add(this.gridViewSkin.rectangleSelection);
      spv.getFixedRows().addListener(observable -> {
         List<T> toRemove = new ArrayList<>();

         for (T cell : this.myFixedCells) {
            if (!spv.getFixedRows().contains(this.spreadSheetView.getFilteredSourceIndex(cell.getIndex()))) {
               cell.setManaged(false);
               cell.setVisible(false);
               toRemove.add(cell);
            }
         }

         this.myFixedCells.removeAll(toRemove);
      });
   }

   public void scrollTo(int index) {
      if (!this.getCells().isEmpty() && !VerticalHeader.isFixedRowEmpty(this.spreadSheetView)) {
         double offset = this.gridViewSkin.getFixedRowHeight();

         while (offset >= 0.0 && index > 0) {
            index--;
            offset -= this.gridViewSkin.getRowHeight(index);
         }
      }

      super.scrollTo(index);
      this.layoutTotal();
      this.layoutFixedRows();
   }

   public double scrollPixels(double delta) {
      double returnValue = super.scrollPixels(delta);
      this.layoutTotal();
      this.layoutFixedRows();
      return returnValue;
   }

   List<T> getFixedCells() {
      return this.myFixedCells;
   }

   GridRow getTopRow() {
      if (!this.sheetChildren.isEmpty()) {
         try {
            int i = this.sheetChildren.size() - 1;

            while (i >= this.sheetChildren.size() || ((GridRow)this.sheetChildren.get(i)).getChildrenUnmodifiable().isEmpty() && i > 0) {
               i--;
            }

            return (GridRow)this.sheetChildren.get(i);
         } catch (Exception var2) {
            return null;
         }
      } else {
         return null;
      }
   }

   protected void layoutChildren() {
      if (this.spreadSheetView != null) {
         this.sortRows();
         super.layoutChildren();
         this.layoutTotal();
         this.layoutFixedRows();
         if (this.getVbar().getVisibleAmount() == 0.0 && this.getVbar().isVisible() && this.getCells().size() != this.getCellCount()) {
            this.getVbar().setMax(1.0);
            this.getVbar().setVisibleAmount((double)((float)this.getCells().size() / (float)this.getCellCount()));
         }
      }

      Pos pos = Pos.TOP_LEFT;
      double width = this.getWidth();
      double height = this.getHeight();
      double top = this.getInsets().getTop();
      double right = this.getInsets().getRight();
      double left = this.getInsets().getLeft();
      double bottom = this.getInsets().getBottom();
      double scaleX = this.scale.getX();
      double shift = 1.0 - scaleX;
      double contentWidth = width / scaleX - left - right - this.getVbar().getWidth();
      double contentHeight = height / scaleX - top - bottom - this.getHbar().getHeight();
      this.layoutInArea(
         this.getHbar(), 0.0 - shift * 10.0, height - this.getHbar().getHeight() * scaleX, contentWidth, contentHeight, 0.0, null, pos.getHpos(), pos.getVpos()
      );
      this.layoutInArea(this.getVbar(), width - this.getVbar().getWidth() + shift, 0.0, contentWidth, contentHeight, 0.0, null, pos.getHpos(), pos.getVpos());
      if (this.corner != null) {
         this.layoutInArea(
            this.corner,
            width - this.getVbar().getWidth() + shift,
            this.getHeight() - this.getHbar().getHeight() * scaleX,
            this.corner.getWidth(),
            this.corner.getHeight(),
            0.0,
            null,
            pos.getHpos(),
            pos.getVpos()
         );
      }
   }

   protected void layoutTotal() {
      this.sortRows();
      this.removeDeportedCells();
      if (this.getCells().isEmpty()) {
         this.reconfigureCells();
      }

      for (GridRow cell : this.getCells()) {
         if (cell != null && cell.getIndex() >= 0 && (!this.gridViewSkin.hBarValue.get(cell.getIndex()) || this.gridViewSkin.rowToLayout.get(cell.getIndex()))) {
            cell.requestLayout();
         }
      }
   }

   private void removeDeportedCells() {
      ArrayList<GridRow> rowToRemove = new ArrayList<>();

      for (Entry<GridRow, Set<CellView>> entry : this.gridViewSkin.deportedCells.entrySet()) {
         ArrayList<CellView> toRemove = new ArrayList<>();

         for (CellView cell : entry.getValue()) {
            if (!cell.isEditing() && !this.getCells().contains(cell.getTableRow())) {
               entry.getKey().removeCell(cell);
               toRemove.add(cell);
            }
         }

         entry.getValue().removeAll(toRemove);
         if (entry.getValue().isEmpty()) {
            rowToRemove.add(entry.getKey());
         }
      }

      for (GridRow row : rowToRemove) {
         this.gridViewSkin.deportedCells.remove(row);
      }
   }

   protected ScrollBar getVerticalBar() {
      return this.getVbar();
   }

   protected ScrollBar getHorizontalBar() {
      return this.getHbar();
   }

   protected List<T> getCells() {
      return super.getCells();
   }

   private List<Node> findSheetChildren() {
      if (!this.getChildren().isEmpty() && this.getChildren().get(0) instanceof Region) {
         Region region = (Region)this.getChildren().get(0);
         if (!region.getChildrenUnmodifiable().isEmpty() && region.getChildrenUnmodifiable().get(0) instanceof Group) {
            return ((Group)region.getChildrenUnmodifiable().get(0)).getChildren();
         }
      }

      return new ArrayList<>();
   }

   private void findCorner() {
      if (!this.getChildren().isEmpty()) {
         for (Node node : this.getChildren()) {
            if (node instanceof StackPane) {
               this.corner = (StackPane)node;
            }
         }
      }
   }

   private void layoutFixedRows() {
      if (!VerticalHeader.isFixedRowEmpty(this.spreadSheetView) && this.getFirstVisibleCellWithinViewport() != null) {
         this.sortRows();
         T row = null;

         label56:
         for (int i = this.spreadSheetView.getFixedRows().size() - 1; i >= 0; i--) {
            Integer fixedRowIndex = (Integer)this.spreadSheetView.getFixedRows().get(i);
            if (!this.spreadSheetView.isRowHidden(i)) {
               fixedRowIndex = this.spreadSheetView.getFilteredRow(fixedRowIndex);
               T lastCell = (T)this.getLastVisibleCellWithinViewport();
               if (lastCell == null || fixedRowIndex <= lastCell.getIndex()) {
                  for (T virtualFlowCells : this.getCells()) {
                     if (virtualFlowCells.getIndex() > fixedRowIndex) {
                        break;
                     }

                     if (virtualFlowCells.getIndex() == fixedRowIndex) {
                        row = this.containsRows(fixedRowIndex);
                        if (row != null) {
                           row.setVisible(false);
                           row.setManaged(false);
                           this.sheetChildren.remove(row);
                        }

                        virtualFlowCells.toFront();
                        continue label56;
                     }
                  }

                  row = this.containsRows(fixedRowIndex);
                  if (row == null) {
                     row = (T)this.getCellFactory().call(this);
                     row.getProperties().put("newcell", null);
                     this.setCellIndex(row, fixedRowIndex);
                     this.resizeCell(row);
                     this.myFixedCells.add(row);
                  }

                  if (!this.sheetChildren.contains(row)) {
                     this.sheetChildren.add(row);
                  }

                  row.setManaged(true);
                  row.setVisible(true);
                  row.toFront();
                  row.requestLayout();
               } else if (row != null) {
                  row.setVisible(false);
                  row.setManaged(false);
                  this.sheetChildren.remove(row);
               }
            }
         }
      }
   }

   private T containsRows(int i) {
      for (T cell : this.myFixedCells) {
         if (cell.getIndex() == i) {
            return cell;
         }
      }

      return null;
   }

   private void sortRows() {
      List<GridRow> temp = this.getCells();
      List<GridRow> tset = new ArrayList<>(temp);
      Collections.sort(tset, ROWCMP);

      for (TableRow<ObservableList<SpreadsheetCell>> r : tset) {
         r.toFront();
      }
   }
}
