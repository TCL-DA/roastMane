package javafx.scene.control.skin;

import com.sun.javafx.scene.NodeHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.animation.Transition;
import javafx.beans.binding.When;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.SizeConverter;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ProgressBarSkin extends ProgressIndicatorSkin {
   private StackPane bar;
   private StackPane track;
   private Region clipRegion;
   private double barWidth;
   private DoubleProperty indeterminateBarLength = null;
   private BooleanProperty indeterminateBarEscape = null;
   private BooleanProperty indeterminateBarFlip = null;
   private DoubleProperty indeterminateBarAnimationTime = null;
   boolean wasIndeterminate = false;

   public ProgressBarSkin(ProgressBar var1) {
      super(var1);
      this.barWidth = (double)((int)(var1.getWidth() - this.snappedLeftInset() - this.snappedRightInset()) * 2)
         * Math.min(1.0, Math.max(0.0, var1.getProgress()))
         / 2.0;
      this.registerChangeListener(var1.widthProperty(), var1x -> this.updateProgress());
      this.initialize();
      this.getSkinnable().requestLayout();
   }

   private DoubleProperty indeterminateBarLengthProperty() {
      if (this.indeterminateBarLength == null) {
         this.indeterminateBarLength = new StyleableDoubleProperty(60.0) {
            public Object getBean() {
               return ProgressBarSkin.this;
            }

            public String getName() {
               return "indeterminateBarLength";
            }

            public CssMetaData<ProgressBar, Number> getCssMetaData() {
               return ProgressBarSkin.StyleableProperties.INDETERMINATE_BAR_LENGTH;
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
               return ProgressBarSkin.this;
            }

            public String getName() {
               return "indeterminateBarEscape";
            }

            public CssMetaData<ProgressBar, Boolean> getCssMetaData() {
               return ProgressBarSkin.StyleableProperties.INDETERMINATE_BAR_ESCAPE;
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
               return ProgressBarSkin.this;
            }

            public String getName() {
               return "indeterminateBarFlip";
            }

            public CssMetaData<ProgressBar, Boolean> getCssMetaData() {
               return ProgressBarSkin.StyleableProperties.INDETERMINATE_BAR_FLIP;
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
               return ProgressBarSkin.this;
            }

            public String getName() {
               return "indeterminateBarAnimationTime";
            }

            public CssMetaData<ProgressBar, Number> getCssMetaData() {
               return ProgressBarSkin.StyleableProperties.INDETERMINATE_BAR_ANIMATION_TIME;
            }
         };
      }

      return this.indeterminateBarAnimationTime;
   }

   private double getIndeterminateBarAnimationTime() {
      return this.indeterminateBarAnimationTime == null ? 2.0 : this.indeterminateBarAnimationTime.get();
   }

   @Override
   public double computeBaselineOffset(double var1, double var3, double var5, double var7) {
      return Double.NEGATIVE_INFINITY;
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      return Math.max(100.0, var9 + this.bar.prefWidth(this.getSkinnable().getWidth()) + var5);
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      return var3 + this.bar.prefHeight(var1) + var7;
   }

   @Override
   protected double computeMaxWidth(double var1, double var3, double var5, double var7, double var9) {
      return this.getSkinnable().prefWidth(var1);
   }

   @Override
   protected double computeMaxHeight(double var1, double var3, double var5, double var7, double var9) {
      return this.getSkinnable().prefHeight(var1);
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      ProgressIndicator var9 = this.getSkinnable();
      boolean var10 = var9.isIndeterminate();
      this.clipRegion.resizeRelocate(0.0, 0.0, var5, var7);
      this.track.resizeRelocate(var1, var3, var5, var7);
      this.bar.resizeRelocate(var1, var3, var10 ? this.getIndeterminateBarLength() : this.barWidth, var7);
      this.track.setVisible(true);
      if (var10) {
         this.createIndeterminateTimeline();
         if (NodeHelper.isTreeShowing(this.getSkinnable())) {
            this.indeterminateTransition.play();
         }

         this.bar.setClip(this.clipRegion);
      } else if (this.indeterminateTransition != null) {
         this.indeterminateTransition.stop();
         this.indeterminateTransition = null;
         this.bar.setClip(null);
         this.bar.setScaleX(1.0);
         this.bar.setTranslateX(0.0);
         this.clipRegion.translateXProperty().unbind();
      }
   }

   @Override
   void initialize() {
      this.track = new StackPane();
      this.track.getStyleClass().setAll(new String[]{"track"});
      this.bar = new StackPane();
      this.bar.getStyleClass().setAll(new String[]{"bar"});
      this.getChildren().setAll(new Node[]{this.track, this.bar});
      this.clipRegion = new Region();
      this.bar.backgroundProperty().addListener((var1, var2, var3) -> {
         if (var3 != null && !var3.getFills().isEmpty()) {
            BackgroundFill[] var4 = new BackgroundFill[var3.getFills().size()];

            for (int var5 = 0; var5 < var3.getFills().size(); var5++) {
               BackgroundFill var6 = (BackgroundFill)var3.getFills().get(var5);
               var4[var5] = new BackgroundFill(Color.BLACK, var6.getRadii(), var6.getInsets());
            }

            this.clipRegion.setBackground(new Background(var4));
         }
      });
   }

   @Override
   void createIndeterminateTimeline() {
      if (this.indeterminateTransition != null) {
         this.indeterminateTransition.stop();
      }

      ProgressIndicator var1 = this.getSkinnable();
      double var2 = var1.getWidth() - (this.snappedLeftInset() + this.snappedRightInset());
      double var4 = this.getIndeterminateBarEscape() ? -this.getIndeterminateBarLength() : 0.0;
      double var6 = this.getIndeterminateBarEscape() ? var2 : var2 - this.getIndeterminateBarLength();
      this.indeterminateTransition = new ProgressBarSkin.IndeterminateTransition(var4, var6, this);
      this.indeterminateTransition.setCycleCount(-1);
      this.clipRegion
         .translateXProperty()
         .bind(
            new When(this.bar.scaleXProperty().isEqualTo(-1.0, 1.0E-100))
               .then(this.bar.translateXProperty().subtract(var2).add(this.indeterminateBarLengthProperty()))
               .otherwise(this.bar.translateXProperty().negate())
         );
   }

   @Override
   void updateProgress() {
      ProgressIndicator var1 = this.getSkinnable();
      boolean var2 = var1.isIndeterminate();
      if (!var2 || !this.wasIndeterminate) {
         this.barWidth = (double)((int)(var1.getWidth() - this.snappedLeftInset() - this.snappedRightInset()) * 2)
            * Math.min(1.0, Math.max(0.0, var1.getProgress()))
            / 2.0;
         this.getSkinnable().requestLayout();
      }

      this.wasIndeterminate = var2;
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return ProgressBarSkin.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   private static class IndeterminateTransition extends Transition {
      private final WeakReference<ProgressBarSkin> skin;
      private final double startX;
      private final double endX;
      private final boolean flip;

      public IndeterminateTransition(double var1, double var3, ProgressBarSkin var5) {
         this.startX = var1;
         this.endX = var3;
         this.skin = new WeakReference<>(var5);
         this.flip = var5.getIndeterminateBarFlip();
         var5.getIndeterminateBarEscape();
         this.setCycleDuration(Duration.seconds(var5.getIndeterminateBarAnimationTime() * (double)(this.flip ? 2 : 1)));
      }

      protected void interpolate(double var1) {
         ProgressBarSkin var3 = this.skin.get();
         if (var3 == null) {
            this.stop();
         } else if (!(var1 <= 0.5) && this.flip) {
            var3.bar.setScaleX(1.0);
            var3.bar.setTranslateX(this.startX + 2.0 * (1.0 - var1) * (this.endX - this.startX));
         } else {
            var3.bar.setScaleX(-1.0);
            var3.bar.setTranslateX(this.startX + (double)(this.flip ? 2 : 1) * var1 * (this.endX - this.startX));
         }
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<ProgressBar, Number> INDETERMINATE_BAR_LENGTH = new CssMetaData<ProgressBar, Number>(
         "-fx-indeterminate-bar-length", SizeConverter.getInstance(), 60.0
      ) {
         public boolean isSettable(ProgressBar var1) {
            ProgressBarSkin var2 = (ProgressBarSkin)var1.getSkin();
            return var2.indeterminateBarLength == null || !var2.indeterminateBarLength.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(ProgressBar var1) {
            ProgressBarSkin var2 = (ProgressBarSkin)var1.getSkin();
            return (StyleableProperty<Number>)var2.indeterminateBarLengthProperty();
         }
      };
      private static final CssMetaData<ProgressBar, Boolean> INDETERMINATE_BAR_ESCAPE = new CssMetaData<ProgressBar, Boolean>(
         "-fx-indeterminate-bar-escape", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(ProgressBar var1) {
            ProgressBarSkin var2 = (ProgressBarSkin)var1.getSkin();
            return var2.indeterminateBarEscape == null || !var2.indeterminateBarEscape.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(ProgressBar var1) {
            ProgressBarSkin var2 = (ProgressBarSkin)var1.getSkin();
            return (StyleableProperty<Boolean>)var2.indeterminateBarEscapeProperty();
         }
      };
      private static final CssMetaData<ProgressBar, Boolean> INDETERMINATE_BAR_FLIP = new CssMetaData<ProgressBar, Boolean>(
         "-fx-indeterminate-bar-flip", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(ProgressBar var1) {
            ProgressBarSkin var2 = (ProgressBarSkin)var1.getSkin();
            return var2.indeterminateBarFlip == null || !var2.indeterminateBarFlip.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(ProgressBar var1) {
            ProgressBarSkin var2 = (ProgressBarSkin)var1.getSkin();
            return (StyleableProperty<Boolean>)var2.indeterminateBarFlipProperty();
         }
      };
      private static final CssMetaData<ProgressBar, Number> INDETERMINATE_BAR_ANIMATION_TIME = new CssMetaData<ProgressBar, Number>(
         "-fx-indeterminate-bar-animation-time", SizeConverter.getInstance(), 2.0
      ) {
         public boolean isSettable(ProgressBar var1) {
            ProgressBarSkin var2 = (ProgressBarSkin)var1.getSkin();
            return var2.indeterminateBarAnimationTime == null || !var2.indeterminateBarAnimationTime.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(ProgressBar var1) {
            ProgressBarSkin var2 = (ProgressBarSkin)var1.getSkin();
            return (StyleableProperty<Number>)var2.indeterminateBarAnimationTimeProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(SkinBase.getClassCssMetaData());
         var0.add(INDETERMINATE_BAR_LENGTH);
         var0.add(INDETERMINATE_BAR_ESCAPE);
         var0.add(INDETERMINATE_BAR_FLIP);
         var0.add(INDETERMINATE_BAR_ANIMATION_TIME);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
