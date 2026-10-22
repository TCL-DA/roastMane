package javafx.scene.layout;

import com.sun.javafx.geom.Vec2d;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;

public class BorderPane extends Pane {
   private static final String MARGIN = "borderpane-margin";
   private static final String ALIGNMENT = "borderpane-alignment";
   private ObjectProperty<Node> center;
   private ObjectProperty<Node> top;
   private ObjectProperty<Node> bottom;
   private ObjectProperty<Node> left;
   private ObjectProperty<Node> right;

   public static void setAlignment(Node var0, Pos var1) {
      setConstraint(var0, "borderpane-alignment", var1);
   }

   public static Pos getAlignment(Node var0) {
      return (Pos)getConstraint(var0, "borderpane-alignment");
   }

   public static void setMargin(Node var0, Insets var1) {
      setConstraint(var0, "borderpane-margin", var1);
   }

   public static Insets getMargin(Node var0) {
      return (Insets)getConstraint(var0, "borderpane-margin");
   }

   private static Insets getNodeMargin(Node var0) {
      Insets var1 = getMargin(var0);
      return var1 != null ? var1 : Insets.EMPTY;
   }

   public static void clearConstraints(Node var0) {
      setAlignment(var0, null);
      setMargin(var0, null);
   }

   public BorderPane() {
   }

   public BorderPane(Node var1) {
      this.setCenter(var1);
   }

   public BorderPane(Node var1, Node var2, Node var3, Node var4, Node var5) {
      this.setCenter(var1);
      this.setTop(var2);
      this.setRight(var3);
      this.setBottom(var4);
      this.setLeft(var5);
   }

   public final ObjectProperty<Node> centerProperty() {
      if (this.center == null) {
         this.center = new BorderPane.BorderPositionProperty("center");
      }

      return this.center;
   }

   public final void setCenter(Node var1) {
      this.centerProperty().set(var1);
   }

   public final Node getCenter() {
      return this.center == null ? null : (Node)this.center.get();
   }

   public final ObjectProperty<Node> topProperty() {
      if (this.top == null) {
         this.top = new BorderPane.BorderPositionProperty("top");
      }

      return this.top;
   }

   public final void setTop(Node var1) {
      this.topProperty().set(var1);
   }

   public final Node getTop() {
      return this.top == null ? null : (Node)this.top.get();
   }

   public final ObjectProperty<Node> bottomProperty() {
      if (this.bottom == null) {
         this.bottom = new BorderPane.BorderPositionProperty("bottom");
      }

      return this.bottom;
   }

   public final void setBottom(Node var1) {
      this.bottomProperty().set(var1);
   }

   public final Node getBottom() {
      return this.bottom == null ? null : (Node)this.bottom.get();
   }

   public final ObjectProperty<Node> leftProperty() {
      if (this.left == null) {
         this.left = new BorderPane.BorderPositionProperty("left");
      }

      return this.left;
   }

   public final void setLeft(Node var1) {
      this.leftProperty().set(var1);
   }

   public final Node getLeft() {
      return this.left == null ? null : (Node)this.left.get();
   }

   public final ObjectProperty<Node> rightProperty() {
      if (this.right == null) {
         this.right = new BorderPane.BorderPositionProperty("right");
      }

      return this.right;
   }

   public final void setRight(Node var1) {
      this.rightProperty().set(var1);
   }

   public final Node getRight() {
      return this.right == null ? null : (Node)this.right.get();
   }

   @Override
   public Orientation getContentBias() {
      Node var1 = this.getCenter();
      if (var1 != null && var1.isManaged() && var1.getContentBias() != null) {
         return var1.getContentBias();
      } else {
         Node var2 = this.getRight();
         if (var2 != null && var2.isManaged() && var2.getContentBias() == Orientation.VERTICAL) {
            return var2.getContentBias();
         } else {
            Node var3 = this.getLeft();
            if (var3 != null && var3.isManaged() && var3.getContentBias() == Orientation.VERTICAL) {
               return var3.getContentBias();
            } else {
               Node var4 = this.getBottom();
               if (var4 != null && var4.isManaged() && var4.getContentBias() == Orientation.HORIZONTAL) {
                  return var4.getContentBias();
               } else {
                  Node var5 = this.getTop();
                  return var5 != null && var5.isManaged() && var5.getContentBias() == Orientation.HORIZONTAL ? var5.getContentBias() : null;
               }
            }
         }
      }
   }

