package org.glassfish.jersey.internal.util.collection;

import java.util.concurrent.TimeUnit;
import org.glassfish.jersey.internal.guava.CacheBuilder;

public abstract class LRU<K, V> {
   public abstract V getIfPresent(Object var1);

   public abstract void put(K var1, V var2);

   public static <K, V> LRU<K, V> create() {
      return LRU.LRUFactory.createLRU();
   }

   private static class LRUFactory {
      public static final int LRU_CACHE_SIZE = 128;
      public static final long TIMEOUT = 5000L;

      private static <K, V> LRU<K, V> createLRU() {
         final org.glassfish.jersey.internal.guava.Cache<K, V> CACHE = CacheBuilder.newBuilder()
            .maximumSize(128L)
            .expireAfterAccess(5000L, TimeUnit.MILLISECONDS)
            .build();
         return new LRU<K, V>() {
            @Override
            public V getIfPresent(Object key) {
               return CACHE.getIfPresent(key);
            }

            @Override
            public void put(K key, V value) {
               CACHE.put(key, value);
            }
         };
      }
   }
}
