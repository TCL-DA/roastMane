package com.sun.marlin;

import java.util.Arrays;

public final class Stroker implements DPathConsumer2D, MarlinConst {
   private static final int MOVE_TO = 0;
   private static final int DRAWING_OP_TO = 1;
   private static final int CLOSE = 2;
   private static final double ERR_JOIN = (double)(1.0F / MIN_SUBPIXELS);
   private static final double ROUND_JOIN_THRESHOLD = ERR_JOIN * ERR_JOIN;
   private static final double C = 4.0 * (Math.sqrt(2.0) - 1.0) / 3.0;
   private static final double SQRT_2 = Math.sqrt(2.0);
   private DPathConsumer2D out;
   private int capStyle;
   private int joinStyle;
   private double lineWidth2;
   private double invHalfLineWidth2Sq;
   private final double[] offset0 = new double[2];
   private final double[] offset1 = new double[2];
   private final double[] offset2 = new double[2];
   private final double[] miter = new double[2];
   private double miterLimitSq;
   private int prev;
   private double sx0;
   private double sy0;
   private double sdx;
   private double sdy;
   private double cx0;
   private double cy0;
   private double cdx;
   private double cdy;
   private double smx;
   private double smy;
   private double cmx;
   private double cmy;
   private final Helpers.PolyStack reverse;
   private final double[] lp = new double[8];
   private final double[] rp = new double[8];
   final RendererContext rdrCtx;
   final Curve curve;
   private double[] clipRect;
   private int cOutCode = 0;
   private int sOutCode = 0;
   private boolean opened = false;
   private boolean capStart = false;
   private boolean monotonize;
   private boolean subdivide = false;
   private final TransformingPathConsumer2D.CurveClipSplitter curveSplitter;

   Stroker(RendererContext var1) {
      this.rdrCtx = var1;
      this.reverse = var1.stats != null
         ? new Helpers.PolyStack(
            var1,
            var1.stats.stat_str_polystack_types,
            var1.stats.stat_str_polystack_curves,
            var1.stats.hist_str_polystack_curves,
            var1.stats.stat_array_str_polystack_curves,
            var1.stats.stat_array_str_polystack_types
         )
         : new Helpers.PolyStack(var1);
      this.curve = var1.curve;
      this.curveSplitter = var1.curveClipSplitter;
   }

   public Stroker init(DPathConsumer2D var1, double var2, int var4, int var5, double var6, boolean var8) {
      this.out = var1;
      this.lineWidth2 = var2 / 2.0;
      this.invHalfLineWidth2Sq = 1.0 / (2.0 * this.lineWidth2 * this.lineWidth2);
      this.monotonize = var8;
      this.capStyle = var4;
      this.joinStyle = var5;
      double var9 = var6 * this.lineWidth2;
      this.miterLimitSq = var9 * var9;
      this.prev = 2;
      this.rdrCtx.stroking = 1;
      if (this.rdrCtx.doClip) {
         double var11 = this.lineWidth2;
         if (var4 == 2) {
            var11 *= SQRT_2;
         }

         if (var5 == 0 && var11 < var9) {
            var11 = var9;
         }

         double[] var13 = this.rdrCtx.clipRect;
         var13[0] -= var11;
         var13[1] += var11;
         var13[2] -= var11;
         var13[3] += var11;
         this.clipRect = var13;
         if (MarlinConst.DO_LOG_CLIP) {
            MarlinUtils.logInfo("clipRect (stroker): " + Arrays.toString(this.rdrCtx.clipRect));
         }

         if (DO_CLIP_SUBDIVIDER) {
            this.subdivide = var8;
            this.curveSplitter.init();
         } else {
            this.subdivide = false;
         }
      } else {
         this.clipRect = null;
         this.cOutCode = 0;
         this.sOutCode = 0;
      }

      return this;
   }

   public void disableClipping() {
      this.clipRect = null;
      this.cOutCode = 0;
      this.sOutCode = 0;
   }

   void dispose() {
      this.reverse.dispose();
      this.opened = false;
      this.capStart = false;
   }

