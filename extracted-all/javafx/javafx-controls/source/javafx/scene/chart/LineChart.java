package javafx.scene.chart;

import com.sun.javafx.charts.Legend;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Duration;

public class LineChart<X, Y> extends XYChart<X, Y> {
   private Map<XYChart.Series<X, Y>, DoubleProperty> seriesYMultiplierMap = new HashMap<>();
   private Timeline dataRemoveTimeline;
   private XYChart.Series<X, Y> seriesOfDataRemoved = null;
   private XYChart.Data<X, Y> dataItemBeingRemoved = null;
   private FadeTransition fadeSymbolTransition = null;
   private Map<XYChart.Data<X, Y>, Double> XYValueMap = new HashMap<>();
   private Timeline seriesRemoveTimeline = null;
   private BooleanProperty createSymbols = new StyleableBooleanProperty(true) {
      protected void invalidated() {
         for (int var1 = 0; var1 < LineChart.this.getData().size(); var1++) {
            XYChart.Series var2 = (XYChart.Series)LineChart.this.getData().get(var1);

            for (int var3 = 0; var3 < var2.getData().size(); var3++) {
               XYChart.Data var4 = (XYChart.Data)var2.getData().get(var3);
               Node var5 = var4.getNode();
               if (this.get() && var5 == null) {
                  var5 = LineChart.this.createSymbol(var2, LineChart.this.getData().indexOf(var2), var4, var3);
                  LineChart.this.getPlotChildren().add(var5);
               } else if (!this.get() && var5 != null) {
                  LineChart.this.getPlotChildren().remove(var5);
                  var5 = null;
                  var4.setNode(null);
               }
            }
         }

         LineChart.this.requestChartLayout();
      }

      public Object getBean() {
         return LineChart.this;
      }

      public String getName() {
         return "createSymbols";
      }

      public CssMetaData<LineChart<?, ?>, Boolean> getCssMetaData() {
         return LineChart.StyleableProperties.CREATE_SYMBOLS;
      }
   };
   private ObjectProperty<LineChart.SortingPolicy> axisSortingPolicy = new ObjectPropertyBase<LineChart.SortingPolicy>(LineChart.SortingPolicy.X_AXIS) {
      protected void invalidated() {
         LineChart.this.requestChartLayout();
      }

      public Object getBean() {
         return LineChart.this;
      }

      public String getName() {
         return "axisSortingPolicy";
      }
   };

   public final boolean getCreateSymbols() {
      return this.createSymbols.getValue();
   }

   public final void setCreateSymbols(boolean var1) {
      this.createSymbols.setValue(var1);
   }

   public final BooleanProperty createSymbolsProperty() {
      return this.createSymbols;
   }

   public final LineChart.SortingPolicy getAxisSortingPolicy() {
      return (LineChart.SortingPolicy)this.axisSortingPolicy.getValue();
   }

   public final void setAxisSortingPolicy(LineChart.SortingPolicy var1) {
      this.axisSortingPolicy.setValue(var1);
   }

   public final ObjectProperty<LineChart.SortingPolicy> axisSortingPolicyProperty() {
      return this.axisSortingPolicy;
   }

   public LineChart(@NamedArg("xAxis") Axis<X> var1, @NamedArg("yAxis") Axis<Y> var2) {
      this(var1, var2, FXCollections.observableArrayList());
   }

   public LineChart(@NamedArg("xAxis") Axis<X> var1, @NamedArg("yAxis") Axis<Y> var2, @NamedArg("data") ObservableList<XYChart.Series<X, Y>> var3) {
      super(var1, var2);
      this.setData(var3);
   }

