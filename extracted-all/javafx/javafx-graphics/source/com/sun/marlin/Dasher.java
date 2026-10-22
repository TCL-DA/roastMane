package com.sun.marlin;

public final class Dasher implements DPathConsumer2D, MarlinConst {
   static final int REC_LIMIT = 16;
   static final double CURVE_LEN_ERR = (double)MarlinProperties.getCurveLengthError();
   static final double MIN_T_INC = 1.5258789E-5F;
   static final double EPS = 1.0E-6;
   static final double MAX_CYCLES = 1.6E7;
   private DPathConsumer2D out;
   private double[] dash;
   private int dashLen;
   private double startPhase;
   private boolean startDashOn;
   private int startIdx;
   private boolean starting;
   private boolean needsMoveTo;
   private int idx;
   private boolean dashOn;
   private double phase;
   private double sx0;
   private double sy0;
   private double cx0;
   private double cy0;
   private final double[] curCurvepts;
   final RendererContext rdrCtx;
   boolean recycleDashes;
   private double[] firstSegmentsBuffer;
   private int firstSegidx;
   final DoubleArrayCache.Reference dashes_ref;
   final DoubleArrayCache.Reference firstSegmentsBuffer_ref;
   private double[] clipRect;
   private int cOutCode = 0;
   private boolean subdivide = DO_CLIP_SUBDIVIDER;
   private final Dasher.LengthIterator li = new Dasher.LengthIterator();
   private final TransformingPathConsumer2D.CurveClipSplitter curveSplitter;
   private double cycleLen;
   private boolean outside;
   private double totalSkipLen;

   Dasher(RendererContext var1) {
      this.rdrCtx = var1;
      this.dashes_ref = var1.newDirtyDoubleArrayRef(256);
      this.firstSegmentsBuffer_ref = var1.newDirtyDoubleArrayRef(256);
      this.firstSegmentsBuffer = this.firstSegmentsBuffer_ref.initial;
      this.curCurvepts = new double[16];
      this.curveSplitter = var1.curveClipSplitter;
   }

   public Dasher init(DPathConsumer2D var1, double[] var2, int var3, double var4, boolean var6) {
      this.out = var1;
      int var7 = 0;
      this.dashOn = true;
      double var8 = 0.0;

      for (int var10 = 0; var10 < var3; var10++) {
         var8 += var2[var10];
      }

      this.cycleLen = var8;
      double var15 = var4 / var8;
      if (var4 < 0.0) {
         if (-var15 >= 1.6E7) {
            var4 = 0.0;
         } else {
            int var12 = FloatMath.floor_int(-var15);
            if ((var12 & var3 & 1) != 0) {
               this.dashOn = !this.dashOn;
            }

            for (var4 += (double)var12 * var8; var4 < 0.0; this.dashOn = !this.dashOn) {
               if (--var7 < 0) {
                  var7 = var3 - 1;
               }

               var4 += var2[var7];
            }
         }
      } else if (var4 > 0.0) {
         if (var15 >= 1.6E7) {
            var4 = 0.0;
         } else {
            int var16 = FloatMath.floor_int(var15);
            if ((var16 & var3 & 1) != 0) {
               this.dashOn = !this.dashOn;
            }

            double var13;
            for (var4 -= (double)var16 * var8; var4 >= (var13 = var2[var7]); this.dashOn = !this.dashOn) {
               var4 -= var13;
               var7 = (var7 + 1) % var3;
            }
         }
      }

      this.dash = var2;
      this.dashLen = var3;
      this.phase = var4;
      this.startPhase = var4;
      this.startDashOn = this.dashOn;
      this.startIdx = var7;
      this.starting = true;
      this.needsMoveTo = false;
      this.firstSegidx = 0;
      this.recycleDashes = var6;
      if (this.rdrCtx.doClip) {
         this.clipRect = this.rdrCtx.clipRect;
      } else {
         this.clipRect = null;
         this.cOutCode = 0;
      }

      return this;
   }

