package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.TreeTableRowBehavior;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;

public class TreeTableRowSkin<T> extends TableRowSkinBase<TreeItem<T>, TreeTableRow<T>, TreeTableCell<T, ?>> {
   private TreeItem<?> treeItem;
   private boolean disclosureNodeDirty = true;
   private Node graphic;
   private final BehaviorBase<TreeTableRow<T>> behavior;
   private TreeTableViewSkin treeTableViewSkin;
   private boolean childrenDirty = false;
   private final InvalidationListener graphicListener = var1x -> {
      this.disclosureNodeDirty = true;
      this.getSkinnable().requestLayout();
   };
   private DoubleProperty indent = null;

   public TreeTableRowSkin(TreeTableRow<T> var1) {
      super(var1);
      this.behavior = new TreeTableRowBehavior<>(var1);
      this.updateTreeItem();
      this.updateTableViewSkin();
      this.registerChangeListener(var1.treeTableViewProperty(), var1x -> this.updateTableViewSkin());
      this.registerChangeListener(var1.indexProperty(), var1x -> this.updateCells = true);
      this.registerChangeListener(var1.treeItemProperty(), var1x -> this.updateTreeItem());
      this.setupTreeTableViewListeners();
   }

   private void setupTreeTableViewListeners() {
      TreeTableView var1 = this.getSkinnable().getTreeTableView();
      if (var1 == null) {
         this.getSkinnable().treeTableViewProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable var1) {
               TreeTableRowSkin.this.getSkinnable().treeTableViewProperty().removeListener(this);
               TreeTableRowSkin.this.setupTreeTableViewListeners();
            }
         });
      } else {
         this.registerChangeListener(var1.treeColumnProperty(), var1x -> {
            this.isDirty = true;
            this.getSkinnable().requestLayout();
         });
         DoubleProperty var2 = this.getTreeTableView().fixedCellSizeProperty();
         if (var2 != null) {
            this.registerChangeListener(var2, var2x -> {
               this.fixedCellSize = var2.get();
               this.fixedCellSizeEnabled = this.fixedCellSize > 0.0;
            });
            this.fixedCellSize = var2.get();
            this.fixedCellSizeEnabled = this.fixedCellSize > 0.0;
            this.registerChangeListener(this.getVirtualFlow().widthProperty(), var1x -> var1.requestLayout());
         }
      }
   }

   public final void setIndent(double var1) {
      this.indentProperty().set(var1);
   }

   public final double getIndent() {
      return this.indent == null ? 10.0 : this.indent.get();
   }

   public final DoubleProperty indentProperty() {
      if (this.indent == null) {
         this.indent = new StyleableDoubleProperty(10.0) {
            public Object getBean() {
               return TreeTableRowSkin.this;
            }

            public String getName() {
               return "indent";
            }

            public CssMetaData<TreeTableRow<?>, Number> getCssMetaData() {
               return TreeTableRowSkin.StyleableProperties.INDENT;
            }
         };
      }

      return this.indent;
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   @Override
   protected void updateChildren() {
      super.updateChildren();
      this.updateDisclosureNodeAndGraphic();
      if (this.childrenDirty) {
         this.childrenDirty = false;
         if (this.cells.isEmpty()) {
            this.getChildren().clear();
         } else {
            this.getChildren().addAll(this.cells);
         }
      }
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      if (this.disclosureNodeDirty) {
         this.updateDisclosureNodeAndGraphic();
         this.disclosureNodeDirty = false;
      }

      Node var9 = this.getDisclosureNode();
      if (var9 != null && var9.getScene() == null) {
         this.updateDisclosureNodeAndGraphic();
      }

      super.layoutChildren(var1, var3, var5, var7);
   }

   protected TreeTableCell<T, ?> createCell(TableColumnBase var1) {
      TreeTableColumn var2 = (TreeTableColumn)var1;
      TreeTableCell var3 = (TreeTableCell)var2.getCellFactory().call(var2);
      var3.updateTableColumn(var2);
      var3.updateTreeTableView(var2.getTreeTableView());
      return var3;
   }

   @Override
   void updateCells(boolean var1) {
      super.updateCells(var1);
      if (var1) {
         this.childrenDirty = true;
         this.updateChildren();
      }
   }

   @Override
   boolean isIndentationRequired() {
      return true;
   }

   @Override
   TableColumnBase getTreeColumn() {
      return this.getTreeTableView().getTreeColumn();
   }

   int getIndentationLevel(TreeTableRow<T> var1) {
      return this.getTreeTableView().getTreeItemLevel(var1.getTreeItem());
   }

   @Override
   double getIndentationPerLevel() {
      return this.getIndent();
   }

   @Override
   Node getDisclosureNode() {
      return this.getSkinnable().getDisclosureNode();
   }

   @Override
   boolean isDisclosureNodeVisible() {
      return this.getDisclosureNode() != null && this.treeItem != null && !this.treeItem.isLeaf();
   }

   @Override
   boolean isShowRoot() {
      return this.getTreeTableView().isShowRoot();
   }

   @Override
   protected ObservableList<TreeTableColumn<T, ?>> getVisibleLeafColumns() {
      return this.getTreeTableView() == null ? FXCollections.emptyObservableList() : this.getTreeTableView().getVisibleLeafColumns();
   }

   protected void updateCell(TreeTableCell<T, ?> var1, TreeTableRow<T> var2) {
      var1.updateTableRow(var2);
   }

   protected TreeTableColumn<T, ?> getTableColumn(TreeTableCell var1) {
      return var1.getTableColumn();
   }

   @Override
   protected ObjectProperty<Node> graphicProperty() {
      TreeTableRow var1 = this.getSkinnable();
      if (var1 == null) {
         return null;
      } else {
         return this.treeItem == null ? null : this.treeItem.graphicProperty();
      }
   }

   private void updateTreeItem() {
      if (this.treeItem != null) {
         this.treeItem.graphicProperty().removeListener(this.graphicListener);
      }

      this.treeItem = this.getSkinnable().getTreeItem();
      if (this.treeItem != null) {
         this.treeItem.graphicProperty().addListener(this.graphicListener);
      }
   }

   private TreeTableView<T> getTreeTableView() {
      return this.getSkinnable().getTreeTableView();
   }

   private void updateDisclosureNodeAndGraphic() {
      if (this.getSkinnable().isEmpty()) {
         this.getChildren().remove(this.graphic);
      } else {
         ObjectProperty var1 = this.graphicProperty();
         Node var2 = var1 == null ? null : (Node)var1.get();
         if (var2 != null) {
            if (var2 != this.graphic) {
               this.getChildren().remove(this.graphic);
            }

            if (!this.getChildren().contains(var2)) {
               this.getChildren().add(var2);
               this.graphic = var2;
            }
         }

         Node var3 = this.getSkinnable().getDisclosureNode();
         if (var3 != null) {
            boolean var4 = this.treeItem != null && !this.treeItem.isLeaf();
            var3.setVisible(var4);
            if (!var4) {
               this.getChildren().remove(var3);
            } else if (var3.getParent() == null) {
               this.getChildren().add(var3);
               var3.toFront();
            } else {
               var3.toBack();
            }

            if (var3.getScene() != null) {
               var3.applyCss();
            }
         }
      }
   }

   private void updateTableViewSkin() {
      TreeTableView var1 = this.getSkinnable().getTreeTableView();
      if (var1 != null && var1.getSkin() instanceof TreeTableViewSkin) {
         this.treeTableViewSkin = (TreeTableViewSkin)var1.getSkin();
      }
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return TreeTableRowSkin.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   @Override
   protected Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      TreeTableView var3 = this.getSkinnable().getTreeTableView();
      switch (var1) {
         case SELECTED_ITEMS:
            ArrayList var10 = new ArrayList();
            int var12 = this.getSkinnable().getIndex();
            Iterator var14 = var3.getSelectionModel().getSelectedCells().iterator();
            if (var14.hasNext()) {
               TreeTablePosition var7 = (TreeTablePosition)var14.next();
               if (var7.getRow() == var12) {
                  TreeTableColumn var8 = var7.getTableColumn();
                  if (var8 == null) {
                     var8 = var3.getVisibleLeafColumn(0);
                  }

                  TreeTableCell var9 = this.cellsMap.get(var8).get();
                  if (var9 != null) {
                     var10.add(var9);
                  }
               }

               return FXCollections.observableArrayList(var10);
            }
         case CELL_AT_ROW_COLUMN:
            int var11 = (Integer)var2[1];
            TreeTableColumn var13 = var3.getVisibleLeafColumn(var11);
            if (this.cellsMap.containsKey(var13)) {
               return this.cellsMap.get(var13).get();
            }

            return null;
         case FOCUS_ITEM:
            TreeTableView.TreeTableViewFocusModel var4 = var3.getFocusModel();
            TreeTablePosition var5 = var4.getFocusedCell();
            TreeTableColumn var6 = var5.getTableColumn();
            if (var6 == null) {
               var6 = var3.getVisibleLeafColumn(0);
            }

            if (this.cellsMap.containsKey(var6)) {
               return this.cellsMap.get(var6).get();
            }

            return null;
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<TreeTableRow<?>, Number> INDENT = new CssMetaData<TreeTableRow<?>, Number>(
         "-fx-indent", SizeConverter.getInstance(), 10.0
      ) {
         public boolean isSettable(TreeTableRow<?> var1) {
            DoubleProperty var2 = ((TreeTableRowSkin)var1.getSkin()).indentProperty();
            return var2 == null || !var2.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TreeTableRow<?> var1) {
            TreeTableRowSkin var2 = (TreeTableRowSkin)var1.getSkin();
            return (StyleableProperty<Number>)var2.indentProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(CellSkinBase.getClassCssMetaData());
         var0.add(INDENT);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