   private static void computeOffset(double var0, double var2, double var4, double[] var6) {
      double var7 = var0 * var0 + var2 * var2;
      if (var7 == 0.0) {
         var6[0] = 0.0;
         var6[1] = 0.0;
      } else {
         var7 = Math.sqrt(var7);
         var6[0] = var2 * var4 / var7;
         var6[1] = -(var0 * var4) / var7;
      }
   }

   private static boolean isCW(double var0, double var2, double var4, double var6) {
      return var0 * var6 <= var2 * var4;
   }

   private void mayDrawRoundJoin(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13) {
      if ((var5 != 0.0 || var7 != 0.0) && (var9 != 0.0 || var11 != 0.0)) {
         double var14 = var5 - var9;
         double var16 = var7 - var11;
         double var18 = var14 * var14 + var16 * var16;
         if (!(var18 < ROUND_JOIN_THRESHOLD)) {
            if (var13) {
               var5 = -var5;
               var7 = -var7;
               var9 = -var9;
               var11 = -var11;
            }

            this.drawRoundJoin(var1, var3, var5, var7, var9, var11, var13);
         }
      }
   }

   private void drawRoundJoin(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13) {
      double var14 = var5 * var9 + var7 * var11;
      if (var14 >= 0.0) {
         this.drawBezApproxForArc(var1, var3, var5, var7, var9, var11, var13);
      } else {
         double var16 = var11 - var7;
         double var18 = var5 - var9;
         double var20 = Math.sqrt(var16 * var16 + var18 * var18);
         double var22 = this.lineWidth2 / var20;
         double var24 = var16 * var22;
         double var26 = var18 * var22;
         if (var13) {
            var24 = -var24;
            var26 = -var26;
         }

         this.drawBezApproxForArc(var1, var3, var5, var7, var24, var26, var13);
         this.drawBezApproxForArc(var1, var3, var24, var26, var9, var11, var13);
      }
   }

   private void drawBezApproxForArc(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13) {
      double var14 = (var5 * var9 + var7 * var11) * this.invHalfLineWidth2Sq;
      if (!(var14 >= 0.5)) {
         double var16 = 1.3333333333333333 * Math.sqrt(0.5 - var14) / (1.0 + Math.sqrt(var14 + 0.5));
         if (var13) {
            var16 = -var16;
         }

         double var18 = var1 + var5;
         double var20 = var3 + var7;
         double var22 = var18 - var16 * var7;
         double var24 = var20 + var16 * var5;
         double var26 = var1 + var9;
         double var28 = var3 + var11;
         double var30 = var26 + var16 * var11;
         double var32 = var28 - var16 * var9;
         this.emitCurveTo(var18, var20, var22, var24, var30, var32, var26, var28, var13);
      }
   }

   private void drawRoundCap(double var1, double var3, double var5, double var7) {
      double var9 = C * var5;
      double var11 = C * var7;
      this.emitCurveTo(var1 + var5 - var11, var3 + var7 + var9, var1 - var7 + var9, var3 + var5 + var11, var1 - var7, var3 + var5);
      this.emitCurveTo(var1 - var7 - var9, var3 + var5 - var11, var1 - var5 - var11, var3 - var7 + var9, var1 - var5, var3 - var7);
   }

   private static void computeMiter(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double[] var16) {
      double var17 = var4 - var0;
      double var19 = var6 - var2;
      double var21 = var12 - var8;
      double var23 = var14 - var10;
      double var25 = var17 * var23 - var21 * var19;
      double var27 = var21 * (var2 - var10) - var23 * (var0 - var8);
      var27 /= var25;
      var16[0] = var0 + var27 * var17;
      var16[1] = var2 + var27 * var19;
   }

   private static void safeComputeMiter(
      double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double[] var16
   ) {
      double var17 = var4 - var0;
      double var19 = var6 - var2;
      double var21 = var12 - var8;
      double var23 = var14 - var10;
      double var25 = var17 * var23 - var21 * var19;
      if (var25 == 0.0) {
         var16[2] = (var0 + var8) / 2.0;
         var16[3] = (var2 + var10) / 2.0;
      } else {
         double var27 = var21 * (var2 - var10) - var23 * (var0 - var8);
         var27 /= var25;
         var16[2] = var0 + var27 * var17;
         var16[3] = var2 + var27 * var19;
      }
   }

