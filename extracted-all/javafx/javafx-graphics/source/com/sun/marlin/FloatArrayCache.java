package com.sun.marlin;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public final class FloatArrayCache implements MarlinConst {
   final boolean clean;
   private final int bucketCapacity;
   private WeakReference<FloatArrayCache.Bucket[]> refBuckets = null;
   final ArrayCacheConst.CacheStats stats;

   FloatArrayCache(boolean var1, int var2) {
      this.clean = var1;
      this.bucketCapacity = var2;
      this.stats = DO_STATS ? new ArrayCacheConst.CacheStats(getLogPrefix(var1) + "FloatArrayCache") : null;
   }

   FloatArrayCache.Bucket getCacheBucket(int var1) {
      int var2 = ArrayCacheConst.getBucket(var1);
      return this.getBuckets()[var2];
   }

   private FloatArrayCache.Bucket[] getBuckets() {
      FloatArrayCache.Bucket[] var1 = this.refBuckets != null ? this.refBuckets.get() : null;
      if (var1 == null) {
         var1 = new FloatArrayCache.Bucket[8];

         for (int var2 = 0; var2 < 8; var2++) {
            var1[var2] = new FloatArrayCache.Bucket(
               this.clean, ArrayCacheConst.ARRAY_SIZES[var2], this.bucketCapacity, DO_STATS ? this.stats.bucketStats[var2] : null
            );
         }

         this.refBuckets = new WeakReference<>(var1);
      }

      return var1;
   }

   FloatArrayCache.Reference createRef(int var1) {
      return new FloatArrayCache.Reference(this, var1);
   }

   static float[] createArray(int var0) {
      return new float[var0];
   }

   static void fill(float[] var0, int var1, int var2, float var3) {
      Arrays.fill(var0, var1, var2, var3);
      if (DO_CHECKS) {
         check(var0, var1, var2, var3);
      }
   }

   public static void check(float[] var0, int var1, int var2, float var3) {
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
      private final float[][] arrays;
      private final ArrayCacheConst.BucketStats stats;

      Bucket(boolean var1, int var2, int var3, ArrayCacheConst.BucketStats var4) {
         this.arraySize = var2;
         this.clean = var1;
         this.stats = var4;
         this.arrays = new float[var3][];
      }

      float[] getArray() {
         if (MarlinConst.DO_STATS) {
            this.stats.getOp++;
         }

         if (this.tail != 0) {
            float[] var1 = this.arrays[--this.tail];
            this.arrays[this.tail] = null;
            return var1;
         } else {
            if (MarlinConst.DO_STATS) {
               this.stats.createOp++;
            }

            return FloatArrayCache.createArray(this.arraySize);
         }
      }

      void putArray(float[] var1) {
         if (MarlinConst.DO_CHECKS && var1.length != this.arraySize) {
            MarlinUtils.logInfo(FloatArrayCache.getLogPrefix(this.clean) + "FloatArrayCache: bad length = " + var1.length);
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
               MarlinUtils.logInfo(FloatArrayCache.getLogPrefix(this.clean) + "FloatArrayCache: array capacity exceeded !");
            }
         }
      }
   }

   static final class Reference {
      final float[] initial;
      private final boolean clean;
      private final FloatArrayCache cache;

      Reference(FloatArrayCache var1, int var2) {
         this.cache = var1;
         this.clean = var1.clean;
         this.initial = FloatArrayCache.createArray(var2);
         if (MarlinConst.DO_STATS) {
            var1.stats.totalInitial += (long)var2;
         }
      }

      float[] getArray(int var1) {
         if (var1 <= ArrayCacheConst.MAX_ARRAY_SIZE) {
            return this.cache.getCacheBucket(var1).getArray();
         } else {
            if (MarlinConst.DO_STATS) {
               this.cache.stats.oversize++;
            }

            if (MarlinConst.DO_LOG_OVERSIZE) {
               MarlinUtils.logInfo(FloatArrayCache.getLogPrefix(this.clean) + "FloatArrayCache: getArray[oversize]: length=\t" + var1);
            }

            return FloatArrayCache.createArray(var1);
         }
      }

      float[] widenArray(float[] var1, int var2, int var3) {
         int var4 = var1.length;
         if (MarlinConst.DO_CHECKS && var4 >= var3) {
            return var1;
         } else {
            if (MarlinConst.DO_STATS) {
               this.cache.stats.resize++;
            }

            float[] var5 = this.getArray(ArrayCacheConst.getNewSize(var2, var3));
            System.arraycopy(var1, 0, var5, 0, var2);
            this.putArray(var1, 0, var2);
            if (MarlinConst.DO_LOG_WIDEN_ARRAY) {
               MarlinUtils.logInfo(
                  FloatArrayCache.getLogPrefix(this.clean)
                     + "FloatArrayCache: widenArray["
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

      float[] putArray(float[] var1) {
         return this.putArray(var1, 0, var1.length);
      }

      float[] putArray(float[] var1, int var2, int var3) {
         if (var1.length <= ArrayCacheConst.MAX_ARRAY_SIZE) {
            if (this.clean && var3 != 0) {
               FloatArrayCache.fill(var1, var2, var3, 0.0F);
            }

            if (var1 != this.initial) {
               this.cache.getCacheBucket(var1.length).putArray(var1);
            }
         }

         return this.initial;
      }
   }
}
