package javafx.scene.control;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

public abstract class SpinnerValueFactory<T> {
   private ObjectProperty<T> value = new SimpleObjectProperty(this, "value");
   private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty(this, "converter");
   private BooleanProperty wrapAround;

   public abstract void decrement(int var1);

   public abstract void increment(int var1);

   public final T getValue() {
      return (T)this.value.get();
   }

   public final void setValue(T var1) {
      this.value.set(var1);
   }

   public final ObjectProperty<T> valueProperty() {
      return this.value;
   }

   public final StringConverter<T> getConverter() {
      return (StringConverter<T>)this.converter.get();
   }

   public final void setConverter(StringConverter<T> var1) {
      this.converter.set(var1);
   }

   public final ObjectProperty<StringConverter<T>> converterProperty() {
      return this.converter;
   }

   public final void setWrapAround(boolean var1) {
      this.wrapAroundProperty().set(var1);
   }

   public final boolean isWrapAround() {
      return this.wrapAround == null ? false : this.wrapAround.get();
   }

   public final BooleanProperty wrapAroundProperty() {
      if (this.wrapAround == null) {
         this.wrapAround = new SimpleBooleanProperty(this, "wrapAround", false);
      }

      return this.wrapAround;
   }

   public static class DoubleSpinnerValueFactory extends SpinnerValueFactory<Double> {
      private DoubleProperty min = new SimpleDoubleProperty(this, "min") {
         protected void invalidated() {
            Double var1 = (Double)DoubleSpinnerValueFactory.this.getValue();
            if (var1 != null) {
               double var2 = this.get();
               if (var2 > DoubleSpinnerValueFactory.this.getMax()) {
                  DoubleSpinnerValueFactory.this.setMin(DoubleSpinnerValueFactory.this.getMax());
               } else {
                  if (var1 < var2) {
                     DoubleSpinnerValueFactory.this.setValue(Double.valueOf(var2));
                  }
               }
            }
         }
      };
      private DoubleProperty max = new SimpleDoubleProperty(this, "max") {
         protected void invalidated() {
            Double var1 = (Double)DoubleSpinnerValueFactory.this.getValue();
            if (var1 != null) {
               double var2 = this.get();
               if (var2 < DoubleSpinnerValueFactory.this.getMin()) {
                  DoubleSpinnerValueFactory.this.setMax(DoubleSpinnerValueFactory.this.getMin());
               } else {
                  if (var1 > var2) {
                     DoubleSpinnerValueFactory.this.setValue(Double.valueOf(var2));
                  }
               }
            }
         }
      };
      private DoubleProperty amountToStepBy = new SimpleDoubleProperty(this, "amountToStepBy");

      public DoubleSpinnerValueFactory(@NamedArg("min") double var1, @NamedArg("max") double var3) {
         this(var1, var3, var1);
      }

      public DoubleSpinnerValueFactory(@NamedArg("min") double var1, @NamedArg("max") double var3, @NamedArg("initialValue") double var5) {
         this(var1, var3, var5, 1.0);
      }

      public DoubleSpinnerValueFactory(
         @NamedArg("min") double var1, @NamedArg("max") double var3, @NamedArg("initialValue") double var5, @NamedArg("amountToStepBy") double var7
      ) {
         this.setMin(var1);
         this.setMax(var3);
         this.setAmountToStepBy(var7);
         this.setConverter(new StringConverter<Double>() {
            private final DecimalFormat df = new DecimalFormat("#.##");

            public String toString(Double var1) {
               return var1 == null ? "" : this.df.format(var1);
            }

            public Double fromString(String var1) {
               try {
                  if (var1 == null) {
                     return null;
                  } else {
                     var1 = var1.trim();
                     return var1.length() < 1 ? null : this.df.parse(var1).doubleValue();
                  }
               } catch (ParseException var3) {
                  throw new RuntimeException(var3);
               }
            }
         });
         this.valueProperty().addListener((var1x, var2, var3x) -> {
            if (var3x != null) {
               if (var3x < this.getMin()) {
                  this.setValue(Double.valueOf(this.getMin()));
               } else if (var3x > this.getMax()) {
                  this.setValue(Double.valueOf(this.getMax()));
               }
            }
         });
         this.setValue(Double.valueOf(var5 >= var1 && var5 <= var3 ? var5 : var1));
      }

