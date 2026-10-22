package com.sun.scenario.animation.shared;

import com.sun.javafx.animation.KeyValueHelper;
import com.sun.scenario.animation.NumberTangentInterpolator;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.beans.value.WritableBooleanValue;
import javafx.beans.value.WritableDoubleValue;
import javafx.beans.value.WritableFloatValue;
import javafx.beans.value.WritableIntegerValue;
import javafx.beans.value.WritableLongValue;
import javafx.beans.value.WritableValue;

public abstract class InterpolationInterval {
   protected final long ticks;
   protected final Interpolator rightInterpolator;

   protected InterpolationInterval(long var1, Interpolator var3) {
      this.ticks = var1;
      this.rightInterpolator = var3;
   }

   public abstract void interpolate(double var1);

   public abstract void recalculateStartValue();

   public static InterpolationInterval create(KeyValue var0, long var1, KeyValue var3, long var4) {
      switch (KeyValueHelper.getType(var0)) {
         case BOOLEAN:
            return new InterpolationInterval.BooleanInterpolationInterval(var0, var1, var3.getEndValue());
         case DOUBLE:
            return (InterpolationInterval)(!(var3.getInterpolator() instanceof NumberTangentInterpolator)
                  && !(var0.getInterpolator() instanceof NumberTangentInterpolator)
               ? new InterpolationInterval.DoubleInterpolationInterval(var0, var1, var3.getEndValue())
               : new InterpolationInterval.TangentDoubleInterpolationInterval(var0, var1, var3, var4));
         case FLOAT:
            return (InterpolationInterval)(!(var3.getInterpolator() instanceof NumberTangentInterpolator)
                  && !(var0.getInterpolator() instanceof NumberTangentInterpolator)
               ? new InterpolationInterval.FloatInterpolationInterval(var0, var1, var3.getEndValue())
               : new InterpolationInterval.TangentFloatInterpolationInterval(var0, var1, var3, var4));
         case INTEGER:
            return (InterpolationInterval)(!(var3.getInterpolator() instanceof NumberTangentInterpolator)
                  && !(var0.getInterpolator() instanceof NumberTangentInterpolator)
               ? new InterpolationInterval.IntegerInterpolationInterval(var0, var1, var3.getEndValue())
               : new InterpolationInterval.TangentIntegerInterpolationInterval(var0, var1, var3, var4));
         case LONG:
            return (InterpolationInterval)(!(var3.getInterpolator() instanceof NumberTangentInterpolator)
                  && !(var0.getInterpolator() instanceof NumberTangentInterpolator)
               ? new InterpolationInterval.LongInterpolationInterval(var0, var1, var3.getEndValue())
               : new InterpolationInterval.TangentLongInterpolationInterval(var0, var1, var3, var4));
         case OBJECT:
            return new InterpolationInterval.ObjectInterpolationInterval(var0, var1, var3.getEndValue());
         default:
            throw new RuntimeException("Should not reach here");
      }
   }

   public static InterpolationInterval create(KeyValue var0, long var1) {
      switch (KeyValueHelper.getType(var0)) {
         case BOOLEAN:
            return new InterpolationInterval.BooleanInterpolationInterval(var0, var1);
         case DOUBLE:
            return (InterpolationInterval)(var0.getInterpolator() instanceof NumberTangentInterpolator
               ? new InterpolationInterval.TangentDoubleInterpolationInterval(var0, var1)
               : new InterpolationInterval.DoubleInterpolationInterval(var0, var1));
         case FLOAT:
            return (InterpolationInterval)(var0.getInterpolator() instanceof NumberTangentInterpolator
               ? new InterpolationInterval.TangentFloatInterpolationInterval(var0, var1)
               : new InterpolationInterval.FloatInterpolationInterval(var0, var1));
         case INTEGER:
            return (InterpolationInterval)(var0.getInterpolator() instanceof NumberTangentInterpolator
               ? new InterpolationInterval.TangentIntegerInterpolationInterval(var0, var1)
               : new InterpolationInterval.IntegerInterpolationInterval(var0, var1));
         case LONG:
            return (InterpolationInterval)(var0.getInterpolator() instanceof NumberTangentInterpolator
               ? new InterpolationInterval.TangentLongInterpolationInterval(var0, var1)
               : new InterpolationInterval.LongInterpolationInterval(var0, var1));
         case OBJECT:
            return new InterpolationInterval.ObjectInterpolationInterval(var0, var1);
         default:
            throw new RuntimeException("Should not reach here");
      }
   }