   @Override
   protected double computeMinWidth(double var1) {
      double var3 = this.getAreaWidth(this.getTop(), -1.0, true);
      double var5 = this.getAreaWidth(this.getBottom(), -1.0, true);
      double var7;
      double var9;
      double var11;
      if (var1 == -1.0
         || !this.childHasContentBias(this.getLeft(), Orientation.VERTICAL)
            && !this.childHasContentBias(this.getRight(), Orientation.VERTICAL)
            && !this.childHasContentBias(this.getCenter(), Orientation.VERTICAL)) {
         var7 = this.getAreaWidth(this.getLeft(), -1.0, false);
         var9 = this.getAreaWidth(this.getRight(), -1.0, false);
         var11 = this.getAreaWidth(this.getCenter(), -1.0, true);
      } else {
         double var13 = this.getAreaHeight(this.getTop(), -1.0, false);
         double var15 = this.getAreaHeight(this.getBottom(), -1.0, false);
         double var17 = Math.max(0.0, var1 - var13 - var15);
         var7 = this.getAreaWidth(this.getLeft(), var17, false);
         var9 = this.getAreaWidth(this.getRight(), var17, false);
         var11 = this.getAreaWidth(this.getCenter(), var17, true);
      }

      Insets var19 = this.getInsets();
      return var19.getLeft() + Math.max(var7 + var11 + var9, Math.max(var3, var5)) + var19.getRight();
   }

   @Override
   protected double computeMinHeight(double var1) {
      Insets var3 = this.getInsets();
      double var4 = this.getAreaHeight(this.getTop(), var1, false);
      double var6 = this.getAreaHeight(this.getBottom(), var1, false);
      double var8 = this.getAreaHeight(this.getLeft(), -1.0, true);
      double var10 = this.getAreaHeight(this.getRight(), -1.0, true);
      double var12;
      if (var1 != -1.0 && this.childHasContentBias(this.getCenter(), Orientation.HORIZONTAL)) {
         double var14 = this.getAreaWidth(this.getLeft(), -1.0, false);
         double var16 = this.getAreaWidth(this.getRight(), -1.0, false);
         var12 = this.getAreaHeight(this.getCenter(), Math.max(0.0, var1 - var14 - var16), true);
      } else {
         var12 = this.getAreaHeight(this.getCenter(), -1.0, true);
      }

      double var18 = Math.max(var12, Math.max(var10, var8));
      return var3.getTop() + var4 + var18 + var6 + var3.getBottom();
   }

   @Override
   protected double computePrefWidth(double var1) {
      double var3 = this.getAreaWidth(this.getTop(), -1.0, false);
      double var5 = this.getAreaWidth(this.getBottom(), -1.0, false);
      double var7;
      double var9;
      double var11;
      if (var1 == -1.0
         || !this.childHasContentBias(this.getLeft(), Orientation.VERTICAL)
            && !this.childHasContentBias(this.getRight(), Orientation.VERTICAL)
            && !this.childHasContentBias(this.getCenter(), Orientation.VERTICAL)) {
         var7 = this.getAreaWidth(this.getLeft(), -1.0, false);
         var9 = this.getAreaWidth(this.getRight(), -1.0, false);
         var11 = this.getAreaWidth(this.getCenter(), -1.0, false);
      } else {
         double var13 = this.getAreaHeight(this.getTop(), -1.0, false);
         double var15 = this.getAreaHeight(this.getBottom(), -1.0, false);
         double var17 = Math.max(0.0, var1 - var13 - var15);
         var7 = this.getAreaWidth(this.getLeft(), var17, false);
         var9 = this.getAreaWidth(this.getRight(), var17, false);
         var11 = this.getAreaWidth(this.getCenter(), var17, false);
      }

      Insets var19 = this.getInsets();
      return var19.getLeft() + Math.max(var7 + var11 + var9, Math.max(var3, var5)) + var19.getRight();
   }

   @Override
   protected double computePrefHeight(double var1) {
      Insets var3 = this.getInsets();
      double var4 = this.getAreaHeight(this.getTop(), var1, false);
      double var6 = this.getAreaHeight(this.getBottom(), var1, false);
      double var8 = this.getAreaHeight(this.getLeft(), -1.0, false);
      double var10 = this.getAreaHeight(this.getRight(), -1.0, false);
      double var12;
      if (var1 != -1.0 && this.childHasContentBias(this.getCenter(), Orientation.HORIZONTAL)) {
         double var14 = this.getAreaWidth(this.getLeft(), -1.0, false);
         double var16 = this.getAreaWidth(this.getRight(), -1.0, false);
         var12 = this.getAreaHeight(this.getCenter(), Math.max(0.0, var1 - var14 - var16), false);
      } else {
         var12 = this.getAreaHeight(this.getCenter(), -1.0, false);
      }

      double var18 = Math.max(var12, Math.max(var10, var8));
      return var3.getTop() + var4 + var18 + var6 + var3.getBottom();
   }

