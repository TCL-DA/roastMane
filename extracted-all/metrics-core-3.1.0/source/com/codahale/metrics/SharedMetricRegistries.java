package com.codahale.metrics;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SharedMetricRegistries {
   private static final ConcurrentMap<String, MetricRegistry> REGISTRIES = new ConcurrentHashMap<>();

   private SharedMetricRegistries() {
   }

   public static void clear() {
      REGISTRIES.clear();
   }

   public static Set<String> names() {
      return REGISTRIES.keySet();
   }

   public static void remove(String key) {
      REGISTRIES.remove(key);
   }

   public static MetricRegistry add(String name, MetricRegistry registry) {
      return REGISTRIES.putIfAbsent(name, registry);
   }

   public static MetricRegistry getOrCreate(String name) {
      MetricRegistry existing = REGISTRIES.get(name);
      if (existing == null) {
         MetricRegistry created = new MetricRegistry();
         MetricRegistry raced = add(name, created);
         return raced == null ? created : raced;
      } else {
         return existing;
      }
   }
}
