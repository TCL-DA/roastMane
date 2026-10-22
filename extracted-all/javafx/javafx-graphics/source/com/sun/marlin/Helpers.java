package com.sun.marlin;

import com.sun.marlin.stats.Histogram;
import com.sun.marlin.stats.StatLong;
import java.util.Arrays;

final class Helpers implements MarlinConst {
   private Helpers() {
      throw new Error("This is a non instantiable class");
   }

   static boolean within(double var0, double var2, double var4) {
      double var6 = var2 - var0;
      return var6 <= var4 && var6 >= -var4;
   }

   static double evalCubic(double var0, double var2, double var4, double var6, double var8) {
      return var8 * (var8 * (var8 * var0 + var2) + var4) + var6;
   }

   static double evalQuad(double var0, double var2, double var4, double var6) {
      return var6 * (var6 * var0 + var2) + var4;
   }

   static int quadraticRoots(double var0, double var2, double var4, double[] var6, int var7) {
      int var13 = var7;
      if (var0 != 0.0) {
         double var9 = var2 * var2 - 4.0 * var0 * var4;
         if (var9 > 0.0) {
            double var11 = Math.sqrt(var9);
            if (var2 >= 0.0) {
               var13 = var7 + 1;
               var6[var7] = 2.0 * var4 / (-var2 - var11);
               var6[var13++] = (-var2 - var11) / (2.0 * var0);
            } else {
               var13 = var7 + 1;
               var6[var7] = (-var2 + var11) / (2.0 * var0);
               var6[var13++] = 2.0 * var4 / (-var2 + var11);
            }
         } else if (var9 == 0.0) {
            var13 = var7 + 1;
            var6[var7] = -var2 / (2.0 * var0);
         }
      } else if (var2 != 0.0) {
         var13 = var7 + 1;
         var6[var7] = -var4 / var2;
      }

      return var13 - var7;
   }

   static int cubicRootsInAB(double var0, double var2, double var4, double var6, double[] var8, int var9, double var10, double var12) {
      if (var0 == 0.0) {
         int var36 = quadraticRoots(var2, var4, var6, var8, var9);
         return filterOutNotInAB(var8, var9, var36, var10, var12) - var9;
      } else {
         var2 /= var0;
         var4 /= var0;
         var6 /= var0;
         double var14 = 0.3333333333333333 * var2;
         double var16 = var2 * var2;
         double var18 = 0.3333333333333333 * (-0.3333333333333333 * var16 + var4);
         double var20 = 0.5 * (0.07407407407407407 * var2 * var16 - var14 * var4 + var6);
         double var22 = var18 * var18 * var18;
         double var24 = var20 * var20 + var22;
         byte var26;
         if (var24 < 0.0) {
            double var27 = 0.3333333333333333 * Math.acos(-var20 / Math.sqrt(-var22));
            double var29 = 2.0 * Math.sqrt(-var18);
            var8[var9] = var29 * Math.cos(var27) - var14;
            var8[var9 + 1] = -var29 * Math.cos(var27 + (Math.PI / 3)) - var14;
            var8[var9 + 2] = -var29 * Math.cos(var27 - (Math.PI / 3)) - var14;
            var26 = 3;
         } else {
            double var37 = Math.sqrt(var24);
            double var38 = Math.cbrt(var37 - var20);
            double var31 = -Math.cbrt(var37 + var20);
            var8[var9] = var38 + var31 - var14;
            var26 = 1;
            if (within(var24, 0.0, 1.0E-8)) {
               var8[var9 + 1] = -0.5 * (var38 + var31) - var14;
               var26 = 2;
            }
         }

         return filterOutNotInAB(var8, var9, var26, var10, var12) - var9;
      }
   }

   static int filterOutNotInAB(double[] var0, int var1, int var2, double var3, double var5) {
      int var7 = var1;
      int var8 = var1;

      for (int var9 = var1 + var2; var8 < var9; var8++) {
         if (var0[var8] >= var3 && var0[var8] < var5) {
            var0[var7++] = var0[var8];
         }
      }

      return var7;
   }

   static double fastLineLen(double var0, double var2, double var4, double var6) {
      double var8 = var4 - var0;
      double var10 = var6 - var2;
      return Math.abs(var8) + Math.abs(var10);
   }

