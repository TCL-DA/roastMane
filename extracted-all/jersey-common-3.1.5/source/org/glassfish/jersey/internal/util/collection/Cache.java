package org.glassfish.jersey.internal.util.collection;

import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class Cache<K, V> implements Function<K, V> {
   private static final Cache.CycleHandler<Object> EMPTY_HANDLER = key -> {
   };
   private final Cache.CycleHandler<K> cycleHandler;
   private final ConcurrentHashMap<K, Cache<K, V>.OriginThreadAwareFuture> cache = new ConcurrentHashMap<>();
   private final Function<K, V> computable;

   public Cache(Function<K, V> computable) {
      this(computable, (Cache.CycleHandler<K>)EMPTY_HANDLER);
   }

   public Cache(Function<K, V> computable, Cache.CycleHandler<K> cycleHandler) {
      this.computable = computable;
      this.cycleHandler = cycleHandler;
   }

   @Override
   public V apply(K key) {
      Cache<K, V>.OriginThreadAwareFuture f = this.cache.get(key);
      if (f == null) {
         Cache<K, V>.OriginThreadAwareFuture ft = new Cache.OriginThreadAwareFuture(key);
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

   public Enumeration<K> keys() {
      return this.cache.keys();
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
      private final FutureTask<V> future;
      private volatile long threadId = Thread.currentThread().getId();

      OriginThreadAwareFuture(K key) {
         Callable<V> eval = () -> {
            Object var2x;
            try {
               var2x = Cache.this.computable.apply(key);
            } finally {
               this.threadId = -1L;
            }

            return (V)var2x;
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
