package com.sun.javafx.binding;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableLongValue;

public final class LongConstant implements ObservableLongValue {
   private final long value;

   private LongConstant(long var1) {
      this.value = var1;
   }

   public static LongConstant valueOf(long var0) {
      return new LongConstant(var0);
   }

   @Override
   public long get() {
      return this.value;
   }

   public Long getValue() {
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
      return this.value;
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
