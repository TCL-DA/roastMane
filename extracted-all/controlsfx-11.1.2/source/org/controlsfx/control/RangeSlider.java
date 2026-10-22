package org.controlsfx.control;

import impl.org.controlsfx.skin.RangeSliderSkin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.StyleConverter;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.util.StringConverter;
import org.controlsfx.tools.Utils;

public class RangeSlider extends ControlsFXControl {
   private DoubleProperty lowValue = new SimpleDoubleProperty(this, "lowValue", 0.0) {
      protected void invalidated() {
         RangeSlider.this.adjustLowValues();
      }
   };
   private BooleanProperty lowValueChanging;
   private DoubleProperty highValue = new SimpleDoubleProperty(this, "highValue", 100.0) {
      protected void invalidated() {
         RangeSlider.this.adjustHighValues();
      }

      public Object getBean() {
         return RangeSlider.this;
      }

      public String getName() {
         return "highValue";
      }
   };
   private BooleanProperty highValueChanging;
   private final ObjectProperty<StringConverter<Number>> tickLabelFormatter = new SimpleObjectProperty();
   private DoubleProperty max;
   private DoubleProperty min;
   private BooleanProperty snapToTicks;
   private DoubleProperty majorTickUnit;
   private IntegerProperty minorTickCount;
   private DoubleProperty blockIncrement;
   private ObjectProperty<Orientation> orientation;
   private BooleanProperty showTickLabels;
   private BooleanProperty showTickMarks;
   private static final String DEFAULT_STYLE_CLASS = "range-slider";
   private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");
   private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");

   public RangeSlider() {
      this(0.0, 1.0, 0.25, 0.75);
   }

   public RangeSlider(double min, double max, double lowValue, double highValue) {
      this.getStyleClass().setAll(new String[]{"range-slider"});
      this.setMax(max);
      this.setMin(min);
      this.adjustValues();
      this.setLowValue(lowValue);
      this.setHighValue(highValue);
   }

   public String getUserAgentStylesheet() {
      return this.getUserAgentStylesheet(RangeSlider.class, "rangeslider.css");
   }

   protected Skin<?> createDefaultSkin() {
      return new RangeSliderSkin(this);
   }

   public final DoubleProperty lowValueProperty() {
      return this.lowValue;
   }

   public final void setLowValue(double d) {
      this.lowValueProperty().set(d);
   }

   public final double getLowValue() {
      return this.lowValue != null ? this.lowValue.get() : 0.0;
   }

   public final BooleanProperty lowValueChangingProperty() {
      if (this.lowValueChanging == null) {
         this.lowValueChanging = new SimpleBooleanProperty(this, "lowValueChanging", false);
      }

      return this.lowValueChanging;
   }

   public final void setLowValueChanging(boolean value) {
      this.lowValueChangingProperty().set(value);
   }

   public final boolean isLowValueChanging() {
      return this.lowValueChanging == null ? false : this.lowValueChanging.get();
   }

   public final DoubleProperty highValueProperty() {
      return this.highValue;
   }

   public final void setHighValue(double d) {
      if (!this.highValueProperty().isBound()) {
         this.highValueProperty().set(d);
      }
   }

   public final double getHighValue() {
      return this.highValue != null ? this.highValue.get() : 100.0;
   }

   public final BooleanProperty highValueChangingProperty() {
      if (this.highValueChanging == null) {
         this.highValueChanging = new SimpleBooleanProperty(this, "highValueChanging", false);
      }

      return this.highValueChanging;
   }

   public final void setHighValueChanging(boolean value) {
      this.highValueChangingProperty().set(value);
   }

   public final boolean isHighValueChanging() {
      return this.highValueChanging == null ? false : this.highValueChanging.get();
   }

   public final StringConverter<Number> getLabelFormatter() {
      return (StringConverter<Number>)this.tickLabelFormatter.get();
   }

   public final void setLabelFormatter(StringConverter<Number> value) {
      this.tickLabelFormatter.set(value);
   }

   public final ObjectProperty<StringConverter<Number>> labelFormatterProperty() {
      return this.tickLabelFormatter;
   }

   public void incrementLowValue() {
      this.adjustLowValue(this.getLowValue() + this.getBlockIncrement());
   }

   public void decrementLowValue() {
      this.adjustLowValue(this.getLowValue() - this.getBlockIncrement());
   }

   public void incrementHighValue() {
      this.adjustHighValue(this.getHighValue() + this.getBlockIncrement());
   }

   public void decrementHighValue() {
      this.adjustHighValue(this.getHighValue() - this.getBlockIncrement());
   }

