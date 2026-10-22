package com.sun.marlin;

public final class FloatMath implements MarlinConst {
   static final boolean CHECK_OVERFLOW = true;
   static final boolean CHECK_NAN = true;

   private FloatMath() {
   }

   public static int max(int var0, int var1) {
      return var0 >= var1 ? var0 : var1;
   }

   public static int min(int var0, int var1) {
      return var0 <= var1 ? var0 : var1;
   }

   public static int ceil_int(float var0) {
      int var1 = (int)var0;
      return !(var0 <= (float)var1) && var1 != Integer.MAX_VALUE && !Float.isNaN(var0) ? var1 + 1 : var1;
   }

   public static int ceil_int(double var0) {
      int var2 = (int)var0;
      return !(var0 <= (double)var2) && var2 != Integer.MAX_VALUE && !Double.isNaN(var0) ? var2 + 1 : var2;
   }

   public static int floor_int(float var0) {
      int var1 = (int)var0;
      return !(var0 >= (float)var1) && var1 != Integer.MIN_VALUE && !Float.isNaN(var0) ? var1 - 1 : var1;
   }

   public static int floor_int(double var0) {
      int var2 = (int)var0;
      return !(var0 >= (double)var2) && var2 != Integer.MIN_VALUE && !Double.isNaN(var0) ? var2 - 1 : var2;
   }
}