   void dispose() {
      if (this.recycleDashes) {
         this.dash = this.dashes_ref.putArray(this.dash);
      }

      this.firstSegmentsBuffer = this.firstSegmentsBuffer_ref.putArray(this.firstSegmentsBuffer);
   }

   public double[] copyDashArray(float[] var1) {
      int var2 = var1.length;
      double[] var3;
      if (var2 <= 256) {
         var3 = this.dashes_ref.initial;
      } else {
         if (DO_STATS) {
            this.rdrCtx.stats.stat_array_dasher_dasher.add(var2);
         }

         var3 = this.dashes_ref.getArray(var2);
      }

      for (int var4 = 0; var4 < var2; var4++) {
         var3[var4] = (double)var1[var4];
      }

      return var3;
   }

   @Override
   public void moveTo(double var1, double var3) {
      if (this.firstSegidx != 0) {
         this.out.moveTo(this.sx0, this.sy0);
         this.emitFirstSegments();
      }

      this.needsMoveTo = true;
      this.idx = this.startIdx;
      this.dashOn = this.startDashOn;
      this.phase = this.startPhase;
      this.cx0 = var1;
      this.cy0 = var3;
      this.sx0 = var1;
      this.sy0 = var3;
      this.starting = true;
      if (this.clipRect != null) {
         int var5 = Helpers.outcode(var1, var3, this.clipRect);
         this.cOutCode = var5;
         this.outside = false;
         this.totalSkipLen = 0.0;
      }
   }

   private void emitSeg(double[] var1, int var2, int var3) {
      switch (var3) {
         case 4:
            this.out.lineTo(var1[var2], var1[var2 + 1]);
            return;
         case 5:
         case 7:
         default:
            return;
         case 6:
            this.out.quadTo(var1[var2], var1[var2 + 1], var1[var2 + 2], var1[var2 + 3]);
            return;
         case 8:
            this.out.curveTo(var1[var2], var1[var2 + 1], var1[var2 + 2], var1[var2 + 3], var1[var2 + 4], var1[var2 + 5]);
      }
   }

   private void emitFirstSegments() {
      double[] var1 = this.firstSegmentsBuffer;
      int var2 = 0;
      int var3 = this.firstSegidx;

      while (var2 < var3) {
         int var4 = (int)var1[var2];
         this.emitSeg(var1, var2 + 1, var4);
         var2 += var4 - 1;
      }

      this.firstSegidx = 0;
   }

   private void goTo(double[] var1, int var2, int var3, boolean var4) {
      int var5 = var2 + var3;
      double var6 = var1[var5 - 4];
      double var8 = var1[var5 - 3];
      if (var4) {
         if (this.starting) {
            this.goTo_starting(var1, var2, var3);
         } else {
            if (this.needsMoveTo) {
               this.needsMoveTo = false;
               this.out.moveTo(this.cx0, this.cy0);
            }

            this.emitSeg(var1, var2, var3);
         }
      } else {
         if (this.starting) {
            this.starting = false;
         }

         this.needsMoveTo = true;
      }

      this.cx0 = var6;
      this.cy0 = var8;
   }

   private void goTo_starting(double[] var1, int var2, int var3) {
      int var4 = var3 - 1;
      int var5 = this.firstSegidx;
      double[] var6 = this.firstSegmentsBuffer;
      if (var5 + var4 > var6.length) {
         if (DO_STATS) {
            this.rdrCtx.stats.stat_array_dasher_firstSegmentsBuffer.add(var5 + var4);
         }

         this.firstSegmentsBuffer = var6 = this.firstSegmentsBuffer_ref.widenArray(var6, var5, var5 + var4);
      }

      var6[var5++] = (double)var3;
      System.arraycopy(var1, var2, var6, var5, --var4);
      this.firstSegidx = var5 + var4;
   }

