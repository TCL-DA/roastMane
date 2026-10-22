package com.codahale.metrics;

import java.util.EventListener;

public interface MetricRegistryListener extends EventListener {
   void onGaugeAdded(String var1, Gauge<?> var2);

   void onGaugeRemoved(String var1);

   void onCounterAdded(String var1, Counter var2);

   void onCounterRemoved(String var1);

   void onHistogramAdded(String var1, Histogram var2);

   void onHistogramRemoved(String var1);

   void onMeterAdded(String var1, Meter var2);

   void onMeterRemoved(String var1);

   void onTimerAdded(String var1, Timer var2);

   void onTimerRemoved(String var1);

   public abstract static class Base implements MetricRegistryListener {
      @Override
      public void onGaugeAdded(String name, Gauge<?> gauge) {
      }

      @Override
      public void onGaugeRemoved(String name) {
      }

      @Override
      public void onCounterAdded(String name, Counter counter) {
      }

      @Override
      public void onCounterRemoved(String name) {
      }

      @Override
      public void onHistogramAdded(String name, Histogram histogram) {
      }

      @Override
      public void onHistogramRemoved(String name) {
      }

      @Override
      public void onMeterAdded(String name, Meter meter) {
      }

      @Override
      public void onMeterRemoved(String name) {
      }

      @Override
      public void onTimerAdded(String name, Timer timer) {
      }

      @Override
      public void onTimerRemoved(String name) {
      }
   }
}
