package com.codahale.metrics;

public abstract class DerivativeGauge<F, T> implements Gauge<T> {
   private final Gauge<F> base;

   protected DerivativeGauge(Gauge<F> base) {
      this.base = base;
   }

   @Override
   public T getValue() {
      return this.transform(this.base.getValue());
   }

   protected abstract T transform(F var1);
}
