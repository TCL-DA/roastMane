package javafx.scene.control.skin;

import com.sun.javafx.scene.control.skin.resources.ControlResources;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.WeakListChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollToEvent;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableFocusModel;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

public abstract class TableViewSkinBase<M, S, C extends Control, I extends IndexedCell<M>, TC extends TableColumnBase<S, ?>> extends VirtualContainerBase<C, I> {
   private static final double GOLDEN_RATIO_MULTIPLIER = 0.618033987;
   private static final boolean IS_PANNABLE = AccessController.doPrivileged(
      (PrivilegedAction<Boolean>)(() -> Boolean.getBoolean("javafx.scene.control.skin.TableViewSkin.pannable"))
   );
   private final String EMPTY_TABLE_TEXT = ControlResources.getString("TableView.noContent");
   private final String NO_COLUMNS_TEXT = ControlResources.getString("TableView.noColumns");
   VirtualFlow<I> flow;
   private boolean contentWidthDirty = true;
   private Region columnReorderLine;
   private Region columnReorderOverlay;
   private TableHeaderRow tableHeaderRow;
   private Callback<C, I> rowFactory;
   private StackPane placeholderRegion;
   private Label placeholderLabel;
   private int visibleColCount;
   boolean needCellsRecreated = true;
   boolean needCellsReconfigured = false;
   private int itemCount = -1;
   private MapChangeListener<Object, Object> propertiesMapListener = var1x -> {
      if (var1x.wasAdded()) {
         if ("refreshKey".equals(var1x.getKey())) {
            this.refreshView();
            this.getSkinnable().getProperties().remove("refreshKey");
         } else if ("recreateKey".equals(var1x.getKey())) {
            this.needCellsRecreated = true;
            this.refreshView();
            this.getSkinnable().getProperties().remove("recreateKey");
         }
      }
   };
   private ListChangeListener<S> rowCountListener = var1x -> {
      while (var1x.next()) {
         if (var1x.wasReplaced()) {
            this.itemCount = 0;
            break;
         } else if (var1x.getRemovedSize() == this.itemCount) {
            this.itemCount = 0;
            break;
         }
      }

      if (this.getSkinnable() instanceof TableView) {
         ((TableView)this.getSkinnable()).edit(-1, null);
      }

      this.markItemCountDirty();
      this.getSkinnable().requestLayout();
   };
   private ListChangeListener<TC> visibleLeafColumnsListener = var1x -> {
      this.updateVisibleColumnCount();

      while (var1x.next()) {
         this.updateVisibleLeafColumnWidthListeners(var1x.getAddedSubList(), var1x.getRemoved());
      }
   };
   private InvalidationListener widthListener = var1x -> {
      this.needCellsReconfigured = true;
      if (this.getSkinnable() != null) {
         this.getSkinnable().requestLayout();
      }
   };
   private InvalidationListener itemsChangeListener;
   private WeakListChangeListener<S> weakRowCountListener = new WeakListChangeListener(this.rowCountListener);
   private WeakListChangeListener<TC> weakVisibleLeafColumnsListener = new WeakListChangeListener(this.visibleLeafColumnsListener);
   private WeakInvalidationListener weakWidthListener = new WeakInvalidationListener(this.widthListener);
   private WeakInvalidationListener weakItemsChangeListener;

