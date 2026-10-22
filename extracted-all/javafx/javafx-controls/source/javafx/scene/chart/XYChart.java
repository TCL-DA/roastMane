package javafx.scene.chart;

import com.sun.javafx.charts.Legend;
import com.sun.javafx.collections.NonIterableChange;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public abstract class XYChart<X, Y> extends Chart {
   private final BitSet colorBits = new BitSet(8);
   static String DEFAULT_COLOR = "default-color";
   final Map<XYChart.Series<X, Y>, Integer> seriesColorMap = new HashMap<>();
   private boolean rangeValid = false;
   private final Line verticalZeroLine = new Line();
   private final Line horizontalZeroLine = new Line();
   private final Path verticalGridLines = new Path();
   private final Path horizontalGridLines = new Path();
   private final Path horizontalRowFill = new Path();
   private final Path verticalRowFill = new Path();
   private final Region plotBackground = new Region();
   private final Group plotArea = new Group() {
      public void requestLayout() {
      }
   };
   private final Group plotContent = new Group();
   private final Rectangle plotAreaClip = new Rectangle();
   private final List<XYChart.Series<X, Y>> displayedSeries = new ArrayList<>();
   private Legend legend = new Legend();
   private final ListChangeListener<XYChart.Series<X, Y>> seriesChanged = var1x -> {
      ObservableList var2x = var1x.getList();

      while (var1x.next()) {
         if (var1x.wasPermutated()) {
            this.displayedSeries.sort((var1xx, var2xx) -> var2x.indexOf(var2xx) - var2x.indexOf(var1xx));
         }

         if (var1x.getRemoved().size() > 0) {
            this.updateLegend();
         }

         HashSet var3 = new HashSet<>(this.displayedSeries);
         var3.removeAll(var1x.getRemoved());

         for (XYChart.Series var5 : var1x.getAddedSubList()) {
            if (!var3.add(var5)) {
               throw new IllegalArgumentException("Duplicate series added");
            }
         }

         for (XYChart.Series var9 : var1x.getRemoved()) {
            var9.setToRemove = true;
            this.seriesRemoved(var9);
         }

         for (int var8 = var1x.getFrom(); var8 < var1x.getTo() && !var1x.wasPermutated(); var8++) {
            XYChart.Series var10 = (XYChart.Series)var1x.getList().get(var8);
            var10.setChart(this);
            if (var10.setToRemove) {
               var10.setToRemove = false;
               var10.getChart().seriesBeingRemovedIsAdded(var10);
            }

            this.displayedSeries.add(var10);
            int var6 = this.colorBits.nextClearBit(0);
            this.colorBits.set(var6, true);
            var10.defaultColorStyleClass = DEFAULT_COLOR + var6 % 8;
            this.seriesColorMap.put(var10, var6 % 8);
            this.seriesAdded(var10, var8);
         }

         if (var1x.getFrom() < var1x.getTo()) {
            this.updateLegend();
         }

         this.seriesChanged(var1x);
      }

      this.invalidateRange();
      this.requestChartLayout();
   };
   private final Axis<X> xAxis;
   private final Axis<Y> yAxis;
   private ObjectProperty<ObservableList<XYChart.Series<X, Y>>> data = new ObjectPropertyBase<ObservableList<XYChart.Series<X, Y>>>() {
      private ObservableList<XYChart.Series<X, Y>> old;

      protected void invalidated() {
         ObservableList var1 = (ObservableList)this.getValue();
         if (var1 != this.old) {
            int var2 = -1;
            if (this.old != null) {
               this.old.removeListener(XYChart.this.seriesChanged);
               if (var1 != null && this.old.size() > 0) {
                  var2 = ((XYChart.Series)this.old.get(0)).getChart().getAnimated() ? 1 : 2;
                  ((XYChart.Series)this.old.get(0)).getChart().setAnimated(false);
               }
            }

            if (var1 != null) {
               var1.addListener(XYChart.this.seriesChanged);
            }

            if (this.old == null && var1 == null) {
               if (this.old != null && this.old.size() > 0) {
                  XYChart.this.seriesChanged.onChanged(new NonIterableChange<XYChart.Series<X, Y>>(0, 0, var1) {
                     public List<XYChart.Series<X, Y>> getRemoved() {
                        return old;
                     }

                     protected int[] getPermutation() {
                        return new int[0];
                     }
                  });
               }
            } else {
               final Object var3 = this.old != null ? this.old : Collections.emptyList();
               int var4 = var1 != null ? var1.size() : 0;
               if (var4 > 0 || !var3.isEmpty()) {
                  XYChart.this.seriesChanged.onChanged(new NonIterableChange<XYChart.Series<X, Y>>(0, var4, var1) {
                     public List<XYChart.Series<X, Y>> getRemoved() {
                        return (List<XYChart.Series<X, Y>>)var3;
                     }

                     protected int[] getPermutation() {
                        return new int[0];
                     }
                  });
               }
            }

            if (var1 != null && var1.size() > 0 && var2 != -1) {
               ((XYChart.Series)var1.get(0)).getChart().setAnimated(var2 == 1);
            }

            this.old = var1;
         }
      }

      public Object getBean() {
         return XYChart.this;
      }

      public String getName() {
         return "data";
      }
   };
   private BooleanProperty verticalGridLinesVisible = new StyleableBooleanProperty(true) {
      protected void invalidated() {
         XYChart.this.requestChartLayout();
      }

      public Object getBean() {
         return XYChart.this;
      }

      public String getName() {
         return "verticalGridLinesVisible";
      }

      public CssMetaData<XYChart<?, ?>, Boolean> getCssMetaData() {
         return XYChart.StyleableProperties.VERTICAL_GRID_LINE_VISIBLE;
      }
   };
   private BooleanProperty horizontalGridLinesVisible = new StyleableBooleanProperty(true) {
      protected void invalidated() {
         XYChart.this.requestChartLayout();
      }

      public Object getBean() {
         return XYChart.this;
      }

      public String getName() {
         return "horizontalGridLinesVisible";
      }

      public CssMetaData<XYChart<?, ?>, Boolean> getCssMetaData() {
         return XYChart.StyleableProperties.HORIZONTAL_GRID_LINE_VISIBLE;
      }
   };
   private BooleanProperty alternativeColumnFillVisible = new StyleableBooleanProperty(false) {
      protected void invalidated() {
         XYChart.this.requestChartLayout();
      }

      public Object getBean() {
         return XYChart.this;
      }

      public String getName() {
         return "alternativeColumnFillVisible";
      }

      public CssMetaData<XYChart<?, ?>, Boolean> getCssMetaData() {
         return XYChart.StyleableProperties.ALTERNATIVE_COLUMN_FILL_VISIBLE;
      }
   };
   private BooleanProperty alternativeRowFillVisible = new StyleableBooleanProperty(true) {
      protected void invalidated() {
         XYChart.this.requestChartLayout();
      }

      public Object getBean() {
         return XYChart.this;
      }

      public String getName() {
         return "alternativeRowFillVisible";
      }

      public CssMetaData<XYChart<?, ?>, Boolean> getCssMetaData() {
         return XYChart.StyleableProperties.ALTERNATIVE_ROW_FILL_VISIBLE;
      }
   };
   private BooleanProperty verticalZeroLineVisible = new StyleableBooleanProperty(true) {
      protected void invalidated() {
         XYChart.this.requestChartLayout();
      }

      public Object getBean() {
         return XYChart.this;
      }

      public String getName() {
         return "verticalZeroLineVisible";
      }

      public CssMetaData<XYChart<?, ?>, Boolean> getCssMetaData() {
         return XYChart.StyleableProperties.VERTICAL_ZERO_LINE_VISIBLE;
      }
   };
   private BooleanProperty horizontalZeroLineVisible = new StyleableBooleanProperty(true) {
      protected void invalidated() {
         XYChart.this.requestChartLayout();
      }

      public Object getBean() {
         return XYChart.this;
      }

      public String getName() {
         return "horizontalZeroLineVisible";
      }

      public CssMetaData<XYChart<?, ?>, Boolean> getCssMetaData() {
         return XYChart.StyleableProperties.HORIZONTAL_ZERO_LINE_VISIBLE;
      }
   };

   public Axis<X> getXAxis() {
      return this.xAxis;
   }

   public Axis<Y> getYAxis() {
      return this.yAxis;
   }

   public final ObservableList<XYChart.Series<X, Y>> getData() {
      return (ObservableList<XYChart.Series<X, Y>>)this.data.getValue();
   }

   public final void setData(ObservableList<XYChart.Series<X, Y>> var1) {
      this.data.setValue(var1);
   }

   public final ObjectProperty<ObservableList<XYChart.Series<X, Y>>> dataProperty() {
      return this.data;
   }

   public final boolean getVerticalGridLinesVisible() {
      return this.verticalGridLinesVisible.get();
   }

   public final void setVerticalGridLinesVisible(boolean var1) {
      this.verticalGridLinesVisible.set(var1);
   }

   public final BooleanProperty verticalGridLinesVisibleProperty() {
      return this.verticalGridLinesVisible;
   }

   public final boolean isHorizontalGridLinesVisible() {
      return this.horizontalGridLinesVisible.get();
   }

   public final void setHorizontalGridLinesVisible(boolean var1) {
      this.horizontalGridLinesVisible.set(var1);
   }

   public final BooleanProperty horizontalGridLinesVisibleProperty() {
      return this.horizontalGridLinesVisible;
   }

   public final boolean isAlternativeColumnFillVisible() {
      return this.alternativeColumnFillVisible.getValue();
   }

   public final void setAlternativeColumnFillVisible(boolean var1) {
      this.alternativeColumnFillVisible.setValue(var1);
   }

   public final BooleanProperty alternativeColumnFillVisibleProperty() {
      return this.alternativeColumnFillVisible;
   }

   public final boolean isAlternativeRowFillVisible() {
      return this.alternativeRowFillVisible.getValue();
   }

   public final void setAlternativeRowFillVisible(boolean var1) {
      this.alternativeRowFillVisible.setValue(var1);
   }

   public final BooleanProperty alternativeRowFillVisibleProperty() {
      return this.alternativeRowFillVisible;
   }

   public final boolean isVerticalZeroLineVisible() {
      return this.verticalZeroLineVisible.get();
   }

   public final void setVerticalZeroLineVisible(boolean var1) {
      this.verticalZeroLineVisible.set(var1);
   }

   public final BooleanProperty verticalZeroLineVisibleProperty() {
      return this.verticalZeroLineVisible;
   }

   public final boolean isHorizontalZeroLineVisible() {
      return this.horizontalZeroLineVisible.get();
   }

   public final void setHorizontalZeroLineVisible(boolean var1) {
      this.horizontalZeroLineVisible.set(var1);
   }

   public final BooleanProperty horizontalZeroLineVisibleProperty() {
      return this.horizontalZeroLineVisible;
   }

   protected ObservableList<Node> getPlotChildren() {
      return this.plotContent.getChildren();
   }

   public XYChart(Axis<X> var1, Axis<Y> var2) {
      this.xAxis = var1;
      if (var1.getSide() == null) {
         var1.setSide(Side.BOTTOM);
      }

      var1.setEffectiveOrientation(Orientation.HORIZONTAL);
      this.yAxis = var2;
      if (var2.getSide() == null) {
         var2.setSide(Side.LEFT);
      }

      var2.setEffectiveOrientation(Orientation.VERTICAL);
      var1.autoRangingProperty().addListener((var1x, var2x, var3) -> this.updateAxisRange());
      var2.autoRangingProperty().addListener((var1x, var2x, var3) -> this.updateAxisRange());
      this.getChartChildren().addAll(new Node[]{this.plotBackground, this.plotArea, var1, var2});
      this.plotArea.setAutoSizeChildren(false);
      this.plotContent.setAutoSizeChildren(false);
      this.plotAreaClip.setSmooth(false);
      this.plotArea.setClip(this.plotAreaClip);
      this.plotArea
         .getChildren()
         .addAll(
            new Node[]{
               this.verticalRowFill,
               this.horizontalRowFill,
               this.verticalGridLines,
               this.horizontalGridLines,
               this.verticalZeroLine,
               this.horizontalZeroLine,
               this.plotContent
            }
         );
      this.plotContent.getStyleClass().setAll(new String[]{"plot-content"});
      this.plotBackground.getStyleClass().setAll(new String[]{"chart-plot-background"});
      this.verticalRowFill.getStyleClass().setAll(new String[]{"chart-alternative-column-fill"});
      this.horizontalRowFill.getStyleClass().setAll(new String[]{"chart-alternative-row-fill"});
      this.verticalGridLines.getStyleClass().setAll(new String[]{"chart-vertical-grid-lines"});
      this.horizontalGridLines.getStyleClass().setAll(new String[]{"chart-horizontal-grid-lines"});
      this.verticalZeroLine.getStyleClass().setAll(new String[]{"chart-vertical-zero-line"});
      this.horizontalZeroLine.getStyleClass().setAll(new String[]{"chart-horizontal-zero-line"});
      this.plotContent.setManaged(false);
      this.plotArea.setManaged(false);
      this.animatedProperty().addListener((var1x, var2x, var3) -> {
         if (this.getXAxis() != null) {
            this.getXAxis().setAnimated(var3);
         }

         if (this.getYAxis() != null) {
            this.getYAxis().setAnimated(var3);
         }
      });
      this.setLegend(this.legend);
   }

   final int getDataSize() {
      ObservableList var1 = this.getData();
      return var1 != null ? var1.size() : 0;
   }

   private void seriesNameChanged() {
      this.updateLegend();
      this.requestChartLayout();
   }

   private void dataItemsChanged(XYChart.Series<X, Y> var1, List<XYChart.Data<X, Y>> var2, int var3, int var4, boolean var5) {
      for (XYChart.Data var7 : var2) {
         this.dataItemRemoved(var7, var1);
      }

      for (int var8 = var3; var8 < var4; var8++) {
         XYChart.Data var9 = (XYChart.Data)var1.getData().get(var8);
         this.dataItemAdded(var1, var8, var9);
      }

      this.invalidateRange();
      this.requestChartLayout();
   }

   private <T> void dataValueChanged(XYChart.Data<X, Y> var1, T var2, ObjectProperty<T> var3) {
      if (var3.get() != var2) {
         this.invalidateRange();
      }

      this.dataItemChanged(var1);
      if (this.shouldAnimate()) {
         this.animate(
            new KeyFrame[]{
               new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(var3, var3.get())}),
               new KeyFrame(Duration.millis(700.0), new KeyValue[]{new KeyValue(var3, var2, Interpolator.EASE_BOTH)})
            }
         );
      } else {
         var3.set(var2);
         this.requestChartLayout();
      }
   }

   protected void updateLegend() {
      ArrayList var1 = new ArrayList();
      if (this.getData() != null) {
         for (int var2 = 0; var2 < this.getData().size(); var2++) {
            XYChart.Series var3 = (XYChart.Series)this.getData().get(var2);
            var1.add(this.createLegendItemForSeries(var3, var2));
         }
      }

      this.legend.getItems().setAll(var1);
      if (var1.size() > 0) {
         if (this.getLegend() == null) {
            this.setLegend(this.legend);
         }
      } else {
         this.setLegend(null);
      }
   }

   Legend.LegendItem createLegendItemForSeries(XYChart.Series<X, Y> var1, int var2) {
      return new Legend.LegendItem(var1.getName());
   }

   void seriesBeingRemovedIsAdded(XYChart.Series<X, Y> var1) {
   }

   void dataBeingRemovedIsAdded(XYChart.Data<X, Y> var1, XYChart.Series<X, Y> var2) {
   }

   protected abstract void dataItemAdded(XYChart.Series<X, Y> var1, int var2, XYChart.Data<X, Y> var3);

   protected abstract void dataItemRemoved(XYChart.Data<X, Y> var1, XYChart.Series<X, Y> var2);

   protected abstract void dataItemChanged(XYChart.Data<X, Y> var1);

   protected abstract void seriesAdded(XYChart.Series<X, Y> var1, int var2);

   protected abstract void seriesRemoved(XYChart.Series<X, Y> var1);

   protected void seriesChanged(Change<? extends XYChart.Series> var1) {
   }

   private void invalidateRange() {
      this.rangeValid = false;
   }

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

         if (var3 != null) {
            var1.invalidateRange(var3);
         }

         if (var4 != null) {
            var2.invalidateRange(var4);
         }
      }
   }

   protected abstract void layoutPlotChildren();

   @Override
   protected final void layoutChartChildren(double var1, double var3, double var5, double var7) {
      if (this.getData() != null) {
         if (!this.rangeValid) {
            this.rangeValid = true;
            if (this.getData() != null) {
               this.updateAxisRange();
            }
         }

         var1 = this.snapPositionY(var1);
         var3 = this.snapPositionX(var3);
         Axis var9 = this.getXAxis();
         ObservableList var10 = var9.getTickMarks();
         Axis var11 = this.getYAxis();
         ObservableList var12 = var11.getTickMarks();
         if (var9 != null && var11 != null) {
            double var13 = 0.0;
            double var15 = 30.0;
            double var17 = 0.0;
            double var19 = 0.0;

            for (int var21 = 0; var21 < 5; var21++) {
               var19 = this.snapSizeY(var7 - var15);
               if (var19 < 0.0) {
                  var19 = 0.0;
               }

               var17 = var11.prefWidth(var19);
               var13 = this.snapSizeX(var5 - var17);
               if (var13 < 0.0) {
                  var13 = 0.0;
               }

               double var22 = var9.prefHeight(var13);
               if (var22 == var15) {
                  break;
               }

               var15 = var22;
            }

            var13 = Math.ceil(var13);
            var15 = Math.ceil(var15);
            var17 = Math.ceil(var17);
            var19 = Math.ceil(var19);
            double var42 = 0.0;
            switch (var9.getEffectiveSide()) {
               case TOP:
                  var9.setVisible(true);
                  var42 = var1 + 1.0;
                  var1 += var15;
                  break;
               case BOTTOM:
                  var9.setVisible(true);
                  var42 = var1 + var19;
            }

            double var23 = 0.0;
            switch (var11.getEffectiveSide()) {
               case LEFT:
                  var11.setVisible(true);
                  var23 = var3 + 1.0;
                  var3 += var17;
                  break;
               case RIGHT:
                  var11.setVisible(true);
                  var23 = var3 + var13;
            }

            var9.resizeRelocate(var3, var42, var13, var15);
            var11.resizeRelocate(var23, var1, var17, var19);
            var9.requestAxisLayout();
            var9.layout();
            var11.requestAxisLayout();
            var11.layout();
            this.layoutPlotChildren();
            double var25 = var9.getZeroPosition();
            double var27 = var11.getZeroPosition();
            if (!Double.isNaN(var25) && this.isVerticalZeroLineVisible()) {
               this.verticalZeroLine.setStartX(var3 + var25 + 0.5);
               this.verticalZeroLine.setStartY(var1);
               this.verticalZeroLine.setEndX(var3 + var25 + 0.5);
               this.verticalZeroLine.setEndY(var1 + var19);
               this.verticalZeroLine.setVisible(true);
            } else {
               this.verticalZeroLine.setVisible(false);
            }

            if (!Double.isNaN(var27) && this.isHorizontalZeroLineVisible()) {
               this.horizontalZeroLine.setStartX(var3);
               this.horizontalZeroLine.setStartY(var1 + var27 + 0.5);
               this.horizontalZeroLine.setEndX(var3 + var13);
               this.horizontalZeroLine.setEndY(var1 + var27 + 0.5);
               this.horizontalZeroLine.setVisible(true);
            } else {
               this.horizontalZeroLine.setVisible(false);
            }

            this.plotBackground.resizeRelocate(var3, var1, var13, var19);
            this.plotAreaClip.setX(var3);
            this.plotAreaClip.setY(var1);
            this.plotAreaClip.setWidth(var13 + 1.0);
            this.plotAreaClip.setHeight(var19 + 1.0);
            this.plotContent.setLayoutX(var3);
            this.plotContent.setLayoutY(var1);
            this.plotContent.requestLayout();
            this.verticalGridLines.getElements().clear();
            if (this.getVerticalGridLinesVisible()) {
               for (int var29 = 0; var29 < var10.size(); var29++) {
                  Axis.TickMark var30 = (Axis.TickMark)var10.get(var29);
                  double var31 = var9.getDisplayPosition(var30.getValue());
                  if ((var31 != var25 || !this.isVerticalZeroLineVisible()) && var31 > 0.0 && var31 <= var13) {
                     this.verticalGridLines.getElements().add(new MoveTo(var3 + var31 + 0.5, var1));
                     this.verticalGridLines.getElements().add(new LineTo(var3 + var31 + 0.5, var1 + var19));
                  }
               }
            }

            this.horizontalGridLines.getElements().clear();
            if (this.isHorizontalGridLinesVisible()) {
               for (int var43 = 0; var43 < var12.size(); var43++) {
                  Axis.TickMark var46 = (Axis.TickMark)var12.get(var43);
                  double var49 = var11.getDisplayPosition(var46.getValue());
                  if ((var49 != var27 || !this.isHorizontalZeroLineVisible()) && var49 >= 0.0 && var49 < var19) {
                     this.horizontalGridLines.getElements().add(new MoveTo(var3, var1 + var49 + 0.5));
                     this.horizontalGridLines.getElements().add(new LineTo(var3 + var13, var1 + var49 + 0.5));
                  }
               }
            }

            this.verticalRowFill.getElements().clear();
            if (this.isAlternativeColumnFillVisible()) {
               ArrayList var44 = new ArrayList();
               ArrayList var47 = new ArrayList();

               for (int var50 = 0; var50 < var10.size(); var50++) {
                  double var32 = var9.getDisplayPosition(((Axis.TickMark)var10.get(var50)).getValue());
                  if (var32 == var25) {
                     var44.add(var32);
                     var47.add(var32);
                  } else if (var32 < var25) {
                     var44.add(var32);
                  } else {
                     var47.add(var32);
                  }
               }

               Collections.sort(var44);
               Collections.sort(var47);

               for (byte var51 = 1; var51 < var44.size(); var51 += 2) {
                  if (var51 + 1 < var44.size()) {
                     double var56 = (Double)var44.get(var51);
                     double var34 = (Double)var44.get(var51 + 1);
                     this.verticalRowFill
                        .getElements()
                        .addAll(
                           new PathElement[]{
                              new MoveTo(var3 + var56, var1),
                              new LineTo(var3 + var56, var1 + var19),
                              new LineTo(var3 + var34, var1 + var19),
                              new LineTo(var3 + var34, var1),
                              new ClosePath()
                           }
                        );
                  }
               }

               for (byte var52 = 0; var52 < var47.size(); var52 += 2) {
                  if (var52 + 1 < var47.size()) {
                     double var57 = (Double)var47.get(var52);
                     double var61 = (Double)var47.get(var52 + 1);
                     this.verticalRowFill
                        .getElements()
                        .addAll(
                           new PathElement[]{
                              new MoveTo(var3 + var57, var1),
                              new LineTo(var3 + var57, var1 + var19),
                              new LineTo(var3 + var61, var1 + var19),
                              new LineTo(var3 + var61, var1),
                              new ClosePath()
                           }
                        );
                  }
               }
            }

            this.horizontalRowFill.getElements().clear();
            if (this.isAlternativeRowFillVisible()) {
               ArrayList var45 = new ArrayList();
               ArrayList var48 = new ArrayList();

               for (int var53 = 0; var53 < var12.size(); var53++) {
                  double var58 = var11.getDisplayPosition(((Axis.TickMark)var12.get(var53)).getValue());
                  if (var58 == var27) {
                     var45.add(var58);
                     var48.add(var58);
                  } else if (var58 < var27) {
                     var45.add(var58);
                  } else {
                     var48.add(var58);
                  }
               }

               Collections.sort(var45);
               Collections.sort(var48);

               for (byte var54 = 1; var54 < var45.size(); var54 += 2) {
                  if (var54 + 1 < var45.size()) {
                     double var59 = (Double)var45.get(var54);
                     double var62 = (Double)var45.get(var54 + 1);
                     this.horizontalRowFill
                        .getElements()
                        .addAll(
                           new PathElement[]{
                              new MoveTo(var3, var1 + var59),
                              new LineTo(var3 + var13, var1 + var59),
                              new LineTo(var3 + var13, var1 + var62),
                              new LineTo(var3, var1 + var62),
                              new ClosePath()
                           }
                        );
                  }
               }

               for (byte var55 = 0; var55 < var48.size(); var55 += 2) {
                  if (var55 + 1 < var48.size()) {
                     double var60 = (Double)var48.get(var55);
                     double var63 = (Double)var48.get(var55 + 1);
                     this.horizontalRowFill
                        .getElements()
                        .addAll(
                           new PathElement[]{
                              new MoveTo(var3, var1 + var60),
                              new LineTo(var3 + var13, var1 + var60),
                              new LineTo(var3 + var13, var1 + var63),
                              new LineTo(var3, var1 + var63),
                              new ClosePath()
                           }
                        );
                  }
               }
            }
         }
      }
   }

   int getSeriesIndex(XYChart.Series<X, Y> var1) {
      return this.displayedSeries.indexOf(var1);
   }

   int getSeriesSize() {
      return this.displayedSeries.size();
   }

   protected final void removeSeriesFromDisplay(XYChart.Series<X, Y> var1) {
      if (var1 != null) {
         var1.setToRemove = false;
      }

      var1.setChart(null);
      this.displayedSeries.remove(var1);
      int var2 = this.seriesColorMap.remove(var1);
      this.colorBits.clear(var2);
   }

   protected final Iterator<XYChart.Series<X, Y>> getDisplayedSeriesIterator() {
      return Collections.unmodifiableList(this.displayedSeries).iterator();
   }

   final KeyFrame[] createSeriesRemoveTimeLine(XYChart.Series<X, Y> var1, long var2) {
      ArrayList var4 = new ArrayList();
      var4.add(var1.getNode());

      for (XYChart.Data var6 : var1.getData()) {
         if (var6.getNode() != null) {
            var4.add(var6.getNode());
         }
      }

      KeyValue[] var8 = new KeyValue[var4.size()];
      KeyValue[] var9 = new KeyValue[var4.size()];

      for (int var7 = 0; var7 < var4.size(); var7++) {
         var8[var7] = new KeyValue(((Node)var4.get(var7)).opacityProperty(), 1);
         var9[var7] = new KeyValue(((Node)var4.get(var7)).opacityProperty(), 0);
      }

      return new KeyFrame[]{new KeyFrame(Duration.ZERO, var8), new KeyFrame(Duration.millis((double)var2), var3 -> {
         this.getPlotChildren().removeAll(var4);
         this.removeSeriesFromDisplay(var1);
      }, var9)};
   }

   protected final X getCurrentDisplayedXValue(XYChart.Data<X, Y> var1) {
      return (X)var1.getCurrentX();
   }

   protected final void setCurrentDisplayedXValue(XYChart.Data<X, Y> var1, X var2) {
      var1.setCurrentX(var2);
   }

   protected final ObjectProperty<X> currentDisplayedXValueProperty(XYChart.Data<X, Y> var1) {
      return var1.currentXProperty();
   }

   protected final Y getCurrentDisplayedYValue(XYChart.Data<X, Y> var1) {
      return (Y)var1.getCurrentY();
   }

   protected final void setCurrentDisplayedYValue(XYChart.Data<X, Y> var1, Y var2) {
      var1.setCurrentY(var2);
   }

   protected final ObjectProperty<Y> currentDisplayedYValueProperty(XYChart.Data<X, Y> var1) {
      return var1.currentYProperty();
   }

   protected final Object getCurrentDisplayedExtraValue(XYChart.Data<X, Y> var1) {
      return var1.getCurrentExtraValue();
   }

   protected final void setCurrentDisplayedExtraValue(XYChart.Data<X, Y> var1, Object var2) {
      var1.setCurrentExtraValue(var2);
   }

   protected final ObjectProperty<Object> currentDisplayedExtraValueProperty(XYChart.Data<X, Y> var1) {
      return var1.currentExtraValueProperty();
   }

   protected final Iterator<XYChart.Data<X, Y>> getDisplayedDataIterator(XYChart.Series<X, Y> var1) {
      return Collections.unmodifiableList(var1.displayedData).iterator();
   }

   protected final void removeDataItemFromDisplay(XYChart.Series<X, Y> var1, XYChart.Data<X, Y> var2) {
      var1.removeDataItemRef(var2);
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return XYChart.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   public static final class Data<X, Y> {
      private boolean setToRemove = false;
      private XYChart.Series<X, Y> series;
      private ObjectProperty<X> xValue = new SimpleObjectProperty<X>(this, "XValue") {
         protected void invalidated() {
            if (Data.this.series != null) {
               XYChart var1 = Data.this.series.getChart();
               if (var1 != null) {
                  var1.dataValueChanged(Data.this, this.get(), Data.this.currentXProperty());
               }
            } else {
               Data.this.setCurrentX(this.get());
            }
         }
      };
      private ObjectProperty<Y> yValue = new SimpleObjectProperty<Y>(this, "YValue") {
         protected void invalidated() {
            if (Data.this.series != null) {
               XYChart var1 = Data.this.series.getChart();
               if (var1 != null) {
                  var1.dataValueChanged(Data.this, this.get(), Data.this.currentYProperty());
               }
            } else {
               Data.this.setCurrentY(this.get());
            }
         }
      };
      private ObjectProperty<Object> extraValue = new SimpleObjectProperty<Object>(this, "extraValue") {
         protected void invalidated() {
            if (Data.this.series != null) {
               XYChart var1 = Data.this.series.getChart();
               if (var1 != null) {
                  var1.dataValueChanged(Data.this, this.get(), Data.this.currentExtraValueProperty());
               }
            }
         }
      };
      private ObjectProperty<Node> node = new SimpleObjectProperty<Node>(this, "node") {
         protected void invalidated() {
            Node var1 = (Node)this.get();
            if (var1 != null) {
               var1.accessibleTextProperty().unbind();
               var1.accessibleTextProperty().bind(new StringBinding() {
                  {
                     this.bind(new Observable[]{Data.this.currentXProperty(), Data.this.currentYProperty()});
                  }

                  protected String computeValue() {
                     String var1 = Data.this.series != null ? Data.this.series.getName() : "";
                     return var1 + " X Axis is " + Data.this.getCurrentX() + " Y Axis is " + Data.this.getCurrentY();
                  }
               });
            }
         }
      };
      private ObjectProperty<X> currentX = new SimpleObjectProperty(this, "currentX");
      private ObjectProperty<Y> currentY = new SimpleObjectProperty(this, "currentY");
      private ObjectProperty<Object> currentExtraValue = new SimpleObjectProperty(this, "currentExtraValue");

      void setSeries(XYChart.Series<X, Y> var1) {
         this.series = var1;
      }

      public final X getXValue() {
         return (X)this.xValue.get();
      }

      public final void setXValue(X var1) {
         this.xValue.set(var1);
         if (this.currentX.get() == null || this.series != null && this.series.getChart() == null) {
            this.currentX.setValue(var1);
         }
      }

      public final ObjectProperty<X> XValueProperty() {
         return this.xValue;
      }

      public final Y getYValue() {
         return (Y)this.yValue.get();
      }

      public final void setYValue(Y var1) {
         this.yValue.set(var1);
         if (this.currentY.get() == null || this.series != null && this.series.getChart() == null) {
            this.currentY.setValue(var1);
         }
      }

      public final ObjectProperty<Y> YValueProperty() {
         return this.yValue;
      }

      public final Object getExtraValue() {
         return this.extraValue.get();
      }

      public final void setExtraValue(Object var1) {
         this.extraValue.set(var1);
      }

      public final ObjectProperty<Object> extraValueProperty() {
         return this.extraValue;
      }

      public final Node getNode() {
         return (Node)this.node.get();
      }

      public final void setNode(Node var1) {
         this.node.set(var1);
      }

      public final ObjectProperty<Node> nodeProperty() {
         return this.node;
      }

      final X getCurrentX() {
         return (X)this.currentX.get();
      }

      final void setCurrentX(X var1) {
         this.currentX.set(var1);
      }

      final ObjectProperty<X> currentXProperty() {
         return this.currentX;
      }

      final Y getCurrentY() {
         return (Y)this.currentY.get();
      }

      final void setCurrentY(Y var1) {
         this.currentY.set(var1);
      }

      final ObjectProperty<Y> currentYProperty() {
         return this.currentY;
      }

      final Object getCurrentExtraValue() {
         return this.currentExtraValue.getValue();
      }

      final void setCurrentExtraValue(Object var1) {
         this.currentExtraValue.setValue(var1);
      }

      final ObjectProperty<Object> currentExtraValueProperty() {
         return this.currentExtraValue;
      }

      public Data() {
      }

      public Data(X var1, Y var2) {
         this.setXValue((X)var1);
         this.setYValue((Y)var2);
         this.setCurrentX((X)var1);
         this.setCurrentY((Y)var2);
      }

      public Data(X var1, Y var2, Object var3) {
         this.setXValue((X)var1);
         this.setYValue((Y)var2);
         this.setExtraValue(var3);
         this.setCurrentX((X)var1);
         this.setCurrentY((Y)var2);
         this.setCurrentExtraValue(var3);
      }

      @Override
      public String toString() {
         return "Data[" + this.getXValue() + "," + this.getYValue() + "," + this.getExtraValue() + "]";
      }
   }

   public static final class Series<X, Y> {
      String defaultColorStyleClass;
      boolean setToRemove = false;
      private List<XYChart.Data<X, Y>> displayedData = new ArrayList<>();
      private final ListChangeListener<XYChart.Data<X, Y>> dataChangeListener = new ListChangeListener<XYChart.Data<X, Y>>() {
         public void onChanged(Change<? extends XYChart.Data<X, Y>> var1) {
            ObservableList var2 = var1.getList();
            XYChart var3 = Series.this.getChart();

            while (var1.next()) {
               if (var3 != null) {
                  if (var1.wasPermutated()) {
                     Series.this.displayedData.sort((var1x, var2x) -> var2.indexOf(var2x) - var2.indexOf(var1x));
                     return;
                  }

                  HashSet var7 = new HashSet<>(Series.this.displayedData);
                  var7.removeAll(var1.getRemoved());

                  for (XYChart.Data var14 : var1.getAddedSubList()) {
                     if (!var7.add(var14)) {
                        throw new IllegalArgumentException("Duplicate data added");
                     }
                  }

                  for (XYChart.Data var15 : var1.getRemoved()) {
                     var15.setToRemove = true;
                  }

                  if (var1.getAddedSize() > 0) {
                     for (XYChart.Data var16 : var1.getAddedSubList()) {
                        if (var16.setToRemove) {
                           if (var3 != null) {
                              var3.dataBeingRemovedIsAdded(var16, Series.this);
                           }

                           var16.setToRemove = false;
                        }
                     }

                     for (XYChart.Data var17 : var1.getAddedSubList()) {
                        var17.setSeries(Series.this);
                     }

                     if (var1.getFrom() == 0) {
                        Series.this.displayedData.addAll(0, var1.getAddedSubList());
                     } else {
                        Series.this.displayedData.addAll(Series.this.displayedData.indexOf(var2.get(var1.getFrom() - 1)) + 1, var1.getAddedSubList());
                     }
                  }

                  var3.dataItemsChanged(Series.this, var1.getRemoved(), var1.getFrom(), var1.getTo(), var1.wasPermutated());
               } else {
                  HashSet var4 = new HashSet();

                  for (XYChart.Data var6 : var2) {
                     if (!var4.add(var6)) {
                        throw new IllegalArgumentException("Duplicate data added");
                     }
                  }

                  for (XYChart.Data var13 : var1.getAddedSubList()) {
                     var13.setSeries(Series.this);
                  }
               }
            }
         }
      };
      private final ReadOnlyObjectWrapper<XYChart<X, Y>> chart = new ReadOnlyObjectWrapper<XYChart<X, Y>>(this, "chart") {
         protected void invalidated() {
            if (this.get() == null) {
               Series.this.displayedData.clear();
            } else {
               Series.this.displayedData.addAll(Series.this.getData());
            }
         }
      };
      private final StringProperty name = new StringPropertyBase() {
         protected void invalidated() {
            this.get();
            if (Series.this.getChart() != null) {
               Series.this.getChart().seriesNameChanged();
            }
         }

         public Object getBean() {
            return Series.this;
         }

         public String getName() {
            return "name";
         }
      };
      private ObjectProperty<Node> node = new SimpleObjectProperty(this, "node");
      private final ObjectProperty<ObservableList<XYChart.Data<X, Y>>> data = new ObjectPropertyBase<ObservableList<XYChart.Data<X, Y>>>() {
         private ObservableList<XYChart.Data<X, Y>> old;

         protected void invalidated() {
            ObservableList var1 = (ObservableList)this.getValue();
            if (this.old != null) {
               this.old.removeListener(Series.this.dataChangeListener);
            }

            if (var1 != null) {
               var1.addListener(Series.this.dataChangeListener);
            }

            if (this.old == null && var1 == null) {
               if (this.old != null && this.old.size() > 0) {
                  Series.this.dataChangeListener.onChanged(new NonIterableChange<XYChart.Data<X, Y>>(0, 0, var1) {
                     public List<XYChart.Data<X, Y>> getRemoved() {
                        return old;
                     }

                     protected int[] getPermutation() {
                        return new int[0];
                     }
                  });
               }
            } else {
               final Object var2 = this.old != null ? this.old : Collections.emptyList();
               int var3 = var1 != null ? var1.size() : 0;
               if (var3 > 0 || !var2.isEmpty()) {
                  Series.this.dataChangeListener.onChanged(new NonIterableChange<XYChart.Data<X, Y>>(0, var3, var1) {
                     public List<XYChart.Data<X, Y>> getRemoved() {
                        return (List<XYChart.Data<X, Y>>)var2;
                     }

                     protected int[] getPermutation() {
                        return new int[0];
                     }
                  });
               }
            }

            this.old = var1;
         }

         public Object getBean() {
            return Series.this;
         }

         public String getName() {
            return "data";
         }
      };

      public final XYChart<X, Y> getChart() {
         return (XYChart<X, Y>)this.chart.get();
      }

      private void setChart(XYChart<X, Y> var1) {
         this.chart.set(var1);
      }

      public final ReadOnlyObjectProperty<XYChart<X, Y>> chartProperty() {
         return this.chart.getReadOnlyProperty();
      }

      public final String getName() {
         return (String)this.name.get();
      }

      public final void setName(String var1) {
         this.name.set(var1);
      }

      public final StringProperty nameProperty() {
         return this.name;
      }

      public final Node getNode() {
         return (Node)this.node.get();
      }

      public final void setNode(Node var1) {
         this.node.set(var1);
      }

      public final ObjectProperty<Node> nodeProperty() {
         return this.node;
      }

      public final ObservableList<XYChart.Data<X, Y>> getData() {
         return (ObservableList<XYChart.Data<X, Y>>)this.data.getValue();
      }

      public final void setData(ObservableList<XYChart.Data<X, Y>> var1) {
         this.data.setValue(var1);
      }

      public final ObjectProperty<ObservableList<XYChart.Data<X, Y>>> dataProperty() {
         return this.data;
      }

      public Series() {
         this(FXCollections.observableArrayList());
      }

      public Series(ObservableList<XYChart.Data<X, Y>> var1) {
         this.setData(var1);

         for (XYChart.Data var3 : var1) {
            var3.setSeries(this);
         }
      }

      public Series(String var1, ObservableList<XYChart.Data<X, Y>> var2) {
         this(var2);
         this.setName(var1);
      }

      @Override
      public String toString() {
         return "Series[" + this.getName() + "]";
      }

      private void removeDataItemRef(XYChart.Data<X, Y> var1) {
         if (var1 != null) {
            var1.setToRemove = false;
         }

         this.displayedData.remove(var1);
      }

      int getItemIndex(XYChart.Data<X, Y> var1) {
         return this.displayedData.indexOf(var1);
      }

      XYChart.Data<X, Y> getItem(int var1) {
         return this.displayedData.get(var1);
      }

      int getDataSize() {
         return this.displayedData.size();
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<XYChart<?, ?>, Boolean> HORIZONTAL_GRID_LINE_VISIBLE = new CssMetaData<XYChart<?, ?>, Boolean>(
         "-fx-horizontal-grid-lines-visible", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(XYChart<?, ?> var1) {
            return var1.horizontalGridLinesVisible == null || !var1.horizontalGridLinesVisible.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(XYChart<?, ?> var1) {
            return (StyleableProperty<Boolean>)var1.horizontalGridLinesVisibleProperty();
         }
      };
      private static final CssMetaData<XYChart<?, ?>, Boolean> HORIZONTAL_ZERO_LINE_VISIBLE = new CssMetaData<XYChart<?, ?>, Boolean>(
         "-fx-horizontal-zero-line-visible", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(XYChart<?, ?> var1) {
            return var1.horizontalZeroLineVisible == null || !var1.horizontalZeroLineVisible.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(XYChart<?, ?> var1) {
            return (StyleableProperty<Boolean>)var1.horizontalZeroLineVisibleProperty();
         }
      };
      private static final CssMetaData<XYChart<?, ?>, Boolean> ALTERNATIVE_ROW_FILL_VISIBLE = new CssMetaData<XYChart<?, ?>, Boolean>(
         "-fx-alternative-row-fill-visible", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(XYChart<?, ?> var1) {
            return var1.alternativeRowFillVisible == null || !var1.alternativeRowFillVisible.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(XYChart<?, ?> var1) {
            return (StyleableProperty<Boolean>)var1.alternativeRowFillVisibleProperty();
         }
      };
      private static final CssMetaData<XYChart<?, ?>, Boolean> VERTICAL_GRID_LINE_VISIBLE = new CssMetaData<XYChart<?, ?>, Boolean>(
         "-fx-vertical-grid-lines-visible", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(XYChart<?, ?> var1) {
            return var1.verticalGridLinesVisible == null || !var1.verticalGridLinesVisible.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(XYChart<?, ?> var1) {
            return (StyleableProperty<Boolean>)var1.verticalGridLinesVisibleProperty();
         }
      };
      private static final CssMetaData<XYChart<?, ?>, Boolean> VERTICAL_ZERO_LINE_VISIBLE = new CssMetaData<XYChart<?, ?>, Boolean>(
         "-fx-vertical-zero-line-visible", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(XYChart<?, ?> var1) {
            return var1.verticalZeroLineVisible == null || !var1.verticalZeroLineVisible.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(XYChart<?, ?> var1) {
            return (StyleableProperty<Boolean>)var1.verticalZeroLineVisibleProperty();
         }
      };
      private static final CssMetaData<XYChart<?, ?>, Boolean> ALTERNATIVE_COLUMN_FILL_VISIBLE = new CssMetaData<XYChart<?, ?>, Boolean>(
         "-fx-alternative-column-fill-visible", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(XYChart<?, ?> var1) {
            return var1.alternativeColumnFillVisible == null || !var1.alternativeColumnFillVisible.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(XYChart<?, ?> var1) {
            return (StyleableProperty<Boolean>)var1.alternativeColumnFillVisibleProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Chart.getClassCssMetaData());
         var0.add(HORIZONTAL_GRID_LINE_VISIBLE);
         var0.add(HORIZONTAL_ZERO_LINE_VISIBLE);
         var0.add(ALTERNATIVE_ROW_FILL_VISIBLE);
         var0.add(VERTICAL_GRID_LINE_VISIBLE);
         var0.add(VERTICAL_ZERO_LINE_VISIBLE);
         var0.add(ALTERNATIVE_COLUMN_FILL_VISIBLE);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