   @Override
   protected void layoutChildren() {
      Insets var1 = this.getInsets();
      double var2 = this.getWidth();
      double var4 = this.getHeight();
      Orientation var6 = this.getContentBias();
      if (var6 == null) {
         double var7 = this.minWidth(-1.0);
         double var9 = this.minHeight(-1.0);
         var2 = var2 < var7 ? var7 : var2;
         var4 = var4 < var9 ? var9 : var4;
      } else if (var6 == Orientation.HORIZONTAL) {
         double var37 = this.minWidth(-1.0);
         var2 = var2 < var37 ? var37 : var2;
         double var40 = this.minHeight(var2);
         var4 = var4 < var40 ? var40 : var4;
      } else {
         double var38 = this.minHeight(-1.0);
         var4 = var4 < var38 ? var38 : var4;
         double var41 = this.minWidth(var4);
         var2 = var2 < var41 ? var41 : var2;
      }

      double var39 = var1.getLeft();
      double var42 = var1.getTop();
      double var11 = var2 - var39 - var1.getRight();
      double var13 = var4 - var42 - var1.getBottom();
      Node var15 = this.getCenter();
      Node var16 = this.getRight();
      Node var17 = this.getBottom();
      Node var18 = this.getLeft();
      Node var19 = this.getTop();
      double var20 = 0.0;
      if (var19 != null && var19.isManaged()) {
         Insets var22 = getNodeMargin(var19);
         double var23 = this.adjustWidthByMargin(var11, var22);
         double var25 = this.adjustHeightByMargin(var13, var22);
         double var43 = this.snapSizeY(var19.prefHeight(var23));
         double var44 = Math.min(var43, var25);
         Vec2d var27 = boundedNodeSizeWithBias(var19, var23, var44, true, true, TEMP_VEC2D);
         double var45 = this.snapSizeY(var27.y);
         var19.resize(this.snapSizeX(var27.x), var45);
         var20 = this.snapSpaceY(var22.getBottom()) + var45 + this.snapSpaceY(var22.getTop());
         Pos var28 = getAlignment(var19);
         positionInArea(
            var19,
            var39,
            var42,
            var11,
            var20,
            0.0,
            var22,
            var28 != null ? var28.getHpos() : HPos.LEFT,
            var28 != null ? var28.getVpos() : VPos.TOP,
            this.isSnapToPixel()
         );
      }

      double var46 = 0.0;
      if (var17 != null && var17.isManaged()) {
         Insets var24 = getNodeMargin(var17);
         double var54 = this.adjustWidthByMargin(var11, var24);
         double var59 = this.adjustHeightByMargin(var13 - var20, var24);
         double var47 = this.snapSizeY(var17.prefHeight(var54));
         double var48 = Math.min(var47, var59);
         Vec2d var29 = boundedNodeSizeWithBias(var17, var54, var48, true, true, TEMP_VEC2D);
         double var49 = this.snapSizeY(var29.y);
         var17.resize(this.snapSizeX(var29.x), var49);
         var46 = this.snapSpaceY(var24.getBottom()) + var49 + this.snapSpaceY(var24.getTop());
         Pos var30 = getAlignment(var17);
         positionInArea(
            var17,
            var39,
            var42 + var13 - var46,
            var11,
            var46,
            0.0,
            var24,
            var30 != null ? var30.getHpos() : HPos.LEFT,
            var30 != null ? var30.getVpos() : VPos.BOTTOM,
            this.isSnapToPixel()
         );
      }

      double var50 = 0.0;
      if (var18 != null && var18.isManaged()) {
         Insets var26 = getNodeMargin(var18);
         double var60 = this.adjustWidthByMargin(var11, var26);
         double var63 = this.adjustHeightByMargin(var13 - var20 - var46, var26);
         double var51 = this.snapSizeX(var18.prefWidth(var63));
         double var52 = Math.min(var51, var60);
         Vec2d var31 = boundedNodeSizeWithBias(var18, var52, var63, true, true, TEMP_VEC2D);
         double var53 = this.snapSizeX(var31.x);
         var18.resize(var53, this.snapSizeY(var31.y));
         var50 = this.snapSpaceX(var26.getLeft()) + var53 + this.snapSpaceX(var26.getRight());
         Pos var32 = getAlignment(var18);
         positionInArea(
            var18,
            var39,
            var42 + var20,
            var50,
            var13 - var20 - var46,
            0.0,
            var26,
            var32 != null ? var32.getHpos() : HPos.LEFT,
            var32 != null ? var32.getVpos() : VPos.TOP,
            this.isSnapToPixel()
         );
      }

      double var55 = 0.0;
      if (var16 != null && var16.isManaged()) {
         Insets var61 = getNodeMargin(var16);
         double var64 = this.adjustWidthByMargin(var11 - var50, var61);
         double var65 = this.adjustHeightByMargin(var13 - var20 - var46, var61);
         double var56 = this.snapSizeX(var16.prefWidth(var65));
         double var57 = Math.min(var56, var64);
         Vec2d var33 = boundedNodeSizeWithBias(var16, var57, var65, true, true, TEMP_VEC2D);
         double var58 = this.snapSizeX(var33.x);
         var16.resize(var58, this.snapSizeY(var33.y));
         var55 = this.snapSpaceX(var61.getLeft()) + var58 + this.snapSpaceX(var61.getRight());
         Pos var34 = getAlignment(var16);
         positionInArea(
            var16,
            var39 + var11 - var55,
            var42 + var20,
            var55,
            var13 - var20 - var46,
            0.0,
            var61,
            var34 != null ? var34.getHpos() : HPos.RIGHT,
            var34 != null ? var34.getVpos() : VPos.TOP,
            this.isSnapToPixel()
         );
      }

      if (var15 != null && var15.isManaged()) {
         Pos var62 = getAlignment(var15);
         this.layoutInArea(
            var15,
            var39 + var50,
            var42 + var20,
            var11 - var50 - var55,
            var13 - var20 - var46,
            0.0,
            getNodeMargin(var15),
            var62 != null ? var62.getHpos() : HPos.CENTER,
            var62 != null ? var62.getVpos() : VPos.CENTER
         );
      }
   }

