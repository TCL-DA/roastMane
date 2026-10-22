package impl.org.controlsfx.skin;

import impl.org.controlsfx.ImplUtils;
import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import org.controlsfx.control.RangeSlider;
import org.controlsfx.tools.Utils;

public class RangeSliderSkin extends SkinBase<RangeSlider> {
   private NumberAxis tickLine = null;
   private double trackToTickGap = 2.0;
   private boolean showTickMarks;
   private double thumbWidth;
   private double thumbHeight;
   private Orientation orientation;
   private StackPane track;
   private double trackStart;
   private double trackLength;
   private double lowThumbPos;
   private double rangeEnd;
   private double rangeStart;
   private RangeSliderSkin.ThumbPane lowThumb;
   private RangeSliderSkin.ThumbPane highThumb;
   private StackPane rangeBar;
   private double preDragPos;
   private Point2D preDragThumbPoint;
   private RangeSliderSkin.FocusedChild currentFocus = RangeSliderSkin.FocusedChild.LOW_THUMB;
   private Callback<Void, RangeSliderSkin.FocusedChild> selectedValue;

   public RangeSliderSkin(RangeSlider rangeSlider) {
      super(rangeSlider);
      this.orientation = ((RangeSlider)this.getSkinnable()).getOrientation();
      this.initFirstThumb();
      this.initSecondThumb();
      this.initRangeBar();
      this.registerChangeListener(rangeSlider.lowValueProperty(), e -> {
         this.positionLowThumb();
         this.rangeBar.resizeRelocate(this.rangeStart, this.rangeBar.getLayoutY(), this.rangeEnd - this.rangeStart, this.rangeBar.getHeight());
      });
      this.registerChangeListener(rangeSlider.highValueProperty(), e -> {
         this.positionHighThumb();
         this.rangeBar.resize(this.rangeEnd - this.rangeStart, this.rangeBar.getHeight());
      });
      this.registerChangeListener(rangeSlider.minProperty(), e -> {
         if (this.showTickMarks && this.tickLine != null) {
            this.tickLine.setLowerBound(((RangeSlider)this.getSkinnable()).getMin());
         }

         ((RangeSlider)this.getSkinnable()).requestLayout();
      });
      this.registerChangeListener(rangeSlider.maxProperty(), e -> {
         if (this.showTickMarks && this.tickLine != null) {
            this.tickLine.setUpperBound(((RangeSlider)this.getSkinnable()).getMax());
         }

         ((RangeSlider)this.getSkinnable()).requestLayout();
      });
      this.registerChangeListener(rangeSlider.orientationProperty(), e -> {
         this.orientation = ((RangeSlider)this.getSkinnable()).getOrientation();
         if (this.showTickMarks && this.tickLine != null) {
            this.tickLine.setSide(this.isHorizontal() ? Side.BOTTOM : Side.RIGHT);
         }

         ((RangeSlider)this.getSkinnable()).requestLayout();
      });
      this.registerChangeListener(
         rangeSlider.showTickMarksProperty(),
         e -> this.setShowTickMarks(((RangeSlider)this.getSkinnable()).isShowTickMarks(), ((RangeSlider)this.getSkinnable()).isShowTickLabels())
      );
      this.registerChangeListener(
         rangeSlider.showTickLabelsProperty(),
         e -> this.setShowTickMarks(((RangeSlider)this.getSkinnable()).isShowTickMarks(), ((RangeSlider)this.getSkinnable()).isShowTickLabels())
      );
      this.registerChangeListener(rangeSlider.majorTickUnitProperty(), e -> {
         if (this.tickLine != null) {
            this.tickLine.setTickUnit(((RangeSlider)this.getSkinnable()).getMajorTickUnit());
            ((RangeSlider)this.getSkinnable()).requestLayout();
         }
      });
      this.registerChangeListener(rangeSlider.minorTickCountProperty(), e -> {
         if (this.tickLine != null) {
            this.tickLine.setMinorTickCount(Math.max(((RangeSlider)this.getSkinnable()).getMinorTickCount(), 0) + 1);
            ((RangeSlider)this.getSkinnable()).requestLayout();
         }
      });
      this.lowThumb.focusedProperty().addListener((ov, t, hasFocus) -> {
         if (hasFocus) {
            this.currentFocus = RangeSliderSkin.FocusedChild.LOW_THUMB;
         }
      });
      this.highThumb.focusedProperty().addListener((ov, t, hasFocus) -> {
         if (hasFocus) {
            this.currentFocus = RangeSliderSkin.FocusedChild.HIGH_THUMB;
         }
      });
      this.rangeBar.focusedProperty().addListener((ov, t, hasFocus) -> {
         if (hasFocus) {
            this.currentFocus = RangeSliderSkin.FocusedChild.RANGE_BAR;
         }
      });
      rangeSlider.focusedProperty().addListener((ov, t, hasFocus) -> {
         if (hasFocus) {
            this.lowThumb.setFocus(true);
         } else {
            this.lowThumb.setFocus(false);
            this.highThumb.setFocus(false);
            this.currentFocus = RangeSliderSkin.FocusedChild.NONE;
         }
      });
      EventHandler<KeyEvent> keyPressEventHandler = event -> {
         if (KeyCode.TAB.equals(event.getCode())) {
            if (this.lowThumb.isFocused()) {
               if (event.isShiftDown()) {
                  ImplUtils.focusPreviousSibling(this.getSkinnable());
               } else {
                  this.lowThumb.setFocus(false);
                  this.highThumb.setFocus(true);
               }

               event.consume();
            } else if (this.highThumb.isFocused()) {
               if (event.isShiftDown()) {
                  this.highThumb.setFocus(false);
                  this.lowThumb.setFocus(true);
               } else {
                  ImplUtils.focusNextSibling(this.getSkinnable());
               }

               event.consume();
            }
         } else if (!KeyCode.LEFT.equals(event.getCode()) && !KeyCode.KP_LEFT.equals(event.getCode())) {
            if (!KeyCode.RIGHT.equals(event.getCode()) && !KeyCode.KP_RIGHT.equals(event.getCode())) {
               if (!KeyCode.DOWN.equals(event.getCode()) && !KeyCode.KP_DOWN.equals(event.getCode())) {
                  if ((KeyCode.UP.equals(event.getCode()) || KeyCode.KP_UP.equals(event.getCode()))
                     && ((RangeSlider)this.getSkinnable()).getOrientation() == Orientation.VERTICAL) {
                     this.incrementValue();
                  }
               } else if (((RangeSlider)this.getSkinnable()).getOrientation() == Orientation.VERTICAL) {
                  this.decrementValue();
               }
            } else if (((RangeSlider)this.getSkinnable()).getOrientation() == Orientation.HORIZONTAL) {
               this.rtl((RangeSlider)this.getSkinnable(), this::decrementValue, this::incrementValue);
            }
         } else if (((RangeSlider)this.getSkinnable()).getOrientation() == Orientation.HORIZONTAL) {
            this.rtl((RangeSlider)this.getSkinnable(), this::incrementValue, this::decrementValue);
         }

         event.consume();
      };
      EventHandler<KeyEvent> keyReleaseEventHandler = event -> {
         if (KeyCode.HOME.equals(event.getCode())) {
            this.home();
         } else if (KeyCode.END.equals(event.getCode())) {
            this.end();
         }

         event.consume();
      };
      ((RangeSlider)this.getSkinnable()).addEventHandler(KeyEvent.KEY_PRESSED, keyPressEventHandler);
      ((RangeSlider)this.getSkinnable()).addEventHandler(KeyEvent.KEY_RELEASED, keyReleaseEventHandler);
      this.setSelectedValue(v -> this.currentFocus);
   }

