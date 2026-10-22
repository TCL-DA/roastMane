package com.sun.marlin;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.transform.BaseTransform;
import java.util.Arrays;

public final class TransformingPathConsumer2D {
   static final double CLIP_RECT_PADDING = 0.25;
   private final RendererContext rdrCtx;
   private final TransformingPathConsumer2D.ClosedPathDetector cpDetector;
   private final TransformingPathConsumer2D.PathClipFilter pathClipper;
   private final TransformingPathConsumer2D.Path2DWrapper wp_Path2DWrapper = new TransformingPathConsumer2D.Path2DWrapper();
   private final TransformingPathConsumer2D.DeltaScaleFilter dt_DeltaScaleFilter = new TransformingPathConsumer2D.DeltaScaleFilter();
   private final TransformingPathConsumer2D.DeltaTransformFilter dt_DeltaTransformFilter = new TransformingPathConsumer2D.DeltaTransformFilter();
   private final TransformingPathConsumer2D.DeltaScaleFilter iv_DeltaScaleFilter = new TransformingPathConsumer2D.DeltaScaleFilter();
   private final TransformingPathConsumer2D.DeltaTransformFilter iv_DeltaTransformFilter = new TransformingPathConsumer2D.DeltaTransformFilter();
   private final TransformingPathConsumer2D.PathTracer tracerInput = new TransformingPathConsumer2D.PathTracer("[Input]");
   private final TransformingPathConsumer2D.PathTracer tracerCPDetector = new TransformingPathConsumer2D.PathTracer("ClosedPathDetector");
   private final TransformingPathConsumer2D.PathTracer tracerFiller = new TransformingPathConsumer2D.PathTracer("Filler");
   private final TransformingPathConsumer2D.PathTracer tracerStroker = new TransformingPathConsumer2D.PathTracer("Stroker");
   private final TransformingPathConsumer2D.PathTracer tracerDasher = new TransformingPathConsumer2D.PathTracer("Dasher");

   TransformingPathConsumer2D(RendererContext var1) {
      this.rdrCtx = var1;
      this.cpDetector = new TransformingPathConsumer2D.ClosedPathDetector(var1);
      this.pathClipper = new TransformingPathConsumer2D.PathClipFilter(var1);
   }

   public DPathConsumer2D wrapPath2D(Path2D var1) {
      return this.wp_Path2DWrapper.init(var1);
   }

   public DPathConsumer2D traceInput(DPathConsumer2D var1) {
      return this.tracerInput.init(var1);
   }

   public DPathConsumer2D traceClosedPathDetector(DPathConsumer2D var1) {
      return this.tracerCPDetector.init(var1);
   }

   public DPathConsumer2D traceFiller(DPathConsumer2D var1) {
      return this.tracerFiller.init(var1);
   }

   public DPathConsumer2D traceStroker(DPathConsumer2D var1) {
      return this.tracerStroker.init(var1);
   }

   public DPathConsumer2D traceDasher(DPathConsumer2D var1) {
      return this.tracerDasher.init(var1);
   }

   public DPathConsumer2D detectClosedPath(DPathConsumer2D var1) {
      return this.cpDetector.init(var1);
   }

   public DPathConsumer2D pathClipper(DPathConsumer2D var1) {
      return this.pathClipper.init(var1);
   }

   public DPathConsumer2D deltaTransformConsumer(DPathConsumer2D var1, BaseTransform var2) {
      if (var2 == null) {
         return var1;
      } else {
         double var3 = var2.getMxx();
         double var5 = var2.getMxy();
         double var7 = var2.getMyx();
         double var9 = var2.getMyy();
         if (var5 == 0.0 && var7 == 0.0) {
            if (var3 == 1.0 && var9 == 1.0) {
               return var1;
            } else {
               if (this.rdrCtx.doClip) {
                  this.rdrCtx.clipInvScale = adjustClipScale(this.rdrCtx.clipRect, var3, var9);
               }

               return this.dt_DeltaScaleFilter.init(var1, var3, var9);
            }
         } else {
            if (this.rdrCtx.doClip) {
               this.rdrCtx.clipInvScale = adjustClipInverseDelta(this.rdrCtx.clipRect, var3, var5, var7, var9);
            }

            return this.dt_DeltaTransformFilter.init(var1, var3, var5, var7, var9);
         }
      }
   }

