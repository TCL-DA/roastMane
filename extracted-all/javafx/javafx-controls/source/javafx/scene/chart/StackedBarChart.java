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
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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

public class StackedBarChart<X, Y> extends XYChart<X, Y> {
   private Map<XYChart.Series<X, Y>, Map<String, List<XYChart.Data<X, Y>>>> seriesCategoryMap = new HashMap<>();
   private final Orientation orientation;
   private CategoryAxis categoryAxis;
   private ValueAxis valueAxis;
   private ListChangeListener<String> categoriesListener = new ListChangeListener<String>() {
      public void onChanged(Change<? extends String> var1) {
         while (var1.next()) {
            for (String var3 : var1.getRemoved()) {
               for (XYChart.Series var5 : StackedBarChart.this.getData()) {
                  for (XYChart.Data var7 : var5.getData()) {
                     if (var3.equals(StackedBarChart.this.orientation == Orientation.VERTICAL ? var7.getXValue() : var7.getYValue())) {
                        boolean var8 = StackedBarChart.this.getAnimated();
                        StackedBarChart.this.setAnimated(false);
                        StackedBarChart.this.dataItemRemoved(var7, var5);
                        StackedBarChart.this.setAnimated(var8);
                     }
                  }
               }

               StackedBarChart.this.requestChartLayout();
            }
         }
      }
   };
   private DoubleProperty categoryGap = new StyleableDoubleProperty(10.0) {
      protected void invalidated() {
         this.get();
         StackedBarChart.this.requestChartLayout();
      }

      public Object getBean() {
         return StackedBarChart.this;
      }

      public String getName() {
         return "categoryGap";
      }

      public CssMetaData<StackedBarChart<?, ?>, Number> getCssMetaData() {
         return StackedBarChart.StyleableProperties.CATEGORY_GAP;
      }
   };
   private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");
   private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");

   public double getCategoryGap() {
      return this.categoryGap.getValue();
   }

   public void setCategoryGap(double var1) {
      this.categoryGap.setValue(var1);
   }

   public DoubleProperty categoryGapProperty() {
      return this.categoryGap;
   }

   public StackedBarChart(@NamedArg("xAxis") Axis<X> var1, @NamedArg("yAxis") Axis<Y> var2) {
      this(var1, var2, FXCollections.observableArrayList());
   }

