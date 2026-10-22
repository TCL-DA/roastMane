package com.sun.javafx.fxml.expression;

import java.util.List;
import java.util.function.Function;

public final class UnaryExpression<U, T> extends Expression<T> {
   private final Expression<U> operand;
   private final Function<U, T> evaluator;

   public UnaryExpression(Expression<U> var1, Function<U, T> var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.operand = var1;
         this.evaluator = var2;
      }
   }

   @Override
   public T evaluate(Object var1) {
      return this.evaluator.apply(this.operand.evaluate(var1));
   }

   @Override
   public void update(Object var1, T var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isDefined(Object var1) {
      return this.operand.isDefined(var1);
   }

   @Override
   public boolean isLValue() {
      return false;
   }

   @Override
   protected void getArguments(List<KeyPath> var1) {
      this.operand.getArguments(var1);
   }
}
