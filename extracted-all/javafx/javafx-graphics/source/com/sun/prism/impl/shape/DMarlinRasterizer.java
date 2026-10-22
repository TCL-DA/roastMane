package com.sun.prism.impl.shape;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.marlin.DMarlinRenderingEngine;
import com.sun.marlin.MarlinRenderer;
import com.sun.marlin.MaskMarlinAlphaConsumer;
import com.sun.marlin.RendererContext;
import com.sun.prism.BasicStroke;
import com.sun.prism.impl.PrismSettings;

public final class DMarlinRasterizer implements ShapeRasterizer {
   private static final MaskData EMPTY_MASK = MaskData.create(new byte[1], 0, 0, 1, 1);

   @Override
   public MaskData getMaskData(Shape var1, BasicStroke var2, RectBounds var3, BaseTransform var4, boolean var5, boolean var6) {
      if (var2 != null && var2.getType() != 0) {
         var1 = var2.createStrokedShape(var1);
         var2 = null;
      }

      if (var3 == null) {
         if (var2 != null) {
            var1 = var2.createStrokedShape(var1);
            var2 = null;
         }

         var3 = new RectBounds();
         var3 = (RectBounds)var4.transform(var1.getBounds(), var3);
      }

      if (var3.isEmpty()) {
         return EMPTY_MASK;
      } else {
         RendererContext var7 = DMarlinRenderingEngine.getRendererContext();
         MarlinRenderer var8 = null;

         MaskData var23;
         try {
            Rectangle var9 = var7.clip;
            var9.setBounds(var3);
            var8 = DMarlinPrismUtils.setupRenderer(var7, var1, var2, var4, var9, var6);
            int var10 = var8.getOutpixMinX();
            int var11 = var8.getOutpixMaxX();
            int var12 = var8.getOutpixMinY();
            int var13 = var8.getOutpixMaxY();
            int var14 = var11 - var10;
            int var15 = var13 - var12;
            if (var14 <= 0 || var15 <= 0) {
               return EMPTY_MASK;
            }

            MaskMarlinAlphaConsumer var16 = var7.consumer;
            if (var16 == null || var14 * var15 > var16.getAlphaLength()) {
               int var17 = var14 * var15 + 4095 & -4096;
               var7.consumer = var16 = new MaskMarlinAlphaConsumer(var17);
               if (PrismSettings.verbose) {
                  System.out.println("new alphas with length = " + var17);
               }
            }

            var16.setBoundsNoClone(var10, var12, var14, var15);
            var8.produceAlphas(var16);
            var23 = var16.getMaskData();
         } finally {
            if (var8 != null) {
               var8.dispose();
            }

            DMarlinRenderingEngine.returnRendererContext(var7);
         }

         return var23;
      }
   }

   static Shape createCenteredStrokedShape(Shape var0, BasicStroke var1) {
      float var2 = var1.getType() == 0 ? var1.getLineWidth() : var1.getLineWidth() * 2.0F;
      RendererContext var3 = DMarlinRenderingEngine.getRendererContext();

      Path2D var5;
      try {
         Path2D var4 = var3.getPath2D();
         DMarlinPrismUtils.strokeTo(var3, var0, var1, var2, var3.transformerPC2D.wrapPath2D(var4));
         var5 = new Path2D(var4);
      } finally {
         DMarlinRenderingEngine.returnRendererContext(var3);
      }

      return var5;
   }
}