   @Override
   protected void updateAxisRange() {
      Axis var1 = this.getXAxis();
      Axis var2 = this.getYAxis();
      ArrayList var3 = null;
      ArrayList var4 = null;
      if (var1.isAutoRanging()) {
         var3 = new ArrayList();
      }

      if (var2.isAutoRanging()) {
         var4 = new ArrayList();
      }

      if (var3 != null || var4 != null) {
         for (XYChart.Series var6 : this.getData()) {
            for (XYChart.Data var8 : var6.getData()) {
               if (var3 != null) {
                  var3.add(var8.getXValue());
               }

               if (var4 != null) {
                  var4.add(var8.getYValue());
               }
            }
         }

         if (var3 != null && (var3.size() != 1 || this.getXAxis().toNumericValue((X)var3.get(0)) != 0.0)) {
            var1.invalidateRange(var3);
         }

         if (var4 != null && (var4.size() != 1 || this.getYAxis().toNumericValue((Y)var4.get(0)) != 0.0)) {
            var2.invalidateRange(var4);
         }
      }
   }

   @Override
   protected void dataItemAdded(XYChart.Series<X, Y> var1, int var2, XYChart.Data<X, Y> var3) {
      Node var4 = this.createSymbol(var1, this.getData().indexOf(var1), var3, var2);
      if (this.shouldAnimate()) {
         if (this.dataRemoveTimeline != null && this.dataRemoveTimeline.getStatus().equals(Status.RUNNING) && this.seriesOfDataRemoved == var1) {
            this.dataRemoveTimeline.stop();
            this.dataRemoveTimeline = null;
            this.getPlotChildren().remove(this.dataItemBeingRemoved.getNode());
            this.removeDataItemFromDisplay(this.seriesOfDataRemoved, this.dataItemBeingRemoved);
            this.seriesOfDataRemoved = null;
            this.dataItemBeingRemoved = null;
         }

         boolean var5 = false;
         if (var2 > 0 && var2 < var1.getData().size() - 1) {
            var5 = true;
            XYChart.Data var23 = (XYChart.Data)var1.getData().get(var2 - 1);
            XYChart.Data var7 = (XYChart.Data)var1.getData().get(var2 + 1);
            if (var23 != null && var7 != null) {
               double var8 = this.getXAxis().toNumericValue((X)var23.getXValue());
               double var10 = this.getYAxis().toNumericValue((Y)var23.getYValue());
               double var12 = this.getXAxis().toNumericValue((X)var7.getXValue());
               double var14 = this.getYAxis().toNumericValue((Y)var7.getYValue());
               double var16 = this.getXAxis().toNumericValue((X)var3.getXValue());
               if (var16 > var8 && var16 < var12) {
                  double var24 = (var14 - var10) / (var12 - var8) * var16 + (var12 * var10 - var14 * var8) / (var12 - var8);
                  var3.setCurrentY(this.getYAxis().toRealValue(var24));
                  var3.setCurrentX(this.getXAxis().toRealValue(var16));
               } else {
                  double var18 = (var12 + var8) / 2.0;
                  double var20 = (var14 + var10) / 2.0;
                  var3.setCurrentX(this.getXAxis().toRealValue(var18));
                  var3.setCurrentY(this.getYAxis().toRealValue(var20));
               }
            }
         } else if (var2 == 0 && var1.getData().size() > 1) {
            var5 = true;
            var3.setCurrentX(((XYChart.Data)var1.getData().get(1)).getXValue());
            var3.setCurrentY(((XYChart.Data)var1.getData().get(1)).getYValue());
         } else if (var2 == var1.getData().size() - 1 && var1.getData().size() > 1) {
            var5 = true;
            int var22 = var1.getData().size() - 2;
            var3.setCurrentX(((XYChart.Data)var1.getData().get(var22)).getXValue());
            var3.setCurrentY(((XYChart.Data)var1.getData().get(var22)).getYValue());
         } else if (var4 != null) {
            var4.setOpacity(0.0);
            this.getPlotChildren().add(var4);
            FadeTransition var6 = new FadeTransition(Duration.millis(500.0), var4);
            var6.setToValue(1.0);
            var6.play();
         }

         if (var5) {
            this.animate(
               new KeyFrame[]{
                  new KeyFrame(Duration.ZERO, var2x -> {
                     if (var4 != null && !this.getPlotChildren().contains(var4)) {
                        this.getPlotChildren().add(var4);
                     }
                  }, new KeyValue[]{new KeyValue(var3.currentYProperty(), var3.getCurrentY()), new KeyValue(var3.currentXProperty(), var3.getCurrentX())}),
                  new KeyFrame(
                     Duration.millis(700.0),
                     new KeyValue[]{
                        new KeyValue(var3.currentYProperty(), var3.getYValue(), Interpolator.EASE_BOTH),
                        new KeyValue(var3.currentXProperty(), var3.getXValue(), Interpolator.EASE_BOTH)
                     }
                  )
               }
            );
         }
      } else if (var4 != null) {
         this.getPlotChildren().add(var4);
      }
   }