   public TableViewSkinBase(C var1) {
      super((C)var1);
      this.flow = this.getVirtualFlow();
      this.flow.setPannable(IS_PANNABLE);
      this.flow.getHbar().valueProperty().addListener(var1x -> this.horizontalScroll());
      this.flow.getHbar().setUnitIncrement(15.0);
      this.flow.getHbar().setBlockIncrement(80.0);
      this.columnReorderLine = new Region();
      this.columnReorderLine.getStyleClass().setAll(new String[]{"column-resize-line"});
      this.columnReorderLine.setManaged(false);
      this.columnReorderLine.setVisible(false);
      this.columnReorderOverlay = new Region();
      this.columnReorderOverlay.getStyleClass().setAll(new String[]{"column-overlay"});
      this.columnReorderOverlay.setVisible(false);
      this.columnReorderOverlay.setManaged(false);
      this.tableHeaderRow = this.createTableHeaderRow();
      this.tableHeaderRow.setFocusTraversable(false);
      this.getChildren().addAll(new Node[]{this.tableHeaderRow, this.flow, this.columnReorderOverlay, this.columnReorderLine});
      this.updateVisibleColumnCount();
      this.updateVisibleLeafColumnWidthListeners(this.getVisibleLeafColumns(), FXCollections.emptyObservableList());
      this.tableHeaderRow.reorderingProperty().addListener(var1x -> this.getSkinnable().requestLayout());
      this.getVisibleLeafColumns().addListener(this.weakVisibleLeafColumnsListener);
      final ObjectProperty var2 = TableSkinUtils.itemsProperty(this);
      this.updateTableItems(null, (ObservableList<S>)var2.get());
      this.itemsChangeListener = new InvalidationListener() {
         private WeakReference<ObservableList<S>> weakItemsRef = new WeakReference<>((ObservableList<S>)var2.get());

         public void invalidated(Observable var1) {
            ObservableList var2x = this.weakItemsRef.get();
            this.weakItemsRef = new WeakReference<>((ObservableList<S>)var2.get());
            TableViewSkinBase.this.updateTableItems(var2x, (ObservableList<S>)var2.get());
         }
      };
      this.weakItemsChangeListener = new WeakInvalidationListener(this.itemsChangeListener);
      var2.addListener(this.weakItemsChangeListener);
      ObservableMap var3 = var1.getProperties();
      var3.remove("refreshKey");
      var3.remove("recreateKey");
      var3.addListener(this.propertiesMapListener);
      var1.addEventHandler(ScrollToEvent.scrollToColumn(), var1x -> this.scrollHorizontally((TC)var1x.getScrollTarget()));
      InvalidationListener var4 = var1x -> {
         this.contentWidthDirty = true;
         this.getSkinnable().requestLayout();
      };
      this.flow.widthProperty().addListener(var4);
      this.flow.getVbar().widthProperty().addListener(var4);
      ObjectProperty var5 = TableSkinUtils.rowFactoryProperty(this);
      this.registerChangeListener(var5, var2x -> {
         Callback var3x = this.rowFactory;
         this.rowFactory = (Callback<C, I>)var5.get();
         if (var3x != this.rowFactory) {
            this.requestRebuildCells();
         }
      });
      this.registerChangeListener(TableSkinUtils.placeholderProperty(this), var1x -> this.updatePlaceholderRegionVisibility());
      this.registerChangeListener(this.flow.getVbar().visibleProperty(), var1x -> this.updateContentWidth());
   }

   @Override
   public void dispose() {
      if (this.getSkinnable() != null) {
         ObjectProperty var1 = TableSkinUtils.itemsProperty(this);
         this.getVisibleLeafColumns().removeListener(this.weakVisibleLeafColumnsListener);
         var1.removeListener(this.weakItemsChangeListener);
         this.getSkinnable().getProperties().removeListener(this.propertiesMapListener);
         this.updateTableItems((ObservableList<S>)var1.get(), null);
         super.dispose();
      }
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      return 400.0;
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      double var11 = this.computePrefHeight(-1.0, var3, var5, var7, var9);
      ObservableList var13 = this.getVisibleLeafColumns();
      if (var13 != null && !var13.isEmpty()) {
         double var14 = var9 + var5;
         int var16 = 0;

         for (int var17 = var13.size(); var16 < var17; var16++) {
            TableColumnBase var18 = (TableColumnBase)var13.get(var16);
            var14 += Math.max(var18.getPrefWidth(), var18.getMinWidth());
         }

         return Math.max(var14, var11 * 0.618033987);
      } else {
         return var11 * 0.618033987;
      }
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      Control var9 = this.getSkinnable();
      if (var9 != null) {
         super.layoutChildren(var1, var3, var5, var7);
         if (this.needCellsRecreated) {
            this.flow.recreateCells();
         } else if (this.needCellsReconfigured) {
            this.flow.reconfigureCells();
         }

         this.needCellsRecreated = false;
         this.needCellsReconfigured = false;
         double var10 = var9.getLayoutBounds().getHeight() / 2.0;
         double var12 = this.tableHeaderRow.prefHeight(-1.0);
         this.layoutInArea(this.tableHeaderRow, var1, var3, var5, var12, var10, HPos.CENTER, VPos.CENTER);
         var3 += var12;
         double var14 = Math.floor(var7 - var12);
         if (this.getItemCount() != 0 && this.visibleColCount != 0) {
            this.layoutInArea(this.flow, var1, var3, var5, var14, var10, HPos.CENTER, VPos.CENTER);
         } else {
            this.layoutInArea(this.placeholderRegion, var1, var3, var5, var14, var10, HPos.CENTER, VPos.CENTER);
         }

         if (this.tableHeaderRow.getReorderingRegion() != null) {
            TableColumnHeader var16 = this.tableHeaderRow.getReorderingRegion();
            TableColumnBase var17 = var16.getTableColumn();
            if (var17 != null) {
               TableColumnHeader var18 = this.tableHeaderRow.getReorderingRegion();
               double var19 = this.tableHeaderRow.sceneToLocal(var18.localToScene(var18.getBoundsInLocal())).getMinX();
               double var21 = var16.getWidth();
               if (var19 < 0.0) {
                  var21 += var19;
               }

               var19 = var19 < 0.0 ? 0.0 : var19;
               if (var19 + var21 > var5) {
                  var21 = var5 - var19;
                  if (this.flow.getVbar().isVisible()) {
                     var21 -= this.flow.getVbar().getWidth() - 1.0;
                  }
               }

               double var23 = var14;
               if (this.flow.getHbar().isVisible()) {
                  var23 = var14 - this.flow.getHbar().getHeight();
               }

               this.columnReorderOverlay.resize(var21, var23);
               this.columnReorderOverlay.setLayoutX(var19);
               this.columnReorderOverlay.setLayoutY(this.tableHeaderRow.getHeight());
            }

            double var26 = this.columnReorderLine.snappedLeftInset() + this.columnReorderLine.snappedRightInset();
            double var20 = var7 - (this.flow.getHbar().isVisible() ? this.flow.getHbar().getHeight() - 1.0 : 0.0);
            this.columnReorderLine.resizeRelocate(0.0, this.columnReorderLine.snappedTopInset(), var26, var20);
         }

         this.columnReorderLine.setVisible(this.tableHeaderRow.isReordering());
         this.columnReorderOverlay.setVisible(this.tableHeaderRow.isReordering());
         this.checkContentWidthState();
      }
   }

