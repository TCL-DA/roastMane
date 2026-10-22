package com.sun.util.reentrant;

import java.lang.ref.Reference;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ReentrantContextProviderCLQ<K extends ReentrantContext> extends ReentrantContextProvider<K> {
   private final ConcurrentLinkedQueue<Reference<K>> ctxQueue = new ConcurrentLinkedQueue<>();

   public ReentrantContextProviderCLQ(int var1) {
      super(var1);
   }

   @Override
   public final K acquire() {
      ReentrantContext var1 = null;
      Reference var2 = null;

      while (var1 == null && (var2 = this.ctxQueue.poll()) != null) {
         var1 = (ReentrantContext)var2.get();
      }

      if (var1 == null) {
         var1 = this.newContext();
         var1.usage = 2;
      }

      return (K)var1;
   }

   @Override
   public final void release(K var1) {
      if (var1.usage == 2) {
         this.ctxQueue.offer(this.getOrCreateReference((K)var1));
      }
   }
}
