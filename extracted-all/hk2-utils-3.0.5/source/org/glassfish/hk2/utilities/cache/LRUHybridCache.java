package org.glassfish.hk2.utilities.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LRUHybridCache<K, V> implements Computable<K, HybridCacheEntry<V>> {
   private final LRUHybridCache.CycleHandler<K> cycleHandler;
   private static final LRUHybridCache.CycleHandler<Object> EMPTY_CYCLE_HANDLER = new LRUHybridCache.CycleHandler<Object>() {
      @Override
      public void handleCycle(Object key) {
      }
   };
   private final ConcurrentHashMap<K, LRUHybridCache<K, V>.OriginThreadAwareFuture> cache = new ConcurrentHashMap<>();
   private final Computable<K, HybridCacheEntry<V>> computable;
   private final Object prunningLock = new Object();
   private final int maxCacheSize;
   private static final Comparator<LRUHybridCache.OriginThreadAwareFuture> COMPARATOR = new LRUHybridCache.CacheEntryImplComparator();

   public LRUHybridCache(int maxCacheSize, Computable<K, HybridCacheEntry<V>> computable) {
      this(maxCacheSize, computable, (LRUHybridCache.CycleHandler<K>)EMPTY_CYCLE_HANDLER);
   }

   public LRUHybridCache(int maxCacheSize, Computable<K, HybridCacheEntry<V>> computable, LRUHybridCache.CycleHandler<K> cycleHandler) {
      this.maxCacheSize = maxCacheSize;
      this.computable = computable;
      this.cycleHandler = cycleHandler;
   }

   public HybridCacheEntry<V> createCacheEntry(K k, V v, boolean dropMe) {
      return new LRUHybridCache.HybridCacheEntryImpl<>(k, v, dropMe);
   }

   public HybridCacheEntry<V> compute(K key) {
      LRUHybridCache<K, V>.OriginThreadAwareFuture f = this.cache.get(key);
      if (f == null) {
         LRUHybridCache<K, V>.OriginThreadAwareFuture ft = new LRUHybridCache.OriginThreadAwareFuture(this, key);
         synchronized (this.prunningLock) {
            if (this.cache.size() + 1 > this.maxCacheSize) {
               this.removeLRUItem();
            }

            f = this.cache.putIfAbsent(key, ft);
         }

         if (f == null) {
            f = ft;
            ft.run();
         }
      } else {
         long tid = f.threadId;
         if (tid != -1L && Thread.currentThread().getId() == f.threadId) {
            this.cycleHandler.handleCycle(key);
         }

         f.lastHit = System.nanoTime();
      }

      try {
         HybridCacheEntry result = f.get();
         if (result.dropMe()) {
            this.cache.remove(key);
         }

         return result;
      } catch (InterruptedException var6) {
         throw new RuntimeException(var6);
      } catch (ExecutionException var7) {
         this.cache.remove(key);
         if (var7.getCause() instanceof RuntimeException) {
            throw (RuntimeException)var7.getCause();
         } else {
            throw new RuntimeException(var7);
         }
      }
   }

   public void clear() {
      this.cache.clear();
   }

   public int size() {
      return this.cache.size();
   }

   public int getMaximumCacheSize() {
      return this.maxCacheSize;
   }

   public boolean containsKey(K key) {
      return this.cache.containsKey(key);
   }

   public void remove(K key) {
      this.cache.remove(key);
   }

   private void removeLRUItem() {
      Collection<LRUHybridCache<K, V>.OriginThreadAwareFuture> values = this.cache.values();
      this.cache.remove(Collections.min(values, COMPARATOR).key);
   }

   public void releaseMatching(CacheKeyFilter<K> filter) {
      if (filter != null) {
         for (K key : this.cache.keySet()) {
            if (filter.matches(key)) {
               this.cache.remove(key);
            }
         }
      }
   }

   private static class CacheEntryImplComparator<K, V> implements Comparator<LRUHybridCache<K, V>.OriginThreadAwareFuture> {
      public int compare(LRUHybridCache<K, V>.OriginThreadAwareFuture first, LRUHybridCache<K, V>.OriginThreadAwareFuture second) {
         long diff = first.lastHit - second.lastHit;
         return diff > 0L ? 1 : (diff == 0L ? 0 : -1);
      }
   }

   public interface CycleHandler<K> {
      void handleCycle(K var1);
   }

   private final class HybridCacheEntryImpl<V1> implements HybridCacheEntry<V1> {
      private final K key;
      private final V1 value;
      private final boolean dropMe;

      public HybridCacheEntryImpl(K key, V1 value, boolean dropMe) {
         this.key = key;
         this.value = value;
         this.dropMe = dropMe;
      }

      @Override
      public V1 getValue() {
         return this.value;
      }

      @Override
      public boolean dropMe() {
         return this.dropMe;
      }

      @Override
      public void removeFromCache() {
         LRUHybridCache.this.remove(this.key);
      }

      @Override
      public int hashCode() {
         int hash = 5;
         return 23 * hash + (this.key != null ? this.key.hashCode() : 0);
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            LRUHybridCache<K, V>.HybridCacheEntryImpl<V1> other = (LRUHybridCache<K, V>.HybridCacheEntryImpl<V1>)obj;
            return this.key == other.key || this.key != null && this.key.equals(other.key);
         }
      }
   }

   private class OriginThreadAwareFuture implements Future<HybridCacheEntry<V>> {
      private final K key;
      private final FutureTask<HybridCacheEntry<V>> future;
      private volatile long threadId;
      private volatile long lastHit;

      OriginThreadAwareFuture(LRUHybridCache<K, HybridCacheEntry<V>> cache, final K key) {
         this.key = key;
         this.threadId = Thread.currentThread().getId();
         Callable<HybridCacheEntry<V>> eval = new Callable<HybridCacheEntry<V>>() {
            public HybridCacheEntry<V> call() throws Exception {
               HybridCacheEntry var2;
               try {
                  HybridCacheEntry<V> result = LRUHybridCache.this.computable.compute(key);
                  var2 = result;
               } finally {
                  OriginThreadAwareFuture.this.threadId = -1L;
               }

               return var2;
            }
         };
         this.future = new FutureTask<>(eval);
         this.lastHit = System.nanoTime();
      }

      @Override
      public int hashCode() {
         return this.future.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            LRUHybridCache<K, V>.OriginThreadAwareFuture other = (LRUHybridCache.OriginThreadAwareFuture)obj;
            return this.future == other.future || this.future != null && this.future.equals(other.future);
         }
      }

      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
         return this.future.cancel(mayInterruptIfRunning);
      }

      @Override
      public boolean isCancelled() {
         return this.future.isCancelled();
      }

      @Override
      public boolean isDone() {
         return this.future.isDone();
      }

      public HybridCacheEntry<V> get() throws InterruptedException, ExecutionException {
         return this.future.get();
      }

      public HybridCacheEntry<V> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
         return this.future.get(timeout, unit);
      }

      public void run() {
         this.future.run();
      }
   }
}
