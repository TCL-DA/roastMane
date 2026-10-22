package javafx.scene.chart;

import com.sun.javafx.charts.Legend;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;
import javafx.util.Duration;

public class BubbleChart<X, Y> extends XYChart<X, Y> {
   public BubbleChart(@NamedArg("xAxis") Axis<X> var1, @NamedArg("yAxis") Axis<Y> var2) {
      this(var1, var2, FXCollections.observableArrayList());
   }

   public BubbleChart(@NamedArg("xAxis") Axis<X> var1, @NamedArg("yAxis") Axis<Y> var2, @NamedArg("data") ObservableList<XYChart.Series<X, Y>> var3) {
      super(var1, var2);
      if (var1 instanceof ValueAxis && var2 instanceof ValueAxis) {
         this.setData(var3);
      } else {
         throw new IllegalArgumentException("Axis type incorrect, X and Y should both be NumberAxis");
      }
   }

   private static double getDoubleValue(Object var0, double var1) {
      return !(var0 instanceof Number) ? var1 : ((Number)var0).doubleValue();
   }

   @Override
   protected void layoutPlotChildren() {
      for (int var1 = 0; var1 < this.getDataSize(); var1++) {
         XYChart.Series var2 = (XYChart.Series)this.getData().get(var1);
         Iterator var3 = this.getDisplayedDataIterator(var2);

         while (var3.hasNext()) {
            XYChart.Data var4 = (XYChart.Data)var3.next();
            double var5 = this.getXAxis().getDisplayPosition((X)var4.getCurrentX());
            double var7 = this.getYAxis().getDisplayPosition((Y)var4.getCurrentY());
            if (!Double.isNaN(var5) && !Double.isNaN(var7)) {
               Node var9 = var4.getNode();
               if (var9 != null && var9 instanceof StackPane) {
                  StackPane var11 = (StackPane)var4.getNode();
                  Ellipse var10;
                  if (var11.getShape() == null) {
                     var10 = new Ellipse(getDoubleValue(var4.getExtraValue(), 1.0), getDoubleValue(var4.getExtraValue(), 1.0));
                  } else {
                     if (!(var11.getShape() instanceof Ellipse)) {
                        return;
                     }

                     var10 = (Ellipse)var11.getShape();
                  }

                  var10.setRadiusX(
                     getDoubleValue(var4.getExtraValue(), 1.0)
                        * (this.getXAxis() instanceof NumberAxis ? Math.abs(((NumberAxis)this.getXAxis()).getScale()) : 1.0)
                  );
                  var10.setRadiusY(
                     getDoubleValue(var4.getExtraValue(), 1.0)
                        * (this.getYAxis() instanceof NumberAxis ? Math.abs(((NumberAxis)this.getYAxis()).getScale()) : 1.0)
                  );
                  var11.setShape(null);
                  var11.setShape(var10);
                  var11.setScaleShape(false);
                  var11.setCenterShape(false);
                  var11.setCacheShape(false);
                  var9.setLayoutX(var5);
                  var9.setLayoutY(var7);
               }
            }
         }
      }
   }

   @Override
   protected void dataItemAdded(XYChart.Series<X, Y> var1, int var2, XYChart.Data<X, Y> var3) {
      Node var4 = this.createBubble(var1, this.getData().indexOf(var1), var3, var2);
      if (this.shouldAnimate()) {
         var4.setOpacity(0.0);
         this.getPlotChildren().add(var4);
         FadeTransition var5 = new FadeTransition(Duration.millis(500.0), var4);
         var5.setToValue(1.0);
         var5.play();
      } else {
         this.getPlotChildren().add(var4);
      }
   }

   @Override
   protected void dataItemRemoved(XYChart.Data<X, Y> var1, XYChart.Series<X, Y> var2) {
      Node var3 = var1.getNode();
      if (this.shouldAnimate()) {
         FadeTransition var4 = new FadeTransition(Duration.millis(500.0), var3);
         var4.setToValue(0.0);
         var4.setOnFinished(var4x -> {
            this.getPlotChildren().remove(var3);
            this.removeDataItemFromDisplay(var2, var1);
            var3.setOpacity(1.0);
         });
         var4.play();
      } else {
         this.getPlotChildren().remove(var3);
         this.removeDataItemFromDisplay(var2, var1);
      }
   }