   public void adjustLowValue(double newValue) {
      double d1 = this.getMin();
      double d2 = this.getMax();
      if (!(d2 <= d1)) {
         newValue = newValue >= d1 ? newValue : d1;
         newValue = newValue <= d2 ? newValue : d2;
         this.setLowValue(this.snapValueToTicks(newValue));
      }
   }

   public void adjustHighValue(double newValue) {
      double d1 = this.getMin();
      double d2 = this.getMax();
      if (!(d2 <= d1)) {
         newValue = newValue >= d1 ? newValue : d1;
         newValue = newValue <= d2 ? newValue : d2;
         this.setHighValue(this.snapValueToTicks(newValue));
      }
   }

   public final void setMax(double value) {
      this.maxProperty().set(value);
   }

   public final double getMax() {
      return this.max == null ? 100.0 : this.max.get();
   }

   public final DoubleProperty maxProperty() {
      if (this.max == null) {
         this.max = new DoublePropertyBase(100.0) {
            protected void invalidated() {
               if (this.get() < RangeSlider.this.getMin()) {
                  RangeSlider.this.setMin(this.get());
               }

               RangeSlider.this.adjustValues();
            }

            public Object getBean() {
               return RangeSlider.this;
            }

            public String getName() {
               return "max";
            }
         };
      }

      return this.max;
   }

   public final void setMin(double value) {
      this.minProperty().set(value);
   }

   public final double getMin() {
      return this.min == null ? 0.0 : this.min.get();
   }

   public final DoubleProperty minProperty() {
      if (this.min == null) {
         this.min = new DoublePropertyBase(0.0) {
            protected void invalidated() {
               if (this.get() > RangeSlider.this.getMax()) {
                  RangeSlider.this.setMax(this.get());
               }

               RangeSlider.this.adjustValues();
            }

            public Object getBean() {
               return RangeSlider.this;
            }

            public String getName() {
               return "min";
            }
         };
      }

      return this.min;
   }

   public final void setSnapToTicks(boolean value) {
      this.snapToTicksProperty().set(value);
   }

   public final boolean isSnapToTicks() {
      return this.snapToTicks == null ? false : this.snapToTicks.get();
   }

   public final BooleanProperty snapToTicksProperty() {
      if (this.snapToTicks == null) {
         this.snapToTicks = new StyleableBooleanProperty(false) {
            public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
               return RangeSlider.StyleableProperties.SNAP_TO_TICKS;
            }

            public Object getBean() {
               return RangeSlider.this;
            }

            public String getName() {
               return "snapToTicks";
            }
         };
      }

