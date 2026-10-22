package org.glassfish.jersey.internal.util.collection;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedTransferQueue;

public final class DataStructures {
   public static final int DEFAULT_CONCURENCY_LEVEL = ceilingNextPowerOfTwo(Runtime.getRuntime().availableProcessors());

   private static int ceilingNextPowerOfTwo(int x) {
      return 1 << 32 - Integer.numberOfLeadingZeros(x - 1);
   }

   @Deprecated
   public static <E> BlockingQueue<E> createLinkedTransferQueue() {
      return new LinkedTransferQueue<>();
   }

   @Deprecated
   public static <K, V> ConcurrentMap<K, V> createConcurrentMap() {
      return new ConcurrentHashMap<>();
   }

   @Deprecated
   public static <K, V> ConcurrentMap<K, V> createConcurrentMap(Map<? extends K, ? extends V> map) {
      return new ConcurrentHashMap<>(map);
   }

   @Deprecated
   public static <K, V> ConcurrentMap<K, V> createConcurrentMap(int initialCapacity) {
      return new ConcurrentHashMap<>(initialCapacity);
   }

   @Deprecated
   public static <K, V> ConcurrentMap<K, V> createConcurrentMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
      return new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
   }
}