      public final void setMin(double var1) {
         this.min.set(var1);
      }

      public final double getMin() {
         return this.min.get();
      }

      public final DoubleProperty minProperty() {
         return this.min;
      }

      public final void setMax(double var1) {
         this.max.set(var1);
      }

      public final double getMax() {
         return this.max.get();
      }

      public final DoubleProperty maxProperty() {
         return this.max;
      }

      public final void setAmountToStepBy(double var1) {
         this.amountToStepBy.set(var1);
      }

      public final double getAmountToStepBy() {
         return this.amountToStepBy.get();
      }

      public final DoubleProperty amountToStepByProperty() {
         return this.amountToStepBy;
      }

      @Override
      public void decrement(int var1) {
         BigDecimal var2 = BigDecimal.valueOf((Double)this.getValue());
         BigDecimal var3 = BigDecimal.valueOf(this.getMin());
         BigDecimal var4 = BigDecimal.valueOf(this.getMax());
         BigDecimal var5 = BigDecimal.valueOf(this.getAmountToStepBy());
         BigDecimal var6 = var2.subtract(var5.multiply(BigDecimal.valueOf((long)var1)));
         this.setValue(
            Double.valueOf(
               var6.compareTo(var3) >= 0 ? var6.doubleValue() : (this.isWrapAround() ? Spinner.wrapValue(var6, var3, var4).doubleValue() : this.getMin())
            )
         );
      }

      @Override
      public void increment(int var1) {
         BigDecimal var2 = BigDecimal.valueOf((Double)this.getValue());
         BigDecimal var3 = BigDecimal.valueOf(this.getMin());
         BigDecimal var4 = BigDecimal.valueOf(this.getMax());
         BigDecimal var5 = BigDecimal.valueOf(this.getAmountToStepBy());
         BigDecimal var6 = var2.add(var5.multiply(BigDecimal.valueOf((long)var1)));
         this.setValue(
            Double.valueOf(
               var6.compareTo(var4) <= 0 ? var6.doubleValue() : (this.isWrapAround() ? Spinner.wrapValue(var6, var3, var4).doubleValue() : this.getMax())
            )
         );
      }
   }

   public static class IntegerSpinnerValueFactory extends SpinnerValueFactory<Integer> {
      private IntegerProperty min = new SimpleIntegerProperty(this, "min") {
         protected void invalidated() {
            Integer var1 = (Integer)IntegerSpinnerValueFactory.this.getValue();
            if (var1 != null) {
               int var2 = this.get();
               if (var2 > IntegerSpinnerValueFactory.this.getMax()) {
                  IntegerSpinnerValueFactory.this.setMin(IntegerSpinnerValueFactory.this.getMax());
               } else {
                  if (var1 < var2) {
                     IntegerSpinnerValueFactory.this.setValue(Integer.valueOf(var2));
                  }
               }
            }
         }
      };
      private IntegerProperty max = new SimpleIntegerProperty(this, "max") {
         protected void invalidated() {
            Integer var1 = (Integer)IntegerSpinnerValueFactory.this.getValue();
            if (var1 != null) {
               int var2 = this.get();
               if (var2 < IntegerSpinnerValueFactory.this.getMin()) {
                  IntegerSpinnerValueFactory.this.setMax(IntegerSpinnerValueFactory.this.getMin());
               } else {
                  if (var1 > var2) {
                     IntegerSpinnerValueFactory.this.setValue(Integer.valueOf(var2));
                  }
               }
            }
         }
      };
      private IntegerProperty amountToStepBy = new SimpleIntegerProperty(this, "amountToStepBy");

      public IntegerSpinnerValueFactory(@NamedArg("min") int var1, @NamedArg("max") int var2) {
         this(var1, var2, var1);
      }

      public IntegerSpinnerValueFactory(@NamedArg("min") int var1, @NamedArg("max") int var2, @NamedArg("initialValue") int var3) {
         this(var1, var2, var3, 1);
      }