      return this.snapToTicks;
   }

   public final void setMajorTickUnit(double value) {
      if (value <= 0.0) {
         throw new IllegalArgumentException("MajorTickUnit cannot be less than or equal to 0.");
      } else {
         this.majorTickUnitProperty().set(value);
      }
   }

   public final double getMajorTickUnit() {
      return this.majorTickUnit == null ? 25.0 : this.majorTickUnit.get();
   }

   public final DoubleProperty majorTickUnitProperty() {
      if (this.majorTickUnit == null) {
         this.majorTickUnit = new StyleableDoubleProperty(25.0) {
            public void invalidated() {
               if (this.get() <= 0.0) {
                  throw new IllegalArgumentException("MajorTickUnit cannot be less than or equal to 0.");
               }
            }

            public CssMetaData<? extends Styleable, Number> getCssMetaData() {
               return RangeSlider.StyleableProperties.MAJOR_TICK_UNIT;
            }

            public Object getBean() {
               return RangeSlider.this;
            }

            public String getName() {
               return "majorTickUnit";
            }
         };
      }

      return this.majorTickUnit;
   }

   public final void setMinorTickCount(int value) {
      this.minorTickCountProperty().set(value);
   }

   public final int getMinorTickCount() {
      return this.minorTickCount == null ? 3 : this.minorTickCount.get();
   }

   public final IntegerProperty minorTickCountProperty() {
      if (this.minorTickCount == null) {
         this.minorTickCount = new StyleableIntegerProperty(3) {
            public CssMetaData<? extends Styleable, Number> getCssMetaData() {
               return RangeSlider.StyleableProperties.MINOR_TICK_COUNT;
            }

            public Object getBean() {
               return RangeSlider.this;
            }

            public String getName() {
               return "minorTickCount";
            }
         };
      }

      return this.minorTickCount;
   }

   public final void setBlockIncrement(double value) {
      this.blockIncrementProperty().set(value);
   }

   public final double getBlockIncrement() {
      return this.blockIncrement == null ? 10.0 : this.blockIncrement.get();
   }

   public final DoubleProperty blockIncrementProperty() {
      if (this.blockIncrement == null) {
         this.blockIncrement = new StyleableDoubleProperty(10.0) {
            public CssMetaData<? extends Styleable, Number> getCssMetaData() {
               return RangeSlider.StyleableProperties.BLOCK_INCREMENT;
            }

            public Object getBean() {
               return RangeSlider.this;
            }

            public String getName() {
               return "blockIncrement";
            }
         };
      }

      return this.blockIncrement;
   }

   public final void setOrientation(Orientation value) {
      this.orientationProperty().set(value);
   }

   public final Orientation getOrientation() {
      return this.orientation == null ? Orientation.HORIZONTAL : (Orientation)this.orientation.get();
   }

   public final ObjectProperty<Orientation> orientationProperty() {
      if (this.orientation == null) {
         this.orientation = new StyleableObjectProperty<Orientation>(Orientation.HORIZONTAL) {
            protected void invalidated() {
               boolean vertical = this.get() == Orientation.VERTICAL;
               RangeSlider.this.pseudoClassStateChanged(RangeSlider.VERTICAL_PSEUDOCLASS_STATE, vertical);
               RangeSlider.this.pseudoClassStateChanged(RangeSlider.HORIZONTAL_PSEUDOCLASS_STATE, !vertical);
            }

            public CssMetaData<? extends Styleable, Orientation> getCssMetaData() {
               return RangeSlider.StyleableProperties.ORIENTATION;
            }

            public Object getBean() {
               return RangeSlider.this;
            }

            public String getName() {
               return "orientation";
            }
         };
      }

      return this.orientation;
   }

   public final void setShowTickLabels(boolean value) {
      this.showTickLabelsProperty().set(value);
   }

   public final boolean isShowTickLabels() {
      return this.showTickLabels == null ? false : this.showTickLabels.get();
   }

   public final BooleanProperty showTickLabelsProperty() {
      if (this.showTickLabels == null) {
         this.showTickLabels = new StyleableBooleanProperty(false) {
            public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
               return RangeSlider.StyleableProperties.SHOW_TICK_LABELS;
            }

            public Object getBean() {
               return RangeSlider.this;
            }

            public String getName() {
               return "showTickLabels";
            }
         };
      }

      return this.showTickLabels;
   }

   public final void setShowTickMarks(boolean value) {
      this.showTickMarksProperty().set(value);
   }

   public final boolean isShowTickMarks() {
      return this.showTickMarks == null ? false : this.showTickMarks.get();
   }

   public final BooleanProperty showTickMarksProperty() {
      if (this.showTickMarks == null) {
         this.showTickMarks = new StyleableBooleanProperty(false) {
            public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
               return RangeSlider.StyleableProperties.SHOW_TICK_MARKS;
            }

            public Object getBean() {
               return RangeSlider.this;
            }

            public String getName() {
               return "showTickMarks";
            }
         };
      }

      return this.showTickMarks;
   }

   private void adjustValues() {
      this.adjustLowValues();
      this.adjustHighValues();
   }

   private void adjustLowValues() {
      if (this.getLowValue() < this.getMin() || this.getLowValue() > this.getMax()) {
         double value = Utils.clamp(this.getMin(), this.getLowValue(), this.getMax());
         this.setLowValue(value);
      } else if (this.getLowValue() >= this.getHighValue() && this.getHighValue() >= this.getMin() && this.getHighValue() <= this.getMax()) {
         double value = Utils.clamp(this.getMin(), this.getLowValue(), this.getHighValue());
         this.setLowValue(value);
      }
   }

   private double snapValueToTicks(double d) {
      double d1 = d;
      if (this.isSnapToTicks()) {
         double d2 = 0.0;
         if (this.getMinorTickCount() != 0) {
            d2 = this.getMajorTickUnit() / (double)(Math.max(this.getMinorTickCount(), 0) + 1);
         } else {
            d2 = this.getMajorTickUnit();
         }

         int i = (int)((d - this.getMin()) / d2);
         double d3 = (double)i * d2 + this.getMin();
         double d4 = (double)(i + 1) * d2 + this.getMin();
         d1 = Utils.nearest(d3, d, d4);
      }

      return Utils.clamp(this.getMin(), d1, this.getMax());
   }

   private void adjustHighValues() {
      if (this.getHighValue() < this.getMin() || this.getHighValue() > this.getMax()) {
         this.setHighValue(Utils.clamp(this.getMin(), this.getHighValue(), this.getMax()));
      } else if (this.getHighValue() < this.getLowValue() && this.getLowValue() >= this.getMin() && this.getLowValue() <= this.getMax()) {
         this.setHighValue(Utils.clamp(this.getLowValue(), this.getHighValue(), this.getMax()));
      }
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return RangeSlider.StyleableProperties.STYLEABLES;
   }

   public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
      return getClassCssMetaData();
   }

   private static class StyleableProperties {
      private static final CssMetaData<RangeSlider, Number> BLOCK_INCREMENT = new CssMetaData<RangeSlider, Number>(
         "-fx-block-increment", SizeConverter.getInstance(), 10.0
      ) {
         public boolean isSettable(RangeSlider n) {
            return n.blockIncrement == null || !n.blockIncrement.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(RangeSlider n) {
            return (StyleableProperty<Number>)n.blockIncrementProperty();
         }
      };
      private static final CssMetaData<RangeSlider, Boolean> SHOW_TICK_LABELS = new CssMetaData<RangeSlider, Boolean>(
         "-fx-show-tick-labels", BooleanConverter.getInstance(), Boolean.FALSE
      ) {
         public boolean isSettable(RangeSlider n) {
            return n.showTickLabels == null || !n.showTickLabels.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(RangeSlider n) {
            return (StyleableProperty<Boolean>)n.showTickLabelsProperty();
         }
      };
      private static final CssMetaData<RangeSlider, Boolean> SHOW_TICK_MARKS = new CssMetaData<RangeSlider, Boolean>(
         "-fx-show-tick-marks", BooleanConverter.getInstance(), Boolean.FALSE
      ) {
         public boolean isSettable(RangeSlider n) {
            return n.showTickMarks == null || !n.showTickMarks.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(RangeSlider n) {
            return (StyleableProperty<Boolean>)n.showTickMarksProperty();
         }
      };
      private static final CssMetaData<RangeSlider, Boolean> SNAP_TO_TICKS = new CssMetaData<RangeSlider, Boolean>(
         "-fx-snap-to-ticks", BooleanConverter.getInstance(), Boolean.FALSE
      ) {
         public boolean isSettable(RangeSlider n) {
            return n.snapToTicks == null || !n.snapToTicks.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(RangeSlider n) {
            return (StyleableProperty<Boolean>)n.snapToTicksProperty();
         }
      };
      private static final CssMetaData<RangeSlider, Number> MAJOR_TICK_UNIT = new CssMetaData<RangeSlider, Number>(
         "-fx-major-tick-unit", SizeConverter.getInstance(), 25.0
      ) {
         public boolean isSettable(RangeSlider n) {
            return n.majorTickUnit == null || !n.majorTickUnit.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(RangeSlider n) {
            return (StyleableProperty<Number>)n.majorTickUnitProperty();
         }
      };
      private static final CssMetaData<RangeSlider, Number> MINOR_TICK_COUNT = new CssMetaData<RangeSlider, Number>(
         "-fx-minor-tick-count", SizeConverter.getInstance(), 3.0
      ) {
         public void set(RangeSlider node, Number value, StyleOrigin origin) {
            super.set(node, value.intValue(), origin);
         }

         public boolean isSettable(RangeSlider n) {
            return n.minorTickCount == null || !n.minorTickCount.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(RangeSlider n) {
            return (StyleableProperty<Number>)n.minorTickCountProperty();
         }
      };
      private static final CssMetaData<RangeSlider, Orientation> ORIENTATION = new CssMetaData<RangeSlider, Orientation>(
         "-fx-orientation", new EnumConverter(Orientation.class), Orientation.HORIZONTAL
      ) {
         public Orientation getInitialValue(RangeSlider node) {
            return node.getOrientation();
         }

         public boolean isSettable(RangeSlider n) {
            return n.orientation == null || !n.orientation.isBound();
         }

         public StyleableProperty<Orientation> getStyleableProperty(RangeSlider n) {
            return (StyleableProperty<Orientation>)n.orientationProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Control.getClassCssMetaData());
         styleables.add(BLOCK_INCREMENT);
         styleables.add(SHOW_TICK_LABELS);
         styleables.add(SHOW_TICK_MARKS);
         styleables.add(SNAP_TO_TICKS);
         styleables.add(MAJOR_TICK_UNIT);
         styleables.add(MINOR_TICK_COUNT);
         styleables.add(ORIENTATION);
         STYLEABLES = Collections.unmodifiableList(styleables);
      }
   }
}