   private void initFirstThumb() {
      this.lowThumb = new RangeSliderSkin.ThumbPane();
      this.lowThumb.getStyleClass().setAll(new String[]{"low-thumb"});
      this.lowThumb.setFocusTraversable(true);
      this.track = new StackPane();
      this.track.setFocusTraversable(false);
      this.track.getStyleClass().setAll(new String[]{"track"});
      this.getChildren().clear();
      this.getChildren().addAll(new Node[]{this.track, this.lowThumb});
      this.setShowTickMarks(((RangeSlider)this.getSkinnable()).isShowTickMarks(), ((RangeSlider)this.getSkinnable()).isShowTickLabels());
      this.track.setOnMousePressed(me -> {
         if (!this.lowThumb.isPressed() && !this.highThumb.isPressed()) {
            if (this.isHorizontal()) {
               this.trackPress(me, me.getX() / this.trackLength);
            } else {
               this.trackPress(me, me.getY() / this.trackLength);
            }
         }
      });
      this.track.setOnMouseReleased(me -> this.trackRelease(me, 0.0));
      this.lowThumb.setOnMousePressed(me -> {
         this.highThumb.setFocus(false);
         this.lowThumb.setFocus(true);
         this.lowThumbPressed(me, 0.0);
         this.preDragThumbPoint = this.lowThumb.localToParent(me.getX(), me.getY());
         this.preDragPos = (((RangeSlider)this.getSkinnable()).getLowValue() - ((RangeSlider)this.getSkinnable()).getMin()) / this.getMaxMinusMinNoZero();
      });
      this.lowThumb.setOnMouseReleased(me -> this.lowThumbReleased(me));
      this.lowThumb.setOnMouseDragged(me -> {
         Point2D cur = this.lowThumb.localToParent(me.getX(), me.getY());
         double dragPos = this.isHorizontal() ? cur.getX() - this.preDragThumbPoint.getX() : -(cur.getY() - this.preDragThumbPoint.getY());
         this.lowThumbDragged(me, this.preDragPos + dragPos / this.trackLength);
      });
   }