   static double linelen(double var0, double var2, double var4, double var6) {
      double var8 = var4 - var0;
      double var10 = var6 - var2;
      return Math.sqrt(var8 * var8 + var10 * var10);
   }

   static double fastQuadLen(double var0, double var2, double var4, double var6, double var8, double var10) {
      double var12 = var4 - var0;
      double var14 = var8 - var4;
      double var16 = var6 - var2;
      double var18 = var10 - var6;
      return Math.abs(var12) + Math.abs(var14) + Math.abs(var16) + Math.abs(var18);
   }

   static double quadlen(double var0, double var2, double var4, double var6, double var8, double var10) {
      return (linelen(var0, var2, var4, var6) + linelen(var4, var6, var8, var10) + linelen(var0, var2, var8, var10)) / 2.0;
   }

   static double fastCurvelen(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14) {
      double var16 = var4 - var0;
      double var18 = var8 - var4;
      double var20 = var12 - var8;
      double var22 = var6 - var2;
      double var24 = var10 - var6;
      double var26 = var14 - var10;
      return Math.abs(var16) + Math.abs(var18) + Math.abs(var20) + Math.abs(var22) + Math.abs(var24) + Math.abs(var26);
   }

   static double curvelen(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14) {
      return (linelen(var0, var2, var4, var6) + linelen(var4, var6, var8, var10) + linelen(var8, var10, var12, var14) + linelen(var0, var2, var12, var14))
         / 2.0;
   }

   static int findSubdivPoints(Curve var0, double[] var1, double[] var2, int var3, double var4) {
      double var6 = var1[2] - var1[0];
      double var8 = var1[3] - var1[1];
      if (var8 != 0.0 && var6 != 0.0) {
         double var10 = Math.sqrt(var6 * var6 + var8 * var8);
         double var12 = var6 / var10;
         double var14 = var8 / var10;
         double var16 = var12 * var1[0] + var14 * var1[1];
         double var18 = var12 * var1[1] - var14 * var1[0];
         double var20 = var12 * var1[2] + var14 * var1[3];
         double var22 = var12 * var1[3] - var14 * var1[2];
         double var24 = var12 * var1[4] + var14 * var1[5];
         double var26 = var12 * var1[5] - var14 * var1[4];
         switch (var3) {
            case 6:
               var0.set(var16, var18, var20, var22, var24, var26);
               break;
            case 8:
               double var28 = var12 * var1[6] + var14 * var1[7];
               double var30 = var12 * var1[7] - var14 * var1[6];
               var0.set(var16, var18, var20, var22, var24, var26, var28, var30);
         }
      } else {
         var0.set(var1, var3);
      }

      int var32 = 0;
      var32 += var0.dxRoots(var2, var32);
      var32 += var0.dyRoots(var2, var32);
      if (var3 == 8) {
         var32 += var0.infPoints(var2, var32);
      }

      var32 += var0.rootsOfROCMinusW(var2, var32, var4, 1.0E-4);
      var32 = filterOutNotInAB(var2, 0, var32, 1.0E-4, 0.9999);
      isort(var2, var32);
      return var32;
   }

   static int findClipPoints(Curve var0, double[] var1, double[] var2, int var3, int var4, double[] var5) {
      var0.set(var1, var3);
      int var6 = 0;
      if ((var4 & 4) != 0) {
         var6 += var0.xPoints(var2, var6, var5[2]);
      }

      if ((var4 & 8) != 0) {
         var6 += var0.xPoints(var2, var6, var5[3]);
      }

      if ((var4 & 1) != 0) {
         var6 += var0.yPoints(var2, var6, var5[0]);
      }

      if ((var4 & 2) != 0) {
         var6 += var0.yPoints(var2, var6, var5[1]);
      }

      isort(var2, var6);
      return var6;
   }

   static void subdivide(double[] var0, double[] var1, double[] var2, int var3) {
      switch (var3) {
         case 6:
            subdivideQuad(var0, var1, var2);
            return;
         case 8:
            subdivideCubic(var0, var1, var2);
            return;
         default:
            throw new InternalError("Unsupported curve type");
      }
   }

