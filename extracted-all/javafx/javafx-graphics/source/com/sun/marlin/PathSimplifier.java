package com.sun.marlin;

public final class PathSimplifier implements DPathConsumer2D {
   private static final double PIX_THRESHOLD = (double)MarlinProperties.getPathSimplifierPixelTolerance();
   private static final double SQUARE_TOLERANCE = PIX_THRESHOLD * PIX_THRESHOLD;
   private DPathConsumer2D delegate;
   private double cx;
   private double cy;
   private boolean skipped;
   private double sx;
   private double sy;

   PathSimplifier() {
   }

   public PathSimplifier init(DPathConsumer2D var1) {
      this.delegate = var1;
      this.skipped = false;
      return this;
   }

   private void finishPath() {
      if (this.skipped) {
         this._lineTo(this.sx, this.sy);
      }
   }

   @Override
   public void pathDone() {
      this.finishPath();
      this.delegate.pathDone();
   }

   @Override
   public void closePath() {
      this.finishPath();
      this.delegate.closePath();
   }

   @Override
   public void moveTo(double var1, double var3) {
      this.finishPath();
      this.delegate.moveTo(var1, var3);
      this.cx = var1;
      this.cy = var3;
   }

   @Override
   public void lineTo(double var1, double var3) {
      double var5 = var1 - this.cx;
      double var7 = var3 - this.cy;
      if (var5 * var5 + var7 * var7 <= SQUARE_TOLERANCE) {
         this.skipped = true;
         this.sx = var1;
         this.sy = var3;
      } else {
         this._lineTo(var1, var3);
      }
   }

   private void _lineTo(double var1, double var3) {
      this.delegate.lineTo(var1, var3);
      this.cx = var1;
      this.cy = var3;
      this.skipped = false;
   }

   @Override
   public void quadTo(double var1, double var3, double var5, double var7) {
      double var9 = var5 - this.cx;
      double var11 = var7 - this.cy;
      if (var9 * var9 + var11 * var11 <= SQUARE_TOLERANCE) {
         var9 = var1 - this.cx;
         var11 = var3 - this.cy;
         if (var9 * var9 + var11 * var11 <= SQUARE_TOLERANCE) {
            this.skipped = true;
            this.sx = var5;
            this.sy = var7;
            return;
         }
      }

      this.delegate.quadTo(var1, var3, var5, var7);
      this.cx = var5;
      this.cy = var7;
      this.skipped = false;
   }

   @Override
   public void curveTo(double var1, double var3, double var5, double var7, double var9, double var11) {
      double var13 = var9 - this.cx;
      double var15 = var11 - this.cy;
      if (var13 * var13 + var15 * var15 <= SQUARE_TOLERANCE) {
         var13 = var1 - this.cx;
         var15 = var3 - this.cy;
         if (var13 * var13 + var15 * var15 <= SQUARE_TOLERANCE) {
            var13 = var5 - this.cx;
            var15 = var7 - this.cy;
            if (var13 * var13 + var15 * var15 <= SQUARE_TOLERANCE) {
               this.skipped = true;
               this.sx = var9;
               this.sy = var11;
               return;
            }
         }
      }

      this.delegate.curveTo(var1, var3, var5, var7, var9, var11);
      this.cx = var9;
      this.cy = var11;
      this.skipped = false;
   }
}