   private void initSecondThumb() {
      this.highThumb = new RangeSliderSkin.ThumbPane();
      this.highThumb.getStyleClass().setAll(new String[]{"high-thumb"});
      if (!this.getChildren().contains(this.highThumb)) {
         this.getChildren().add(this.highThumb);
      }

      this.highThumb.setOnMousePressed(e -> {
         this.lowThumb.setFocus(false);
         this.highThumb.setFocus(true);
         this.highThumbPressed(e, 0.0);
         this.preDragThumbPoint = this.highThumb.localToParent(e.getX(), e.getY());
         this.preDragPos = (((RangeSlider)this.getSkinnable()).getHighValue() - ((RangeSlider)this.getSkinnable()).getMin()) / this.getMaxMinusMinNoZero();
      });
      this.highThumb.setOnMouseReleased(e -> this.highThumbReleased(e));
      this.highThumb
         .setOnMouseDragged(
            e -> {
               boolean orientation = ((RangeSlider)this.getSkinnable()).getOrientation() == Orientation.HORIZONTAL;
               double trackLength = orientation ? this.track.getWidth() : this.track.getHeight();
               Point2D point2d = this.highThumb.localToParent(e.getX(), e.getY());
               double d = ((RangeSlider)this.getSkinnable()).getOrientation() != Orientation.HORIZONTAL
                  ? -(point2d.getY() - this.preDragThumbPoint.getY())
                  : point2d.getX() - this.preDragThumbPoint.getX();
               this.highThumbDragged(e, this.preDragPos + d / trackLength);
            }
         );
   }

   private void initRangeBar() {
      this.rangeBar = new StackPane();
      this.rangeBar.setFocusTraversable(false);
      this.rangeBar.cursorProperty().bind(new ObjectBinding<Cursor>() {
         {
            this.bind(new Observable[]{RangeSliderSkin.this.rangeBar.hoverProperty()});
         }

         protected Cursor computeValue() {
            return RangeSliderSkin.this.rangeBar.isHover() ? Cursor.HAND : Cursor.DEFAULT;
         }
      });
      this.rangeBar.getStyleClass().setAll(new String[]{"range-bar"});
      this.rangeBar.setOnMousePressed(e -> {
         this.rangeBar.requestFocus();
         this.preDragPos = this.isHorizontal() ? e.getX() : -e.getY();
      });
      this.rangeBar.setOnMouseDragged(e -> {
         double delta = (this.isHorizontal() ? e.getX() : -e.getY()) - this.preDragPos;
         this.moveRange(delta);
      });
      this.rangeBar.setOnMouseReleased(e -> this.confirmRange());
      this.getChildren().add(this.rangeBar);
   }

