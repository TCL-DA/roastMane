package org.eclipse.jetty.util;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Spliterator;
import java.util.ServiceLoader.Provider;
import java.util.function.Consumer;

class ServiceLoaderSpliterator<T> implements Spliterator<Provider<T>> {
   private final Iterator<T> iterator;

   public ServiceLoaderSpliterator(ServiceLoader<T> serviceLoader) {
      this.iterator = serviceLoader.iterator();
   }

   @Override
   public boolean tryAdvance(Consumer<? super Provider<T>> action) {
      ServiceLoaderSpliterator.ServiceProvider<T> next;
      try {
         if (!this.iterator.hasNext()) {
            return false;
         }

         next = new ServiceLoaderSpliterator.ServiceProvider<>(this.iterator.next());
      } catch (Throwable var4) {
         next = new ServiceLoaderSpliterator.ServiceProvider<>(var4);
      }

      action.accept(next);
      return true;
   }

   @Override
   public Spliterator<Provider<T>> trySplit() {
      return null;
   }

   @Override
   public long estimateSize() {
      return Long.MAX_VALUE;
   }

   @Override
   public int characteristics() {
      return 16;
   }

   private static class ServiceProvider<T> implements Provider<T> {
      private final T service;
      private final Throwable error;

      public ServiceProvider(T service) {
         this.service = service;
         this.error = null;
      }

      public ServiceProvider(Throwable error) {
         this.service = null;
         this.error = error;
      }

      @Override
      public Class<? extends T> type() {
         return (Class<? extends T>)this.get().getClass();
      }

      @Override
      public T get() {
         if (this.service == null) {
            throw new ServiceConfigurationError("", this.error);
         } else {
            return this.service;
         }
      }
   }
}