   @Override
   protected void dataItemRemoved(XYChart.Data<X, Y> var1, XYChart.Series<X, Y> var2) {
      Node var3 = var1.getNode();
      if (var3 != null) {
         var3.focusTraversableProperty().unbind();
      }

      int var4 = var2.getItemIndex(var1);
      if (this.shouldAnimate()) {
         this.XYValueMap.clear();
         boolean var5 = false;
         int var6 = var2.getDataSize();
         int var7 = var2.getData().size();
         if (var4 > 0 && var4 < var6 - 1) {
            var5 = true;
            XYChart.Data var26 = var2.getItem(var4 - 1);
            XYChart.Data var9 = var2.getItem(var4 + 1);
            double var10 = this.getXAxis().toNumericValue((X)var26.getXValue());
            double var12 = this.getYAxis().toNumericValue((Y)var26.getYValue());
            double var14 = this.getXAxis().toNumericValue((X)var9.getXValue());
            double var16 = this.getYAxis().toNumericValue((Y)var9.getYValue());
            double var18 = this.getXAxis().toNumericValue((X)var1.getXValue());
            double var20 = this.getYAxis().toNumericValue((Y)var1.getYValue());
            if (var18 > var10 && var18 < var14) {
               double var27 = (var16 - var12) / (var14 - var10) * var18 + (var14 * var12 - var16 * var10) / (var14 - var10);
               var1.setCurrentX(this.getXAxis().toRealValue(var18));
               var1.setCurrentY(this.getYAxis().toRealValue(var20));
               var1.setXValue(this.getXAxis().toRealValue(var18));
               var1.setYValue(this.getYAxis().toRealValue(var27));
            } else {
               double var22 = (var14 + var10) / 2.0;
               double var24 = (var16 + var12) / 2.0;
               var1.setCurrentX(this.getXAxis().toRealValue(var22));
               var1.setCurrentY(this.getYAxis().toRealValue(var24));
            }
         } else if (var4 == 0 && var7 > 1) {
            var5 = true;
            var1.setXValue(((XYChart.Data)var2.getData().get(0)).getXValue());
            var1.setYValue(((XYChart.Data)var2.getData().get(0)).getYValue());
         } else if (var4 == var6 - 1 && var7 > 1) {
            var5 = true;
            int var8 = var7 - 1;
            var1.setXValue(((XYChart.Data)var2.getData().get(var8)).getXValue());
            var1.setYValue(((XYChart.Data)var2.getData().get(var8)).getYValue());
         } else if (var3 != null) {
            this.fadeSymbolTransition = new FadeTransition(Duration.millis(500.0), var3);
            this.fadeSymbolTransition.setToValue(0.0);
            this.fadeSymbolTransition.setOnFinished(var4x -> {
               var1.setSeries(null);
               this.getPlotChildren().remove(var3);
               this.removeDataItemFromDisplay(var2, var1);
               var3.setOpacity(1.0);
            });
            this.fadeSymbolTransition.play();
         } else {
            var1.setSeries(null);
            this.removeDataItemFromDisplay(var2, var1);
         }

         if (var5) {
            this.dataRemoveTimeline = this.createDataRemoveTimeline(var1, var3, var2);
            this.seriesOfDataRemoved = var2;
            this.dataItemBeingRemoved = var1;
            this.dataRemoveTimeline.play();
         }
      } else {
         var1.setSeries(null);
         if (var3 != null) {
            this.getPlotChildren().remove(var3);
         }

         this.removeDataItemFromDisplay(var2, var1);
      }
   }

   @Override
   protected void dataItemChanged(XYChart.Data<X, Y> var1) {
   }

