package org.glassfish.hk2.utilities.cache;

import org.glassfish.hk2.utilities.cache.internal.LRUCacheCheapRead;

public abstract class LRUCache<K, V> {
   public static <K, V> LRUCache<K, V> createCache(int maxCacheSize) {
      return new LRUCacheCheapRead<>(maxCacheSize);
   }

   public abstract V get(K var1);

   public abstract CacheEntry put(K var1, V var2);

   public abstract void releaseCache();

   public abstract int getMaxCacheSize();

   public abstract void releaseMatching(CacheKeyFilter<K> var1);
}
