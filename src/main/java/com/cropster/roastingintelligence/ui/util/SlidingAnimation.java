package com.cropster.roastingintelligence.ui.util;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.util.Objects;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.animation.Animation.Status;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlidingAnimation {
   private final Logger logger = LoggerFactory.getLogger(SlidingAnimation.class);
   private final Region slidingRegion;
   private final Region shrinkingPane;
   private final SlidingAnimation.SlidingDirection direction;
   private final Insets initialPadding;
   private final boolean translate;
   private final BooleanProperty animated = new SimpleBooleanProperty(true);
   private ParallelTransition animation;
   private EventHandler<ActionEvent> onFinished;
   private EventHandler<ActionEvent> onResize;
   private SlidingAnimation.SlidingStatus status;
   private SlidingAnimation.SlidingMode mode;

   private static double getColumnWidth(GridPane slidingGridPane, int c) {
      double columnWidth = 0.0;

      for (int r = 0; r < slidingGridPane.getRowCount(); r++) {
         columnWidth = Math.max(columnWidth, slidingGridPane.getCellBounds(c, r).getWidth());
      }

      return columnWidth;
   }

   public SlidingAnimation(Region slidingRegion, Region shrinkingPane, SlidingAnimation.SlidingDirection direction) {
      this(slidingRegion, shrinkingPane, direction, true);
   }

   public SlidingAnimation(Region slidingRegion, Region shrinkingPane, SlidingAnimation.SlidingDirection direction, boolean translate) {
      assert Platform.isFxApplicationThread();

      Objects.requireNonNull(slidingRegion, "Null 'slidingRegion' parameter");
      Objects.requireNonNull(shrinkingPane, "Null 'shrinkingPane' parameter");
      this.slidingRegion = slidingRegion;
      this.shrinkingPane = shrinkingPane;
      this.direction = direction;
      this.initialPadding = shrinkingPane.getPadding();
      this.status = SlidingAnimation.SlidingStatus.IN;
      this.mode = SlidingAnimation.SlidingMode.SLIDE_AND_PUSH;
      this.translate = translate;
      if (JavaFxUtil.isAncestor(shrinkingPane, slidingRegion)) {
         slidingRegion.setManaged(false);
      }

      slidingRegion.setVisible(false);
      this.resetPosition();
   }

   public SlidingAnimation.SlidingStatus getStatus() {
      return this.status;
   }

   public SlidingAnimation.SlidingMode getMode() {
      return this.mode;
   }

   public void setMode(SlidingAnimation.SlidingMode mode) {
      if (this.mode != mode) {
         this.slideIn();
         this.mode = mode;
      }
   }

   public void setOnFinished(EventHandler<ActionEvent> onFinished) {
      this.onFinished = onFinished;
   }

   public void slideIn() {
      if (this.status != SlidingAnimation.SlidingStatus.IN && this.status != SlidingAnimation.SlidingStatus.SLIDING_IN) {
         if (this.animation != null && this.animation.getStatus().equals(Status.RUNNING)) {
            this.animation.stop();
         }

         double visibleWidth = this.getVisibleWidth();
         if (!this.translate) {
            visibleWidth -= SlidingAnimation.SlidingDirection.RTL.equals(this.direction) ? this.initialPadding.getRight() : this.initialPadding.getLeft();
         }

         this.slide(-visibleWidth, SlidingAnimation.SlidingStatus.IN);
      }
   }

   public void slideOut() {
      if (this.status != SlidingAnimation.SlidingStatus.OUT && this.status != SlidingAnimation.SlidingStatus.SLIDING_OUT) {
         if (this.animation != null && this.animation.getStatus().equals(Status.RUNNING)) {
            this.animation.stop();
         }

         this.resetPosition();
         double visibleWidth = this.getVisibleWidth();
         double slidingRegionLayoutWidth = this.getSlidingRegionWidth();
         this.slide(slidingRegionLayoutWidth - visibleWidth, SlidingAnimation.SlidingStatus.OUT);
      }
   }

   public void showColumns(int columns) {
      if (!(this.slidingRegion instanceof GridPane slidingGridPane)) {
         throw new UnsupportedOperationException("Sliding region is not a GridPane");
      } else {
         int columnCount = slidingGridPane.getColumnCount();
         Validate.inclusiveBetween(0L, (long)columnCount, (long)columns, "Invalid number of columns: " + columns);
         double width = 0.0;
         if (SlidingAnimation.SlidingDirection.RTL.equals(this.direction)) {
            for (int c = 0; c < columns; c++) {
               width += getColumnWidth(slidingGridPane, c);
            }
         } else {
            for (int c = columnCount - 1; c >= columnCount - columns; c--) {
               width += getColumnWidth(slidingGridPane, c);
            }
         }

         width += slidingGridPane.getHgap() * (double)(columns - 1);
         this.slideTo(width);
      }
   }

   public BooleanProperty animatedProperty() {
      return this.animated;
   }

   public boolean isAnimated() {
      return this.animated.get();
   }

   public void setAnimated(boolean animated) {
      this.animated.set(animated);
   }

   private double getVisibleWidth() {
      Bounds slidingRegionBoundsInLocal = this.slidingRegion.getLayoutBounds();
      Bounds slidingRegionBoundsInScene = this.slidingRegion.localToScene(slidingRegionBoundsInLocal);
      Bounds shrinkingPaneBoundsInLocal = this.shrinkingPane.getLayoutBounds();
      Bounds shrinkingPaneBoundsInScene = this.shrinkingPane.localToScene(shrinkingPaneBoundsInLocal);
      if (shrinkingPaneBoundsInScene.intersects(slidingRegionBoundsInScene)) {
         double intersectionRight = Math.min(shrinkingPaneBoundsInScene.getMaxX(), slidingRegionBoundsInScene.getMaxX());
         double intersectionLeft = Math.max(shrinkingPaneBoundsInScene.getMinX(), slidingRegionBoundsInScene.getMinX());
         return intersectionRight - intersectionLeft;
      } else {
         return 0.0;
      }
   }

   private void resetPosition() {
      if (this.translate && this.status == SlidingAnimation.SlidingStatus.IN) {
         double offsetX = -this.direction.getTranslateXFactor() * this.getSlidingRegionWidth();
         this.slidingRegion.setTranslateX(offsetX);
         this.shrinkingPane.setPadding(this.initialPadding);
      }
   }

   private void slideTo(double width) {
      if (this.animation != null && this.animation.getStatus().equals(Status.RUNNING)) {
         this.animation.stop();
      }

      double visibleWidth = this.getVisibleWidth();
      double slidingRegionLayoutWidth = this.getSlidingRegionWidth();
      double slidingWidth = Math.max(0.0, Math.min(width, slidingRegionLayoutWidth));
      if (slidingWidth == 0.0) {
         this.slideIn();
      } else if (slidingWidth == slidingRegionLayoutWidth) {
         this.slideOut();
      } else {
         this.slide(slidingWidth - visibleWidth, SlidingAnimation.SlidingStatus.HALFWAY);
      }
   }

   private double getSlidingRegionWidth() {
      double w = this.slidingRegion.getPrefWidth();
      if (w == -1.0) {
         w = this.slidingRegion.getLayoutBounds().getWidth();
      }

      return w;
   }

   private void slide(double slidingWidth, SlidingAnimation.SlidingStatus finalStatus) {
      if (this.shrinkingPane != null && slidingWidth != 0.0 && (this.animation == null || !this.animation.getStatus().equals(Status.RUNNING))) {
         if (!this.slidingRegion.isVisible()) {
            this.slidingRegion.setVisible(true);
         }

         if (this.isAnimated()) {
            this.slideWithAnimation(slidingWidth, finalStatus);
         } else {
            this.slideWithoutAnimation(slidingWidth, finalStatus);
         }
      }
   }

   private void slideWithAnimation(double slidingWidth, SlidingAnimation.SlidingStatus finalStatus) {
      int duration = 300;
      int cycles = (int)Math.abs(slidingWidth);
      if (cycles == 0) {
         this.logger.warn("Cycles is 0: no animation performed (slidingWidth: {})", slidingWidth);
         this.status = finalStatus;
         if (finalStatus == SlidingAnimation.SlidingStatus.IN) {
            this.slidingRegion.setVisible(false);
         }
      } else {
         int frame = duration / cycles;

         while (duration % cycles != 0) {
            duration = cycles * ++frame;
         }

         this.animation = new ParallelTransition();
         if (this.translate) {
            this.animation.getChildren().add(this.createTranslateAnimation(slidingWidth, duration));
         }

         if (this.mode == SlidingAnimation.SlidingMode.SLIDE_AND_PUSH) {
            Timeline paddingAnimation = this.createPaddingAnimation(frame, slidingWidth > 0.0 ? 1.0 : -1.0);
            paddingAnimation.setCycleCount(cycles);
            this.animation.getChildren().add(paddingAnimation);
         }

         this.animation.setOnFinished(e -> Platform.runLater(() -> {
               this.status = finalStatus;
               if (finalStatus == SlidingAnimation.SlidingStatus.IN) {
                  this.slidingRegion.setVisible(false);
               }

               if (this.onFinished != null) {
                  this.onFinished.handle(e);
               }
            }));

         this.status = switch (finalStatus) {
            case OUT -> SlidingAnimation.SlidingStatus.SLIDING_OUT;
            case IN -> SlidingAnimation.SlidingStatus.SLIDING_IN;
            case HALFWAY -> SlidingAnimation.SlidingStatus.SLIDING_HALFWAY;
            default -> throw new IllegalStateException("Invalid final status: " + finalStatus);
         };
         this.animation.play();
      }
   }

   private void slideWithoutAnimation(double slidingWidth, SlidingAnimation.SlidingStatus finalStatus) {
      this.animation = null;
      if (this.translate) {
         this.slidingRegion.setTranslateX(this.slidingRegion.getTranslateX() + this.direction.getTranslateXFactor() * slidingWidth);
      }

      if (this.mode == SlidingAnimation.SlidingMode.SLIDE_AND_PUSH) {
         if (SlidingAnimation.SlidingDirection.RTL.equals(this.direction)) {
            this.shrinkingPane
               .setPadding(
                  new Insets(
                     this.initialPadding.getTop(),
                     this.shrinkingPane.getPadding().getRight() + slidingWidth,
                     this.initialPadding.getBottom(),
                     this.initialPadding.getLeft()
                  )
               );
         } else {
            this.shrinkingPane
               .setPadding(
                  new Insets(
                     this.initialPadding.getTop(),
                     this.initialPadding.getRight(),
                     this.initialPadding.getBottom(),
                     this.shrinkingPane.getPadding().getLeft() + slidingWidth
                  )
               );
         }
      }

      this.status = finalStatus;
      if (finalStatus == SlidingAnimation.SlidingStatus.IN) {
         this.slidingRegion.setVisible(false);
      }

      if (this.onFinished != null) {
         this.onFinished.handle(new ActionEvent());
      }
   }

   private Timeline createPaddingAnimation(int frame, double increment) {
      Timeline paddingAnimation;
      if (SlidingAnimation.SlidingDirection.RTL.equals(this.direction)) {
         paddingAnimation = new Timeline(
            new KeyFrame[]{
               new KeyFrame(
                  new Duration((double)frame),
                  e -> this.shrinkingPane
                        .setPadding(
                           new Insets(
                              this.initialPadding.getTop(),
                              this.shrinkingPane.getPadding().getRight() + increment,
                              this.initialPadding.getBottom(),
                              this.initialPadding.getLeft()
                           )
                        ),
                  new KeyValue[0]
               )
            }
         );
      } else {
         paddingAnimation = new Timeline(
            new KeyFrame[]{
               new KeyFrame(
                  new Duration((double)frame),
                  e -> this.shrinkingPane
                        .setPadding(
                           new Insets(
                              this.initialPadding.getTop(),
                              this.initialPadding.getRight(),
                              this.initialPadding.getBottom(),
                              this.shrinkingPane.getPadding().getLeft() + increment
                           )
                        ),
                  new KeyValue[0]
               )
            }
         );
      }

      return paddingAnimation;
   }

   private TranslateTransition createTranslateAnimation(double slidingWidth, int duration) {
      TranslateTransition translator = new TranslateTransition(new Duration((double)duration), this.slidingRegion);
      double tx = this.slidingRegion.getTranslateX() + this.direction.getTranslateXFactor() * slidingWidth;
      translator.setToX(tx);
      return translator;
   }

   public static enum SlidingDirection {
      LTR(1.0),
      RTL(-1.0);

      private double translateXFactor;

      private SlidingDirection(double translateXFactor) {
         this.translateXFactor = translateXFactor;
      }

      public double getTranslateXFactor() {
         return this.translateXFactor;
      }
   }

   public static enum SlidingMode {
      SLIDE_AND_PUSH,
      SLIDE_OVER;
   }

   public static enum SlidingStatus {
      IN,
      OUT,
      HALFWAY,
      SLIDING_IN,
      SLIDING_OUT,
      SLIDING_HALFWAY;
   }
}
