package com.sun.javafx.fxml.expression;

import java.util.List;

public class VariableExpression extends Expression<Object> {
   private KeyPath keyPath;

   public VariableExpression(KeyPath var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.keyPath = var1;
      }
   }

   public KeyPath getKeyPath() {
      return this.keyPath;
   }

   @Override
   public Object evaluate(Object var1) {
      return get(var1, this.keyPath);
   }

   @Override
   public void update(Object var1, Object var2) {
      set(var1, this.keyPath, var2);
   }

   @Override
   public boolean isDefined(Object var1) {
      return isDefined(var1, this.keyPath);
   }

   @Override
   public boolean isLValue() {
      return true;
   }

   @Override
   protected void getArguments(List<KeyPath> var1) {
      var1.add(this.keyPath);
   }

   @Override
   public String toString() {
      return this.keyPath.toString();
   }
}