   @Override
   public void lineTo(double var1, double var3) {
      int var5 = this.cOutCode;
      if (this.clipRect != null) {
         int var6 = Helpers.outcode(var1, var3, this.clipRect);
         int var7 = var5 | var6;
         if (var7 != 0) {
            int var8 = var5 & var6;
            if (var8 != 0) {
               this.cOutCode = var6;
               this.skipLineTo(var1, var3);
               return;
            }

            if (this.subdivide) {
               this.subdivide = false;
               boolean var9 = this.curveSplitter.splitLine(this.cx0, this.cy0, var1, var3, var7, this);
               this.subdivide = true;
               if (var9) {
                  return;
               }
            }
         }

         this.cOutCode = var6;
         if (this.outside) {
            this.outside = false;
            this.skipLen();
         }
      }

      this._lineTo(var1, var3);
   }

   private void _lineTo(double var1, double var3) {
      double var5 = var1 - this.cx0;
      double var7 = var3 - this.cy0;
      double var9 = var5 * var5 + var7 * var7;
      if (var9 != 0.0) {
         var9 = Math.sqrt(var9);
         double var11 = var5 / var9;
         double var13 = var7 / var9;
         double[] var15 = this.curCurvepts;
         double[] var16 = this.dash;
         int var17 = this.dashLen;
         int var18 = this.idx;
         boolean var19 = this.dashOn;
         double var20 = this.phase;

         while (true) {
            double var22 = var16[var18] - var20;
            double var24 = var9 - var22;
            if (var24 <= 1.0E-6) {
               var15[0] = var1;
               var15[1] = var3;
               this.goTo(var15, 0, 4, var19);
               var20 += var9;
               if (Math.abs(var24) <= 1.0E-6) {
                  var20 = 0.0;
                  var18 = (var18 + 1) % var17;
                  var19 = !var19;
               }

               this.idx = var18;
               this.dashOn = var19;
               this.phase = var20;
               return;
            }

            var15[0] = this.cx0 + var22 * var11;
            var15[1] = this.cy0 + var22 * var13;
            this.goTo(var15, 0, 4, var19);
            var9 = var24;
            var18 = (var18 + 1) % var17;
            var19 = !var19;
            var20 = 0.0;
         }
      }
   }

   private void skipLineTo(double var1, double var3) {
      double var5 = var1 - this.cx0;
      double var7 = var3 - this.cy0;
      double var9 = var5 * var5 + var7 * var7;
      if (var9 != 0.0) {
         var9 = Math.sqrt(var9);
      }

      this.outside = true;
      this.totalSkipLen += var9;
      this.needsMoveTo = true;
      this.starting = false;
      this.cx0 = var1;
      this.cy0 = var3;
   }

   public void skipLen() {
      double var1 = this.totalSkipLen;
      this.totalSkipLen = 0.0;
      double[] var3 = this.dash;
      int var4 = this.dashLen;
      int var5 = this.idx;
      boolean var6 = this.dashOn;
      double var7 = this.phase;
      long var9 = (long)Math.floor(var1 / this.cycleLen) - 2L;
      if (var9 > 0L) {
         var1 -= this.cycleLen * (double)var9;
         long var11 = var9 * (long)var4;
         var5 = (int)(var11 + (long)var5) % var4;
         var6 = (var11 + (var6 ? 1L : 0L) & 1L) == 1L;
      }

      while (true) {
         double var16 = var3[var5] - var7;
         double var13 = var1 - var16;
         if (var13 <= 1.0E-6) {
            var7 += var1;
            if (Math.abs(var13) <= 1.0E-6) {
               var7 = 0.0;
               var5 = (var5 + 1) % var4;
               var6 = !var6;
            }

            this.idx = var5;
            this.dashOn = var6;
            this.phase = var7;
            return;
         }

         var1 = var13;
         var5 = (var5 + 1) % var4;
         var6 = !var6;
         var7 = 0.0;
      }
   }

