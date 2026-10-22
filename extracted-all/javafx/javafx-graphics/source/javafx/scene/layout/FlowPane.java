package javafx.scene.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.util.Callback;

public class FlowPane extends Pane {
   private static final String MARGIN_CONSTRAINT = "flowpane-margin";
   private static final Callback<Node, Insets> marginAccessor = var0 -> getMargin(var0);
   private ObjectProperty<Orientation> orientation;
   private DoubleProperty hgap;
   private DoubleProperty vgap;
   private DoubleProperty prefWrapLength;
   private ObjectProperty<Pos> alignment;
   private ObjectProperty<HPos> columnHalignment;
   private ObjectProperty<VPos> rowValignment;
   private List<FlowPane.Run> runs = null;
   private double lastMaxRunLength = -1.0;
   boolean computingRuns = false;

   public static void setMargin(Node var0, Insets var1) {
      setConstraint(var0, "flowpane-margin", var1);
   }

   public static Insets getMargin(Node var0) {
      return (Insets)getConstraint(var0, "flowpane-margin");
   }

   public static void clearConstraints(Node var0) {
      setMargin(var0, null);
   }

   public FlowPane() {
   }

   public FlowPane(Orientation var1) {
      this();
      this.setOrientation(var1);
   }

   public FlowPane(double var1, double var3) {
      this();
      this.setHgap(var1);
      this.setVgap(var3);
   }

   public FlowPane(Orientation var1, double var2, double var4) {
      this();
      this.setOrientation(var1);
      this.setHgap(var2);
      this.setVgap(var4);
   }

   public FlowPane(Node... var1) {
      this.getChildren().addAll(var1);
   }

   public FlowPane(Orientation var1, Node... var2) {
      this();
      this.setOrientation(var1);
      this.getChildren().addAll(var2);
   }

   public FlowPane(double var1, double var3, Node... var5) {
      this();
      this.setHgap(var1);
      this.setVgap(var3);
      this.getChildren().addAll(var5);
   }

   public FlowPane(Orientation var1, double var2, double var4, Node... var6) {
      this();
      this.setOrientation(var1);
      this.setHgap(var2);
      this.setVgap(var4);
      this.getChildren().addAll(var6);
   }

   public final ObjectProperty<Orientation> orientationProperty() {
      if (this.orientation == null) {
         this.orientation = new StyleableObjectProperty(Orientation.HORIZONTAL) {
            public void invalidated() {
               FlowPane.this.requestLayout();
            }

            @Override
            public CssMetaData<FlowPane, Orientation> getCssMetaData() {
               return FlowPane.StyleableProperties.ORIENTATION;
            }

            public Object getBean() {
               return FlowPane.this;
            }

            public String getName() {
               return "orientation";
            }
         };
      }

      return this.orientation;
   }

   public final void setOrientation(Orientation var1) {
      this.orientationProperty().set(var1);
   }

   public final Orientation getOrientation() {
      return this.orientation == null ? Orientation.HORIZONTAL : (Orientation)this.orientation.get();
   }

   public final DoubleProperty hgapProperty() {
      if (this.hgap == null) {
         this.hgap = new StyleableDoubleProperty() {
            public void invalidated() {
               FlowPane.this.requestLayout();
            }

            @Override
            public CssMetaData<FlowPane, Number> getCssMetaData() {
               return FlowPane.StyleableProperties.HGAP;
            }

            public Object getBean() {
               return FlowPane.this;
            }

            public String getName() {
               return "hgap";
            }
         };
      }

      return this.hgap;
   }

   public final void setHgap(double var1) {
      this.hgapProperty().set(var1);
   }

   public final double getHgap() {
      return this.hgap == null ? 0.0 : this.hgap.get();
   }

   public final DoubleProperty vgapProperty() {
      if (this.vgap == null) {
         this.vgap = new StyleableDoubleProperty() {
            public void invalidated() {
               FlowPane.this.requestLayout();
            }

            @Override
            public CssMetaData<FlowPane, Number> getCssMetaData() {
               return FlowPane.StyleableProperties.VGAP;
            }

            public Object getBean() {
               return FlowPane.this;
            }

            public String getName() {
               return "vgap";
            }
         };
      }

      return this.vgap;
   }

