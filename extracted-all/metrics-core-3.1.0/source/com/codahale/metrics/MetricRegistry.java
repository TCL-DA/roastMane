package com.codahale.metrics;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MetricRegistry implements MetricSet {
   private final ConcurrentMap<String, Metric> metrics = this.buildMap();
   private final List<MetricRegistryListener> listeners = new CopyOnWriteArrayList<>();

   public static String name(String name, String... names) {
      StringBuilder builder = new StringBuilder();
      append(builder, name);
      if (names != null) {
         for (String s : names) {
            append(builder, s);
         }
      }

      return builder.toString();
   }

   public static String name(Class<?> klass, String... names) {
      return name(klass.getName(), names);
   }

   private static void append(StringBuilder builder, String part) {
      if (part != null && !part.isEmpty()) {
         if (builder.length() > 0) {
            builder.append('.');
         }

         builder.append(part);
      }
   }

   protected ConcurrentMap<String, Metric> buildMap() {
      return new ConcurrentHashMap<>();
   }

   public <T extends Metric> T register(String name, T metric) throws IllegalArgumentException {
      if (metric instanceof MetricSet) {
         this.registerAll(name, (MetricSet)metric);
      } else {
         Metric existing = this.metrics.putIfAbsent(name, metric);
         if (existing != null) {
            throw new IllegalArgumentException("A metric named " + name + " already exists");
         }

         this.onMetricAdded(name, metric);
      }

      return metric;
   }

   public void registerAll(MetricSet metrics) throws IllegalArgumentException {
      this.registerAll(null, metrics);
   }

   public Counter counter(String name) {
      return this.getOrAdd(name, MetricRegistry.MetricBuilder.COUNTERS);
   }

   public Histogram histogram(String name) {
      return this.getOrAdd(name, MetricRegistry.MetricBuilder.HISTOGRAMS);
   }

   public Meter meter(String name) {
      return this.getOrAdd(name, MetricRegistry.MetricBuilder.METERS);
   }

   public Timer timer(String name) {
      return this.getOrAdd(name, MetricRegistry.MetricBuilder.TIMERS);
   }

   public boolean remove(String name) {
      Metric metric = this.metrics.remove(name);
      if (metric != null) {
         this.onMetricRemoved(name, metric);
         return true;
      } else {
         return false;
      }
   }

   public void removeMatching(MetricFilter filter) {
      for (Entry<String, Metric> entry : this.metrics.entrySet()) {
         if (filter.matches(entry.getKey(), entry.getValue())) {
            this.remove(entry.getKey());
         }
      }
   }

   public void addListener(MetricRegistryListener listener) {
      this.listeners.add(listener);

      for (Entry<String, Metric> entry : this.metrics.entrySet()) {
         this.notifyListenerOfAddedMetric(listener, entry.getValue(), entry.getKey());
      }
   }

   public void removeListener(MetricRegistryListener listener) {
      this.listeners.remove(listener);
   }

   public SortedSet<String> getNames() {
      return Collections.unmodifiableSortedSet(new TreeSet<>(this.metrics.keySet()));
   }

   public SortedMap<String, Gauge> getGauges() {
      return this.getGauges(MetricFilter.ALL);
   }

   public SortedMap<String, Gauge> getGauges(MetricFilter filter) {
      return this.getMetrics(Gauge.class, filter);
   }

   public SortedMap<String, Counter> getCounters() {
      return this.getCounters(MetricFilter.ALL);
   }

   public SortedMap<String, Counter> getCounters(MetricFilter filter) {
      return this.getMetrics(Counter.class, filter);
   }

   public SortedMap<String, Histogram> getHistograms() {
      return this.getHistograms(MetricFilter.ALL);
   }

   public SortedMap<String, Histogram> getHistograms(MetricFilter filter) {
      return this.getMetrics(Histogram.class, filter);
   }

   public SortedMap<String, Meter> getMeters() {
      return this.getMeters(MetricFilter.ALL);
   }

   public SortedMap<String, Meter> getMeters(MetricFilter filter) {
      return this.getMetrics(Meter.class, filter);
   }

   public SortedMap<String, Timer> getTimers() {
      return this.getTimers(MetricFilter.ALL);
   }

   public SortedMap<String, Timer> getTimers(MetricFilter filter) {
      return this.getMetrics(Timer.class, filter);
   }

   private <T extends Metric> T getOrAdd(String name, MetricRegistry.MetricBuilder<T> builder) {
      Metric metric = this.metrics.get(name);
      if (builder.isInstance(metric)) {
         return (T)metric;
      } else {
         if (metric == null) {
            try {
               return this.register(name, builder.newMetric());
            } catch (IllegalArgumentException var6) {
               Metric added = this.metrics.get(name);
               if (builder.isInstance(added)) {
                  return (T)added;
               }
            }
         }

         throw new IllegalArgumentException(name + " is already used for a different type of metric");
      }
   }

   private <T extends Metric> SortedMap<String, T> getMetrics(Class<T> klass, MetricFilter filter) {
      TreeMap<String, T> timers = new TreeMap<>();

      for (Entry<String, Metric> entry : this.metrics.entrySet()) {
         if (klass.isInstance(entry.getValue()) && filter.matches(entry.getKey(), entry.getValue())) {
            timers.put(entry.getKey(), (T)entry.getValue());
         }
      }

      return Collections.unmodifiableSortedMap(timers);
   }

   private void onMetricAdded(String name, Metric metric) {
      for (MetricRegistryListener listener : this.listeners) {
         this.notifyListenerOfAddedMetric(listener, metric, name);
      }
   }

   private void notifyListenerOfAddedMetric(MetricRegistryListener listener, Metric metric, String name) {
      if (metric instanceof Gauge) {
         listener.onGaugeAdded(name, (Gauge<?>)metric);
      } else if (metric instanceof Counter) {
         listener.onCounterAdded(name, (Counter)metric);
      } else if (metric instanceof Histogram) {
         listener.onHistogramAdded(name, (Histogram)metric);
      } else if (metric instanceof Meter) {
         listener.onMeterAdded(name, (Meter)metric);
      } else {
         if (!(metric instanceof Timer)) {
            throw new IllegalArgumentException("Unknown metric type: " + metric.getClass());
         }

         listener.onTimerAdded(name, (Timer)metric);
      }
   }

   private void onMetricRemoved(String name, Metric metric) {
      for (MetricRegistryListener listener : this.listeners) {
         this.notifyListenerOfRemovedMetric(name, metric, listener);
      }
   }

   private void notifyListenerOfRemovedMetric(String name, Metric metric, MetricRegistryListener listener) {
      if (metric instanceof Gauge) {
         listener.onGaugeRemoved(name);
      } else if (metric instanceof Counter) {
         listener.onCounterRemoved(name);
      } else if (metric instanceof Histogram) {
         listener.onHistogramRemoved(name);
      } else if (metric instanceof Meter) {
         listener.onMeterRemoved(name);
      } else {
         if (!(metric instanceof Timer)) {
            throw new IllegalArgumentException("Unknown metric type: " + metric.getClass());
         }

         listener.onTimerRemoved(name);
      }
   }

   private void registerAll(String prefix, MetricSet metrics) throws IllegalArgumentException {
      for (Entry<String, Metric> entry : metrics.getMetrics().entrySet()) {
         if (entry.getValue() instanceof MetricSet) {
            this.registerAll(name(prefix, entry.getKey()), (MetricSet)entry.getValue());
         } else {
            this.register(name(prefix, entry.getKey()), entry.getValue());
         }
      }
   }

   @Override
   public Map<String, Metric> getMetrics() {
      return Collections.unmodifiableMap(this.metrics);
   }

   private interface MetricBuilder<T extends Metric> {
      MetricRegistry.MetricBuilder<Counter> COUNTERS = new MetricRegistry.MetricBuilder<Counter>() {
         public Counter newMetric() {
            return new Counter();
         }

         @Override
         public boolean isInstance(Metric metric) {
            return Counter.class.isInstance(metric);
         }
      };
      MetricRegistry.MetricBuilder<Histogram> HISTOGRAMS = new MetricRegistry.MetricBuilder<Histogram>() {
         public Histogram newMetric() {
            return new Histogram(new ExponentiallyDecayingReservoir());
         }

         @Override
         public boolean isInstance(Metric metric) {
            return Histogram.class.isInstance(metric);
         }
      };
      MetricRegistry.MetricBuilder<Meter> METERS = new MetricRegistry.MetricBuilder<Meter>() {
         public Meter newMetric() {
            return new Meter();
         }

         @Override
         public boolean isInstance(Metric metric) {
            return Meter.class.isInstance(metric);
         }
      };
      MetricRegistry.MetricBuilder<Timer> TIMERS = new MetricRegistry.MetricBuilder<Timer>() {
         public Timer newMetric() {
            return new Timer();
         }

         @Override
         public boolean isInstance(Metric metric) {
            return Timer.class.isInstance(metric);
         }
      };

      T newMetric();

      boolean isInstance(Metric var1);
   }
}
