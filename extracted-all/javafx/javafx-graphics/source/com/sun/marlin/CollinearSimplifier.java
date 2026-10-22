package com.sun.marlin;

public final class CollinearSimplifier implements DPathConsumer2D {
   static final double EPS = 1.0E-4;
   DPathConsumer2D delegate;
   CollinearSimplifier.SimplifierState state;
   double px1;
   double py1;
   double px2;
   double py2;
   double pslope;

   CollinearSimplifier() {
   }

   public CollinearSimplifier init(DPathConsumer2D var1) {
      this.delegate = var1;
      this.state = CollinearSimplifier.SimplifierState.Empty;
      return this;
   }

   @Override
   public void pathDone() {
      this.emitStashedLine();
      this.state = CollinearSimplifier.SimplifierState.Empty;
      this.delegate.pathDone();
   }

   @Override
   public void closePath() {
      this.emitStashedLine();
      this.state = CollinearSimplifier.SimplifierState.Empty;
      this.delegate.closePath();
   }

   @Override
   public void quadTo(double var1, double var3, double var5, double var7) {
      this.emitStashedLine();
      this.delegate.quadTo(var1, var3, var5, var7);
      this.state = CollinearSimplifier.SimplifierState.PreviousPoint;
      this.px1 = var5;
      this.py1 = var7;
   }

   @Override
   public void curveTo(double var1, double var3, double var5, double var7, double var9, double var11) {
      this.emitStashedLine();
      this.delegate.curveTo(var1, var3, var5, var7, var9, var11);
      this.state = CollinearSimplifier.SimplifierState.PreviousPoint;
      this.px1 = var9;
      this.py1 = var11;
   }

   @Override
   public void moveTo(double var1, double var3) {
      this.emitStashedLine();
      this.delegate.moveTo(var1, var3);
      this.state = CollinearSimplifier.SimplifierState.PreviousPoint;
      this.px1 = var1;
      this.py1 = var3;
   }

   @Override
   public void lineTo(double var1, double var3) {
      switch (this.state) {
         case Empty:
            this.delegate.lineTo(var1, var3);
            this.state = CollinearSimplifier.SimplifierState.PreviousPoint;
            this.px1 = var1;
            this.py1 = var3;
            return;
         case PreviousPoint:
            this.state = CollinearSimplifier.SimplifierState.PreviousLine;
            this.px2 = var1;
            this.py2 = var3;
            this.pslope = getSlope(this.px1, this.py1, var1, var3);
            return;
         case PreviousLine:
            double var5 = getSlope(this.px2, this.py2, var1, var3);
            if (var5 != this.pslope && !(Math.abs(this.pslope - var5) < 1.0E-4)) {
               this.delegate.lineTo(this.px2, this.py2);
               this.px1 = this.px2;
               this.py1 = this.py2;
               this.px2 = var1;
               this.py2 = var3;
               this.pslope = var5;
               return;
            }

            this.px2 = var1;
            this.py2 = var3;
            return;
      }
   }

   private void emitStashedLine() {
      if (this.state == CollinearSimplifier.SimplifierState.PreviousLine) {
         this.delegate.lineTo(this.px2, this.py2);
      }
   }

   private static double getSlope(double var0, double var2, double var4, double var6) {
      double var8 = var6 - var2;
      if (var8 == 0.0) {
         return var4 > var0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
      } else {
         return (var4 - var0) / var8;
      }
   }

   static enum SimplifierState {
      Empty,
      PreviousPoint,
      PreviousLine;
   }
}
