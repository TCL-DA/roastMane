package com.sun.javafx.fxml.expression;

import java.util.List;
import java.util.function.BiFunction;

public final class BinaryExpression<U, T> extends Expression<T> {
   private final BiFunction<U, U, T> evaluator;
   private final Expression<U> left;
   private final Expression<U> right;

   public BinaryExpression(Expression<U> var1, Expression<U> var2, BiFunction<U, U, T> var3) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 == null) {
         throw new NullPointerException();
      } else {
         this.left = var1;
         this.right = var2;
         this.evaluator = var3;
      }
   }

   @Override
   public T evaluate(Object var1) {
      return this.evaluator.apply(this.left.evaluate(var1), this.right.evaluate(var1));
   }

   @Override
   public void update(Object var1, Object var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isDefined(Object var1) {
      return this.left.isDefined(var1) && this.right.isDefined(var1);
   }

   @Override
   public boolean isLValue() {
      return false;
   }

   @Override
   protected void getArguments(List<KeyPath> var1) {
      this.left.getArguments(var1);
      this.right.getArguments(var1);
   }
}
