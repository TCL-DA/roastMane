package com.sun.marlin;

final class MergeSort {
   public static final int INSERTION_SORT_THRESHOLD = 14;

   static void mergeSortNoCopy(int[] var0, int[] var1, int[] var2, int[] var3, int var4, int var5) {
      if (var4 <= var0.length && var4 <= var1.length && var4 <= var2.length && var4 <= var3.length) {
         mergeSort(var0, var1, var0, var2, var1, var3, var5, var4);
         if (var5 != 0 && var2[var5 - 1] > var2[var5]) {
            int var6 = 0;
            int var7 = 0;

            for (int var8 = var5; var6 < var4; var6++) {
               if (var8 < var4 && (var7 >= var5 || var2[var7] > var2[var8])) {
                  var0[var6] = var2[var8];
                  var1[var6] = var3[var8];
                  var8++;
               } else {
                  var0[var6] = var2[var7];
                  var1[var6] = var3[var7];
                  var7++;
               }
            }
         } else {
            System.arraycopy(var2, 0, var0, 0, var4);
            System.arraycopy(var3, 0, var1, 0, var4);
         }
      } else {
         throw new ArrayIndexOutOfBoundsException("bad arguments: toIndex=" + var4);
      }
   }

   private static void mergeSort(int[] var0, int[] var1, int[] var2, int[] var3, int[] var4, int[] var5, int var6, int var7) {
      int var8 = var7 - var6;
      if (var8 <= 14) {
         var3[var6] = var0[var6];
         var5[var6] = var1[var6];
         int var13 = var6 + 1;

         for (int var15 = var6; var13 < var7; var15 = var13++) {
            int var17 = var0[var13];
            int var19 = var1[var13];

            while (var3[var15] > var17) {
               var3[var15 + 1] = var3[var15];
               var5[var15 + 1] = var5[var15];
               if (var15-- == var6) {
                  break;
               }
            }

            var3[var15 + 1] = var17;
            var5[var15 + 1] = var19;
         }
      } else {
         int var9 = var6 + var7 >> 1;
         mergeSort(var0, var1, var3, var2, var5, var4, var6, var9);
         mergeSort(var0, var1, var3, var2, var5, var4, var9, var7);
         if (var2[var7 - 1] <= var2[var6]) {
            int var14 = var9 - var6;
            int var16 = var7 - var9;
            int var18 = var14 != var16 ? 1 : 0;
            System.arraycopy(var2, var6, var3, var9 + var18, var14);
            System.arraycopy(var2, var9, var3, var6, var16);
            System.arraycopy(var4, var6, var5, var9 + var18, var14);
            System.arraycopy(var4, var9, var5, var6, var16);
         } else if (var2[var9 - 1] <= var2[var9]) {
            System.arraycopy(var2, var6, var3, var6, var8);
            System.arraycopy(var4, var6, var5, var6, var8);
         } else {
            int var10 = var6;
            int var11 = var6;

            for (int var12 = var9; var10 < var7; var10++) {
               if (var12 < var7 && (var11 >= var9 || var2[var11] > var2[var12])) {
                  var3[var10] = var2[var12];
                  var5[var10] = var4[var12];
                  var12++;
               } else {
                  var3[var10] = var2[var11];
                  var5[var10] = var4[var11];
                  var11++;
               }
            }
         }
      }
   }

   private MergeSort() {
   }
}
