package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.TreeCellBehavior;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javafx.beans.property.DoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class TreeCellSkin<T> extends CellSkinBase<TreeCell<T>> {
   private static final Map<TreeView<?>, Double> maxDisclosureWidthMap = new WeakHashMap<>();
   private boolean disclosureNodeDirty = true;
   private TreeItem<?> treeItem;
   private final BehaviorBase<TreeCell<T>> behavior;
   private DoubleProperty indent = null;

   public TreeCellSkin(TreeCell<T> var1) {
      super(var1);
      this.behavior = new TreeCellBehavior<>(var1);
      this.updateTreeItem();
      this.registerChangeListener(var1.treeItemProperty(), var1x -> {
         this.updateTreeItem();
         this.disclosureNodeDirty = true;
         this.getSkinnable().requestLayout();
      });
      this.registerChangeListener(var1.textProperty(), var1x -> this.getSkinnable().requestLayout());
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
               return TreeCellSkin.this;
            }

            public String getName() {
               return "indent";
            }

            public CssMetaData<TreeCell<?>, Number> getCssMetaData() {
               return TreeCellSkin.StyleableProperties.INDENT;
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
      this.updateDisclosureNode();
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      TreeView var9 = this.getSkinnable().getTreeView();
      if (var9 != null) {
         if (this.disclosureNodeDirty) {
            this.updateDisclosureNode();
            this.disclosureNodeDirty = false;
         }

         Node var10 = this.getSkinnable().getDisclosureNode();
         int var11 = var9.getTreeItemLevel(this.treeItem);
         if (!var9.isShowRoot()) {
            var11--;
         }

         double var12 = this.getIndent() * (double)var11;
         var1 += var12;
         boolean var14 = var10 != null && this.treeItem != null && !this.treeItem.isLeaf();
         double var15 = maxDisclosureWidthMap.containsKey(var9) ? maxDisclosureWidthMap.get(var9) : 18.0;
         double var17 = var15;
         if (var14) {
            if (var10 == null || var10.getScene() == null) {
               this.updateChildren();
            }

            if (var10 != null) {
               var17 = var10.prefWidth(var7);
               if (var17 > var15) {
                  maxDisclosureWidthMap.put(var9, var17);
               }

               double var19 = var10.prefHeight(var17);
               var10.resize(var17, var19);
               this.positionInArea(var10, var1, var3, var17, var19, 0.0, HPos.CENTER, VPos.CENTER);
            }
         }

         int var24 = this.treeItem != null && this.treeItem.getGraphic() == null ? 0 : 3;
         var1 += var17 + (double)var24;
         var5 -= var12 + var17 + (double)var24;
         Node var20 = this.getSkinnable().getGraphic();
         if (var20 != null && !this.getChildren().contains(var20)) {
            this.getChildren().add(var20);
         }

         this.layoutLabelInArea(var1, var3, var5, var7);
      }
   }

   @Override
   protected double computeMinHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = this.getFixedCellSize();
      if (var11 > 0.0) {
         return var11;
      } else {
         double var13 = super.computeMinHeight(var1, var3, var5, var7, var9);
         Node var15 = this.getSkinnable().getDisclosureNode();
         return var15 == null ? var13 : Math.max(var15.minHeight(-1.0), var13);
      }
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = this.getFixedCellSize();
      if (var11 > 0.0) {
         return var11;
      } else {
         TreeCell var13 = this.getSkinnable();
         double var14 = super.computePrefHeight(var1, var3, var5, var7, var9);
         Node var16 = var13.getDisclosureNode();
         double var17 = var16 == null ? var14 : Math.max(var16.prefHeight(-1.0), var14);
         return this.snapSizeY(Math.max(var13.getMinHeight(), var17));
      }
   }

   @Override
   protected double computeMaxHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = this.getFixedCellSize();
      return var11 > 0.0 ? var11 : super.computeMaxHeight(var1, var3, var5, var7, var9);
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      double var11 = super.computePrefWidth(var1, var3, var5, var7, var9);
      double var13 = this.snappedLeftInset() + this.snappedRightInset();
      TreeView var15 = this.getSkinnable().getTreeView();
      if (var15 == null) {
         return var13;
      } else if (this.treeItem == null) {
         return var13;
      } else {
         int var16 = var15.getTreeItemLevel(this.treeItem);
         if (!var15.isShowRoot()) {
            var16--;
         }

         var13 = var11 + this.getIndent() * (double)var16;
         Node var17 = this.getSkinnable().getDisclosureNode();
         double var18 = var17 == null ? 0.0 : var17.prefWidth(-1.0);
         double var20 = maxDisclosureWidthMap.containsKey(var15) ? maxDisclosureWidthMap.get(var15) : 0.0;
         return var13 + Math.max(var20, var18);
      }
   }

   private double getFixedCellSize() {
      TreeView var1 = this.getSkinnable().getTreeView();
      return var1 != null ? var1.getFixedCellSize() : -1.0;
   }

   private void updateTreeItem() {
      this.treeItem = this.getSkinnable().getTreeItem();
   }

   private void updateDisclosureNode() {
      if (!this.getSkinnable().isEmpty()) {
         Node var1 = this.getSkinnable().getDisclosureNode();
         if (var1 != null) {
            boolean var2 = this.treeItem != null && !this.treeItem.isLeaf();
            var1.setVisible(var2);
            if (!var2) {
               this.getChildren().remove(var1);
            } else if (var1.getParent() == null) {
               this.getChildren().add(var1);
               var1.toFront();
            } else {
               var1.toBack();
            }

            if (var1.getScene() != null) {
               var1.applyCss();
            }
         }
      }
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return TreeCellSkin.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   private static class StyleableProperties {
      private static final CssMetaData<TreeCell<?>, Number> INDENT = new CssMetaData<TreeCell<?>, Number>("-fx-indent", SizeConverter.getInstance(), 10.0) {
         public boolean isSettable(TreeCell<?> var1) {
            DoubleProperty var2 = ((TreeCellSkin)var1.getSkin()).indentProperty();
            return var2 == null || !var2.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TreeCell<?> var1) {
            TreeCellSkin var2 = (TreeCellSkin)var1.getSkin();
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
