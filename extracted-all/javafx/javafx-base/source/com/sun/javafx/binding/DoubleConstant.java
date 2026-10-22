package com.sun.javafx.binding;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableDoubleValue;

public final class DoubleConstant implements ObservableDoubleValue {
   private final double value;

   private DoubleConstant(double var1) {
      this.value = var1;
   }

   public static DoubleConstant valueOf(double var0) {
      return new DoubleConstant(var0);
   }

   @Override
   public double get() {
      return this.value;
   }

   public Double getValue() {
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
      return (float)this.value;
   }

   @Override
   public double doubleValue() {
      return this.value;
   }
}
