package javafx.scene.control.skin;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.tk.Toolkit;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javafx.animation.FadeTransition;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.css.StyleOrigin;
import javafx.css.StyleableObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TableColumnBase;
import javafx.util.Duration;

public abstract class TableRowSkinBase<T, C extends IndexedCell, R extends IndexedCell> extends CellSkinBase<C> {
   private static boolean IS_STUB_TOOLKIT = Toolkit.getToolkit().toString().contains("StubToolkit");
   private static boolean DO_ANIMATIONS = !IS_STUB_TOOLKIT && !PlatformUtil.isEmbedded();
   private static final Duration FADE_DURATION = Duration.millis(200.0);
   static final Map<TableColumnBase<?, ?>, Double> maxDisclosureWidthMap = new WeakHashMap<>();
   private static final int DEFAULT_FULL_REFRESH_COUNTER = 100;
   WeakHashMap<TableColumnBase, Reference<R>> cellsMap;
   final List<R> cells = new ArrayList<>();
   private int fullRefreshCounter = 100;
   boolean isDirty = false;
   boolean updateCells = false;
   double fixedCellSize;
   boolean fixedCellSizeEnabled;
   private ListChangeListener<TableColumnBase> visibleLeafColumnsListener = var1x -> {
      this.isDirty = true;
      this.getSkinnable().requestLayout();
   };
   private WeakListChangeListener<TableColumnBase> weakVisibleLeafColumnsListener = new WeakListChangeListener(this.visibleLeafColumnsListener);

   public TableRowSkinBase(C var1) {
      super((C)var1);
      this.getSkinnable().setPickOnBounds(false);
      this.recreateCells();
      this.updateCells(true);
      this.getVisibleLeafColumns().addListener(this.weakVisibleLeafColumnsListener);
      var1.itemProperty().addListener(var1x -> this.requestCellUpdate());
      this.registerChangeListener(var1.indexProperty(), var1x -> {
         if (this.getSkinnable().isEmpty()) {
            this.requestCellUpdate();
         }
      });
   }

   protected abstract R createCell(TableColumnBase<T, ?> var1);

   protected abstract void updateCell(R var1, C var2);

   protected abstract TableColumnBase<T, ?> getTableColumn(R var1);

   protected abstract ObservableList<? extends TableColumnBase> getVisibleLeafColumns();

   protected ObjectProperty<Node> graphicProperty() {
      return null;
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      this.checkState();
      if (!this.cellsMap.isEmpty()) {
         ObservableList var9 = this.getVisibleLeafColumns();
         if (var9.isEmpty()) {
            super.layoutChildren(var1, var3, var5, var7);
         } else {
            IndexedCell var10 = this.getSkinnable();
            double var11 = 0.0;
            double var13 = 0.0;
            double var15 = 0.0;
            boolean var17 = this.isIndentationRequired();
            boolean var18 = this.isDisclosureNodeVisible();
            int var19 = 0;
            Node var20 = null;
            if (var17) {
               TableColumnBase var21 = this.getTreeColumn();
               var19 = var21 == null ? 0 : var9.indexOf(var21);
               var19 = var19 < 0 ? 0 : var19;
               int var22 = this.getIndentationLevel((C)var10);
               if (!this.isShowRoot()) {
                  var22--;
               }

               double var23 = this.getIndentationPerLevel();
               var11 = (double)var22 * var23;
               double var25 = maxDisclosureWidthMap.containsKey(var21) ? maxDisclosureWidthMap.get(var21) : 0.0;
               var13 = var25;
               var20 = this.getDisclosureNode();
               if (var20 != null) {
                  var20.setVisible(var18);
                  if (var18) {
                     var13 = var20.prefWidth(var7);
                     if (var13 > var25) {
                        maxDisclosureWidthMap.put(var21, var13);
                        VirtualFlow var27 = this.getVirtualFlow();
                        int var28 = this.getSkinnable().getIndex();

                        for (int var29 = 0; var29 < var27.cells.size(); var29++) {
                           IndexedCell var30 = (IndexedCell)var27.cells.get(var29);
                           if (var30 != null && !var30.isEmpty()) {
                              var30.requestLayout();
                              var30.layout();
                           }
                        }
                     }
                  }
               }
            }

            double var48 = this.snappedTopInset() + this.snappedBottomInset();
            double var49 = this.snappedLeftInset() + this.snappedRightInset();
            double var50 = var10.getHeight();
            int var31 = var10.getIndex();
            if (var31 >= 0) {
               int var32 = 0;

               for (int var33 = this.cells.size(); var32 < var33; var32++) {
                  IndexedCell var34 = this.cells.get(var32);
                  TableColumnBase var35 = this.getTableColumn((R)var34);
                  boolean var36 = true;
                  double var46;
                  if (this.fixedCellSizeEnabled) {
                     var36 = this.isColumnPartiallyOrFullyVisible(var35);
                     var46 = this.fixedCellSize;
                  } else {
                     var46 = Math.max(var50, var34.prefHeight(-1.0));
                     var46 = this.snapSizeY(var46) - this.snapSizeY(var48);
                  }

                  double var45;
                  if (var36) {
                     if (this.fixedCellSizeEnabled && var34.getParent() == null) {
                        this.getChildren().add(var34);
                     }

                     var45 = var34.prefWidth(var46) - this.snapSizeX(var49);
                     boolean var37 = var7 <= 24.0;
                     StyleOrigin var38 = ((StyleableObjectProperty)var34.alignmentProperty()).getStyleOrigin();
                     if (!var37 && var38 == null) {
                        var34.setAlignment(Pos.TOP_LEFT);
                     }

                     if (var17 && var32 == var19) {
                        if (var18) {
                           double var39 = var20.prefHeight(var13);
                           if (var45 > 0.0 && var45 < var13 + var11) {
                              this.fadeOut(var20);
                           } else {
                              this.fadeIn(var20);
                              var20.resize(var13, var39);
                              var20.relocate(var1 + var11, var37 ? var7 / 2.0 - var39 / 2.0 : var3 + var34.getPadding().getTop());
                              var20.toFront();
                           }
                        }

                        ObjectProperty var51 = this.graphicProperty();
                        Node var40 = var51 == null ? null : (Node)var51.get();
                        if (var40 != null) {
                           var15 = var40.prefWidth(-1.0) + 3.0;
                           double var41 = var40.prefHeight(var15);
                           if (var45 > 0.0 && var45 < var13 + var11 + var15) {
                              this.fadeOut(var40);
                           } else {
                              this.fadeIn(var40);
                              var40.relocate(var1 + var11 + var13, var37 ? var7 / 2.0 - var41 / 2.0 : var3 + var34.getPadding().getTop());
                              var40.toFront();
                           }
                        }
                     }

                     var34.resize(var45, var46);
                     var34.relocate(var1, this.snappedTopInset());
                     var34.requestLayout();
                  } else {
                     var45 = this.snapSizeX(var34.prefWidth(-1.0)) - this.snapSizeX(var49);
                     if (this.fixedCellSizeEnabled) {
                        this.getChildren().remove(var34);
                     }
                  }

                  var1 += var45;
               }
            }
         }
      }
   }