   private double getAreaWidth(Node var1, double var2, boolean var4) {
      if (var1 != null && var1.isManaged()) {
         Insets var5 = getNodeMargin(var1);
         return var4 ? this.computeChildMinAreaWidth(var1, -1.0, var5, var2, false) : this.computeChildPrefAreaWidth(var1, -1.0, var5, var2, false);
      } else {
         return 0.0;
      }
   }

   private double getAreaHeight(Node var1, double var2, boolean var4) {
      if (var1 != null && var1.isManaged()) {
         Insets var5 = getNodeMargin(var1);
         return var4 ? this.computeChildMinAreaHeight(var1, -1.0, var5, var2) : this.computeChildPrefAreaHeight(var1, -1.0, var5, var2);
      } else {
         return 0.0;
      }
   }

   private boolean childHasContentBias(Node var1, Orientation var2) {
      return var1 != null && var1.isManaged() ? var1.getContentBias() == var2 : false;
   }

   private final class BorderPositionProperty extends ObjectPropertyBase<Node> {
      private Node oldValue = null;
      private final String propertyName;
      private boolean isBeingInvalidated;

      BorderPositionProperty(String var2) {
         this.propertyName = var2;
         BorderPane.this.getChildren().addListener(new ListChangeListener<Node>() {
            public void onChanged(Change<? extends Node> var1) {
               if (BorderPositionProperty.this.oldValue != null && !BorderPositionProperty.this.isBeingInvalidated) {
                  while (var1.next()) {
                     if (var1.wasRemoved()) {
                        List var2 = var1.getRemoved();
                        int var3 = 0;

                        for (int var4 = var2.size(); var3 < var4; var3++) {
                           if (var2.get(var3) == BorderPositionProperty.this.oldValue) {
                              BorderPositionProperty.this.oldValue = null;
                              BorderPositionProperty.this.set(null);
                           }
                        }
                     }
                  }
               }
            }
         });
      }

      protected void invalidated() {
         ObservableList var1 = BorderPane.this.getChildren();
         this.isBeingInvalidated = true;

         try {
            if (this.oldValue != null) {
               var1.remove(this.oldValue);
            }

            Node var2 = (Node)this.get();
            this.oldValue = var2;
            if (var2 != null) {
               var1.add(var2);
            }
         } finally {
            this.isBeingInvalidated = false;
         }
      }

      public Object getBean() {
         return BorderPane.this;
      }

      public String getName() {
         return this.propertyName;
      }
   }
}
