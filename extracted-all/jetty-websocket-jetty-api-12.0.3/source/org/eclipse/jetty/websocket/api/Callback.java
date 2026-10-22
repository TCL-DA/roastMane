package org.eclipse.jetty.websocket.api;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Callback {
   Callback NOOP = new Callback() {
   };

   static Callback from(final Runnable success, final Consumer<Throwable> failure) {
      return new Callback() {
         @Override
         public void succeed() {
            success.run();
         }

         @Override
         public void fail(Throwable x) {
            failure.accept(x);
         }
      };
   }

   default void succeed() {
   }

   default void fail(Throwable x) {
   }

   default BiConsumer<? super Void, ? super Throwable> asBiConsumer() {
      return (r, x) -> {
         if (x == null) {
            this.succeed();
         } else {
            this.fail(x);
         }
      };
   }

   public static class Completable extends CompletableFuture<Void> implements Callback {
      public static Callback.Completable with(Consumer<Callback.Completable> consumer) {
         Callback.Completable completable = new Callback.Completable();
         consumer.accept(completable);
         return completable;
      }

      @Override
      public void succeed() {
         this.complete(null);
      }

      @Override
      public void fail(Throwable x) {
         this.completeExceptionally(x);
      }

      public Callback.Completable compose(Consumer<Callback.Completable> consumer) {
         Callback.Completable completable = new Callback.Completable();
         this.whenComplete((r, x) -> {
            if (x == null) {
               consumer.accept(completable);
            } else {
               completable.fail(x);
            }
         });
         return completable;
      }
   }
}