   private static double adjustClipScale(double[] var0, double var1, double var3) {
      double var5 = 1.0 / var3;
      var0[0] *= var5;
      var0[1] *= var5;
      if (var0[1] < var0[0]) {
         double var7 = var0[0];
         var0[0] = var0[1];
         var0[1] = var7;
      }

      double var11 = 1.0 / var1;
      var0[2] *= var11;
      var0[3] *= var11;
      if (var0[3] < var0[2]) {
         double var9 = var0[2];
         var0[2] = var0[3];
         var0[3] = var9;
      }

      if (MarlinConst.DO_LOG_CLIP) {
         MarlinUtils.logInfo("clipRect (ClipScale): " + Arrays.toString(var0));
      }

      return 0.5 * (Math.abs(var11) + Math.abs(var5));
   }

   private static double adjustClipInverseDelta(double[] var0, double var1, double var3, double var5, double var7) {
      double var9 = var1 * var7 - var3 * var5;
      double var11 = var7 / var9;
      double var13 = -var3 / var9;
      double var15 = -var5 / var9;
      double var17 = var1 / var9;
      double var27 = var0[2] * var11 + var0[0] * var13;
      double var29 = var0[2] * var15 + var0[0] * var17;
      double var21 = var27;
      double var19 = var27;
      double var25 = var29;
      double var23 = var29;
      var27 = var0[3] * var11 + var0[0] * var13;
      var29 = var0[3] * var15 + var0[0] * var17;
      if (var27 < var27) {
         var19 = var27;
      } else if (var27 > var27) {
         var21 = var27;
      }

      if (var29 < var29) {
         var23 = var29;
      } else if (var29 > var29) {
         var25 = var29;
      }

      var27 = var0[2] * var11 + var0[1] * var13;
      var29 = var0[2] * var15 + var0[1] * var17;
      if (var27 < var19) {
         var19 = var27;
      } else if (var27 > var21) {
         var21 = var27;
      }

      if (var29 < var23) {
         var23 = var29;
      } else if (var29 > var25) {
         var25 = var29;
      }

      var27 = var0[3] * var11 + var0[1] * var13;
      var29 = var0[3] * var15 + var0[1] * var17;
      if (var27 < var19) {
         var19 = var27;
      } else if (var27 > var21) {
         var21 = var27;
      }

      if (var29 < var23) {
         var23 = var29;
      } else if (var29 > var25) {
         var25 = var29;
      }

      var0[0] = var23;
      var0[1] = var25;
      var0[2] = var19;
      var0[3] = var21;
      if (MarlinConst.DO_LOG_CLIP) {
         MarlinUtils.logInfo("clipRect (ClipInverseDelta): " + Arrays.toString(var0));
      }

      double var31 = Math.sqrt(var11 * var11 + var13 * var13);
      double var33 = Math.sqrt(var15 * var15 + var17 * var17);
      return 0.5 * (var31 + var33);
   }

   public DPathConsumer2D inverseDeltaTransformConsumer(DPathConsumer2D var1, BaseTransform var2) {
      if (var2 == null) {
         return var1;
      } else {
         double var3 = var2.getMxx();
         double var5 = var2.getMxy();
         double var7 = var2.getMyx();
         double var9 = var2.getMyy();
         if (var5 != 0.0 || var7 != 0.0) {
            double var11 = var3 * var9 - var5 * var7;
            return this.iv_DeltaTransformFilter.init(var1, var9 / var11, -var5 / var11, -var7 / var11, var3 / var11);
         } else {
            return (DPathConsumer2D)(var3 == 1.0 && var9 == 1.0 ? var1 : this.iv_DeltaScaleFilter.init(var1, 1.0 / var3, 1.0 / var9));
         }
      }
   }

   static final class ClosedPathDetector implements DPathConsumer2D {
      private final RendererContext rdrCtx;
      private final Helpers.PolyStack stack;
      private DPathConsumer2D out;

      ClosedPathDetector(RendererContext var1) {
         this.rdrCtx = var1;
         this.stack = var1.stats != null
            ? new Helpers.PolyStack(
               var1,
               var1.stats.stat_cpd_polystack_types,
               var1.stats.stat_cpd_polystack_curves,
               var1.stats.hist_cpd_polystack_curves,
               var1.stats.stat_array_cpd_polystack_curves,
               var1.stats.stat_array_cpd_polystack_types
            )
            : new Helpers.PolyStack(var1);
      }

