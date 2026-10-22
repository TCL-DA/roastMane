package com.sun.javafx.binding;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableIntegerValue;

public final class IntegerConstant implements ObservableIntegerValue {
   private final int value;

   private IntegerConstant(int var1) {
      this.value = var1;
   }

   public static IntegerConstant valueOf(int var0) {
      return new IntegerConstant(var0);
   }

   @Override
   public int get() {
      return this.value;
   }

   public Integer getValue() {
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
      return this.value;
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
      return (double)this.value;
   }
}