   int getIndentationLevel(C var1) {
      return 0;
   }

   double getIndentationPerLevel() {
      return 0.0;
   }

   boolean isIndentationRequired() {
      return false;
   }

   TableColumnBase getTreeColumn() {
      return null;
   }

   Node getDisclosureNode() {
      return null;
   }

   boolean isDisclosureNodeVisible() {
      return false;
   }

   boolean isShowRoot() {
      return true;
   }

   void updateCells(boolean var1) {
      if (var1) {
         if (this.fullRefreshCounter == 0) {
            this.recreateCells();
         }

         this.fullRefreshCounter--;
      }

      boolean var2 = this.cells.isEmpty();
      this.cells.clear();
      IndexedCell var3 = this.getSkinnable();
      int var4 = var3.getIndex();
      ObservableList var5 = this.getVisibleLeafColumns();
      int var6 = 0;

      for (int var7 = var5.size(); var6 < var7; var6++) {
         TableColumnBase var8 = (TableColumnBase)var5.get(var6);
         IndexedCell var9 = null;
         if (this.cellsMap.containsKey(var8)) {
            var9 = this.cellsMap.get(var8).get();
            if (var9 == null) {
               this.cellsMap.remove(var8);
            }
         }

         if (var9 == null) {
            var9 = this.createCellAndCache(var8);
         }

         this.updateCell((R)var9, (C)var3);
         var9.updateIndex(var4);
         this.cells.add((R)var9);
      }

      if (this.fixedCellSizeEnabled) {
         ArrayList var10 = new ArrayList();

         for (Node var12 : this.getChildren()) {
            if (var12 instanceof IndexedCell) {
               TableColumnBase var13 = this.getTableColumn((R)var12);
               if (!this.getVisibleLeafColumns().contains(var13)) {
                  var10.add(var12);
               }
            }
         }

         this.getChildren().removeAll(var10);
      } else if (var1 || var2) {
         this.getChildren().setAll(this.cells);
      }
   }