      TransformingPathConsumer2D.ClosedPathDetector init(DPathConsumer2D var1) {
         this.out = var1;
         return this;
      }

      void dispose() {
         this.stack.dispose();
      }

      @Override
      public void pathDone() {
         this.finish(false);
         this.out.pathDone();
         this.dispose();
      }

      @Override
      public void closePath() {
         this.finish(true);
         this.out.closePath();
      }

      @Override
      public void moveTo(double var1, double var3) {
         this.finish(false);
         this.out.moveTo(var1, var3);
      }

      private void finish(boolean var1) {
         this.rdrCtx.closedPath = var1;
         this.stack.pullAll(this.out);
      }

      @Override
      public void lineTo(double var1, double var3) {
         this.stack.pushLine(var1, var3);
      }

      @Override
      public void curveTo(double var1, double var3, double var5, double var7, double var9, double var11) {
         this.stack.pushCubic(var9, var11, var5, var7, var1, var3);
      }

      @Override
      public void quadTo(double var1, double var3, double var5, double var7) {
         this.stack.pushQuad(var5, var7, var1, var3);
      }
   }

   public static final class CurveBasicMonotonizer {
      private static final int MAX_N_CURVES = 11;
      private double lw2;
      int nbSplits;
      final double[] middle = new double[68];
      private final double[] subdivTs = new double[10];
      private final Curve curve;

      CurveBasicMonotonizer(RendererContext var1) {
         this.curve = var1.curve;
      }

      public void init(double var1) {
         this.lw2 = var1 * var1 / 4.0;
      }

      TransformingPathConsumer2D.CurveBasicMonotonizer curve(
         double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15
      ) {
         double[] var17 = this.middle;
         var17[0] = var1;
         var17[1] = var3;
         var17[2] = var5;
         var17[3] = var7;
         var17[4] = var9;
         var17[5] = var11;
         var17[6] = var13;
         var17[7] = var15;
         double[] var18 = this.subdivTs;
         int var19 = Helpers.findSubdivPoints(this.curve, var17, var18, 8, this.lw2);
         double var20 = 0.0;
         int var22 = 0;

         for (byte var23 = 0; var22 < var19; var23 += 6) {
            double var24 = var18[var22];
            Helpers.subdivideCubicAt((var24 - var20) / (1.0 - var20), var17, var23, var17, var23, var23 + 6);
            var20 = var24;
            var22++;
         }

         this.nbSplits = var19;
         return this;
      }

      TransformingPathConsumer2D.CurveBasicMonotonizer quad(double var1, double var3, double var5, double var7, double var9, double var11) {
         double[] var13 = this.middle;
         var13[0] = var1;
         var13[1] = var3;
         var13[2] = var5;
         var13[3] = var7;
         var13[4] = var9;
         var13[5] = var11;
         double[] var14 = this.subdivTs;
         int var15 = Helpers.findSubdivPoints(this.curve, var13, var14, 6, this.lw2);
         double var16 = 0.0;
         int var18 = 0;

         for (byte var19 = 0; var18 < var15; var19 += 4) {
            double var20 = var14[var18];
            Helpers.subdivideQuadAt((var20 - var16) / (1.0 - var16), var13, var19, var13, var19, var19 + 4);
            var16 = var20;
            var18++;
         }

         this.nbSplits = var15;
         return this;
      }
   }

   static final class CurveClipSplitter {
      static final double LEN_TH = (double)MarlinProperties.getSubdividerMinLength();
      static final boolean DO_CHECK_LENGTH = LEN_TH > 0.0;
      private static final boolean TRACE = false;
      private static final int MAX_N_CURVES = 12;
      private final RendererContext rdrCtx;
      private double minLength;
      final double[] clipRect;
      final double[] clipRectPad = new double[4];
      private boolean init_clipRectPad = false;
      final double[] middle = new double[98];
      private final double[] subdivTs = new double[12];
      private final Curve curve;

      CurveClipSplitter(RendererContext var1) {
         this.rdrCtx = var1;
         this.clipRect = var1.clipRect;
         this.curve = var1.curve;
      }

