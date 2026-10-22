package javafx.scene.chart;

import com.sun.javafx.charts.Legend;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.geometry.Orientation;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class BarChart<X, Y> extends XYChart<X, Y> {
   private Map<XYChart.Series<X, Y>, Map<String, XYChart.Data<X, Y>>> seriesCategoryMap = new HashMap<>();
   private final Orientation orientation;
   private CategoryAxis categoryAxis;
   private ValueAxis valueAxis;
   private Timeline dataRemoveTimeline;
   private double bottomPos = 0.0;
   private static String NEGATIVE_STYLE = "negative";
   private ParallelTransition pt;
   private Map<XYChart.Data<X, Y>, Double> XYValueMap = new HashMap<>();
   private DoubleProperty barGap = new StyleableDoubleProperty(4.0) {
      protected void invalidated() {
         this.get();
         BarChart.this.requestChartLayout();
      }

      public Object getBean() {
         return BarChart.this;
      }

      public String getName() {
         return "barGap";
      }

      public CssMetaData<BarChart<?, ?>, Number> getCssMetaData() {
         return BarChart.StyleableProperties.BAR_GAP;
      }
   };
   private DoubleProperty categoryGap = new StyleableDoubleProperty(10.0) {
      protected void invalidated() {
         this.get();
         BarChart.this.requestChartLayout();
      }

      public Object getBean() {
         return BarChart.this;
      }

      public String getName() {
         return "categoryGap";
      }

      public CssMetaData<BarChart<?, ?>, Number> getCssMetaData() {
         return BarChart.StyleableProperties.CATEGORY_GAP;
      }
   };
   private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");
   private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");

   public final double getBarGap() {
      return this.barGap.getValue();
   }

   public final void setBarGap(double var1) {
      this.barGap.setValue(var1);
   }

   public final DoubleProperty barGapProperty() {
      return this.barGap;
   }

   public final double getCategoryGap() {
      return this.categoryGap.getValue();
   }

   public final void setCategoryGap(double var1) {
      this.categoryGap.setValue(var1);
   }

   public final DoubleProperty categoryGapProperty() {
      return this.categoryGap;
   }

   public BarChart(@NamedArg("xAxis") Axis<X> var1, @NamedArg("yAxis") Axis<Y> var2) {
      this(var1, var2, FXCollections.observableArrayList());
   }

   public BarChart(@NamedArg("xAxis") Axis<X> var1, @NamedArg("yAxis") Axis<Y> var2, @NamedArg("data") ObservableList<XYChart.Series<X, Y>> var3) {
      super(var1, var2);
      this.getStyleClass().add("bar-chart");
      if (var1 instanceof ValueAxis && var2 instanceof CategoryAxis || var2 instanceof ValueAxis && var1 instanceof CategoryAxis) {
         if (var1 instanceof CategoryAxis) {
            this.categoryAxis = (CategoryAxis)var1;
            this.valueAxis = (ValueAxis)var2;
            this.orientation = Orientation.VERTICAL;
         } else {
            this.categoryAxis = (CategoryAxis)var2;
            this.valueAxis = (ValueAxis)var1;
            this.orientation = Orientation.HORIZONTAL;
         }

         this.pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, this.orientation == Orientation.HORIZONTAL);
         this.pseudoClassStateChanged(VERTICAL_PSEUDOCLASS_STATE, this.orientation == Orientation.VERTICAL);
         this.setData(var3);
      } else {
         throw new IllegalArgumentException("Axis type incorrect, one of X,Y should be CategoryAxis and the other NumberAxis");
      }
   }

   public BarChart(
      @NamedArg("xAxis") Axis<X> var1,
      @NamedArg("yAxis") Axis<Y> var2,
      @NamedArg("data") ObservableList<XYChart.Series<X, Y>> var3,
      @NamedArg("categoryGap") double var4
   ) {
      this(var1, var2);
      this.setData(var3);
      this.setCategoryGap(var4);
   }

   @Override
   protected void dataItemAdded(XYChart.Series<X, Y> var1, int var2, XYChart.Data<X, Y> var3) {
      String var4;
      if (this.orientation == Orientation.VERTICAL) {
         var4 = (String)var3.getXValue();
      } else {
         var4 = (String)var3.getYValue();
      }

      Object var5 = this.seriesCategoryMap.get(var1);
      if (var5 == null) {
         var5 = new HashMap();
         this.seriesCategoryMap.put(var1, (Map<String, XYChart.Data<X, Y>>)var5);
      }

      if (!this.categoryAxis.getCategories().contains(var4)) {
         this.categoryAxis.getCategories().add(var2, var4);
      } else if (var5.containsKey(var4)) {
         XYChart.Data var6 = (XYChart.Data)var5.get(var4);
         this.getPlotChildren().remove(var6.getNode());
         this.removeDataItemFromDisplay(var1, var6);
         this.requestChartLayout();
         var5.remove(var4);
      }

      var5.put(var4, var3);
      Node var7 = this.createBar(var1, this.getData().indexOf(var1), var3, var2);
      if (this.shouldAnimate()) {
         this.animateDataAdd(var3, var7);
      } else {
         this.getPlotChildren().add(var7);
      }
   }

   @Override
   protected void dataItemRemoved(XYChart.Data<X, Y> var1, XYChart.Series<X, Y> var2) {
      Node var3 = var1.getNode();
      if (var3 != null) {
         var3.focusTraversableProperty().unbind();
      }

      if (this.shouldAnimate()) {
         this.XYValueMap.clear();
         this.dataRemoveTimeline = this.createDataRemoveTimeline(var1, var3, var2);
         this.dataRemoveTimeline.setOnFinished(var3x -> {
            var1.setSeries(null);
            this.removeDataItemFromDisplay(var2, var1);
         });
         this.dataRemoveTimeline.play();
      } else {
         this.processDataRemove(var2, var1);
         this.removeDataItemFromDisplay(var2, var1);
      }
   }

   @Override
   protected void dataItemChanged(XYChart.Data<X, Y> var1) {
      double var2;
      double var4;
      if (this.orientation == Orientation.VERTICAL) {
         var2 = ((Number)var1.getYValue()).doubleValue();
         var4 = ((Number)var1.getCurrentY()).doubleValue();
      } else {
         var2 = ((Number)var1.getXValue()).doubleValue();
         var4 = ((Number)var1.getCurrentX()).doubleValue();
      }

      if (var4 > 0.0 && var2 < 0.0) {
         var1.getNode().getStyleClass().add(NEGATIVE_STYLE);
      } else if (var4 < 0.0 && var2 > 0.0) {
         var1.getNode().getStyleClass().remove(NEGATIVE_STYLE);
      }
   }

   @Override
   protected void seriesChanged(Change<? extends XYChart.Series> var1) {
      for (int var2 = 0; var2 < this.getDataSize(); var2++) {
         XYChart.Series var3 = (XYChart.Series)this.getData().get(var2);

         for (int var4 = 0; var4 < var3.getData().size(); var4++) {
            XYChart.Data var5 = (XYChart.Data)var3.getData().get(var4);
            Node var6 = var5.getNode();
            var6.getStyleClass().setAll(new String[]{"chart-bar", "series" + var2, "data" + var4, var3.defaultColorStyleClass});
         }
      }
   }

   @Override
   protected void seriesAdded(XYChart.Series<X, Y> var1, int var2) {
      HashMap var3 = new HashMap();

      for (int var4 = 0; var4 < var1.getData().size(); var4++) {
         XYChart.Data var5 = (XYChart.Data)var1.getData().get(var4);
         Node var6 = this.createBar(var1, var2, var5, var4);
         String var7;
         if (this.orientation == Orientation.VERTICAL) {
            var7 = (String)var5.getXValue();
         } else {
            var7 = (String)var5.getYValue();
         }

         var3.put(var7, var5);
         if (this.shouldAnimate()) {
            this.animateDataAdd(var5, var6);
         } else {
            double var8 = this.orientation == Orientation.VERTICAL ? ((Number)var5.getYValue()).doubleValue() : ((Number)var5.getXValue()).doubleValue();
            if (var8 < 0.0) {
               var6.getStyleClass().add(NEGATIVE_STYLE);
            }

            this.getPlotChildren().add(var6);
         }
      }

      if (var3.size() > 0) {
         this.seriesCategoryMap.put(var1, var3);
      }
   }

   @Override
   protected void seriesRemoved(XYChart.Series<X, Y> var1) {
      if (this.shouldAnimate()) {
         this.pt = new ParallelTransition();
         this.pt.setOnFinished(var2 -> this.removeSeriesFromDisplay(var1));
         this.XYValueMap.clear();

         for (XYChart.Data var3 : var1.getData()) {
            Node var4 = var3.getNode();
            if (this.getSeriesSize() > 1) {
               Timeline var5 = this.createDataRemoveTimeline(var3, var4, var1);
               this.pt.getChildren().add(var5);
            } else {
               FadeTransition var8 = new FadeTransition(Duration.millis(700.0), var4);
               var8.setFromValue(1.0);
               var8.setToValue(0.0);
               var8.setOnFinished(var4x -> {
                  this.processDataRemove(var1, var3);
                  var4.setOpacity(1.0);
               });
               this.pt.getChildren().add(var8);
            }
         }

         this.pt.play();
      } else {
         for (XYChart.Data var7 : var1.getData()) {
            this.processDataRemove(var1, var7);
         }

         this.removeSeriesFromDisplay(var1);
      }
   }

   @Override
   protected void layoutPlotChildren() {
      double var1 = this.categoryAxis.getCategorySpacing();
      double var3 = var1 - (this.getCategoryGap() + this.getBarGap());
      double var5 = var3 / (double)this.getSeriesSize() - this.getBarGap();
      double var7 = -((var1 - this.getCategoryGap()) / 2.0);
      double var9 = this.valueAxis.getLowerBound() > 0.0 ? this.valueAxis.getDisplayPosition(this.valueAxis.getLowerBound()) : this.valueAxis.getZeroPosition();
      if (var5 <= 0.0) {
         var5 = 1.0;
      }

      int var11 = 0;

      for (String var13 : this.categoryAxis.getCategories()) {
         int var14 = 0;
         Iterator var15 = this.getDisplayedSeriesIterator();

         while (var15.hasNext()) {
            XYChart.Series var16 = (XYChart.Series)var15.next();
            XYChart.Data var17 = this.getDataItem(var16, var14, var11, var13);
            if (var17 != null) {
               Node var18 = var17.getNode();
               double var19;
               double var21;
               if (this.orientation == Orientation.VERTICAL) {
                  var19 = this.getXAxis().getDisplayPosition((X)var17.getCurrentX());
                  var21 = this.getYAxis().getDisplayPosition((Y)var17.getCurrentY());
               } else {
                  var19 = this.getYAxis().getDisplayPosition((Y)var17.getCurrentY());
                  var21 = this.getXAxis().getDisplayPosition((X)var17.getCurrentX());
               }

               if (!Double.isNaN(var19) && !Double.isNaN(var21)) {
                  double var23 = Math.min(var21, var9);
                  double var25 = Math.max(var21, var9);
                  this.bottomPos = var23;
                  if (this.orientation == Orientation.VERTICAL) {
                     var18.resizeRelocate(var19 + var7 + (var5 + this.getBarGap()) * (double)var14, var23, var5, var25 - var23);
                  } else {
                     var18.resizeRelocate(var23, var19 + var7 + (var5 + this.getBarGap()) * (double)var14, var25 - var23, var5);
                  }

                  var14++;
               }
            }
         }

         var11++;
      }
   }

   @Override
   Legend.LegendItem createLegendItemForSeries(XYChart.Series<X, Y> var1, int var2) {
      Legend.LegendItem var3 = new Legend.LegendItem(var1.getName());
      var3.getSymbol().getStyleClass().addAll(new String[]{"chart-bar", "series" + var2, "bar-legend-symbol", var1.defaultColorStyleClass});
      return var3;
   }

   private void updateMap(XYChart.Series<X, Y> var1, XYChart.Data<X, Y> var2) {
      String var3 = this.orientation == Orientation.VERTICAL ? (String)var2.getXValue() : (String)var2.getYValue();
      Map var4 = this.seriesCategoryMap.get(var1);
      if (var4 != null) {
         var4.remove(var3);
         if (var4.isEmpty()) {
            this.seriesCategoryMap.remove(var1);
         }
      }

      if (this.seriesCategoryMap.isEmpty() && this.categoryAxis.isAutoRanging()) {
         this.categoryAxis.getCategories().clear();
      }
   }

   private void processDataRemove(XYChart.Series<X, Y> var1, XYChart.Data<X, Y> var2) {
      Node var3 = var2.getNode();
      this.getPlotChildren().remove(var3);
      this.updateMap(var1, var2);
   }

   private void animateDataAdd(XYChart.Data<X, Y> var1, Node var2) {
      if (this.orientation == Orientation.VERTICAL) {
         double var3 = ((Number)var1.getYValue()).doubleValue();
         if (var3 < 0.0) {
            var2.getStyleClass().add(NEGATIVE_STYLE);
         }

         var1.setCurrentY(this.getYAxis().toRealValue(var3 < 0.0 ? -this.bottomPos : this.bottomPos));
         this.getPlotChildren().add(var2);
         var1.setYValue(this.getYAxis().toRealValue(var3));
         this.animate(
            new KeyFrame[]{
               new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(var1.currentYProperty(), var1.getCurrentY())}),
               new KeyFrame(Duration.millis(700.0), new KeyValue[]{new KeyValue(var1.currentYProperty(), var1.getYValue(), Interpolator.EASE_BOTH)})
            }
         );
      } else {
         double var5 = ((Number)var1.getXValue()).doubleValue();
         if (var5 < 0.0) {
            var2.getStyleClass().add(NEGATIVE_STYLE);
         }

         var1.setCurrentX(this.getXAxis().toRealValue(var5 < 0.0 ? -this.bottomPos : this.bottomPos));
         this.getPlotChildren().add(var2);
         var1.setXValue(this.getXAxis().toRealValue(var5));
         this.animate(
            new KeyFrame[]{
               new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(var1.currentXProperty(), var1.getCurrentX())}),
               new KeyFrame(Duration.millis(700.0), new KeyValue[]{new KeyValue(var1.currentXProperty(), var1.getXValue(), Interpolator.EASE_BOTH)})
            }
         );
      }
   }

   private Timeline createDataRemoveTimeline(XYChart.Data<X, Y> var1, Node var2, XYChart.Series<X, Y> var3) {
      Timeline var4 = new Timeline();
      if (this.orientation == Orientation.VERTICAL) {
         this.XYValueMap.put(var1, ((Number)var1.getYValue()).doubleValue());
         var1.setYValue(this.getYAxis().toRealValue(this.bottomPos));
         var4.getKeyFrames()
            .addAll(
               new KeyFrame[]{
                  new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(var1.currentYProperty(), var1.getCurrentY())}),
                  new KeyFrame(Duration.millis(700.0), var3x -> {
                     this.processDataRemove(var3, var1);
                     this.XYValueMap.clear();
                  }, new KeyValue[]{new KeyValue(var1.currentYProperty(), var1.getYValue(), Interpolator.EASE_BOTH)})
               }
            );
      } else {
         this.XYValueMap.put(var1, ((Number)var1.getXValue()).doubleValue());
         var1.setXValue(this.getXAxis().toRealValue(this.getXAxis().getZeroPosition()));
         var4.getKeyFrames()
            .addAll(
               new KeyFrame[]{
                  new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(var1.currentXProperty(), var1.getCurrentX())}),
                  new KeyFrame(Duration.millis(700.0), var3x -> {
                     this.processDataRemove(var3, var1);
                     this.XYValueMap.clear();
                  }, new KeyValue[]{new KeyValue(var1.currentXProperty(), var1.getXValue(), Interpolator.EASE_BOTH)})
               }
            );
      }

      return var4;
   }

   @Override
   void dataBeingRemovedIsAdded(XYChart.Data<X, Y> var1, XYChart.Series<X, Y> var2) {
      if (this.dataRemoveTimeline != null) {
         this.dataRemoveTimeline.setOnFinished(null);
         this.dataRemoveTimeline.stop();
      }

      this.processDataRemove(var2, var1);
      var1.setSeries(null);
      this.removeDataItemFromDisplay(var2, var1);
      this.restoreDataValues(var1);
      this.XYValueMap.clear();
   }

   private void restoreDataValues(XYChart.Data var1) {
      Double var2 = this.XYValueMap.get(var1);
      if (var2 != null) {
         if (this.orientation.equals(Orientation.VERTICAL)) {
            var1.setYValue(var2);
            var1.setCurrentY(var2);
         } else {
            var1.setXValue(var2);
            var1.setCurrentX(var2);
         }
      }
   }

   @Override
   void seriesBeingRemovedIsAdded(XYChart.Series<X, Y> var1) {
      boolean var2 = this.pt.getChildren().size() == 1;
      if (this.pt != null) {
         if (!this.pt.getChildren().isEmpty()) {
            for (Animation var4 : this.pt.getChildren()) {
               var4.setOnFinished(null);
            }
         }

         for (XYChart.Data var6 : var1.getData()) {
            this.processDataRemove(var1, var6);
            if (!var2) {
               this.restoreDataValues(var6);
            }
         }

         this.XYValueMap.clear();
         this.pt.setOnFinished(null);
         this.pt.getChildren().clear();
         this.pt.stop();
         this.removeSeriesFromDisplay(var1);
      }
   }

   private Node createBar(XYChart.Series<X, Y> var1, int var2, XYChart.Data<X, Y> var3, int var4) {
      Object var5 = var3.getNode();
      if (var5 == null) {
         var5 = new StackPane();
         var5.setAccessibleRole(AccessibleRole.TEXT);
         var5.setAccessibleRoleDescription("Bar");
         var5.focusTraversableProperty().bind(Platform.accessibilityActiveProperty());
         var3.setNode((Node)var5);
      }

      var5.getStyleClass().setAll(new String[]{"chart-bar", "series" + var2, "data" + var4, var1.defaultColorStyleClass});
      return (Node)var5;
   }

   private XYChart.Data<X, Y> getDataItem(XYChart.Series<X, Y> var1, int var2, int var3, String var4) {
      Map var5 = this.seriesCategoryMap.get(var1);
      return var5 != null ? (XYChart.Data)var5.get(var4) : null;
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return BarChart.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   private static class StyleableProperties {
      private static final CssMetaData<BarChart<?, ?>, Number> BAR_GAP = new CssMetaData<BarChart<?, ?>, Number>(
         "-fx-bar-gap", SizeConverter.getInstance(), 4.0
      ) {
         public boolean isSettable(BarChart<?, ?> var1) {
            return var1.barGap == null || !var1.barGap.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(BarChart<?, ?> var1) {
            return (StyleableProperty<Number>)var1.barGapProperty();
         }
      };
      private static final CssMetaData<BarChart<?, ?>, Number> CATEGORY_GAP = new CssMetaData<BarChart<?, ?>, Number>(
         "-fx-category-gap", SizeConverter.getInstance(), 10.0
      ) {
         public boolean isSettable(BarChart<?, ?> var1) {
            return var1.categoryGap == null || !var1.categoryGap.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(BarChart<?, ?> var1) {
            return (StyleableProperty<Number>)var1.categoryGapProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(XYChart.getClassCssMetaData());
         var0.add(BAR_GAP);
         var0.add(CATEGORY_GAP);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
