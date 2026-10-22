package javafx.scene.chart;

import com.sun.javafx.charts.Legend;
import java.util.Iterator;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ScatterChart<X, Y> extends XYChart<X, Y> {
   public ScatterChart(@NamedArg("xAxis") Axis<X> var1, @NamedArg("yAxis") Axis<Y> var2) {
      this(var1, var2, FXCollections.observableArrayList());
   }

   public ScatterChart(@NamedArg("xAxis") Axis<X> var1, @NamedArg("yAxis") Axis<Y> var2, @NamedArg("data") ObservableList<XYChart.Series<X, Y>> var3) {
      super(var1, var2);
      this.setData(var3);
   }

   @Override
   protected void dataItemAdded(XYChart.Series<X, Y> var1, int var2, XYChart.Data<X, Y> var3) {
      Object var4 = var3.getNode();
      if (var4 == null) {
         var4 = new StackPane();
         var4.setAccessibleRole(AccessibleRole.TEXT);
         var4.setAccessibleRoleDescription("Point");
         var4.focusTraversableProperty().bind(Platform.accessibilityActiveProperty());
         var3.setNode((Node)var4);
      }

      var4.getStyleClass().setAll(new String[]{"chart-symbol", "series" + this.getData().indexOf(var1), "data" + var2, var1.defaultColorStyleClass});
      if (this.shouldAnimate()) {
         var4.setOpacity(0.0);
         this.getPlotChildren().add(var4);
         FadeTransition var5 = new FadeTransition(Duration.millis(500.0), (Node)var4);
         var5.setToValue(1.0);
         var5.play();
      } else {
         this.getPlotChildren().add(var4);
      }
   }

   @Override
   protected void dataItemRemoved(XYChart.Data<X, Y> var1, XYChart.Series<X, Y> var2) {
      Node var3 = var1.getNode();
      if (var3 != null) {
         var3.focusTraversableProperty().unbind();
      }

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
         this.dataItemAdded(var1, var3, (XYChart.Data<X, Y>)var1.getData().get(var3));
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
               if (var9 != null) {
                  double var10 = var9.prefWidth(-1.0);
                  double var12 = var9.prefHeight(-1.0);
                  var9.resizeRelocate(var5 - var10 / 2.0, var7 - var12 / 2.0, var10, var12);
               }
            }
         }
      }
   }

   @Override
   Legend.LegendItem createLegendItemForSeries(XYChart.Series<X, Y> var1, int var2) {
      Legend.LegendItem var3 = new Legend.LegendItem(var1.getName());
      Node var4 = var1.getData().isEmpty() ? null : ((XYChart.Data)var1.getData().get(0)).getNode();
      if (var4 != null) {
         var3.getSymbol().getStyleClass().addAll(var4.getStyleClass());
      }

      return var3;
   }
}
