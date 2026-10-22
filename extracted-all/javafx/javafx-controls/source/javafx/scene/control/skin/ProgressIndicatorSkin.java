package javafx.scene.control.skin;

import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.TreeShowingExpression;
import com.sun.javafx.scene.control.skin.Utils;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.NodeOrientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.util.Duration;

public class ProgressIndicatorSkin extends SkinBase<ProgressIndicator> {
   private final String DONE = ControlResources.getString("ProgressIndicator.doneString");
   final Duration CLIPPED_DELAY = new Duration(300.0);
   final Duration UNCLIPPED_DELAY = new Duration(0.0);
   private ProgressIndicatorSkin.IndeterminateSpinner spinner;
   private ProgressIndicatorSkin.DeterminateIndicator determinateIndicator;
   private ProgressIndicator control;
   private TreeShowingExpression treeShowingExpression;
   Animation indeterminateTransition;
   private ObjectProperty<Paint> progressColor = new StyleableObjectProperty<Paint>(null) {
      protected void invalidated() {
         Paint var1 = (Paint)this.get();
         if (var1 != null && !(var1 instanceof Color)) {
            if (this.isBound()) {
               this.unbind();
            }

            this.set(null);
            throw new IllegalArgumentException("Only Color objects are supported");
         } else {
            if (ProgressIndicatorSkin.this.spinner != null) {
               ProgressIndicatorSkin.this.spinner.setFillOverride(var1);
            }

            if (ProgressIndicatorSkin.this.determinateIndicator != null) {
               ProgressIndicatorSkin.this.determinateIndicator.setFillOverride(var1);
            }
         }
      }

      public Object getBean() {
         return ProgressIndicatorSkin.this;
      }

      public String getName() {
         return "progressColorProperty";
      }

      public CssMetaData<ProgressIndicator, Paint> getCssMetaData() {
         return ProgressIndicatorSkin.PROGRESS_COLOR;
      }
   };
   private IntegerProperty indeterminateSegmentCount = new StyleableIntegerProperty(8) {
      protected void invalidated() {
         if (ProgressIndicatorSkin.this.spinner != null) {
            ProgressIndicatorSkin.this.spinner.rebuild();
         }
      }

      public Object getBean() {
         return ProgressIndicatorSkin.this;
      }

      public String getName() {
         return "indeterminateSegmentCount";
      }

      public CssMetaData<ProgressIndicator, Number> getCssMetaData() {
         return ProgressIndicatorSkin.INDETERMINATE_SEGMENT_COUNT;
      }
   };
   private final BooleanProperty spinEnabled = new StyleableBooleanProperty(false) {
      protected void invalidated() {
         if (ProgressIndicatorSkin.this.spinner != null) {
            ProgressIndicatorSkin.this.spinner.setSpinEnabled(this.get());
         }
      }

      public CssMetaData<ProgressIndicator, Boolean> getCssMetaData() {
         return ProgressIndicatorSkin.SPIN_ENABLED;
      }

      public Object getBean() {
         return ProgressIndicatorSkin.this;
      }

      public String getName() {
         return "spinEnabled";
      }
   };
   private static final CssMetaData<ProgressIndicator, Paint> PROGRESS_COLOR = new CssMetaData<ProgressIndicator, Paint>(
      "-fx-progress-color", PaintConverter.getInstance(), null
   ) {
      public boolean isSettable(ProgressIndicator var1) {
         ProgressIndicatorSkin var2 = (ProgressIndicatorSkin)var1.getSkin();
         return var2.progressColor == null || !var2.progressColor.isBound();
      }

      public StyleableProperty<Paint> getStyleableProperty(ProgressIndicator var1) {
         ProgressIndicatorSkin var2 = (ProgressIndicatorSkin)var1.getSkin();
         return (StyleableProperty<Paint>)var2.progressColor;
      }
   };
   private static final CssMetaData<ProgressIndicator, Number> INDETERMINATE_SEGMENT_COUNT = new CssMetaData<ProgressIndicator, Number>(
      "-fx-indeterminate-segment-count", SizeConverter.getInstance(), 8
   ) {
      public boolean isSettable(ProgressIndicator var1) {
         ProgressIndicatorSkin var2 = (ProgressIndicatorSkin)var1.getSkin();
         return var2.indeterminateSegmentCount == null || !var2.indeterminateSegmentCount.isBound();
      }

      public StyleableProperty<Number> getStyleableProperty(ProgressIndicator var1) {
         ProgressIndicatorSkin var2 = (ProgressIndicatorSkin)var1.getSkin();
         return (StyleableProperty<Number>)var2.indeterminateSegmentCount;
      }
   };
   private static final CssMetaData<ProgressIndicator, Boolean> SPIN_ENABLED = new CssMetaData<ProgressIndicator, Boolean>(
      "-fx-spin-enabled", BooleanConverter.getInstance(), Boolean.FALSE
   ) {
      public boolean isSettable(ProgressIndicator var1) {
         ProgressIndicatorSkin var2 = (ProgressIndicatorSkin)var1.getSkin();
         return var2.spinEnabled == null || !var2.spinEnabled.isBound();
      }

      public StyleableProperty<Boolean> getStyleableProperty(ProgressIndicator var1) {
         ProgressIndicatorSkin var2 = (ProgressIndicatorSkin)var1.getSkin();
         return (StyleableProperty<Boolean>)var2.spinEnabled;
      }
   };
   private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