   public final void setVgap(double var1) {
      this.vgapProperty().set(var1);
   }

   public final double getVgap() {
      return this.vgap == null ? 0.0 : this.vgap.get();
   }

   public final DoubleProperty prefWrapLengthProperty() {
      if (this.prefWrapLength == null) {
         this.prefWrapLength = new DoublePropertyBase(400.0) {
            protected void invalidated() {
               FlowPane.this.requestLayout();
            }

            public Object getBean() {
               return FlowPane.this;
            }

            public String getName() {
               return "prefWrapLength";
            }
         };
      }

      return this.prefWrapLength;
   }

   public final void setPrefWrapLength(double var1) {
      this.prefWrapLengthProperty().set(var1);
   }

   public final double getPrefWrapLength() {
      return this.prefWrapLength == null ? 400.0 : this.prefWrapLength.get();
   }

   public final ObjectProperty<Pos> alignmentProperty() {
      if (this.alignment == null) {
         this.alignment = new StyleableObjectProperty<Pos>(Pos.TOP_LEFT) {
            public void invalidated() {
               FlowPane.this.requestLayout();
            }

            @Override
            public CssMetaData<FlowPane, Pos> getCssMetaData() {
               return FlowPane.StyleableProperties.ALIGNMENT;
            }

            public Object getBean() {
               return FlowPane.this;
            }

            public String getName() {
               return "alignment";
            }
         };
      }

      return this.alignment;
   }

   public final void setAlignment(Pos var1) {
      this.alignmentProperty().set(var1);
   }

   public final Pos getAlignment() {
      return this.alignment == null ? Pos.TOP_LEFT : (Pos)this.alignment.get();
   }

   private Pos getAlignmentInternal() {
      Pos var1 = this.getAlignment();
      return var1 == null ? Pos.TOP_LEFT : var1;
   }

   public final ObjectProperty<HPos> columnHalignmentProperty() {
      if (this.columnHalignment == null) {
         this.columnHalignment = new StyleableObjectProperty<HPos>(HPos.LEFT) {
            public void invalidated() {
               FlowPane.this.requestLayout();
            }

            @Override
            public CssMetaData<FlowPane, HPos> getCssMetaData() {
               return FlowPane.StyleableProperties.COLUMN_HALIGNMENT;
            }

            public Object getBean() {
               return FlowPane.this;
            }

            public String getName() {
               return "columnHalignment";
            }
         };
      }

      return this.columnHalignment;
   }

   public final void setColumnHalignment(HPos var1) {
      this.columnHalignmentProperty().set(var1);
   }

   public final HPos getColumnHalignment() {
      return this.columnHalignment == null ? HPos.LEFT : (HPos)this.columnHalignment.get();
   }

   private HPos getColumnHalignmentInternal() {
      HPos var1 = this.getColumnHalignment();
      return var1 == null ? HPos.LEFT : var1;
   }

   public final ObjectProperty<VPos> rowValignmentProperty() {
      if (this.rowValignment == null) {
         this.rowValignment = new StyleableObjectProperty<VPos>(VPos.CENTER) {
            public void invalidated() {
               FlowPane.this.requestLayout();
            }

            @Override
            public CssMetaData<FlowPane, VPos> getCssMetaData() {
               return FlowPane.StyleableProperties.ROW_VALIGNMENT;
            }

            public Object getBean() {
               return FlowPane.this;
            }

            public String getName() {
               return "rowValignment";
            }
         };
      }

      return this.rowValignment;
   }

   public final void setRowValignment(VPos var1) {
      this.rowValignmentProperty().set(var1);
   }

   public final VPos getRowValignment() {
      return this.rowValignment == null ? VPos.CENTER : (VPos)this.rowValignment.get();
   }

   private VPos getRowValignmentInternal() {
      VPos var1 = this.getRowValignment();
      return var1 == null ? VPos.CENTER : var1;
   }

   @Override
   public Orientation getContentBias() {
      return this.getOrientation();
   }