   private void somethingTo(int var1) {
      double[] var2 = this.curCurvepts;
      if (!pointCurve(var2, var1)) {
         Dasher.LengthIterator var3 = this.li;
         double[] var4 = this.dash;
         int var5 = this.dashLen;
         var3.initializeIterationOnCurve(var2, var1);
         int var6 = this.idx;
         boolean var7 = this.dashOn;
         double var8 = this.phase;
         int var10 = 0;
         double var11 = 0.0;

         double var13;
         for (double var15 = var4[var6] - var8; (var13 = var3.next(var15)) < 1.0; var15 = var4[var6]) {
            if (var13 != 0.0) {
               Helpers.subdivideAt((var13 - var11) / (1.0 - var11), var2, var10, var2, 0, var1);
               var11 = var13;
               this.goTo(var2, 2, var1, var7);
               var10 = var1;
            }

            var6 = (var6 + 1) % var5;
            var7 = !var7;
            var8 = 0.0;
         }

         this.goTo(var2, var10 + 2, var1, var7);
         var8 += var3.lastSegLen();
         if (var8 + 1.0E-6 >= var4[var6]) {
            var8 = 0.0;
            var6 = (var6 + 1) % var5;
            var7 = !var7;
         }

         this.idx = var6;
         this.dashOn = var7;
         this.phase = var8;
         var3.reset();
      }
   }

   private void skipSomethingTo(int var1) {
      double[] var2 = this.curCurvepts;
      if (!pointCurve(var2, var1)) {
         Dasher.LengthIterator var3 = this.li;
         var3.initializeIterationOnCurve(var2, var1);
         double var4 = var3.totalLength();
         this.outside = true;
         this.totalSkipLen += var4;
         this.needsMoveTo = true;
         this.starting = false;
      }
   }

   private static boolean pointCurve(double[] var0, int var1) {
      for (int var2 = 2; var2 < var1; var2++) {
         if (var0[var2] != var0[var2 - 2]) {
            return false;
         }
      }

      return true;
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
               this.skipCurveTo(var1, var3, var5, var7, var9, var11);
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
         if (this.outside) {
            this.outside = false;
            this.skipLen();
         }
      }