   private void drawMiter(
      double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, double var19, boolean var21
   ) {
      if ((var17 != var13 || var19 != var15) && (var1 != 0.0 || var3 != 0.0) && (var9 != 0.0 || var11 != 0.0)) {
         if (var21) {
            var13 = -var13;
            var15 = -var15;
            var17 = -var17;
            var19 = -var19;
         }

         computeMiter(
            var5 - var1 + var13,
            var7 - var3 + var15,
            var5 + var13,
            var7 + var15,
            var9 + var5 + var17,
            var11 + var7 + var19,
            var5 + var17,
            var7 + var19,
            this.miter
         );
         double var22 = this.miter[0];
         double var24 = this.miter[1];
         double var26 = (var22 - var5) * (var22 - var5) + (var24 - var7) * (var24 - var7);
         if (var26 < this.miterLimitSq) {
            this.emitLineTo(var22, var24, var21);
         }
      }
   }

   @Override
   public void moveTo(double var1, double var3) {
      this._moveTo(var1, var3, this.cOutCode);
      this.sx0 = var1;
      this.sy0 = var3;
      this.sdx = 1.0;
      this.sdy = 0.0;
      this.opened = false;
      this.capStart = false;
      if (this.clipRect != null) {
         int var5 = Helpers.outcode(var1, var3, this.clipRect);
         this.cOutCode = var5;
         this.sOutCode = var5;
      }
   }

   private void _moveTo(double var1, double var3, int var5) {
      if (this.prev == 0) {
         this.cx0 = var1;
         this.cy0 = var3;
      } else {
         if (this.prev == 1) {
            this.finish(var5);
         }

         this.prev = 0;
         this.cx0 = var1;
         this.cy0 = var3;
         this.cdx = 1.0;
         this.cdy = 0.0;
      }
   }

   @Override
   public void lineTo(double var1, double var3) {
      this.lineTo(var1, var3, false);
   }

