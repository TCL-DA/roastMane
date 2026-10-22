package com.sun.util.reentrant;

import java.lang.ref.Reference;

public abstract class ReentrantContextProviderTL<K extends ReentrantContext> extends ReentrantContextProvider<K> {
   private final ThreadLocal<Reference<K>> ctxTL = new ThreadLocal<>();
   private final ReentrantContextProviderCLQ<K> ctxProviderCLQ;

   public ReentrantContextProviderTL(int var1) {
      this(var1, 2);
   }

   public ReentrantContextProviderTL(int var1, int var2) {
      super(var1);
      this.ctxProviderCLQ = new ReentrantContextProviderCLQ<K>(var2) {
         @Override
         protected K newContext() {
            return (K)ReentrantContextProviderTL.this.newContext();
         }
      };
   }

   @Override
   public final K acquire() {
      ReentrantContext var1 = null;
      Reference var2 = this.ctxTL.get();
      if (var2 != null) {
         var1 = (ReentrantContext)var2.get();
      }

      if (var1 == null) {
         var1 = this.newContext();
         this.ctxTL.set(this.getOrCreateReference((K)var1));
      }

      if (var1.usage == 0) {
         var1.usage = 1;
      } else {
         var1 = this.ctxProviderCLQ.acquire();
      }

      return (K)var1;
   }

   @Override
   public final void release(K var1) {
      if (var1.usage == 1) {
         var1.usage = 0;
      } else {
         this.ctxProviderCLQ.release((K)var1);
      }
   }
}
