package org.glassfish.hk2.utilities.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Cache<K, V> implements Computable<K, V> {
   private final Cache.CycleHandler<K> cycleHandler;
   private static final Cache.CycleHandler<Object> EMPTY_HANDLER = new Cache.CycleHandler<Object>() {
      @Override
      public void handleCycle(Object key) {
      }
   };
   private final ConcurrentHashMap<K, Cache<K, V>.OriginThreadAwareFuture> cache = new ConcurrentHashMap<>();
   private final Computable<K, V> computable;

   public Cache(Computable<K, V> computable) {
      this(computable, (Cache.CycleHandler<K>)EMPTY_HANDLER);
   }

   public Cache(Computable<K, V> computable, Cache.CycleHandler<K> cycleHandler) {
      this.computable = computable;
      this.cycleHandler = cycleHandler;
   }

   @Override
   public V compute(K key) {
      Cache<K, V>.OriginThreadAwareFuture f = this.cache.get(key);
      if (f == null) {
         Cache<K, V>.OriginThreadAwareFuture ft = new Cache.OriginThreadAwareFuture(this, key);
         f = this.cache.putIfAbsent(key, ft);
         if (f == null) {
            f = ft;
            ft.run();
         }
      } else {
         long tid = f.threadId;
         if (tid != -1L && Thread.currentThread().getId() == f.threadId) {
            this.cycleHandler.handleCycle(key);
         }
      }

      try {
         return f.get();
      } catch (InterruptedException var5) {
         throw new RuntimeException(var5);
      } catch (ExecutionException var6) {
         this.cache.remove(key);
         Throwable cause = var6.getCause();
         if (cause == null) {
            throw new RuntimeException(var6);
         } else if (cause instanceof RuntimeException) {
            throw (RuntimeException)cause;
         } else {
            throw new RuntimeException(cause);
         }
      }
   }

   public void clear() {
      this.cache.clear();
   }

   public boolean containsKey(K key) {
      return this.cache.containsKey(key);
   }

   public void remove(K key) {
      this.cache.remove(key);
   }

   public int size() {
      return this.cache.size();
   }

   public interface CycleHandler<K> {
      void handleCycle(K var1);
   }

   private class OriginThreadAwareFuture implements Future<V> {
      private volatile long threadId = Thread.currentThread().getId();
      private final FutureTask<V> future;

      OriginThreadAwareFuture(Cache<K, V> cache, final K key) {
         Callable<V> eval = new Callable<V>() {
            @Override
            public V call() throws Exception {
               Object var1;
               try {
                  var1 = Cache.this.computable.compute(key);
               } finally {
                  OriginThreadAwareFuture.this.threadId = -1L;
               }

               return (V)var1;
            }
         };
         this.future = new FutureTask<>(eval);
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
            Cache<K, V>.OriginThreadAwareFuture other = (Cache.OriginThreadAwareFuture)obj;
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

      @Override
      public V get() throws InterruptedException, ExecutionException {
         return this.future.get();
      }

      @Override
      public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
         return this.future.get(timeout, unit);
      }

      public void run() {
         this.future.run();
      }
   }
}
