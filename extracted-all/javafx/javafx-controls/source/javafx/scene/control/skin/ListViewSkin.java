package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.ListViewBehavior;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.WeakListChangeListener;
import javafx.collections.WeakMapChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.FocusModel;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class ListViewSkin<T> extends VirtualContainerBase<ListView<T>, ListCell<T>> {
   private static final boolean IS_PANNABLE = AccessController.doPrivileged(
      (PrivilegedAction<Boolean>)(() -> Boolean.getBoolean("javafx.scene.control.skin.ListViewSkin.pannable"))
   );
   private static final String EMPTY_LIST_TEXT = ControlResources.getString("ListView.noContent");
   private final VirtualFlow<ListCell<T>> flow;
   private StackPane placeholderRegion;
   private Node placeholderNode;
   private ObservableList<T> listViewItems;
   private boolean needCellsRebuilt = true;
   private boolean needCellsReconfigured = false;
   private int itemCount = -1;
   private ListViewBehavior<T> behavior;
   private MapChangeListener<Object, Object> propertiesMapListener = var1x -> {
      if (var1x.wasAdded()) {
         if ("recreateKey".equals(var1x.getKey())) {
            this.needCellsRebuilt = true;
            this.getSkinnable().requestLayout();
            this.getSkinnable().getProperties().remove("recreateKey");
         }
      }
   };
   private WeakMapChangeListener<Object, Object> weakPropertiesMapListener = new WeakMapChangeListener(this.propertiesMapListener);
   private final ListChangeListener<T> listViewItemsListener = new ListChangeListener<T>() {
      public void onChanged(Change<? extends T> var1) {
         while (var1.next()) {
            if (var1.wasReplaced()) {
               for (int var2 = var1.getFrom(); var2 < var1.getTo(); var2++) {
                  ListViewSkin.this.flow.setCellDirty(var2);
               }
               break;
            } else if (var1.getRemovedSize() == ListViewSkin.this.itemCount) {
               ListViewSkin.this.itemCount = 0;
               break;
            }
         }

         ListViewSkin.this.getSkinnable().edit(-1);
         ListViewSkin.this.markItemCountDirty();
         ListViewSkin.this.getSkinnable().requestLayout();
      }
   };
   private final WeakListChangeListener<T> weakListViewItemsListener = new WeakListChangeListener(this.listViewItemsListener);
   private final InvalidationListener itemsChangeListener = var1x -> this.updateListViewItems();
   private WeakInvalidationListener weakItemsChangeListener = new WeakInvalidationListener(this.itemsChangeListener);
   private EventHandler<MouseEvent> ml;

   public ListViewSkin(ListView<T> var1) {
      super(var1);
      this.behavior = new ListViewBehavior<>(var1);
      this.behavior.setOnFocusPreviousRow(() -> this.onFocusPreviousCell());
      this.behavior.setOnFocusNextRow(() -> this.onFocusNextCell());
      this.behavior.setOnMoveToFirstCell(() -> this.onMoveToFirstCell());
      this.behavior.setOnMoveToLastCell(() -> this.onMoveToLastCell());
      this.behavior.setOnSelectPreviousRow(() -> this.onSelectPreviousCell());
      this.behavior.setOnSelectNextRow(() -> this.onSelectNextCell());
      this.behavior.setOnScrollPageDown(this::onScrollPageDown);
      this.behavior.setOnScrollPageUp(this::onScrollPageUp);
      this.updateListViewItems();
      this.flow = this.getVirtualFlow();
      this.flow.setId("virtual-flow");
      this.flow.setPannable(IS_PANNABLE);
      this.flow.setVertical(var1.getOrientation() == Orientation.VERTICAL);
      this.flow.setCellFactory(var1x -> this.createCell());
      this.flow.setFixedCellSize(var1.getFixedCellSize());
      this.getChildren().add(this.flow);
      this.ml = var1x -> {
         if (var1.getEditingIndex() > -1) {
            var1.edit(-1);
         }

         if (var1.isFocusTraversable()) {
            var1.requestFocus();
         }
      };
      this.flow.getVbar().addEventFilter(MouseEvent.MOUSE_PRESSED, this.ml);
      this.flow.getHbar().addEventFilter(MouseEvent.MOUSE_PRESSED, this.ml);
      this.updateItemCount();
      var1.itemsProperty().addListener(this.weakItemsChangeListener);
      ObservableMap var2 = var1.getProperties();
      var2.remove("recreateKey");
      var2.addListener(this.weakPropertiesMapListener);
      this.registerChangeListener(var1.itemsProperty(), var1x -> this.updateListViewItems());
      this.registerChangeListener(var1.orientationProperty(), var2x -> this.flow.setVertical(var1.getOrientation() == Orientation.VERTICAL));
      this.registerChangeListener(var1.cellFactoryProperty(), var1x -> this.flow.recreateCells());
      this.registerChangeListener(var1.parentProperty(), var1x -> {
         if (var1.getParent() != null && var1.isVisible()) {
            var1.requestLayout();
         }
      });
      this.registerChangeListener(var1.placeholderProperty(), var1x -> this.updatePlaceholderRegionVisibility());
      this.registerChangeListener(var1.fixedCellSizeProperty(), var2x -> this.flow.setFixedCellSize(var1.getFixedCellSize()));
   }

   @Override
   public void dispose() {
      if (this.getSkinnable() != null) {
         this.getSkinnable().getProperties().removeListener(this.weakPropertiesMapListener);
         this.getSkinnable().itemsProperty().removeListener(this.weakItemsChangeListener);
         if (this.listViewItems != null) {
            this.listViewItems.removeListener(this.weakListViewItemsListener);
            this.listViewItems = null;
         }

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
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      super.layoutChildren(var1, var3, var5, var7);
      if (this.needCellsRebuilt) {
         this.flow.rebuildCells();
      } else if (this.needCellsReconfigured) {
         this.flow.reconfigureCells();
      }

      this.needCellsRebuilt = false;
      this.needCellsReconfigured = false;
      if (this.getItemCount() == 0) {
         if (this.placeholderRegion != null) {
            this.placeholderRegion.setVisible(var5 > 0.0 && var7 > 0.0);
            this.placeholderRegion.resizeRelocate(var1, var3, var5, var7);
         }
      } else {
         this.flow.resizeRelocate(var1, var3, var5, var7);
      }
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      this.checkState();
      if (this.getItemCount() == 0) {
         if (this.placeholderRegion == null) {
            this.updatePlaceholderRegionVisibility();
         }

         if (this.placeholderRegion != null) {
            return this.placeholderRegion.prefWidth(var1) + var9 + var5;
         }
      }

      return this.computePrefHeight(-1.0, var3, var5, var7, var9) * 0.618033987;
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      return 400.0;
   }

   @Override
   protected int getItemCount() {
      return this.itemCount;
   }

   @Override
   protected void updateItemCount() {
      if (this.flow != null) {
         int var1 = this.itemCount;
         int var2 = this.listViewItems == null ? 0 : this.listViewItems.size();
         this.itemCount = var2;
         this.flow.setCellCount(var2);
         this.updatePlaceholderRegionVisibility();
         if (var2 == var1) {
            this.needCellsReconfigured = true;
         } else if (var1 == 0) {
            this.requestRebuildCells();
         }
      }
   }

   @Override
   protected Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case FOCUS_ITEM:
            FocusModel var10 = this.getSkinnable().getFocusModel();
            int var11 = var10.getFocusedIndex();
            if (var11 == -1) {
               if (this.placeholderRegion != null && this.placeholderRegion.isVisible()) {
                  return this.placeholderRegion.getChildren().get(0);
               }

               if (this.getItemCount() <= 0) {
                  return null;
               }

               var11 = 0;
            }

            return this.flow.getPrivateCell(var11);
         case ITEM_COUNT:
            return this.getItemCount();
         case ITEM_AT_INDEX:
            Integer var9 = (Integer)var2[0];
            if (var9 == null) {
               return null;
            } else {
               if (0 <= var9 && var9 < this.getItemCount()) {
                  return this.flow.getPrivateCell(var9);
               }

               return null;
            }
         case SELECTED_ITEMS:
            MultipleSelectionModel var3 = this.getSkinnable().getSelectionModel();
            ObservableList var4 = var3.getSelectedIndices();
            ArrayList var5 = new ArrayList(var4.size());

            for (int var7 : var4) {
               ListCell var8 = this.flow.getPrivateCell(var7);
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
            if (var8 instanceof ListCell) {
               ListCell var9 = (ListCell)var8;
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
                     if (var6 instanceof ListCell) {
                        ListCell var7 = (ListCell)var6;
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

   private ListCell<T> createCell() {
      ListCell var1;
      if (this.getSkinnable().getCellFactory() != null) {
         var1 = (ListCell)this.getSkinnable().getCellFactory().call(this.getSkinnable());
      } else {
         var1 = createDefaultCellImpl();
      }

      var1.updateListView(this.getSkinnable());
      return var1;
   }

   private void updateListViewItems() {
      if (this.listViewItems != null) {
         this.listViewItems.removeListener(this.weakListViewItemsListener);
      }

      this.listViewItems = this.getSkinnable().getItems();
      if (this.listViewItems != null) {
         this.listViewItems.addListener(this.weakListViewItemsListener);
      }

      this.markItemCountDirty();
      this.getSkinnable().requestLayout();
   }

   private final void updatePlaceholderRegionVisibility() {
      boolean var1 = this.getItemCount() == 0;
      if (var1) {
         this.placeholderNode = this.getSkinnable().getPlaceholder();
         if (this.placeholderNode == null && EMPTY_LIST_TEXT != null && !EMPTY_LIST_TEXT.isEmpty()) {
            this.placeholderNode = new Label();
            ((Label)this.placeholderNode).setText(EMPTY_LIST_TEXT);
         }

         if (this.placeholderNode != null) {
            if (this.placeholderRegion == null) {
               this.placeholderRegion = new StackPane();
               this.placeholderRegion.getStyleClass().setAll(new String[]{"placeholder"});
               this.getChildren().add(this.placeholderRegion);
            }

            this.placeholderRegion.getChildren().setAll(new Node[]{this.placeholderNode});
         }
      }

      this.flow.setVisible(!var1);
      if (this.placeholderRegion != null) {
         this.placeholderRegion.setVisible(var1);
      }
   }

   private static <T> ListCell<T> createDefaultCellImpl() {
      return new ListCell<T>() {
         @Override
         public void updateItem(T var1, boolean var2) {
            super.updateItem(var1, var2);
            if (var2) {
               this.setText(null);
               this.setGraphic(null);
            } else if (var1 instanceof Node) {
               this.setText(null);
               Node var3 = this.getGraphic();
               Node var4 = (Node)var1;
               if (var3 == null || !var3.equals(var4)) {
                  this.setGraphic(var4);
               }
            } else {
               this.setText(var1 == null ? "null" : var1.toString());
               this.setGraphic(null);
            }
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
      MultipleSelectionModel var1 = this.getSkinnable().getSelectionModel();
      if (var1 != null) {
         int var2 = var1.getSelectedIndex();
         this.flow.scrollTo(var2);
         IndexedCell var3 = this.flow.getFirstVisibleCell();
         if (var3 == null || var2 < var3.getIndex()) {
            this.flow.setPosition((double)var2 / (double)this.getItemCount());
         }
      }
   }

   private void onSelectNextCell() {
      MultipleSelectionModel var1 = this.getSkinnable().getSelectionModel();
      if (var1 != null) {
         int var2 = var1.getSelectedIndex();
         this.flow.scrollTo(var2);
         ListCell var3 = this.flow.getLastVisibleCell();
         if (var3 == null || var3.getIndex() < var2) {
            this.flow.setPosition((double)var2 / (double)this.getItemCount());
         }
      }
   }

   private void onMoveToFirstCell() {
      this.flow.scrollTo(0);
      this.flow.setPosition(0.0);
   }

   private void onMoveToLastCell() {
      int var1 = this.getItemCount() - 1;
      this.flow.scrollTo(var1);
      this.flow.setPosition(1.0);
   }

   private int onScrollPageDown(boolean var1) {
      ListCell var2 = this.flow.getLastVisibleCellWithinViewport();
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
                  ListCell var8 = this.flow.getLastVisibleCellWithinViewport();
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
      ListCell var2 = this.flow.getFirstVisibleCellWithinViewport();
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
                  ListCell var8 = this.flow.getFirstVisibleCellWithinViewport();
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
