package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.SliderBehavior;
import javafx.animation.Transition;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Slider;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class SliderSkin extends SkinBase<Slider> {
   private NumberAxis tickLine = null;
   private double trackToTickGap = 2.0;
   private boolean showTickMarks;
   private double thumbWidth;
   private double thumbHeight;
   private double trackStart;
   private double trackLength;
   private double thumbTop;
   private double thumbLeft;
   private double preDragThumbPos;
   private Point2D dragStart;
   private StackPane thumb;
   private StackPane track;
   private boolean trackClicked = false;
   private final SliderBehavior behavior;
   StringConverter<Number> stringConverterWrapper = new StringConverter<Number>() {
      Slider slider = SliderSkin.this.getSkinnable();

      public String toString(Number var1) {
         return var1 != null ? this.slider.getLabelFormatter().toString(var1.doubleValue()) : "";
      }

      public Number fromString(String var1) {
         return (Number)this.slider.getLabelFormatter().fromString(var1);
      }
   };

   public SliderSkin(Slider var1) {
      super(var1);
      this.behavior = new SliderBehavior(var1);
      this.initialize();
      var1.requestLayout();
      this.registerChangeListener(var1.minProperty(), var2 -> {
         if (this.showTickMarks && this.tickLine != null) {
            this.tickLine.setLowerBound(var1.getMin());
         }

         this.getSkinnable().requestLayout();
      });
      this.registerChangeListener(var1.maxProperty(), var2 -> {
         if (this.showTickMarks && this.tickLine != null) {
            this.tickLine.setUpperBound(var1.getMax());
         }

         this.getSkinnable().requestLayout();
      });
      this.registerChangeListener(var1.valueProperty(), var1x -> this.positionThumb(this.trackClicked));
      this.registerChangeListener(var1.orientationProperty(), var2 -> {
         if (this.showTickMarks && this.tickLine != null) {
            this.tickLine.setSide(var1.getOrientation() == Orientation.VERTICAL ? Side.RIGHT : (var1.getOrientation() == null ? Side.RIGHT : Side.BOTTOM));
         }

         this.getSkinnable().requestLayout();
      });
      this.registerChangeListener(var1.showTickMarksProperty(), var2 -> this.setShowTickMarks(var1.isShowTickMarks(), var1.isShowTickLabels()));
      this.registerChangeListener(var1.showTickLabelsProperty(), var2 -> this.setShowTickMarks(var1.isShowTickMarks(), var1.isShowTickLabels()));
      this.registerChangeListener(var1.majorTickUnitProperty(), var2 -> {
         if (this.tickLine != null) {
            this.tickLine.setTickUnit(var1.getMajorTickUnit());
            this.getSkinnable().requestLayout();
         }
      });
      this.registerChangeListener(var1.minorTickCountProperty(), var2 -> {
         if (this.tickLine != null) {
            this.tickLine.setMinorTickCount(Math.max(var1.getMinorTickCount(), 0) + 1);
            this.getSkinnable().requestLayout();
         }
      });
      this.registerChangeListener(var1.labelFormatterProperty(), var2 -> {
         if (this.tickLine != null) {
            if (var1.getLabelFormatter() == null) {
               this.tickLine.setTickLabelFormatter(null);
            } else {
               this.tickLine.setTickLabelFormatter(this.stringConverterWrapper);
               this.tickLine.requestAxisLayout();
            }
         }
      });
      this.registerChangeListener(var1.snapToTicksProperty(), var1x -> var1.adjustValue(var1.getValue()));
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      this.thumbWidth = this.snapSizeX(this.thumb.prefWidth(-1.0));
      this.thumbHeight = this.snapSizeY(this.thumb.prefHeight(-1.0));
      this.thumb.resize(this.thumbWidth, this.thumbHeight);
      double var9 = this.track.getBackground() == null
         ? 0.0
         : (
            this.track.getBackground().getFills().size() > 0
               ? ((BackgroundFill)this.track.getBackground().getFills().get(0)).getRadii().getTopLeftHorizontalRadius()
               : 0.0
         );
      if (this.getSkinnable().getOrientation() == Orientation.HORIZONTAL) {
         double var11 = this.showTickMarks ? this.tickLine.prefHeight(-1.0) : 0.0;
         double var13 = this.snapSizeY(this.track.prefHeight(-1.0));
         double var15 = Math.max(var13, this.thumbHeight);
         double var17 = var15 + (this.showTickMarks ? this.trackToTickGap + var11 : 0.0);
         double var19 = var3 + (var7 - var17) / 2.0;
         this.trackLength = this.snapSizeX(var5 - this.thumbWidth);
         this.trackStart = this.snapPositionX(var1 + this.thumbWidth / 2.0);
         double var21 = (double)((int)(var19 + (var15 - var13) / 2.0));
         this.thumbTop = (double)((int)(var19 + (var15 - this.thumbHeight) / 2.0));
         this.positionThumb(false);
         this.track.resizeRelocate((double)((int)(this.trackStart - var9)), var21, (double)((int)(this.trackLength + var9 + var9)), var13);
         if (this.showTickMarks) {
            this.tickLine.setLayoutX(this.trackStart);
            this.tickLine.setLayoutY(var21 + var13 + this.trackToTickGap);
            this.tickLine.resize(this.trackLength, var11);
            this.tickLine.requestAxisLayout();
         } else {
            if (this.tickLine != null) {
               this.tickLine.resize(0.0, 0.0);
               this.tickLine.requestAxisLayout();
            }

            this.tickLine = null;
         }
      } else {
         double var23 = this.showTickMarks ? this.tickLine.prefWidth(-1.0) : 0.0;
         double var24 = this.snapSizeX(this.track.prefWidth(-1.0));
         double var25 = Math.max(var24, this.thumbWidth);
         double var26 = var25 + (this.showTickMarks ? this.trackToTickGap + var23 : 0.0);
         double var27 = var1 + (var5 - var26) / 2.0;
         this.trackLength = this.snapSizeY(var7 - this.thumbHeight);
         this.trackStart = this.snapPositionY(var3 + this.thumbHeight / 2.0);
         double var28 = (double)((int)(var27 + (var25 - var24) / 2.0));
         this.thumbLeft = (double)((int)(var27 + (var25 - this.thumbWidth) / 2.0));
         this.positionThumb(false);
         this.track.resizeRelocate(var28, (double)((int)(this.trackStart - var9)), var24, (double)((int)(this.trackLength + var9 + var9)));
         if (this.showTickMarks) {
            this.tickLine.setLayoutX(var28 + var24 + this.trackToTickGap);
            this.tickLine.setLayoutY(this.trackStart);
            this.tickLine.resize(var23, this.trackLength);
            this.tickLine.requestAxisLayout();
         } else {
            if (this.tickLine != null) {
               this.tickLine.resize(0.0, 0.0);
               this.tickLine.requestAxisLayout();
            }

            this.tickLine = null;
         }
      }
   }

   @Override
   protected double computeMinWidth(double var1, double var3, double var5, double var7, double var9) {
      Slider var11 = this.getSkinnable();
      return var11.getOrientation() == Orientation.HORIZONTAL
         ? var9 + this.minTrackLength() + this.thumb.minWidth(-1.0) + var5
         : var9 + this.thumb.prefWidth(-1.0) + var5;
   }

   @Override
   protected double computeMinHeight(double var1, double var3, double var5, double var7, double var9) {
      Slider var11 = this.getSkinnable();
      if (var11.getOrientation() == Orientation.HORIZONTAL) {
         double var12 = this.showTickMarks ? this.tickLine.prefHeight(-1.0) + this.trackToTickGap : 0.0;
         return var3 + this.thumb.prefHeight(-1.0) + var12 + var7;
      } else {
         return var3 + this.minTrackLength() + this.thumb.prefHeight(-1.0) + var7;
      }
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      Slider var11 = this.getSkinnable();
      if (var11.getOrientation() == Orientation.HORIZONTAL) {
         return this.showTickMarks ? Math.max(140.0, this.tickLine.prefWidth(-1.0)) : 140.0;
      } else {
         double var12 = this.showTickMarks ? this.tickLine.prefWidth(-1.0) + this.trackToTickGap : 0.0;
         return var9 + Math.max(this.thumb.prefWidth(-1.0), this.track.prefWidth(-1.0)) + var12 + var5;
      }
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      Slider var11 = this.getSkinnable();
      if (var11.getOrientation() == Orientation.HORIZONTAL) {
         return var3
            + Math.max(this.thumb.prefHeight(-1.0), this.track.prefHeight(-1.0))
            + (this.showTickMarks ? this.trackToTickGap + this.tickLine.prefHeight(-1.0) : 0.0)
            + var7;
      } else {
         return this.showTickMarks ? Math.max(140.0, this.tickLine.prefHeight(-1.0)) : 140.0;
      }
   }

   @Override
   protected double computeMaxWidth(double var1, double var3, double var5, double var7, double var9) {
      return this.getSkinnable().getOrientation() == Orientation.HORIZONTAL ? Double.MAX_VALUE : this.getSkinnable().prefWidth(-1.0);
   }

   @Override
   protected double computeMaxHeight(double var1, double var3, double var5, double var7, double var9) {
      return this.getSkinnable().getOrientation() == Orientation.HORIZONTAL ? this.getSkinnable().prefHeight(var1) : Double.MAX_VALUE;
   }

   private void initialize() {
      this.thumb = new StackPane() {
         public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
            switch (var1) {
               case VALUE:
                  return SliderSkin.this.getSkinnable().getValue();
               default:
                  return super.queryAccessibleAttribute(var1, var2);
            }
         }
      };
      this.thumb.getStyleClass().setAll(new String[]{"thumb"});
      this.thumb.setAccessibleRole(AccessibleRole.THUMB);
      this.track = new StackPane();
      this.track.getStyleClass().setAll(new String[]{"track"});
      this.getChildren().clear();
      this.getChildren().addAll(new Node[]{this.track, this.thumb});
      this.setShowTickMarks(this.getSkinnable().isShowTickMarks(), this.getSkinnable().isShowTickLabels());
      this.track.setOnMousePressed(var1 -> {
         if (!this.thumb.isPressed()) {
            this.trackClicked = true;
            if (this.getSkinnable().getOrientation() == Orientation.HORIZONTAL) {
               this.behavior.trackPress(var1, var1.getX() / this.trackLength);
            } else {
               this.behavior.trackPress(var1, var1.getY() / this.trackLength);
            }

            this.trackClicked = false;
         }
      });
      this.track.setOnMouseDragged(var1 -> {
         if (!this.thumb.isPressed()) {
            if (this.getSkinnable().getOrientation() == Orientation.HORIZONTAL) {
               this.behavior.trackPress(var1, var1.getX() / this.trackLength);
            } else {
               this.behavior.trackPress(var1, var1.getY() / this.trackLength);
            }
         }
      });
      this.thumb.setOnMousePressed(var1 -> {
         this.behavior.thumbPressed(var1, 0.0);
         this.dragStart = this.thumb.localToParent(var1.getX(), var1.getY());
         this.preDragThumbPos = (this.getSkinnable().getValue() - this.getSkinnable().getMin()) / (this.getSkinnable().getMax() - this.getSkinnable().getMin());
      });
      this.thumb.setOnMouseReleased(var1 -> this.behavior.thumbReleased(var1));
      this.thumb
         .setOnMouseDragged(
            var1 -> {
               Point2D var2 = this.thumb.localToParent(var1.getX(), var1.getY());
               double var3 = this.getSkinnable().getOrientation() == Orientation.HORIZONTAL
                  ? var2.getX() - this.dragStart.getX()
                  : -(var2.getY() - this.dragStart.getY());
               this.behavior.thumbDragged(var1, this.preDragThumbPos + var3 / this.trackLength);
            }
         );
   }

   private void setShowTickMarks(boolean var1, boolean var2) {
      this.showTickMarks = var1 || var2;
      Slider var3 = this.getSkinnable();
      if (this.showTickMarks) {
         if (this.tickLine == null) {
            this.tickLine = new NumberAxis();
            this.tickLine.setAutoRanging(false);
            this.tickLine.setSide(var3.getOrientation() == Orientation.VERTICAL ? Side.RIGHT : (var3.getOrientation() == null ? Side.RIGHT : Side.BOTTOM));
            this.tickLine.setUpperBound(var3.getMax());
            this.tickLine.setLowerBound(var3.getMin());
            this.tickLine.setTickUnit(var3.getMajorTickUnit());
            this.tickLine.setTickMarkVisible(var1);
            this.tickLine.setTickLabelsVisible(var2);
            this.tickLine.setMinorTickVisible(var1);
            this.tickLine.setMinorTickCount(Math.max(var3.getMinorTickCount(), 0) + 1);
            if (var3.getLabelFormatter() != null) {
               this.tickLine.setTickLabelFormatter(this.stringConverterWrapper);
            }

            this.getChildren().clear();
            this.getChildren().addAll(new Node[]{this.tickLine, this.track, this.thumb});
         } else {
            this.tickLine.setTickLabelsVisible(var2);
            this.tickLine.setTickMarkVisible(var1);
            this.tickLine.setMinorTickVisible(var1);
         }
      } else {
         this.getChildren().clear();
         this.getChildren().addAll(new Node[]{this.track, this.thumb});
      }

      this.getSkinnable().requestLayout();
   }

   void positionThumb(boolean var1) {
      Slider var2 = this.getSkinnable();
      if (!(var2.getValue() > var2.getMax())) {
         boolean var3 = var2.getOrientation() == Orientation.HORIZONTAL;
         final double var4 = var3
            ? this.trackStart + (this.trackLength * ((var2.getValue() - var2.getMin()) / (var2.getMax() - var2.getMin())) - this.thumbWidth / 2.0)
            : this.thumbLeft;
         final double var6 = var3
            ? this.thumbTop
            : this.snappedTopInset() + this.trackLength - this.trackLength * ((var2.getValue() - var2.getMin()) / (var2.getMax() - var2.getMin()));
         if (var1) {
            final double var8 = this.thumb.getLayoutX();
            final double var10 = this.thumb.getLayoutY();
            Transition var12 = new Transition() {
               {
                  this.setCycleDuration(Duration.millis(200.0));
               }

               protected void interpolate(double var1) {
                  if (!Double.isNaN(var8)) {
                     SliderSkin.this.thumb.setLayoutX(var8 + var1 * (var4 - var8));
                  }

                  if (!Double.isNaN(var10)) {
                     SliderSkin.this.thumb.setLayoutY(var10 + var1 * (var6 - var10));
                  }
               }
            };
            var12.play();
         } else {
            this.thumb.setLayoutX(var4);
            this.thumb.setLayoutY(var6);
         }
      }
   }

   double minTrackLength() {
      return 2.0 * this.thumb.prefWidth(-1.0);
   }
}
