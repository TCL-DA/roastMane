package org.glassfish.hk2.utilities.cache;

public interface HybridCacheEntry<V> extends CacheEntry {
   V getValue();

   boolean dropMe();
}
