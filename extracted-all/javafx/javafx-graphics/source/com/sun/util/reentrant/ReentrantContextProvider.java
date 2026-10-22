package com.sun.util.reentrant;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public abstract class ReentrantContextProvider<K extends ReentrantContext> {
   static final byte USAGE_TL_INACTIVE = 0;
   static final byte USAGE_TL_IN_USE = 1;
   static final byte USAGE_CLQ = 2;
   public static final int REF_HARD = 0;
   public static final int REF_SOFT = 1;
   public static final int REF_WEAK = 2;
   private final int refType;

   protected ReentrantContextProvider(int var1) {
      this.refType = var1;
   }

   protected abstract K newContext();

   public abstract K acquire();

   public abstract void release(K var1);

   protected final Reference<K> getOrCreateReference(K var1) {
      if (var1.reference == null) {
         switch (this.refType) {
            case 0:
               var1.reference = new ReentrantContextProvider.HardReference<>(var1);
               break;
            case 1:
               var1.reference = new SoftReference<>(var1);
               break;
            case 2:
            default:
               var1.reference = new WeakReference<>(var1);
         }
      }

      return (Reference<K>)var1.reference;
   }

   static final class HardReference<V> extends WeakReference<V> {
      private final V strongRef;

      HardReference(V var1) {
         super(null);
         this.strongRef = (V)var1;
      }

      @Override
      public V get() {
         return this.strongRef;
      }
   }
}