   private static class BooleanInterpolationInterval extends InterpolationInterval {
      private final WritableBooleanValue target;
      private boolean leftValue;
      private final boolean rightValue;

      private BooleanInterpolationInterval(KeyValue var1, long var2, Object var4) {
         super(var2, var1.getInterpolator());

         assert var1.getTarget() instanceof WritableBooleanValue && var1.getEndValue() instanceof Boolean && var4 instanceof Boolean;

         this.target = (WritableBooleanValue)var1.getTarget();
         this.rightValue = (Boolean)var1.getEndValue();
         this.leftValue = (Boolean)var4;
      }

      private BooleanInterpolationInterval(KeyValue var1, long var2) {
         super(var2, var1.getInterpolator());

         assert var1.getTarget() instanceof WritableBooleanValue && var1.getEndValue() instanceof Boolean;

         this.target = (WritableBooleanValue)var1.getTarget();
         this.rightValue = (Boolean)var1.getEndValue();
         this.leftValue = this.target.get();
      }

      @Override
      public void interpolate(double var1) {
         boolean var3 = this.rightInterpolator.interpolate(this.leftValue, this.rightValue, var1);
         this.target.set(var3);
      }

      @Override
      public void recalculateStartValue() {
         this.leftValue = this.target.get();
      }
   }

   private static class DoubleInterpolationInterval extends InterpolationInterval {
      private final WritableDoubleValue target;
      private double leftValue;
      private final double rightValue;

      private DoubleInterpolationInterval(KeyValue var1, long var2, Object var4) {
         super(var2, var1.getInterpolator());

         assert var1.getTarget() instanceof WritableDoubleValue && var1.getEndValue() instanceof Number && var4 instanceof Number;

         this.target = (WritableDoubleValue)var1.getTarget();
         this.rightValue = ((Number)var1.getEndValue()).doubleValue();
         this.leftValue = ((Number)var4).doubleValue();
      }

      private DoubleInterpolationInterval(KeyValue var1, long var2) {
         super(var2, var1.getInterpolator());

         assert var1.getTarget() instanceof WritableDoubleValue && var1.getEndValue() instanceof Number;

         this.target = (WritableDoubleValue)var1.getTarget();
         this.rightValue = ((Number)var1.getEndValue()).doubleValue();
         this.leftValue = this.target.get();
      }

      @Override
      public void interpolate(double var1) {
         double var3 = this.rightInterpolator.interpolate(this.leftValue, this.rightValue, var1);
         this.target.set(var3);
      }

      @Override
      public void recalculateStartValue() {
         this.leftValue = this.target.get();
      }
   }

   private static class FloatInterpolationInterval extends InterpolationInterval {
      private final WritableFloatValue target;
      private float leftValue;
      private final float rightValue;

      private FloatInterpolationInterval(KeyValue var1, long var2, Object var4) {
         super(var2, var1.getInterpolator());

         assert var1.getTarget() instanceof WritableFloatValue && var1.getEndValue() instanceof Number && var4 instanceof Number;

         this.target = (WritableFloatValue)var1.getTarget();
         this.rightValue = ((Number)var1.getEndValue()).floatValue();
         this.leftValue = ((Number)var4).floatValue();
      }

      private FloatInterpolationInterval(KeyValue var1, long var2) {
         super(var2, var1.getInterpolator());

         assert var1.getTarget() instanceof WritableFloatValue && var1.getEndValue() instanceof Number;

         this.target = (WritableFloatValue)var1.getTarget();
         this.rightValue = ((Number)var1.getEndValue()).floatValue();
         this.leftValue = this.target.get();
      }

      @Override
      public void interpolate(double var1) {
         float var3 = (float)this.rightInterpolator.interpolate((double)this.leftValue, (double)this.rightValue, var1);
         this.target.set(var3);
      }

      @Override
      public void recalculateStartValue() {
         this.leftValue = this.target.get();
      }
   }

   private static class IntegerInterpolationInterval extends InterpolationInterval {
      private final WritableIntegerValue target;
      private int leftValue;
      private final int rightValue;

      private IntegerInterpolationInterval(KeyValue var1, long var2, Object var4) {
         super(var2, var1.getInterpolator());

         assert var1.getTarget() instanceof WritableIntegerValue && var1.getEndValue() instanceof Number && var4 instanceof Number;

         this.target = (WritableIntegerValue)var1.getTarget();
         this.rightValue = ((Number)var1.getEndValue()).intValue();
         this.leftValue = ((Number)var4).intValue();
      }

