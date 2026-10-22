package com.sun.javafx.collections;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

public class SortHelper {
   private int[] permutation;
   private int[] reversePermutation;
   private static final int INSERTIONSORT_THRESHOLD = 7;

   public <T extends Comparable<? super T>> int[] sort(List<T> var1) {
      Comparable[] var2 = (Comparable[])Array.newInstance(Comparable.class, var1.size());

      try {
         var2 = var1.toArray(var2);
      } catch (ArrayStoreException var6) {
         throw new ClassCastException();
      }

      int[] var3 = this.sort((T[])var2);
      ListIterator var4 = var1.listIterator();

      for (int var5 = 0; var5 < var2.length; var5++) {
         var4.next();
         var4.set(var2[var5]);
      }

      return var3;
   }

   public <T> int[] sort(List<T> var1, Comparator<? super T> var2) {
      Object[] var3 = var1.toArray();
      int[] var4 = this.sort(var3, var2);
      ListIterator var5 = var1.listIterator();

      for (int var6 = 0; var6 < var3.length; var6++) {
         var5.next();
         var5.set(var3[var6]);
      }

      return var4;
   }

   public <T extends Comparable<? super T>> int[] sort(T[] var1) {
      return this.sort(var1, null);
   }

   public <T> int[] sort(T[] var1, Comparator<? super T> var2) {
      Object[] var3 = (Object[])var1.clone();
      int[] var4 = this.initPermutation(var1.length);
      if (var2 == null) {
         this.mergeSort(var3, var1, 0, var1.length, 0);
      } else {
         this.mergeSort(var3, var1, 0, var1.length, 0, var2);
      }

      this.reversePermutation = null;
      this.permutation = null;
      return var4;
   }

   public <T> int[] sort(T[] var1, int var2, int var3, Comparator<? super T> var4) {
      rangeCheck(var1.length, var2, var3);
      Object[] var5 = copyOfRange(var1, var2, var3);
      int[] var6 = this.initPermutation(var1.length);
      if (var4 == null) {
         this.mergeSort(var5, var1, var2, var3, -var2);
      } else {
         this.mergeSort(var5, var1, var2, var3, -var2, var4);
      }

      this.reversePermutation = null;
      this.permutation = null;
      return Arrays.copyOfRange(var6, var2, var3);
   }

   public int[] sort(int[] var1, int var2, int var3) {
      rangeCheck(var1.length, var2, var3);
      int[] var4 = copyOfRange(var1, var2, var3);
      int[] var5 = this.initPermutation(var1.length);
      this.mergeSort(var4, var1, var2, var3, -var2);
      this.reversePermutation = null;
      this.permutation = null;
      return Arrays.copyOfRange(var5, var2, var3);
   }

   private static void rangeCheck(int var0, int var1, int var2) {
      if (var1 > var2) {
         throw new IllegalArgumentException("fromIndex(" + var1 + ") > toIndex(" + var2 + ")");
      } else if (var1 < 0) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else if (var2 > var0) {
         throw new ArrayIndexOutOfBoundsException(var2);
      }
   }

   private static int[] copyOfRange(int[] var0, int var1, int var2) {
      int var3 = var2 - var1;
      if (var3 < 0) {
         throw new IllegalArgumentException(var1 + " > " + var2);
      } else {
         int[] var4 = new int[var3];
         System.arraycopy(var0, var1, var4, 0, Math.min(var0.length - var1, var3));
         return var4;
      }
   }

   private static <T> T[] copyOfRange(T[] var0, int var1, int var2) {
      return (T[])copyOfRange(var0, var1, var2, (Class<? extends T[]>)var0.getClass());
   }

   private static <T, U> T[] copyOfRange(U[] var0, int var1, int var2, Class<? extends T[]> var3) {
      int var4 = var2 - var1;
      if (var4 < 0) {
         throw new IllegalArgumentException(var1 + " > " + var2);
      } else {
         Object[] var5 = var3 == Object[].class ? new Object[var4] : (Object[])Array.newInstance(var3.getComponentType(), var4);
         System.arraycopy(var0, var1, var5, 0, Math.min(var0.length - var1, var4));
         return (T[])var5;
      }
   }

   private void mergeSort(int[] var1, int[] var2, int var3, int var4, int var5) {
      int var6 = var4 - var3;
      if (var6 < 7) {
         for (int var15 = var3; var15 < var4; var15++) {
            for (int var16 = var15; var16 > var3 && Integer.valueOf(var2[var16 - 1]).compareTo(var2[var16]) > 0; var16--) {
               this.swap(var2, var16, var16 - 1);
            }
         }
      } else {
         int var8 = var4;
         var3 += var5;
         var4 += var5;
         int var9 = var3 + var4 >>> 1;
         this.mergeSort(var2, var1, var3, var9, -var5);
         this.mergeSort(var2, var1, var9, var4, -var5);
         if (Integer.valueOf(var1[var9 - 1]).compareTo(var1[var9]) <= 0) {
            System.arraycopy(var1, var3, var2, var3, var6);
         } else {
            int var10 = var3;
            int var11 = var3;

            for (int var12 = var9; var10 < var8; var10++) {
               if (var12 < var4 && (var11 >= var9 || Integer.valueOf(var1[var11]).compareTo(var1[var12]) > 0)) {
                  var2[var10] = var1[var12];
                  this.permutation[this.reversePermutation[var12++]] = var10;
               } else {
                  var2[var10] = var1[var11];
                  this.permutation[this.reversePermutation[var11++]] = var10;
               }
            }

            var10 = var3;

            while (var10 < var8) {
               this.reversePermutation[this.permutation[var10]] = var10++;
            }
         }
      }
   }