      public IntegerSpinnerValueFactory(
         @NamedArg("min") int var1, @NamedArg("max") int var2, @NamedArg("initialValue") int var3, @NamedArg("amountToStepBy") int var4
      ) {
         this.setMin(var1);
         this.setMax(var2);
         this.setAmountToStepBy(var4);
         this.setConverter(new IntegerStringConverter());
         this.valueProperty().addListener((var1x, var2x, var3x) -> {
            if (var3x < this.getMin()) {
               this.setValue(Integer.valueOf(this.getMin()));
            } else if (var3x > this.getMax()) {
               this.setValue(Integer.valueOf(this.getMax()));
            }
         });
         this.setValue(Integer.valueOf(var3 >= var1 && var3 <= var2 ? var3 : var1));
      }

      public final void setMin(int var1) {
         this.min.set(var1);
      }

      public final int getMin() {
         return this.min.get();
      }

      public final IntegerProperty minProperty() {
         return this.min;
      }

      public final void setMax(int var1) {
         this.max.set(var1);
      }

      public final int getMax() {
         return this.max.get();
      }

      public final IntegerProperty maxProperty() {
         return this.max;
      }

      public final void setAmountToStepBy(int var1) {
         this.amountToStepBy.set(var1);
      }

      public final int getAmountToStepBy() {
         return this.amountToStepBy.get();
      }

      public final IntegerProperty amountToStepByProperty() {
         return this.amountToStepBy;
      }

      @Override
      public void decrement(int var1) {
         int var2 = this.getMin();
         int var3 = this.getMax();
         int var4 = (Integer)this.getValue() - var1 * this.getAmountToStepBy();
         this.setValue(Integer.valueOf(var4 >= var2 ? var4 : (this.isWrapAround() ? Spinner.wrapValue(var4, var2, var3) + 1 : var2)));
      }

      @Override
      public void increment(int var1) {
         int var2 = this.getMin();
         int var3 = this.getMax();
         int var4 = (Integer)this.getValue();
         int var5 = var4 + var1 * this.getAmountToStepBy();
         this.setValue(Integer.valueOf(var5 <= var3 ? var5 : (this.isWrapAround() ? Spinner.wrapValue(var5, var2, var3) - 1 : var3)));
      }
   }

   public static class ListSpinnerValueFactory<T> extends SpinnerValueFactory<T> {
      private int currentIndex = 0;
      private final ListChangeListener<T> itemsContentObserver = var1x -> this.updateCurrentIndex();
      private WeakListChangeListener<T> weakItemsContentObserver = new WeakListChangeListener(this.itemsContentObserver);
      private ObjectProperty<ObservableList<T>> items;

      public ListSpinnerValueFactory(@NamedArg("items") ObservableList<T> var1) {
         this.setItems(var1);
         this.setConverter(new StringConverter<T>() {
            public String toString(T var1) {
               return var1 == null ? "" : var1.toString();
            }

            public T fromString(String var1) {
               return (T)var1;
            }
         });
         this.valueProperty().addListener((var2, var3, var4) -> {
            int var5 = -1;
            if (var1.contains(var4)) {
               var5 = var1.indexOf(var4);
            } else {
               var1.add(var4);
               var5 = var1.indexOf(var4);
            }

            this.currentIndex = var5;
         });
         this.setValue(this._getValue(this.currentIndex));
      }

      public final void setItems(ObservableList<T> var1) {
         this.itemsProperty().set(var1);
      }

      public final ObservableList<T> getItems() {
         return this.items == null ? null : (ObservableList)this.items.get();
      }

      public final ObjectProperty<ObservableList<T>> itemsProperty() {
         if (this.items == null) {
            this.items = new SimpleObjectProperty<ObservableList<T>>(this, "items") {
               WeakReference<ObservableList<T>> oldItemsRef;

               protected void invalidated() {
                  ObservableList var1 = this.oldItemsRef == null ? null : this.oldItemsRef.get();
                  ObservableList var2 = ListSpinnerValueFactory.this.getItems();
                  if (var1 != null) {
                     var1.removeListener(ListSpinnerValueFactory.this.weakItemsContentObserver);
                  }

                  if (var2 != null) {
                     var2.addListener(ListSpinnerValueFactory.this.weakItemsContentObserver);
                  }

                  ListSpinnerValueFactory.this.updateCurrentIndex();
                  this.oldItemsRef = new WeakReference<>(ListSpinnerValueFactory.this.getItems());
               }
            };
         }

         return this.items;
      }

