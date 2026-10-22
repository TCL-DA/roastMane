package javafx.scene.chart;

import com.sun.javafx.charts.Legend;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
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

public class StackedAreaChart<X, Y> extends XYChart<X, Y> {
   private Map<XYChart.Series<X, Y>, DoubleProperty> seriesYMultiplierMap = new HashMap<>();
   private BooleanProperty createSymbols = new StyleableBooleanProperty(true) {
      protected void invalidated() {
         for (int var1 = 0; var1 < StackedAreaChart.this.getData().size(); var1++) {
            XYChart.Series var2 = (XYChart.Series)StackedAreaChart.this.getData().get(var1);

            for (int var3 = 0; var3 < var2.getData().size(); var3++) {
               XYChart.Data var4 = (XYChart.Data)var2.getData().get(var3);
               Node var5 = var4.getNode();
               if (this.get() && var5 == null) {
                  var5 = StackedAreaChart.this.createSymbol(var2, StackedAreaChart.this.getData().indexOf(var2), var4, var3);
                  if (null != var5) {
                     StackedAreaChart.this.getPlotChildren().add(var5);
                  }
               } else if (!this.get() && var5 != null) {
                  StackedAreaChart.this.getPlotChildren().remove(var5);
                  var5 = null;
                  var4.setNode(null);
               }
            }
         }

         StackedAreaChart.this.requestChartLayout();
      }

      public Object getBean() {
         return this;
      }

      public String getName() {
         return "createSymbols";
      }

      public CssMetaData<StackedAreaChart<?, ?>, Boolean> getCssMetaData() {
         return StackedAreaChart.StyleableProperties.CREATE_SYMBOLS;
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

   public StackedAreaChart(@NamedArg("xAxis") Axis<X> var1, @NamedArg("yAxis") Axis<Y> var2) {
      this(var1, var2, FXCollections.observableArrayList());
   }

   public StackedAreaChart(@NamedArg("xAxis") Axis<X> var1, @NamedArg("yAxis") Axis<Y> var2, @NamedArg("data") ObservableList<XYChart.Series<X, Y>> var3) {
      super(var1, var2);
      if (!(var2 instanceof ValueAxis)) {
         throw new IllegalArgumentException("Axis type incorrect, yAxis must be of ValueAxis type.");
      } else {
         this.setData(var3);
      }
   }

   private static double doubleValue(Number var0) {
      return doubleValue(var0, 0.0);
   }

   private static double doubleValue(Number var0, double var1) {
      return var0 == null ? var1 : var0.doubleValue();
   }

   @Override
   protected void dataItemAdded(XYChart.Series<X, Y> var1, int var2, XYChart.Data<X, Y> var3) {
      Node var4 = this.createSymbol(var1, this.getData().indexOf(var1), var3, var2);
      if (this.shouldAnimate()) {
         boolean var5 = false;
         if (var2 > 0 && var2 < var1.getData().size() - 1) {
            var5 = true;
            XYChart.Data var23 = (XYChart.Data)var1.getData().get(var2 - 1);
            XYChart.Data var7 = (XYChart.Data)var1.getData().get(var2 + 1);
            double var8 = this.getXAxis().toNumericValue((X)var23.getXValue());
            double var10 = this.getYAxis().toNumericValue((Y)var23.getYValue());
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
               var3.setOpacity(1.0);
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
      var4.setStrokeLineJoin(StrokeLineJoin.BEVEL);
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
            }

            this.getPlotChildren().add(var10);
            if (this.shouldAnimate()) {
               var7.add(new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(var10.opacityProperty(), 0)}));
               var7.add(new KeyFrame(Duration.millis(200.0), new KeyValue[]{new KeyValue(var10.opacityProperty(), 1)}));
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
   protected void updateAxisRange() {
      Axis var1 = this.getXAxis();
      Axis var2 = this.getYAxis();
      if (var1.isAutoRanging()) {
         ArrayList var3 = new ArrayList();

         for (XYChart.Series var5 : this.getData()) {
            for (XYChart.Data var7 : var5.getData()) {
               var3.add(var7.getXValue());
            }
         }

         var1.invalidateRange(var3);
      }

      if (var2.isAutoRanging()) {
         double var19 = Double.MAX_VALUE;
         Iterator var20 = this.getDisplayedSeriesIterator();
         boolean var21 = true;
         TreeMap var22 = new TreeMap();
         TreeMap var8 = new TreeMap();

         for (TreeMap var9 = new TreeMap(); var20.hasNext(); var21 = var19 == Double.MAX_VALUE) {
            var9.clear();
            XYChart.Series var10 = (XYChart.Series)var20.next();

            for (XYChart.Data var12 : var10.getData()) {
               if (var12 != null) {
                  double var13 = var1.toNumericValue(var12.getXValue());
                  double var15 = var2.toNumericValue(var12.getYValue());
                  var9.put(Double.valueOf(var13), Double.valueOf(var15));
                  if (var21) {
                     var22.put(Double.valueOf(var13), Double.valueOf(var15));
                     var19 = Math.min(var19, var15);
                  } else if (var8.containsKey(Double.valueOf(var13))) {
                     var22.put(Double.valueOf(var13), Double.valueOf((Double)var8.get(Double.valueOf(var13)) + var15));
                  } else {
                     Entry var17 = var8.higherEntry(var13);
                     Entry var18 = var8.lowerEntry(var13);
                     if (var17 != null && var18 != null) {
                        var22.put(
                           Double.valueOf(var13),
                           Double.valueOf(
                              (var13 - (Double)var18.getKey())
                                    / ((Double)var17.getKey() - (Double)var18.getKey())
                                    * ((Double)var18.getValue() + (Double)var17.getValue())
                                 + var15
                           )
                        );
                     } else if (var17 != null) {
                        var22.put(Double.valueOf(var13), Double.valueOf((Double)var17.getValue() + var15));
                     } else if (var18 != null) {
                        var22.put(Double.valueOf(var13), Double.valueOf((Double)var18.getValue() + var15));
                     } else {
                        var22.put(Double.valueOf(var13), Double.valueOf(var15));
                     }
                  }
               }
            }

            for (Entry var24 : var8.entrySet()) {
               if (!var22.keySet().contains(var24.getKey())) {
                  Double var25 = (Double)var24.getKey();
                  Double var14 = (Double)var24.getValue();
                  Entry var26 = var9.higherEntry(var25);
                  Entry var16 = var9.lowerEntry(var25);
                  if (var26 != null && var16 != null) {
                     var22.put(
                        var25,
                        Double.valueOf(
                           (var25 - (Double)var16.getKey())
                                 / ((Double)var26.getKey() - (Double)var16.getKey())
                                 * ((Double)var16.getValue() + (Double)var26.getValue())
                              + var14
                        )
                     );
                  } else if (var26 != null) {
                     var22.put(var25, Double.valueOf((Double)var26.getValue() + var14));
                  } else if (var16 != null) {
                     var22.put(var25, Double.valueOf((Double)var16.getValue() + var14));
                  } else {
                     var22.put(var25, var14);
                  }
               }
            }

            var8.clear();
            var8.putAll(var22);
            var22.clear();
         }

         if (var19 != Double.MAX_VALUE) {
            var2.invalidateRange(Arrays.asList(var2.toRealValue(var19), var2.toRealValue(Collections.<Double>max(var8.values()))));
         }
      }
   }

   @Override
   protected void layoutPlotChildren() {
      ArrayList var1 = new ArrayList();
      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 < this.getDataSize(); var3++) {
         XYChart.Series var4 = (XYChart.Series)this.getData().get(var3);
         var2.clear();

         for (StackedAreaChart.DataPointInfo var6 : var1) {
            var6.partOf = StackedAreaChart.PartOf.PREVIOUS;
            var2.add(var6);
         }

         var1.clear();
         Iterator var35 = this.getDisplayedDataIterator(var4);

         while (var35.hasNext()) {
            XYChart.Data var37 = (XYChart.Data)var35.next();
            StackedAreaChart.DataPointInfo var7 = new StackedAreaChart.DataPointInfo<>(
               var37, var37.getXValue(), var37.getYValue(), StackedAreaChart.PartOf.CURRENT
            );
            var2.add(var7);
         }

         DoubleProperty var36 = this.seriesYMultiplierMap.get(var4);
         Path var38 = (Path)((Group)var4.getNode()).getChildren().get(1);
         Path var39 = (Path)((Group)var4.getNode()).getChildren().get(0);
         var38.getElements().clear();
         var39.getElements().clear();
         int var8 = 0;
         this.sortAggregateList(var2);
         Axis var9 = this.getYAxis();
         Axis var10 = this.getXAxis();
         boolean var11 = false;
         boolean var12 = false;
         int var13 = this.findNextCurrent(var2, -1);
         int var14 = this.findPreviousCurrent(var2, var2.size());
         double var15 = var9.getZeroPosition();
         if (Double.isNaN(var15)) {
            ValueAxis var17 = (ValueAxis)var9;
            if (var17.getLowerBound() > 0.0) {
               var15 = var17.getDisplayPosition(var17.getLowerBound());
            } else {
               var15 = var17.getDisplayPosition(var17.getUpperBound());
            }
         }

         for (StackedAreaChart.DataPointInfo var18 : var2) {
            if (var8 == var14) {
               var12 = true;
            }

            if (var8 == var13) {
               var11 = true;
            }

            XYChart.Data var19 = var18.dataItem;
            if (var18.partOf.equals(StackedAreaChart.PartOf.CURRENT)) {
               int var20 = this.findPreviousPrevious(var2, var8);
               int var21 = this.findNextPrevious(var2, var8);
               if (var20 == -1 || var21 == -1 && !((StackedAreaChart.DataPointInfo)var2.get(var20)).x.equals(var18.x)) {
                  if (var11) {
                     XYChart.Data var55 = new XYChart.Data<>(var18.x, 0);
                     this.addDropDown(var1, var55, (X)var55.getXValue(), (Y)var55.getYValue(), var10.getDisplayPosition(var55.getCurrentX()), var15);
                  }

                  double var56 = var10.getDisplayPosition(var19.getCurrentX());
                  double var59 = var9.getDisplayPosition(var9.toRealValue(var9.toNumericValue(var19.getCurrentY()) * var36.getValue()));
                  this.addPoint(var1, var19, (X)var19.getXValue(), (Y)var19.getYValue(), var56, var59, StackedAreaChart.PartOf.CURRENT, false, !var11);
                  if (var8 == var14) {
                     XYChart.Data var62 = new XYChart.Data<>(var18.x, 0);
                     this.addDropDown(var1, var62, (X)var62.getXValue(), (Y)var62.getYValue(), var10.getDisplayPosition(var62.getCurrentX()), var15);
                  }
               } else {
                  StackedAreaChart.DataPointInfo var22 = (StackedAreaChart.DataPointInfo)var2.get(var20);
                  if (var22.x.equals(var18.x)) {
                     if (var22.dropDown) {
                        var20 = this.findPreviousPrevious(var2, var20);
                        var22 = (StackedAreaChart.DataPointInfo)var2.get(var20);
                     }

                     if (var22.x.equals(var18.x)) {
                        double var24 = var10.getDisplayPosition(var19.getCurrentX());
                        double var26 = var9.toNumericValue(var19.getCurrentY()) + var9.toNumericValue(var22.y);
                        double var28 = var9.getDisplayPosition(var9.toRealValue(var26 * var36.getValue()));
                        this.addPoint(var1, var19, var18.x, (Y)var9.toRealValue(var26), var24, var28, StackedAreaChart.PartOf.CURRENT, false, !var11);
                     }

                     if (var12) {
                        this.addDropDown(var1, var19, var22.x, var22.y, var22.displayX, var22.displayY);
                     }
                  } else {
                     StackedAreaChart.DataPointInfo var23 = var21 == -1 ? null : (StackedAreaChart.DataPointInfo)var2.get(var21);
                     var22 = var20 == -1 ? null : (StackedAreaChart.DataPointInfo)var2.get(var20);
                     double var54 = var9.toNumericValue(var19.getCurrentY());
                     if (var22 != null && var23 != null) {
                        double var58 = var10.getDisplayPosition(var19.getCurrentX());
                        double var61 = this.interpolate(var22.displayX, var22.displayY, var23.displayX, var23.displayY, var58);
                        double var30 = this.interpolate(
                           var10.toNumericValue(var22.x),
                           var9.toNumericValue(var22.y),
                           var10.toNumericValue(var23.x),
                           var9.toNumericValue(var23.y),
                           var10.toNumericValue(var18.x)
                        );
                        if (var11) {
                           XYChart.Data var32 = new XYChart.Data<>(var18.x, var30);
                           this.addDropDown(var1, var32, var18.x, (Y)var9.toRealValue(var30), var58, var61);
                        }

                        double var65 = var9.getDisplayPosition(var9.toRealValue((var54 + var30) * var36.getValue()));
                        this.addPoint(var1, var19, var18.x, (Y)var9.toRealValue(var54 + var30), var58, var65, StackedAreaChart.PartOf.CURRENT, false, !var11);
                        if (var8 == var14) {
                           XYChart.Data var34 = new XYChart.Data<>(var18.x, var30);
                           this.addDropDown(var1, var34, var18.x, (Y)var9.toRealValue(var30), var58, var61);
                        }
                     }
                  }
               }
            } else {
               int var47 = this.findPreviousCurrent(var2, var8);
               int var49 = this.findNextCurrent(var2, var8);
               if (var18.dropDown) {
                  if (var10.toNumericValue(var18.x) <= var10.toNumericValue(((StackedAreaChart.DataPointInfo)var2.get(var13)).x)
                     || var10.toNumericValue(var18.x) > var10.toNumericValue(((StackedAreaChart.DataPointInfo)var2.get(var14)).x)) {
                     this.addDropDown(var1, var19, var18.x, var18.y, var18.displayX, var18.displayY);
                  }
               } else if (var47 != -1 && var49 != -1) {
                  StackedAreaChart.DataPointInfo var53 = (StackedAreaChart.DataPointInfo)var2.get(var49);
                  if (!var53.x.equals(var18.x)) {
                     StackedAreaChart.DataPointInfo var51 = (StackedAreaChart.DataPointInfo)var2.get(var47);
                     double var57 = var10.getDisplayPosition(var19.getCurrentX());
                     double var60 = this.interpolate(
                        var10.toNumericValue(var51.x),
                        var9.toNumericValue(var51.y),
                        var10.toNumericValue(var53.x),
                        var9.toNumericValue(var53.y),
                        var10.toNumericValue(var18.x)
                     );
                     double var63 = var9.toNumericValue(var18.y) + var60;
                     double var64 = var9.getDisplayPosition(var9.toRealValue(var63 * var36.getValue()));
                     this.addPoint(
                        var1,
                        new XYChart.Data<>(var18.x, (Y)var60),
                        var18.x,
                        (Y)var9.toRealValue(var63),
                        var57,
                        var64,
                        StackedAreaChart.PartOf.CURRENT,
                        true,
                        true
                     );
                  }
               } else {
                  this.addPoint(var1, var19, var18.x, var18.y, var18.displayX, var18.displayY, StackedAreaChart.PartOf.CURRENT, true, false);
               }
            }

            var8++;
            if (var11) {
               var11 = false;
            }

            if (var12) {
               var12 = false;
            }
         }

         if (!var1.isEmpty()) {
            var38.getElements().add(new MoveTo(((StackedAreaChart.DataPointInfo)var1.get(0)).displayX, ((StackedAreaChart.DataPointInfo)var1.get(0)).displayY));
            var39.getElements().add(new MoveTo(((StackedAreaChart.DataPointInfo)var1.get(0)).displayX, ((StackedAreaChart.DataPointInfo)var1.get(0)).displayY));
         }

         for (StackedAreaChart.DataPointInfo var43 : var1) {
            if (var43.lineTo) {
               var38.getElements().add(new LineTo(var43.displayX, var43.displayY));
            } else {
               var38.getElements().add(new MoveTo(var43.displayX, var43.displayY));
            }

            var39.getElements().add(new LineTo(var43.displayX, var43.displayY));
            if (!var43.skipSymbol) {
               Node var45 = var43.dataItem.getNode();
               if (var45 != null) {
                  double var48 = var45.prefWidth(-1.0);
                  double var52 = var45.prefHeight(-1.0);
                  var45.resizeRelocate(var43.displayX - var48 / 2.0, var43.displayY - var52 / 2.0, var48, var52);
               }
            }
         }

         for (int var42 = var2.size() - 1; var42 > 0; var42--) {
            StackedAreaChart.DataPointInfo var44 = (StackedAreaChart.DataPointInfo)var2.get(var42);
            if (StackedAreaChart.PartOf.PREVIOUS.equals(var44.partOf)) {
               var39.getElements().add(new LineTo(var44.displayX, var44.displayY));
            }
         }

         if (!var39.getElements().isEmpty()) {
            var39.getElements().add(new ClosePath());
         }
      }
   }

   private void addDropDown(ArrayList<StackedAreaChart.DataPointInfo<X, Y>> var1, XYChart.Data<X, Y> var2, X var3, Y var4, double var5, double var7) {
      StackedAreaChart.DataPointInfo var9 = new StackedAreaChart.DataPointInfo(true);
      var9.setValues(var2, var3, var4, var5, var7, StackedAreaChart.PartOf.CURRENT, true, false);
      var1.add(var9);
   }

   private void addPoint(
      ArrayList<StackedAreaChart.DataPointInfo<X, Y>> var1,
      XYChart.Data<X, Y> var2,
      X var3,
      Y var4,
      double var5,
      double var7,
      StackedAreaChart.PartOf var9,
      boolean var10,
      boolean var11
   ) {
      StackedAreaChart.DataPointInfo var12 = new StackedAreaChart.DataPointInfo();
      var12.setValues(var2, var3, var4, var5, var7, var9, var10, var11);
      var1.add(var12);
   }

   private int findNextCurrent(ArrayList<StackedAreaChart.DataPointInfo<X, Y>> var1, int var2) {
      for (int var3 = var2 + 1; var3 < var1.size(); var3++) {
         if (((StackedAreaChart.DataPointInfo)var1.get(var3)).partOf.equals(StackedAreaChart.PartOf.CURRENT)) {
            return var3;
         }
      }

      return -1;
   }

   private int findPreviousCurrent(ArrayList<StackedAreaChart.DataPointInfo<X, Y>> var1, int var2) {
      for (int var3 = var2 - 1; var3 >= 0; var3--) {
         if (((StackedAreaChart.DataPointInfo)var1.get(var3)).partOf.equals(StackedAreaChart.PartOf.CURRENT)) {
            return var3;
         }
      }

      return -1;
   }

   private int findPreviousPrevious(ArrayList<StackedAreaChart.DataPointInfo<X, Y>> var1, int var2) {
      for (int var3 = var2 - 1; var3 >= 0; var3--) {
         if (((StackedAreaChart.DataPointInfo)var1.get(var3)).partOf.equals(StackedAreaChart.PartOf.PREVIOUS)) {
            return var3;
         }
      }

      return -1;
   }

   private int findNextPrevious(ArrayList<StackedAreaChart.DataPointInfo<X, Y>> var1, int var2) {
      for (int var3 = var2 + 1; var3 < var1.size(); var3++) {
         if (((StackedAreaChart.DataPointInfo)var1.get(var3)).partOf.equals(StackedAreaChart.PartOf.PREVIOUS)) {
            return var3;
         }
      }

      return -1;
   }

   private void sortAggregateList(ArrayList<StackedAreaChart.DataPointInfo<X, Y>> var1) {
      Collections.sort(var1, (var1x, var2) -> {
         XYChart.Data var3 = var1x.dataItem;
         XYChart.Data var4 = var2.dataItem;
         double var5 = this.getXAxis().toNumericValue((X)var3.getXValue());
         double var7 = this.getXAxis().toNumericValue((X)var4.getXValue());
         return var5 < var7 ? -1 : (var5 == var7 ? 0 : 1);
      });
   }

   private double interpolate(double var1, double var3, double var5, double var7, double var9) {
      return (var7 - var3) / (var5 - var1) * (var9 - var1) + var3;
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
      return StackedAreaChart.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   static final class DataPointInfo<X, Y> {
      X x;
      Y y;
      double displayX;
      double displayY;
      XYChart.Data<X, Y> dataItem;
      StackedAreaChart.PartOf partOf;
      boolean skipSymbol = false;
      boolean lineTo = false;
      boolean dropDown = false;

      DataPointInfo() {
      }

      DataPointInfo(XYChart.Data<X, Y> var1, X var2, Y var3, StackedAreaChart.PartOf var4) {
         this.dataItem = var1;
         this.x = (X)var2;
         this.y = (Y)var3;
         this.partOf = var4;
      }

      DataPointInfo(boolean var1) {
         this.dropDown = var1;
      }

      void setValues(XYChart.Data<X, Y> var1, X var2, Y var3, double var4, double var6, StackedAreaChart.PartOf var8, boolean var9, boolean var10) {
         this.dataItem = var1;
         this.x = (X)var2;
         this.y = (Y)var3;
         this.displayX = var4;
         this.displayY = var6;
         this.partOf = var8;
         this.skipSymbol = var9;
         this.lineTo = var10;
      }

      public final X getX() {
         return this.x;
      }

      public final Y getY() {
         return this.y;
      }
   }

   private static enum PartOf {
      CURRENT,
      PREVIOUS;
   }

   private static class StyleableProperties {
      private static final CssMetaData<StackedAreaChart<?, ?>, Boolean> CREATE_SYMBOLS = new CssMetaData<StackedAreaChart<?, ?>, Boolean>(
         "-fx-create-symbols", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(StackedAreaChart<?, ?> var1) {
            return var1.createSymbols == null || !var1.createSymbols.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(StackedAreaChart<?, ?> var1) {
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