   static void isort(double[] var0, int var1) {
      for (int var2 = 1; var2 < var1; var2++) {
         double var4 = var0[var2];

         int var3;
         for (var3 = var2 - 1; var3 >= 0 && var0[var3] > var4; var3--) {
            var0[var3 + 1] = var0[var3];
         }

         var0[var3 + 1] = var4;
      }
   }

   static void subdivideCubic(double[] var0, double[] var1, double[] var2) {
      double var3 = var0[0];
      double var5 = var0[1];
      double var7 = var0[2];
      double var9 = var0[3];
      double var11 = var0[4];
      double var13 = var0[5];
      double var15 = var0[6];
      double var17 = var0[7];
      var1[0] = var3;
      var1[1] = var5;
      var2[6] = var15;
      var2[7] = var17;
      var3 = (var3 + var7) / 2.0;
      var5 = (var5 + var9) / 2.0;
      var15 = (var15 + var11) / 2.0;
      var17 = (var17 + var13) / 2.0;
      double var19 = (var7 + var11) / 2.0;
      double var21 = (var9 + var13) / 2.0;
      var7 = (var3 + var19) / 2.0;
      var9 = (var5 + var21) / 2.0;
      var11 = (var15 + var19) / 2.0;
      var13 = (var17 + var21) / 2.0;
      var19 = (var7 + var11) / 2.0;
      var21 = (var9 + var13) / 2.0;
      var1[2] = var3;
      var1[3] = var5;
      var1[4] = var7;
      var1[5] = var9;
      var1[6] = var19;
      var1[7] = var21;
      var2[0] = var19;
      var2[1] = var21;
      var2[2] = var11;
      var2[3] = var13;
      var2[4] = var15;
      var2[5] = var17;
   }

   static void subdivideCubicAt(double var0, double[] var2, int var3, double[] var4, int var5, int var6) {
      double var7 = var2[var3];
      double var9 = var2[var3 + 1];
      double var11 = var2[var3 + 2];
      double var13 = var2[var3 + 3];
      double var15 = var2[var3 + 4];
      double var17 = var2[var3 + 5];
      double var19 = var2[var3 + 6];
      double var21 = var2[var3 + 7];
      var4[var5] = var7;
      var4[var5 + 1] = var9;
      var4[var6 + 6] = var19;
      var4[var6 + 7] = var21;
      var7 += var0 * (var11 - var7);
      var9 += var0 * (var13 - var9);
      var19 = var15 + var0 * (var19 - var15);
      var21 = var17 + var0 * (var21 - var17);
      double var23 = var11 + var0 * (var15 - var11);
      double var25 = var13 + var0 * (var17 - var13);
      var11 = var7 + var0 * (var23 - var7);
      var13 = var9 + var0 * (var25 - var9);
      var15 = var23 + var0 * (var19 - var23);
      var17 = var25 + var0 * (var21 - var25);
      var23 = var11 + var0 * (var15 - var11);
      var25 = var13 + var0 * (var17 - var13);
      var4[var5 + 2] = var7;
      var4[var5 + 3] = var9;
      var4[var5 + 4] = var11;
      var4[var5 + 5] = var13;
      var4[var5 + 6] = var23;
      var4[var5 + 7] = var25;
      var4[var6] = var23;
      var4[var6 + 1] = var25;
      var4[var6 + 2] = var15;
      var4[var6 + 3] = var17;
      var4[var6 + 4] = var19;
      var4[var6 + 5] = var21;
   }

   static void subdivideQuad(double[] var0, double[] var1, double[] var2) {
      double var3 = var0[0];
      double var5 = var0[1];
      double var7 = var0[2];
      double var9 = var0[3];
      double var11 = var0[4];
      double var13 = var0[5];
      var1[0] = var3;
      var1[1] = var5;
      var2[4] = var11;
      var2[5] = var13;
      var3 = (var3 + var7) / 2.0;
      var5 = (var5 + var9) / 2.0;
      var11 = (var11 + var7) / 2.0;
      var13 = (var13 + var9) / 2.0;
      var7 = (var3 + var11) / 2.0;
      var9 = (var5 + var13) / 2.0;
      var1[2] = var3;
      var1[3] = var5;
      var1[4] = var7;
      var1[5] = var9;
      var2[0] = var7;
      var2[1] = var9;
      var2[2] = var11;
      var2[3] = var13;
   }