      @Override
      public void decrement(int var1) {
         int var2 = this.getItemsSize() - 1;
         int var3 = this.currentIndex - var1;
         this.currentIndex = var3 >= 0 ? var3 : (this.isWrapAround() ? Spinner.wrapValue(var3, 0, var2 + 1) : 0);
         this.setValue(this._getValue(this.currentIndex));
      }

      @Override
      public void increment(int var1) {
         int var2 = this.getItemsSize() - 1;
         int var3 = this.currentIndex + var1;
         this.currentIndex = var3 <= var2 ? var3 : (this.isWrapAround() ? Spinner.wrapValue(var3, 0, var2 + 1) : var2);
         this.setValue(this._getValue(this.currentIndex));
      }

      private int getItemsSize() {
         ObservableList var1 = this.getItems();
         return var1 == null ? 0 : var1.size();
      }

      private void updateCurrentIndex() {
         int var1 = this.getItemsSize();
         if (this.currentIndex < 0 || this.currentIndex >= var1) {
            this.currentIndex = 0;
         }

         this.setValue(this._getValue(this.currentIndex));
      }

      private T _getValue(int var1) {
         ObservableList var2 = this.getItems();
         return (T)(var2 == null ? null : (var1 >= 0 && var1 < var2.size() ? var2.get(var1) : null));
      }
   }

   static class LocalDateSpinnerValueFactory extends SpinnerValueFactory<LocalDate> {
      private ObjectProperty<LocalDate> min = new SimpleObjectProperty<LocalDate>(this, "min") {
         protected void invalidated() {
            LocalDate var1 = (LocalDate)LocalDateSpinnerValueFactory.this.getValue();
            if (var1 != null) {
               LocalDate var2 = (LocalDate)this.get();
               if (var2.isAfter(LocalDateSpinnerValueFactory.this.getMax())) {
                  LocalDateSpinnerValueFactory.this.setMin(LocalDateSpinnerValueFactory.this.getMax());
               } else {
                  if (var1.isBefore(var2)) {
                     LocalDateSpinnerValueFactory.this.setValue(var2);
                  }
               }
            }
         }
      };
      private ObjectProperty<LocalDate> max = new SimpleObjectProperty<LocalDate>(this, "max") {
         protected void invalidated() {
            LocalDate var1 = (LocalDate)LocalDateSpinnerValueFactory.this.getValue();
            if (var1 != null) {
               LocalDate var2 = (LocalDate)this.get();
               if (var2.isBefore(LocalDateSpinnerValueFactory.this.getMin())) {
                  LocalDateSpinnerValueFactory.this.setMax(LocalDateSpinnerValueFactory.this.getMin());
               } else {
                  if (var1.isAfter(var2)) {
                     LocalDateSpinnerValueFactory.this.setValue(var2);
                  }
               }
            }
         }
      };
      private ObjectProperty<TemporalUnit> temporalUnit = new SimpleObjectProperty(this, "temporalUnit");
      private LongProperty amountToStepBy = new SimpleLongProperty(this, "amountToStepBy");

      public LocalDateSpinnerValueFactory() {
         this(LocalDate.now());
      }

      public LocalDateSpinnerValueFactory(@NamedArg("initialValue") LocalDate var1) {
         this(LocalDate.MIN, LocalDate.MAX, var1);
      }

      public LocalDateSpinnerValueFactory(@NamedArg("min") LocalDate var1, @NamedArg("min") LocalDate var2, @NamedArg("initialValue") LocalDate var3) {
         this(var1, var2, var3, 1L, ChronoUnit.DAYS);
      }

