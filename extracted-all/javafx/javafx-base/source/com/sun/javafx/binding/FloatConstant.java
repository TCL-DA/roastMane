package com.sun.javafx.binding;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableFloatValue;

public final class FloatConstant implements ObservableFloatValue {
   private final float value;

   private FloatConstant(float var1) {
      this.value = var1;
   }

   public static FloatConstant valueOf(float var0) {
      return new FloatConstant(var0);
   }

   @Override
   public float get() {
      return this.value;
   }

   public Float getValue() {
      return this.value;
   }

   @Override
   public void addListener(InvalidationListener var1) {
   }

   @Override
   public void addListener(ChangeListener<? super Number> var1) {
   }

   @Override
   public void removeListener(InvalidationListener var1) {
   }

   @Override
   public void removeListener(ChangeListener<? super Number> var1) {
   }

   @Override
   public int intValue() {
      return (int)this.value;
   }

   @Override
   public long longValue() {
      return (long)this.value;
   }

   @Override
   public float floatValue() {
      return this.value;
   }

   @Override
   public double doubleValue() {
      return (double)this.value;
   }
}