      private IntegerInterpolationInterval(KeyValue var1, long var2) {
         super(var2, var1.getInterpolator());

         assert var1.getTarget() instanceof WritableIntegerValue && var1.getEndValue() instanceof Number;

         this.target = (WritableIntegerValue)var1.getTarget();
         this.rightValue = ((Number)var1.getEndValue()).intValue();
         this.leftValue = this.target.get();
      }

      @Override
      public void interpolate(double var1) {
         int var3 = this.rightInterpolator.interpolate(this.leftValue, this.rightValue, var1);
         this.target.set(var3);
      }

      @Override
      public void recalculateStartValue() {
         this.leftValue = this.target.get();
      }
   }

   private static class LongInterpolationInterval extends InterpolationInterval {
      private final WritableLongValue target;
      private long leftValue;
      private final long rightValue;

      private LongInterpolationInterval(KeyValue var1, long var2, Object var4) {
         super(var2, var1.getInterpolator());

         assert var1.getTarget() instanceof WritableLongValue && var1.getEndValue() instanceof Number && var4 instanceof Number;

         this.target = (WritableLongValue)var1.getTarget();
         this.rightValue = ((Number)var1.getEndValue()).longValue();
         this.leftValue = ((Number)var4).longValue();
      }

      private LongInterpolationInterval(KeyValue var1, long var2) {
         super(var2, var1.getInterpolator());

         assert var1.getTarget() instanceof WritableLongValue && var1.getEndValue() instanceof Number;

         this.target = (WritableLongValue)var1.getTarget();
         this.rightValue = ((Number)var1.getEndValue()).longValue();
         this.leftValue = this.target.get();
      }

      @Override
      public void interpolate(double var1) {
         long var3 = this.rightInterpolator.interpolate(this.leftValue, this.rightValue, var1);
         this.target.set(var3);
      }

      @Override
      public void recalculateStartValue() {
         this.leftValue = this.target.get();
      }
   }

   private static class ObjectInterpolationInterval extends InterpolationInterval {
      private final WritableValue target;
      private Object leftValue;
      private final Object rightValue;

      private ObjectInterpolationInterval(KeyValue var1, long var2, Object var4) {
         super(var2, var1.getInterpolator());
         this.target = var1.getTarget();
         this.rightValue = var1.getEndValue();
         this.leftValue = var4;
      }

      private ObjectInterpolationInterval(KeyValue var1, long var2) {
         super(var2, var1.getInterpolator());
         this.target = var1.getTarget();
         this.rightValue = var1.getEndValue();
         this.leftValue = this.target.getValue();
      }

      @Override
      public void interpolate(double var1) {
         Object var3 = this.rightInterpolator.interpolate(this.leftValue, this.rightValue, var1);
         this.target.setValue(var3);
      }

      @Override
      public void recalculateStartValue() {
         this.leftValue = this.target.getValue();
      }
   }

   private static class TangentDoubleInterpolationInterval extends InterpolationInterval.TangentInterpolationInterval {
      private final WritableDoubleValue target;

      private TangentDoubleInterpolationInterval(KeyValue var1, long var2, KeyValue var4, long var5) {
         super(var1, var2, var4, var5);

         assert var1.getTarget() instanceof WritableDoubleValue;

         this.target = (WritableDoubleValue)var1.getTarget();
      }

      private TangentDoubleInterpolationInterval(KeyValue var1, long var2) {
         super(var1, var2);

         assert var1.getTarget() instanceof WritableDoubleValue;

         this.target = (WritableDoubleValue)var1.getTarget();
         this.recalculateStartValue(this.target.get());
      }

      @Override
      public void interpolate(double var1) {
         this.target.set(this.calculate(var1));
      }

      @Override
      public void recalculateStartValue() {
         this.recalculateStartValue(this.target.get());
      }
   }

   private static class TangentFloatInterpolationInterval extends InterpolationInterval.TangentInterpolationInterval {
      private final WritableFloatValue target;

      private TangentFloatInterpolationInterval(KeyValue var1, long var2, KeyValue var4, long var5) {
         super(var1, var2, var4, var5);

         assert var1.getTarget() instanceof WritableFloatValue;

         this.target = (WritableFloatValue)var1.getTarget();
      }

      private TangentFloatInterpolationInterval(KeyValue var1, long var2) {
         super(var1, var2);

         assert var1.getTarget() instanceof WritableFloatValue;

         this.target = (WritableFloatValue)var1.getTarget();
         this.recalculateStartValue((double)this.target.get());
      }

      @Override
      public void interpolate(double var1) {
         this.target.set((float)this.calculate(var1));
      }