   private void lineTo(double var1, double var3, boolean var5) {
      int var6 = this.cOutCode;
      if (!var5 && this.clipRect != null) {
         int var7 = Helpers.outcode(var1, var3, this.clipRect);
         int var8 = var6 | var7;
         if (var8 != 0) {
            int var9 = var6 & var7;
            if (var9 != 0) {
               this.cOutCode = var7;
               this._moveTo(var1, var3, var6);
               this.opened = true;
               return;
            }

            if (this.subdivide) {
               this.subdivide = false;
               boolean var10 = this.curveSplitter.splitLine(this.cx0, this.cy0, var1, var3, var8, this);
               this.subdivide = true;
               if (var10) {
                  return;
               }
            }
         }

         this.cOutCode = var7;
      }

      double var15 = var1 - this.cx0;
      double var16 = var3 - this.cy0;
      if (var15 == 0.0 && var16 == 0.0) {
         var15 = 1.0;
      }

      computeOffset(var15, var16, this.lineWidth2, this.offset0);
      double var11 = this.offset0[0];
      double var13 = this.offset0[1];
      this.drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, var15, var16, this.cmx, this.cmy, var11, var13, var6);
      this.emitLineTo(this.cx0 + var11, this.cy0 + var13);
      this.emitLineTo(var1 + var11, var3 + var13);
      this.emitLineToRev(this.cx0 - var11, this.cy0 - var13);
      this.emitLineToRev(var1 - var11, var3 - var13);
      this.prev = 1;
      this.cx0 = var1;
      this.cy0 = var3;
      this.cdx = var15;
      this.cdy = var16;
      this.cmx = var11;
      this.cmy = var13;
   }

   @Override
   public void closePath() {
      if (this.prev != 1 && !this.opened) {
         if (this.prev != 2) {
            this.emitMoveTo(this.cx0, this.cy0 - this.lineWidth2);
            this.sdx = 1.0;
            this.sdy = 0.0;
            this.cdx = 1.0;
            this.cdy = 0.0;
            this.smx = 0.0;
            this.smy = -this.lineWidth2;
            this.cmx = 0.0;
            this.cmy = -this.lineWidth2;
            this.finish(this.cOutCode);
         }
      } else {
         if ((this.sOutCode & this.cOutCode) == 0) {
            if (this.cx0 != this.sx0 || this.cy0 != this.sy0) {
               this.lineTo(this.sx0, this.sy0, true);
            }

            this.drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, this.sdx, this.sdy, this.cmx, this.cmy, this.smx, this.smy, this.sOutCode);
            this.emitLineTo(this.sx0 + this.smx, this.sy0 + this.smy);
            if (this.opened) {
               this.emitLineTo(this.sx0 - this.smx, this.sy0 - this.smy);
            } else {
               this.emitMoveTo(this.sx0 - this.smx, this.sy0 - this.smy);
            }
         }

         this.emitReverse();
         this.prev = 2;
         this.cx0 = this.sx0;
         this.cy0 = this.sy0;
         this.cOutCode = this.sOutCode;
         if (this.opened) {
            this.opened = false;
         } else {
            this.emitClose();
         }
      }
   }

   private void emitReverse() {
      this.reverse.popAll(this.out);
   }

   @Override
   public void pathDone() {
      if (this.prev == 1) {
         this.finish(this.cOutCode);
      }

      this.out.pathDone();
      this.prev = 2;
      this.dispose();
   }

   private void finish(int var1) {
      if (this.rdrCtx.closedPath) {
         this.emitReverse();
      } else {
         if (var1 == 0) {
            if (this.capStyle == 1) {
               this.drawRoundCap(this.cx0, this.cy0, this.cmx, this.cmy);
            } else if (this.capStyle == 2) {
               this.emitLineTo(this.cx0 - this.cmy + this.cmx, this.cy0 + this.cmx + this.cmy);
               this.emitLineTo(this.cx0 - this.cmy - this.cmx, this.cy0 + this.cmx - this.cmy);
            }
         }

         this.emitReverse();
         if (!this.capStart) {
            this.capStart = true;
            if (this.sOutCode == 0) {
               if (this.capStyle == 1) {
                  this.drawRoundCap(this.sx0, this.sy0, -this.smx, -this.smy);
               } else if (this.capStyle == 2) {
                  this.emitLineTo(this.sx0 + this.smy - this.smx, this.sy0 - this.smx - this.smy);
                  this.emitLineTo(this.sx0 + this.smy + this.smx, this.sy0 - this.smx + this.smy);
               }
            }
         }
      }

      this.emitClose();
   }

   private void emitMoveTo(double var1, double var3) {
      this.out.moveTo(var1, var3);
   }

   private void emitLineTo(double var1, double var3) {
      this.out.lineTo(var1, var3);
   }

   private void emitLineToRev(double var1, double var3) {
      this.reverse.pushLine(var1, var3);
   }

   private void emitLineTo(double var1, double var3, boolean var5) {
      if (var5) {
         this.emitLineToRev(var1, var3);
      } else {
         this.emitLineTo(var1, var3);
      }
   }

   private void emitQuadTo(double var1, double var3, double var5, double var7) {
      this.out.quadTo(var1, var3, var5, var7);
   }

   private void emitQuadToRev(double var1, double var3, double var5, double var7) {
      this.reverse.pushQuad(var1, var3, var5, var7);
   }

   private void emitCurveTo(double var1, double var3, double var5, double var7, double var9, double var11) {
      this.out.curveTo(var1, var3, var5, var7, var9, var11);
   }

   private void emitCurveToRev(double var1, double var3, double var5, double var7, double var9, double var11) {
      this.reverse.pushCubic(var1, var3, var5, var7, var9, var11);
   }

   private void emitCurveTo(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, boolean var17) {
      if (var17) {
         this.reverse.pushCubic(var1, var3, var5, var7, var9, var11);
      } else {
         this.out.curveTo(var5, var7, var9, var11, var13, var15);
      }
   }

   private void emitClose() {
      this.out.closePath();
   }

   private void drawJoin(
      double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, double var19, int var21
   ) {
      if (this.prev != 1) {
         this.emitMoveTo(var5 + var17, var7 + var19);
         if (!this.opened) {
            this.sdx = var9;
            this.sdy = var11;
            this.smx = var17;
            this.smy = var19;
         }
      } else {
         boolean var22 = isCW(var1, var3, var9, var11);
         if (var21 == 0) {
            if (this.joinStyle == 0) {
               this.drawMiter(var1, var3, var5, var7, var9, var11, var13, var15, var17, var19, var22);
            } else if (this.joinStyle == 1) {
               this.mayDrawRoundJoin(var5, var7, var13, var15, var17, var19, var22);
            }
         }

         this.emitLineTo(var5, var7, !var22);
      }

      this.prev = 1;
   }

   private static boolean within(double var0, double var2, double var4, double var6, double var8) {
      assert var8 > 0.0 : "";

      return Helpers.within(var0, var4, var8) && Helpers.within(var2, var6, var8);
   }

   private void getLineOffsets(double var1, double var3, double var5, double var7, double[] var9, double[] var10) {
      computeOffset(var5 - var1, var7 - var3, this.lineWidth2, this.offset0);
      double var11 = this.offset0[0];
      double var13 = this.offset0[1];
      var9[0] = var1 + var11;
      var9[1] = var3 + var13;
      var9[2] = var5 + var11;
      var9[3] = var7 + var13;
      var10[0] = var1 - var11;
      var10[1] = var3 - var13;
      var10[2] = var5 - var11;
      var10[3] = var7 - var13;
   }

   private int computeOffsetCubic(double[] var1, int var2, double[] var3, double[] var4) {
      double var5 = var1[var2];
      double var7 = var1[var2 + 1];
      double var9 = var1[var2 + 2];
      double var11 = var1[var2 + 3];
      double var13 = var1[var2 + 4];
      double var15 = var1[var2 + 5];
      double var17 = var1[var2 + 6];
      double var19 = var1[var2 + 7];
      double var21 = var17 - var13;
      double var23 = var19 - var15;
      double var25 = var9 - var5;
      double var27 = var11 - var7;
      boolean var29 = within(var5, var7, var9, var11, 6.0 * Math.ulp(var11));
      boolean var30 = within(var13, var15, var17, var19, 6.0 * Math.ulp(var19));
      if (var29 && var30) {
         this.getLineOffsets(var5, var7, var17, var19, var3, var4);
         return 4;
      } else {
         if (var29) {
            var25 = var13 - var5;
            var27 = var15 - var7;
         } else if (var30) {
            var21 = var17 - var9;
            var23 = var19 - var11;
         }

         double var31 = var25 * var21 + var27 * var23;
         var31 *= var31;
         double var33 = var25 * var25 + var27 * var27;
         double var35 = var21 * var21 + var23 * var23;
         if (Helpers.within(var31, var33 * var35, 4.0 * Math.ulp(var31))) {
            this.getLineOffsets(var5, var7, var17, var19, var3, var4);
            return 4;
         } else {
            double var37 = (var5 + 3.0 * (var9 + var13) + var17) / 8.0;
            double var39 = (var7 + 3.0 * (var11 + var15) + var19) / 8.0;
            double var41 = var13 + var17 - var5 - var9;
            double var43 = var15 + var19 - var7 - var11;
            computeOffset(var25, var27, this.lineWidth2, this.offset0);
            computeOffset(var41, var43, this.lineWidth2, this.offset1);
            computeOffset(var21, var23, this.lineWidth2, this.offset2);
            double var45 = var5 + this.offset0[0];
            double var47 = var7 + this.offset0[1];
            double var49 = var37 + this.offset1[0];
            double var51 = var39 + this.offset1[1];
            double var53 = var17 + this.offset2[0];
            double var55 = var19 + this.offset2[1];
            double var57 = 4.0 / (3.0 * (var25 * var23 - var27 * var21));
            double var59 = 2.0 * var49 - var45 - var53;
            double var61 = 2.0 * var51 - var47 - var55;
            double var63 = var57 * (var23 * var59 - var21 * var61);
            double var65 = var57 * (var25 * var61 - var27 * var59);
            double var67 = var45 + var63 * var25;
            double var69 = var47 + var63 * var27;
            double var71 = var53 + var65 * var21;
            double var73 = var55 + var65 * var23;
            var3[0] = var45;
            var3[1] = var47;
            var3[2] = var67;
            var3[3] = var69;
            var3[4] = var71;
            var3[5] = var73;
            var3[6] = var53;
            var3[7] = var55;
            var45 = var5 - this.offset0[0];
            var47 = var7 - this.offset0[1];
            var49 -= 2.0 * this.offset1[0];
            var51 -= 2.0 * this.offset1[1];
            var53 = var17 - this.offset2[0];
            var55 = var19 - this.offset2[1];
            var59 = 2.0 * var49 - var45 - var53;
            var61 = 2.0 * var51 - var47 - var55;
            var63 = var57 * (var23 * var59 - var21 * var61);
            var65 = var57 * (var25 * var61 - var27 * var59);
            var67 = var45 + var63 * var25;
            var69 = var47 + var63 * var27;
            var71 = var53 + var65 * var21;
            var73 = var55 + var65 * var23;
            var4[0] = var45;
            var4[1] = var47;
            var4[2] = var67;
            var4[3] = var69;
            var4[4] = var71;
            var4[5] = var73;
            var4[6] = var53;
            var4[7] = var55;
            return 8;
         }
      }
   }

   private int computeOffsetQuad(double[] var1, int var2, double[] var3, double[] var4) {
      double var5 = var1[var2];
      double var7 = var1[var2 + 1];
      double var9 = var1[var2 + 2];
      double var11 = var1[var2 + 3];
      double var13 = var1[var2 + 4];
      double var15 = var1[var2 + 5];
      double var17 = var13 - var9;
      double var19 = var15 - var11;
      double var21 = var9 - var5;
      double var23 = var11 - var7;
      boolean var25 = within(var5, var7, var9, var11, 6.0 * Math.ulp(var11));
      boolean var26 = within(var9, var11, var13, var15, 6.0 * Math.ulp(var15));
      if (!var25 && !var26) {
         double var27 = var21 * var17 + var23 * var19;
         var27 *= var27;
         double var29 = var21 * var21 + var23 * var23;
         double var31 = var17 * var17 + var19 * var19;
         if (Helpers.within(var27, var29 * var31, 4.0 * Math.ulp(var27))) {
            this.getLineOffsets(var5, var7, var13, var15, var3, var4);
            return 4;
         } else {
            computeOffset(var21, var23, this.lineWidth2, this.offset0);
            computeOffset(var17, var19, this.lineWidth2, this.offset1);
            double var33 = var5 + this.offset0[0];
            double var35 = var7 + this.offset0[1];
            double var37 = var13 + this.offset1[0];
            double var39 = var15 + this.offset1[1];
            safeComputeMiter(var33, var35, var33 + var21, var35 + var23, var37, var39, var37 - var17, var39 - var19, var3);
            var3[0] = var33;
            var3[1] = var35;
            var3[4] = var37;
            var3[5] = var39;
            var33 = var5 - this.offset0[0];
            var35 = var7 - this.offset0[1];
            var37 = var13 - this.offset1[0];
            var39 = var15 - this.offset1[1];
            safeComputeMiter(var33, var35, var33 + var21, var35 + var23, var37, var39, var37 - var17, var39 - var19, var4);
            var4[0] = var33;
            var4[1] = var35;
            var4[4] = var37;
            var4[5] = var39;
            return 6;
         }
      } else {
         this.getLineOffsets(var5, var7, var13, var15, var3, var4);
         return 4;
      }
   }

   @Override
   public void curveTo(double var1, double var3, double var5, double var7, double var9, double var11) {
      int var13 = this.cOutCode;
      if (this.clipRect != null) {
         int var14 = Helpers.outcode(var1, var3, this.clipRect);
         int var15 = Helpers.outcode(var5, var7, this.clipRect);
         int var16 = Helpers.outcode(var9, var11, this.clipRect);
         int var17 = var13 | var14 | var15 | var16;
         if (var17 != 0) {
            int var18 = var13 & var14 & var15 & var16;
            if (var18 != 0) {
               this.cOutCode = var16;
               this._moveTo(var9, var11, var13);
               this.opened = true;
               return;
            }

            if (this.subdivide) {
               this.subdivide = false;
               boolean var19 = this.curveSplitter.splitCurve(this.cx0, this.cy0, var1, var3, var5, var7, var9, var11, var17, this);
               this.subdivide = true;
               if (var19) {
                  return;
               }
            }
         }

         this.cOutCode = var16;
      }

      this._curveTo(var1, var3, var5, var7, var9, var11, var13);
   }

   private void _curveTo(double var1, double var3, double var5, double var7, double var9, double var11, int var13) {
      double var14 = var1 - this.cx0;
      double var16 = var3 - this.cy0;
      double var18 = var9 - var5;
      double var20 = var11 - var7;
      if (var14 == 0.0 && var16 == 0.0) {
         var14 = var5 - this.cx0;
         var16 = var7 - this.cy0;
         if (var14 == 0.0 && var16 == 0.0) {
            var14 = var9 - this.cx0;
            var16 = var11 - this.cy0;
         }
      }

      if (var18 == 0.0 && var20 == 0.0) {
         var18 = var9 - var1;
         var20 = var11 - var3;
         if (var18 == 0.0 && var20 == 0.0) {
            var18 = var9 - this.cx0;
            var20 = var11 - this.cy0;
         }
      }

      if (var14 == 0.0 && var16 == 0.0) {
         if (this.clipRect != null) {
            this.cOutCode = var13;
         }

         this.lineTo(this.cx0, this.cy0);
      } else {
         if (Math.abs(var14) < 0.1 && Math.abs(var16) < 0.1) {
            double var22 = Math.sqrt(var14 * var14 + var16 * var16);
            var14 /= var22;
            var16 /= var22;
         }

         if (Math.abs(var18) < 0.1 && Math.abs(var20) < 0.1) {
            double var29 = Math.sqrt(var18 * var18 + var20 * var20);
            var18 /= var29;
            var20 /= var29;
         }

         computeOffset(var14, var16, this.lineWidth2, this.offset0);
         this.drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, var14, var16, this.cmx, this.cmy, this.offset0[0], this.offset0[1], var13);
         int var30 = 0;
         double[] var24 = this.lp;
         double[] var23;
         if (this.monotonize) {
            TransformingPathConsumer2D.CurveBasicMonotonizer var25 = this.rdrCtx.monotonizer.curve(this.cx0, this.cy0, var1, var3, var5, var7, var9, var11);
            var30 = var25.nbSplits;
            var23 = var25.middle;
         } else {
            var23 = var24;
            var24[0] = this.cx0;
            var24[1] = this.cy0;
            var24[2] = var1;
            var24[3] = var3;
            var24[4] = var5;
            var24[5] = var7;
            var24[6] = var9;
            var24[7] = var11;
         }

         double[] var31 = this.rp;
         int var26 = 0;
         int var27 = 0;

         for (byte var28 = 0; var27 <= var30; var28 += 6) {
            var26 = this.computeOffsetCubic(var23, var28, var24, var31);
            this.emitLineTo(var24[0], var24[1]);
            switch (var26) {
               case 4:
                  this.emitLineTo(var24[2], var24[3]);
                  this.emitLineToRev(var31[0], var31[1]);
                  break;
               case 8:
                  this.emitCurveTo(var24[2], var24[3], var24[4], var24[5], var24[6], var24[7]);
                  this.emitCurveToRev(var31[0], var31[1], var31[2], var31[3], var31[4], var31[5]);
            }

            this.emitLineToRev(var31[var26 - 2], var31[var26 - 1]);
            var27++;
         }

         this.prev = 1;
         this.cx0 = var9;
         this.cy0 = var11;
         this.cdx = var18;
         this.cdy = var20;
         this.cmx = (var24[var26 - 2] - var31[var26 - 2]) / 2.0;
         this.cmy = (var24[var26 - 1] - var31[var26 - 1]) / 2.0;
      }
   }

   @Override
   public void quadTo(double var1, double var3, double var5, double var7) {
      int var9 = this.cOutCode;
      if (this.clipRect != null) {
         int var10 = Helpers.outcode(var1, var3, this.clipRect);
         int var11 = Helpers.outcode(var5, var7, this.clipRect);
         int var12 = var9 | var10 | var11;
         if (var12 != 0) {
            int var13 = var9 & var10 & var11;
            if (var13 != 0) {
               this.cOutCode = var11;
               this._moveTo(var5, var7, var9);
               this.opened = true;
               return;
            }

            if (this.subdivide) {
               this.subdivide = false;
               boolean var14 = this.curveSplitter.splitQuad(this.cx0, this.cy0, var1, var3, var5, var7, var12, this);
               this.subdivide = true;
               if (var14) {
                  return;
               }
            }
         }

         this.cOutCode = var11;
      }

      this._quadTo(var1, var3, var5, var7, var9);
   }

   private void _quadTo(double var1, double var3, double var5, double var7, int var9) {
      double var10 = var1 - this.cx0;
      double var12 = var3 - this.cy0;
      double var14 = var5 - var1;
      double var16 = var7 - var3;
      if (var10 == 0.0 && var12 == 0.0 || var14 == 0.0 && var16 == 0.0) {
         var10 = var14 = var5 - this.cx0;
         var12 = var16 = var7 - this.cy0;
      }

      if (var10 == 0.0 && var12 == 0.0) {
         if (this.clipRect != null) {
            this.cOutCode = var9;
         }

         this.lineTo(this.cx0, this.cy0);
      } else {
         if (Math.abs(var10) < 0.1 && Math.abs(var12) < 0.1) {
            double var18 = Math.sqrt(var10 * var10 + var12 * var12);
            var10 /= var18;
            var12 /= var18;
         }

         if (Math.abs(var14) < 0.1 && Math.abs(var16) < 0.1) {
            double var25 = Math.sqrt(var14 * var14 + var16 * var16);
            var14 /= var25;
            var16 /= var25;
         }

         computeOffset(var10, var12, this.lineWidth2, this.offset0);
         this.drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, var10, var12, this.cmx, this.cmy, this.offset0[0], this.offset0[1], var9);
         int var26 = 0;
         double[] var20 = this.lp;
         double[] var19;
         if (this.monotonize) {
            TransformingPathConsumer2D.CurveBasicMonotonizer var21 = this.rdrCtx.monotonizer.quad(this.cx0, this.cy0, var1, var3, var5, var7);
            var26 = var21.nbSplits;
            var19 = var21.middle;
         } else {
            var19 = var20;
            var20[0] = this.cx0;
            var20[1] = this.cy0;
            var20[2] = var1;
            var20[3] = var3;
            var20[4] = var5;
            var20[5] = var7;
         }

         double[] var27 = this.rp;
         int var22 = 0;
         int var23 = 0;

         for (byte var24 = 0; var23 <= var26; var24 += 4) {
            var22 = this.computeOffsetQuad(var19, var24, var20, var27);
            this.emitLineTo(var20[0], var20[1]);
            switch (var22) {
               case 4:
                  this.emitLineTo(var20[2], var20[3]);
                  this.emitLineToRev(var27[0], var27[1]);
                  break;
               case 6:
                  this.emitQuadTo(var20[2], var20[3], var20[4], var20[5]);
                  this.emitQuadToRev(var27[0], var27[1], var27[2], var27[3]);
            }

            this.emitLineToRev(var27[var22 - 2], var27[var22 - 1]);
            var23++;
         }

         this.prev = 1;
         this.cx0 = var5;
         this.cy0 = var7;
         this.cdx = var14;
         this.cdy = var16;
         this.cmx = (var20[var22 - 2] - var27[var22 - 2]) / 2.0;
         this.cmy = (var20[var22 - 1] - var27[var22 - 1]) / 2.0;
      }
   }
}
