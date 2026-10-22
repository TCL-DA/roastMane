package org.glassfish.jersey.internal.guava;

public abstract class CacheLoader<K, V> {
   protected CacheLoader() {
   }

   public abstract V load(K var1) throws Exception;

   public ListenableFuture<V> reload(K key, V oldValue) throws Exception {
      Preconditions.checkNotNull(key);
      Preconditions.checkNotNull(oldValue);
      return Futures.immediateFuture(this.load(key));
   }

   public static final class InvalidCacheLoadException extends RuntimeException {
      public InvalidCacheLoadException(String message) {
         super(message);
      }
   }
}
