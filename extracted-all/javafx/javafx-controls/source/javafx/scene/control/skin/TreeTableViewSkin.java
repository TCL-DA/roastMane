package javafx.scene.control.skin;

import com.sun.javafx.scene.control.TreeTableViewBackingList;
import com.sun.javafx.scene.control.behavior.TreeTableViewBehavior;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class TreeTableViewSkin<T> extends TableViewSkinBase<T, TreeItem<T>, TreeTableView<T>, TreeTableRow<T>, TreeTableColumn<T, ?>> {
   TreeTableViewBackingList<T> tableBackingList;
   ObjectProperty<ObservableList<TreeItem<T>>> tableBackingListProperty;
   private WeakReference<TreeItem<T>> weakRootRef;
   private final TreeTableViewBehavior<T> behavior;
   private EventHandler<TreeItem.TreeModificationEvent<T>> rootListener = var1x -> {
      if (var1x.wasAdded() && var1x.wasRemoved() && var1x.getAddedSize() == var1x.getRemovedSize()) {
         this.markItemCountDirty();
         this.getSkinnable().requestLayout();
      } else if (var1x.getEventType().equals(TreeItem.valueChangedEvent())) {
         this.requestRebuildCells();
      } else {
         for (EventType var2x = var1x.getEventType(); var2x != null; var2x = var2x.getSuperType()) {
            if (var2x.equals(TreeItem.expandedItemCountChangeEvent())) {
               this.markItemCountDirty();
               this.getSkinnable().requestLayout();
               break;
            }
         }
      }

      this.getSkinnable().edit(-1, null);
   };
   private WeakEventHandler<TreeItem.TreeModificationEvent<T>> weakRootListener;

   public TreeTableViewSkin(TreeTableView<T> var1) {
      super(var1);
      this.behavior = new TreeTableViewBehavior<>(var1);
      this.flow.setFixedCellSize(var1.getFixedCellSize());
      this.flow.setCellFactory(var1x -> this.createCell());
      this.setRoot(this.getSkinnable().getRoot());
      EventHandler var2 = var1x -> {
         if (var1.getEditingCell() != null) {
            var1.edit(-1, null);
         }

         if (var1.isFocusTraversable()) {
            var1.requestFocus();
         }
      };
      this.flow.getVbar().addEventFilter(MouseEvent.MOUSE_PRESSED, var2);
      this.flow.getHbar().addEventFilter(MouseEvent.MOUSE_PRESSED, var2);
      this.behavior.setOnFocusPreviousRow(() -> this.onFocusAboveCell());
      this.behavior.setOnFocusNextRow(() -> this.onFocusBelowCell());
      this.behavior.setOnMoveToFirstCell(() -> this.onMoveToFirstCell());
      this.behavior.setOnMoveToLastCell(() -> this.onMoveToLastCell());
      this.behavior.setOnScrollPageDown(var1x -> this.onScrollPageDown(var1x));
      this.behavior.setOnScrollPageUp(var1x -> this.onScrollPageUp(var1x));
      this.behavior.setOnSelectPreviousRow(() -> this.onSelectAboveCell());
      this.behavior.setOnSelectNextRow(() -> this.onSelectBelowCell());
      this.behavior.setOnSelectLeftCell(() -> this.onSelectLeftCell());
      this.behavior.setOnSelectRightCell(() -> this.onSelectRightCell());
      this.behavior.setOnFocusLeftCell(() -> this.onFocusLeftCell());
      this.behavior.setOnFocusRightCell(() -> this.onFocusRightCell());
      this.registerChangeListener(var1.rootProperty(), var1x -> {
         this.getSkinnable().edit(-1, null);
         this.setRoot(this.getSkinnable().getRoot());
      });
      this.registerChangeListener(var1.showRootProperty(), var1x -> {
         if (!this.getSkinnable().isShowRoot() && this.getRoot() != null) {
            this.getRoot().setExpanded(true);
         }

         this.updateItemCount();
      });
      this.registerChangeListener(var1.rowFactoryProperty(), var1x -> this.flow.recreateCells());
      this.registerChangeListener(var1.expandedItemCountProperty(), var1x -> this.markItemCountDirty());
      this.registerChangeListener(var1.fixedCellSizeProperty(), var1x -> this.flow.setFixedCellSize(this.getSkinnable().getFixedCellSize()));
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   @Override
   protected Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case ROW_AT_INDEX:
            int var8 = (Integer)var2[0];
            return var8 < 0 ? null : this.flow.getPrivateCell(var8);
         case SELECTED_ITEMS:
            ArrayList var3 = new ArrayList();
            TreeTableView.TreeTableViewSelectionModel var4 = this.getSkinnable().getSelectionModel();

            for (TreeTablePosition var6 : var4.getSelectedCells()) {
               TreeTableRow var7 = this.flow.getPrivateCell(var6.getRow());
               if (var7 != null) {
                  var3.add(var7);
               }
            }

            return FXCollections.observableArrayList(var3);
         case FOCUS_ITEM:
         case CELL_AT_ROW_COLUMN:
         case COLUMN_AT_INDEX:
         case HEADER:
         case VERTICAL_SCROLLBAR:
         case HORIZONTAL_SCROLLBAR:
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   @Override
   protected void executeAccessibleAction(AccessibleAction var1, Object... var2) {
      switch (var1) {
         case SHOW_ITEM:
            Node var8 = (Node)var2[0];
            if (var8 instanceof TreeTableCell) {
               TreeTableCell var9 = (TreeTableCell)var8;
               this.flow.scrollTo(var9.getIndex());
            }
            break;
         case SET_SELECTED_ITEMS:
            ObservableList var3 = (ObservableList)var2[0];
            if (var3 != null) {
               TreeTableView.TreeTableViewSelectionModel var4 = this.getSkinnable().getSelectionModel();
               if (var4 != null) {
                  var4.clearSelection();

                  for (Node var6 : var3) {
                     if (var6 instanceof TreeTableCell) {
                        TreeTableCell var7 = (TreeTableCell)var6;
                        var4.select(var7.getIndex(), var7.getTableColumn());
                     }
                  }
               }
            }
            break;
         default:
            super.executeAccessibleAction(var1, var2);
      }
   }

   private TreeTableRow<T> createCell() {
      TreeTableView var2 = this.getSkinnable();
      TreeTableRow var1;
      if (var2.getRowFactory() != null) {
         var1 = (TreeTableRow)var2.getRowFactory().call(var2);
      } else {
         var1 = new TreeTableRow();
      }

      if (var1.getDisclosureNode() == null) {
         StackPane var3 = new StackPane();
         var3.getStyleClass().setAll(new String[]{"tree-disclosure-node"});
         var3.setMouseTransparent(true);
         StackPane var4 = new StackPane();
         var4.getStyleClass().setAll(new String[]{"arrow"});
         var3.getChildren().add(var4);
         var1.setDisclosureNode(var3);
      }

      var1.updateTreeTableView(var2);
      return var1;
   }

   private TreeItem<T> getRoot() {
      return this.weakRootRef == null ? null : this.weakRootRef.get();
   }

   private void setRoot(TreeItem<T> var1) {
      if (this.getRoot() != null && this.weakRootListener != null) {
         this.getRoot().removeEventHandler(TreeItem.treeNotificationEvent(), this.weakRootListener);
      }

      this.weakRootRef = new WeakReference<>(var1);
      if (this.getRoot() != null) {
         this.weakRootListener = new WeakEventHandler(this.rootListener);
         this.getRoot().addEventHandler(TreeItem.treeNotificationEvent(), this.weakRootListener);
      }

      this.updateItemCount();
   }

   @Override
   protected int getItemCount() {
      return this.getSkinnable().getExpandedItemCount();
   }

   @Override
   void horizontalScroll() {
      super.horizontalScroll();
      if (this.getSkinnable().getFixedCellSize() > 0.0) {
         this.flow.requestCellLayout();
      }
   }

   @Override
   protected void updateItemCount() {
      this.updatePlaceholderRegionVisibility();
      this.tableBackingList.resetSize();
      int var1 = this.flow.getCellCount();
      int var2 = this.getItemCount();
      this.flow.setCellCount(var2);
      if (var2 == var1) {
         this.needCellsReconfigured = true;
      }
   }
}