   private void setShowTickMarks(boolean ticksVisible, boolean labelsVisible) {
      this.showTickMarks = ticksVisible || labelsVisible;
      RangeSlider rangeSlider = (RangeSlider)this.getSkinnable();
      if (this.showTickMarks) {
         if (this.tickLine == null) {
            this.tickLine = new NumberAxis();
            this.tickLine.setFocusTraversable(false);
            this.tickLine.tickLabelFormatterProperty().bind(((RangeSlider)this.getSkinnable()).labelFormatterProperty());
            this.tickLine.setAnimated(false);
            this.tickLine.setAutoRanging(false);
            this.tickLine.setSide(this.isHorizontal() ? Side.BOTTOM : Side.RIGHT);
            this.tickLine.setUpperBound(rangeSlider.getMax());
            this.tickLine.setLowerBound(rangeSlider.getMin());
            this.tickLine.setTickUnit(rangeSlider.getMajorTickUnit());
            this.tickLine.setTickMarkVisible(ticksVisible);
            this.tickLine.setTickLabelsVisible(labelsVisible);
            this.tickLine.setMinorTickVisible(ticksVisible);
            this.tickLine.setMinorTickCount(Math.max(rangeSlider.getMinorTickCount(), 0) + 1);
            this.getChildren().clear();
            this.getChildren().addAll(new Node[]{this.tickLine, this.track, this.lowThumb});
         } else {
            this.tickLine.setTickLabelsVisible(labelsVisible);
            this.tickLine.setTickMarkVisible(ticksVisible);
            this.tickLine.setMinorTickVisible(ticksVisible);
         }
      } else {
         this.getChildren().clear();
         this.getChildren().addAll(new Node[]{this.track, this.lowThumb});
      }

      ((RangeSlider)this.getSkinnable()).requestLayout();
   }

   private double getMaxMinusMinNoZero() {
      RangeSlider s = (RangeSlider)this.getSkinnable();
      return s.getMax() - s.getMin() == 0.0 ? 1.0 : s.getMax() - s.getMin();
   }

   private void positionLowThumb() {
      RangeSlider s = (RangeSlider)this.getSkinnable();
      boolean horizontal = this.isHorizontal();
      double lx = horizontal
         ? this.trackStart + (this.trackLength * ((s.getLowValue() - s.getMin()) / this.getMaxMinusMinNoZero()) - this.thumbWidth / 2.0)
         : this.lowThumbPos;
      double ly = horizontal
         ? this.lowThumbPos
         : ((RangeSlider)this.getSkinnable()).getInsets().getTop()
            + this.trackLength
            - this.trackLength * ((s.getLowValue() - s.getMin()) / this.getMaxMinusMinNoZero());
      this.lowThumb.setLayoutX(lx);
      this.lowThumb.setLayoutY(ly);
      if (horizontal) {
         this.rangeStart = lx + this.thumbWidth;
      } else {
         this.rangeEnd = ly;
      }
   }

   private void positionHighThumb() {
      RangeSlider slider = (RangeSlider)this.getSkinnable();
      boolean orientation = ((RangeSlider)this.getSkinnable()).getOrientation() == Orientation.HORIZONTAL;
      double thumbWidth = this.lowThumb.getWidth();
      double thumbHeight = this.lowThumb.getHeight();
      this.highThumb.resize(thumbWidth, thumbHeight);
      double pad = 0.0;
      double trackStart = orientation ? this.track.getLayoutX() : this.track.getLayoutY();
      trackStart += pad;
      double trackLength = orientation ? this.track.getWidth() : this.track.getHeight();
      trackLength -= 2.0 * pad;
      double x = orientation
         ? trackStart + (trackLength * ((slider.getHighValue() - slider.getMin()) / this.getMaxMinusMinNoZero()) - thumbWidth / 2.0)
         : this.lowThumb.getLayoutX();
      double y = orientation
         ? this.lowThumb.getLayoutY()
         : ((RangeSlider)this.getSkinnable()).getInsets().getTop()
            + trackLength
            - trackLength * ((slider.getHighValue() - slider.getMin()) / this.getMaxMinusMinNoZero());
      this.highThumb.setLayoutX(x);
      this.highThumb.setLayoutY(y);
      if (orientation) {
         this.rangeEnd = x;
      } else {
         this.rangeStart = y + thumbWidth;
      }
   }

