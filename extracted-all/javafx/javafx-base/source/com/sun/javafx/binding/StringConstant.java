package com.sun.javafx.binding;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.StringExpression;
import javafx.beans.value.ChangeListener;

public class StringConstant extends StringExpression {
   private final String value;

   private StringConstant(String var1) {
      this.value = var1;
   }

   public static StringConstant valueOf(String var0) {
      return new StringConstant(var0);
   }

   public String get() {
      return this.value;
   }

   @Override
   public String getValue() {
      return this.value;
   }

   @Override
   public void addListener(InvalidationListener var1) {
   }

   @Override
   public void addListener(ChangeListener<? super String> var1) {
   }

   @Override
   public void removeListener(InvalidationListener var1) {
   }

   @Override
   public void removeListener(ChangeListener<? super String> var1) {
   }
}