      void init() {
         this.init_clipRectPad = true;
         if (DO_CHECK_LENGTH) {
            this.minLength = this.rdrCtx.clipInvScale == 0.0 ? LEN_TH : LEN_TH * this.rdrCtx.clipInvScale;
            if (MarlinConst.DO_LOG_CLIP) {
               MarlinUtils.logInfo("CurveClipSplitter.minLength = " + this.minLength);
            }
         }
      }

      private void initPaddedClip() {
         double[] var1 = this.clipRect;
         double[] var2 = this.clipRectPad;
         var2[0] = var1[0] - 0.25;
         var2[1] = var1[1] + 0.25;
         var2[2] = var1[2] - 0.25;
         var2[3] = var1[3] + 0.25;
      }

      boolean splitLine(double var1, double var3, double var5, double var7, int var9, DPathConsumer2D var10) {
         if (DO_CHECK_LENGTH && Helpers.fastLineLen(var1, var3, var5, var7) <= this.minLength) {
            return false;
         } else {
            double[] var11 = this.middle;
            var11[0] = var1;
            var11[1] = var3;
            var11[2] = var5;
            var11[3] = var7;
            return this.subdivideAtIntersections(4, var9, var10);
         }
      }

      boolean splitQuad(double var1, double var3, double var5, double var7, double var9, double var11, int var13, DPathConsumer2D var14) {
         if (DO_CHECK_LENGTH && Helpers.fastQuadLen(var1, var3, var5, var7, var9, var11) <= this.minLength) {
            return false;
         } else {
            double[] var15 = this.middle;
            var15[0] = var1;
            var15[1] = var3;
            var15[2] = var5;
            var15[3] = var7;
            var15[4] = var9;
            var15[5] = var11;
            return this.subdivideAtIntersections(6, var13, var14);
         }
      }

      boolean splitCurve(
         double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, int var17, DPathConsumer2D var18
      ) {
         if (DO_CHECK_LENGTH && Helpers.fastCurvelen(var1, var3, var5, var7, var9, var11, var13, var15) <= this.minLength) {
            return false;
         } else {
            double[] var19 = this.middle;
            var19[0] = var1;
            var19[1] = var3;
            var19[2] = var5;
            var19[3] = var7;
            var19[4] = var9;
            var19[5] = var11;
            var19[6] = var13;
            var19[7] = var15;
            return this.subdivideAtIntersections(8, var17, var18);
         }
      }

      private boolean subdivideAtIntersections(int var1, int var2, DPathConsumer2D var3) {
         double[] var4 = this.middle;
         double[] var5 = this.subdivTs;
         if (this.init_clipRectPad) {
            this.init_clipRectPad = false;
            this.initPaddedClip();
         }

         int var6 = Helpers.findClipPoints(this.curve, var4, var5, var1, var2, this.clipRectPad);
         if (var6 == 0) {
            return false;
         } else {
            double var7 = 0.0;
            int var9 = 0;

            for (int var10 = 0; var9 < var6; var10 += var1) {
               double var11 = var5[var9];
               Helpers.subdivideAt((var11 - var7) / (1.0 - var7), var4, var10, var4, var10, var1);
               var7 = var11;
               var9++;
            }

            var9 = 0;

            for (int var14 = 0; var9 <= var6; var14 += var1) {
               emitCurrent(var1, var4, var14, var3);
               var9++;
            }

            return true;
         }
      }

      static void emitCurrent(int var0, double[] var1, int var2, DPathConsumer2D var3) {
         if (var0 == 8) {
            var3.curveTo(var1[var2 + 2], var1[var2 + 3], var1[var2 + 4], var1[var2 + 5], var1[var2 + 6], var1[var2 + 7]);
         } else if (var0 == 4) {
            var3.lineTo(var1[var2 + 2], var1[var2 + 3]);
         } else {
            var3.quadTo(var1[var2 + 2], var1[var2 + 3], var1[var2 + 4], var1[var2 + 5]);
         }
      }
   }

   static final class DeltaScaleFilter implements DPathConsumer2D {
      private DPathConsumer2D out;
      private double sx;
      private double sy;

      TransformingPathConsumer2D.DeltaScaleFilter init(DPathConsumer2D var1, double var2, double var4) {
         this.out = var1;
         this.sx = var2;
         this.sy = var4;
         return this;
      }