   public StackedBarChart(@NamedArg("xAxis") Axis<X> var1, @NamedArg("yAxis") Axis<Y> var2, @NamedArg("data") ObservableList<XYChart.Series<X, Y>> var3) {
      super(var1, var2);
      this.getStyleClass().add("stacked-bar-chart");
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
         this.categoryAxis.getCategories().addListener(this.categoriesListener);
      } else {
         throw new IllegalArgumentException("Axis type incorrect, one of X,Y should be CategoryAxis and the other NumberAxis");
      }
   }

   public StackedBarChart(
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
         this.seriesCategoryMap.put(var1, (Map<String, List<XYChart.Data<X, Y>>>)var5);
      }

      Object var6 = var5.get(var4) != null ? (List)var5.get(var4) : new ArrayList();
      var6.add(var3);
      var5.put(var4, var6);
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
         Timeline var4 = this.createDataRemoveTimeline(var1, var3, var2);
         var4.setOnFinished(var3x -> this.removeDataItemFromDisplay(var2, var1));
         var4.play();
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
         var4 = ((Number)this.getCurrentDisplayedYValue(var1)).doubleValue();
      } else {
         var2 = ((Number)var1.getXValue()).doubleValue();
         var4 = ((Number)this.getCurrentDisplayedXValue(var1)).doubleValue();
      }

      if (var4 > 0.0 && var2 < 0.0) {
         var1.getNode().getStyleClass().add("negative");
      } else if (var4 < 0.0 && var2 > 0.0) {
         var1.getNode().getStyleClass().remove("negative");
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

         Object var8 = var3.get(var7) != null ? (List)var3.get(var7) : new ArrayList();
         var8.add(var5);
         var3.put(var7, var8);
         if (this.shouldAnimate()) {
            this.animateDataAdd(var5, var6);
         } else {
            double var9 = this.orientation == Orientation.VERTICAL ? ((Number)var5.getYValue()).doubleValue() : ((Number)var5.getXValue()).doubleValue();
            if (var9 < 0.0) {
               var6.getStyleClass().add("negative");
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
         ParallelTransition var2 = new ParallelTransition();
         var2.setOnFinished(var2x -> {
            this.removeSeriesFromDisplay(var1);
            this.requestChartLayout();
         });

         for (XYChart.Data var4 : var1.getData()) {
            Node var5 = var4.getNode();
            if (this.getSeriesSize() > 1) {
               Timeline var6 = this.createDataRemoveTimeline(var4, var5, var1);
               var2.getChildren().add(var6);
            } else {
               FadeTransition var9 = new FadeTransition(Duration.millis(700.0), var5);
               var9.setFromValue(1.0);
               var9.setToValue(0.0);
               var9.setOnFinished(var4x -> {
                  this.processDataRemove(var1, var4);
                  var5.setOpacity(1.0);
               });
               var2.getChildren().add(var9);
            }
         }

         var2.play();
      } else {
         for (XYChart.Data var8 : var1.getData()) {
            this.processDataRemove(var1, var8);
         }

         this.removeSeriesFromDisplay(var1);
         this.requestChartLayout();
      }
   }

   @Override
   protected void updateAxisRange() {
      boolean var1 = this.categoryAxis == this.getXAxis();
      if (this.categoryAxis.isAutoRanging()) {
         ArrayList var2 = new ArrayList();

         for (XYChart.Series var4 : this.getData()) {
            for (XYChart.Data var6 : var4.getData()) {
               if (var6 != null) {
                  var2.add(var1 ? var6.getXValue() : var6.getYValue());
               }
            }
         }

         this.categoryAxis.invalidateRange(var2);
      }

      if (this.valueAxis.isAutoRanging()) {
         ArrayList var15 = new ArrayList();

         for (String var17 : this.categoryAxis.getAllDataCategories()) {
            double var18 = 0.0;
            double var7 = 0.0;
            Iterator var9 = this.getDisplayedSeriesIterator();

            while (var9.hasNext()) {
               XYChart.Series var10 = (XYChart.Series)var9.next();

               for (XYChart.Data var12 : this.getDataItem(var10, var17)) {
                  if (var12 != null) {
                     boolean var13 = var12.getNode().getStyleClass().contains("negative");
                     Number var14 = (Number)(var1 ? var12.getYValue() : var12.getXValue());
                     if (!var13) {
                        var7 += this.valueAxis.toNumericValue(var14);
                     } else {
                        var18 += this.valueAxis.toNumericValue(var14);
                     }
                  }
               }
            }

            var15.add(var7);
            var15.add(var18);
         }

         this.valueAxis.invalidateRange(var15);
      }
   }

   @Override
   protected void layoutPlotChildren() {
      double var1 = this.categoryAxis.getCategorySpacing();
      double var3 = var1 - this.getCategoryGap();
      double var5 = var3;
      double var7 = -((var1 - this.getCategoryGap()) / 2.0);

      for (String var10 : this.categoryAxis.getCategories()) {
         double var11 = 0.0;
         double var13 = 0.0;
         Iterator var15 = this.getDisplayedSeriesIterator();

         while (var15.hasNext()) {
            XYChart.Series var16 = (XYChart.Series)var15.next();

            for (XYChart.Data var18 : this.getDataItem(var16, var10)) {
               if (var18 != null) {
                  Node var19 = var18.getNode();
                  Object var24 = this.getCurrentDisplayedXValue(var18);
                  Object var25 = this.getCurrentDisplayedYValue(var18);
                  double var20;
                  double var22;
                  if (this.orientation == Orientation.VERTICAL) {
                     var20 = this.getXAxis().getDisplayPosition((X)var24);
                     var22 = this.getYAxis().toNumericValue((Y)var25);
                  } else {
                     var20 = this.getYAxis().getDisplayPosition((Y)var25);
                     var22 = this.getXAxis().toNumericValue((X)var24);
                  }

                  boolean var30 = var19.getStyleClass().contains("negative");
                  double var26;
                  double var28;
                  if (!var30) {
                     var26 = this.valueAxis.getDisplayPosition(var11);
                     var28 = this.valueAxis.getDisplayPosition(var11 + var22);
                     var11 += var22;
                  } else {
                     var26 = this.valueAxis.getDisplayPosition(var13 + var22);
                     var28 = this.valueAxis.getDisplayPosition(var13);
                     var13 += var22;
                  }

                  if (this.orientation == Orientation.VERTICAL) {
                     var19.resizeRelocate(var20 + var7, var28, var5, var26 - var28);
                  } else {
                     var19.resizeRelocate(var26, var20 + var7, var28 - var26, var5);
                  }
               }
            }
         }
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
            var2.getStyleClass().add("negative");
         }

         var1.setYValue(this.getYAxis().toRealValue(this.getYAxis().getZeroPosition()));
         this.setCurrentDisplayedYValue(var1, this.getYAxis().toRealValue(this.getYAxis().getZeroPosition()));
         this.getPlotChildren().add(var2);
         var1.setYValue(this.getYAxis().toRealValue(var3));
         this.animate(
            new KeyFrame[]{
               new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(this.currentDisplayedYValueProperty(var1), this.getCurrentDisplayedYValue(var1))}),
               new KeyFrame(
                  Duration.millis(700.0), new KeyValue[]{new KeyValue(this.currentDisplayedYValueProperty(var1), var1.getYValue(), Interpolator.EASE_BOTH)}
               )
            }
         );
      } else {
         double var5 = ((Number)var1.getXValue()).doubleValue();
         if (var5 < 0.0) {
            var2.getStyleClass().add("negative");
         }

         var1.setXValue(this.getXAxis().toRealValue(this.getXAxis().getZeroPosition()));
         this.setCurrentDisplayedXValue(var1, this.getXAxis().toRealValue(this.getXAxis().getZeroPosition()));
         this.getPlotChildren().add(var2);
         var1.setXValue(this.getXAxis().toRealValue(var5));
         this.animate(
            new KeyFrame[]{
               new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(this.currentDisplayedXValueProperty(var1), this.getCurrentDisplayedXValue(var1))}),
               new KeyFrame(
                  Duration.millis(700.0), new KeyValue[]{new KeyValue(this.currentDisplayedXValueProperty(var1), var1.getXValue(), Interpolator.EASE_BOTH)}
               )
            }
         );
      }
   }

   private Timeline createDataRemoveTimeline(XYChart.Data<X, Y> var1, Node var2, XYChart.Series<X, Y> var3) {
      Timeline var4 = new Timeline();
      if (this.orientation == Orientation.VERTICAL) {
         var1.setYValue(this.getYAxis().toRealValue(this.getYAxis().getZeroPosition()));
         var4.getKeyFrames()
            .addAll(
               new KeyFrame[]{
                  new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(this.currentDisplayedYValueProperty(var1), this.getCurrentDisplayedYValue(var1))}),
                  new KeyFrame(
                     Duration.millis(700.0),
                     var3x -> this.processDataRemove(var3, var1),
                     new KeyValue[]{new KeyValue(this.currentDisplayedYValueProperty(var1), var1.getYValue(), Interpolator.EASE_BOTH)}
                  )
               }
            );
      } else {
         var1.setXValue(this.getXAxis().toRealValue(this.getXAxis().getZeroPosition()));
         var4.getKeyFrames()
            .addAll(
               new KeyFrame[]{
                  new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(this.currentDisplayedXValueProperty(var1), this.getCurrentDisplayedXValue(var1))}),
                  new KeyFrame(
                     Duration.millis(700.0),
                     var3x -> this.processDataRemove(var3, var1),
                     new KeyValue[]{new KeyValue(this.currentDisplayedXValueProperty(var1), var1.getXValue(), Interpolator.EASE_BOTH)}
                  )
               }
            );
      }

      return var4;
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

   private List<XYChart.Data<X, Y>> getDataItem(XYChart.Series<X, Y> var1, String var2) {
      Map var3 = this.seriesCategoryMap.get(var1);
      return (List<XYChart.Data<X, Y>>)(var3 != null ? (var3.get(var2) != null ? (List)var3.get(var2) : new ArrayList<>()) : new ArrayList<>());
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return StackedBarChart.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   private static class StyleableProperties {
      private static final CssMetaData<StackedBarChart<?, ?>, Number> CATEGORY_GAP = new CssMetaData<StackedBarChart<?, ?>, Number>(
         "-fx-category-gap", SizeConverter.getInstance(), 10.0
      ) {
         public boolean isSettable(StackedBarChart<?, ?> var1) {
            return var1.categoryGap == null || !var1.categoryGap.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(StackedBarChart<?, ?> var1) {
            return (StyleableProperty<Number>)var1.categoryGapProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(XYChart.getClassCssMetaData());
         var0.add(CATEGORY_GAP);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