   protected TableHeaderRow createTableHeaderRow() {
      return new TableHeaderRow(this);
   }

   protected TableHeaderRow getTableHeaderRow() {
      return this.tableHeaderRow;
   }

   private TableSelectionModel<S> getSelectionModel() {
      return TableSkinUtils.getSelectionModel(this);
   }

   private TableFocusModel<M, ?> getFocusModel() {
      return TableSkinUtils.getFocusModel(this);
   }

   private TablePositionBase<? extends TC> getFocusedCell() {
      return TableSkinUtils.getFocusedCell(this);
   }

   private ObservableList<? extends TC> getVisibleLeafColumns() {
      return TableSkinUtils.getVisibleLeafColumns(this);
   }

   @Override
   protected void updateItemCount() {
      this.updatePlaceholderRegionVisibility();
      int var1 = this.itemCount;
      int var2 = this.getItemCount();
      this.itemCount = var2;
      if (this.itemCount == 0) {
         this.flow.getHbar().setValue(0.0);
      }

      this.flow.setCellCount(var2);
      if (var2 == var1) {
         this.needCellsReconfigured = true;
      } else if (var1 == 0) {
         this.requestRebuildCells();
      }
   }

   private void checkContentWidthState() {
      if (this.contentWidthDirty || this.getItemCount() == 0) {
         this.updateContentWidth();
         this.contentWidthDirty = false;
      }
   }

   void horizontalScroll() {
      this.tableHeaderRow.updateScrollX();
   }

   protected void onFocusAboveCell() {
      TableFocusModel var1 = this.getFocusModel();
      if (var1 != null) {
         this.flow.scrollTo(var1.getFocusedIndex());
      }
   }

   protected void onFocusBelowCell() {
      TableFocusModel var1 = this.getFocusModel();
      if (var1 != null) {
         this.flow.scrollTo(var1.getFocusedIndex());
      }
   }