   @Override
   protected void seriesChanged(Change<? extends XYChart.Series> var1) {
      for (int var2 = 0; var2 < this.getDataSize(); var2++) {
         XYChart.Series var3 = (XYChart.Series)this.getData().get(var2);
         Node var4 = var3.getNode();
         if (var4 != null) {
            var4.getStyleClass().setAll(new String[]{"chart-series-line", "series" + var2, var3.defaultColorStyleClass});
         }

         for (int var5 = 0; var5 < var3.getData().size(); var5++) {
            Node var6 = ((XYChart.Data)var3.getData().get(var5)).getNode();
            if (var6 != null) {
               var6.getStyleClass().setAll(new String[]{"chart-line-symbol", "series" + var2, "data" + var5, var3.defaultColorStyleClass});
            }
         }
      }
   }

   @Override
   protected void seriesAdded(XYChart.Series<X, Y> var1, int var2) {
      Path var3 = new Path();
      var3.setStrokeLineJoin(StrokeLineJoin.BEVEL);
      var1.setNode(var3);
      SimpleDoubleProperty var4 = new SimpleDoubleProperty(this, "seriesYMultiplier");
      this.seriesYMultiplierMap.put(var1, var4);
      if (this.shouldAnimate()) {
         var3.setOpacity(0.0);
         var4.setValue(0.0);
      } else {
         var4.setValue(1.0);
      }

      this.getPlotChildren().add(var3);
      ArrayList var5 = new ArrayList();
      if (this.shouldAnimate()) {
         var5.add(new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(var3.opacityProperty(), 0), new KeyValue(var4, 0)}));
         var5.add(new KeyFrame(Duration.millis(200.0), new KeyValue[]{new KeyValue(var3.opacityProperty(), 1)}));
         var5.add(new KeyFrame(Duration.millis(500.0), new KeyValue[]{new KeyValue(var4, 1)}));
      }

      for (int var6 = 0; var6 < var1.getData().size(); var6++) {
         XYChart.Data var7 = (XYChart.Data)var1.getData().get(var6);
         Node var8 = this.createSymbol(var1, var2, var7, var6);
         if (var8 != null) {
            if (this.shouldAnimate()) {
               var8.setOpacity(0.0);
            }

            this.getPlotChildren().add(var8);
            if (this.shouldAnimate()) {
               var5.add(new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(var8.opacityProperty(), 0)}));
               var5.add(new KeyFrame(Duration.millis(200.0), new KeyValue[]{new KeyValue(var8.opacityProperty(), 1)}));
            }
         }
      }

      if (this.shouldAnimate()) {
         this.animate(var5.toArray(new KeyFrame[var5.size()]));
      }
   }

   @Override
   protected void seriesRemoved(XYChart.Series<X, Y> var1) {
      this.seriesYMultiplierMap.remove(var1);
      if (this.shouldAnimate()) {
         this.seriesRemoveTimeline = new Timeline(this.createSeriesRemoveTimeLine(var1, 900L));
         this.seriesRemoveTimeline.play();
      } else {
         this.getPlotChildren().remove(var1.getNode());

         for (XYChart.Data var3 : var1.getData()) {
            this.getPlotChildren().remove(var3.getNode());
         }

         this.removeSeriesFromDisplay(var1);
      }
   }

   @Override
   protected void layoutPlotChildren() {
      ArrayList var1 = new ArrayList(this.getDataSize());

      for (int var2 = 0; var2 < this.getDataSize(); var2++) {
         XYChart.Series var3 = (XYChart.Series)this.getData().get(var2);
         DoubleProperty var4 = this.seriesYMultiplierMap.get(var3);
         Node var5 = var3.getNode();
         if (var5 instanceof Path) {
            AreaChart.makePaths(this, var3, var1, null, (Path)var5, var4.get(), this.getAxisSortingPolicy());
         }
      }
   }

   @Override
   void dataBeingRemovedIsAdded(XYChart.Data var1, XYChart.Series var2) {
      if (this.fadeSymbolTransition != null) {
         this.fadeSymbolTransition.setOnFinished(null);
         this.fadeSymbolTransition.stop();
      }

      if (this.dataRemoveTimeline != null) {
         this.dataRemoveTimeline.setOnFinished(null);
         this.dataRemoveTimeline.stop();
      }

      Node var3 = var1.getNode();
      if (var3 != null) {
         this.getPlotChildren().remove(var3);
      }

      var1.setSeries(null);
      this.removeDataItemFromDisplay(var2, var1);
      Double var4 = this.XYValueMap.get(var1);
      if (var4 != null) {
         var1.setYValue(var4);
         var1.setCurrentY(var4);
      }

      this.XYValueMap.clear();
   }

   @Override
   void seriesBeingRemovedIsAdded(XYChart.Series<X, Y> var1) {
      if (this.seriesRemoveTimeline != null) {
         this.seriesRemoveTimeline.setOnFinished(null);
         this.seriesRemoveTimeline.stop();
         this.getPlotChildren().remove(var1.getNode());

         for (XYChart.Data var3 : var1.getData()) {
            this.getPlotChildren().remove(var3.getNode());
         }

         this.removeSeriesFromDisplay(var1);
      }
   }

   private Timeline createDataRemoveTimeline(XYChart.Data<X, Y> var1, Node var2, XYChart.Series<X, Y> var3) {
      Timeline var4 = new Timeline();
      this.XYValueMap.put(var1, ((Number)var1.getYValue()).doubleValue());
      var4.getKeyFrames()
         .addAll(
            new KeyFrame[]{
               new KeyFrame(
                  Duration.ZERO,
                  new KeyValue[]{new KeyValue(var1.currentYProperty(), var1.getCurrentY()), new KeyValue(var1.currentXProperty(), var1.getCurrentX())}
               ),
               new KeyFrame(
                  Duration.millis(500.0),
                  var4x -> {
                     if (var2 != null) {
                        this.getPlotChildren().remove(var2);
                     }

                     this.removeDataItemFromDisplay(var3, var1);
                     this.XYValueMap.clear();
                  },
                  new KeyValue[]{
                     new KeyValue(var1.currentYProperty(), var1.getYValue(), Interpolator.EASE_BOTH),
                     new KeyValue(var1.currentXProperty(), var1.getXValue(), Interpolator.EASE_BOTH)
                  }
               )
            }
         );
      return var4;
   }

   private Node createSymbol(XYChart.Series<X, Y> var1, int var2, XYChart.Data<X, Y> var3, int var4) {
      Object var5 = var3.getNode();
      if (var5 == null && this.getCreateSymbols()) {
         var5 = new StackPane();
         var5.setAccessibleRole(AccessibleRole.TEXT);
         var5.setAccessibleRoleDescription("Point");
         var5.focusTraversableProperty().bind(Platform.accessibilityActiveProperty());
         var3.setNode((Node)var5);
      }

      if (var5 != null) {
         var5.getStyleClass().addAll(new String[]{"chart-line-symbol", "series" + var2, "data" + var4, var1.defaultColorStyleClass});
      }

      return (Node)var5;
   }

   @Override
   Legend.LegendItem createLegendItemForSeries(XYChart.Series<X, Y> var1, int var2) {
      Legend.LegendItem var3 = new Legend.LegendItem(var1.getName());
      var3.getSymbol().getStyleClass().addAll(new String[]{"chart-line-symbol", "series" + var2, var1.defaultColorStyleClass});
      return var3;
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return LineChart.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   public static enum SortingPolicy {
      NONE,
      X_AXIS,
      Y_AXIS;
   }

   private static class StyleableProperties {
      private static final CssMetaData<LineChart<?, ?>, Boolean> CREATE_SYMBOLS = new CssMetaData<LineChart<?, ?>, Boolean>(
         "-fx-create-symbols", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(LineChart<?, ?> var1) {
            return var1.createSymbols == null || !var1.createSymbols.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(LineChart<?, ?> var1) {
            return (StyleableProperty<Boolean>)var1.createSymbolsProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(XYChart.getClassCssMetaData());
         var0.add(CREATE_SYMBOLS);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
