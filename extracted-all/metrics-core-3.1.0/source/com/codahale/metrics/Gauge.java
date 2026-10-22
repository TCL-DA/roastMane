package com.codahale.metrics;

public interface Gauge<T> extends Metric {
   T getValue();
}