      this._curveTo(var1, var3, var5, var7, var9, var11);
   }

   private void _curveTo(double var1, double var3, double var5, double var7, double var9, double var11) {
      double[] var13 = this.curCurvepts;
      TransformingPathConsumer2D.CurveBasicMonotonizer var14 = this.rdrCtx.monotonizer.curve(this.cx0, this.cy0, var1, var3, var5, var7, var9, var11);
      int var15 = var14.nbSplits;
      double[] var16 = var14.middle;
      int var17 = 0;

      for (byte var18 = 0; var17 <= var15; var18 += 6) {
         System.arraycopy(var16, var18, var13, 0, 8);
         this.somethingTo(8);
         var17++;
      }
   }

   private void skipCurveTo(double var1, double var3, double var5, double var7, double var9, double var11) {
      double[] var13 = this.curCurvepts;
      var13[0] = this.cx0;
      var13[1] = this.cy0;
      var13[2] = var1;
      var13[3] = var3;
      var13[4] = var5;
      var13[5] = var7;
      var13[6] = var9;
      var13[7] = var11;
      this.skipSomethingTo(8);
      this.cx0 = var9;
      this.cy0 = var11;
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
               this.skipQuadTo(var1, var3, var5, var7);
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
         if (this.outside) {
            this.outside = false;
            this.skipLen();
         }
      }

      this._quadTo(var1, var3, var5, var7);
   }

   private void _quadTo(double var1, double var3, double var5, double var7) {
      double[] var9 = this.curCurvepts;
      TransformingPathConsumer2D.CurveBasicMonotonizer var10 = this.rdrCtx.monotonizer.quad(this.cx0, this.cy0, var1, var3, var5, var7);
      int var11 = var10.nbSplits;
      double[] var12 = var10.middle;
      int var13 = 0;

      for (byte var14 = 0; var13 <= var11; var14 += 4) {
         System.arraycopy(var12, var14, var9, 0, 8);
         this.somethingTo(6);
         var13++;
      }
   }

   private void skipQuadTo(double var1, double var3, double var5, double var7) {
      double[] var9 = this.curCurvepts;
      var9[0] = this.cx0;
      var9[1] = this.cy0;
      var9[2] = var1;
      var9[3] = var3;
      var9[4] = var5;
      var9[5] = var7;
      this.skipSomethingTo(6);
      this.cx0 = var5;
      this.cy0 = var7;
   }

   @Override
   public void closePath() {
      if (this.cx0 != this.sx0 || this.cy0 != this.sy0) {
         this.lineTo(this.sx0, this.sy0);
      }

      if (this.firstSegidx != 0) {
         if (!this.dashOn || this.needsMoveTo) {
            this.out.moveTo(this.sx0, this.sy0);
         }

         this.emitFirstSegments();
      }

      this.moveTo(this.sx0, this.sy0);
   }

   @Override
   public void pathDone() {
      if (this.firstSegidx != 0) {
         this.out.moveTo(this.sx0, this.sy0);
         this.emitFirstSegments();
      }

      this.out.pathDone();
      this.dispose();
   }

   static final class LengthIterator {
      private final double[][] recCurveStack;
      private final boolean[] sidesRight;
      private int curveType;
      private double nextT;
      private double lenAtNextT;
      private double lastT;
      private double lenAtLastT;
      private double lenAtLastSplit;
      private double lastSegLen;
      private int recLevel;
      private boolean done;
      private final double[] curLeafCtrlPolyLengths = new double[3];
      private int cachedHaveLowAcceleration = -1;
      private final double[] nextRoots = new double[4];
      private final double[] flatLeafCoefCache = new double[]{0.0, 0.0, -1.0, 0.0};

      LengthIterator() {
         this.recCurveStack = new double[17][8];
         this.sidesRight = new boolean[16];
         this.nextT = Double.MAX_VALUE;
         this.lenAtNextT = Double.MAX_VALUE;
         this.lenAtLastSplit = Double.MIN_VALUE;
         this.recLevel = Integer.MIN_VALUE;
         this.lastSegLen = Double.MAX_VALUE;
         this.done = true;
      }

      void reset() {
      }

      void initializeIterationOnCurve(double[] var1, int var2) {
         System.arraycopy(var1, 0, this.recCurveStack[0], 0, 8);
         this.curveType = var2;
         this.recLevel = 0;
         this.lastT = 0.0;
         this.lenAtLastT = 0.0;
         this.nextT = 0.0;
         this.lenAtNextT = 0.0;
         this.goLeft();
         this.lenAtLastSplit = 0.0;
         if (this.recLevel > 0) {
            this.sidesRight[0] = false;
            this.done = false;
         } else {
            this.sidesRight[0] = true;
            this.done = true;
         }

         this.lastSegLen = 0.0;
      }

      private boolean haveLowAcceleration(double var1) {
         if (this.cachedHaveLowAcceleration == -1) {
            double var3 = this.curLeafCtrlPolyLengths[0];
            double var5 = this.curLeafCtrlPolyLengths[1];
            if (!Helpers.within(var3, var5, var1 * var5)) {
               this.cachedHaveLowAcceleration = 0;
               return false;
            } else {
               if (this.curveType == 8) {
                  double var7 = this.curLeafCtrlPolyLengths[2];
                  double var9 = var1 * var7;
                  if (!Helpers.within(var5, var7, var9) || !Helpers.within(var3, var7, var9)) {
                     this.cachedHaveLowAcceleration = 0;
                     return false;
                  }
               }

               this.cachedHaveLowAcceleration = 1;
               return true;
            }
         } else {
            return this.cachedHaveLowAcceleration == 1;
         }
      }

      double next(double var1) {
         double var3 = this.lenAtLastSplit + var1;

         while (this.lenAtNextT < var3) {
            if (this.done) {
               this.lastSegLen = this.lenAtNextT - this.lenAtLastSplit;
               return 1.0;
            }

            this.goToNextLeaf();
         }

         this.lenAtLastSplit = var3;
         double var5 = this.lenAtNextT - this.lenAtLastT;
         double var7 = (var3 - this.lenAtLastT) / var5;
         if (!this.haveLowAcceleration(0.05)) {
            double[] var9 = this.flatLeafCoefCache;
            if (var9[2] < 0.0) {
               double var10 = this.curLeafCtrlPolyLengths[0];
               double var12 = var10 + this.curLeafCtrlPolyLengths[1];
               if (this.curveType == 8) {
                  double var14 = var12 + this.curLeafCtrlPolyLengths[2];
                  var9[0] = 3.0 * (var10 - var12) + var14;
                  var9[1] = 3.0 * (var12 - 2.0 * var10);
                  var9[2] = 3.0 * var10;
                  var9[3] = -var14;
               } else if (this.curveType == 6) {
                  var9[0] = 0.0;
                  var9[1] = var12 - 2.0 * var10;
                  var9[2] = 2.0 * var10;
                  var9[3] = -var12;
               }
            }

            double var20 = var9[0];
            double var21 = var9[1];
            double var22 = var9[2];
            double var16 = var7 * var9[3];
            int var18 = Helpers.cubicRootsInAB(var20, var21, var22, var16, this.nextRoots, 0, 0.0, 1.0);
            if (var18 == 1 && !Double.isNaN(this.nextRoots[0])) {
               var7 = this.nextRoots[0];
            }
         }

         var7 = var7 * (this.nextT - this.lastT) + this.lastT;
         if (var7 >= 1.0) {
            var7 = 1.0;
            this.done = true;
         }

         this.lastSegLen = var1;
         return var7;
      }

      double totalLength() {
         while (!this.done) {
            this.goToNextLeaf();
         }

         this.reset();
         return this.lenAtNextT;
      }

      double lastSegLen() {
         return this.lastSegLen;
      }

      private void goToNextLeaf() {
         boolean[] var1 = this.sidesRight;
         int var2 = this.recLevel;
         var2--;

         while (var1[var2]) {
            if (var2 == 0) {
               this.recLevel = 0;
               this.done = true;
               return;
            }

            var2--;
         }

         var1[var2] = true;
         System.arraycopy(this.recCurveStack[var2++], 0, this.recCurveStack[var2], 0, 8);
         this.recLevel = var2;
         this.goLeft();
      }

      private void goLeft() {
         double var1 = this.onLeaf();
         if (var1 >= 0.0) {
            this.lastT = this.nextT;
            this.lenAtLastT = this.lenAtNextT;
            this.nextT = this.nextT + (double)(1 << 16 - this.recLevel) * 1.5258789E-5F;
            this.lenAtNextT += var1;
            this.flatLeafCoefCache[2] = -1.0;
            this.cachedHaveLowAcceleration = -1;
         } else {
            Helpers.subdivide(this.recCurveStack[this.recLevel], this.recCurveStack[this.recLevel + 1], this.recCurveStack[this.recLevel], this.curveType);
            this.sidesRight[this.recLevel] = false;
            this.recLevel++;
            this.goLeft();
         }
      }

      private double onLeaf() {
         double[] var1 = this.recCurveStack[this.recLevel];
         int var2 = this.curveType;
         double var3 = 0.0;
         double var5 = var1[0];
         double var7 = var1[1];

         for (byte var9 = 2; var9 < var2; var9 += 2) {
            double var10 = var1[var9];
            double var12 = var1[var9 + 1];
            double var14 = Helpers.linelen(var5, var7, var10, var12);
            var3 += var14;
            this.curLeafCtrlPolyLengths[(var9 >> 1) - 1] = var14;
            var5 = var10;
            var7 = var12;
         }

         double var16 = Helpers.linelen(var1[0], var1[1], var5, var7);
         return !(var3 - var16 < Dasher.CURVE_LEN_ERR) && this.recLevel != 16 ? -1.0 : (var3 + var16) / 2.0;
      }
   }
}