   @Override
   protected double computeMinWidth(double var1) {
      if (this.getContentBias() == Orientation.HORIZONTAL) {
         double var3 = 0.0;
         ObservableList var5 = this.getChildren();
         int var6 = 0;

         for (int var7 = var5.size(); var6 < var7; var6++) {
            Node var8 = (Node)var5.get(var6);
            if (var8.isManaged()) {
               var3 = Math.max(var3, var8.prefWidth(-1.0));
            }
         }

         Insets var9 = this.getInsets();
         return var9.getLeft() + this.snapSizeX(var3) + var9.getRight();
      } else {
         return this.computePrefWidth(var1);
      }
   }

   @Override
   protected double computeMinHeight(double var1) {
      if (this.getContentBias() == Orientation.VERTICAL) {
         double var3 = 0.0;
         ObservableList var5 = this.getChildren();
         int var6 = 0;

         for (int var7 = var5.size(); var6 < var7; var6++) {
            Node var8 = (Node)var5.get(var6);
            if (var8.isManaged()) {
               var3 = Math.max(var3, var8.prefHeight(-1.0));
            }
         }

         Insets var9 = this.getInsets();
         return var9.getTop() + this.snapSizeY(var3) + var9.getBottom();
      } else {
         return this.computePrefHeight(var1);
      }
   }

   @Override
   protected double computePrefWidth(double var1) {
      Insets var3 = this.getInsets();
      if (this.getOrientation() == Orientation.HORIZONTAL) {
         double var9 = this.getPrefWrapLength();
         List var10 = this.getRuns(var9);
         double var7 = this.computeContentWidth(var10);
         var7 = this.getPrefWrapLength() > var7 ? this.getPrefWrapLength() : var7;
         return var3.getLeft() + this.snapSizeX(var7) + var3.getRight();
      } else {
         double var4 = var1 != -1.0 ? var1 - var3.getTop() - var3.getBottom() : this.getPrefWrapLength();
         List var6 = this.getRuns(var4);
         return var3.getLeft() + this.computeContentWidth(var6) + var3.getRight();
      }
   }

   @Override
   protected double computePrefHeight(double var1) {
      Insets var3 = this.getInsets();
      if (this.getOrientation() == Orientation.HORIZONTAL) {
         double var9 = var1 != -1.0 ? var1 - var3.getLeft() - var3.getRight() : this.getPrefWrapLength();
         List var10 = this.getRuns(var9);
         return var3.getTop() + this.computeContentHeight(var10) + var3.getBottom();
      } else {
         double var4 = this.getPrefWrapLength();
         List var6 = this.getRuns(var4);
         double var7 = this.computeContentHeight(var6);
         var7 = this.getPrefWrapLength() > var7 ? this.getPrefWrapLength() : var7;
         return var3.getTop() + this.snapSizeY(var7) + var3.getBottom();
      }
   }

   @Override
   public void requestLayout() {
      if (!this.computingRuns) {
         this.runs = null;
      }

      super.requestLayout();
   }

   private List<FlowPane.Run> getRuns(double var1) {
      if (this.runs == null || var1 != this.lastMaxRunLength) {
         this.computingRuns = true;
         this.lastMaxRunLength = var1;
         this.runs = new ArrayList<>();
         double var3 = 0.0;
         double var5 = 0.0;
         FlowPane.Run var7 = new FlowPane.Run();
         double var8 = this.snapSpaceY(this.getVgap());
         double var10 = this.snapSpaceX(this.getHgap());
         ObservableList var12 = this.getChildren();
         int var13 = 0;

         for (int var14 = var12.size(); var13 < var14; var13++) {
            Node var15 = (Node)var12.get(var13);
            if (var15.isManaged()) {
               FlowPane.LayoutRect var16 = new FlowPane.LayoutRect();
               var16.node = var15;
               Insets var17 = getMargin(var15);
               var16.width = this.computeChildPrefAreaWidth(var15, var17);
               var16.height = this.computeChildPrefAreaHeight(var15, var17);
               double var18 = this.getOrientation() == Orientation.HORIZONTAL ? var16.width : var16.height;
               if (var3 + var18 > var1 && var3 > 0.0) {
                  this.normalizeRun(var7, var5);
                  if (this.getOrientation() == Orientation.HORIZONTAL) {
                     var5 += var7.height + var8;
                  } else {
                     var5 += var7.width + var10;
                  }

                  this.runs.add(var7);
                  var3 = 0.0;
                  var7 = new FlowPane.Run();
               }

               if (this.getOrientation() == Orientation.HORIZONTAL) {
                  var16.x = var3;
                  var3 += var16.width + var10;
               } else {
                  var16.y = var3;
                  var3 += var16.height + var8;
               }

               var7.rects.add(var16);
            }
         }

         this.normalizeRun(var7, var5);
         this.runs.add(var7);
         this.computingRuns = false;
      }

      return this.runs;
   }