   static void subdivideQuadAt(double var0, double[] var2, int var3, double[] var4, int var5, int var6) {
      double var7 = var2[var3];
      double var9 = var2[var3 + 1];
      double var11 = var2[var3 + 2];
      double var13 = var2[var3 + 3];
      double var15 = var2[var3 + 4];
      double var17 = var2[var3 + 5];
      var4[var5] = var7;
      var4[var5 + 1] = var9;
      var4[var6 + 4] = var15;
      var4[var6 + 5] = var17;
      var7 += var0 * (var11 - var7);
      var9 += var0 * (var13 - var9);
      var15 = var11 + var0 * (var15 - var11);
      var17 = var13 + var0 * (var17 - var13);
      var11 = var7 + var0 * (var15 - var7);
      var13 = var9 + var0 * (var17 - var9);
      var4[var5 + 2] = var7;
      var4[var5 + 3] = var9;
      var4[var5 + 4] = var11;
      var4[var5 + 5] = var13;
      var4[var6] = var11;
      var4[var6 + 1] = var13;
      var4[var6 + 2] = var15;
      var4[var6 + 3] = var17;
   }

   static void subdivideLineAt(double var0, double[] var2, int var3, double[] var4, int var5, int var6) {
      double var7 = var2[var3];
      double var9 = var2[var3 + 1];
      double var11 = var2[var3 + 2];
      double var13 = var2[var3 + 3];
      var4[var5] = var7;
      var4[var5 + 1] = var9;
      var4[var6 + 2] = var11;
      var4[var6 + 3] = var13;
      var7 += var0 * (var11 - var7);
      var9 += var0 * (var13 - var9);
      var4[var5 + 2] = var7;
      var4[var5 + 3] = var9;
      var4[var6] = var7;
      var4[var6 + 1] = var9;
   }

   static void subdivideAt(double var0, double[] var2, int var3, double[] var4, int var5, int var6) {
      if (var6 == 8) {
         subdivideCubicAt(var0, var2, var3, var4, var5, var5 + var6);
      } else if (var6 == 4) {
         subdivideLineAt(var0, var2, var3, var4, var5, var5 + var6);
      } else {
         subdivideQuadAt(var0, var2, var3, var4, var5, var5 + var6);
      }
   }

   static int outcode(double var0, double var2, double[] var4) {
      byte var5;
      if (var2 < var4[0]) {
         var5 = 1;
      } else if (var2 >= var4[1]) {
         var5 = 2;
      } else {
         var5 = 0;
      }

      if (var0 < var4[2]) {
         var5 |= 4;
      } else if (var0 >= var4[3]) {
         var5 |= 8;
      }

      return var5;
   }

   static final class IndexStack {
      private static final int INITIAL_COUNT = MarlinConst.INITIAL_EDGES_COUNT >> 2;
      private int end;
      private int[] indices;
      private final IntArrayCache.Reference indices_ref;
      private int indicesUseMark;
      private final StatLong stat_idxstack_indices;
      private final Histogram hist_idxstack_indices;
      private final StatLong stat_array_idxstack_indices;

      IndexStack(RendererContext var1) {
         this(var1, null, null, null);
      }

      IndexStack(RendererContext var1, StatLong var2, Histogram var3, StatLong var4) {
         this.indices_ref = var1.newDirtyIntArrayRef(INITIAL_COUNT);
         this.indices = this.indices_ref.initial;
         this.end = 0;
         if (MarlinConst.DO_STATS) {
            this.indicesUseMark = 0;
         }

         this.stat_idxstack_indices = var2;
         this.hist_idxstack_indices = var3;
         this.stat_array_idxstack_indices = var4;
      }

      void dispose() {
         this.end = 0;
         if (MarlinConst.DO_STATS) {
            this.stat_idxstack_indices.add(this.indicesUseMark);
            this.hist_idxstack_indices.add(this.indicesUseMark);
            this.indicesUseMark = 0;
         }

         this.indices = this.indices_ref.putArray(this.indices);
      }

      boolean isEmpty() {
         return this.end == 0;
      }

