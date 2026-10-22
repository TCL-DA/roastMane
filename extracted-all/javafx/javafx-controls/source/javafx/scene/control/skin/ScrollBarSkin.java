package javafx.scene.control.skin;

import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.behavior.ScrollBarBehavior;
import com.sun.javafx.util.Utils;
import java.util.function.Consumer;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class ScrollBarSkin extends SkinBase<ScrollBar> {
   private final ScrollBarBehavior behavior;
   private StackPane thumb;
   private StackPane trackBackground;
   private StackPane track;
   private ScrollBarSkin.EndButton incButton;
   private ScrollBarSkin.EndButton decButton;
   private double trackLength;
   private double thumbLength;
   private double preDragThumbPos;
   private Point2D dragStart;
   private double trackPos;

   public ScrollBarSkin(ScrollBar var1) {
      super(var1);
      this.behavior = new ScrollBarBehavior(var1);
      this.initialize();
      this.getSkinnable().requestLayout();
      Consumer var2 = var1x -> {
         this.positionThumb();
         this.getSkinnable().requestLayout();
      };
      this.registerChangeListener(var1.minProperty(), var2);
      this.registerChangeListener(var1.maxProperty(), var2);
      this.registerChangeListener(var1.visibleAmountProperty(), var2);
      this.registerChangeListener(var1.valueProperty(), var1x -> this.positionThumb());
      this.registerChangeListener(var1.orientationProperty(), var1x -> this.getSkinnable().requestLayout());
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
      ScrollBar var9 = this.getSkinnable();
      double var10;
      if (var9.getMax() > var9.getMin()) {
         var10 = var9.getVisibleAmount() / (var9.getMax() - var9.getMin());
      } else {
         var10 = 1.0;
      }

      if (var9.getOrientation() == Orientation.VERTICAL) {
         if (!Properties.IS_TOUCH_SUPPORTED) {
            double var12 = this.snapSizeY(this.decButton.prefHeight(-1.0));
            double var14 = this.snapSizeY(this.incButton.prefHeight(-1.0));
            this.decButton.resize(var5, var12);
            this.incButton.resize(var5, var14);
            this.trackLength = this.snapSizeY(var7 - (var12 + var14));
            this.thumbLength = this.snapSizeY(Utils.clamp(this.minThumbLength(), this.trackLength * var10, this.trackLength));
            this.trackBackground.resizeRelocate(this.snapPositionX(var1), this.snapPositionY(var3), var5, this.trackLength + var12 + var14);
            this.decButton.relocate(this.snapPositionX(var1), this.snapPositionY(var3));
            this.incButton.relocate(this.snapPositionX(var1), this.snapPositionY(var3 + var7 - var14));
            this.track.resizeRelocate(this.snapPositionX(var1), this.snapPositionY(var3 + var12), var5, this.trackLength);
            this.thumb.resize(this.snapSizeX(var1 >= 0.0 ? var5 : var5 + var1), this.thumbLength);
            this.positionThumb();
         } else {
            this.trackLength = this.snapSizeY(var7);
            this.thumbLength = this.snapSizeY(Utils.clamp(this.minThumbLength(), this.trackLength * var10, this.trackLength));
            this.track.resizeRelocate(this.snapPositionX(var1), this.snapPositionY(var3), var5, this.trackLength);
            this.thumb.resize(this.snapSizeX(var1 >= 0.0 ? var5 : var5 + var1), this.thumbLength);
            this.positionThumb();
         }
      } else {
         if (!Properties.IS_TOUCH_SUPPORTED) {
            double var16 = this.snapSizeX(this.decButton.prefWidth(-1.0));
            double var17 = this.snapSizeX(this.incButton.prefWidth(-1.0));
            this.decButton.resize(var16, var7);
            this.incButton.resize(var17, var7);
            this.trackLength = this.snapSizeX(var5 - (var16 + var17));
            this.thumbLength = this.snapSizeX(Utils.clamp(this.minThumbLength(), this.trackLength * var10, this.trackLength));
            this.trackBackground.resizeRelocate(this.snapPositionX(var1), this.snapPositionY(var3), this.trackLength + var16 + var17, var7);
            this.decButton.relocate(this.snapPositionX(var1), this.snapPositionY(var3));
            this.incButton.relocate(this.snapPositionX(var1 + var5 - var17), this.snapPositionY(var3));
            this.track.resizeRelocate(this.snapPositionX(var1 + var16), this.snapPositionY(var3), this.trackLength, var7);
            this.thumb.resize(this.thumbLength, this.snapSizeY(var3 >= 0.0 ? var7 : var7 + var3));
            this.positionThumb();
         } else {
            this.trackLength = this.snapSizeX(var5);
            this.thumbLength = this.snapSizeX(Utils.clamp(this.minThumbLength(), this.trackLength * var10, this.trackLength));
            this.track.resizeRelocate(this.snapPositionX(var1), this.snapPositionY(var3), this.trackLength, var7);
            this.thumb.resize(this.thumbLength, this.snapSizeY(var3 >= 0.0 ? var7 : var7 + var3));
            this.positionThumb();
         }

         var9.resize(this.snapSizeX(var9.getWidth()), this.snapSizeY(var9.getHeight()));
      }

      if (var9.getOrientation() == Orientation.VERTICAL
            && var7
               >= this.computeMinHeight(-1.0, (double)((int)var3), this.snappedRightInset(), this.snappedBottomInset(), (double)((int)var1))
                  - (var3 + this.snappedBottomInset())
         || var9.getOrientation() == Orientation.HORIZONTAL
            && var5
               >= this.computeMinWidth(-1.0, (double)((int)var3), this.snappedRightInset(), this.snappedBottomInset(), (double)((int)var1))
                  - (var1 + this.snappedRightInset())) {
         this.trackBackground.setVisible(true);
         this.track.setVisible(true);
         this.thumb.setVisible(true);
         if (!Properties.IS_TOUCH_SUPPORTED) {
            this.incButton.setVisible(true);
            this.decButton.setVisible(true);
         }
      } else {
         this.trackBackground.setVisible(false);
         this.track.setVisible(false);
         this.thumb.setVisible(false);
         if (!Properties.IS_TOUCH_SUPPORTED) {
            if (var7 >= this.decButton.computeMinWidth(-1.0)) {
               this.decButton.setVisible(true);
            } else {
               this.decButton.setVisible(false);
            }

            if (var7 >= this.incButton.computeMinWidth(-1.0)) {
               this.incButton.setVisible(true);
            } else {
               this.incButton.setVisible(false);
            }
         }
      }
   }

   @Override
   protected double computeMinWidth(double var1, double var3, double var5, double var7, double var9) {
      if (this.getSkinnable().getOrientation() == Orientation.VERTICAL) {
         return this.getBreadth();
      } else {
         return !Properties.IS_TOUCH_SUPPORTED
            ? this.decButton.minWidth(-1.0) + this.incButton.minWidth(-1.0) + this.minTrackLength() + var9 + var5
            : this.minTrackLength() + var9 + var5;
      }
   }

   @Override
   protected double computeMinHeight(double var1, double var3, double var5, double var7, double var9) {
      if (this.getSkinnable().getOrientation() == Orientation.VERTICAL) {
         return !Properties.IS_TOUCH_SUPPORTED
            ? this.decButton.minHeight(-1.0) + this.incButton.minHeight(-1.0) + this.minTrackLength() + var3 + var7
            : this.minTrackLength() + var3 + var7;
      } else {
         return this.getBreadth();
      }
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      ScrollBar var11 = this.getSkinnable();
      return var11.getOrientation() == Orientation.VERTICAL ? this.getBreadth() : 100.0 + var9 + var5;
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      ScrollBar var11 = this.getSkinnable();
      return var11.getOrientation() == Orientation.VERTICAL ? 100.0 + var3 + var7 : this.getBreadth();
   }

   @Override
   protected double computeMaxWidth(double var1, double var3, double var5, double var7, double var9) {
      ScrollBar var11 = this.getSkinnable();
      return var11.getOrientation() == Orientation.VERTICAL ? var11.prefWidth(-1.0) : Double.MAX_VALUE;
   }

   @Override
   protected double computeMaxHeight(double var1, double var3, double var5, double var7, double var9) {
      ScrollBar var11 = this.getSkinnable();
      return var11.getOrientation() == Orientation.VERTICAL ? Double.MAX_VALUE : var11.prefHeight(-1.0);
   }

   private void initialize() {
      this.track = new StackPane();
      this.track.getStyleClass().setAll(new String[]{"track"});
      this.trackBackground = new StackPane();
      this.trackBackground.getStyleClass().setAll(new String[]{"track-background"});
      this.thumb = new StackPane() {
         public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
            switch (var1) {
               case VALUE:
                  return ScrollBarSkin.this.getSkinnable().getValue();
               default:
                  return super.queryAccessibleAttribute(var1, var2);
            }
         }
      };
      this.thumb.getStyleClass().setAll(new String[]{"thumb"});
      this.thumb.setAccessibleRole(AccessibleRole.THUMB);
      if (!Properties.IS_TOUCH_SUPPORTED) {
         this.incButton = new ScrollBarSkin.EndButton("increment-button", "increment-arrow") {
            public void executeAccessibleAction(AccessibleAction var1, Object... var2) {
               switch (var1) {
                  case FIRE:
                     ScrollBarSkin.this.getSkinnable().increment();
                     break;
                  default:
                     super.executeAccessibleAction(var1, var2);
               }
            }
         };
         this.incButton.setAccessibleRole(AccessibleRole.INCREMENT_BUTTON);
         this.incButton.setOnMousePressed(var1 -> {
            if (!this.thumb.isVisible() || this.trackLength > this.thumbLength) {
               this.behavior.incButtonPressed();
            }

            var1.consume();
         });
         this.incButton.setOnMouseReleased(var1 -> {
            if (!this.thumb.isVisible() || this.trackLength > this.thumbLength) {
               this.behavior.incButtonReleased();
            }

            var1.consume();
         });
         this.decButton = new ScrollBarSkin.EndButton("decrement-button", "decrement-arrow") {
            public void executeAccessibleAction(AccessibleAction var1, Object... var2) {
               switch (var1) {
                  case FIRE:
                     ScrollBarSkin.this.getSkinnable().decrement();
                     break;
                  default:
                     super.executeAccessibleAction(var1, var2);
               }
            }
         };
         this.decButton.setAccessibleRole(AccessibleRole.DECREMENT_BUTTON);
         this.decButton.setOnMousePressed(var1 -> {
            if (!this.thumb.isVisible() || this.trackLength > this.thumbLength) {
               this.behavior.decButtonPressed();
            }

            var1.consume();
         });
         this.decButton.setOnMouseReleased(var1 -> {
            if (!this.thumb.isVisible() || this.trackLength > this.thumbLength) {
               this.behavior.decButtonReleased();
            }

            var1.consume();
         });
      }

      this.track.setOnMousePressed(var1 -> {
         if (!this.thumb.isPressed() && var1.getButton() == MouseButton.PRIMARY) {
            if (this.getSkinnable().getOrientation() == Orientation.VERTICAL) {
               if (this.trackLength != 0.0) {
                  this.behavior.trackPress(var1.getY() / this.trackLength);
                  var1.consume();
               }
            } else if (this.trackLength != 0.0) {
               this.behavior.trackPress(var1.getX() / this.trackLength);
               var1.consume();
            }
         }
      });
      this.track.setOnMouseReleased(var1 -> {
         this.behavior.trackRelease();
         var1.consume();
      });
      this.thumb.setOnMousePressed(var1 -> {
         if (var1.isSynthesized()) {
            var1.consume();
         } else {
            if (this.getSkinnable().getMax() > this.getSkinnable().getMin()) {
               this.dragStart = this.thumb.localToParent(var1.getX(), var1.getY());
               double var2 = Utils.clamp(this.getSkinnable().getMin(), this.getSkinnable().getValue(), this.getSkinnable().getMax());
               this.preDragThumbPos = (var2 - this.getSkinnable().getMin()) / (this.getSkinnable().getMax() - this.getSkinnable().getMin());
               var1.consume();
            }
         }
      });
      this.thumb
         .setOnMouseDragged(
            var1 -> {
               if (var1.isSynthesized()) {
                  var1.consume();
               } else {
                  if (this.getSkinnable().getMax() > this.getSkinnable().getMin()) {
                     if (this.trackLength > this.thumbLength) {
                        Point2D var2 = this.thumb.localToParent(var1.getX(), var1.getY());
                        if (this.dragStart == null) {
                           this.dragStart = this.thumb.localToParent(var1.getX(), var1.getY());
                        }

                        double var3 = this.getSkinnable().getOrientation() == Orientation.VERTICAL
                           ? var2.getY() - this.dragStart.getY()
                           : var2.getX() - this.dragStart.getX();
                        this.behavior.thumbDragged(this.preDragThumbPos + var3 / (this.trackLength - this.thumbLength));
                     }

                     var1.consume();
                  }
               }
            }
         );
      this.thumb.setOnScrollStarted(var1 -> {
         if (var1.isDirect() && this.getSkinnable().getMax() > this.getSkinnable().getMin()) {
            this.dragStart = this.thumb.localToParent(var1.getX(), var1.getY());
            double var2 = Utils.clamp(this.getSkinnable().getMin(), this.getSkinnable().getValue(), this.getSkinnable().getMax());
            this.preDragThumbPos = (var2 - this.getSkinnable().getMin()) / (this.getSkinnable().getMax() - this.getSkinnable().getMin());
            var1.consume();
         }
      });
      this.thumb
         .setOnScroll(
            var1 -> {
               if (var1.isDirect() && this.getSkinnable().getMax() > this.getSkinnable().getMin()) {
                  if (this.trackLength > this.thumbLength) {
                     Point2D var2 = this.thumb.localToParent(var1.getX(), var1.getY());
                     if (this.dragStart == null) {
                        this.dragStart = this.thumb.localToParent(var1.getX(), var1.getY());
                     }

                     double var3 = this.getSkinnable().getOrientation() == Orientation.VERTICAL
                        ? var2.getY() - this.dragStart.getY()
                        : var2.getX() - this.dragStart.getX();
                     this.behavior.thumbDragged(this.preDragThumbPos + var3 / (this.trackLength - this.thumbLength));
                  }

                  var1.consume();
               }
            }
         );
      this.getSkinnable().addEventHandler(ScrollEvent.SCROLL, var1 -> {
         if (this.trackLength > this.thumbLength) {
            double var2 = var1.getDeltaX();
            double var4 = var1.getDeltaY();
            var2 = Math.abs(var2) < Math.abs(var4) ? var4 : var2;
            ScrollBar var6 = this.getSkinnable();
            double var7 = this.getSkinnable().getOrientation() == Orientation.VERTICAL ? var4 : var2;
            if (var1.isDirect()) {
               if (this.trackLength > this.thumbLength) {
                  this.behavior.thumbDragged((this.getSkinnable().getOrientation() == Orientation.VERTICAL ? var1.getY() : var1.getX()) / this.trackLength);
                  var1.consume();
               }
            } else if (var7 > 0.0 && var6.getValue() > var6.getMin()) {
               var6.decrement();
               var1.consume();
            } else if (var7 < 0.0 && var6.getValue() < var6.getMax()) {
               var6.increment();
               var1.consume();
            }
         }
      });
      this.getChildren().clear();
      if (!Properties.IS_TOUCH_SUPPORTED) {
         this.getChildren().addAll(new Node[]{this.trackBackground, this.incButton, this.decButton, this.track, this.thumb});
      } else {
         this.getChildren().addAll(new Node[]{this.track, this.thumb});
      }
   }

   double getBreadth() {
      if (!Properties.IS_TOUCH_SUPPORTED) {
         return this.getSkinnable().getOrientation() == Orientation.VERTICAL
            ? Math.max(this.decButton.prefWidth(-1.0), this.incButton.prefWidth(-1.0)) + this.snappedLeftInset() + this.snappedRightInset()
            : Math.max(this.decButton.prefHeight(-1.0), this.incButton.prefHeight(-1.0)) + this.snappedTopInset() + this.snappedBottomInset();
      } else {
         return this.getSkinnable().getOrientation() == Orientation.VERTICAL
            ? Math.max(8.0, 8.0) + this.snappedLeftInset() + this.snappedRightInset()
            : Math.max(8.0, 8.0) + this.snappedTopInset() + this.snappedBottomInset();
      }
   }

   double minThumbLength() {
      return 1.5 * this.getBreadth();
   }

   double minTrackLength() {
      return 2.0 * this.getBreadth();
   }

   void positionThumb() {
      ScrollBar var1 = this.getSkinnable();
      double var2 = Utils.clamp(var1.getMin(), var1.getValue(), var1.getMax());
      this.trackPos = var1.getMax() - var1.getMin() > 0.0
         ? (this.trackLength - this.thumbLength) * (var2 - var1.getMin()) / (var1.getMax() - var1.getMin())
         : 0.0;
      if (!Properties.IS_TOUCH_SUPPORTED) {
         if (var1.getOrientation() == Orientation.VERTICAL) {
            this.trackPos = this.trackPos + this.decButton.prefHeight(-1.0);
         } else {
            this.trackPos = this.trackPos + this.decButton.prefWidth(-1.0);
         }
      }

      this.thumb
         .setTranslateX(this.snapPositionX(var1.getOrientation() == Orientation.VERTICAL ? this.snappedLeftInset() : this.trackPos + this.snappedLeftInset()));
      this.thumb
         .setTranslateY(this.snapPositionY(var1.getOrientation() == Orientation.VERTICAL ? this.trackPos + this.snappedTopInset() : this.snappedTopInset()));
   }

   private Node getThumb() {
      return this.thumb;
   }

   private Node getTrack() {
      return this.track;
   }

   private Node getIncrementButton() {
      return this.incButton;
   }

   private Node getDecrementButton() {
      return this.decButton;
   }

   private static class EndButton extends Region {
      private Region arrow;

      private EndButton(String var1, String var2) {
         this.getStyleClass().setAll(new String[]{var1});
         this.arrow = new Region();
         this.arrow.getStyleClass().setAll(new String[]{var2});
         this.getChildren().setAll(new Node[]{this.arrow});
         this.requestLayout();
      }

      protected void layoutChildren() {
         double var1 = this.snappedTopInset();
         double var3 = this.snappedLeftInset();
         double var5 = this.snappedBottomInset();
         double var7 = this.snappedRightInset();
         double var9 = this.snapSizeX(this.arrow.prefWidth(-1.0));
         double var11 = this.snapSizeY(this.arrow.prefHeight(-1.0));
         double var13 = this.snapPositionY((this.getHeight() - (var1 + var5 + var11)) / 2.0);
         double var15 = this.snapPositionX((this.getWidth() - (var3 + var7 + var9)) / 2.0);
         this.arrow.resizeRelocate(var15 + var3, var13 + var1, var9, var11);
      }

      protected double computeMinHeight(double var1) {
         return this.prefHeight(-1.0);
      }

      protected double computeMinWidth(double var1) {
         return this.prefWidth(-1.0);
      }

      protected double computePrefWidth(double var1) {
         double var3 = this.snappedLeftInset();
         double var5 = this.snappedRightInset();
         double var7 = this.snapSizeX(this.arrow.prefWidth(-1.0));
         return var3 + var7 + var5;
      }

      protected double computePrefHeight(double var1) {
         double var3 = this.snappedTopInset();
         double var5 = this.snappedBottomInset();
         double var7 = this.snapSizeY(this.arrow.prefHeight(-1.0));
         return var3 + var7 + var5;
      }
   }
}
