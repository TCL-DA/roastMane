package org.eclipse.jetty.util;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.slf4j.LoggerFactory;

public interface Promise<C> {
   Promise<Object> NOOP = new Promise<Object>() {
   };

   static <T> Promise<T> noop() {
      return (Promise<T>)NOOP;
   }

   default void succeeded(C result) {
   }

   default void failed(Throwable x) {
   }

   default void completeWith(CompletableFuture<C> completable) {
      completable.whenComplete((o, x) -> {
         if (x == null) {
            this.succeeded((C)o);
         } else {
            this.failed(x);
         }
      });
   }

   static <T> Promise<T> from(final Consumer<T> success, final Consumer<Throwable> failure) {
      return new Promise<T>() {
         @Override
         public void succeeded(T result) {
            success.accept(result);
         }

         @Override
         public void failed(Throwable x) {
            failure.accept(x);
         }
      };
   }

   static <T> Promise<T> from(final CompletableFuture<? super T> completable) {
      return completable instanceof Promise ? (Promise)completable : new Promise<T>() {
         @Override
         public void succeeded(T result) {
            completable.complete(result);
         }

         @Override
         public void failed(Throwable x) {
            completable.completeExceptionally(x);
         }
      };
   }

   public static class Adapter<U> implements Promise<U> {
      @Override
      public void failed(Throwable x) {
         LoggerFactory.getLogger(this.getClass()).warn("Failed", x);
      }
   }

   public static class Completable<S> extends CompletableFuture<S> implements Promise<S> {
      public static <R> Promise.Completable<R> with(Consumer<Promise<R>> consumer) {
         Promise.Completable<R> completable = new Promise.Completable();
         consumer.accept(completable);
         return completable;
      }

      @Override
      public void succeeded(S result) {
         this.complete(result);
      }

      @Override
      public void failed(Throwable x) {
         this.completeExceptionally(x);
      }
   }

   public static class Wrapper<W> implements Promise<W> {
      private final Promise<W> promise;

      public Wrapper(Promise<W> promise) {
         this.promise = Objects.requireNonNull(promise);
      }

      @Override
      public void succeeded(W result) {
         this.promise.succeeded(result);
      }

      @Override
      public void failed(Throwable x) {
         this.promise.failed(x);
      }

      public Promise<W> getPromise() {
         return this.promise;
      }

      public Promise<W> unwrap() {
         Promise<W> result = this.promise;

         while (result instanceof Promise.Wrapper) {
            result = ((Promise.Wrapper)result).unwrap();
         }

         return result;
      }
   }
}