      void reset() {
         this.end = 0;
      }

      void push(int var1) {
         int[] var2 = this.indices;
         int var3 = this.end;
         if (var3 != 0 && var2[var3 - 1] == var1) {
            this.end--;
         } else {
            if (var2.length <= var3) {
               if (MarlinConst.DO_STATS) {
                  this.stat_array_idxstack_indices.add(var3 + 1);
               }

               this.indices = var2 = this.indices_ref.widenArray(var2, var3, var3 + 1);
            }

            var2[this.end++] = var1;
            if (MarlinConst.DO_STATS && this.end > this.indicesUseMark) {
               this.indicesUseMark = this.end;
            }
         }
      }

      void pullAll(double[] var1, DPathConsumer2D var2) {
         int var3 = this.end;
         if (var3 != 0) {
            int[] var4 = this.indices;

            for (int var5 = 0; var5 < var3; var5++) {
               int var6 = var4[var5] << 1;
               var2.lineTo(var1[var6], var1[var6 + 1]);
            }

            this.end = 0;
         }
      }
   }

   static final class PolyStack {
      private static final byte TYPE_LINETO = 0;
      private static final byte TYPE_QUADTO = 1;
      private static final byte TYPE_CUBICTO = 2;
      private static final int INITIAL_CURVES_COUNT = MarlinConst.INITIAL_EDGES_COUNT << 1;
      private static final int INITIAL_TYPES_COUNT = MarlinConst.INITIAL_EDGES_COUNT;
      double[] curves;
      int end;
      byte[] curveTypes;
      int numCurves;
      final DoubleArrayCache.Reference curves_ref;
      final ByteArrayCache.Reference curveTypes_ref;
      int curveTypesUseMark;
      int curvesUseMark;
      private final StatLong stat_polystack_types;
      private final StatLong stat_polystack_curves;
      private final Histogram hist_polystack_curves;
      private final StatLong stat_array_polystack_curves;
      private final StatLong stat_array_polystack_curveTypes;

      PolyStack(RendererContext var1) {
         this(var1, null, null, null, null, null);
      }

      PolyStack(RendererContext var1, StatLong var2, StatLong var3, Histogram var4, StatLong var5, StatLong var6) {
         this.curves_ref = var1.newDirtyDoubleArrayRef(INITIAL_CURVES_COUNT);
         this.curves = this.curves_ref.initial;
         this.curveTypes_ref = var1.newDirtyByteArrayRef(INITIAL_TYPES_COUNT);
         this.curveTypes = this.curveTypes_ref.initial;
         this.numCurves = 0;
         this.end = 0;
         if (MarlinConst.DO_STATS) {
            this.curveTypesUseMark = 0;
            this.curvesUseMark = 0;
         }

         this.stat_polystack_types = var2;
         this.stat_polystack_curves = var3;
         this.hist_polystack_curves = var4;
         this.stat_array_polystack_curves = var5;
         this.stat_array_polystack_curveTypes = var6;
      }

      void dispose() {
         this.end = 0;
         this.numCurves = 0;
         if (MarlinConst.DO_STATS) {
            this.stat_polystack_types.add(this.curveTypesUseMark);
            this.stat_polystack_curves.add(this.curvesUseMark);
            this.hist_polystack_curves.add(this.curvesUseMark);
            this.curveTypesUseMark = 0;
            this.curvesUseMark = 0;
         }

         this.curves = this.curves_ref.putArray(this.curves);
         this.curveTypes = this.curveTypes_ref.putArray(this.curveTypes);
      }

      private void ensureSpace(int var1) {
         if (this.curves.length - this.end < var1) {
            if (MarlinConst.DO_STATS) {
               this.stat_array_polystack_curves.add(this.end + var1);
            }

            this.curves = this.curves_ref.widenArray(this.curves, this.end, this.end + var1);
         }

         if (this.curveTypes.length <= this.numCurves) {
            if (MarlinConst.DO_STATS) {
               this.stat_array_polystack_curveTypes.add(this.numCurves + 1);
            }

            this.curveTypes = this.curveTypes_ref.widenArray(this.curveTypes, this.numCurves, this.numCurves + 1);
         }
      }

