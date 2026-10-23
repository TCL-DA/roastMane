package com.cropster.roastingintelligence.ui.components.skin;

import com.cropster.roastingintelligence.ui.components.ProgressDot;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.TreeShowingExpression;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.beans.binding.When;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.SizeConverter;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ProgressDotSkin extends SkinBase<ProgressDot> {
   private static final double DOT_WIDTH = 6.0;
   private ProgressDot control;
   private StackPane bar;
   private StackPane track;
   private Region clipRegion;
   private TreeShowingExpression treeShowingExpression;
   private Animation indeterminateTransition;
   private boolean wasIndeterminate = false;
   private double barWidth;
   private DoubleProperty indeterminateBarLength = null;
   private BooleanProperty indeterminateBarEscape = null;
   private BooleanProperty indeterminateBarFlip = null;
   private DoubleProperty indeterminateBarAnimationTime = null;

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return ProgressDotSkin.StyleableProperties.STYLEABLES;
   }

   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   public ProgressDotSkin(ProgressDot control) {
      super(control);
      this.control = control;
      this.treeShowingExpression = new TreeShowingExpression(control);
      this.barWidth = (double)((int)(control.getWidth() - this.snappedLeftInset() - this.snappedRightInset()) * 2)
         * Math.min(1.0, Math.max(0.0, control.getProgress()))
         / 2.0;
      this.registerChangeListener(control.indeterminateProperty(), e -> this.initialize());
      this.registerChangeListener(control.progressProperty(), e -> this.updateProgress());
      this.registerChangeListener(control.widthProperty(), o -> this.updateProgress());
      this.registerChangeListener(control.sceneProperty(), e -> this.updateAnimation());
      this.registerChangeListener(this.treeShowingExpression, e -> this.updateAnimation());
      this.initialize();
      this.updateAnimation();
      ((ProgressDot)this.getSkinnable()).requestLayout();
   }

   private void updateProgress() {
      ProgressDot control = (ProgressDot)this.getSkinnable();
      boolean isIndeterminate = control.isIndeterminate();
      if (!isIndeterminate || !this.wasIndeterminate) {
         this.barWidth = (double)((int)(control.getWidth() - this.snappedLeftInset() - this.snappedRightInset()) * 2)
            * Math.min(1.0, Math.max(0.0, control.getProgress()))
            / 2.0;
         ((ProgressDot)this.getSkinnable()).requestLayout();
      }

      this.wasIndeterminate = isIndeterminate;
   }

   void updateAnimation() {
      ProgressIndicator control = (ProgressIndicator)this.getSkinnable();
      boolean isTreeShowing = NodeHelper.isTreeShowing(control) && control.getScene() != null;
      if (this.indeterminateTransition != null) {
         this.pauseTimeline(!isTreeShowing);
      } else if (isTreeShowing) {
         this.createIndeterminateTimeline();
      }
   }

   void pauseTimeline(boolean pause) {
      if (((ProgressDot)this.getSkinnable()).isIndeterminate()) {
         if (this.indeterminateTransition == null) {
            this.createIndeterminateTimeline();
         }

         if (pause) {
            this.indeterminateTransition.pause();
         } else {
            this.indeterminateTransition.play();
         }
      }
   }

   private void createIndeterminateTimeline() {
      if (this.indeterminateTransition != null) {
         this.indeterminateTransition.stop();
      }

      ProgressDot control = (ProgressDot)this.getSkinnable();
      double w = control.getWidth() - (this.snappedLeftInset() + this.snappedRightInset());
      double startX = this.getIndeterminateBarEscape() ? -this.getIndeterminateBarLength() : 0.0;
      double endX = this.getIndeterminateBarEscape() ? w : w - this.getIndeterminateBarLength();
      this.indeterminateTransition = new ProgressDotSkin.IndeterminateTransition(startX, endX, this);
      this.indeterminateTransition.setCycleCount(-1);
      this.clipRegion
         .translateXProperty()
         .bind(
            new When(this.bar.scaleXProperty().isEqualTo(-1.0, 1.0E-100))
               .then(this.bar.translateXProperty().subtract(w).add(this.indeterminateBarLengthProperty()))
               .otherwise(this.bar.translateXProperty().negate())
         );
   }

   private void initialize() {
      this.track = new StackPane();
      this.track.getStyleClass().setAll(new String[]{"track"});
      this.bar = new StackPane();
      this.bar.getStyleClass().setAll(new String[]{"bar"});
      this.getChildren().setAll(new Node[]{this.track, this.bar});
      this.clipRegion = new Region();
      this.bar.backgroundProperty().addListener((observable, oldValue, newValue) -> {
         if (newValue != null && !newValue.getFills().isEmpty()) {
            BackgroundFill[] fills = new BackgroundFill[newValue.getFills().size()];

            for (int i = 0; i < newValue.getFills().size(); i++) {
               BackgroundFill bf = (BackgroundFill)newValue.getFills().get(i);
               fills[i] = new BackgroundFill(Color.BLACK, bf.getRadii(), bf.getInsets());
            }

            this.clipRegion.setBackground(new Background(fills));
         }
      });
   }

   public void dispose() {
      super.dispose();
      this.treeShowingExpression.dispose();
      if (this.indeterminateTransition != null) {
         this.indeterminateTransition.stop();
         this.indeterminateTransition = null;
      }

      this.control = null;
   }

   protected void layoutChildren(double x, double y, double w, double h) {
      ProgressDot control = (ProgressDot)this.getSkinnable();
      boolean isIndeterminate = control.isIndeterminate();
      this.clipRegion.resizeRelocate(0.0, 0.0, w, h);
      this.track.resizeRelocate(x, y, w, h);
      if (isIndeterminate) {
         this.bar.resizeRelocate(x, y, this.getIndeterminateBarLength(), h);
      } else if (this.barWidth < 3.0) {
         double outsideBarWidth = 3.0 - this.barWidth;
         this.bar.resizeRelocate(0.0, y, 6.0 - outsideBarWidth, h);
      } else if (this.barWidth > w - 3.0) {
         double outsideBarWidth = this.barWidth - (w - 3.0);
         this.bar.resizeRelocate(this.barWidth - 3.0, y, 6.0 - outsideBarWidth, h);
      } else {
         this.bar.resizeRelocate(this.barWidth - 3.0, y, 6.0, h);
      }

      this.track.setVisible(true);
      if (isIndeterminate) {
         this.createIndeterminateTimeline();
         if (NodeHelper.isTreeShowing(this.getSkinnable())) {
            this.indeterminateTransition.play();
         }

         this.bar.setClip(this.clipRegion);
      } else if (this.indeterminateTransition != null) {
         this.indeterminateTransition.stop();
         this.indeterminateTransition = null;
         this.bar.setScaleX(1.0);
         this.bar.setTranslateX(0.0);
         this.bar.setClip(null);
         this.bar.setScaleX(1.0);
         this.bar.setTranslateX(0.0);
         this.clipRegion.translateXProperty().unbind();
      }
   }

   public double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
      return Double.NEGATIVE_INFINITY;
   }

   protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return 0.0;
   }

   protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return 0.0;
   }

   protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return Math.max(100.0, leftInset + this.bar.prefWidth(((ProgressDot)this.getSkinnable()).getWidth()) + rightInset);
   }

   protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return topInset + this.bar.prefHeight(width) + bottomInset;
   }

   protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return ((ProgressDot)this.getSkinnable()).prefWidth(height);
   }

   protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return ((ProgressDot)this.getSkinnable()).prefHeight(width);
   }

   private DoubleProperty indeterminateBarLengthProperty() {
      if (this.indeterminateBarLength == null) {
         this.indeterminateBarLength = new StyleableDoubleProperty(60.0) {
            public Object getBean() {
               return ProgressDotSkin.this;
            }

            public String getName() {
               return "indeterminateBarLength";
            }

            public CssMetaData<ProgressDot, Number> getCssMetaData() {
               return ProgressDotSkin.StyleableProperties.INDETERMINATE_BAR_LENGTH;
            }
         };
      }

      return this.indeterminateBarLength;
   }

   private Double getIndeterminateBarLength() {
      return this.indeterminateBarLength == null ? 60.0 : this.indeterminateBarLength.get();
   }

   private BooleanProperty indeterminateBarEscapeProperty() {
      if (this.indeterminateBarEscape == null) {
         this.indeterminateBarEscape = new StyleableBooleanProperty(true) {
            public Object getBean() {
               return ProgressDotSkin.this;
            }

            public String getName() {
               return "indeterminateBarEscape";
            }

            public CssMetaData<ProgressDot, Boolean> getCssMetaData() {
               return ProgressDotSkin.StyleableProperties.INDETERMINATE_BAR_ESCAPE;
            }
         };
      }

      return this.indeterminateBarEscape;
   }

   private Boolean getIndeterminateBarEscape() {
      return this.indeterminateBarEscape == null ? true : this.indeterminateBarEscape.get();
   }

   private BooleanProperty indeterminateBarFlipProperty() {
      if (this.indeterminateBarFlip == null) {
         this.indeterminateBarFlip = new StyleableBooleanProperty(true) {
            public Object getBean() {
               return ProgressDotSkin.this;
            }

            public String getName() {
               return "indeterminateBarFlip";
            }

            public CssMetaData<ProgressDot, Boolean> getCssMetaData() {
               return ProgressDotSkin.StyleableProperties.INDETERMINATE_BAR_FLIP;
            }
         };
      }

      return this.indeterminateBarFlip;
   }

   private Boolean getIndeterminateBarFlip() {
      return this.indeterminateBarFlip == null ? true : this.indeterminateBarFlip.get();
   }

   private DoubleProperty indeterminateBarAnimationTimeProperty() {
      if (this.indeterminateBarAnimationTime == null) {
         this.indeterminateBarAnimationTime = new StyleableDoubleProperty(2.0) {
            public Object getBean() {
               return ProgressDotSkin.this;
            }

            public String getName() {
               return "indeterminateBarAnimationTime";
            }

            public CssMetaData<ProgressDot, Number> getCssMetaData() {
               return ProgressDotSkin.StyleableProperties.INDETERMINATE_BAR_ANIMATION_TIME;
            }
         };
      }

      return this.indeterminateBarAnimationTime;
   }

   private double getIndeterminateBarAnimationTime() {
      return this.indeterminateBarAnimationTime == null ? 2.0 : this.indeterminateBarAnimationTime.get();
   }

   private static class IndeterminateTransition extends Transition {
      private final WeakReference<ProgressDotSkin> skin;
      private final double startX;
      private final double endX;
      private final boolean flip;

      IndeterminateTransition(double startX, double endX, ProgressDotSkin progressDotSkin) {
         this.startX = startX;
         this.endX = endX;
         this.skin = new WeakReference<>(progressDotSkin);
         this.flip = progressDotSkin.getIndeterminateBarFlip();
         progressDotSkin.getIndeterminateBarEscape();
         this.setCycleDuration(Duration.seconds(progressDotSkin.getIndeterminateBarAnimationTime() * (double)(this.flip ? 2 : 1)));
      }

      protected void interpolate(double frac) {
         ProgressDotSkin s = this.skin.get();
         if (s == null) {
            this.stop();
         } else if (!(frac <= 0.5) && this.flip) {
            s.bar.setScaleX(1.0);
            s.bar.setTranslateX(this.startX + 2.0 * (1.0 - frac) * (this.endX - this.startX));
         } else {
            s.bar.setScaleX(-1.0);
            s.bar.setTranslateX(this.startX + (double)(this.flip ? 2 : 1) * frac * (this.endX - this.startX));
         }
      }
   }

   private static final class StyleableProperties {
      private static final CssMetaData<ProgressDot, Number> INDETERMINATE_BAR_LENGTH = new CssMetaData<ProgressDot, Number>(
         "-fx-indeterminate-bar-length", SizeConverter.getInstance(), 60.0
      ) {
         public boolean isSettable(ProgressDot n) {
            ProgressDotSkin skin = (ProgressDotSkin)n.getSkin();
            return skin.indeterminateBarLength == null || !skin.indeterminateBarLength.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(ProgressDot n) {
            ProgressDotSkin skin = (ProgressDotSkin)n.getSkin();
            return (StyleableProperty<Number>)skin.indeterminateBarLengthProperty();
         }
      };
      private static final CssMetaData<ProgressDot, Boolean> INDETERMINATE_BAR_ESCAPE = new CssMetaData<ProgressDot, Boolean>(
         "-fx-indeterminate-bar-escape", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(ProgressDot n) {
            ProgressDotSkin skin = (ProgressDotSkin)n.getSkin();
            return skin.indeterminateBarEscape == null || !skin.indeterminateBarEscape.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(ProgressDot n) {
            ProgressDotSkin skin = (ProgressDotSkin)n.getSkin();
            return (StyleableProperty<Boolean>)skin.indeterminateBarEscapeProperty();
         }
      };
      private static final CssMetaData<ProgressDot, Boolean> INDETERMINATE_BAR_FLIP = new CssMetaData<ProgressDot, Boolean>(
         "-fx-indeterminate-bar-flip", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(ProgressDot n) {
            ProgressDotSkin skin = (ProgressDotSkin)n.getSkin();
            return skin.indeterminateBarFlip == null || !skin.indeterminateBarFlip.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(ProgressDot n) {
            ProgressDotSkin skin = (ProgressDotSkin)n.getSkin();
            return (StyleableProperty<Boolean>)skin.indeterminateBarFlipProperty();
         }
      };
      private static final CssMetaData<ProgressDot, Number> INDETERMINATE_BAR_ANIMATION_TIME = new CssMetaData<ProgressDot, Number>(
         "-fx-indeterminate-bar-animation-time", SizeConverter.getInstance(), 2.0
      ) {
         public boolean isSettable(ProgressDot n) {
            ProgressDotSkin skin = (ProgressDotSkin)n.getSkin();
            return skin.indeterminateBarAnimationTime == null || !skin.indeterminateBarAnimationTime.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(ProgressDot n) {
            ProgressDotSkin skin = (ProgressDotSkin)n.getSkin();
            return (StyleableProperty<Number>)skin.indeterminateBarAnimationTimeProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(SkinBase.getClassCssMetaData());
         styleables.add(INDETERMINATE_BAR_LENGTH);
         styleables.add(INDETERMINATE_BAR_ESCAPE);
         styleables.add(INDETERMINATE_BAR_FLIP);
         styleables.add(INDETERMINATE_BAR_ANIMATION_TIME);
         STYLEABLES = Collections.unmodifiableList(styleables);
      }
   }
}