      @Override
      public void recalculateStartValue() {
         this.recalculateStartValue((double)this.target.get());
      }
   }

   private static class TangentIntegerInterpolationInterval extends InterpolationInterval.TangentInterpolationInterval {
      private final WritableIntegerValue target;

      private TangentIntegerInterpolationInterval(KeyValue var1, long var2, KeyValue var4, long var5) {
         super(var1, var2, var4, var5);

         assert var1.getTarget() instanceof WritableIntegerValue;

         this.target = (WritableIntegerValue)var1.getTarget();
      }

      private TangentIntegerInterpolationInterval(KeyValue var1, long var2) {
         super(var1, var2);

         assert var1.getTarget() instanceof WritableIntegerValue;

         this.target = (WritableIntegerValue)var1.getTarget();
         this.recalculateStartValue((double)this.target.get());
      }

      @Override
      public void interpolate(double var1) {
         this.target.set((int)Math.round(this.calculate(var1)));
      }

      @Override
      public void recalculateStartValue() {
         this.recalculateStartValue((double)this.target.get());
      }
   }

   private abstract static class TangentInterpolationInterval extends InterpolationInterval {
      private final double duration;
      private final double p2;
      protected final double p3;
      private final NumberTangentInterpolator leftInterpolator;
      protected double p0;
      private double p1;

      private TangentInterpolationInterval(KeyValue var1, long var2, KeyValue var4, long var5) {
         super(var2, var1.getInterpolator());

         assert var1.getEndValue() instanceof Number && var4.getEndValue() instanceof Number;

         this.duration = (double)var5;
         Interpolator var7 = var4.getInterpolator();
         this.leftInterpolator = var7 instanceof NumberTangentInterpolator ? (NumberTangentInterpolator)var7 : null;
         this.recalculateStartValue(((Number)var4.getEndValue()).doubleValue());
         NumberTangentInterpolator var8 = this.rightInterpolator instanceof NumberTangentInterpolator
            ? (NumberTangentInterpolator)this.rightInterpolator
            : null;
         this.p3 = ((Number)var1.getEndValue()).doubleValue();
         double var9 = var8 == null ? 0.0 : (var8.getInValue() - this.p3) * (double)var5 / var8.getInTicks() / 3.0;
         this.p2 = this.p3 + var9;
      }

      private TangentInterpolationInterval(KeyValue var1, long var2) {
         super(var2, var1.getInterpolator());

         assert var1.getEndValue() instanceof Number;

         this.duration = (double)var2;
         this.leftInterpolator = null;
         NumberTangentInterpolator var4 = this.rightInterpolator instanceof NumberTangentInterpolator
            ? (NumberTangentInterpolator)this.rightInterpolator
            : null;
         this.p3 = ((Number)var1.getEndValue()).doubleValue();
         double var5 = var4 == null ? 0.0 : (var4.getInValue() - this.p3) * this.duration / var4.getInTicks() / 3.0;
         this.p2 = this.p3 + var5;
      }

      protected double calculate(double var1) {
         double var3 = 1.0 - var1;
         double var5 = var1 * var1;
         double var7 = var3 * var3;
         return var7 * var3 * this.p0 + 3.0 * var7 * var1 * this.p1 + 3.0 * var3 * var5 * this.p2 + var5 * var1 * this.p3;
      }

      protected final void recalculateStartValue(double var1) {
         this.p0 = var1;
         double var3 = this.leftInterpolator == null
            ? 0.0
            : (this.leftInterpolator.getOutValue() - this.p0) * this.duration / this.leftInterpolator.getOutTicks() / 3.0;
         this.p1 = this.p0 + var3;
      }
   }

   private static class TangentLongInterpolationInterval extends InterpolationInterval.TangentInterpolationInterval {
      private final WritableLongValue target;

      private TangentLongInterpolationInterval(KeyValue var1, long var2, KeyValue var4, long var5) {
         super(var1, var2, var4, var5);

         assert var1.getTarget() instanceof WritableLongValue;

         this.target = (WritableLongValue)var1.getTarget();
      }

      private TangentLongInterpolationInterval(KeyValue var1, long var2) {
         super(var1, var2);

         assert var1.getTarget() instanceof WritableLongValue;

         this.target = (WritableLongValue)var1.getTarget();
         this.recalculateStartValue((double)this.target.get());
      }

      @Override
      public void interpolate(double var1) {
         this.target.set(Math.round(this.calculate(var1)));
      }

      @Override
      public void recalculateStartValue() {
         this.recalculateStartValue((double)this.target.get());
      }
   }
}
