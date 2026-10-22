package com.sun.marlin;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public final class IntArrayCache implements MarlinConst {
   final boolean clean;
   private final int bucketCapacity;
   private WeakReference<IntArrayCache.Bucket[]> refBuckets = null;
   final ArrayCacheConst.CacheStats stats;

   IntArrayCache(boolean var1, int var2) {
      this.clean = var1;
      this.bucketCapacity = var2;
      this.stats = DO_STATS ? new ArrayCacheConst.CacheStats(getLogPrefix(var1) + "IntArrayCache") : null;
   }

   IntArrayCache.Bucket getCacheBucket(int var1) {
      int var2 = ArrayCacheConst.getBucket(var1);
      return this.getBuckets()[var2];
   }

   private IntArrayCache.Bucket[] getBuckets() {
      IntArrayCache.Bucket[] var1 = this.refBuckets != null ? this.refBuckets.get() : null;
      if (var1 == null) {
         var1 = new IntArrayCache.Bucket[8];

         for (int var2 = 0; var2 < 8; var2++) {
            var1[var2] = new IntArrayCache.Bucket(
               this.clean, ArrayCacheConst.ARRAY_SIZES[var2], this.bucketCapacity, DO_STATS ? this.stats.bucketStats[var2] : null
            );
         }

         this.refBuckets = new WeakReference<>(var1);
      }

      return var1;
   }

   IntArrayCache.Reference createRef(int var1) {
      return new IntArrayCache.Reference(this, var1);
   }

   static int[] createArray(int var0) {
      return new int[var0];
   }

   static void fill(int[] var0, int var1, int var2, int var3) {
      Arrays.fill(var0, var1, var2, var3);
      if (DO_CHECKS) {
         check(var0, var1, var2, var3);
      }
   }

   public static void check(int[] var0, int var1, int var2, int var3) {
      if (DO_CHECKS) {
         for (int var4 = 0; var4 < var0.length; var4++) {
            if (var0[var4] != var3) {
               MarlinUtils.logException(
                  "Invalid value at: " + var4 + " = " + var0[var4] + " from: " + var1 + " to: " + var2 + "\n" + Arrays.toString(var0), new Throwable()
               );
               Arrays.fill(var0, var3);
               return;
            }
         }
      }
   }

   static String getLogPrefix(boolean var0) {
      return var0 ? "Clean" : "Dirty";
   }

   static final class Bucket {
      private int tail = 0;
      private final int arraySize;
      private final boolean clean;
      private final int[][] arrays;
      private final ArrayCacheConst.BucketStats stats;

      Bucket(boolean var1, int var2, int var3, ArrayCacheConst.BucketStats var4) {
         this.arraySize = var2;
         this.clean = var1;
         this.stats = var4;
         this.arrays = new int[var3][];
      }

      int[] getArray() {
         if (MarlinConst.DO_STATS) {
            this.stats.getOp++;
         }

         if (this.tail != 0) {
            int[] var1 = this.arrays[--this.tail];
            this.arrays[this.tail] = null;
            return var1;
         } else {
            if (MarlinConst.DO_STATS) {
               this.stats.createOp++;
            }

            return IntArrayCache.createArray(this.arraySize);
         }
      }

      void putArray(int[] var1) {
         if (MarlinConst.DO_CHECKS && var1.length != this.arraySize) {
            MarlinUtils.logInfo(IntArrayCache.getLogPrefix(this.clean) + "IntArrayCache: bad length = " + var1.length);
         } else {
            if (MarlinConst.DO_STATS) {
               this.stats.returnOp++;
            }

            if (this.arrays.length > this.tail) {
               this.arrays[this.tail++] = var1;
               if (MarlinConst.DO_STATS) {
                  this.stats.updateMaxSize(this.tail);
               }
            } else if (MarlinConst.DO_CHECKS) {
               MarlinUtils.logInfo(IntArrayCache.getLogPrefix(this.clean) + "IntArrayCache: array capacity exceeded !");
            }
         }
      }
   }

   static final class Reference {
      final int[] initial;
      private final boolean clean;
      private final IntArrayCache cache;

      Reference(IntArrayCache var1, int var2) {
         this.cache = var1;
         this.clean = var1.clean;
         this.initial = IntArrayCache.createArray(var2);
         if (MarlinConst.DO_STATS) {
            var1.stats.totalInitial += (long)var2;
         }
      }

      int[] getArray(int var1) {
         if (var1 <= ArrayCacheConst.MAX_ARRAY_SIZE) {
            return this.cache.getCacheBucket(var1).getArray();
         } else {
            if (MarlinConst.DO_STATS) {
               this.cache.stats.oversize++;
            }

            if (MarlinConst.DO_LOG_OVERSIZE) {
               MarlinUtils.logInfo(IntArrayCache.getLogPrefix(this.clean) + "IntArrayCache: getArray[oversize]: length=\t" + var1);
            }

            return IntArrayCache.createArray(var1);
         }
      }

      int[] widenArray(int[] var1, int var2, int var3) {
         int var4 = var1.length;
         if (MarlinConst.DO_CHECKS && var4 >= var3) {
            return var1;
         } else {
            if (MarlinConst.DO_STATS) {
               this.cache.stats.resize++;
            }

            int[] var5 = this.getArray(ArrayCacheConst.getNewSize(var2, var3));
            System.arraycopy(var1, 0, var5, 0, var2);
            this.putArray(var1, 0, var2);
            if (MarlinConst.DO_LOG_WIDEN_ARRAY) {
               MarlinUtils.logInfo(
                  IntArrayCache.getLogPrefix(this.clean)
                     + "IntArrayCache: widenArray["
                     + var5.length
                     + "]: usedSize=\t"
                     + var2
                     + "\tlength=\t"
                     + var4
                     + "\tneeded length=\t"
                     + var3
               );
            }

            return var5;
         }
      }

      int[] putArray(int[] var1) {
         return this.putArray(var1, 0, var1.length);
      }

      int[] putArray(int[] var1, int var2, int var3) {
         if (var1.length <= ArrayCacheConst.MAX_ARRAY_SIZE) {
            if (this.clean && var3 != 0) {
               IntArrayCache.fill(var1, var2, var3, 0);
            }

            if (var1 != this.initial) {
               this.cache.getCacheBucket(var1.length).putArray(var1);
            }
         }

         return this.initial;
      }
   }
}
