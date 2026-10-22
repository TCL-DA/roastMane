package com.sun.scenario.effect.impl.sw.java;

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.Blend;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.impl.HeapImage;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.state.RenderState;

public class JSWBlend_HARD_LIGHTPeer extends JSWEffectPeer {
   public JSWBlend_HARD_LIGHTPeer(FilterContext var1, Renderer var2, String var3) {
      super(var1, var2, var3);
   }

   protected final Blend getEffect() {
      return (Blend)super.getEffect();
   }

   private float getOpacity() {
      return this.getEffect().getOpacity();
   }

   @Override
   public ImageData filter(Effect var1, RenderState var2, BaseTransform var3, Rectangle var4, ImageData... var5) {
      this.setEffect(var1);
      Rectangle var6 = this.getResultBounds(var3, var4, var5);
      this.setDestBounds(var6);
      HeapImage var7 = (HeapImage)var5[0].getTransformedImage(var6);
      byte var8 = 0;
      byte var9 = 0;
      int var10 = var7.getPhysicalWidth();
      int var11 = var7.getPhysicalHeight();
      int var12 = var7.getScanlineStride();
      int[] var13 = var7.getPixelArray();
      Rectangle var14 = new Rectangle(var8, var9, var10, var11);
      Rectangle var15 = var5[0].getTransformedBounds(var6);
      BaseTransform var16 = BaseTransform.IDENTITY_TRANSFORM;
      this.setInputBounds(0, var15);
      this.setInputNativeBounds(0, var14);
      HeapImage var17 = (HeapImage)var5[1].getTransformedImage(var6);
      byte var18 = 0;
      byte var19 = 0;
      int var20 = var17.getPhysicalWidth();
      int var21 = var17.getPhysicalHeight();
      int var22 = var17.getScanlineStride();
      int[] var23 = var17.getPixelArray();
      Rectangle var24 = new Rectangle(var18, var19, var20, var21);
      Rectangle var25 = var5[1].getTransformedBounds(var6);
      BaseTransform var26 = BaseTransform.IDENTITY_TRANSFORM;
      this.setInputBounds(1, var25);
      this.setInputNativeBounds(1, var24);
      float[] var27 = new float[4];
      this.getTextureCoordinates(0, var27, (float)var15.x, (float)var15.y, (float)var10, (float)var11, var6, var16);
      float[] var28 = new float[4];
      this.getTextureCoordinates(1, var28, (float)var25.x, (float)var25.y, (float)var20, (float)var21, var6, var26);
      int var29 = var6.width;
      int var30 = var6.height;
      HeapImage var31 = (HeapImage)this.getRenderer().getCompatibleImage(var29, var30);
      this.setDestNativeBounds(var31.getPhysicalWidth(), var31.getPhysicalHeight());
      int var32 = var31.getScanlineStride();
      int[] var33 = var31.getPixelArray();
      float var39 = this.getOpacity();
      float var40 = (var27[2] - var27[0]) / (float)var29;
      float var41 = (var27[3] - var27[1]) / (float)var30;
      float var42 = (var28[2] - var28[0]) / (float)var29;
      float var43 = (var28[3] - var28[1]) / (float)var30;
      float var44 = var27[1] + var41 * 0.5F;
      float var45 = var28[1] + var43 * 0.5F;

      for (int var46 = 0; var46 < 0 + var30; var46++) {
         float var47 = (float)var46;
         int var34 = var46 * var32;
         float var48 = var27[0] + var40 * 0.5F;
         float var49 = var28[0] + var42 * 0.5F;

         for (int var50 = 0; var50 < 0 + var29; var50++) {
            float var51 = (float)var50;
            int var58;
            if (var48 >= 0.0F && var44 >= 0.0F) {
               int var59 = (int)(var48 * (float)var10);
               int var60 = (int)(var44 * (float)var11);
               boolean var61 = var59 >= var10 || var60 >= var11;
               var58 = var61 ? 0 : var13[var60 * var12 + var59];
            } else {
               var58 = 0;
            }

            float var52 = (float)(var58 >> 16 & 0xFF) / 255.0F;
            float var53 = (float)(var58 >> 8 & 0xFF) / 255.0F;
            float var54 = (float)(var58 & 0xFF) / 255.0F;
            float var55 = (float)(var58 >>> 24) / 255.0F;
            int var62;
            if (var49 >= 0.0F && var45 >= 0.0F) {
               int var63 = (int)(var49 * (float)var20);
               int var64 = (int)(var45 * (float)var21);
               boolean var65 = var63 >= var20 || var64 >= var21;
               var62 = var65 ? 0 : var23[var64 * var22 + var63];
            } else {
               var62 = 0;
            }

            var52 = (float)(var62 >> 16 & 0xFF) / 255.0F;
            var53 = (float)(var62 >> 8 & 0xFF) / 255.0F;
            var54 = (float)(var62 & 0xFF) / 255.0F;
            float var84 = (float)(var62 >>> 24) / 255.0F;
            float var87 = var52 * var39;
            float var88 = var53 * var39;
            float var89 = var54 * var39;
            float var90 = var84 * var39;
            float var79 = var55 + var90 - var55 * var90;
            float var80 = 0.5F * var90;
            float var76;
            if (var87 > var80) {
               var76 = var87 + var55 * (var87 - var90) - var52 * (2.0F * var87 - var90 - 1.0F);
            } else {
               var76 = 2.0F * var52 * var87 + var52 * (1.0F - var90) + var87 * (1.0F - var55);
            }

            float var77;
            if (var88 > var80) {
               var77 = var88 + var55 * (var88 - var90) - var53 * (2.0F * var88 - var90 - 1.0F);
            } else {
               var77 = 2.0F * var53 * var88 + var53 * (1.0F - var90) + var88 * (1.0F - var55);
            }

            float var78;
            if (var89 > var80) {
               var78 = var89 + var55 * (var89 - var90) - var54 * (2.0F * var89 - var90 - 1.0F);
            } else {
               var78 = 2.0F * var54 * var89 + var54 * (1.0F - var90) + var89 * (1.0F - var55);
            }

            float var35 = var76;
            float var36 = var77;
            float var37 = var78;
            float var38 = var79;
            if (var79 < 0.0F) {
               var38 = 0.0F;
            } else if (var79 > 1.0F) {
               var38 = 1.0F;
            }

            if (var76 < 0.0F) {
               var35 = 0.0F;
            } else if (var76 > var38) {
               var35 = var38;
            }

            if (var77 < 0.0F) {
               var36 = 0.0F;
            } else if (var77 > var38) {
               var36 = var38;
            }

            if (var78 < 0.0F) {
               var37 = 0.0F;
            } else if (var78 > var38) {
               var37 = var38;
            }

            var33[var34 + var50] = (int)(var35 * 255.0F) << 16 | (int)(var36 * 255.0F) << 8 | (int)(var37 * 255.0F) << 0 | (int)(var38 * 255.0F) << 24;
            var48 += var40;
            var49 += var42;
         }

         var44 += var41;
         var45 += var43;
      }

      var5[0].releaseTransformedImage(var7);
      var5[1].releaseTransformedImage(var17);
      return new ImageData(this.getFilterContext(), var31, var6);
   }
}
