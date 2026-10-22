package javafx.scene.chart;

import com.sun.javafx.charts.ChartLayoutAnimator;
import com.sun.javafx.charts.Legend;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.control.skin.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public abstract class Chart extends Region {
   private static final int MIN_WIDTH_TO_LEAVE_FOR_CHART_CONTENT = 200;
   private static final int MIN_HEIGHT_TO_LEAVE_FOR_CHART_CONTENT = 150;
   private final Label titleLabel = new Label();
   private final Pane chartContent = new Pane() {
      protected void layoutChildren() {
         double var1 = this.snappedTopInset();
         double var3 = this.snappedLeftInset();
         double var5 = this.snappedBottomInset();
         double var7 = this.snappedRightInset();
         double var9 = this.getWidth();
         double var11 = this.getHeight();
         double var13 = this.snapSizeX(var9 - (var3 + var7));
         double var15 = this.snapSizeY(var11 - (var1 + var5));
         Chart.this.layoutChartChildren(this.snapPositionY(var1), this.snapPositionX(var3), var13, var15);
      }

      public boolean usesMirroring() {
         return Chart.this.useChartContentMirroring;
      }
   };
   boolean useChartContentMirroring = true;
   private final ChartLayoutAnimator animator = new ChartLayoutAnimator(this.chartContent);
   private StringProperty title = new StringPropertyBase() {
      protected void invalidated() {
         Chart.this.titleLabel.setText(this.get());
      }

      public Object getBean() {
         return Chart.this;
      }

      public String getName() {
         return "title";
      }
   };
   private ObjectProperty<Side> titleSide = new StyleableObjectProperty<Side>(Side.TOP) {
      protected void invalidated() {
         Chart.this.requestLayout();
      }

      public CssMetaData<Chart, Side> getCssMetaData() {
         return Chart.StyleableProperties.TITLE_SIDE;
      }

      public Object getBean() {
         return Chart.this;
      }

      public String getName() {
         return "titleSide";
      }
   };
   private final ObjectProperty<Node> legend = new ObjectPropertyBase<Node>() {
      private Node old = null;

      protected void invalidated() {
         Node var1 = (Node)this.get();
         if (this.old != null) {
            Chart.this.getChildren().remove(this.old);
         }

         if (var1 != null) {
            Chart.this.getChildren().add(var1);
            var1.setVisible(Chart.this.isLegendVisible());
         }

         this.old = var1;
      }

      public Object getBean() {
         return Chart.this;
      }

      public String getName() {
         return "legend";
      }
   };
   private final BooleanProperty legendVisible = new StyleableBooleanProperty(true) {
      protected void invalidated() {
         Chart.this.requestLayout();
      }

      public CssMetaData<Chart, Boolean> getCssMetaData() {
         return Chart.StyleableProperties.LEGEND_VISIBLE;
      }

      public Object getBean() {
         return Chart.this;
      }

      public String getName() {
         return "legendVisible";
      }
   };
   private ObjectProperty<Side> legendSide = new StyleableObjectProperty<Side>(Side.BOTTOM) {
      protected void invalidated() {
         Side var1 = (Side)this.get();
         Node var2 = Chart.this.getLegend();
         if (var2 instanceof Legend) {
            ((Legend)var2).setVertical(Side.LEFT.equals(var1) || Side.RIGHT.equals(var1));
         }

         Chart.this.requestLayout();
      }

      public CssMetaData<Chart, Side> getCssMetaData() {
         return Chart.StyleableProperties.LEGEND_SIDE;
      }

      public Object getBean() {
         return Chart.this;
      }

      public String getName() {
         return "legendSide";
      }
   };
   private BooleanProperty animated = new SimpleBooleanProperty(this, "animated", true);

   public final String getTitle() {
      return (String)this.title.get();
   }

   public final void setTitle(String var1) {
      this.title.set(var1);
   }

   public final StringProperty titleProperty() {
      return this.title;
   }

   public final Side getTitleSide() {
      return (Side)this.titleSide.get();
   }

   public final void setTitleSide(Side var1) {
      this.titleSide.set(var1);
   }

   public final ObjectProperty<Side> titleSideProperty() {
      return this.titleSide;
   }

   protected final Node getLegend() {
      return (Node)this.legend.getValue();
   }

   protected final void setLegend(Node var1) {
      this.legend.setValue(var1);
   }

   protected final ObjectProperty<Node> legendProperty() {
      return this.legend;
   }

   public final boolean isLegendVisible() {
      return this.legendVisible.getValue();
   }

   public final void setLegendVisible(boolean var1) {
      this.legendVisible.setValue(var1);
   }

   public final BooleanProperty legendVisibleProperty() {
      return this.legendVisible;
   }

   public final Side getLegendSide() {
      return (Side)this.legendSide.get();
   }

   public final void setLegendSide(Side var1) {
      this.legendSide.set(var1);
   }

   public final ObjectProperty<Side> legendSideProperty() {
      return this.legendSide;
   }

   public final boolean getAnimated() {
      return this.animated.get();
   }

   public final void setAnimated(boolean var1) {
      this.animated.set(var1);
   }

   public final BooleanProperty animatedProperty() {
      return this.animated;
   }

   protected ObservableList<Node> getChartChildren() {
      return this.chartContent.getChildren();
   }

   public Chart() {
      this.titleLabel.setAlignment(Pos.CENTER);
      this.titleLabel.focusTraversableProperty().bind(Platform.accessibilityActiveProperty());
      this.getChildren().addAll(new Node[]{this.titleLabel, this.chartContent});
      this.getStyleClass().add("chart");
      this.titleLabel.getStyleClass().add("chart-title");
      this.chartContent.getStyleClass().add("chart-content");
      this.chartContent.setManaged(false);
   }

   void animate(KeyFrame... var1) {
      this.animator.animate(var1);
   }

   protected void animate(Animation var1) {
      this.animator.animate(var1);
   }

   protected void requestChartLayout() {
      this.chartContent.requestLayout();
   }

   protected final boolean shouldAnimate() {
      return this.getAnimated() && NodeHelper.isTreeShowing(this);
   }

   protected abstract void layoutChartChildren(double var1, double var3, double var5, double var7);

   protected void layoutChildren() {
      double var1 = this.snappedTopInset();
      double var3 = this.snappedLeftInset();
      double var5 = this.snappedBottomInset();
      double var7 = this.snappedRightInset();
      double var9 = this.getWidth();
      double var11 = this.getHeight();
      if (this.getTitle() != null) {
         this.titleLabel.setVisible(true);
         if (this.getTitleSide().equals(Side.TOP)) {
            double var13 = this.snapSizeY(this.titleLabel.prefHeight(var9 - var3 - var7));
            this.titleLabel.resizeRelocate(var3, var1, var9 - var3 - var7, var13);
            var1 += var13;
         } else if (this.getTitleSide().equals(Side.BOTTOM)) {
            double var19 = this.snapSizeY(this.titleLabel.prefHeight(var9 - var3 - var7));
            this.titleLabel.resizeRelocate(var3, var11 - var5 - var19, var9 - var3 - var7, var19);
            var5 += var19;
         } else if (this.getTitleSide().equals(Side.LEFT)) {
            double var20 = this.snapSizeX(this.titleLabel.prefWidth(var11 - var1 - var5));
            this.titleLabel.resizeRelocate(var3, var1, var20, var11 - var1 - var5);
            var3 += var20;
         } else if (this.getTitleSide().equals(Side.RIGHT)) {
            double var21 = this.snapSizeX(this.titleLabel.prefWidth(var11 - var1 - var5));
            this.titleLabel.resizeRelocate(var9 - var7 - var21, var1, var21, var11 - var1 - var5);
            var7 += var21;
         }
      } else {
         this.titleLabel.setVisible(false);
      }

      Node var22 = this.getLegend();
      if (var22 != null) {
         boolean var14 = this.isLegendVisible();
         if (var14) {
            if (this.getLegendSide() == Side.TOP) {
               double var15 = this.snapSizeY(var22.prefHeight(var9 - var3 - var7));
               double var17 = Utils.boundedSize(this.snapSizeX(var22.prefWidth(var15)), 0.0, var9 - var3 - var7);
               var22.resizeRelocate(var3 + (var9 - var3 - var7 - var17) / 2.0, var1, var17, var15);
               if (var11 - var5 - var1 - var15 < 150.0) {
                  var14 = false;
               } else {
                  var1 += var15;
               }
            } else if (this.getLegendSide() == Side.BOTTOM) {
               double var23 = this.snapSizeY(var22.prefHeight(var9 - var3 - var7));
               double var26 = Utils.boundedSize(this.snapSizeX(var22.prefWidth(var23)), 0.0, var9 - var3 - var7);
               var22.resizeRelocate(var3 + (var9 - var3 - var7 - var26) / 2.0, var11 - var5 - var23, var26, var23);
               if (var11 - var5 - var1 - var23 < 150.0) {
                  var14 = false;
               } else {
                  var5 += var23;
               }
            } else if (this.getLegendSide() == Side.LEFT) {
               double var24 = this.snapSizeX(var22.prefWidth(var11 - var1 - var5));
               double var27 = Utils.boundedSize(this.snapSizeY(var22.prefHeight(var24)), 0.0, var11 - var1 - var5);
               var22.resizeRelocate(var3, var1 + (var11 - var1 - var5 - var27) / 2.0, var24, var27);
               if (var9 - var3 - var7 - var24 < 200.0) {
                  var14 = false;
               } else {
                  var3 += var24;
               }
            } else if (this.getLegendSide() == Side.RIGHT) {
               double var25 = this.snapSizeX(var22.prefWidth(var11 - var1 - var5));
               double var28 = Utils.boundedSize(this.snapSizeY(var22.prefHeight(var25)), 0.0, var11 - var1 - var5);
               var22.resizeRelocate(var9 - var7 - var25, var1 + (var11 - var1 - var5 - var28) / 2.0, var25, var28);
               if (var9 - var3 - var7 - var25 < 200.0) {
                  var14 = false;
               } else {
                  var7 += var25;
               }
            }
         }

         var22.setVisible(var14);
      }

      this.chartContent.resizeRelocate(var3, var1, var9 - var3 - var7, var11 - var1 - var5);
   }

   protected double computeMinHeight(double var1) {
      return 150.0;
   }

   protected double computeMinWidth(double var1) {
      return 200.0;
   }

   protected double computePrefWidth(double var1) {
      return 500.0;
   }

   protected double computePrefHeight(double var1) {
      return 400.0;
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return Chart.StyleableProperties.STYLEABLES;
   }

   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   private static class StyleableProperties {
      private static final CssMetaData<Chart, Side> TITLE_SIDE = new CssMetaData<Chart, Side>("-fx-title-side", new EnumConverter(Side.class), Side.TOP) {
         public boolean isSettable(Chart var1) {
            return var1.titleSide == null || !var1.titleSide.isBound();
         }

         public StyleableProperty<Side> getStyleableProperty(Chart var1) {
            return (StyleableProperty<Side>)var1.titleSideProperty();
         }
      };
      private static final CssMetaData<Chart, Side> LEGEND_SIDE = new CssMetaData<Chart, Side>("-fx-legend-side", new EnumConverter(Side.class), Side.BOTTOM) {
         public boolean isSettable(Chart var1) {
            return var1.legendSide == null || !var1.legendSide.isBound();
         }

         public StyleableProperty<Side> getStyleableProperty(Chart var1) {
            return (StyleableProperty<Side>)var1.legendSideProperty();
         }
      };
      private static final CssMetaData<Chart, Boolean> LEGEND_VISIBLE = new CssMetaData<Chart, Boolean>(
         "-fx-legend-visible", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(Chart var1) {
            return var1.legendVisible == null || !var1.legendVisible.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(Chart var1) {
            return (StyleableProperty<Boolean>)var1.legendVisibleProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList(Region.getClassCssMetaData());
         var0.add(TITLE_SIDE);
         var0.add(LEGEND_VISIBLE);
         var0.add(LEGEND_SIDE);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
