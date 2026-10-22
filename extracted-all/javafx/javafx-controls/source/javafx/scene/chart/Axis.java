package javafx.scene.chart;

import com.sun.javafx.scene.NodeHelper;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.FontCssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public abstract class Axis<T> extends Region {
   Text measure = new Text();
   private Orientation effectiveOrientation;
   private double effectiveTickLabelRotation = Double.NaN;
   private Label axisLabel = new Label();
   private final Path tickMarkPath = new Path();
   private double oldLength = 0.0;
   boolean rangeValid = false;
   boolean measureInvalid = false;
   boolean tickLabelsVisibleInvalid = false;
   private BitSet labelsToSkip = new BitSet();
   private final ObservableList<Axis.TickMark<T>> tickMarks = FXCollections.observableArrayList();
   private final ObservableList<Axis.TickMark<T>> unmodifiableTickMarks = FXCollections.unmodifiableObservableList(this.tickMarks);
   private ObjectProperty<Side> side = new StyleableObjectProperty<Side>() {
      protected void invalidated() {
         Side var1 = (Side)this.get();
         Axis.this.pseudoClassStateChanged(Axis.TOP_PSEUDOCLASS_STATE, var1 == Side.TOP);
         Axis.this.pseudoClassStateChanged(Axis.RIGHT_PSEUDOCLASS_STATE, var1 == Side.RIGHT);
         Axis.this.pseudoClassStateChanged(Axis.BOTTOM_PSEUDOCLASS_STATE, var1 == Side.BOTTOM);
         Axis.this.pseudoClassStateChanged(Axis.LEFT_PSEUDOCLASS_STATE, var1 == Side.LEFT);
         Axis.this.requestAxisLayout();
      }

      public CssMetaData<Axis<?>, Side> getCssMetaData() {
         return Axis.StyleableProperties.SIDE;
      }

      public Object getBean() {
         return Axis.this;
      }

      public String getName() {
         return "side";
      }
   };
   private ObjectProperty<String> label = new ObjectPropertyBase<String>() {
      protected void invalidated() {
         Axis.this.axisLabel.setText((String)this.get());
         Axis.this.requestAxisLayout();
      }

      public Object getBean() {
         return Axis.this;
      }

      public String getName() {
         return "label";
      }
   };
   private BooleanProperty tickMarkVisible = new StyleableBooleanProperty(true) {
      protected void invalidated() {
         Axis.this.tickMarkPath.setVisible(this.get());
         Axis.this.requestAxisLayout();
      }

      public CssMetaData<Axis<?>, Boolean> getCssMetaData() {
         return Axis.StyleableProperties.TICK_MARK_VISIBLE;
      }

      public Object getBean() {
         return Axis.this;
      }

      public String getName() {
         return "tickMarkVisible";
      }
   };
   private BooleanProperty tickLabelsVisible = new StyleableBooleanProperty(true) {
      protected void invalidated() {
         for (Axis.TickMark var2 : Axis.this.tickMarks) {
            var2.setTextVisible(this.get());
         }

         Axis.this.tickLabelsVisibleInvalid = true;
         Axis.this.requestAxisLayout();
      }

      public CssMetaData<Axis<?>, Boolean> getCssMetaData() {
         return Axis.StyleableProperties.TICK_LABELS_VISIBLE;
      }

      public Object getBean() {
         return Axis.this;
      }

      public String getName() {
         return "tickLabelsVisible";
      }
   };
   private DoubleProperty tickLength = new StyleableDoubleProperty(8.0) {
      protected void invalidated() {
         if (Axis.this.tickLength.get() < 0.0 && !Axis.this.tickLength.isBound()) {
            Axis.this.tickLength.set(0.0);
         }

         Axis.this.requestAxisLayout();
      }

      public CssMetaData<Axis<?>, Number> getCssMetaData() {
         return Axis.StyleableProperties.TICK_LENGTH;
      }

      public Object getBean() {
         return Axis.this;
      }

      public String getName() {
         return "tickLength";
      }
   };
   private BooleanProperty autoRanging = new BooleanPropertyBase(true) {
      protected void invalidated() {
         if (this.get()) {
            Axis.this.requestAxisLayout();
         }
      }

      public Object getBean() {
         return Axis.this;
      }

      public String getName() {
         return "autoRanging";
      }
   };
   private ObjectProperty<Font> tickLabelFont = new StyleableObjectProperty<Font>(Font.font("System", 8.0)) {
      protected void invalidated() {
         Font var1 = (Font)this.get();
         Axis.this.measure.setFont(var1);

         for (Axis.TickMark var3 : Axis.this.getTickMarks()) {
            var3.textNode.setFont(var1);
         }

         Axis.this.measureInvalid = true;
         Axis.this.requestAxisLayout();
      }

      public CssMetaData<Axis<?>, Font> getCssMetaData() {
         return Axis.StyleableProperties.TICK_LABEL_FONT;
      }

      public Object getBean() {
         return Axis.this;
      }

      public String getName() {
         return "tickLabelFont";
      }
   };
   private ObjectProperty<Paint> tickLabelFill = new StyleableObjectProperty<Paint>(Color.BLACK) {
      protected void invalidated() {
         for (Axis.TickMark var2 : Axis.this.tickMarks) {
            var2.textNode.setFill(Axis.this.getTickLabelFill());
         }
      }

      public CssMetaData<Axis<?>, Paint> getCssMetaData() {
         return Axis.StyleableProperties.TICK_LABEL_FILL;
      }

      public Object getBean() {
         return Axis.this;
      }

      public String getName() {
         return "tickLabelFill";
      }
   };
   private DoubleProperty tickLabelGap = new StyleableDoubleProperty(3.0) {
      protected void invalidated() {
         Axis.this.requestAxisLayout();
      }

      public CssMetaData<Axis<?>, Number> getCssMetaData() {
         return Axis.StyleableProperties.TICK_LABEL_TICK_GAP;
      }

      public Object getBean() {
         return Axis.this;
      }

      public String getName() {
         return "tickLabelGap";
      }
   };
   private BooleanProperty animated = new SimpleBooleanProperty(this, "animated", true);
   private DoubleProperty tickLabelRotation = new DoublePropertyBase(0.0) {
      protected void invalidated() {
         if (Axis.this.isAutoRanging()) {
            Axis.this.invalidateRange();
         }

         Axis.this.requestAxisLayout();
      }

      public Object getBean() {
         return Axis.this;
      }

      public String getName() {
         return "tickLabelRotation";
      }
   };
   private static final PseudoClass TOP_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("top");
   private static final PseudoClass BOTTOM_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("bottom");
   private static final PseudoClass LEFT_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("left");
   private static final PseudoClass RIGHT_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("right");

   public ObservableList<Axis.TickMark<T>> getTickMarks() {
      return this.unmodifiableTickMarks;
   }

   public final Side getSide() {
      return (Side)this.side.get();
   }

   public final void setSide(Side var1) {
      this.side.set(var1);
   }

   public final ObjectProperty<Side> sideProperty() {
      return this.side;
   }

   final void setEffectiveOrientation(Orientation var1) {
      this.effectiveOrientation = var1;
   }

   final Side getEffectiveSide() {
      Side var1 = this.getSide();
      if (var1 == null
         || var1.isVertical() && this.effectiveOrientation == Orientation.HORIZONTAL
         || var1.isHorizontal() && this.effectiveOrientation == Orientation.VERTICAL) {
         return this.effectiveOrientation == Orientation.VERTICAL ? Side.LEFT : Side.BOTTOM;
      } else {
         return var1;
      }
   }

   public final String getLabel() {
      return (String)this.label.get();
   }

   public final void setLabel(String var1) {
      this.label.set(var1);
   }

   public final ObjectProperty<String> labelProperty() {
      return this.label;
   }

   public final boolean isTickMarkVisible() {
      return this.tickMarkVisible.get();
   }

   public final void setTickMarkVisible(boolean var1) {
      this.tickMarkVisible.set(var1);
   }

   public final BooleanProperty tickMarkVisibleProperty() {
      return this.tickMarkVisible;
   }

   public final boolean isTickLabelsVisible() {
      return this.tickLabelsVisible.get();
   }

   public final void setTickLabelsVisible(boolean var1) {
      this.tickLabelsVisible.set(var1);
   }

   public final BooleanProperty tickLabelsVisibleProperty() {
      return this.tickLabelsVisible;
   }

   public final double getTickLength() {
      return this.tickLength.get();
   }

   public final void setTickLength(double var1) {
      this.tickLength.set(var1);
   }

   public final DoubleProperty tickLengthProperty() {
      return this.tickLength;
   }

   public final boolean isAutoRanging() {
      return this.autoRanging.get();
   }

   public final void setAutoRanging(boolean var1) {
      this.autoRanging.set(var1);
   }

   public final BooleanProperty autoRangingProperty() {
      return this.autoRanging;
   }

   public final Font getTickLabelFont() {
      return (Font)this.tickLabelFont.get();
   }

   public final void setTickLabelFont(Font var1) {
      this.tickLabelFont.set(var1);
   }

   public final ObjectProperty<Font> tickLabelFontProperty() {
      return this.tickLabelFont;
   }

   public final Paint getTickLabelFill() {
      return (Paint)this.tickLabelFill.get();
   }

   public final void setTickLabelFill(Paint var1) {
      this.tickLabelFill.set(var1);
   }

   public final ObjectProperty<Paint> tickLabelFillProperty() {
      return this.tickLabelFill;
   }

   public final double getTickLabelGap() {
      return this.tickLabelGap.get();
   }

   public final void setTickLabelGap(double var1) {
      this.tickLabelGap.set(var1);
   }

   public final DoubleProperty tickLabelGapProperty() {
      return this.tickLabelGap;
   }

   public final boolean getAnimated() {
      return this.animated.get();
   }

   public final void setAnimated(boolean var1) {
      this.animated.set(var1);
   }

   public final BooleanProperty animatedProperty() {
      return this.animated;
   }

   public final double getTickLabelRotation() {
      return this.tickLabelRotation.getValue();
   }

   public final void setTickLabelRotation(double var1) {
      this.tickLabelRotation.setValue(var1);
   }

   public final DoubleProperty tickLabelRotationProperty() {
      return this.tickLabelRotation;
   }

   public Axis() {
      this.getStyleClass().setAll(new String[]{"axis"});
      this.axisLabel.getStyleClass().add("axis-label");
      this.axisLabel.setAlignment(Pos.CENTER);
      this.tickMarkPath.getStyleClass().add("axis-tick-mark");
      this.getChildren().addAll(new Node[]{this.axisLabel, this.tickMarkPath});
   }

   protected final boolean isRangeValid() {
      return this.rangeValid;
   }

   protected final void invalidateRange() {
      this.rangeValid = false;
   }

   protected final boolean shouldAnimate() {
      return this.getAnimated() && NodeHelper.isTreeShowing(this);
   }

   public void requestLayout() {
   }

   public void requestAxisLayout() {
      super.requestLayout();
   }

   public void invalidateRange(List<T> var1) {
      this.invalidateRange();
      this.requestAxisLayout();
   }

   protected abstract Object autoRange(double var1);

   protected abstract void setRange(Object var1, boolean var2);

   protected abstract Object getRange();

   public abstract double getZeroPosition();

   public abstract double getDisplayPosition(T var1);

   public abstract T getValueForDisplay(double var1);

   public abstract boolean isValueOnAxis(T var1);

   public abstract double toNumericValue(T var1);

   public abstract T toRealValue(double var1);

   protected abstract List<T> calculateTickValues(double var1, Object var3);

   protected double computePrefHeight(double var1) {
      Side var3 = this.getEffectiveSide();
      if (var3.isVertical()) {
         return 100.0;
      } else {
         Object var4 = this.autoRange(var1);
         double var5 = 0.0;
         if (this.isTickLabelsVisible()) {
            for (Object var9 : this.calculateTickValues(var1, var4)) {
               var5 = Math.max(var5, this.measureTickMarkSize((T)var9, var4).getHeight());
            }
         }

         double var11 = this.isTickMarkVisible() ? (this.getTickLength() > 0.0 ? this.getTickLength() : 0.0) : 0.0;
         double var12 = this.axisLabel.getText() != null && this.axisLabel.getText().length() != 0 ? this.axisLabel.prefHeight(-1.0) : 0.0;
         return var5 + this.getTickLabelGap() + var11 + var12;
      }
   }

   protected double computePrefWidth(double var1) {
      Side var3 = this.getEffectiveSide();
      if (!var3.isVertical()) {
         return 100.0;
      } else {
         Object var4 = this.autoRange(var1);
         double var5 = 0.0;
         if (this.isTickLabelsVisible()) {
            for (Object var9 : this.calculateTickValues(var1, var4)) {
               var5 = Math.max(var5, this.measureTickMarkSize((T)var9, var4).getWidth());
            }
         }

         double var11 = this.isTickMarkVisible() ? (this.getTickLength() > 0.0 ? this.getTickLength() : 0.0) : 0.0;
         double var12 = this.axisLabel.getText() != null && this.axisLabel.getText().length() != 0 ? this.axisLabel.prefHeight(-1.0) : 0.0;
         return var5 + this.getTickLabelGap() + var11 + var12;
      }
   }

   protected void tickMarksUpdated() {
   }

   protected void layoutChildren() {
      boolean var1 = this.oldLength == 0.0;
      Side var2 = this.getEffectiveSide();
      double var3 = var2.isVertical() ? this.getHeight() : this.getWidth();
      boolean var5 = !this.isRangeValid();
      boolean var6 = this.oldLength != var3;
      if (var6 || var5) {
         Object var7;
         if (!this.isAutoRanging()) {
            var7 = this.getRange();
         } else {
            var7 = this.autoRange(var3);
            this.setRange(var7, this.getAnimated() && !var1 && NodeHelper.isTreeShowing(this) && var5);
         }

         List var8 = this.calculateTickValues(var3, var7);

         for (Iterator var9 = this.tickMarks.iterator(); var9.hasNext(); var9.remove()) {
            Axis.TickMark var10 = (Axis.TickMark)var9.next();
            Axis.TickMark var11 = var10;
            if (this.shouldAnimate()) {
               FadeTransition var12 = new FadeTransition(Duration.millis(250.0), var10.textNode);
               var12.setToValue(0.0);
               var12.setOnFinished(var2x -> this.getChildren().remove(var11.textNode));
               var12.play();
            } else {
               this.getChildren().remove(var11.textNode);
            }
         }

         for (Object var20 : var8) {
            Axis.TickMark var21 = new Axis.TickMark();
            var21.setValue(var20);
            var21.textNode.setText(this.getTickMarkLabel((T)var20));
            var21.textNode.setFont(this.getTickLabelFont());
            var21.textNode.setFill(this.getTickLabelFill());
            var21.setTextVisible(this.isTickLabelsVisible());
            if (this.shouldAnimate()) {
               var21.textNode.setOpacity(0.0);
            }

            this.getChildren().add(var21.textNode);
            this.tickMarks.add(var21);
            if (this.shouldAnimate()) {
               FadeTransition var13 = new FadeTransition(Duration.millis(750.0), var21.textNode);
               var13.setFromValue(0.0);
               var13.setToValue(1.0);
               var13.play();
            }
         }

         this.tickMarksUpdated();
         this.oldLength = var3;
         this.rangeValid = true;
      }

      if (var6 || var5 || this.measureInvalid || this.tickLabelsVisibleInvalid) {
         this.measureInvalid = false;
         this.tickLabelsVisibleInvalid = false;
         this.labelsToSkip.clear();
         int var16 = 0;
         double var17 = 0.0;
         double var19 = 0.0;

         for (Axis.TickMark var26 : this.tickMarks) {
            var26.setPosition(this.getDisplayPosition((T)var26.getValue()));
            if (var26.isTextVisible()) {
               double var14 = this.measureTickMarkSize((T)var26.getValue(), var2);
               var17 += var14;
               var19 = (double)Math.round(Math.max(var19, var14));
            }
         }

         if (var19 > 0.0 && var3 < var17) {
            var16 = (int)((double)this.tickMarks.size() * var19 / var3) + 1;
         }

         if (var16 > 0) {
            int var23 = 0;

            for (Axis.TickMark var30 : this.tickMarks) {
               if (var30.isTextVisible()) {
                  var30.setTextVisible(var23++ % var16 == 0);
               }
            }
         }

         if (this.tickMarks.size() > 2) {
            Axis.TickMark var24 = (Axis.TickMark)this.tickMarks.get(0);
            Axis.TickMark var28 = (Axis.TickMark)this.tickMarks.get(1);
            if (this.isTickLabelsOverlap(var2, var24, var28, this.getTickLabelGap())) {
               var28.setTextVisible(false);
            }

            var24 = (Axis.TickMark)this.tickMarks.get(this.tickMarks.size() - 2);
            var28 = (Axis.TickMark)this.tickMarks.get(this.tickMarks.size() - 1);
            if (this.isTickLabelsOverlap(var2, var24, var28, this.getTickLabelGap())) {
               var24.setTextVisible(false);
            }
         }

         this.updateTickMarks(var2, var3);
      }
   }

   private void updateTickMarks(Side var1, double var2) {
      this.tickMarkPath.getElements().clear();
      double var4 = this.getWidth();
      double var6 = this.getHeight();
      double var8 = this.isTickMarkVisible() && this.getTickLength() > 0.0 ? this.getTickLength() : 0.0;
      double var10 = this.getEffectiveTickLabelRotation();
      if (Side.LEFT.equals(var1)) {
         this.tickMarkPath.setLayoutX(-0.5);
         this.tickMarkPath.setLayoutY(0.5);
         if (this.getLabel() != null) {
            this.axisLabel.getTransforms().setAll(new Transform[]{new Translate(0.0, var6), new Rotate(-90.0, 0.0, 0.0)});
            this.axisLabel.setLayoutX(0.0);
            this.axisLabel.setLayoutY(0.0);
            this.axisLabel.resize(var6, Math.ceil(this.axisLabel.prefHeight(var4)));
         }

         for (Axis.TickMark var13 : this.tickMarks) {
            this.positionTextNode(var13.textNode, var4 - this.getTickLabelGap() - var8, var13.getPosition(), var10, var1);
            this.updateTickMark(var13, var2, var4 - var8, var13.getPosition(), var4, var13.getPosition());
         }
      } else if (Side.RIGHT.equals(var1)) {
         this.tickMarkPath.setLayoutX(0.5);
         this.tickMarkPath.setLayoutY(0.5);
         if (this.getLabel() != null) {
            double var14 = Math.ceil(this.axisLabel.prefHeight(var4));
            this.axisLabel.getTransforms().setAll(new Transform[]{new Translate(0.0, var6), new Rotate(-90.0, 0.0, 0.0)});
            this.axisLabel.setLayoutX(var4 - var14);
            this.axisLabel.setLayoutY(0.0);
            this.axisLabel.resize(var6, var14);
         }

         for (Axis.TickMark var19 : this.tickMarks) {
            this.positionTextNode(var19.textNode, this.getTickLabelGap() + var8, var19.getPosition(), var10, var1);
            this.updateTickMark(var19, var2, 0.0, var19.getPosition(), var8, var19.getPosition());
         }
      } else if (Side.TOP.equals(var1)) {
         this.tickMarkPath.setLayoutX(0.5);
         this.tickMarkPath.setLayoutY(-0.5);
         if (this.getLabel() != null) {
            this.axisLabel.getTransforms().clear();
            this.axisLabel.setLayoutX(0.0);
            this.axisLabel.setLayoutY(0.0);
            this.axisLabel.resize(var4, Math.ceil(this.axisLabel.prefHeight(var4)));
         }

         for (Axis.TickMark var20 : this.tickMarks) {
            this.positionTextNode(var20.textNode, var20.getPosition(), var6 - var8 - this.getTickLabelGap(), var10, var1);
            this.updateTickMark(var20, var2, var20.getPosition(), var6, var20.getPosition(), var6 - var8);
         }
      } else {
         this.tickMarkPath.setLayoutX(0.5);
         this.tickMarkPath.setLayoutY(0.5);
         if (this.getLabel() != null) {
            this.axisLabel.getTransforms().clear();
            double var17 = Math.ceil(this.axisLabel.prefHeight(var4));
            this.axisLabel.setLayoutX(0.0);
            this.axisLabel.setLayoutY(var6 - var17);
            this.axisLabel.resize(var4, var17);
         }

         for (Axis.TickMark var21 : this.tickMarks) {
            this.positionTextNode(var21.textNode, var21.getPosition(), var8 + this.getTickLabelGap(), var10, var1);
            this.updateTickMark(var21, var2, var21.getPosition(), 0.0, var21.getPosition(), var8);
         }
      }
   }

   private boolean isTickLabelsOverlap(Side var1, Axis.TickMark<T> var2, Axis.TickMark<T> var3, double var4) {
      if (var2.isTextVisible() && var3.isTextVisible()) {
         double var6 = this.measureTickMarkSize((T)var2.getValue(), var1);
         double var8 = this.measureTickMarkSize((T)var3.getValue(), var1);
         double var10 = var2.getPosition() - var6 / 2.0;
         double var12 = var2.getPosition() + var6 / 2.0;
         double var14 = var3.getPosition() - var8 / 2.0;
         double var16 = var3.getPosition() + var8 / 2.0;
         return var1.isVertical() ? var10 - var16 <= var4 : var14 - var12 <= var4;
      } else {
         return false;
      }
   }

   private void positionTextNode(Text var1, double var2, double var4, double var6, Side var8) {
      var1.setLayoutX(0.0);
      var1.setLayoutY(0.0);
      var1.setRotate(var6);
      Bounds var9 = var1.getBoundsInParent();
      if (Side.LEFT.equals(var8)) {
         var1.setLayoutX(var2 - var9.getWidth() - var9.getMinX());
         var1.setLayoutY(var4 - var9.getHeight() / 2.0 - var9.getMinY());
      } else if (Side.RIGHT.equals(var8)) {
         var1.setLayoutX(var2 - var9.getMinX());
         var1.setLayoutY(var4 - var9.getHeight() / 2.0 - var9.getMinY());
      } else if (Side.TOP.equals(var8)) {
         var1.setLayoutX(var2 - var9.getWidth() / 2.0 - var9.getMinX());
         var1.setLayoutY(var4 - var9.getHeight() - var9.getMinY());
      } else {
         var1.setLayoutX(var2 - var9.getWidth() / 2.0 - var9.getMinX());
         var1.setLayoutY(var4 - var9.getMinY());
      }
   }

   private void updateTickMark(Axis.TickMark<T> var1, double var2, double var4, double var6, double var8, double var10) {
      if (var1.getPosition() >= 0.0 && var1.getPosition() <= Math.ceil(var2)) {
         var1.textNode.setVisible(var1.isTextVisible());
         this.tickMarkPath.getElements().addAll(new PathElement[]{new MoveTo(var4, var6), new LineTo(var8, var10)});
      } else {
         var1.textNode.setVisible(false);
      }
   }

   protected abstract String getTickMarkLabel(T var1);

   protected final Dimension2D measureTickMarkLabelSize(String var1, double var2) {
      this.measure.setRotate(var2);
      this.measure.setText(var1);
      Bounds var4 = this.measure.getBoundsInParent();
      return new Dimension2D(var4.getWidth(), var4.getHeight());
   }

   protected final Dimension2D measureTickMarkSize(T var1, double var2) {
      return this.measureTickMarkLabelSize(this.getTickMarkLabel((T)var1), var2);
   }

   protected Dimension2D measureTickMarkSize(T var1, Object var2) {
      return this.measureTickMarkSize((T)var1, this.getEffectiveTickLabelRotation());
   }

   private double measureTickMarkSize(T var1, Side var2) {
      Dimension2D var3 = this.measureTickMarkSize((T)var1, this.getEffectiveTickLabelRotation());
      return var2.isVertical() ? var3.getHeight() : var3.getWidth();
   }

   final double getEffectiveTickLabelRotation() {
      return this.isAutoRanging() && !Double.isNaN(this.effectiveTickLabelRotation) ? this.effectiveTickLabelRotation : this.getTickLabelRotation();
   }

   final void setEffectiveTickLabelRotation(double var1) {
      this.effectiveTickLabelRotation = var1;
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return Axis.StyleableProperties.STYLEABLES;
   }

   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   private static class StyleableProperties {
      private static final CssMetaData<Axis<?>, Side> SIDE = new CssMetaData<Axis<?>, Side>("-fx-side", new EnumConverter(Side.class)) {
         public boolean isSettable(Axis<?> var1) {
            return var1.side == null || !var1.side.isBound();
         }

         public StyleableProperty<Side> getStyleableProperty(Axis<?> var1) {
            return (StyleableProperty<Side>)var1.sideProperty();
         }
      };
      private static final CssMetaData<Axis<?>, Number> TICK_LENGTH = new CssMetaData<Axis<?>, Number>("-fx-tick-length", SizeConverter.getInstance(), 8.0) {
         public boolean isSettable(Axis<?> var1) {
            return var1.tickLength == null || !var1.tickLength.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(Axis<?> var1) {
            return (StyleableProperty<Number>)var1.tickLengthProperty();
         }
      };
      private static final CssMetaData<Axis<?>, Font> TICK_LABEL_FONT = new FontCssMetaData<Axis<?>>("-fx-tick-label-font", Font.font("system", 8.0)) {
         public boolean isSettable(Axis<?> var1) {
            return var1.tickLabelFont == null || !var1.tickLabelFont.isBound();
         }

         public StyleableProperty<Font> getStyleableProperty(Axis<?> var1) {
            return (StyleableProperty<Font>)var1.tickLabelFontProperty();
         }
      };
      private static final CssMetaData<Axis<?>, Paint> TICK_LABEL_FILL = new CssMetaData<Axis<?>, Paint>(
         "-fx-tick-label-fill", PaintConverter.getInstance(), Color.BLACK
      ) {
         public boolean isSettable(Axis<?> var1) {
            return var1.tickLabelFill == null | !var1.tickLabelFill.isBound();
         }

         public StyleableProperty<Paint> getStyleableProperty(Axis<?> var1) {
            return (StyleableProperty<Paint>)var1.tickLabelFillProperty();
         }
      };
      private static final CssMetaData<Axis<?>, Number> TICK_LABEL_TICK_GAP = new CssMetaData<Axis<?>, Number>(
         "-fx-tick-label-gap", SizeConverter.getInstance(), 3.0
      ) {
         public boolean isSettable(Axis<?> var1) {
            return var1.tickLabelGap == null || !var1.tickLabelGap.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(Axis<?> var1) {
            return (StyleableProperty<Number>)var1.tickLabelGapProperty();
         }
      };
      private static final CssMetaData<Axis<?>, Boolean> TICK_MARK_VISIBLE = new CssMetaData<Axis<?>, Boolean>(
         "-fx-tick-mark-visible", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(Axis<?> var1) {
            return var1.tickMarkVisible == null || !var1.tickMarkVisible.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(Axis<?> var1) {
            return (StyleableProperty<Boolean>)var1.tickMarkVisibleProperty();
         }
      };
      private static final CssMetaData<Axis<?>, Boolean> TICK_LABELS_VISIBLE = new CssMetaData<Axis<?>, Boolean>(
         "-fx-tick-labels-visible", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(Axis<?> var1) {
            return var1.tickLabelsVisible == null || !var1.tickLabelsVisible.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(Axis<?> var1) {
            return (StyleableProperty<Boolean>)var1.tickLabelsVisibleProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList(Region.getClassCssMetaData());
         var0.add(SIDE);
         var0.add(TICK_LENGTH);
         var0.add(TICK_LABEL_FONT);
         var0.add(TICK_LABEL_FILL);
         var0.add(TICK_LABEL_TICK_GAP);
         var0.add(TICK_MARK_VISIBLE);
         var0.add(TICK_LABELS_VISIBLE);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }

   public static final class TickMark<T> {
      private StringProperty label = new StringPropertyBase() {
         protected void invalidated() {
            TickMark.this.textNode.setText(this.getValue());
         }

         public Object getBean() {
            return TickMark.this;
         }

         public String getName() {
            return "label";
         }
      };
      private ObjectProperty<T> value = new SimpleObjectProperty(this, "value");
      private DoubleProperty position = new SimpleDoubleProperty(this, "position");
      Text textNode = new Text();
      private BooleanProperty textVisible = new BooleanPropertyBase(true) {
         protected void invalidated() {
            if (!this.get()) {
               TickMark.this.textNode.setVisible(false);
            }
         }

         public Object getBean() {
            return TickMark.this;
         }

         public String getName() {
            return "textVisible";
         }
      };

      public final String getLabel() {
         return (String)this.label.get();
      }

      public final void setLabel(String var1) {
         this.label.set(var1);
      }

      public final StringExpression labelProperty() {
         return this.label;
      }

      public final T getValue() {
         return (T)this.value.get();
      }

      public final void setValue(T var1) {
         this.value.set(var1);
      }

      public final ObjectExpression<T> valueProperty() {
         return this.value;
      }

      public final double getPosition() {
         return this.position.get();
      }

      public final void setPosition(double var1) {
         this.position.set(var1);
      }

      public final DoubleExpression positionProperty() {
         return this.position;
      }

      public final boolean isTextVisible() {
         return this.textVisible.get();
      }

      public final void setTextVisible(boolean var1) {
         this.textVisible.set(var1);
      }

      @Override
      public String toString() {
         return this.value.get().toString();
      }
   }
}