      @Override
      public void moveTo(double var1, double var3) {
         this.out.moveTo(var1 * this.sx, var3 * this.sy);
      }

      @Override
      public void lineTo(double var1, double var3) {
         this.out.lineTo(var1 * this.sx, var3 * this.sy);
      }

      @Override
      public void quadTo(double var1, double var3, double var5, double var7) {
         this.out.quadTo(var1 * this.sx, var3 * this.sy, var5 * this.sx, var7 * this.sy);
      }

      @Override
      public void curveTo(double var1, double var3, double var5, double var7, double var9, double var11) {
         this.out.curveTo(var1 * this.sx, var3 * this.sy, var5 * this.sx, var7 * this.sy, var9 * this.sx, var11 * this.sy);
      }

      @Override
      public void closePath() {
         this.out.closePath();
      }

      @Override
      public void pathDone() {
         this.out.pathDone();
      }
   }

   static final class DeltaTransformFilter implements DPathConsumer2D {
      private DPathConsumer2D out;
      private double mxx;
      private double mxy;
      private double myx;
      private double myy;

      TransformingPathConsumer2D.DeltaTransformFilter init(DPathConsumer2D var1, double var2, double var4, double var6, double var8) {
         this.out = var1;
         this.mxx = var2;
         this.mxy = var4;
         this.myx = var6;
         this.myy = var8;
         return this;
      }

      @Override
      public void moveTo(double var1, double var3) {
         this.out.moveTo(var1 * this.mxx + var3 * this.mxy, var1 * this.myx + var3 * this.myy);
      }

      @Override
      public void lineTo(double var1, double var3) {
         this.out.lineTo(var1 * this.mxx + var3 * this.mxy, var1 * this.myx + var3 * this.myy);
      }

      @Override
      public void quadTo(double var1, double var3, double var5, double var7) {
         this.out
            .quadTo(var1 * this.mxx + var3 * this.mxy, var1 * this.myx + var3 * this.myy, var5 * this.mxx + var7 * this.mxy, var5 * this.myx + var7 * this.myy);
      }

      @Override
      public void curveTo(double var1, double var3, double var5, double var7, double var9, double var11) {
         this.out
            .curveTo(
               var1 * this.mxx + var3 * this.mxy,
               var1 * this.myx + var3 * this.myy,
               var5 * this.mxx + var7 * this.mxy,
               var5 * this.myx + var7 * this.myy,
               var9 * this.mxx + var11 * this.mxy,
               var9 * this.myx + var11 * this.myy
            );
      }

      @Override
      public void closePath() {
         this.out.closePath();
      }

      @Override
      public void pathDone() {
         this.out.pathDone();
      }
   }

   static final class Path2DWrapper implements DPathConsumer2D {
      private Path2D p2d;

      TransformingPathConsumer2D.Path2DWrapper init(Path2D var1) {
         this.p2d = var1;
         return this;
      }

      @Override
      public void moveTo(double var1, double var3) {
         this.p2d.moveTo((float)var1, (float)var3);
      }

      @Override
      public void lineTo(double var1, double var3) {
         this.p2d.lineTo((float)var1, (float)var3);
      }

      @Override
      public void closePath() {
         this.p2d.closePath();
      }

      @Override
      public void pathDone() {
      }

      @Override
      public void curveTo(double var1, double var3, double var5, double var7, double var9, double var11) {
         this.p2d.curveTo((float)var1, (float)var3, (float)var5, (float)var7, (float)var9, (float)var11);
      }

      @Override
      public void quadTo(double var1, double var3, double var5, double var7) {
         this.p2d.quadTo((float)var1, (float)var3, (float)var5, (float)var7);
      }
   }

   static final class PathClipFilter implements DPathConsumer2D {
      private DPathConsumer2D out;
      private final double[] clipRect;
      private final double[] corners = new double[8];
      private boolean init_corners = false;
      private final Helpers.IndexStack stack;
      private int cOutCode = 0;
      private int gOutCode = 15;
      private boolean outside = false;
      private double sx0;
      private double sy0;
      private double cx0;
      private double cy0;
      private double cox0;
      private double coy0;
      private boolean subdivide = MarlinConst.DO_CLIP_SUBDIVIDER;
      private final TransformingPathConsumer2D.CurveClipSplitter curveSplitter;

