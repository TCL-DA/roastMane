package com.sun.prism.sw;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.marlin.DMarlinRenderingEngine;
import com.sun.marlin.IntArrayCache;
import com.sun.marlin.MarlinAlphaConsumer;
import com.sun.marlin.MarlinConst;
import com.sun.marlin.MarlinRenderer;
import com.sun.marlin.RendererContext;
import com.sun.pisces.PiscesRenderer;
import com.sun.prism.BasicStroke;
import com.sun.prism.PixelFormat;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
import com.sun.prism.impl.PrismSettings;
import com.sun.prism.impl.shape.DMarlinPrismUtils;
import java.lang.ref.SoftReference;

final class SWContext {
   private final ResourceFactory factory;
   private final SWContext.ShapeRenderer shapeRenderer;
   private SoftReference<SWRTTexture> readBackBufferRef;
   private SoftReference<SWArgbPreTexture> imagePaintTextureRef;

   SWContext(ResourceFactory var1) {
      this.factory = var1;
      switch (PrismSettings.rasterizerSpec) {
         case DoubleMarlin:
         default:
            this.shapeRenderer = new SWContext.DMarlinShapeRenderer();
      }
   }

   void renderShape(PiscesRenderer var1, Shape var2, BasicStroke var3, BaseTransform var4, Rectangle var5, boolean var6) {
      this.shapeRenderer.renderShape(var1, var2, var3, var4, var5, var6);
   }

   private SWRTTexture initRBBuffer(int var1, int var2) {
      SWRTTexture var3 = (SWRTTexture)this.factory.createRTTexture(var1, var2, Texture.WrapMode.CLAMP_NOT_NEEDED);
      this.readBackBufferRef = new SoftReference<>(var3);
      return var3;
   }

   private void disposeRBBuffer() {
      if (this.readBackBufferRef != null) {
         this.readBackBufferRef.clear();
         this.readBackBufferRef = null;
      }
   }

   SWRTTexture validateRBBuffer(int var1, int var2) {
      SWRTTexture var3;
      if (this.readBackBufferRef == null) {
         var3 = this.initRBBuffer(var1, var2);
      } else {
         var3 = this.readBackBufferRef.get();
         if (var3 == null || var3.getPhysicalWidth() < var1 || var3.getPhysicalHeight() < var2) {
            this.disposeRBBuffer();
            var3 = this.initRBBuffer(var1, var2);
         }

         var3.setContentWidth(var1);
         var3.setContentHeight(var2);
      }

      return var3;
   }

   private SWArgbPreTexture initImagePaintTexture(int var1, int var2) {
      SWArgbPreTexture var3 = (SWArgbPreTexture)this.factory
         .createTexture(PixelFormat.INT_ARGB_PRE, Texture.Usage.DEFAULT, Texture.WrapMode.REPEAT, var1, var2);
      this.imagePaintTextureRef = new SoftReference<>(var3);
      return var3;
   }

   private void disposeImagePaintTexture() {
      if (this.imagePaintTextureRef != null) {
         this.imagePaintTextureRef.clear();
         this.imagePaintTextureRef = null;
      }
   }

   SWArgbPreTexture validateImagePaintTexture(int var1, int var2) {
      SWArgbPreTexture var3;
      if (this.imagePaintTextureRef == null) {
         var3 = this.initImagePaintTexture(var1, var2);
      } else {
         var3 = this.imagePaintTextureRef.get();
         if (var3 == null || var3.getPhysicalWidth() < var1 || var3.getPhysicalHeight() < var2) {
            this.disposeImagePaintTexture();
            var3 = this.initImagePaintTexture(var1, var2);
         }

         var3.setContentWidth(var1);
         var3.setContentHeight(var2);
      }

      return var3;
   }

   void dispose() {
      this.disposeRBBuffer();
      this.disposeImagePaintTexture();
      this.shapeRenderer.dispose();
   }