      public LocalDateSpinnerValueFactory(
         @NamedArg("min") LocalDate var1,
         @NamedArg("min") LocalDate var2,
         @NamedArg("initialValue") LocalDate var3,
         @NamedArg("amountToStepBy") long var4,
         @NamedArg("temporalUnit") TemporalUnit var6
      ) {
         this.setMin(var1);
         this.setMax(var2);
         this.setAmountToStepBy(var4);
         this.setTemporalUnit(var6);
         this.setConverter(new StringConverter<LocalDate>() {
            public String toString(LocalDate var1) {
               return var1 == null ? "" : var1.toString();
            }

            public LocalDate fromString(String var1) {
               return LocalDate.parse(var1);
            }
         });
         this.valueProperty().addListener((var1x, var2x, var3x) -> {
            if (this.getMin() != null && var3x.isBefore(this.getMin())) {
               this.setValue(this.getMin());
            } else if (this.getMax() != null && var3x.isAfter(this.getMax())) {
               this.setValue(this.getMax());
            }
         });
         this.setValue(var3 != null ? var3 : LocalDate.now());
      }

      public final void setMin(LocalDate var1) {
         this.min.set(var1);
      }

      public final LocalDate getMin() {
         return (LocalDate)this.min.get();
      }

      public final ObjectProperty<LocalDate> minProperty() {
         return this.min;
      }

      public final void setMax(LocalDate var1) {
         this.max.set(var1);
      }

      public final LocalDate getMax() {
         return (LocalDate)this.max.get();
      }

      public final ObjectProperty<LocalDate> maxProperty() {
         return this.max;
      }

      public final void setTemporalUnit(TemporalUnit var1) {
         this.temporalUnit.set(var1);
      }

      public final TemporalUnit getTemporalUnit() {
         return (TemporalUnit)this.temporalUnit.get();
      }

      public final ObjectProperty<TemporalUnit> temporalUnitProperty() {
         return this.temporalUnit;
      }

      public final void setAmountToStepBy(long var1) {
         this.amountToStepBy.set(var1);
      }

      public final long getAmountToStepBy() {
         return this.amountToStepBy.get();
      }

      public final LongProperty amountToStepByProperty() {
         return this.amountToStepBy;
      }

      @Override
      public void decrement(int var1) {
         LocalDate var2 = (LocalDate)this.getValue();
         LocalDate var3 = this.getMin();
         LocalDate var4 = var2.minus(this.getAmountToStepBy() * (long)var1, this.getTemporalUnit());
         if (var3 != null && this.isWrapAround() && var4.isBefore(var3)) {
            var4 = this.getMax();
         }

         this.setValue(var4);
      }

      @Override
      public void increment(int var1) {
         LocalDate var2 = (LocalDate)this.getValue();
         LocalDate var3 = this.getMax();
         LocalDate var4 = var2.plus(this.getAmountToStepBy() * (long)var1, this.getTemporalUnit());
         if (var3 != null && this.isWrapAround() && var4.isAfter(var3)) {
            var4 = this.getMin();
         }

         this.setValue(var4);
      }
   }

   static class LocalTimeSpinnerValueFactory extends SpinnerValueFactory<LocalTime> {
      private ObjectProperty<LocalTime> min = new SimpleObjectProperty<LocalTime>(this, "min") {
         protected void invalidated() {
            LocalTime var1 = (LocalTime)LocalTimeSpinnerValueFactory.this.getValue();
            if (var1 != null) {
               LocalTime var2 = (LocalTime)this.get();
               if (var2.isAfter(LocalTimeSpinnerValueFactory.this.getMax())) {
                  LocalTimeSpinnerValueFactory.this.setMin(LocalTimeSpinnerValueFactory.this.getMax());
               } else {
                  if (var1.isBefore(var2)) {
                     LocalTimeSpinnerValueFactory.this.setValue(var2);
                  }
               }
            }
         }
      };
      private ObjectProperty<LocalTime> max = new SimpleObjectProperty<LocalTime>(this, "max") {
         protected void invalidated() {
            LocalTime var1 = (LocalTime)LocalTimeSpinnerValueFactory.this.getValue();
            if (var1 != null) {
               LocalTime var2 = (LocalTime)this.get();
               if (var2.isBefore(LocalTimeSpinnerValueFactory.this.getMin())) {
                  LocalTimeSpinnerValueFactory.this.setMax(LocalTimeSpinnerValueFactory.this.getMin());
               } else {
                  if (var1.isAfter(var2)) {
                     LocalTimeSpinnerValueFactory.this.setValue(var2);
                  }
               }
            }
         }
      };
      private ObjectProperty<TemporalUnit> temporalUnit = new SimpleObjectProperty(this, "temporalUnit");
      private LongProperty amountToStepBy = new SimpleLongProperty(this, "amountToStepBy");

