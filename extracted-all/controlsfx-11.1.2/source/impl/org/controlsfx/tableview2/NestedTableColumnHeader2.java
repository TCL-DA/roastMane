package impl.org.controlsfx.tableview2;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.tableview2.TableView2;

public class NestedTableColumnHeader2 extends NestedTableColumnHeader {
   int lastColumnResized = -1;
   private TableView2Skin<?> skin;
   private TableView2<?> tableView;

   public NestedTableColumnHeader2(TableColumnBase<?, ?> tc) {
      super(tc);
   }

   private void init() {
      this.skin = (TableView2Skin<?>)this.getTableSkin();
      this.tableView = (TableView2<?>)this.skin.getSkinnable();
      this.widthProperty().addListener(observable -> this.skin.hBarValue.clear());
      this.getColumnHeaders().addListener(o -> this.skin.getTableHeaderRow2().updateVisibleLeafColumnHeaders());
      this.tableView.getVisibleLeafColumns().addListener(o -> this.skin.getTableHeaderRow2().updateVisibleLeafColumnHeaders());
   }

   protected TableColumnHeader createTableColumnHeader(TableColumnBase col) {
      if (col != null && !col.getColumns().isEmpty() && col != this.getTableColumn()) {
         NestedTableColumnHeader2 rootHeader = new NestedTableColumnHeader2(col);
         final ObservableList<Node> rootChildren = rootHeader.getChildren();
         rootChildren.addListener(new InvalidationListener() {
            public void invalidated(Observable o) {
               if (rootChildren.size() > 0) {
                  TableColumnHeader tableColumnHeader = (TableColumnHeader)rootChildren.get(0);
                  NestedTableColumnHeader2.this.addMouseReleasedListener(tableColumnHeader);
                  rootChildren.removeListener(this);
               }
            }
         });
         this.addMousePressedListener(rootHeader);
         return rootHeader;
      } else {
         TableColumnHeader tableColumnHeader = new TableColumnHeader(col);
         this.addMousePressedListener(tableColumnHeader);
         this.addMouseReleasedListener(tableColumnHeader);
         return tableColumnHeader;
      }
   }

   protected void layoutChildren() {
      super.layoutChildren();
      this.layoutFixedColumns();
      this.updateDragRectangles();
   }

   public void layoutFixedColumns() {
      if (this.skin == null && this.tableView == null) {
         this.init();
      }

      if (this.skin != null && !this.getChildren().isEmpty()) {
         double h = this.getHeight() - this.snappedTopInset() - this.snappedBottomInset();
         double hbarValue = this.skin.getHBar().getValue();
         int labelHeight = (int)((Node)this.getChildren().get(0)).prefHeight(-1.0);
         double fixedColumnWidth = 0.0;
         double x = this.snappedLeftInset();
         int max = this.getColumnHeaders().size();
         max = max > this.tableView.getVisibleLeafColumns().size() ? this.tableView.getVisibleLeafColumns().size() : max;
         max = max > this.tableView.getColumns().size() ? this.tableView.getColumns().size() : max;

         for (int j = 0; j < max; j++) {
            TableColumnHeader n = (TableColumnHeader)this.getColumnHeaders().get(j);
            if (n.isVisible()) {
               double prefWidth = this.snapSize(n.prefWidth(-1.0));
               n.resize(prefWidth, this.snapSize(h - (double)labelHeight));
               TableColumn<?, ?> column = (TableColumn<?, ?>)n.getTableColumn();
               boolean isLeafColumn = column.getParentColumn() != null;

               while (column.getParentColumn() != null) {
                  column = (TableColumn<?, ?>)column.getParentColumn();
               }

               if (this.tableView.isColumnFixingEnabled() && this.tableView.getFixedColumns().contains(column)) {
                  double tableCellX = 0.0;
                  if (hbarValue + fixedColumnWidth > x) {
                     tableCellX = Math.abs((isLeafColumn ? 0.0 : hbarValue) + fixedColumnWidth - x);
                     n.toFront();
                     fixedColumnWidth += prefWidth;
                  }

                  n.relocate(x + tableCellX, (double)labelHeight + this.snappedTopInset());
               }

               x += prefWidth;
            }
         }
      }
   }

   private void updateDragRectangles() {
      this.getChildren().removeIf(r -> r instanceof Rectangle && (r.getLayoutX() < 1.0 || this.tableView.getParent() instanceof RowHeader));
   }

   private void addMouseReleasedListener(TableColumnHeader tableColumnHeader) {
      tableColumnHeader.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
         TableColumnHeader reorderingRegion = this.skin.getTableHeaderRow2().getReorderingRegion();
         if (tableColumnHeader.equals(reorderingRegion)) {
            tableColumnHeader.getTableColumn().setReorderable(true);
         }

         if (e.getButton() != MouseButton.PRIMARY) {
            e.consume();
         }
      });
   }

   private void addMousePressedListener(TableColumnHeader tableColumnHeader) {
      tableColumnHeader.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
         ContextMenu menu = tableColumnHeader.getTableColumn().getContextMenu();
         if (menu != null && menu.isShowing()) {
            menu.hide();
         }
      });
   }
}