   private void normalizeRun(FlowPane.Run var1, double var2) {
      if (this.getOrientation() == Orientation.HORIZONTAL) {
         ArrayList var4 = new ArrayList();
         var1.width = (double)(var1.rects.size() - 1) * this.snapSpaceX(this.getHgap());
         int var5 = 0;

         for (int var6 = var1.rects.size(); var5 < var6; var5++) {
            FlowPane.LayoutRect var7 = var1.rects.get(var5);
            var4.add(var7.node);
            var1.width = var1.width + var7.width;
            var7.y = var2;
         }

         var1.height = this.computeMaxPrefAreaHeight(var4, marginAccessor, this.getRowValignment());
         var1.baselineOffset = this.getRowValignment() == VPos.BASELINE
            ? this.getAreaBaselineOffset(var4, marginAccessor, var1x -> var1.rects.get(var1x).width, var1.height, true)
            : 0.0;
      } else {
         var1.height = (double)(var1.rects.size() - 1) * this.snapSpaceY(this.getVgap());
         double var9 = 0.0;
         int var10 = 0;

         for (int var11 = var1.rects.size(); var10 < var11; var10++) {
            FlowPane.LayoutRect var8 = var1.rects.get(var10);
            var1.height = var1.height + var8.height;
            var8.x = var2;
            var9 = Math.max(var9, var8.width);
         }

         var1.width = var9;
         var1.baselineOffset = var1.height;
      }
   }

   private double computeContentWidth(List<FlowPane.Run> var1) {
      double var2 = this.getOrientation() == Orientation.HORIZONTAL ? 0.0 : (double)(var1.size() - 1) * this.snapSpaceX(this.getHgap());
      int var4 = 0;

      for (int var5 = var1.size(); var4 < var5; var4++) {
         FlowPane.Run var6 = (FlowPane.Run)var1.get(var4);
         if (this.getOrientation() == Orientation.HORIZONTAL) {
            var2 = Math.max(var2, var6.width);
         } else {
            var2 += var6.width;
         }
      }

      return var2;
   }

   private double computeContentHeight(List<FlowPane.Run> var1) {
      double var2 = this.getOrientation() == Orientation.VERTICAL ? 0.0 : (double)(var1.size() - 1) * this.snapSpaceY(this.getVgap());
      int var4 = 0;

      for (int var5 = var1.size(); var4 < var5; var4++) {
         FlowPane.Run var6 = (FlowPane.Run)var1.get(var4);
         if (this.getOrientation() == Orientation.VERTICAL) {
            var2 = Math.max(var2, var6.height);
         } else {
            var2 += var6.height;
         }
      }

      return var2;
   }

