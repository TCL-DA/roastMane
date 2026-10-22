package com.sun.marlin;

public interface DPathConsumer2D {
   void moveTo(double var1, double var3);

   void lineTo(double var1, double var3);

   void quadTo(double var1, double var3, double var5, double var7);

   void curveTo(double var1, double var3, double var5, double var7, double var9, double var11);

   void closePath();

   void pathDone();
}