      public LocalTimeSpinnerValueFactory() {
         this(LocalTime.now());
      }

      public LocalTimeSpinnerValueFactory(@NamedArg("initialValue") LocalTime var1) {
         this(LocalTime.MIN, LocalTime.MAX, var1);
      }

      public LocalTimeSpinnerValueFactory(@NamedArg("min") LocalTime var1, @NamedArg("min") LocalTime var2, @NamedArg("initialValue") LocalTime var3) {
         this(var1, var2, var3, 1L, ChronoUnit.HOURS);
      }

      public LocalTimeSpinnerValueFactory(
         @NamedArg("min") LocalTime var1,
         @NamedArg("min") LocalTime var2,
         @NamedArg("initialValue") LocalTime var3,
         @NamedArg("amountToStepBy") long var4,
         @NamedArg("temporalUnit") TemporalUnit var6
      ) {
         this.setMin(var1);
         this.setMax(var2);
         this.setAmountToStepBy(var4);
         this.setTemporalUnit(var6);
         this.setConverter(new StringConverter<LocalTime>() {
            private DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

            public String toString(LocalTime var1) {
               return var1 == null ? "" : var1.format(this.dtf);
            }

            public LocalTime fromString(String var1) {
               return LocalTime.parse(var1);
            }
         });
         this.valueProperty().addListener((var1x, var2x, var3x) -> {
            if (this.getMin() != null && var3x.isBefore(this.getMin())) {
               this.setValue(this.getMin());
            } else if (this.getMax() != null && var3x.isAfter(this.getMax())) {
               this.setValue(this.getMax());
            }
         });
         this.setValue(var3 != null ? var3 : LocalTime.now());
      }

      public final void setMin(LocalTime var1) {
         this.min.set(var1);
      }

      public final LocalTime getMin() {
         return (LocalTime)this.min.get();
      }

      public final ObjectProperty<LocalTime> minProperty() {
         return this.min;
      }

      public final void setMax(LocalTime var1) {
         this.max.set(var1);
      }

      public final LocalTime getMax() {
         return (LocalTime)this.max.get();
      }

      public final ObjectProperty<LocalTime> maxProperty() {
         return this.max;
      }

      public final void setTemporalUnit(TemporalUnit var1) {
         this.temporalUnit.set(var1);
      }

      public final TemporalUnit getTemporalUnit() {
         return (TemporalUnit)this.temporalUnit.get();
      }

      public final ObjectProperty<TemporalUnit> temporalUnitProperty() {
         return this.temporalUnit;
      }

      public final void setAmountToStepBy(long var1) {
         this.amountToStepBy.set(var1);
      }

      public final long getAmountToStepBy() {
         return this.amountToStepBy.get();
      }

      public final LongProperty amountToStepByProperty() {
         return this.amountToStepBy;
      }

      @Override
      public void decrement(int var1) {
         LocalTime var2 = (LocalTime)this.getValue();
         LocalTime var3 = this.getMin();
         Duration var4 = Duration.of(this.getAmountToStepBy() * (long)var1, this.getTemporalUnit());
         long var5 = var4.toMinutes() * 60L;
         long var7 = (long)var2.toSecondOfDay();
         if (!this.isWrapAround() && var5 > var7) {
            this.setValue(var3 == null ? LocalTime.MIN : var3);
         } else {
            this.setValue(var2.minus(var4));
         }
      }

      @Override
      public void increment(int var1) {
         LocalTime var2 = (LocalTime)this.getValue();
         LocalTime var3 = this.getMax();
         Duration var4 = Duration.of(this.getAmountToStepBy() * (long)var1, this.getTemporalUnit());
         long var5 = var4.toMinutes() * 60L;
         long var7 = (long)var2.toSecondOfDay();
         if (!this.isWrapAround() && var5 > (long)LocalTime.MAX.toSecondOfDay() - var7) {
            this.setValue(var3 == null ? LocalTime.MAX : var3);
         } else {
            this.setValue(var2.plus(var4));
         }
      }
   }
}
