package impl.org.controlsfx.tableview2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.Region;
import org.controlsfx.control.tableview2.TableView2;

final class TableView2VirtualFlow<T extends IndexedCell<?>> extends VirtualFlow<T> {
   private final TableView2<?> tableView;
   private final TableView2Skin<?> skin;
   private final ArrayList<T> myFixedCells = new ArrayList<>();
   public final List<Node> sheetChildren;
   private boolean adjusting;
   private final DoubleProperty adjustedPixels = new SimpleDoubleProperty() {
      protected void invalidated() {
         if (!TableView2VirtualFlow.this.adjusting
            && this.get() > 0.0
            && TableView2VirtualFlow.this.myFixedCells.size() < TableView2VirtualFlow.this.tableView.getFixedRows().size()) {
            TableView2VirtualFlow.this.layoutChildren();
         }

         TableView2VirtualFlow.this.adjusting = false;
      }
   };
   private final ChangeListener<Number> hBarValueChangeListener = new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
         TableView2VirtualFlow.this.skin.hBarValue.clear();
      }
   };

   public TableView2VirtualFlow(TableView2Skin<?> skin) {
      this.skin = skin;
      this.tableView = (TableView2<?>)skin.getSkinnable();
      ChangeListener<Number> listenerY = (ov, t, t1) -> this.layoutTotal();
      this.getVbar().valueProperty().addListener(listenerY);
      this.getHbar().valueProperty().addListener(this.hBarValueChangeListener);
      this.widthProperty().addListener(this.hBarValueChangeListener);
      this.sheetChildren = this.findSheetChildren();
   }

   public void init() {
      this.getHbar().maxProperty().addListener((observable, oldValue, newValue) -> {
         this.getHbar().setBlockIncrement(this.getWidth());
         this.getHbar().setUnitIncrement(newValue.doubleValue() / 20.0);
      });
      this.tableView.getFixedRows().addListener(observable -> {
         List<T> toRemove = new ArrayList<>();

         for (T cell : this.myFixedCells) {
            if (!this.tableView.getFixedRows().contains(cell.getIndex())) {
               cell.setManaged(false);
               cell.setVisible(false);
               toRemove.add(cell);
            }
         }

         this.myFixedCells.removeAll(toRemove);
      });
   }

   public void scrollTo(int index) {
      if (!this.getCells().isEmpty() && !this.tableView.getFixedRows().isEmpty()) {
         double offset = this.skin.getFixedRowHeight();

         while (offset >= 0.0 && index > 0) {
            index--;
            offset -= this.skin.getRowHeight(index);
         }
      }

      super.scrollTo(index);
      this.layoutTotal();
      this.layoutFixedRows();
   }

   public double scrollPixels(double delta) {
      double returnValue = super.scrollPixels(delta);
      this.adjusting = true;
      this.adjustedPixels.set(delta);
      this.layoutTotal();
      this.layoutFixedRows();
      return returnValue;
   }

   List<T> getFixedCells() {
      return this.myFixedCells;
   }

   void rebuildFixedCells() {
      this.myFixedCells.clear();
   }

   DoubleProperty adjustedPixelsProperty() {
      return this.adjustedPixels;
   }

   protected void layoutChildren() {
      if (this.tableView != null) {
         this.sortRows();
         super.layoutChildren();
         this.layoutTotal();
         this.layoutFixedRows();
         if (this.getVbar().getVisibleAmount() == 0.0 && this.getVbar().isVisible() && this.getCells().size() != this.getCellCount()) {
            this.getVbar().setMax(1.0);
            this.getVbar().setVisibleAmount((double)((float)this.getCells().size() / (float)this.getCellCount()));
         }
      }
   }

   protected void layoutTotal() {
      this.sortRows();
      this.removeDeportedCells();
      if (this.getCells().isEmpty()) {
         this.reconfigureCells();
      }

      for (T cell : this.getCells()) {
         if (cell != null) {
            cell.getProperties().put("fixed", false);
            if (cell.getIndex() >= 0 && (!this.skin.hBarValue.get(cell.getIndex()) || this.skin.rowToLayout.get(cell.getIndex()))) {
               if (this.skin.rowToLayout.get(cell.getIndex())) {
                  cell.getProperties().put("fixed", true);
               }

               cell.requestLayout();
            }
         }
      }
   }

   private <S> void removeDeportedCells() {
      ArrayList<TableRow2<S>> rowToRemove = new ArrayList<>();

      for (Object o : this.skin.deportedCells.entrySet()) {
         Entry<TableRow2<S>, Set<TableCell<S, ?>>> entry = (Entry<TableRow2<S>, Set<TableCell<S, ?>>>)o;
         ArrayList<TableCell<S, ?>> toRemove = new ArrayList<>();

         for (TableCell<S, ?> cell : entry.getValue()) {
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

      for (TableRow2<S> row : rowToRemove) {
         this.skin.deportedCells.remove(row);
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

   private void layoutFixedRows() {
      if (!this.tableView.getFixedRows().isEmpty() && this.tableView.isRowFixingEnabled() && this.getFirstVisibleCellWithinViewport() != null) {
         this.sortRows();
         T row = null;

         label53:
         for (int i = this.tableView.getFixedRows().size() - 1; i >= 0; i--) {
            Integer fixedRowIndex = (Integer)this.tableView.getFixedRows().get(i);
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
                        row.getProperties().put("fixed", false);
                        this.sheetChildren.remove(row);
                     }

                     virtualFlowCells.toFront();
                     continue label53;
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
               row.getProperties().put("fixed", true);
               row.toFront();
               row.requestLayout();
            } else if (row != null) {
               row.setVisible(false);
               row.setManaged(false);
               row.getProperties().put("fixed", false);
               this.sheetChildren.remove(row);
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
      List<T> temp = this.getCells();
      List<T> tset = new ArrayList<>(temp);
      Collections.sort(tset, (i1, i2) -> i2.getIndex() - i1.getIndex());

      for (T r : tset) {
         r.toFront();
      }
   }
}
