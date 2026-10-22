package com.digitalpetri.enip.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class FutureUtils {
   public static <T> FutureUtils.CompletionBuilder<T> complete(CompletableFuture<T> future) {
      return new FutureUtils.CompletionBuilder<>(future);
   }

   private static final class AsyncCompletionBuilder<T> extends FutureUtils.CompletionBuilder<T> {
      private final Executor executor;

      AsyncCompletionBuilder(CompletableFuture<T> toComplete, Executor executor) {
         super(toComplete);
         this.executor = executor;
      }

      @Override
      public CompletableFuture<T> with(CompletableFuture<T> future) {
         future.whenCompleteAsync((v, ex) -> {
            if (ex != null) {
               this.toComplete.completeExceptionally(ex);
            } else {
               this.toComplete.complete((T)v);
            }
         }, this.executor);
         return this.toComplete;
      }
   }

   public static class CompletionBuilder<T> {
      final CompletableFuture<T> toComplete;

      private CompletionBuilder(CompletableFuture<T> toComplete) {
         this.toComplete = toComplete;
      }

      public FutureUtils.CompletionBuilder<T> async(Executor executor) {
         return new FutureUtils.AsyncCompletionBuilder<>(this.toComplete, executor);
      }

      public CompletableFuture<T> with(CompletableFuture<T> future) {
         future.whenComplete((v, ex) -> {
            if (ex != null) {
               this.toComplete.completeExceptionally(ex);
            } else {
               this.toComplete.complete((T)v);
            }
         });
         return this.toComplete;
      }
   }
}
