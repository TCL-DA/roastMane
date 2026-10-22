package impl.org.controlsfx.tableview2;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.skin.TableViewSkinBase;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.tableview2.TableColumn2;
import org.controlsfx.control.tableview2.TableView2;

public class SouthTableColumnHeader extends Region {
   private static final double ROW_HEIGHT = 20.0;
   private final TableViewSkinBase skin;
   private final TableColumn column;
   private TableColumn2 column2;
   private TableView2 tableView2;
   private final Rectangle clip;
   private final InvalidationListener tableColumnWidthListener = o -> this.requestLayout();
   private final WeakInvalidationListener weakTableColumnWidthListener = new WeakInvalidationListener(this.tableColumnWidthListener);
   private final InvalidationListener southNodeListener = o -> this.updateSouthNode();
   private final WeakInvalidationListener weakSouthNodeListener = new WeakInvalidationListener(this.southNodeListener);
   private final InvalidationListener southHeaderBlendedListener = o -> this.updateSouthHeaderColumnStyle();
   private final WeakInvalidationListener weakSouthHeaderBlendedListener = new WeakInvalidationListener(this.southHeaderBlendedListener);

   public SouthTableColumnHeader(TableViewSkinBase skin, TableColumnBase columnBase) {
      this.skin = skin;
      this.column = (TableColumn)columnBase;
      this.getStyleClass().setAll(new String[]{"column-header", "south-header"});
      this.initTableView();
      if (this.tableView2 == null) {
         this.column.tableViewProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable observable) {
               SouthTableColumnHeader.this.initTableView();
               SouthTableColumnHeader.this.column.tableViewProperty().removeListener(this);
            }
         });
      }

      if (this.column instanceof TableColumn2) {
         this.column2 = (TableColumn2)this.column;
         this.column2.southNodeProperty().addListener(this.weakSouthNodeListener);
         this.updateSouthNode();
      }

      columnBase.widthProperty().addListener(this.weakTableColumnWidthListener);
      this.setOnContextMenuRequested(e -> {
         TableColumnBase col = columnBase;
         ContextMenu cm = columnBase.getContextMenu();
         if (cm == null) {
            while (col.getParentColumn() != null && cm == null) {
               col = col.getParentColumn();
               cm = col.getContextMenu();
            }
         }

         if (cm != null && e.getSource() instanceof Node) {
            cm.show((Node)e.getSource(), e.getScreenX(), e.getScreenY());
         }
      });
      this.clip = new Rectangle(0.0, 0.0, this.column.getWidth(), this.getHeight());
      this.clip.widthProperty().bind(this.column.widthProperty());
      this.clip.heightProperty().bind(this.heightProperty());
      this.setClip(this.clip);
   }

   private void initTableView() {
      if (this.column.getTableView() instanceof TableView2) {
         this.tableView2 = (TableView2)this.column.getTableView();
         if (this.tableView2 != null) {
            this.tableView2.southHeaderBlendedProperty().addListener(this.weakSouthHeaderBlendedListener);
            this.updateSouthHeaderColumnStyle();
         }
      }
   }

   private void updateSouthNode() {
      Node southNode = this.column2.getSouthNode();
      if (southNode != null) {
         this.getChildren().add(southNode);
      } else {
         this.getChildren().clear();
      }
   }

   private void updateSouthHeaderColumnStyle() {
      if (this.tableView2.isSouthHeaderBlended() && !this.getStyleClass().contains("south")) {
         this.getStyleClass().add("south");
      } else if (!this.tableView2.isSouthHeaderBlended() && this.getStyleClass().contains("south")) {
         this.getStyleClass().remove("south");
      }
   }

   protected void layoutChildren() {
      this.resize(this.column.getWidth(), this.getHeight());
      if (!this.getChildren().isEmpty()) {
         Node n = (Node)this.getChildren().get(0);
         n.resizeRelocate(0.0, 0.0, this.column.getWidth(), this.getHeight());
      }
   }

   protected double computePrefWidth(double height) {
      return this.column.getWidth();
   }

   protected double computePrefHeight(double width) {
      if (this.column != null && !this.getChildren().isEmpty()) {
         Node n = (Node)this.getChildren().get(0);
         return !n.isVisible() && !n.isManaged() ? 0.0 : Math.max(20.0, n.prefHeight(-1.0)) + this.snappedTopInset() + this.snappedBottomInset();
      } else {
         return 0.0;
      }
   }

   void dispose() {
      if (this.column != null) {
         this.column.widthProperty().removeListener(this.weakTableColumnWidthListener);
      }

      if (this.column2 != null) {
         this.column2.southNodeProperty().removeListener(this.weakSouthNodeListener);
      }
   }

   public TableColumnBase getTableColumn() {
      return this.column;
   }
}
