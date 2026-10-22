package com.sun.marlin;

import java.util.Arrays;

public final class ArrayCacheConst implements MarlinConst {
   static final int BUCKETS = 8;
   static final int MIN_ARRAY_SIZE = 4096;
   static final int MAX_ARRAY_SIZE;
   static final int THRESHOLD_SMALL_ARRAY_SIZE = 4194304;
   static final int THRESHOLD_ARRAY_SIZE;
   static final long THRESHOLD_HUGE_ARRAY_SIZE;
   static final int[] ARRAY_SIZES = new int[8];

   private ArrayCacheConst() {
   }

   static int getBucket(int var0) {
      for (int var1 = 0; var1 < ARRAY_SIZES.length; var1++) {
         if (var0 <= ARRAY_SIZES[var1]) {
            return var1;
         }
      }

      return -1;
   }

   public static int getNewSize(int var0, int var1) {
      if (var1 < 0) {
         throw new ArrayIndexOutOfBoundsException("array exceeds maximum capacity !");
      } else {
         assert var0 >= 0;

         int var3;
         if (var0 > THRESHOLD_ARRAY_SIZE) {
            var3 = var0 + (var0 >> 1);
         } else {
            var3 = var0 << 1;
         }

         if (var3 < var1) {
            var3 = (var1 >> 12) + 1 << 12;
         }

         if (var3 < 0) {
            var3 = Integer.MAX_VALUE;
         }

         return var3;
      }
   }

   public static long getNewLargeSize(long var0, long var2) {
      if (var2 >> 31 != 0L) {
         throw new ArrayIndexOutOfBoundsException("array exceeds maximum capacity !");
      } else {
         assert var0 >= 0L;

         long var4;
         if (var0 > THRESHOLD_HUGE_ARRAY_SIZE) {
            var4 = var0 + (var0 >> 2);
         } else if (var0 > (long)THRESHOLD_ARRAY_SIZE) {
            var4 = var0 + (var0 >> 1);
         } else if (var0 > 4194304L) {
            var4 = var0 << 1;
         } else {
            var4 = var0 << 2;
         }

         if (var4 < var2) {
            var4 = (var2 >> 12) + 1L << 12;
         }

         if (var4 > 2147483647L) {
            var4 = 2147483647L;
         }

         return var4;
      }
   }

   static {
      int var0 = 4096;
      byte var1 = 2;

      for (int var2 = 0; var2 < 8; var0 <<= var1) {
         ARRAY_SIZES[var2] = var0;
         if (DO_TRACE) {
            MarlinUtils.logInfo("arraySize[" + var2 + "]: " + var0);
         }

         if (var0 >= 4194304) {
            var1 = 1;
         }

         var2++;
      }

      MAX_ARRAY_SIZE = var0 >> var1;
      if (MAX_ARRAY_SIZE <= 0) {
         throw new IllegalStateException("Invalid max array size !");
      } else {
         THRESHOLD_ARRAY_SIZE = 16777216;
         THRESHOLD_HUGE_ARRAY_SIZE = 50331648L;
         if (DO_STATS) {
            MarlinUtils.logInfo("ArrayCache.BUCKETS        = 8");
            MarlinUtils.logInfo("ArrayCache.MIN_ARRAY_SIZE = 4096");
            MarlinUtils.logInfo("ArrayCache.MAX_ARRAY_SIZE = " + MAX_ARRAY_SIZE);
            MarlinUtils.logInfo("ArrayCache.ARRAY_SIZES = " + Arrays.toString(ARRAY_SIZES));
            MarlinUtils.logInfo("ArrayCache.THRESHOLD_ARRAY_SIZE = " + THRESHOLD_ARRAY_SIZE);
            MarlinUtils.logInfo("ArrayCache.THRESHOLD_HUGE_ARRAY_SIZE = " + THRESHOLD_HUGE_ARRAY_SIZE);
         }
      }
   }

   static final class BucketStats {
      int getOp = 0;
      int createOp = 0;
      int returnOp = 0;
      int maxSize = 0;

      void reset() {
         this.getOp = 0;
         this.createOp = 0;
         this.returnOp = 0;
         this.maxSize = 0;
      }

      void updateMaxSize(int var1) {
         if (var1 > this.maxSize) {
            this.maxSize = var1;
         }
      }
   }

   static final class CacheStats {
      final String name;
      final ArrayCacheConst.BucketStats[] bucketStats;
      int resize = 0;
      int oversize = 0;
      long totalInitial = 0L;

      CacheStats(String var1) {
         this.name = var1;
         this.bucketStats = new ArrayCacheConst.BucketStats[8];

         for (int var2 = 0; var2 < 8; var2++) {
            this.bucketStats[var2] = new ArrayCacheConst.BucketStats();
         }
      }

      void reset() {
         this.resize = 0;
         this.oversize = 0;

         for (int var1 = 0; var1 < 8; var1++) {
            this.bucketStats[var1].reset();
         }
      }

      long dumpStats() {
         long var1 = 0L;
         if (MarlinConst.DO_STATS) {
            for (int var3 = 0; var3 < 8; var3++) {
               ArrayCacheConst.BucketStats var4 = this.bucketStats[var3];
               if (var4.maxSize != 0) {
                  var1 += (long)(this.getByteFactor() * var4.maxSize * ArrayCacheConst.ARRAY_SIZES[var3]);
               }
            }

            if (this.totalInitial != 0L || var1 != 0L || this.resize != 0 || this.oversize != 0) {
               MarlinUtils.logInfo(
                  this.name
                     + ": resize: "
                     + this.resize
                     + " - oversize: "
                     + this.oversize
                     + " - initial: "
                     + this.getTotalInitialBytes()
                     + " bytes ("
                     + this.totalInitial
                     + " elements) - cache: "
                     + var1
                     + " bytes"
               );
            }

            if (var1 != 0L) {
               MarlinUtils.logInfo(this.name + ": usage stats:");

               for (int var5 = 0; var5 < 8; var5++) {
                  ArrayCacheConst.BucketStats var6 = this.bucketStats[var5];
                  if (var6.getOp != 0) {
                     MarlinUtils.logInfo(
                        "  Bucket["
                           + ArrayCacheConst.ARRAY_SIZES[var5]
                           + "]: get: "
                           + var6.getOp
                           + " - put: "
                           + var6.returnOp
                           + " - create: "
                           + var6.createOp
                           + " :: max size: "
                           + var6.maxSize
                     );
                  }
               }
            }
         }

         return var1;
      }

      private int getByteFactor() {
         byte var1 = 1;
         if (this.name.contains("Int") || this.name.contains("Float")) {
            var1 = 4;
         } else if (this.name.contains("Double")) {
            var1 = 8;
         }

         return var1;
      }

      long getTotalInitialBytes() {
         return (long)this.getByteFactor() * this.totalInitial;
      }
   }
}