   static final class DMarlinShapeRenderer implements SWContext.ShapeRenderer {
      private final SWContext.DirectRTMarlinAlphaConsumer alphaConsumer = new SWContext.DirectRTMarlinAlphaConsumer();

      @Override
      public void renderShape(PiscesRenderer var1, Shape var2, BasicStroke var3, BaseTransform var4, Rectangle var5, boolean var6) {
         if (var3 != null && var3.getType() != 0) {
            var2 = var3.createStrokedShape(var2);
            var3 = null;
         }

         RendererContext var7 = DMarlinRenderingEngine.getRendererContext();
         MarlinRenderer var8 = null;

         try {
            if (var2 instanceof Path2D) {
               var8 = DMarlinPrismUtils.setupRenderer(var7, (Path2D)var2, var3, var4, var5, var6);
            }

            if (var8 == null) {
               var8 = DMarlinPrismUtils.setupRenderer(var7, var2, var3, var4, var5, var6);
            }

            int var9 = var8.getOutpixMinX();
            int var10 = var8.getOutpixMaxX();
            int var11 = var8.getOutpixMinY();
            int var12 = var8.getOutpixMaxY();
            int var13 = var10 - var9;
            int var14 = var12 - var11;
            if (var13 <= 0 || var14 <= 0) {
               return;
            }

            this.alphaConsumer.initConsumer(var9, var11, var13, var14, var1);
            var8.produceAlphas(this.alphaConsumer);
         } finally {
            if (var8 != null) {
               var8.dispose();
            }

            DMarlinRenderingEngine.returnRendererContext(var7);
         }
      }

      @Override
      public void dispose() {
      }
   }

   static final class DirectRTMarlinAlphaConsumer implements MarlinAlphaConsumer {
      private byte[] alpha_map;
      private int x;
      private int y;
      private int w;
      private int h;
      private int rowNum;
      private PiscesRenderer pr;

      public void initConsumer(int var1, int var2, int var3, int var4, PiscesRenderer var5) {
         this.x = var1;
         this.y = var2;
         this.w = var3;
         this.h = var4;
         this.rowNum = 0;
         this.pr = var5;
      }

      @Override
      public int getOriginX() {
         return this.x;
      }

      @Override
      public int getOriginY() {
         return this.y;
      }

      @Override
      public int getWidth() {
         return this.w;
      }

      @Override
      public int getHeight() {
         return this.h;
      }

      @Override
      public void setMaxAlpha(int var1) {
         if (this.alpha_map == null || this.alpha_map.length != var1 + 1) {
            this.alpha_map = new byte[var1 + 1];

            for (int var2 = 0; var2 <= var1; var2++) {
               this.alpha_map[var2] = (byte)((var2 * 255 + var1 / 2) / var1);
            }
         }
      }

      @Override
      public boolean supportBlockFlags() {
         return false;
      }

      @Override
      public void clearAlphas(int var1) {
      }

      @Override
      public void setAndClearRelativeAlphas(int[] var1, int var2, int var3, int var4) {
         this.pr.emitAndClearAlphaRow(this.alpha_map, var1, var2, var3, var4, var3 - this.x, this.rowNum);
         this.rowNum++;
         int var5 = var4 - this.x;
         if (var5 <= this.w) {
            var1[var5] = 0;
         } else {
            var1[this.w] = 0;
         }

         if (MarlinConst.DO_CHECKS) {
            IntArrayCache.check(var1, var3 - this.x, var5 + 1, 0);
         }
      }

      @Override
      public void setAndClearRelativeAlphas(int[] var1, int[] var2, int var3, int var4, int var5) {
         throw new UnsupportedOperationException();
      }
   }

   interface ShapeRenderer {
      void renderShape(PiscesRenderer var1, Shape var2, BasicStroke var3, BaseTransform var4, Rectangle var5, boolean var6);

      void dispose();
   }
}