   VirtualFlow<C> getVirtualFlow() {
      for (Object var1 = this.getSkinnable(); var1 != null; var1 = var1.getParent()) {
         if (var1 instanceof VirtualFlow) {
            return (VirtualFlow<C>)var1;
         }
      }

      return null;
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      double var11 = 0.0;

      for (IndexedCell var14 : this.cells) {
         var11 += var14.prefWidth(var1);
      }

      return var11;
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      if (this.fixedCellSizeEnabled) {
         return this.fixedCellSize;
      } else {
         this.checkState();
         if (this.getCellSize() < 24.0) {
            return this.getCellSize();
         } else {
            double var11 = 0.0;
            int var13 = this.cells.size();

            for (int var14 = 0; var14 < var13; var14++) {
               IndexedCell var15 = this.cells.get(var14);
               var11 = Math.max(var11, var15.prefHeight(-1.0));
            }

            return Math.max(var11, Math.max(this.getCellSize(), this.getSkinnable().minHeight(-1.0)));
         }
      }
   }

   @Override
   protected double computeMinHeight(double var1, double var3, double var5, double var7, double var9) {
      if (this.fixedCellSizeEnabled) {
         return this.fixedCellSize;
      } else {
         this.checkState();
         if (this.getCellSize() < 24.0) {
            return this.getCellSize();
         } else {
            double var11 = 0.0;
            int var13 = this.cells.size();

            for (int var14 = 0; var14 < var13; var14++) {
               IndexedCell var15 = this.cells.get(var14);
               var11 = Math.max(var11, var15.minHeight(-1.0));
            }

            return var11;
         }
      }
   }

   @Override
   protected double computeMaxHeight(double var1, double var3, double var5, double var7, double var9) {
      return this.fixedCellSizeEnabled ? this.fixedCellSize : super.computeMaxHeight(var1, var3, var5, var7, var9);
   }

   final void checkState() {
      if (this.isDirty) {
         this.updateCells(true);
         this.isDirty = false;
         this.updateCells = false;
      } else if (this.updateCells) {
         this.updateCells(false);
         this.updateCells = false;
      }
   }

   private boolean isColumnPartiallyOrFullyVisible(TableColumnBase var1) {
      if (var1 != null && var1.isVisible()) {
         VirtualFlow var2 = this.getVirtualFlow();
         double var3 = var2 == null ? 0.0 : var2.getHbar().getValue();
         double var5 = 0.0;
         ObservableList var7 = this.getVisibleLeafColumns();
         int var8 = 0;

         for (int var9 = var7.size(); var8 < var9; var8++) {
            TableColumnBase var10 = (TableColumnBase)var7.get(var8);
            if (var10.equals(var1)) {
               break;
            }

            var5 += var10.getWidth();
         }

         double var13 = var5 + var1.getWidth();
         Insets var14 = this.getSkinnable().getPadding();
         double var11 = this.getSkinnable().getWidth() - var14.getLeft() + var14.getRight();
         return (var5 >= var3 || var13 > var3) && (var5 < var11 + var3 || var13 <= var11 + var3);
      } else {
         return false;
      }
   }

   private void requestCellUpdate() {
      this.updateCells = true;
      this.getSkinnable().requestLayout();
      int var1 = this.getSkinnable().getIndex();
      int var2 = 0;

      for (int var3 = this.cells.size(); var2 < var3; var2++) {
         this.cells.get(var2).updateIndex(var1);
      }
   }

   private void recreateCells() {
      if (this.cellsMap != null) {
         for (Reference var3 : this.cellsMap.values()) {
            IndexedCell var4 = (IndexedCell)var3.get();
            if (var4 != null) {
               var4.updateIndex(-1);
               var4.getSkin().dispose();
               var4.setSkin(null);
            }
         }

         this.cellsMap.clear();
      }

      ObservableList var5 = this.getVisibleLeafColumns();
      this.cellsMap = new WeakHashMap<>(var5.size());
      this.fullRefreshCounter = 100;
      this.getChildren().clear();

      for (TableColumnBase var7 : var5) {
         if (!this.cellsMap.containsKey(var7)) {
            this.createCellAndCache(var7);
         }
      }
   }

   private R createCellAndCache(TableColumnBase<T, ?> var1) {
      IndexedCell var2 = this.createCell(var1);
      this.cellsMap.put(var1, (Reference<R>)(new WeakReference<>(var2)));
      return (R)var2;
   }

   private void fadeOut(Node var1) {
      if (!(var1.getOpacity() < 1.0)) {
         if (!DO_ANIMATIONS) {
            var1.setOpacity(0.0);
         } else {
            FadeTransition var2 = new FadeTransition(FADE_DURATION, var1);
            var2.setToValue(0.0);
            var2.play();
         }
      }
   }

   private void fadeIn(Node var1) {
      if (!(var1.getOpacity() > 0.0)) {
         if (!DO_ANIMATIONS) {
            var1.setOpacity(1.0);
         } else {
            FadeTransition var2 = new FadeTransition(FADE_DURATION, var1);
            var2.setToValue(1.0);
            var2.play();
         }
      }
   }
}