   public ProgressIndicatorSkin(ProgressIndicator var1) {
      super(var1);
      this.control = var1;
      this.treeShowingExpression = new TreeShowingExpression(var1);
      this.registerChangeListener(var1.indeterminateProperty(), var1x -> this.initialize());
      this.registerChangeListener(var1.progressProperty(), var1x -> this.updateProgress());
      this.registerChangeListener(var1.sceneProperty(), var1x -> this.updateAnimation());
      this.registerChangeListener(this.treeShowingExpression, var1x -> this.updateAnimation());
      this.initialize();
      this.updateAnimation();
   }

   Paint getProgressColor() {
      return (Paint)this.progressColor.get();
   }

   @Override
   public void dispose() {
      super.dispose();
      this.treeShowingExpression.dispose();
      if (this.indeterminateTransition != null) {
         this.indeterminateTransition.stop();
         this.indeterminateTransition = null;
      }

      if (this.spinner != null) {
         this.spinner = null;
      }

      this.control = null;
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      if (this.spinner != null && this.control.isIndeterminate()) {
         this.spinner.layoutChildren();
         this.spinner.resizeRelocate(0.0, 0.0, var5, var7);
      } else if (this.determinateIndicator != null) {
         this.determinateIndicator.layoutChildren();
         this.determinateIndicator.resizeRelocate(0.0, 0.0, var5, var7);
      }
   }

   @Override
   protected double computeMinWidth(double var1, double var3, double var5, double var7, double var9) {
      double var11 = 0.0;
      if (this.spinner != null && this.control.isIndeterminate()) {
         var11 = this.spinner.minWidth(-1.0);
      } else if (this.determinateIndicator != null) {
         var11 = this.determinateIndicator.minWidth(-1.0);
      }

      return var11;
   }

   @Override
   protected double computeMinHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = 0.0;
      if (this.spinner != null && this.control.isIndeterminate()) {
         var11 = this.spinner.minHeight(-1.0);
      } else if (this.determinateIndicator != null) {
         var11 = this.determinateIndicator.minHeight(-1.0);
      }

      return var11;
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      double var11 = 0.0;
      if (this.spinner != null && this.control.isIndeterminate()) {
         var11 = this.spinner.prefWidth(var1);
      } else if (this.determinateIndicator != null) {
         var11 = this.determinateIndicator.prefWidth(var1);
      }

      return var11;
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = 0.0;
      if (this.spinner != null && this.control.isIndeterminate()) {
         var11 = this.spinner.prefHeight(var1);
      } else if (this.determinateIndicator != null) {
         var11 = this.determinateIndicator.prefHeight(var1);
      }

