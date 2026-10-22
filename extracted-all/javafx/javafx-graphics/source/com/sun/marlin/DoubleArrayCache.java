package com.sun.marlin;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public final class DoubleArrayCache implements MarlinConst {
   final boolean clean;
   private final int bucketCapacity;
   private WeakReference<DoubleArrayCache.Bucket[]> refBuckets = null;
   final ArrayCacheConst.CacheStats stats;

   DoubleArrayCache(boolean var1, int var2) {
      this.clean = var1;
      this.bucketCapacity = var2;
      this.stats = DO_STATS ? new ArrayCacheConst.CacheStats(getLogPrefix(var1) + "DoubleArrayCache") : null;
   }

   DoubleArrayCache.Bucket getCacheBucket(int var1) {
      int var2 = ArrayCacheConst.getBucket(var1);
      return this.getBuckets()[var2];
   }

   private DoubleArrayCache.Bucket[] getBuckets() {
      DoubleArrayCache.Bucket[] var1 = this.refBuckets != null ? this.refBuckets.get() : null;
      if (var1 == null) {
         var1 = new DoubleArrayCache.Bucket[8];

         for (int var2 = 0; var2 < 8; var2++) {
            var1[var2] = new DoubleArrayCache.Bucket(
               this.clean, ArrayCacheConst.ARRAY_SIZES[var2], this.bucketCapacity, DO_STATS ? this.stats.bucketStats[var2] : null
            );
         }

         this.refBuckets = new WeakReference<>(var1);
      }

      return var1;
   }

   DoubleArrayCache.Reference createRef(int var1) {
      return new DoubleArrayCache.Reference(this, var1);
   }

   static double[] createArray(int var0) {
      return new double[var0];
   }

   static void fill(double[] var0, int var1, int var2, double var3) {
      Arrays.fill(var0, var1, var2, var3);
      if (DO_CHECKS) {
         check(var0, var1, var2, var3);
      }
   }

   public static void check(double[] var0, int var1, int var2, double var3) {
      if (DO_CHECKS) {
         for (int var5 = 0; var5 < var0.length; var5++) {
            if (var0[var5] != var3) {
               MarlinUtils.logException(
                  "Invalid value at: " + var5 + " = " + var0[var5] + " from: " + var1 + " to: " + var2 + "\n" + Arrays.toString(var0), new Throwable()
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
      private final double[][] arrays;
      private final ArrayCacheConst.BucketStats stats;

      Bucket(boolean var1, int var2, int var3, ArrayCacheConst.BucketStats var4) {
         this.arraySize = var2;
         this.clean = var1;
         this.stats = var4;
         this.arrays = new double[var3][];
      }

      double[] getArray() {
         if (MarlinConst.DO_STATS) {
            this.stats.getOp++;
         }

         if (this.tail != 0) {
            double[] var1 = this.arrays[--this.tail];
            this.arrays[this.tail] = null;
            return var1;
         } else {
            if (MarlinConst.DO_STATS) {
               this.stats.createOp++;
            }

            return DoubleArrayCache.createArray(this.arraySize);
         }
      }

      void putArray(double[] var1) {
         if (MarlinConst.DO_CHECKS && var1.length != this.arraySize) {
            MarlinUtils.logInfo(DoubleArrayCache.getLogPrefix(this.clean) + "DoubleArrayCache: bad length = " + var1.length);
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
               MarlinUtils.logInfo(DoubleArrayCache.getLogPrefix(this.clean) + "DoubleArrayCache: array capacity exceeded !");
            }
         }
      }
   }

   static final class Reference {
      final double[] initial;
      private final boolean clean;
      private final DoubleArrayCache cache;

      Reference(DoubleArrayCache var1, int var2) {
         this.cache = var1;
         this.clean = var1.clean;
         this.initial = DoubleArrayCache.createArray(var2);
         if (MarlinConst.DO_STATS) {
            var1.stats.totalInitial += (long)var2;
         }
      }

      double[] getArray(int var1) {
         if (var1 <= ArrayCacheConst.MAX_ARRAY_SIZE) {
            return this.cache.getCacheBucket(var1).getArray();
         } else {
            if (MarlinConst.DO_STATS) {
               this.cache.stats.oversize++;
            }

            if (MarlinConst.DO_LOG_OVERSIZE) {
               MarlinUtils.logInfo(DoubleArrayCache.getLogPrefix(this.clean) + "DoubleArrayCache: getArray[oversize]: length=\t" + var1);
            }

            return DoubleArrayCache.createArray(var1);
         }
      }

      double[] widenArray(double[] var1, int var2, int var3) {
         int var4 = var1.length;
         if (MarlinConst.DO_CHECKS && var4 >= var3) {
            return var1;
         } else {
            if (MarlinConst.DO_STATS) {
               this.cache.stats.resize++;
            }

            double[] var5 = this.getArray(ArrayCacheConst.getNewSize(var2, var3));
            System.arraycopy(var1, 0, var5, 0, var2);
            this.putArray(var1, 0, var2);
            if (MarlinConst.DO_LOG_WIDEN_ARRAY) {
               MarlinUtils.logInfo(
                  DoubleArrayCache.getLogPrefix(this.clean)
                     + "DoubleArrayCache: widenArray["
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

      double[] putArray(double[] var1) {
         return this.putArray(var1, 0, var1.length);
      }

      double[] putArray(double[] var1, int var2, int var3) {
         if (var1.length <= ArrayCacheConst.MAX_ARRAY_SIZE) {
            if (this.clean && var3 != 0) {
               DoubleArrayCache.fill(var1, var2, var3, 0.0);
            }

            if (var1 != this.initial) {
               this.cache.getCacheBucket(var1.length).putArray(var1);
            }
         }

         return this.initial;
      }
   }
}
