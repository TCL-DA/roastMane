package javafx.scene.chart;

import com.sun.javafx.charts.Legend;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
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
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Duration;

public class AreaChart<X, Y> extends XYChart<X, Y> {
   private Map<XYChart.Series<X, Y>, DoubleProperty> seriesYMultiplierMap = new HashMap<>();
   private BooleanProperty createSymbols = new StyleableBooleanProperty(true) {
      protected void invalidated() {
         for (int var1 = 0; var1 < AreaChart.this.getData().size(); var1++) {
            XYChart.Series var2 = (XYChart.Series)AreaChart.this.getData().get(var1);

            for (int var3 = 0; var3 < var2.getData().size(); var3++) {
               XYChart.Data var4 = (XYChart.Data)var2.getData().get(var3);
               Node var5 = var4.getNode();
               if (this.get() && var5 == null) {
                  var5 = AreaChart.this.createSymbol(var2, AreaChart.this.getData().indexOf(var2), var4, var3);
                  if (null != var5) {
                     AreaChart.this.getPlotChildren().add(var5);
                  }
               } else if (!this.get() && var5 != null) {
                  AreaChart.this.getPlotChildren().remove(var5);
                  var5 = null;
                  var4.setNode(null);
               }
            }
         }

         AreaChart.this.requestChartLayout();
      }

      public Object getBean() {
         return this;
      }

      public String getName() {
         return "createSymbols";
      }

      public CssMetaData<AreaChart<?, ?>, Boolean> getCssMetaData() {
         return AreaChart.StyleableProperties.CREATE_SYMBOLS;
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

   public AreaChart(@NamedArg("xAxis") Axis<X> var1, @NamedArg("yAxis") Axis<Y> var2) {
      this(var1, var2, FXCollections.observableArrayList());
   }

   public AreaChart(@NamedArg("xAxis") Axis<X> var1, @NamedArg("yAxis") Axis<Y> var2, @NamedArg("data") ObservableList<XYChart.Series<X, Y>> var3) {
      super(var1, var2);
      this.setData(var3);
   }

   private static double doubleValue(Number var0) {
      return doubleValue(var0, 0.0);
   }

   private static double doubleValue(Number var0, double var1) {
      return var0 == null ? var1 : var0.doubleValue();
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
         boolean var5 = false;
         if (var2 > 0 && var2 < var1.getData().size() - 1) {
            var5 = true;
            XYChart.Data var22 = (XYChart.Data)var1.getData().get(var2 - 1);
            XYChart.Data var7 = (XYChart.Data)var1.getData().get(var2 + 1);
            double var8 = this.getXAxis().toNumericValue((X)var22.getXValue());
            double var10 = this.getYAxis().toNumericValue((Y)var22.getYValue());
            double var12 = this.getXAxis().toNumericValue((X)var7.getXValue());
            double var14 = this.getYAxis().toNumericValue((Y)var7.getYValue());
            double var16 = this.getXAxis().toNumericValue((X)var3.getXValue());
            double var18 = this.getYAxis().toNumericValue((Y)var3.getYValue());
            double var20 = (var14 - var10) / (var12 - var8) * var16 + (var12 * var10 - var14 * var8) / (var12 - var8);
            var3.setCurrentY(this.getYAxis().toRealValue(var20));
            var3.setCurrentX(this.getXAxis().toRealValue(var16));
         } else if (var2 == 0 && var1.getData().size() > 1) {
            var5 = true;
            var3.setCurrentX(((XYChart.Data)var1.getData().get(1)).getXValue());
            var3.setCurrentY(((XYChart.Data)var1.getData().get(1)).getYValue());
         } else if (var2 == var1.getData().size() - 1 && var1.getData().size() > 1) {
            var5 = true;
            int var6 = var1.getData().size() - 2;
            var3.setCurrentX(((XYChart.Data)var1.getData().get(var6)).getXValue());
            var3.setCurrentY(((XYChart.Data)var1.getData().get(var6)).getYValue());
         }

         if (var4 != null) {
            var4.setOpacity(0.0);
            this.getPlotChildren().add(var4);
            FadeTransition var23 = new FadeTransition(Duration.millis(500.0), var4);
            var23.setToValue(1.0);
            var23.play();
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
                     Duration.millis(800.0),
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
         boolean var5 = false;
         int var6 = var2.getDataSize();
         int var7 = var2.getData().size();
         if (var4 > 0 && var4 < var6 - 1) {
            var5 = true;
            XYChart.Data var25 = var2.getItem(var4 - 1);
            XYChart.Data var9 = var2.getItem(var4 + 1);
            double var10 = this.getXAxis().toNumericValue((X)var25.getXValue());
            double var12 = this.getYAxis().toNumericValue((Y)var25.getYValue());
            double var14 = this.getXAxis().toNumericValue((X)var9.getXValue());
            double var16 = this.getYAxis().toNumericValue((Y)var9.getYValue());
            double var18 = this.getXAxis().toNumericValue((X)var1.getXValue());
            double var20 = this.getYAxis().toNumericValue((Y)var1.getYValue());
            double var22 = (var16 - var12) / (var14 - var10) * var18 + (var14 * var12 - var16 * var10) / (var14 - var10);
            var1.setCurrentX(this.getXAxis().toRealValue(var18));
            var1.setCurrentY(this.getYAxis().toRealValue(var20));
            var1.setXValue(this.getXAxis().toRealValue(var18));
            var1.setYValue(this.getYAxis().toRealValue(var22));
         } else if (var4 == 0 && var7 > 1) {
            var5 = true;
            var1.setXValue(((XYChart.Data)var2.getData().get(0)).getXValue());
            var1.setYValue(((XYChart.Data)var2.getData().get(0)).getYValue());
         } else if (var4 == var6 - 1 && var7 > 1) {
            var5 = true;
            int var24 = var7 - 1;
            var1.setXValue(((XYChart.Data)var2.getData().get(var24)).getXValue());
            var1.setYValue(((XYChart.Data)var2.getData().get(var24)).getYValue());
         } else if (var3 != null) {
            var3.setOpacity(0.0);
            FadeTransition var8 = new FadeTransition(Duration.millis(500.0), var3);
            var8.setToValue(0.0);
            var8.setOnFinished(var4x -> {
               this.getPlotChildren().remove(var3);
               this.removeDataItemFromDisplay(var2, var1);
            });
            var8.play();
         } else {
            var1.setSeries(null);
            this.removeDataItemFromDisplay(var2, var1);
         }

         if (var5) {
            this.animate(
               new KeyFrame[]{
                  new KeyFrame(
                     Duration.ZERO,
                     new KeyValue[]{new KeyValue(var1.currentYProperty(), var1.getCurrentY()), new KeyValue(var1.currentXProperty(), var1.getCurrentX())}
                  ),
                  new KeyFrame(
                     Duration.millis(800.0),
                     var4x -> {
                        var1.setSeries(null);
                        this.getPlotChildren().remove(var3);
                        this.removeDataItemFromDisplay(var2, var1);
                     },
                     new KeyValue[]{
                        new KeyValue(var1.currentYProperty(), var1.getYValue(), Interpolator.EASE_BOTH),
                        new KeyValue(var1.currentXProperty(), var1.getXValue(), Interpolator.EASE_BOTH)
                     }
                  )
               }
            );
         }
      } else {
         var1.setSeries(null);
         this.getPlotChildren().remove(var3);
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
         Path var4 = (Path)((Group)var3.getNode()).getChildren().get(1);
         Path var5 = (Path)((Group)var3.getNode()).getChildren().get(0);
         var4.getStyleClass().setAll(new String[]{"chart-series-area-line", "series" + var2, var3.defaultColorStyleClass});
         var5.getStyleClass().setAll(new String[]{"chart-series-area-fill", "series" + var2, var3.defaultColorStyleClass});

         for (int var6 = 0; var6 < var3.getData().size(); var6++) {
            XYChart.Data var7 = (XYChart.Data)var3.getData().get(var6);
            Node var8 = var7.getNode();
            if (var8 != null) {
               var8.getStyleClass().setAll(new String[]{"chart-area-symbol", "series" + var2, "data" + var6, var3.defaultColorStyleClass});
            }
         }
      }
   }

   @Override
   protected void seriesAdded(XYChart.Series<X, Y> var1, int var2) {
      Path var3 = new Path();
      Path var4 = new Path();
      var3.setStrokeLineJoin(StrokeLineJoin.BEVEL);
      Group var5 = new Group(new Node[]{var4, var3});
      var1.setNode(var5);
      SimpleDoubleProperty var6 = new SimpleDoubleProperty(this, "seriesYMultiplier");
      this.seriesYMultiplierMap.put(var1, var6);
      if (this.shouldAnimate()) {
         var6.setValue(0.0);
      } else {
         var6.setValue(1.0);
      }

      this.getPlotChildren().add(var5);
      ArrayList var7 = new ArrayList();
      if (this.shouldAnimate()) {
         var7.add(new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(var5.opacityProperty(), 0), new KeyValue(var6, 0)}));
         var7.add(new KeyFrame(Duration.millis(200.0), new KeyValue[]{new KeyValue(var5.opacityProperty(), 1)}));
         var7.add(new KeyFrame(Duration.millis(500.0), new KeyValue[]{new KeyValue(var6, 1)}));
      }

      for (int var8 = 0; var8 < var1.getData().size(); var8++) {
         XYChart.Data var9 = (XYChart.Data)var1.getData().get(var8);
         Node var10 = this.createSymbol(var1, var2, var9, var8);
         if (var10 != null) {
            if (this.shouldAnimate()) {
               var10.setOpacity(0.0);
               this.getPlotChildren().add(var10);
               var7.add(new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(var10.opacityProperty(), 0)}));
               var7.add(new KeyFrame(Duration.millis(200.0), new KeyValue[]{new KeyValue(var10.opacityProperty(), 1)}));
            } else {
               this.getPlotChildren().add(var10);
            }
         }
      }

      if (this.shouldAnimate()) {
         this.animate(var7.toArray(new KeyFrame[var7.size()]));
      }
   }

   @Override
   protected void seriesRemoved(XYChart.Series<X, Y> var1) {
      this.seriesYMultiplierMap.remove(var1);
      if (this.shouldAnimate()) {
         Timeline var2 = new Timeline(this.createSeriesRemoveTimeLine(var1, 400L));
         var2.play();
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
         ObservableList var5 = ((Group)var3.getNode()).getChildren();
         Path var6 = (Path)var5.get(0);
         Path var7 = (Path)var5.get(1);
         makePaths(this, var3, var1, var6, var7, var4.get(), LineChart.SortingPolicy.X_AXIS);
      }
   }

   static <X, Y> void makePaths(
      XYChart<X, Y> var0, XYChart.Series<X, Y> var1, List<LineTo> var2, Path var3, Path var4, double var5, LineChart.SortingPolicy var7
   ) {
      Axis var8 = var0.getXAxis();
      Axis var9 = var0.getYAxis();
      double var10 = var4.getStrokeWidth() / 2.0;
      boolean var12 = var7 == LineChart.SortingPolicy.X_AXIS;
      boolean var13 = var7 == LineChart.SortingPolicy.Y_AXIS;
      double var14 = var12 ? -var10 : Double.NEGATIVE_INFINITY;
      double var16 = var12 ? var8.getWidth() + var10 : Double.POSITIVE_INFINITY;
      double var18 = var13 ? -var10 : Double.NEGATIVE_INFINITY;
      double var20 = var13 ? var9.getHeight() + var10 : Double.POSITIVE_INFINITY;
      LineTo var22 = null;
      LineTo var23 = null;
      var2.clear();
      Iterator var24 = var0.getDisplayedDataIterator(var1);

      while (var24.hasNext()) {
         XYChart.Data var25 = (XYChart.Data)var24.next();
         double var26 = var8.getDisplayPosition(var25.getCurrentX());
         double var28 = var9.getDisplayPosition(var9.toRealValue(var9.toNumericValue(var25.getCurrentY()) * var5));
         boolean var30 = Double.isNaN(var26) || Double.isNaN(var28);
         Node var31 = var25.getNode();
         if (var31 != null) {
            double var32 = var31.prefWidth(-1.0);
            double var34 = var31.prefHeight(-1.0);
            if (var30) {
               var31.resizeRelocate(-var32 * 2.0, -var34 * 2.0, var32, var34);
            } else {
               var31.resizeRelocate(var26 - var32 / 2.0, var28 - var34 / 2.0, var32, var34);
            }
         }

         if (!var30) {
            if (!(var26 < var14) && !(var28 < var18)) {
               if (var26 <= var16 && var28 <= var20) {
                  var2.add(new LineTo(var26, var28));
               } else if (var23 == null) {
                  var23 = new LineTo(var26, var28);
               } else if (var12 && var26 < var23.getX() || var13 && var28 < var23.getY()) {
                  var23.setX(var26);
                  var23.setY(var28);
               }
            } else if (var22 == null) {
               var22 = new LineTo(var26, var28);
            } else if (var12 && var22.getX() < var26 || var13 && var22.getY() < var28) {
               var22.setX(var26);
               var22.setY(var28);
            }
         }
      }

      if (!var2.isEmpty() || var22 != null || var23 != null) {
         if (var12) {
            Collections.sort(var2, (var0x, var1x) -> Double.compare(var0x.getX(), var1x.getX()));
         } else if (var13) {
            Collections.sort(var2, (var0x, var1x) -> Double.compare(var0x.getY(), var1x.getY()));
         }

         if (var22 != null) {
            var2.add(0, var22);
         }

         if (var23 != null) {
            var2.add(var23);
         }

         LineTo var36 = (LineTo)var2.get(0);
         LineTo var37 = (LineTo)var2.get(var2.size() - 1);
         double var38 = var36.getY();
         ObservableList var39 = var4.getElements();
         var39.clear();
         var39.add(new MoveTo(var36.getX(), var38));
         var39.addAll(var2);
         if (var3 != null) {
            ObservableList var29 = var3.getElements();
            var29.clear();
            double var40 = var9.getDisplayPosition(var9.toRealValue(0.0));
            var29.add(new MoveTo(var36.getX(), var40));
            var29.addAll(var2);
            var29.add(new LineTo(var37.getX(), var40));
            var29.add(new ClosePath());
         }
      }
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
         var5.getStyleClass().setAll(new String[]{"chart-area-symbol", "series" + var2, "data" + var4, var1.defaultColorStyleClass});
      }

      return (Node)var5;
   }

   @Override
   Legend.LegendItem createLegendItemForSeries(XYChart.Series<X, Y> var1, int var2) {
      Legend.LegendItem var3 = new Legend.LegendItem(var1.getName());
      var3.getSymbol().getStyleClass().addAll(new String[]{"chart-area-symbol", "series" + var2, "area-legend-symbol", var1.defaultColorStyleClass});
      return var3;
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return AreaChart.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   private static class StyleableProperties {
      private static final CssMetaData<AreaChart<?, ?>, Boolean> CREATE_SYMBOLS = new CssMetaData<AreaChart<?, ?>, Boolean>(
         "-fx-create-symbols", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(AreaChart<?, ?> var1) {
            return var1.createSymbols == null || !var1.createSymbols.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(AreaChart<?, ?> var1) {
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