      PathClipFilter(RendererContext var1) {
         this.clipRect = var1.clipRect;
         this.curveSplitter = var1.curveClipSplitter;
         this.stack = var1.stats != null
            ? new Helpers.IndexStack(
               var1, var1.stats.stat_pcf_idxstack_indices, var1.stats.hist_pcf_idxstack_indices, var1.stats.stat_array_pcf_idxstack_indices
            )
            : new Helpers.IndexStack(var1);
      }

      TransformingPathConsumer2D.PathClipFilter init(DPathConsumer2D var1) {
         this.out = var1;
         if (MarlinConst.DO_CLIP_SUBDIVIDER) {
            this.curveSplitter.init();
         }

         this.init_corners = true;
         this.gOutCode = 15;
         return this;
      }

      void dispose() {
         this.stack.dispose();
      }

      private void finishPath() {
         if (this.outside) {
            if (this.gOutCode == 0) {
               this.finish();
            } else {
               this.outside = false;
               this.stack.reset();
            }
         }
      }

      private void finish() {
         this.outside = false;
         if (!this.stack.isEmpty()) {
            if (this.init_corners) {
               this.init_corners = false;
               double[] var1 = this.corners;
               double[] var2 = this.clipRect;
               var1[0] = var2[2];
               var1[1] = var2[0];
               var1[2] = var2[2];
               var1[3] = var2[1];
               var1[4] = var2[3];
               var1[5] = var2[0];
               var1[6] = var2[3];
               var1[7] = var2[1];
            }

            this.stack.pullAll(this.corners, this.out);
         }

         this.out.lineTo(this.cox0, this.coy0);
         this.cx0 = this.cox0;
         this.cy0 = this.coy0;
      }

      @Override
      public void pathDone() {
         this.finishPath();
         this.out.pathDone();
         this.dispose();
      }

      @Override
      public void closePath() {
         this.finishPath();
         this.out.closePath();
         this.cOutCode = Helpers.outcode(this.sx0, this.sy0, this.clipRect);
         this.cx0 = this.sx0;
         this.cy0 = this.sy0;
      }

      @Override
      public void moveTo(double var1, double var3) {
         this.finishPath();
         this.out.moveTo(var1, var3);
         this.cOutCode = Helpers.outcode(var1, var3, this.clipRect);
         this.cx0 = var1;
         this.cy0 = var3;
         this.sx0 = var1;
         this.sy0 = var3;
      }

      @Override
      public void lineTo(double var1, double var3) {
         int var5 = this.cOutCode;
         int var6 = Helpers.outcode(var1, var3, this.clipRect);
         int var7 = var5 | var6;
         if (var7 != 0) {
            int var8 = var5 & var6;
            if (var8 != 0) {
               this.cOutCode = var6;
               this.gOutCode &= var8;
               this.outside = true;
               this.cox0 = var1;
               this.coy0 = var3;
               this.clip(var8, var5, var6);
               return;
            }

            if (this.subdivide) {
               this.subdivide = false;
               boolean var9;
               if (this.outside) {
                  var9 = this.curveSplitter.splitLine(this.cox0, this.coy0, var1, var3, var7, this);
               } else {
                  var9 = this.curveSplitter.splitLine(this.cx0, this.cy0, var1, var3, var7, this);
               }

               this.subdivide = true;
               if (var9) {
                  return;
               }
            }
         }

         this.cOutCode = var6;
         this.gOutCode = 0;
         if (this.outside) {
            this.finish();
         }

         this.out.lineTo(var1, var3);
         this.cx0 = var1;
         this.cy0 = var3;
      }

      private void clip(int var1, int var2, int var3) {
         if (var2 != var3 && (var1 & 12) != 0) {
            int var4 = var2 | var3;
            int var5 = var4 & 3;
            int var6 = var4 & 12;
            int var7 = var6 == 4 ? 0 : 2;
            switch (var5) {
               case 1:
                  this.stack.push(var7);
                  return;
               case 2:
                  this.stack.push(var7 + 1);
                  return;
               default:
                  if ((var2 & 1) != 0) {
                     this.stack.push(var7);
                     this.stack.push(var7 + 1);
                  } else {
                     this.stack.push(var7 + 1);
                     this.stack.push(var7);
                  }
            }
         }
      }