   protected void layoutChildren(double x, double y, double w, double h) {
      this.thumbWidth = this.lowThumb.prefWidth(-1.0);
      this.thumbHeight = this.lowThumb.prefHeight(-1.0);
      this.lowThumb.resize(this.thumbWidth, this.thumbHeight);
      double trackRadius = this.track.getBackground() == null
         ? 0.0
         : (
            this.track.getBackground().getFills().size() > 0
               ? ((BackgroundFill)this.track.getBackground().getFills().get(0)).getRadii().getTopLeftHorizontalRadius()
               : 0.0
         );
      if (this.isHorizontal()) {
         double tickLineHeight = this.showTickMarks ? this.tickLine.prefHeight(-1.0) : 0.0;
         double trackHeight = this.track.prefHeight(-1.0);
         double trackAreaHeight = Math.max(trackHeight, this.thumbHeight);
         double totalHeightNeeded = trackAreaHeight + (this.showTickMarks ? this.trackToTickGap + tickLineHeight : 0.0);
         double startY = y + (h - totalHeightNeeded) / 2.0;
         this.trackLength = w - this.thumbWidth;
         this.trackStart = x + this.thumbWidth / 2.0;
         double trackTop = (double)((int)(startY + (trackAreaHeight - trackHeight) / 2.0));
         this.lowThumbPos = (double)((int)(startY + (trackAreaHeight - this.thumbHeight) / 2.0));
         this.positionLowThumb();
         this.track.resizeRelocate(this.trackStart - trackRadius, trackTop, this.trackLength + trackRadius + trackRadius, trackHeight);
         this.positionHighThumb();
         this.rangeBar.resizeRelocate(this.rangeStart, trackTop, this.rangeEnd - this.rangeStart, trackHeight);
         if (this.showTickMarks) {
            this.tickLine.setLayoutX(this.trackStart);
            this.tickLine.setLayoutY(trackTop + trackHeight + this.trackToTickGap);
            this.tickLine.resize(this.trackLength, tickLineHeight);
            this.tickLine.requestAxisLayout();
         } else {
            if (this.tickLine != null) {
               this.tickLine.resize(0.0, 0.0);
               this.tickLine.requestAxisLayout();
            }

            this.tickLine = null;
         }
      } else {
         double tickLineWidth = this.showTickMarks ? this.tickLine.prefWidth(-1.0) : 0.0;
         double trackWidth = this.track.prefWidth(-1.0);
         double trackAreaWidth = Math.max(trackWidth, this.thumbWidth);
         double totalWidthNeeded = trackAreaWidth + (this.showTickMarks ? this.trackToTickGap + tickLineWidth : 0.0);
         double startX = x + (w - totalWidthNeeded) / 2.0;
         this.trackLength = h - this.thumbHeight;
         this.trackStart = y + this.thumbHeight / 2.0;
         double trackLeft = (double)((int)(startX + (trackAreaWidth - trackWidth) / 2.0));
         this.lowThumbPos = (double)((int)(startX + (trackAreaWidth - this.thumbWidth) / 2.0));
         this.positionLowThumb();
         this.track.resizeRelocate(trackLeft, this.trackStart - trackRadius, trackWidth, this.trackLength + trackRadius + trackRadius);
         this.positionHighThumb();
         this.rangeBar.resizeRelocate(trackLeft, this.rangeStart, trackWidth, this.rangeEnd - this.rangeStart);
         if (this.showTickMarks) {
            this.tickLine.setLayoutX(trackLeft + trackWidth + this.trackToTickGap);
            this.tickLine.setLayoutY(this.trackStart);
            this.tickLine.resize(tickLineWidth, this.trackLength);
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

   private double minTrackLength() {
      return 2.0 * this.lowThumb.prefWidth(-1.0);
   }

   protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.isHorizontal()
         ? leftInset + this.minTrackLength() + this.lowThumb.minWidth(-1.0) + rightInset
         : leftInset + this.lowThumb.prefWidth(-1.0) + rightInset;
   }

   protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.isHorizontal()
         ? topInset + this.lowThumb.prefHeight(-1.0) + bottomInset
         : topInset + this.minTrackLength() + this.lowThumb.prefHeight(-1.0) + bottomInset;
   }

   protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      if (this.isHorizontal()) {
         return this.showTickMarks ? Math.max(140.0, this.tickLine.prefWidth(-1.0)) : 140.0;
      } else {
         return leftInset
            + Math.max(this.lowThumb.prefWidth(-1.0), this.track.prefWidth(-1.0))
            + (this.showTickMarks ? this.trackToTickGap + this.tickLine.prefWidth(-1.0) : 0.0)
            + rightInset;
      }
   }

   protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      if (this.isHorizontal()) {
         return ((RangeSlider)this.getSkinnable()).getInsets().getTop()
            + Math.max(this.lowThumb.prefHeight(-1.0), this.track.prefHeight(-1.0))
            + (this.showTickMarks ? this.trackToTickGap + this.tickLine.prefHeight(-1.0) : 0.0)
            + bottomInset;
      } else {
         return this.showTickMarks ? Math.max(140.0, this.tickLine.prefHeight(-1.0)) : 140.0;
      }
   }

   protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.isHorizontal() ? Double.MAX_VALUE : ((RangeSlider)this.getSkinnable()).prefWidth(-1.0);
   }

   protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.isHorizontal() ? ((RangeSlider)this.getSkinnable()).prefHeight(width) : Double.MAX_VALUE;
   }

   private boolean isHorizontal() {
      return this.orientation == null || this.orientation == Orientation.HORIZONTAL;
   }

   public void setSelectedValue(Callback<Void, RangeSliderSkin.FocusedChild> c) {
      this.selectedValue = c;
   }

   private void highThumbReleased(MouseEvent e) {
      RangeSlider slider = (RangeSlider)this.getSkinnable();
      slider.setHighValueChanging(false);
      if (slider.isSnapToTicks()) {
         slider.setHighValue(this.snapValueToTicks(slider.getHighValue()));
      }
   }

   private void highThumbPressed(MouseEvent e, double position) {
      RangeSlider slider = (RangeSlider)this.getSkinnable();
      if (!slider.isFocused()) {
         slider.requestFocus();
      }

      slider.setHighValueChanging(true);
   }

   private void highThumbDragged(MouseEvent e, double position) {
      RangeSlider slider = (RangeSlider)this.getSkinnable();
      slider.setHighValue(Utils.clamp(slider.getMin(), position * (slider.getMax() - slider.getMin()) + slider.getMin(), slider.getMax()));
   }

   private void moveRange(double position) {
      RangeSlider slider = (RangeSlider)this.getSkinnable();
      double min = slider.getMin();
      double max = slider.getMax();
      double lowValue = slider.getLowValue();
      double newLowValue = Utils.clamp(
         min, lowValue + position * (max - min) / (slider.getOrientation() == Orientation.HORIZONTAL ? slider.getWidth() : slider.getHeight()), max
      );
      double highValue = slider.getHighValue();
      double newHighValue = Utils.clamp(
         min, highValue + position * (max - min) / (slider.getOrientation() == Orientation.HORIZONTAL ? slider.getWidth() : slider.getHeight()), max
      );
      if (!(newLowValue <= min) && !(newHighValue >= max)) {
         slider.setLowValueChanging(true);
         slider.setHighValueChanging(true);
         slider.setLowValue(newLowValue);
         slider.setHighValue(newHighValue);
      }
   }

   private void confirmRange() {
      RangeSlider slider = (RangeSlider)this.getSkinnable();
      slider.setLowValueChanging(false);
      if (slider.isSnapToTicks()) {
         slider.setLowValue(this.snapValueToTicks(slider.getLowValue()));
      }

      slider.setHighValueChanging(false);
      if (slider.isSnapToTicks()) {
         slider.setHighValue(this.snapValueToTicks(slider.getHighValue()));
      }
   }

   private void trackPress(MouseEvent e, double position) {
      RangeSlider rangeSlider = (RangeSlider)this.getSkinnable();
      if (!rangeSlider.isFocused()) {
         rangeSlider.requestFocus();
      }

      if (this.selectedValue != null) {
         double newPosition;
         if (rangeSlider.getOrientation().equals(Orientation.HORIZONTAL)) {
            newPosition = position * (rangeSlider.getMax() - rangeSlider.getMin()) + rangeSlider.getMin();
         } else {
            newPosition = (1.0 - position) * (rangeSlider.getMax() - rangeSlider.getMin()) + rangeSlider.getMin();
         }

         if (newPosition < rangeSlider.getLowValue()) {
            rangeSlider.adjustLowValue(newPosition);
         } else {
            rangeSlider.adjustHighValue(newPosition);
         }
      }
   }

   public void trackRelease(MouseEvent e, double position) {
   }

   public void lowThumbPressed(MouseEvent e, double position) {
      RangeSlider rangeSlider = (RangeSlider)this.getSkinnable();
      if (!rangeSlider.isFocused()) {
         rangeSlider.requestFocus();
      }

      rangeSlider.setLowValueChanging(true);
   }

   public void lowThumbDragged(MouseEvent e, double position) {
      RangeSlider rangeSlider = (RangeSlider)this.getSkinnable();
      double newValue = Utils.clamp(rangeSlider.getMin(), position * (rangeSlider.getMax() - rangeSlider.getMin()) + rangeSlider.getMin(), rangeSlider.getMax());
      rangeSlider.setLowValue(newValue);
   }

   public void lowThumbReleased(MouseEvent e) {
      RangeSlider rangeSlider = (RangeSlider)this.getSkinnable();
      rangeSlider.setLowValueChanging(false);
      if (rangeSlider.isSnapToTicks()) {
         rangeSlider.setLowValue(this.snapValueToTicks(rangeSlider.getLowValue()));
      }
   }

   void home() {
      RangeSlider slider = (RangeSlider)this.getNode();
      slider.adjustHighValue(slider.getMin());
   }

   private void decrementValue() {
      RangeSlider slider = (RangeSlider)this.getSkinnable();
      if (this.selectedValue != null) {
         if (this.selectedValue.call(null) == RangeSliderSkin.FocusedChild.HIGH_THUMB) {
            if (slider.isSnapToTicks()) {
               slider.adjustHighValue(slider.getHighValue() - this.computeIncrement());
            } else {
               slider.decrementHighValue();
            }
         } else if (slider.isSnapToTicks()) {
            slider.adjustLowValue(slider.getLowValue() - this.computeIncrement());
         } else {
            slider.decrementLowValue();
         }
      }
   }

   private void end() {
      RangeSlider slider = (RangeSlider)this.getNode();
      slider.adjustHighValue(slider.getMax());
   }

   private void incrementValue() {
      RangeSlider slider = (RangeSlider)this.getSkinnable();
      if (this.selectedValue != null) {
         if (this.selectedValue.call(null) == RangeSliderSkin.FocusedChild.HIGH_THUMB) {
            if (slider.isSnapToTicks()) {
               slider.adjustHighValue(slider.getHighValue() + this.computeIncrement());
            } else {
               slider.incrementHighValue();
            }
         } else if (slider.isSnapToTicks()) {
            slider.adjustLowValue(slider.getLowValue() + this.computeIncrement());
         } else {
            slider.incrementLowValue();
         }
      }
   }

   private double computeIncrement() {
      RangeSlider rangeSlider = (RangeSlider)this.getSkinnable();
      double d;
      if (rangeSlider.getMinorTickCount() != 0) {
         d = rangeSlider.getMajorTickUnit() / (double)(Math.max(rangeSlider.getMinorTickCount(), 0) + 1);
      } else {
         d = rangeSlider.getMajorTickUnit();
      }

      return rangeSlider.getBlockIncrement() > 0.0 && rangeSlider.getBlockIncrement() < d ? d : rangeSlider.getBlockIncrement();
   }

   private void rtl(RangeSlider node, Runnable rtlMethod, Runnable nonRtlMethod) {
      if (node.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT) {
         rtlMethod.run();
      } else {
         nonRtlMethod.run();
      }
   }

   private double snapValueToTicks(double d) {
      RangeSlider rangeSlider = (RangeSlider)this.getSkinnable();
      double d2;
      if (rangeSlider.getMinorTickCount() != 0) {
         d2 = rangeSlider.getMajorTickUnit() / (double)(Math.max(rangeSlider.getMinorTickCount(), 0) + 1);
      } else {
         d2 = rangeSlider.getMajorTickUnit();
      }

      int i = (int)((d - rangeSlider.getMin()) / d2);
      double d3 = (double)i * d2 + rangeSlider.getMin();
      double d4 = (double)(i + 1) * d2 + rangeSlider.getMin();
      double d1 = Utils.nearest(d3, d, d4);
      return Utils.clamp(rangeSlider.getMin(), d1, rangeSlider.getMax());
   }

   public static enum FocusedChild {
      LOW_THUMB,
      HIGH_THUMB,
      RANGE_BAR,
      NONE;
   }

   private static class ThumbPane extends StackPane {
      public void setFocus(boolean value) {
         this.setFocused(value);
      }
   }
}