      return var11;
   }

   @Override
   protected double computeMaxWidth(double var1, double var3, double var5, double var7, double var9) {
      return this.computePrefWidth(var1, var3, var5, var7, var9);
   }

   @Override
   protected double computeMaxHeight(double var1, double var3, double var5, double var7, double var9) {
      return this.computePrefHeight(var1, var3, var5, var7, var9);
   }

   void initialize() {
      boolean var1 = this.control.isIndeterminate();
      if (var1) {
         if (this.determinateIndicator != null) {
            this.determinateIndicator.unregisterListener();
         }

         this.determinateIndicator = null;
         this.spinner = new ProgressIndicatorSkin.IndeterminateSpinner(this.spinEnabled.get(), (Paint)this.progressColor.get());
         this.getChildren().setAll(new Node[]{this.spinner});
         if (NodeHelper.isTreeShowing(this.control) && this.indeterminateTransition != null) {
            this.indeterminateTransition.play();
         }
      } else {
         if (this.spinner != null) {
            if (this.indeterminateTransition != null) {
               this.indeterminateTransition.stop();
            }

            this.spinner = null;
         }

         this.determinateIndicator = new ProgressIndicatorSkin.DeterminateIndicator(this.control, this, (Paint)this.progressColor.get());
         this.getChildren().setAll(new Node[]{this.determinateIndicator});
      }
   }

   void updateProgress() {
      if (this.determinateIndicator != null) {
         this.determinateIndicator.updateProgress(this.control.getProgress());
      }
   }

   void createIndeterminateTimeline() {
      if (this.spinner != null) {
         this.spinner.rebuildTimeline();
      }
   }

   void pauseTimeline(boolean var1) {
      if (this.getSkinnable().isIndeterminate()) {
         if (this.indeterminateTransition == null) {
            this.createIndeterminateTimeline();
         }

         if (var1) {
            this.indeterminateTransition.pause();
         } else {
            this.indeterminateTransition.play();
         }
      }
   }

   void updateAnimation() {
      ProgressIndicator var1 = this.getSkinnable();
      boolean var2 = NodeHelper.isTreeShowing(var1) && var1.getScene() != null;
      if (this.indeterminateTransition != null) {
         this.pauseTimeline(!var2);
      } else if (var2) {
         this.createIndeterminateTimeline();
      }
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   static {
      ArrayList var0 = new ArrayList<>(SkinBase.getClassCssMetaData());
      var0.add(PROGRESS_COLOR);
      var0.add(INDETERMINATE_SEGMENT_COUNT);
      var0.add(SPIN_ENABLED);
      STYLEABLES = Collections.unmodifiableList(var0);
   }

   private final class DeterminateIndicator extends Region {
      private double textGap = 2.0;
      private int intProgress;
      private int degProgress;
      private Text text;
      private StackPane indicator;
      private StackPane progress;
      private StackPane tick;
      private Arc arcShape;
      private Circle indicatorCircle;
      private double doneTextWidth;
      private double doneTextHeight;

      public DeterminateIndicator(ProgressIndicator var2, ProgressIndicatorSkin var3, Paint var4) {
         this.getStyleClass().add("determinate-indicator");
         this.intProgress = (int)Math.round(var2.getProgress() * 100.0);
         this.degProgress = (int)(360.0 * var2.getProgress());
         this.getChildren().clear();
         this.text = new Text(var2.getProgress() >= 1.0 ? ProgressIndicatorSkin.this.DONE : this.intProgress + "%");
         this.text.setTextOrigin(VPos.TOP);
         this.text.getStyleClass().setAll(new String[]{"text", "percentage"});
         ProgressIndicatorSkin.this.registerChangeListener(this.text.fontProperty(), var1x -> {
            this.doneTextWidth = Utils.computeTextWidth(this.text.getFont(), ProgressIndicatorSkin.this.DONE, 0.0);
            this.doneTextHeight = Utils.computeTextHeight(this.text.getFont(), ProgressIndicatorSkin.this.DONE, 0.0, TextBoundsType.LOGICAL_VERTICAL_CENTER);
         });
         this.indicator = new StackPane();
         this.indicator.setScaleShape(false);
         this.indicator.setCenterShape(false);
         this.indicator.getStyleClass().setAll(new String[]{"indicator"});
         this.indicatorCircle = new Circle();
         this.indicator.setShape(this.indicatorCircle);
         this.arcShape = new Arc();
         this.arcShape.setType(ArcType.ROUND);
         this.arcShape.setStartAngle(90.0);
         this.progress = new StackPane();
         this.progress.getStyleClass().setAll(new String[]{"progress"});
         this.progress.setScaleShape(false);
         this.progress.setCenterShape(false);
         this.progress.setShape(this.arcShape);
         this.progress.getChildren().clear();
         this.setFillOverride(var4);
         this.tick = new StackPane();
         this.tick.getStyleClass().setAll(new String[]{"tick"});
         this.getChildren().setAll(new Node[]{this.indicator, this.progress, this.text, this.tick});
         this.updateProgress(var2.getProgress());
      }

      private void unregisterListener() {
         ProgressIndicatorSkin.this.unregisterChangeListeners(this.text.fontProperty());
      }

      private void setFillOverride(Paint var1) {
         if (var1 instanceof Color) {
            Color var2 = (Color)var1;
            this.progress
               .setStyle(
                  "-fx-background-color: rgba("
                     + (int)(255.0 * var2.getRed())
                     + ","
                     + (int)(255.0 * var2.getGreen())
                     + ","
                     + (int)(255.0 * var2.getBlue())
                     + ","
                     + var2.getOpacity()
                     + ");"
               );
         } else {
            this.progress.setStyle(null);
         }
      }

      public boolean usesMirroring() {
         return false;
      }

      private void updateProgress(double var1) {
         this.intProgress = (int)Math.round(var1 * 100.0);
         this.text.setText(var1 >= 1.0 ? ProgressIndicatorSkin.this.DONE : this.intProgress + "%");
         this.degProgress = (int)(360.0 * var1);
         this.arcShape.setLength((double)(-this.degProgress));
         this.requestLayout();
      }

      protected void layoutChildren() {
         double var1 = ProgressIndicatorSkin.this.control.snappedLeftInset();
         double var3 = ProgressIndicatorSkin.this.control.snappedRightInset();
         double var5 = ProgressIndicatorSkin.this.control.snappedTopInset();
         double var7 = ProgressIndicatorSkin.this.control.snappedBottomInset();
         double var9 = ProgressIndicatorSkin.this.control.getWidth() - var1 - var3;
         double var11 = ProgressIndicatorSkin.this.control.getHeight() - var5 - var7 - this.textGap - this.doneTextHeight;
         double var13 = var9 / 2.0;
         double var15 = var11 / 2.0;
         double var17 = Math.floor(Math.min(var13, var15));
         double var19 = this.snapPositionX(var1 + var13);
         double var21 = this.snapPositionY(var5 + var17);
         double var23 = this.indicator.snappedLeftInset();
         double var25 = this.indicator.snappedRightInset();
         double var27 = this.indicator.snappedTopInset();
         double var29 = this.indicator.snappedBottomInset();
         double var31 = this.snapSizeX(Math.min(Math.min(var17 - var23, var17 - var25), Math.min(var17 - var27, var17 - var29)));
         this.indicatorCircle.setRadius(var17);
         this.indicator.setLayoutX(var19);
         this.indicator.setLayoutY(var21);
         this.arcShape.setRadiusX(var31);
         this.arcShape.setRadiusY(var31);
         this.progress.setLayoutX(var19);
         this.progress.setLayoutY(var21);
         double var33 = this.progress.snappedLeftInset();
         double var35 = this.progress.snappedRightInset();
         double var37 = this.progress.snappedTopInset();
         double var39 = this.progress.snappedBottomInset();
         double var41 = this.snapSizeX(Math.min(Math.min(var31 - var33, var31 - var35), Math.min(var31 - var37, var31 - var39)));
         double var43 = Math.ceil(Math.sqrt(var41 * var41 / 2.0));
         this.tick.setLayoutX(var19 - var43);
         this.tick.setLayoutY(var21 - var43);
         this.tick.resize(var43 + var43, var43 + var43);
         this.tick.setVisible(ProgressIndicatorSkin.this.control.getProgress() >= 1.0);
         double var45 = this.text.getLayoutBounds().getWidth();
         double var47 = this.text.getLayoutBounds().getHeight();
         if (ProgressIndicatorSkin.this.control.getWidth() >= var45 && ProgressIndicatorSkin.this.control.getHeight() >= var47) {
            if (!this.text.isVisible()) {
               this.text.setVisible(true);
            }

            this.text.setLayoutY(this.snapPositionY(var21 + var17 + this.textGap));
            this.text.setLayoutX(this.snapPositionX(var19 - var45 / 2.0));
         } else if (this.text.isVisible()) {
            this.text.setVisible(false);
         }
      }

      protected double computePrefWidth(double var1) {
         double var3 = ProgressIndicatorSkin.this.control.snappedLeftInset();
         double var5 = ProgressIndicatorSkin.this.control.snappedRightInset();
         double var7 = this.indicator.snappedLeftInset();
         double var9 = this.indicator.snappedRightInset();
         double var11 = this.indicator.snappedTopInset();
         double var13 = this.indicator.snappedBottomInset();
         double var15 = this.snapSizeX(Math.max(Math.max(var7, var9), Math.max(var11, var13)));
         double var17 = this.progress.snappedLeftInset();
         double var19 = this.progress.snappedRightInset();
         double var21 = this.progress.snappedTopInset();
         double var23 = this.progress.snappedBottomInset();
         double var25 = this.snapSizeX(Math.max(Math.max(var17, var19), Math.max(var21, var23)));
         double var27 = this.tick.snappedLeftInset();
         double var29 = this.tick.snappedRightInset();
         double var31 = var15 + var25 + var27 + var29 + var25 + var15;
         return var3 + Math.max(var31, this.doneTextWidth) + var5;
      }

      protected double computePrefHeight(double var1) {
         double var3 = ProgressIndicatorSkin.this.control.snappedTopInset();
         double var5 = ProgressIndicatorSkin.this.control.snappedBottomInset();
         double var7 = this.indicator.snappedLeftInset();
         double var9 = this.indicator.snappedRightInset();
         double var11 = this.indicator.snappedTopInset();
         double var13 = this.indicator.snappedBottomInset();
         double var15 = this.snapSizeY(Math.max(Math.max(var7, var9), Math.max(var11, var13)));
         double var17 = this.progress.snappedLeftInset();
         double var19 = this.progress.snappedRightInset();
         double var21 = this.progress.snappedTopInset();
         double var23 = this.progress.snappedBottomInset();
         double var25 = this.snapSizeY(Math.max(Math.max(var17, var19), Math.max(var21, var23)));
         double var27 = this.tick.snappedTopInset();
         double var29 = this.tick.snappedBottomInset();
         double var31 = var15 + var25 + var27 + var29 + var25 + var15;
         return var3 + var31 + this.textGap + this.doneTextHeight + var5;
      }

      protected double computeMaxWidth(double var1) {
         return this.computePrefWidth(var1);
      }

      protected double computeMaxHeight(double var1) {
         return this.computePrefHeight(var1);
      }
   }

   private final class IndeterminateSpinner extends Region {
      private ProgressIndicatorSkin.IndeterminateSpinner.IndicatorPaths pathsG;
      private final List<Double> opacities = new ArrayList<>();
      private boolean spinEnabled = false;
      private Paint fillOverride = null;

      private IndeterminateSpinner(boolean var2, Paint var3) {
         this.spinEnabled = var2;
         this.fillOverride = var3;
         this.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
         this.getStyleClass().setAll(new String[]{"spinner"});
         this.pathsG = new ProgressIndicatorSkin.IndeterminateSpinner.IndicatorPaths();
         this.getChildren().add(this.pathsG);
         this.rebuild();
         this.rebuildTimeline();
      }

      public void setFillOverride(Paint var1) {
         this.fillOverride = var1;
         this.rebuild();
      }

      public void setSpinEnabled(boolean var1) {
         this.spinEnabled = var1;
         this.rebuildTimeline();
      }

      private void rebuildTimeline() {
         if (this.spinEnabled) {
            if (ProgressIndicatorSkin.this.indeterminateTransition == null) {
               ProgressIndicatorSkin.this.indeterminateTransition = new Timeline();
               ProgressIndicatorSkin.this.indeterminateTransition.setCycleCount(-1);
               ProgressIndicatorSkin.this.indeterminateTransition.setDelay(ProgressIndicatorSkin.this.UNCLIPPED_DELAY);
            } else {
               ProgressIndicatorSkin.this.indeterminateTransition.stop();
               ((Timeline)ProgressIndicatorSkin.this.indeterminateTransition).getKeyFrames().clear();
            }

            ObservableList var1 = FXCollections.observableArrayList();
            var1.add(new KeyFrame(Duration.millis(1.0), new KeyValue[]{new KeyValue(this.pathsG.rotateProperty(), 360)}));
            var1.add(new KeyFrame(Duration.millis(3900.0), new KeyValue[]{new KeyValue(this.pathsG.rotateProperty(), 0)}));

            for (byte var2 = 100; var2 <= 3900; var2 += 100) {
               var1.add(new KeyFrame(Duration.millis((double)var2), var1x -> this.shiftColors(), new KeyValue[0]));
            }

            ((Timeline)ProgressIndicatorSkin.this.indeterminateTransition).getKeyFrames().setAll(var1);
            if (NodeHelper.isTreeShowing(ProgressIndicatorSkin.this.control)) {
               ProgressIndicatorSkin.this.indeterminateTransition.playFromStart();
            } else {
               ProgressIndicatorSkin.this.indeterminateTransition.jumpTo(Duration.ZERO);
            }
         } else if (ProgressIndicatorSkin.this.indeterminateTransition != null) {
            ProgressIndicatorSkin.this.indeterminateTransition.stop();
            ((Timeline)ProgressIndicatorSkin.this.indeterminateTransition).getKeyFrames().clear();
            ProgressIndicatorSkin.this.indeterminateTransition = null;
         }
      }

      protected void layoutChildren() {
         double var1 = ProgressIndicatorSkin.this.control.getWidth()
            - ProgressIndicatorSkin.this.control.snappedLeftInset()
            - ProgressIndicatorSkin.this.control.snappedRightInset();
         double var3 = ProgressIndicatorSkin.this.control.getHeight()
            - ProgressIndicatorSkin.this.control.snappedTopInset()
            - ProgressIndicatorSkin.this.control.snappedBottomInset();
         double var5 = this.pathsG.prefWidth(-1.0);
         double var7 = this.pathsG.prefHeight(-1.0);
         double var9 = var1 / var5;
         double var11 = var9;
         if (var9 * var7 > var3) {
            var11 = var3 / var7;
         }

         double var13 = var5 * var11;
         double var15 = var7 * var11;
         this.pathsG.resizeRelocate((var1 - var13) / 2.0, (var3 - var15) / 2.0, var13, var15);
      }

      private void rebuild() {
         int var1 = ProgressIndicatorSkin.this.indeterminateSegmentCount.get();
         this.opacities.clear();
         this.pathsG.getChildren().clear();
         double var2 = 0.8 / (double)(var1 - 1);

         for (int var4 = 0; var4 < var1; var4++) {
            Region var5 = new Region();
            var5.setScaleShape(false);
            var5.setCenterShape(false);
            var5.getStyleClass().addAll(new String[]{"segment", "segment" + var4});
            if (this.fillOverride instanceof Color) {
               Color var6 = (Color)this.fillOverride;
               var5.setStyle(
                  "-fx-background-color: rgba("
                     + (int)(255.0 * var6.getRed())
                     + ","
                     + (int)(255.0 * var6.getGreen())
                     + ","
                     + (int)(255.0 * var6.getBlue())
                     + ","
                     + var6.getOpacity()
                     + ");"
               );
            } else {
               var5.setStyle(null);
            }

            this.pathsG.getChildren().add(var5);
            this.opacities.add(Math.max(0.1, 1.0 - var2 * (double)var4));
         }
      }

      private void shiftColors() {
         if (this.opacities.size() > 0) {
            int var1 = ProgressIndicatorSkin.this.indeterminateSegmentCount.get();
            Collections.rotate(this.opacities, -1);

            for (int var2 = 0; var2 < var1; var2++) {
               ((Node)this.pathsG.getChildren().get(var2)).setOpacity(this.opacities.get(var2));
            }
         }
      }

      private class IndicatorPaths extends Pane {
         protected double computePrefWidth(double var1) {
            double var3 = 0.0;

            for (Node var6 : this.getChildren()) {
               if (var6 instanceof Region) {
                  Region var7 = (Region)var6;
                  if (var7.getShape() != null) {
                     var3 = Math.max(var3, var7.getShape().getLayoutBounds().getMaxX());
                  } else {
                     var3 = Math.max(var3, var7.prefWidth(var1));
                  }
               }
            }

            return var3;
         }

         protected double computePrefHeight(double var1) {
            double var3 = 0.0;

            for (Node var6 : this.getChildren()) {
               if (var6 instanceof Region) {
                  Region var7 = (Region)var6;
                  if (var7.getShape() != null) {
                     var3 = Math.max(var3, var7.getShape().getLayoutBounds().getMaxY());
                  } else {
                     var3 = Math.max(var3, var7.prefHeight(var1));
                  }
               }
            }

            return var3;
         }

         protected void layoutChildren() {
            double var1 = this.getWidth() / this.computePrefWidth(-1.0);

            for (Node var4 : this.getChildren()) {
               if (var4 instanceof Region) {
                  Region var5 = (Region)var4;
                  if (var5.getShape() != null) {
                     var5.resize(var5.getShape().getLayoutBounds().getMaxX(), var5.getShape().getLayoutBounds().getMaxY());
                     var5.getTransforms().setAll(new Transform[]{new Scale(var1, var1, 0.0, 0.0)});
                  } else {
                     var5.autosize();
                  }
               }
            }
         }
      }
   }
}