   protected void onSelectAboveCell() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         this.flow.scrollTo(var1.getSelectedIndex());
      }
   }

   protected void onSelectBelowCell() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         this.flow.scrollTo(var1.getSelectedIndex());
      }
   }

   protected void onSelectLeftCell() {
      this.scrollHorizontally();
   }

   protected void onSelectRightCell() {
      this.scrollHorizontally();
   }

   protected void onFocusLeftCell() {
      this.scrollHorizontally();
   }

   protected void onFocusRightCell() {
      this.scrollHorizontally();
   }

   protected void onMoveToFirstCell() {
      this.flow.scrollTo(0);
      this.flow.setPosition(0.0);
   }

   protected void onMoveToLastCell() {
      int var1 = this.getItemCount();
      this.flow.scrollTo(var1);
      this.flow.setPosition(1.0);
   }

   private void updateTableItems(ObservableList<S> var1, ObservableList<S> var2) {
      if (var1 != null) {
         var1.removeListener(this.weakRowCountListener);
      }

      if (var2 != null) {
         var2.addListener(this.weakRowCountListener);
      }

      this.markItemCountDirty();
      this.getSkinnable().requestLayout();
   }

   Region getColumnReorderLine() {
      return this.columnReorderLine;
   }

   protected int onScrollPageDown(boolean var1) {
      TableSelectionModel var2 = this.getSelectionModel();
      if (var2 == null) {
         return -1;
      } else {
         int var3 = this.getItemCount();
         IndexedCell var4 = this.flow.getLastVisibleCellWithinViewport();
         if (var4 == null) {
            return -1;
         } else {
            int var5 = var4.getIndex();
            var5 = var5 >= var3 ? var3 - 1 : var5;
            boolean var6;
            if (var1) {
               var6 = var4.isFocused() || this.isCellFocused(var5);
            } else {
               var6 = var4.isSelected() || this.isCellSelected(var5);
            }

            if (var6) {
               boolean var7 = this.isLeadIndex(var1, var5);
               if (var7) {
                  this.flow.scrollToTop((I)var4);
                  IndexedCell var8 = this.flow.getLastVisibleCellWithinViewport();
                  var4 = var8 == null ? var4 : var8;
               }
            }

            int var10 = var4.getIndex();
            var10 = var10 >= var3 ? var3 - 1 : var10;
            this.flow.scrollTo(var10);
            return var10;
         }
      }
   }

   protected int onScrollPageUp(boolean var1) {
      IndexedCell var2 = this.flow.getFirstVisibleCellWithinViewport();
      if (var2 == null) {
         return -1;
      } else {
         int var3 = var2.getIndex();
         boolean var4 = false;
         if (var1) {
            var4 = var2.isFocused() || this.isCellFocused(var3);
         } else {
            var4 = var2.isSelected() || this.isCellSelected(var3);
         }

         if (var4) {
            boolean var5 = this.isLeadIndex(var1, var3);
            if (var5) {
               this.flow.scrollToBottom((I)var2);
               IndexedCell var6 = this.flow.getFirstVisibleCellWithinViewport();
               var2 = var6 == null ? var2 : var6;
            }
         }

         int var8 = var2.getIndex();
         this.flow.scrollTo(var8);
         return var8;
      }
   }

   private boolean isLeadIndex(boolean var1, int var2) {
      TableSelectionModel var3 = this.getSelectionModel();
      TableFocusModel var4 = this.getFocusModel();
      return var1 && var4.getFocusedIndex() == var2 || !var1 && var3.getSelectedIndex() == var2;
   }

   private void updateVisibleColumnCount() {
      this.visibleColCount = this.getVisibleLeafColumns().size();
      this.updatePlaceholderRegionVisibility();
      this.requestRebuildCells();
   }

   private void updateVisibleLeafColumnWidthListeners(List<? extends TC> var1, List<? extends TC> var2) {
      int var3 = 0;

      for (int var4 = var2.size(); var3 < var4; var3++) {
         TableColumnBase var5 = (TableColumnBase)var2.get(var3);
         var5.widthProperty().removeListener(this.weakWidthListener);
      }

      var3 = 0;

      for (int var7 = var1.size(); var3 < var7; var3++) {
         TableColumnBase var8 = (TableColumnBase)var1.get(var3);
         var8.widthProperty().addListener(this.weakWidthListener);
      }

      this.requestRebuildCells();
   }

   final void updatePlaceholderRegionVisibility() {
      boolean var1 = this.visibleColCount == 0 || this.getItemCount() == 0;
      if (var1) {
         if (this.placeholderRegion == null) {
            this.placeholderRegion = new StackPane();
            this.placeholderRegion.getStyleClass().setAll(new String[]{"placeholder"});
            this.getChildren().add(this.placeholderRegion);
         }

         Node var2 = (Node)TableSkinUtils.placeholderProperty(this).get();
         if (var2 == null) {
            if (this.placeholderLabel == null) {
               this.placeholderLabel = new Label();
            }

            String var3 = this.visibleColCount == 0 ? this.NO_COLUMNS_TEXT : this.EMPTY_TABLE_TEXT;
            this.placeholderLabel.setText(var3);
            this.placeholderRegion.getChildren().setAll(new Node[]{this.placeholderLabel});
         } else {
            this.placeholderRegion.getChildren().setAll(new Node[]{var2});
         }
      }

      this.flow.setVisible(!var1);
      if (this.placeholderRegion != null) {
         this.placeholderRegion.setVisible(var1);
      }
   }

   private void updateContentWidth() {
      double var1 = this.flow.getWidth();
      if (this.flow.getVbar().isVisible()) {
         var1 -= this.flow.getVbar().getWidth();
      }

      if (var1 <= 0.0) {
         Control var3 = this.getSkinnable();
         var1 = var3.getWidth() - (this.snappedLeftInset() + this.snappedRightInset());
      }

      var1 = Math.max(0.0, var1);
      this.getSkinnable().getProperties().put("TableView.contentWidth", Math.floor(var1));
   }

   private void refreshView() {
      this.markItemCountDirty();
      Control var1 = this.getSkinnable();
      if (var1 != null) {
         var1.requestLayout();
      }
   }

   public void scrollHorizontally() {
      TableFocusModel var1 = this.getFocusModel();
      if (var1 != null) {
         TableColumnBase var2 = this.getFocusedCell().getTableColumn();
         this.scrollHorizontally((TC)var2);
      }
   }

   protected void scrollHorizontally(TC var1) {
      if (var1 != null && var1.isVisible()) {
         Control var2 = this.getSkinnable();
         TableColumnHeader var3 = this.tableHeaderRow.getColumnHeaderFor(var1);
         if (var3 != null && !(var3.getWidth() <= 0.0)) {
            double var4 = 0.0;

            for (TableColumnBase var7 : this.getVisibleLeafColumns()) {
               if (var7.equals(var1)) {
                  break;
               }

               var4 += var7.getWidth();
            }

            double var18 = var4 + var1.getWidth();
            double var8 = var2.getWidth() - this.snappedLeftInset() - this.snappedRightInset();
            double var10 = this.flow.getHbar().getValue();
            double var12 = this.flow.getHbar().getMax();
            double var14;
            if (var4 < var10 && var4 >= 0.0) {
               var14 = var4;
            } else {
               double var16 = !(var4 < 0.0) && !(var18 > var8) ? 0.0 : var4 - var10;
               var14 = var10 + var16 > var12 ? var12 : var10 + var16;
            }

            this.flow.getHbar().setValue(var14);
         } else {
            Platform.runLater(() -> this.scrollHorizontally((TC)var1));
         }
      }
   }

   private boolean isCellSelected(int var1) {
      TableSelectionModel var2 = this.getSelectionModel();
      if (var2 == null) {
         return false;
      } else if (!var2.isCellSelectionEnabled()) {
         return false;
      } else {
         int var3 = this.getVisibleLeafColumns().size();

         for (int var4 = 0; var4 < var3; var4++) {
            if (var2.isSelected(var1, TableSkinUtils.getVisibleLeafColumn(this, var4))) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean isCellFocused(int var1) {
      TableFocusModel var2 = this.getFocusModel();
      if (var2 == null) {
         return false;
      } else {
         int var3 = this.getVisibleLeafColumns().size();

         for (int var4 = 0; var4 < var3; var4++) {
            if (var2.isFocused(var1, TableSkinUtils.getVisibleLeafColumn(this, var4))) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   protected Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case FOCUS_ITEM:
            TableFocusModel var6 = this.getFocusModel();
            int var7 = var6.getFocusedIndex();
            if (var7 == -1) {
               if (this.placeholderRegion != null && this.placeholderRegion.isVisible()) {
                  return this.placeholderRegion.getChildren().get(0);
               }

               if (this.getItemCount() <= 0) {
                  return null;
               }

               var7 = 0;
            }

            return this.flow.getPrivateCell(var7);
         case CELL_AT_ROW_COLUMN:
            int var5 = (Integer)var2[0];
            return this.flow.getPrivateCell(var5);
         case COLUMN_AT_INDEX:
            int var3 = (Integer)var2[0];
            TableColumnBase var4 = TableSkinUtils.getVisibleLeafColumn(this, var3);
            return this.getTableHeaderRow().getColumnHeaderFor(var4);
         case HEADER:
            return this.getTableHeaderRow();
         case VERTICAL_SCROLLBAR:
            return this.flow.getVbar();
         case HORIZONTAL_SCROLLBAR:
            return this.flow.getHbar();
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }
}
