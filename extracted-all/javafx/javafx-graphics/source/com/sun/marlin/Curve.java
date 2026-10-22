package com.sun.marlin;

final class Curve {
   double ax;
   double ay;
   double bx;
   double by;
   double cx;
   double cy;
   double dx;
   double dy;
   double dax;
   double day;
   double dbx;
   double dby;

   void set(double[] var1, int var2) {
      if (var2 == 8) {
         this.set(var1[0], var1[1], var1[2], var1[3], var1[4], var1[5], var1[6], var1[7]);
      } else if (var2 == 4) {
         this.set(var1[0], var1[1], var1[2], var1[3]);
      } else {
         this.set(var1[0], var1[1], var1[2], var1[3], var1[4], var1[5]);
      }
   }

   void set(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15) {
      double var17 = 3.0 * (var9 - var5);
      double var19 = 3.0 * (var11 - var7);
      double var21 = 3.0 * (var5 - var1);
      double var23 = 3.0 * (var7 - var3);
      this.ax = var13 - var1 - var17;
      this.ay = var15 - var3 - var19;
      this.bx = var17 - var21;
      this.by = var19 - var23;
      this.cx = var21;
      this.cy = var23;
      this.dx = var1;
      this.dy = var3;
      this.dax = 3.0 * this.ax;
      this.day = 3.0 * this.ay;
      this.dbx = 2.0 * this.bx;
      this.dby = 2.0 * this.by;
   }

   void set(double var1, double var3, double var5, double var7, double var9, double var11) {
      double var13 = var5 - var1;
      double var15 = var7 - var3;
      this.ax = 0.0;
      this.ay = 0.0;
      this.bx = var9 - var5 - var13;
      this.by = var11 - var7 - var15;
      this.cx = 2.0 * var13;
      this.cy = 2.0 * var15;
      this.dx = var1;
      this.dy = var3;
      this.dax = 0.0;
      this.day = 0.0;
      this.dbx = 2.0 * this.bx;
      this.dby = 2.0 * this.by;
   }

   void set(double var1, double var3, double var5, double var7) {
      double var9 = var5 - var1;
      double var11 = var7 - var3;
      this.ax = 0.0;
      this.ay = 0.0;
      this.bx = 0.0;
      this.by = 0.0;
      this.cx = var9;
      this.cy = var11;
      this.dx = var1;
      this.dy = var3;
      this.dax = 0.0;
      this.day = 0.0;
      this.dbx = 0.0;
      this.dby = 0.0;
   }

   int dxRoots(double[] var1, int var2) {
      return Helpers.quadraticRoots(this.dax, this.dbx, this.cx, var1, var2);
   }

   int dyRoots(double[] var1, int var2) {
      return Helpers.quadraticRoots(this.day, this.dby, this.cy, var1, var2);
   }

   int infPoints(double[] var1, int var2) {
      double var3 = this.dax * this.dby - this.dbx * this.day;
      double var5 = 2.0 * (this.cy * this.dax - this.day * this.cx);
      double var7 = this.cy * this.dbx - this.cx * this.dby;
      return Helpers.quadraticRoots(var3, var5, var7, var1, var2);
   }

   int xPoints(double[] var1, int var2, double var3) {
      return Helpers.cubicRootsInAB(this.ax, this.bx, this.cx, this.dx - var3, var1, var2, 0.0, 1.0);
   }

   int yPoints(double[] var1, int var2, double var3) {
      return Helpers.cubicRootsInAB(this.ay, this.by, this.cy, this.dy - var3, var1, var2, 0.0, 1.0);
   }

   private int perpendiculardfddf(double[] var1, int var2) {
      assert var1.length >= var2 + 4;

      double var3 = 2.0 * (this.dax * this.dax + this.day * this.day);
      double var5 = 3.0 * (this.dax * this.dbx + this.day * this.dby);
      double var7 = 2.0 * (this.dax * this.cx + this.day * this.cy) + this.dbx * this.dbx + this.dby * this.dby;
      double var9 = this.dbx * this.cx + this.dby * this.cy;
      return Helpers.cubicRootsInAB(var3, var5, var7, var9, var1, var2, 0.0, 1.0);
   }

   int rootsOfROCMinusW(double[] var1, int var2, double var3, double var5) {
      assert var2 <= 6 && var1.length >= 10;

      int var7 = var2;
      int var8 = var2 + this.perpendiculardfddf(var1, var2);
      var1[var8] = 1.0;
      double var9 = 0.0;
      double var11 = this.ROCsq(var9) - var3;

      for (int var13 = var2; var13 <= var8; var13++) {
         double var14 = var1[var13];
         double var16 = this.ROCsq(var14) - var3;
         if (var11 == 0.0) {
            var1[var7++] = var9;
         } else if (var16 * var11 < 0.0) {
            var1[var7++] = this.falsePositionROCsqMinusX(var9, var14, var3, var5);
         }

         var9 = var14;
         var11 = var16;
      }

      return var7 - var2;
   }

   private static double eliminateInf(double var0) {
      return var0 == Double.POSITIVE_INFINITY ? Double.MAX_VALUE : (var0 == Double.NEGATIVE_INFINITY ? Double.MIN_VALUE : var0);
   }

   private double falsePositionROCsqMinusX(double var1, double var3, double var5, double var7) {
      int var9 = 0;
      double var10 = var3;
      double var12 = eliminateInf(this.ROCsq(var3) - var5);
      double var14 = var1;
      double var16 = eliminateInf(this.ROCsq(var1) - var5);
      double var18 = var1;

      for (int var22 = 0; var22 < 100 && Math.abs(var10 - var14) > var7 * Math.abs(var10 + var14); var22++) {
         var18 = (var16 * var10 - var12 * var14) / (var16 - var12);
         double var20 = this.ROCsq(var18) - var5;
         if (sameSign(var20, var12)) {
            var12 = var20;
            var10 = var18;
            if (var9 < 0) {
               var16 /= (double)(1 << -var9);
               var9--;
            } else {
               var9 = -1;
            }
         } else {
            if (!(var20 * var16 > 0.0)) {
               break;
            }

            var16 = var20;
            var14 = var18;
            if (var9 > 0) {
               var12 /= (double)(1 << var9);
               var9++;
            } else {
               var9 = 1;
            }
         }
      }

      return var18;
   }

   private static boolean sameSign(double var0, double var2) {
      return var0 < 0.0 && var2 < 0.0 || var0 > 0.0 && var2 > 0.0;
   }

   private double ROCsq(double var1) {
      double var3 = var1 * (var1 * this.dax + this.dbx) + this.cx;
      double var5 = var1 * (var1 * this.day + this.dby) + this.cy;
      double var7 = 2.0 * this.dax * var1 + this.dbx;
      double var9 = 2.0 * this.day * var1 + this.dby;
      double var11 = var3 * var3 + var5 * var5;
      double var13 = var7 * var7 + var9 * var9;
      double var15 = var7 * var3 + var9 * var5;
      return var11 * (var11 * var11 / (var11 * var13 - var15 * var15));
   }
}