      @Override
      public void curveTo(double var1, double var3, double var5, double var7, double var9, double var11) {
         int var13 = this.cOutCode;
         int var14 = Helpers.outcode(var1, var3, this.clipRect);
         int var15 = Helpers.outcode(var5, var7, this.clipRect);
         int var16 = Helpers.outcode(var9, var11, this.clipRect);
         int var17 = var13 | var14 | var15 | var16;
         if (var17 != 0) {
            int var18 = var13 & var14 & var15 & var16;
            if (var18 != 0) {
               this.cOutCode = var16;
               this.gOutCode &= var18;
               this.outside = true;
               this.cox0 = var9;
               this.coy0 = var11;
               this.clip(var18, var13, var16);
               return;
            }

            if (this.subdivide) {
               this.subdivide = false;
               boolean var19;
               if (this.outside) {
                  var19 = this.curveSplitter.splitCurve(this.cox0, this.coy0, var1, var3, var5, var7, var9, var11, var17, this);
               } else {
                  var19 = this.curveSplitter.splitCurve(this.cx0, this.cy0, var1, var3, var5, var7, var9, var11, var17, this);
               }

               this.subdivide = true;
               if (var19) {
                  return;
               }
            }
         }

         this.cOutCode = var16;
         this.gOutCode = 0;
         if (this.outside) {
            this.finish();
         }

         this.out.curveTo(var1, var3, var5, var7, var9, var11);
         this.cx0 = var9;
         this.cy0 = var11;
      }

      @Override
      public void quadTo(double var1, double var3, double var5, double var7) {
         int var9 = this.cOutCode;
         int var10 = Helpers.outcode(var1, var3, this.clipRect);
         int var11 = Helpers.outcode(var5, var7, this.clipRect);
         int var12 = var9 | var10 | var11;
         if (var12 != 0) {
            int var13 = var9 & var10 & var11;
            if (var13 != 0) {
               this.cOutCode = var11;
               this.gOutCode &= var13;
               this.outside = true;
               this.cox0 = var5;
               this.coy0 = var7;
               this.clip(var13, var9, var11);
               return;
            }

            if (this.subdivide) {
               this.subdivide = false;
               boolean var14;
               if (this.outside) {
                  var14 = this.curveSplitter.splitQuad(this.cox0, this.coy0, var1, var3, var5, var7, var12, this);
               } else {
                  var14 = this.curveSplitter.splitQuad(this.cx0, this.cy0, var1, var3, var5, var7, var12, this);
               }

               this.subdivide = true;
               if (var14) {
                  return;
               }
            }
         }

         this.cOutCode = var11;
         this.gOutCode = 0;
         if (this.outside) {
            this.finish();
         }

         this.out.quadTo(var1, var3, var5, var7);
         this.cx0 = var5;
         this.cy0 = var7;
      }
   }

   static final class PathTracer implements DPathConsumer2D {
      private final String prefix;
      private DPathConsumer2D out;

      PathTracer(String var1) {
         this.prefix = var1 + ": ";
      }

      TransformingPathConsumer2D.PathTracer init(DPathConsumer2D var1) {
         this.out = var1;
         return this;
      }

      @Override
      public void moveTo(double var1, double var3) {
         this.log("p.moveTo(" + var1 + ", " + var3 + ");");
         this.out.moveTo(var1, var3);
      }

      @Override
      public void lineTo(double var1, double var3) {
         this.log("p.lineTo(" + var1 + ", " + var3 + ");");
         this.out.lineTo(var1, var3);
      }

      @Override
      public void curveTo(double var1, double var3, double var5, double var7, double var9, double var11) {
         this.log("p.curveTo(" + var1 + ", " + var3 + ", " + var5 + ", " + var7 + ", " + var9 + ", " + var11 + ");");
         this.out.curveTo(var1, var3, var5, var7, var9, var11);
      }

      @Override
      public void quadTo(double var1, double var3, double var5, double var7) {
         this.log("p.quadTo(" + var1 + ", " + var3 + ", " + var5 + ", " + var7 + ");");
         this.out.quadTo(var1, var3, var5, var7);
      }

      @Override
      public void closePath() {
         this.log("p.closePath();");
         this.out.closePath();
      }

      @Override
      public void pathDone() {
         this.log("p.pathDone();");
         this.out.pathDone();
      }

      private void log(String var1) {
         MarlinUtils.logInfo(this.prefix + var1);
      }
   }
}
