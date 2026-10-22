package javafx.scene.chart;

import com.sun.javafx.charts.Legend;
import com.sun.javafx.collections.NonIterableChange;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.SizeConverter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Side;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

public class PieChart extends Chart {
   private static final int MIN_PIE_RADIUS = 25;
   private static final double LABEL_TICK_GAP = 6.0;
   private static final double LABEL_BALL_RADIUS = 2.0;
   private BitSet colorBits = new BitSet(8);
   private double pieRadius;
   private PieChart.Data begin = null;
   private final Path labelLinePath = new Path() {
      public boolean usesMirroring() {
         return false;
      }
   };
   private List<PieChart.LabelLayoutInfo> labelLayoutInfos = null;
   private Legend legend = new Legend();
   private PieChart.Data dataItemBeingRemoved = null;
   private Timeline dataRemoveTimeline = null;
   private final ListChangeListener<PieChart.Data> dataChangeListener = var1x -> {
      while (var1x.next()) {
         if (var1x.wasPermutated()) {
            PieChart.Data var9 = this.begin;

            for (int var13 = 0; var13 < this.getData().size(); var13++) {
               PieChart.Data var14 = (PieChart.Data)this.getData().get(var13);
               this.updateDataItemStyleClass(var14, var13);
               if (var13 == 0) {
                  this.begin = var14;
                  var9 = this.begin;
                  this.begin.next = null;
               } else {
                  var9.next = var14;
                  var14.next = null;
                  var9 = var14;
               }
            }

            this.updateLegend();
            this.requestChartLayout();
            return;
         }

         for (int var2 = var1x.getFrom(); var2 < var1x.getTo(); var2++) {
            PieChart.Data var3 = (PieChart.Data)this.getData().get(var2);
            var3.setChart(this);
            if (this.begin == null) {
               this.begin = var3;
               this.begin.next = null;
            } else if (var2 == 0) {
               var3.next = this.begin;
               this.begin = var3;
            } else {
               PieChart.Data var4 = this.begin;

               for (int var5 = 0; var5 < var2 - 1; var5++) {
                  var4 = var4.next;
               }

               var3.next = var4.next;
               var4.next = var3;
            }
         }

         for (PieChart.Data var10 : var1x.getRemoved()) {
            this.dataItemRemoved(var10);
         }

         for (int var7 = var1x.getFrom(); var7 < var1x.getTo(); var7++) {
            PieChart.Data var11 = (PieChart.Data)this.getData().get(var7);
            var11.defaultColorIndex = this.colorBits.nextClearBit(0);
            this.colorBits.set(var11.defaultColorIndex);
            this.dataItemAdded(var11, var7);
         }

         if (var1x.wasRemoved() || var1x.wasAdded()) {
            for (int var8 = 0; var8 < this.getData().size(); var8++) {
               PieChart.Data var12 = (PieChart.Data)this.getData().get(var8);
               this.updateDataItemStyleClass(var12, var8);
            }

            this.updateLegend();
         }
      }

      this.requestChartLayout();
   };
   private ObjectProperty<ObservableList<PieChart.Data>> data = new ObjectPropertyBase<ObservableList<PieChart.Data>>() {
      private ObservableList<PieChart.Data> old;

      protected void invalidated() {
         ObservableList var1 = (ObservableList)this.getValue();
         if (this.old != null) {
            this.old.removeListener(PieChart.this.dataChangeListener);
         }

         if (var1 != null) {
            var1.addListener(PieChart.this.dataChangeListener);
         }

         if (this.old == null && var1 == null) {
            if (this.old != null && this.old.size() > 0) {
               PieChart.this.dataChangeListener.onChanged(new NonIterableChange<PieChart.Data>(0, 0, var1) {
                  public List<PieChart.Data> getRemoved() {
                     return old;
                  }

                  public boolean wasPermutated() {
                     return false;
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
               PieChart.this.dataChangeListener.onChanged(new NonIterableChange<PieChart.Data>(0, var3, var1) {
                  public List<PieChart.Data> getRemoved() {
                     return (List<PieChart.Data>)var2;
                  }

                  public boolean wasPermutated() {
                     return false;
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
         return PieChart.this;
      }

      public String getName() {
         return "data";
      }
   };
   private DoubleProperty startAngle = new StyleableDoubleProperty(0.0) {
      public void invalidated() {
         this.get();
         PieChart.this.requestChartLayout();
      }

      public Object getBean() {
         return PieChart.this;
      }

      public String getName() {
         return "startAngle";
      }

      public CssMetaData<PieChart, Number> getCssMetaData() {
         return PieChart.StyleableProperties.START_ANGLE;
      }
   };
   private BooleanProperty clockwise = new StyleableBooleanProperty(true) {
      public void invalidated() {
         this.get();
         PieChart.this.requestChartLayout();
      }

      public Object getBean() {
         return PieChart.this;
      }

      public String getName() {
         return "clockwise";
      }

      public CssMetaData<PieChart, Boolean> getCssMetaData() {
         return PieChart.StyleableProperties.CLOCKWISE;
      }
   };
   private DoubleProperty labelLineLength = new StyleableDoubleProperty(20.0) {
      public void invalidated() {
         this.get();
         PieChart.this.requestChartLayout();
      }

      public Object getBean() {
         return PieChart.this;
      }

      public String getName() {
         return "labelLineLength";
      }

      public CssMetaData<PieChart, Number> getCssMetaData() {
         return PieChart.StyleableProperties.LABEL_LINE_LENGTH;
      }
   };
   private BooleanProperty labelsVisible = new StyleableBooleanProperty(true) {
      public void invalidated() {
         this.get();
         PieChart.this.requestChartLayout();
      }

      public Object getBean() {
         return PieChart.this;
      }

      public String getName() {
         return "labelsVisible";
      }

      public CssMetaData<PieChart, Boolean> getCssMetaData() {
         return PieChart.StyleableProperties.LABELS_VISIBLE;
      }
   };

   public final ObservableList<PieChart.Data> getData() {
      return (ObservableList<PieChart.Data>)this.data.getValue();
   }

   public final void setData(ObservableList<PieChart.Data> var1) {
      this.data.setValue(var1);
   }

   public final ObjectProperty<ObservableList<PieChart.Data>> dataProperty() {
      return this.data;
   }

   public final double getStartAngle() {
      return this.startAngle.getValue();
   }

   public final void setStartAngle(double var1) {
      this.startAngle.setValue(var1);
   }

   public final DoubleProperty startAngleProperty() {
      return this.startAngle;
   }

   public final void setClockwise(boolean var1) {
      this.clockwise.setValue(var1);
   }

   public final boolean isClockwise() {
      return this.clockwise.getValue();
   }

   public final BooleanProperty clockwiseProperty() {
      return this.clockwise;
   }

   public final double getLabelLineLength() {
      return this.labelLineLength.getValue();
   }

   public final void setLabelLineLength(double var1) {
      this.labelLineLength.setValue(var1);
   }

   public final DoubleProperty labelLineLengthProperty() {
      return this.labelLineLength;
   }

   public final void setLabelsVisible(boolean var1) {
      this.labelsVisible.setValue(var1);
   }

   public final boolean getLabelsVisible() {
      return this.labelsVisible.getValue();
   }

   public final BooleanProperty labelsVisibleProperty() {
      return this.labelsVisible;
   }

   public PieChart() {
      this(FXCollections.observableArrayList());
   }

   public PieChart(ObservableList<PieChart.Data> var1) {
      this.getChartChildren().add(this.labelLinePath);
      this.labelLinePath.getStyleClass().add("chart-pie-label-line");
      this.setLegend(this.legend);
      this.setData(var1);
      this.useChartContentMirroring = false;
   }

   private void dataNameChanged(PieChart.Data var1) {
      var1.textNode.setText(var1.getName());
      this.requestChartLayout();
      this.updateLegend();
   }

   private void dataPieValueChanged(PieChart.Data var1) {
      if (this.shouldAnimate()) {
         this.animate(
            new KeyFrame[]{
               new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(var1.currentPieValueProperty(), var1.getCurrentPieValue())}),
               new KeyFrame(Duration.millis(500.0), new KeyValue[]{new KeyValue(var1.currentPieValueProperty(), var1.getPieValue(), Interpolator.EASE_BOTH)})
            }
         );
      } else {
         var1.setCurrentPieValue(var1.getPieValue());
         this.requestChartLayout();
      }
   }

   private Node createArcRegion(PieChart.Data var1) {
      Object var2 = var1.getNode();
      if (var2 == null) {
         var2 = new Region();
         var2.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
         var2.setPickOnBounds(false);
         var1.setNode((Node)var2);
      }

      return (Node)var2;
   }

   private Text createPieLabel(PieChart.Data var1) {
      Text var2 = var1.textNode;
      var2.setText(var1.getName());
      return var2;
   }

   private void updateDataItemStyleClass(PieChart.Data var1, int var2) {
      Node var3 = var1.getNode();
      if (var3 != null) {
         var3.getStyleClass().setAll(new String[]{"chart-pie", "data" + var2, "default-color" + var1.defaultColorIndex % 8});
         if (var1.getPieValue() < 0.0) {
            var3.getStyleClass().add("negative");
         }
      }
   }

   private void dataItemAdded(PieChart.Data var1, int var2) {
      Node var3 = this.createArcRegion(var1);
      Text var4 = this.createPieLabel(var1);
      var1.getChart().getChartChildren().add(var3);
      if (this.shouldAnimate()) {
         if (this.dataRemoveTimeline != null && this.dataRemoveTimeline.getStatus().equals(Status.RUNNING) && this.dataItemBeingRemoved == var1) {
            this.dataRemoveTimeline.stop();
            this.dataRemoveTimeline = null;
            this.getChartChildren().remove(var1.textNode);
            this.getChartChildren().remove(var3);
            this.removeDataItemRef(var1);
         }

         this.animate(
            new KeyFrame[]{
               new KeyFrame(
                  Duration.ZERO,
                  new KeyValue[]{
                     new KeyValue(var1.currentPieValueProperty(), var1.getCurrentPieValue()),
                     new KeyValue(var1.radiusMultiplierProperty(), var1.getRadiusMultiplier())
                  }
               ),
               new KeyFrame(
                  Duration.millis(500.0),
                  var3x -> {
                     var4.setOpacity(0.0);
                     if (var1.getChart() == null) {
                        var1.setChart(this);
                     }

                     var1.getChart().getChartChildren().add(var4);
                     FadeTransition var4x = new FadeTransition(Duration.millis(150.0), var4);
                     var4x.setToValue(1.0);
                     var4x.play();
                  },
                  new KeyValue[]{
                     new KeyValue(var1.currentPieValueProperty(), var1.getPieValue(), Interpolator.EASE_BOTH),
                     new KeyValue(var1.radiusMultiplierProperty(), 1, Interpolator.EASE_BOTH)
                  }
               )
            }
         );
      } else {
         this.getChartChildren().add(var4);
         var1.setRadiusMultiplier(1.0);
         var1.setCurrentPieValue(var1.getPieValue());
      }

      for (int var5 = 0; var5 < this.getChartChildren().size(); var5++) {
         Node var6 = (Node)this.getChartChildren().get(var5);
         if (var6 instanceof Text) {
            var6.toFront();
         }
      }
   }

   private void removeDataItemRef(PieChart.Data var1) {
      if (this.begin == var1) {
         this.begin = var1.next;
      } else {
         PieChart.Data var2 = this.begin;

         while (var2 != null && var2.next != var1) {
            var2 = var2.next;
         }

         if (var2 != null) {
            var2.next = var1.next;
         }
      }
   }

   private Timeline createDataRemoveTimeline(PieChart.Data var1) {
      Node var2 = var1.getNode();
      Timeline var3 = new Timeline();
      var3.getKeyFrames()
         .addAll(
            new KeyFrame[]{
               new KeyFrame(
                  Duration.ZERO,
                  new KeyValue[]{
                     new KeyValue(var1.currentPieValueProperty(), var1.getCurrentPieValue()),
                     new KeyValue(var1.radiusMultiplierProperty(), var1.getRadiusMultiplier())
                  }
               ),
               new KeyFrame(Duration.millis(500.0), var3x -> {
                  this.colorBits.clear(var1.defaultColorIndex);
                  this.getChartChildren().remove(var2);
                  FadeTransition var4 = new FadeTransition(Duration.millis(150.0), var1.textNode);
                  var4.setFromValue(1.0);
                  var4.setToValue(0.0);
                  var4.setOnFinished(new EventHandler<ActionEvent>() {
                     public void handle(ActionEvent var1x) {
                        PieChart.this.getChartChildren().remove(var1.textNode);
                        var1.setChart(null);
                        PieChart.this.removeDataItemRef(var1);
                        var1.textNode.setOpacity(1.0);
                     }
                  });
                  var4.play();
               }, new KeyValue[]{new KeyValue(var1.currentPieValueProperty(), 0, Interpolator.EASE_BOTH), new KeyValue(var1.radiusMultiplierProperty(), 0)})
            }
         );
      return var3;
   }

   private void dataItemRemoved(PieChart.Data var1) {
      Node var2 = var1.getNode();
      if (this.shouldAnimate()) {
         this.dataRemoveTimeline = this.createDataRemoveTimeline(var1);
         this.dataItemBeingRemoved = var1;
         this.animate(this.dataRemoveTimeline);
      } else {
         this.colorBits.clear(var1.defaultColorIndex);
         this.getChartChildren().remove(var1.textNode);
         this.getChartChildren().remove(var2);
         var1.setChart(null);
         this.removeDataItemRef(var1);
      }
   }

   @Override
   protected void layoutChartChildren(double var1, double var3, double var5, double var7) {
      double var9 = 0.0;

      for (PieChart.Data var11 = this.begin; var11 != null; var11 = var11.next) {
         var9 += Math.abs(var11.getCurrentPieValue());
      }

      double var42 = var9 != 0.0 ? 360.0 / var9 : 0.0;
      double[] var13 = null;
      double[] var14 = null;
      double[] var15 = null;
      double var16 = 1.0;
      ArrayList var18 = null;
      boolean var19 = this.getLabelsVisible();
      if (var19) {
         double var20 = 0.0;
         double var22 = 0.0;
         var13 = new double[this.getDataSize()];
         var14 = new double[this.getDataSize()];
         var15 = new double[this.getDataSize()];
         var18 = new ArrayList();
         int var24 = 0;
         double var25 = this.getStartAngle();

         for (PieChart.Data var27 = this.begin; var27 != null; var27 = var27.next) {
            var27.textNode.getTransforms().clear();
            double var28 = this.isClockwise() ? -var42 * Math.abs(var27.getCurrentPieValue()) : var42 * Math.abs(var27.getCurrentPieValue());
            var15[var24] = normalizeAngle(var25 + var28 / 2.0);
            double var30 = calcX(var15[var24], this.getLabelLineLength(), 0.0);
            double var32 = calcY(var15[var24], this.getLabelLineLength(), 0.0);
            var13[var24] = var30;
            var14[var24] = var32;
            var20 = Math.max(var20, 2.0 * (var27.textNode.getLayoutBounds().getWidth() + 6.0 + Math.abs(var30)));
            if (var32 > 0.0) {
               var22 = Math.max(var22, 2.0 * Math.abs(var32 + var27.textNode.getLayoutBounds().getMaxY()));
            } else {
               var22 = Math.max(var22, 2.0 * Math.abs(var32 + var27.textNode.getLayoutBounds().getMinY()));
            }

            var25 += var28;
            var24++;
         }

         this.pieRadius = Math.min(var5 - var20, var7 - var22) / 2.0;
         if (this.pieRadius < 25.0) {
            double var48 = var5 - 25.0 - 25.0;
            double var29 = var7 - 25.0 - 25.0;
            var16 = Math.min(var48 / var20, var29 / var22);
            if ((this.begin != null || !(var16 < 0.7)) && !(this.begin.textNode.getFont().getSize() * var16 < 9.0)) {
               this.pieRadius = 25.0;

               for (int var31 = 0; var31 < var13.length; var31++) {
                  var13[var31] *= var16;
                  var14[var31] *= var16;
               }
            } else {
               var19 = false;
               var16 = 1.0;
            }
         }
      }

      if (!var19) {
         this.pieRadius = Math.min(var5, var7) / 2.0;
         this.labelLinePath.getElements().clear();
      }

      if (this.getChartChildren().size() > 0) {
         double var43 = var5 / 2.0 + var3;
         double var44 = var7 / 2.0 + var1;
         int var45 = 0;

         for (PieChart.Data var46 = this.begin; var46 != null; var46 = var46.next) {
            var46.textNode.setVisible(var19);
            if (var19) {
               double var26 = this.isClockwise() ? -var42 * Math.abs(var46.getCurrentPieValue()) : var42 * Math.abs(var46.getCurrentPieValue());
               boolean var51 = !(var15[var45] > -90.0) || !(var15[var45] < 90.0);
               double var54 = calcX(var15[var45], this.pieRadius, var43);
               double var58 = calcY(var15[var45], this.pieRadius, var44);
               double var33 = var51
                  ? var13[var45] + var54 - var46.textNode.getLayoutBounds().getMaxX() - 6.0
                  : var13[var45] + var54 - var46.textNode.getLayoutBounds().getMinX() + 6.0;
               double var35 = var14[var45] + var58 - var46.textNode.getLayoutBounds().getMinY() / 2.0 - 2.0;
               double var37 = var54 + var13[var45];
               double var39 = var58 + var14[var45];
               PieChart.LabelLayoutInfo var41 = new PieChart.LabelLayoutInfo(var54, var58, var37, var39, var33, var35, var46.textNode, Math.abs(var26));
               var18.add(var41);
               if (var16 < 1.0) {
                  var46.textNode.getTransforms().add(new Scale(var16, var16, var51 ? var46.textNode.getLayoutBounds().getWidth() : 0.0, 0.0));
               }
            }

            var45++;
         }

         double var47 = this.getStartAngle();

         for (PieChart.Data var49 = this.begin; var49 != null; var49 = var49.next) {
            Node var52 = var49.getNode();
            Arc var55 = null;
            if (var52 != null && var52 instanceof Region) {
               Region var56 = (Region)var52;
               if (var56.getShape() == null) {
                  var55 = new Arc();
                  var56.setShape(var55);
               } else {
                  var55 = (Arc)var56.getShape();
               }

               var56.setScaleShape(false);
               var56.setCenterShape(false);
               var56.setCacheShape(false);
            }

            double var57 = this.isClockwise() ? -var42 * Math.abs(var49.getCurrentPieValue()) : var42 * Math.abs(var49.getCurrentPieValue());
            var55.setStartAngle(var47);
            var55.setLength(var57);
            var55.setType(ArcType.ROUND);
            var55.setRadiusX(this.pieRadius * var49.getRadiusMultiplier());
            var55.setRadiusY(this.pieRadius * var49.getRadiusMultiplier());
            var52.setLayoutX(var43);
            var52.setLayoutY(var44);
            var47 += var57;
         }

         if (var18 != null) {
            this.resolveCollision(var18);
            if (!var18.equals(this.labelLayoutInfos)) {
               this.labelLinePath.getElements().clear();

               for (PieChart.LabelLayoutInfo var53 : var18) {
                  if (var53.text.isVisible()) {
                     this.drawLabelLinePath(var53);
                  }
               }

               this.labelLayoutInfos = var18;
            }
         }
      }
   }

   private void resolveCollision(List<PieChart.LabelLayoutInfo> var1) {
      int var2 = this.begin != null ? (int)this.begin.textNode.getLayoutBounds().getHeight() : 0;

      for (int var3 = 0; var3 < var1.size(); var3++) {
         for (int var4 = var3 + 1; var4 < var1.size(); var4++) {
            PieChart.LabelLayoutInfo var5 = (PieChart.LabelLayoutInfo)var1.get(var3);
            PieChart.LabelLayoutInfo var6 = (PieChart.LabelLayoutInfo)var1.get(var4);
            if (var5.text.isVisible()
               && var6.text.isVisible()
               && (
                  this.fuzzyGT(var6.textY, var5.textY)
                     ? this.fuzzyLT(var6.textY - (double)var2 - var5.textY, 2.0)
                     : this.fuzzyLT(var5.textY - (double)var2 - var6.textY, 2.0)
               )
               && (
                  this.fuzzyGT(var5.textX, var6.textX)
                     ? this.fuzzyLT(var5.textX - var6.textX, var6.text.prefWidth(-1.0))
                     : this.fuzzyLT(var6.textX - var5.textX, var5.text.prefWidth(-1.0))
               )) {
               if (this.fuzzyLT(var5.size, var6.size)) {
                  var5.text.setVisible(false);
               } else {
                  var6.text.setVisible(false);
               }
            }
         }
      }
   }

   private int fuzzyCompare(double var1, double var3) {
      double var5 = 1.0E-5;
      return Math.abs(var1 - var3) < var5 ? 0 : (var1 < var3 ? -1 : 1);
   }

   private boolean fuzzyGT(double var1, double var3) {
      return this.fuzzyCompare(var1, var3) == 1;
   }

   private boolean fuzzyLT(double var1, double var3) {
      return this.fuzzyCompare(var1, var3) == -1;
   }

   private void drawLabelLinePath(PieChart.LabelLayoutInfo var1) {
      var1.text.setLayoutX(var1.textX);
      var1.text.setLayoutY(var1.textY);
      this.labelLinePath.getElements().add(new MoveTo(var1.startX, var1.startY));
      this.labelLinePath.getElements().add(new LineTo(var1.endX, var1.endY));
      this.labelLinePath.getElements().add(new MoveTo(var1.endX - 2.0, var1.endY));
      this.labelLinePath.getElements().add(new ArcTo(2.0, 2.0, 90.0, var1.endX, var1.endY - 2.0, false, true));
      this.labelLinePath.getElements().add(new ArcTo(2.0, 2.0, 90.0, var1.endX + 2.0, var1.endY, false, true));
      this.labelLinePath.getElements().add(new ArcTo(2.0, 2.0, 90.0, var1.endX, var1.endY + 2.0, false, true));
      this.labelLinePath.getElements().add(new ArcTo(2.0, 2.0, 90.0, var1.endX - 2.0, var1.endY, false, true));
      this.labelLinePath.getElements().add(new ClosePath());
   }

   private void updateLegend() {
      Node var1 = this.getLegend();
      if (var1 == null || var1 == this.legend) {
         this.legend.setVertical(this.getLegendSide().equals(Side.LEFT) || this.getLegendSide().equals(Side.RIGHT));
         ArrayList var2 = new ArrayList();
         if (this.getData() != null) {
            for (PieChart.Data var4 : this.getData()) {
               Legend.LegendItem var5 = new Legend.LegendItem(var4.getName());
               var5.getSymbol().getStyleClass().addAll(var4.getNode().getStyleClass());
               var5.getSymbol().getStyleClass().add("pie-legend-symbol");
               var2.add(var5);
            }
         }

         this.legend.getItems().setAll(var2);
         if (var2.size() > 0) {
            if (var1 == null) {
               this.setLegend(this.legend);
            }
         } else {
            this.setLegend(null);
         }
      }
   }

   private int getDataSize() {
      int var1 = 0;

      for (PieChart.Data var2 = this.begin; var2 != null; var2 = var2.next) {
         var1++;
      }

      return var1;
   }

   private static double calcX(double var0, double var2, double var4) {
      return var4 + var2 * Math.cos(Math.toRadians(-var0));
   }

   private static double calcY(double var0, double var2, double var4) {
      return var4 + var2 * Math.sin(Math.toRadians(-var0));
   }

   private static double normalizeAngle(double var0) {
      double var2 = var0 % 360.0;
      if (var2 <= -180.0) {
         var2 += 360.0;
      }

      if (var2 > 180.0) {
         var2 -= 360.0;
      }

      return var2;
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return PieChart.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   public static final class Data {
      private Text textNode = new Text();
      private PieChart.Data next = null;
      private int defaultColorIndex;
      private ReadOnlyObjectWrapper<PieChart> chart = new ReadOnlyObjectWrapper(this, "chart");
      private StringProperty name = new StringPropertyBase() {
         protected void invalidated() {
            if (Data.this.getChart() != null) {
               Data.this.getChart().dataNameChanged(Data.this);
            }
         }

         public Object getBean() {
            return Data.this;
         }

         public String getName() {
            return "name";
         }
      };
      private DoubleProperty pieValue = new DoublePropertyBase() {
         protected void invalidated() {
            if (Data.this.getChart() != null) {
               Data.this.getChart().dataPieValueChanged(Data.this);
            }
         }

         public Object getBean() {
            return Data.this;
         }

         public String getName() {
            return "pieValue";
         }
      };
      private DoubleProperty currentPieValue = new SimpleDoubleProperty(this, "currentPieValue");
      private DoubleProperty radiusMultiplier = new SimpleDoubleProperty(this, "radiusMultiplier");
      private ReadOnlyObjectWrapper<Node> node = new ReadOnlyObjectWrapper(this, "node");

      public final PieChart getChart() {
         return (PieChart)this.chart.getValue();
      }

      private void setChart(PieChart var1) {
         this.chart.setValue(var1);
      }

      public final ReadOnlyObjectProperty<PieChart> chartProperty() {
         return this.chart.getReadOnlyProperty();
      }

      public final void setName(String var1) {
         this.name.setValue(var1);
      }

      public final String getName() {
         return this.name.getValue();
      }

      public final StringProperty nameProperty() {
         return this.name;
      }

      public final double getPieValue() {
         return this.pieValue.getValue();
      }

      public final void setPieValue(double var1) {
         this.pieValue.setValue(var1);
      }

      public final DoubleProperty pieValueProperty() {
         return this.pieValue;
      }

      private double getCurrentPieValue() {
         return this.currentPieValue.getValue();
      }

      private void setCurrentPieValue(double var1) {
         this.currentPieValue.setValue(var1);
      }

      private DoubleProperty currentPieValueProperty() {
         return this.currentPieValue;
      }

      private double getRadiusMultiplier() {
         return this.radiusMultiplier.getValue();
      }

      private void setRadiusMultiplier(double var1) {
         this.radiusMultiplier.setValue(var1);
      }

      private DoubleProperty radiusMultiplierProperty() {
         return this.radiusMultiplier;
      }

      public Node getNode() {
         return (Node)this.node.getValue();
      }

      private void setNode(Node var1) {
         this.node.setValue(var1);
      }

      public ReadOnlyObjectProperty<Node> nodeProperty() {
         return this.node.getReadOnlyProperty();
      }

      public Data(String var1, double var2) {
         this.setName(var1);
         this.setPieValue(var2);
         this.textNode.getStyleClass().addAll(new String[]{"text", "chart-pie-label"});
         this.textNode.setAccessibleRole(AccessibleRole.TEXT);
         this.textNode.setAccessibleRoleDescription("slice");
         this.textNode.focusTraversableProperty().bind(Platform.accessibilityActiveProperty());
         this.textNode.accessibleTextProperty().bind(new StringBinding() {
            {
               this.bind(new Observable[]{Data.this.nameProperty(), Data.this.currentPieValueProperty()});
            }

            protected String computeValue() {
               return Data.this.getName() + " represents " + Data.this.getCurrentPieValue() + " percent";
            }
         });
      }

      @Override
      public String toString() {
         return "Data[" + this.getName() + "," + this.getPieValue() + "]";
      }
   }

   private static final class LabelLayoutInfo {
      double startX;
      double startY;
      double endX;
      double endY;
      double textX;
      double textY;
      Text text;
      double size;

      LabelLayoutInfo(double var1, double var3, double var5, double var7, double var9, double var11, Text var13, double var14) {
         this.startX = var1;
         this.startY = var3;
         this.endX = var5;
         this.endY = var7;
         this.textX = var9;
         this.textY = var11;
         this.text = var13;
         this.size = var14;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            PieChart.LabelLayoutInfo var2 = (PieChart.LabelLayoutInfo)var1;
            return Double.compare(var2.startX, this.startX) == 0
               && Double.compare(var2.startY, this.startY) == 0
               && Double.compare(var2.endX, this.endX) == 0
               && Double.compare(var2.endY, this.endY) == 0
               && Double.compare(var2.textX, this.textX) == 0
               && Double.compare(var2.textY, this.textY) == 0
               && Double.compare(var2.size, this.size) == 0;
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.startX, this.startY, this.endX, this.endY, this.textX, this.textY, this.size);
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<PieChart, Boolean> CLOCKWISE = new CssMetaData<PieChart, Boolean>(
         "-fx-clockwise", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(PieChart var1) {
            return var1.clockwise == null || !var1.clockwise.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(PieChart var1) {
            return (StyleableProperty<Boolean>)var1.clockwiseProperty();
         }
      };
      private static final CssMetaData<PieChart, Boolean> LABELS_VISIBLE = new CssMetaData<PieChart, Boolean>(
         "-fx-pie-label-visible", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(PieChart var1) {
            return var1.labelsVisible == null || !var1.labelsVisible.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(PieChart var1) {
            return (StyleableProperty<Boolean>)var1.labelsVisibleProperty();
         }
      };
      private static final CssMetaData<PieChart, Number> LABEL_LINE_LENGTH = new CssMetaData<PieChart, Number>(
         "-fx-label-line-length", SizeConverter.getInstance(), 20.0
      ) {
         public boolean isSettable(PieChart var1) {
            return var1.labelLineLength == null || !var1.labelLineLength.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(PieChart var1) {
            return (StyleableProperty<Number>)var1.labelLineLengthProperty();
         }
      };
      private static final CssMetaData<PieChart, Number> START_ANGLE = new CssMetaData<PieChart, Number>("-fx-start-angle", SizeConverter.getInstance(), 0.0) {
         public boolean isSettable(PieChart var1) {
            return var1.startAngle == null || !var1.startAngle.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(PieChart var1) {
            return (StyleableProperty<Number>)var1.startAngleProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Chart.getClassCssMetaData());
         var0.add(CLOCKWISE);
         var0.add(LABELS_VISIBLE);
         var0.add(LABEL_LINE_LENGTH);
         var0.add(START_ANGLE);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
