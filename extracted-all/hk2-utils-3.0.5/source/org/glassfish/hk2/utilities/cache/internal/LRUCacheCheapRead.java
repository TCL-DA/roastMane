package org.glassfish.hk2.utilities.cache.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.glassfish.hk2.utilities.cache.CacheEntry;
import org.glassfish.hk2.utilities.cache.CacheKeyFilter;
import org.glassfish.hk2.utilities.cache.LRUCache;

public class LRUCacheCheapRead<K, V> extends LRUCache<K, V> {
   final Object prunningLock = new Object();
   final int maxCacheSize;
   Map<K, LRUCacheCheapRead.CacheEntryImpl<K, V>> cache = new ConcurrentHashMap<>();
   private static final LRUCacheCheapRead.CacheEntryImplComparator COMPARATOR = new LRUCacheCheapRead.CacheEntryImplComparator();

   public LRUCacheCheapRead(int maxCacheSize) {
      this.maxCacheSize = maxCacheSize;
   }

   @Override
   public V get(K key) {
      LRUCacheCheapRead.CacheEntryImpl<K, V> entry = this.cache.get(key);
      return entry != null ? entry.hit().value : null;
   }

   @Override
   public CacheEntry put(K key, V value) {
      LRUCacheCheapRead.CacheEntryImpl<K, V> entry = new LRUCacheCheapRead.CacheEntryImpl<>(key, value, this);
      synchronized (this.prunningLock) {
         if (this.cache.size() + 1 > this.maxCacheSize) {
            this.removeLRUItem();
         }

         this.cache.put(key, entry);
         return entry;
      }
   }

   @Override
   public void releaseCache() {
      this.cache.clear();
   }

   @Override
   public int getMaxCacheSize() {
      return this.maxCacheSize;
   }

   @Override
   public void releaseMatching(CacheKeyFilter<K> filter) {
      if (filter != null) {
         for (Entry<K, LRUCacheCheapRead.CacheEntryImpl<K, V>> entry : new HashMap<>(this.cache).entrySet()) {
            if (filter.matches(entry.getKey())) {
               entry.getValue().removeFromCache();
            }
         }
      }
   }

   private void removeLRUItem() {
      Collection<LRUCacheCheapRead.CacheEntryImpl<K, V>> values = this.cache.values();
      Collections.min(values, COMPARATOR).removeFromCache();
   }

   private static class CacheEntryImpl<K, V> implements CacheEntry {
      final K key;
      final V value;
      final LRUCacheCheapRead<K, V> parent;
      long lastHit;

      public CacheEntryImpl(K k, V v, LRUCacheCheapRead<K, V> cache) {
         this.parent = cache;
         this.key = k;
         this.value = v;
         this.lastHit = System.nanoTime();
      }

      @Override
      public void removeFromCache() {
         this.parent.cache.remove(this.key);
      }

      public LRUCacheCheapRead.CacheEntryImpl<K, V> hit() {
         this.lastHit = System.nanoTime();
         return this;
      }
   }

   private static class CacheEntryImplComparator implements Comparator<LRUCacheCheapRead.CacheEntryImpl<?, ?>> {
      public int compare(LRUCacheCheapRead.CacheEntryImpl<?, ?> first, LRUCacheCheapRead.CacheEntryImpl<?, ?> second) {
         long diff = first.lastHit - second.lastHit;
         return diff > 0L ? 1 : (diff == 0L ? 0 : -1);
      }
   }
}
