package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.TreeViewBehavior;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.FocusModel;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class TreeViewSkin<T> extends VirtualContainerBase<TreeView<T>, TreeCell<T>> {
   private static final boolean IS_PANNABLE = AccessController.doPrivileged(
      (PrivilegedAction<Boolean>)(() -> Boolean.getBoolean("javafx.scene.control.skin.TreeViewSkin.pannable"))
   );
   private final VirtualFlow<TreeCell<T>> flow;
   private WeakReference<TreeItem<T>> weakRoot;
   private final TreeViewBehavior<T> behavior;
   private MapChangeListener<Object, Object> propertiesMapListener = var1x -> {
      if (var1x.wasAdded()) {
         if ("recreateKey".equals(var1x.getKey())) {
            this.requestRebuildCells();
            this.getSkinnable().getProperties().remove("recreateKey");
         }
      }
   };
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

      this.getSkinnable().edit(null);
   };
   private WeakEventHandler<TreeItem.TreeModificationEvent<T>> weakRootListener;
   private EventHandler<MouseEvent> ml;

   public TreeViewSkin(TreeView var1) {
      super(var1);
      this.behavior = new TreeViewBehavior<>(var1);
      this.flow = this.getVirtualFlow();
      this.flow.setPannable(IS_PANNABLE);
      this.flow.setCellFactory(this::createCell);
      this.flow.setFixedCellSize(var1.getFixedCellSize());
      this.getChildren().add(this.flow);
      this.setRoot(this.getSkinnable().getRoot());
      this.ml = var1x -> {
         if (var1.getEditingItem() != null) {
            var1.edit(null);
         }

         if (var1.isFocusTraversable()) {
            var1.requestFocus();
         }
      };
      this.flow.getVbar().addEventFilter(MouseEvent.MOUSE_PRESSED, this.ml);
      this.flow.getHbar().addEventFilter(MouseEvent.MOUSE_PRESSED, this.ml);
      ObservableMap var2 = var1.getProperties();
      var2.remove("recreateKey");
      var2.addListener(this.propertiesMapListener);
      this.behavior.setOnFocusPreviousRow(() -> this.onFocusPreviousCell());
      this.behavior.setOnFocusNextRow(() -> this.onFocusNextCell());
      this.behavior.setOnMoveToFirstCell(() -> this.onMoveToFirstCell());
      this.behavior.setOnMoveToLastCell(() -> this.onMoveToLastCell());
      this.behavior.setOnScrollPageDown(this::onScrollPageDown);
      this.behavior.setOnScrollPageUp(this::onScrollPageUp);
      this.behavior.setOnSelectPreviousRow(() -> this.onSelectPreviousCell());
      this.behavior.setOnSelectNextRow(() -> this.onSelectNextCell());
      this.registerChangeListener(var1.rootProperty(), var1x -> this.setRoot(this.getSkinnable().getRoot()));
      this.registerChangeListener(var1.showRootProperty(), var1x -> {
         if (!this.getSkinnable().isShowRoot() && this.getRoot() != null) {
            this.getRoot().setExpanded(true);
         }

         this.updateItemCount();
      });
      this.registerChangeListener(var1.cellFactoryProperty(), var1x -> this.flow.recreateCells());
      this.registerChangeListener(var1.fixedCellSizeProperty(), var1x -> this.flow.setFixedCellSize(this.getSkinnable().getFixedCellSize()));
      this.updateItemCount();
   }

   @Override
   public void dispose() {
      if (this.getSkinnable() != null) {
         this.getSkinnable().getProperties().removeListener(this.propertiesMapListener);
         this.setRoot(null);
         this.flow.setCellFactory(null);
         this.flow.getVbar().removeEventFilter(MouseEvent.MOUSE_PRESSED, this.ml);
         this.flow.getHbar().removeEventFilter(MouseEvent.MOUSE_PRESSED, this.ml);
         super.dispose();
         if (this.behavior != null) {
            this.behavior.dispose();
         }
      }
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      return this.computePrefHeight(-1.0, var3, var5, var7, var9) * 0.618033987;
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      return 400.0;
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      super.layoutChildren(var1, var3, var5, var7);
      this.flow.resizeRelocate(var1, var3, var5, var7);
   }

   @Override
   protected Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case FOCUS_ITEM:
            FocusModel var10 = this.getSkinnable().getFocusModel();
            int var11 = var10.getFocusedIndex();
            if (var11 == -1) {
               if (this.getItemCount() <= 0) {
                  return null;
               }

               var11 = 0;
            }

            return this.flow.getPrivateCell(var11);
         case ROW_AT_INDEX:
            int var9 = (Integer)var2[0];
            return var9 < 0 ? null : this.flow.getPrivateCell(var9);
         case SELECTED_ITEMS:
            MultipleSelectionModel var3 = this.getSkinnable().getSelectionModel();
            ObservableList var4 = var3.getSelectedIndices();
            ArrayList var5 = new ArrayList(var4.size());

            for (int var7 : var4) {
               TreeCell var8 = this.flow.getPrivateCell(var7);
               if (var8 != null) {
                  var5.add(var8);
               }
            }

            return FXCollections.observableArrayList(var5);
         case VERTICAL_SCROLLBAR:
            return this.flow.getVbar();
         case HORIZONTAL_SCROLLBAR:
            return this.flow.getHbar();
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   @Override
   protected void executeAccessibleAction(AccessibleAction var1, Object... var2) {
      switch (var1) {
         case SHOW_ITEM:
            Node var8 = (Node)var2[0];
            if (var8 instanceof TreeCell) {
               TreeCell var9 = (TreeCell)var8;
               this.flow.scrollTo(var9.getIndex());
            }
            break;
         case SET_SELECTED_ITEMS:
            ObservableList var3 = (ObservableList)var2[0];
            if (var3 != null) {
               MultipleSelectionModel var4 = this.getSkinnable().getSelectionModel();
               if (var4 != null) {
                  var4.clearSelection();

                  for (Node var6 : var3) {
                     if (var6 instanceof TreeCell) {
                        TreeCell var7 = (TreeCell)var6;
                        var4.select(var7.getIndex());
                     }
                  }
               }
            }
            break;
         default:
            super.executeAccessibleAction(var1, var2);
      }
   }

   private TreeCell<T> createCell(VirtualFlow<TreeCell<T>> var1) {
      TreeCell var2;
      if (this.getSkinnable().getCellFactory() != null) {
         var2 = (TreeCell)this.getSkinnable().getCellFactory().call(this.getSkinnable());
      } else {
         var2 = this.createDefaultCellImpl();
      }

      if (var2.getDisclosureNode() == null) {
         StackPane var3 = new StackPane();
         var3.getStyleClass().setAll(new String[]{"tree-disclosure-node"});
         StackPane var4 = new StackPane();
         var4.getStyleClass().setAll(new String[]{"arrow"});
         var3.getChildren().add(var4);
         var2.setDisclosureNode(var3);
      }

      var2.updateTreeView(this.getSkinnable());
      return var2;
   }

   private TreeItem<T> getRoot() {
      return this.weakRoot == null ? null : this.weakRoot.get();
   }

   private void setRoot(TreeItem<T> var1) {
      if (this.getRoot() != null && this.weakRootListener != null) {
         this.getRoot().removeEventHandler(TreeItem.treeNotificationEvent(), this.weakRootListener);
      }

      this.weakRoot = new WeakReference<>(var1);
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
   protected void updateItemCount() {
      int var1 = this.getItemCount();
      this.requestRebuildCells();
      this.flow.setCellCount(var1);
      this.getSkinnable().requestLayout();
   }

   private TreeCell<T> createDefaultCellImpl() {
      return new TreeCell<T>() {
         private HBox hbox;
         private WeakReference<TreeItem<T>> treeItemRef;
         private InvalidationListener treeItemGraphicListener = var1x -> this.updateDisplay((boolean)this.getItem(), this.isEmpty());
         private InvalidationListener treeItemListener = new InvalidationListener() {
            public void invalidated(Observable var1) {
               TreeItem var2 = treeItemRef == null ? null : treeItemRef.get();
               if (var2 != null) {
                  var2.graphicProperty().removeListener(weakTreeItemGraphicListener);
               }

               TreeItem var3 = getTreeItem();
               if (var3 != null) {
                  var3.graphicProperty().addListener(weakTreeItemGraphicListener);
                  treeItemRef = new WeakReference<>(var3);
               }
            }
         };
         private WeakInvalidationListener weakTreeItemGraphicListener = new WeakInvalidationListener(TreeViewSkin.super.treeItemGraphicListener);
         private WeakInvalidationListener weakTreeItemListener = new WeakInvalidationListener(TreeViewSkin.super.treeItemListener);

         {
            this.treeItemProperty().addListener(this.weakTreeItemListener);
            if (this.getTreeItem() != null) {
               this.getTreeItem().graphicProperty().addListener(this.weakTreeItemGraphicListener);
            }
         }

         private void updateDisplay(T var1, boolean var2) {
            if (var1 != null && !var2) {
               TreeItem var3 = this.getTreeItem();
               Node var4 = var3 == null ? null : var3.getGraphic();
               if (var4 != null) {
                  if (var1 instanceof Node) {
                     this.setText(null);
                     if (this.hbox == null) {
                        this.hbox = new HBox(3.0);
                     }

                     this.hbox.getChildren().setAll(new Node[]{var4, (Node)var1});
                     this.setGraphic(this.hbox);
                  } else {
                     this.hbox = null;
                     this.setText(var1.toString());
                     this.setGraphic(var4);
                  }
               } else {
                  this.hbox = null;
                  if (var1 instanceof Node) {
                     this.setText(null);
                     this.setGraphic((Node)var1);
                  } else {
                     this.setText(var1.toString());
                     this.setGraphic(null);
                  }
               }
            } else {
               this.hbox = null;
               this.setText(null);
               this.setGraphic(null);
            }
         }

         @Override
         public void updateItem(T var1, boolean var2) {
            super.updateItem(var1, var2);
            this.updateDisplay((boolean)var1, var2);
         }
      };
   }

   private void onFocusPreviousCell() {
      FocusModel var1 = this.getSkinnable().getFocusModel();
      if (var1 != null) {
         this.flow.scrollTo(var1.getFocusedIndex());
      }
   }

   private void onFocusNextCell() {
      FocusModel var1 = this.getSkinnable().getFocusModel();
      if (var1 != null) {
         this.flow.scrollTo(var1.getFocusedIndex());
      }
   }

   private void onSelectPreviousCell() {
      int var1 = this.getSkinnable().getSelectionModel().getSelectedIndex();
      this.flow.scrollTo(var1);
   }

   private void onSelectNextCell() {
      int var1 = this.getSkinnable().getSelectionModel().getSelectedIndex();
      this.flow.scrollTo(var1);
   }

   private void onMoveToFirstCell() {
      this.flow.scrollTo(0);
      this.flow.setPosition(0.0);
   }

   private void onMoveToLastCell() {
      this.flow.scrollTo(this.getItemCount());
      this.flow.setPosition(1.0);
   }

   private int onScrollPageDown(boolean var1) {
      TreeCell var2 = this.flow.getLastVisibleCellWithinViewport();
      if (var2 == null) {
         return -1;
      } else {
         MultipleSelectionModel var3 = this.getSkinnable().getSelectionModel();
         FocusModel var4 = this.getSkinnable().getFocusModel();
         if (var3 != null && var4 != null) {
            int var5 = var2.getIndex();
            boolean var6 = false;
            if (var1) {
               var6 = var2.isFocused() || var4.isFocused(var5);
            } else {
               var6 = var2.isSelected() || var3.isSelected(var5);
            }

            if (var6) {
               boolean var7 = var1 && var4.getFocusedIndex() == var5 || !var1 && var3.getSelectedIndex() == var5;
               if (var7) {
                  this.flow.scrollToTop(var2);
                  TreeCell var8 = this.flow.getLastVisibleCellWithinViewport();
                  var2 = var8 == null ? var2 : var8;
               }
            }

            int var10 = var2.getIndex();
            this.flow.scrollTo(var2);
            return var10;
         } else {
            return -1;
         }
      }
   }

   private int onScrollPageUp(boolean var1) {
      TreeCell var2 = this.flow.getFirstVisibleCellWithinViewport();
      if (var2 == null) {
         return -1;
      } else {
         MultipleSelectionModel var3 = this.getSkinnable().getSelectionModel();
         FocusModel var4 = this.getSkinnable().getFocusModel();
         if (var3 != null && var4 != null) {
            int var5 = var2.getIndex();
            boolean var6 = false;
            if (var1) {
               var6 = var2.isFocused() || var4.isFocused(var5);
            } else {
               var6 = var2.isSelected() || var3.isSelected(var5);
            }

            if (var6) {
               boolean var7 = var1 && var4.getFocusedIndex() == var5 || !var1 && var3.getSelectedIndex() == var5;
               if (var7) {
                  this.flow.scrollToBottom(var2);
                  TreeCell var8 = this.flow.getFirstVisibleCellWithinViewport();
                  var2 = var8 == null ? var2 : var8;
               }
            }

            int var10 = var2.getIndex();
            this.flow.scrollTo(var2);
            return var10;
         } else {
            return -1;
         }
      }
   }
}
