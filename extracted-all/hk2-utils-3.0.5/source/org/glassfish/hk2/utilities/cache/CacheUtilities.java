package org.glassfish.hk2.utilities.cache;

import org.glassfish.hk2.utilities.cache.internal.WeakCARCacheImpl;

public class CacheUtilities {
   public static <K, V> WeakCARCache<K, V> createWeakCARCache(Computable<K, V> computable, int maxSize, boolean isWeak) {
      return new WeakCARCacheImpl<>(computable, maxSize, isWeak);
   }
}
