package com.sun.javafx.fxml.expression;

import java.util.List;

public class LiteralExpression<T> extends Expression<T> {
   private T value;

   public LiteralExpression(T var1) {
      this.value = (T)var1;
   }

   @Override
   public T evaluate(Object var1) {
      return this.value;
   }

   @Override
   public void update(Object var1, T var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isDefined(Object var1) {
      return true;
   }

   @Override
   public boolean isLValue() {
      return false;
   }

   @Override
   protected void getArguments(List<KeyPath> var1) {
   }

   @Override
   public String toString() {
      return String.valueOf(this.value);
   }
}