   @Override
   protected void layoutChildren() {
      Insets var1 = this.getInsets();
      double var2 = this.getWidth();
      double var4 = this.getHeight();
      double var6 = var1.getTop();
      double var8 = var1.getLeft();
      double var10 = var1.getBottom();
      double var12 = var1.getRight();
      double var14 = var2 - var8 - var12;
      double var16 = var4 - var6 - var10;
      List var18 = this.getRuns(this.getOrientation() == Orientation.HORIZONTAL ? var14 : var16);
      int var19 = 0;

      for (int var20 = var18.size(); var19 < var20; var19++) {
         FlowPane.Run var21 = (FlowPane.Run)var18.get(var19);
         double var22 = var8
            + computeXOffset(
               var14, this.getOrientation() == Orientation.HORIZONTAL ? var21.width : this.computeContentWidth(var18), this.getAlignmentInternal().getHpos()
            );
         double var24 = var6
            + computeYOffset(
               var16, this.getOrientation() == Orientation.VERTICAL ? var21.height : this.computeContentHeight(var18), this.getAlignmentInternal().getVpos()
            );

         for (int var26 = 0; var26 < var21.rects.size(); var26++) {
            FlowPane.LayoutRect var27 = var21.rects.get(var26);
            double var28 = var22 + var27.x;
            double var30 = var24 + var27.y;
            this.layoutInArea(
               var27.node,
               var28,
               var30,
               this.getOrientation() == Orientation.HORIZONTAL ? var27.width : var21.width,
               this.getOrientation() == Orientation.VERTICAL ? var27.height : var21.height,
               var21.baselineOffset,
               getMargin(var27.node),
               this.getColumnHalignmentInternal(),
               this.getRowValignmentInternal()
            );
         }
      }
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return FlowPane.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   private static class LayoutRect {
      public Node node;
      double x;
      double y;
      double width;
      double height;

      @Override
      public String toString() {
         return "LayoutRect node id=" + this.node.getId() + " " + this.x + "," + this.y + " " + this.width + "x" + this.height;
      }
   }

   private static class Run {
      ArrayList<FlowPane.LayoutRect> rects = new ArrayList<>();
      double width;
      double height;
      double baselineOffset;
   }

   private static class StyleableProperties {
      private static final CssMetaData<FlowPane, Pos> ALIGNMENT = new CssMetaData<FlowPane, Pos>("-fx-alignment", new EnumConverter<>(Pos.class), Pos.TOP_LEFT) {
         public boolean isSettable(FlowPane var1) {
            return var1.alignment == null || !var1.alignment.isBound();
         }

         public StyleableProperty<Pos> getStyleableProperty(FlowPane var1) {
            return (StyleableProperty<Pos>)var1.alignmentProperty();
         }
      };
      private static final CssMetaData<FlowPane, HPos> COLUMN_HALIGNMENT = new CssMetaData<FlowPane, HPos>(
         "-fx-column-halignment", new EnumConverter<>(HPos.class), HPos.LEFT
      ) {
         public boolean isSettable(FlowPane var1) {
            return var1.columnHalignment == null || !var1.columnHalignment.isBound();
         }

         public StyleableProperty<HPos> getStyleableProperty(FlowPane var1) {
            return (StyleableProperty<HPos>)var1.columnHalignmentProperty();
         }
      };
      private static final CssMetaData<FlowPane, Number> HGAP = new CssMetaData<FlowPane, Number>("-fx-hgap", SizeConverter.getInstance(), 0.0) {
         public boolean isSettable(FlowPane var1) {
            return var1.hgap == null || !var1.hgap.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(FlowPane var1) {
            return (StyleableProperty<Number>)var1.hgapProperty();
         }
      };
      private static final CssMetaData<FlowPane, VPos> ROW_VALIGNMENT = new CssMetaData<FlowPane, VPos>(
         "-fx-row-valignment", new EnumConverter<>(VPos.class), VPos.CENTER
      ) {
         public boolean isSettable(FlowPane var1) {
            return var1.rowValignment == null || !var1.rowValignment.isBound();
         }

         public StyleableProperty<VPos> getStyleableProperty(FlowPane var1) {
            return (StyleableProperty<VPos>)var1.rowValignmentProperty();
         }
      };
      private static final CssMetaData<FlowPane, Orientation> ORIENTATION = new CssMetaData<FlowPane, Orientation>(
         "-fx-orientation", new EnumConverter<>(Orientation.class), Orientation.HORIZONTAL
      ) {
         public Orientation getInitialValue(FlowPane var1) {
            return var1.getOrientation();
         }

         public boolean isSettable(FlowPane var1) {
            return var1.orientation == null || !var1.orientation.isBound();
         }

         public StyleableProperty<Orientation> getStyleableProperty(FlowPane var1) {
            return (StyleableProperty<Orientation>)var1.orientationProperty();
         }
      };
      private static final CssMetaData<FlowPane, Number> VGAP = new CssMetaData<FlowPane, Number>("-fx-vgap", SizeConverter.getInstance(), 0.0) {
         public boolean isSettable(FlowPane var1) {
            return var1.vgap == null || !var1.vgap.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(FlowPane var1) {
            return (StyleableProperty<Number>)var1.vgapProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Region.getClassCssMetaData());
         var0.add(ALIGNMENT);
         var0.add(COLUMN_HALIGNMENT);
         var0.add(HGAP);
         var0.add(ROW_VALIGNMENT);
         var0.add(ORIENTATION);
         var0.add(VGAP);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