   @Override
   protected void dataItemChanged(XYChart.Data<X, Y> var1) {
   }

   @Override
   protected void seriesAdded(XYChart.Series<X, Y> var1, int var2) {
      for (int var3 = 0; var3 < var1.getData().size(); var3++) {
         XYChart.Data var4 = (XYChart.Data)var1.getData().get(var3);
         Node var5 = this.createBubble(var1, var2, var4, var3);
         if (this.shouldAnimate()) {
            var5.setOpacity(0.0);
            this.getPlotChildren().add(var5);
            FadeTransition var6 = new FadeTransition(Duration.millis(500.0), var5);
            var6.setToValue(1.0);
            var6.play();
         } else {
            this.getPlotChildren().add(var5);
         }
      }
   }

   @Override
   protected void seriesRemoved(XYChart.Series<X, Y> var1) {
      if (this.shouldAnimate()) {
         ParallelTransition var2 = new ParallelTransition();
         var2.setOnFinished(var2x -> this.removeSeriesFromDisplay(var1));

         for (XYChart.Data var4 : var1.getData()) {
            Node var5 = var4.getNode();
            FadeTransition var6 = new FadeTransition(Duration.millis(500.0), var5);
            var6.setToValue(0.0);
            var6.setOnFinished(var2x -> {
               this.getPlotChildren().remove(var5);
               var5.setOpacity(1.0);
            });
            var2.getChildren().add(var6);
         }

         var2.play();
      } else {
         for (XYChart.Data var8 : var1.getData()) {
            Node var9 = var8.getNode();
            this.getPlotChildren().remove(var9);
         }

         this.removeSeriesFromDisplay(var1);
      }
   }

   private Node createBubble(XYChart.Series<X, Y> var1, int var2, final XYChart.Data<X, Y> var3, int var4) {
      Object var5 = var3.getNode();
      if (var5 == null) {
         var5 = new StackPane() {
            public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
               switch (var1) {
                  case TEXT:
                     String var3x = this.getAccessibleText();
                     if (var3.getExtraValue() == null) {
                        return var3x;
                     }

                     return var3x + " Bubble radius is " + var3.getExtraValue();
                  default:
                     return super.queryAccessibleAttribute(var1, var2);
               }
            }
         };
         var5.setAccessibleRole(AccessibleRole.TEXT);
         var5.setAccessibleRoleDescription("Bubble");
         var5.focusTraversableProperty().bind(Platform.accessibilityActiveProperty());
         var3.setNode((Node)var5);
      }

      var5.getStyleClass().setAll(new String[]{"chart-bubble", "series" + var2, "data" + var4, var1.defaultColorStyleClass});
      return (Node)var5;
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

      boolean var5 = var1 instanceof CategoryAxis;
      boolean var6 = var2 instanceof CategoryAxis;
      if (var3 != null || var4 != null) {
         for (XYChart.Series var8 : this.getData()) {
            for (XYChart.Data var10 : var8.getData()) {
               if (var3 != null) {
                  if (var5) {
                     var3.add(var10.getXValue());
                  } else {
                     var3.add(var1.toRealValue(var1.toNumericValue(var10.getXValue()) + getDoubleValue(var10.getExtraValue(), 0.0)));
                     var3.add(var1.toRealValue(var1.toNumericValue(var10.getXValue()) - getDoubleValue(var10.getExtraValue(), 0.0)));
                  }
               }

               if (var4 != null) {
                  if (var6) {
                     var4.add(var10.getYValue());
                  } else {
                     var4.add(var2.toRealValue(var2.toNumericValue(var10.getYValue()) + getDoubleValue(var10.getExtraValue(), 0.0)));
                     var4.add(var2.toRealValue(var2.toNumericValue(var10.getYValue()) - getDoubleValue(var10.getExtraValue(), 0.0)));
                  }
               }
            }
         }

         if (var3 != null) {
            var1.invalidateRange(var3);
         }

         if (var4 != null) {
            var2.invalidateRange(var4);
         }
      }
   }

   @Override
   Legend.LegendItem createLegendItemForSeries(XYChart.Series<X, Y> var1, int var2) {
      Legend.LegendItem var3 = new Legend.LegendItem(var1.getName());
      var3.getSymbol().getStyleClass().addAll(new String[]{"series" + var2, "chart-bubble", "bubble-legend-symbol", var1.defaultColorStyleClass});
      return var3;
   }
}
