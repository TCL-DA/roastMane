package com.segment.analytics.messages;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class ImmutableMap {
   static final boolean HAS_GUAVA = hasGuavaOnClasspath();

   private ImmutableMap() {
      throw new AssertionError("No instances.");
   }

   private static boolean hasGuavaOnClasspath() {
      try {
         Class.forName("com.google.common.collect.ImmutableMap");
         return true;
      } catch (ClassNotFoundException var1) {
         return false;
      }
   }

   static <K, V> Map<K, V> copyOf(Map<K, V> map) {
      return (Map<K, V>)(HAS_GUAVA ? com.google.common.collect.ImmutableMap.copyOf(map) : Collections.unmodifiableMap(new LinkedHashMap<>(map)));
   }
}
