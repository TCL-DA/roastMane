package com.sun.marlin;

public interface MarlinRenderer extends DPathConsumer2D {
   MarlinRenderer init(int var1, int var2, int var3, int var4, int var5);

   void dispose();

   int getOutpixMinX();

   int getOutpixMaxX();

   int getOutpixMinY();

   int getOutpixMaxY();

   void produceAlphas(MarlinAlphaConsumer var1);

   double getOffsetX();

   double getOffsetY();
}