      void pushCubic(double var1, double var3, double var5, double var7, double var9, double var11) {
         this.ensureSpace(6);
         this.curveTypes[this.numCurves++] = 2;
         double[] var13 = this.curves;
         int var14 = this.end;
         var13[var14++] = var9;
         var13[var14++] = var11;
         var13[var14++] = var5;
         var13[var14++] = var7;
         var13[var14++] = var1;
         var13[var14++] = var3;
         this.end = var14;
      }

      void pushQuad(double var1, double var3, double var5, double var7) {
         this.ensureSpace(4);
         this.curveTypes[this.numCurves++] = 1;
         double[] var9 = this.curves;
         int var10 = this.end;
         var9[var10++] = var5;
         var9[var10++] = var7;
         var9[var10++] = var1;
         var9[var10++] = var3;
         this.end = var10;
      }

      void pushLine(double var1, double var3) {
         this.ensureSpace(2);
         this.curveTypes[this.numCurves++] = 0;
         this.curves[this.end++] = var1;
         this.curves[this.end++] = var3;
      }

      void pullAll(DPathConsumer2D var1) {
         int var2 = this.numCurves;
         if (var2 != 0) {
            if (MarlinConst.DO_STATS) {
               if (this.numCurves > this.curveTypesUseMark) {
                  this.curveTypesUseMark = this.numCurves;
               }

               if (this.end > this.curvesUseMark) {
                  this.curvesUseMark = this.end;
               }
            }

            byte[] var3 = this.curveTypes;
            double[] var4 = this.curves;
            byte var5 = 0;

            for (int var6 = 0; var6 < var2; var6++) {
               switch (var3[var6]) {
                  case 0:
                     var1.lineTo(var4[var5], var4[var5 + 1]);
                     var5 += 2;
                     break;
                  case 1:
                     var1.quadTo(var4[var5], var4[var5 + 1], var4[var5 + 2], var4[var5 + 3]);
                     var5 += 4;
                     break;
                  case 2:
                     var1.curveTo(var4[var5], var4[var5 + 1], var4[var5 + 2], var4[var5 + 3], var4[var5 + 4], var4[var5 + 5]);
                     var5 += 6;
               }
            }

            this.numCurves = 0;
            this.end = 0;
         }
      }

      void popAll(DPathConsumer2D var1) {
         int var2 = this.numCurves;
         if (var2 != 0) {
            if (MarlinConst.DO_STATS) {
               if (this.numCurves > this.curveTypesUseMark) {
                  this.curveTypesUseMark = this.numCurves;
               }

               if (this.end > this.curvesUseMark) {
                  this.curvesUseMark = this.end;
               }
            }

            byte[] var3 = this.curveTypes;
            double[] var4 = this.curves;
            int var5 = this.end;

            while (var2 != 0) {
               var2--;
               switch (var3[var2]) {
                  case 0:
                     var5 -= 2;
                     var1.lineTo(var4[var5], var4[var5 + 1]);
                     break;
                  case 1:
                     var5 -= 4;
                     var1.quadTo(var4[var5], var4[var5 + 1], var4[var5 + 2], var4[var5 + 3]);
                     break;
                  case 2:
                     var5 -= 6;
                     var1.curveTo(var4[var5], var4[var5 + 1], var4[var5 + 2], var4[var5 + 3], var4[var5 + 4], var4[var5 + 5]);
               }
            }

            this.numCurves = 0;
            this.end = 0;
         }
      }

      @Override
      public String toString() {
         String var1 = "";
         int var2 = this.numCurves;
         int var3 = this.end;

         while (var2 != 0) {
            var2--;
            byte var4;
            switch (this.curveTypes[var2]) {
               case 0:
                  var4 = 2;
                  var1 = var1 + "line: ";
                  break;
               case 1:
                  var4 = 4;
                  var1 = var1 + "quad: ";
                  break;
               case 2:
                  var4 = 6;
                  var1 = var1 + "cubic: ";
                  break;
               default:
                  var4 = 0;
            }

            var3 -= var4;
            var1 = var1 + Arrays.toString(Arrays.copyOfRange(this.curves, var3, var3 + var4)) + "\n";
         }

         return var1;
      }
   }
}