   private void mergeSort(Object[] var1, Object[] var2, int var3, int var4, int var5) {
      int var6 = var4 - var3;
      if (var6 < 7) {
         for (int var15 = var3; var15 < var4; var15++) {
            for (int var16 = var15; var16 > var3 && ((Comparable)var2[var16 - 1]).compareTo(var2[var16]) > 0; var16--) {
               this.swap(var2, var16, var16 - 1);
            }
         }
      } else {
         int var8 = var4;
         var3 += var5;
         var4 += var5;
         int var9 = var3 + var4 >>> 1;
         this.mergeSort(var2, var1, var3, var9, -var5);
         this.mergeSort(var2, var1, var9, var4, -var5);
         if (((Comparable)var1[var9 - 1]).compareTo(var1[var9]) <= 0) {
            System.arraycopy(var1, var3, var2, var3, var6);
         } else {
            int var10 = var3;
            int var11 = var3;

            for (int var12 = var9; var10 < var8; var10++) {
               if (var12 < var4 && (var11 >= var9 || ((Comparable)var1[var11]).compareTo(var1[var12]) > 0)) {
                  var2[var10] = var1[var12];
                  this.permutation[this.reversePermutation[var12++]] = var10;
               } else {
                  var2[var10] = var1[var11];
                  this.permutation[this.reversePermutation[var11++]] = var10;
               }
            }

            var10 = var3;

            while (var10 < var8) {
               this.reversePermutation[this.permutation[var10]] = var10++;
            }
         }
      }
   }

   private void mergeSort(Object[] var1, Object[] var2, int var3, int var4, int var5, Comparator var6) {
      int var7 = var4 - var3;
      if (var7 < 7) {
         for (int var16 = var3; var16 < var4; var16++) {
            for (int var17 = var16; var17 > var3 && var6.compare(var2[var17 - 1], var2[var17]) > 0; var17--) {
               this.swap(var2, var17, var17 - 1);
            }
         }
      } else {
         int var9 = var4;
         var3 += var5;
         var4 += var5;
         int var10 = var3 + var4 >>> 1;
         this.mergeSort(var2, var1, var3, var10, -var5, var6);
         this.mergeSort(var2, var1, var10, var4, -var5, var6);
         if (var6.compare(var1[var10 - 1], var1[var10]) <= 0) {
            System.arraycopy(var1, var3, var2, var3, var7);
         } else {
            int var11 = var3;
            int var12 = var3;

            for (int var13 = var10; var11 < var9; var11++) {
               if (var13 < var4 && (var12 >= var10 || var6.compare(var1[var12], var1[var13]) > 0)) {
                  var2[var11] = var1[var13];
                  this.permutation[this.reversePermutation[var13++]] = var11;
               } else {
                  var2[var11] = var1[var12];
                  this.permutation[this.reversePermutation[var12++]] = var11;
               }
            }

            var11 = var3;

            while (var11 < var9) {
               this.reversePermutation[this.permutation[var11]] = var11++;
            }
         }
      }
   }

   private void swap(int[] var1, int var2, int var3) {
      int var4 = var1[var2];
      var1[var2] = var1[var3];
      var1[var3] = var4;
      this.permutation[this.reversePermutation[var2]] = var3;
      this.permutation[this.reversePermutation[var3]] = var2;
      int var5 = this.reversePermutation[var2];
      this.reversePermutation[var2] = this.reversePermutation[var3];
      this.reversePermutation[var3] = var5;
   }

   private void swap(Object[] var1, int var2, int var3) {
      Object var4 = var1[var2];
      var1[var2] = var1[var3];
      var1[var3] = var4;
      this.permutation[this.reversePermutation[var2]] = var3;
      this.permutation[this.reversePermutation[var3]] = var2;
      int var5 = this.reversePermutation[var2];
      this.reversePermutation[var2] = this.reversePermutation[var3];
      this.reversePermutation[var3] = var5;
   }

   private int[] initPermutation(int var1) {
      this.permutation = new int[var1];
      this.reversePermutation = new int[var1];

      for (int var2 = 0; var2 < var1; var2++) {
         this.permutation[var2] = this.reversePermutation[var2] = var2;
      }

      return this.permutation;
   }
}
