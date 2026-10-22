package com.sun.javafx.binding;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;

public class ObjectConstant<T> implements ObservableObjectValue<T> {
   private final T value;

   private ObjectConstant(T var1) {
      this.value = (T)var1;
   }

   public static <T> ObjectConstant<T> valueOf(T var0) {
      return new ObjectConstant<>((T)var0);
   }

   @Override
   public T get() {
      return this.value;
   }

   @Override
   public T getValue() {
      return this.value;
   }

   @Override
   public void addListener(InvalidationListener var1) {
   }

   @Override
   public void addListener(ChangeListener<? super T> var1) {
   }

   @Override
   public void removeListener(InvalidationListener var1) {
   }

   @Override
   public void removeListener(ChangeListener<? super T> var1) {
   }
}
