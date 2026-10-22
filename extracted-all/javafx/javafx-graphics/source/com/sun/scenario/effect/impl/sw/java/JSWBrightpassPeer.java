package com.sun.scenario.effect.impl.sw.java;

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.Brightpass;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.impl.HeapImage;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.state.RenderState;

public class JSWBrightpassPeer extends JSWEffectPeer {
   public JSWBrightpassPeer(FilterContext var1, Renderer var2, String var3) {
      super(var1, var2, var3);
   }

   protected final Brightpass getEffect() {
      return (Brightpass)super.getEffect();
   }

   private float getThreshold() {
      return this.getEffect().getThreshold();
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
      float[] var17 = new float[4];
      this.getTextureCoordinates(0, var17, (float)var15.x, (float)var15.y, (float)var10, (float)var11, var6, var16);
      int var18 = var6.width;
      int var19 = var6.height;
      HeapImage var20 = (HeapImage)this.getRenderer().getCompatibleImage(var18, var19);
      this.setDestNativeBounds(var20.getPhysicalWidth(), var20.getPhysicalHeight());
      int var21 = var20.getScanlineStride();
      int[] var22 = var20.getPixelArray();
      float var28 = this.getThreshold();
      float var29 = (var17[2] - var17[0]) / (float)var18;
      float var30 = (var17[3] - var17[1]) / (float)var19;
      float var31 = var17[1] + var30 * 0.5F;

      for (int var32 = 0; var32 < 0 + var19; var32++) {
         float var33 = (float)var32;
         int var23 = var32 * var21;
         float var34 = var17[0] + var29 * 0.5F;

         for (int var35 = 0; var35 < 0 + var18; var35++) {
            float var36 = (float)var35;
            float var37 = 0.2125F;
            float var38 = 0.7154F;
            float var39 = 0.0721F;
            int var46;
            if (var34 >= 0.0F && var31 >= 0.0F) {
               int var47 = (int)(var34 * (float)var10);
               int var48 = (int)(var31 * (float)var11);
               boolean var49 = var47 >= var10 || var48 >= var11;
               var46 = var49 ? 0 : var13[var48 * var12 + var47];
            } else {
               var46 = 0;
            }

            float var40 = (float)(var46 >> 16 & 0xFF) / 255.0F;
            float var41 = (float)(var46 >> 8 & 0xFF) / 255.0F;
            float var42 = (float)(var46 & 0xFF) / 255.0F;
            float var43 = (float)(var46 >>> 24) / 255.0F;
            float var55 = var37 * var40 + var38 * var41 + var39 * var42;
            float var51 = 0.0F;
            float var52 = var55 - var43 * var28;
            float var50 = var51 > var52 ? var51 : var52;
            var51 = Math.signum(var50);
            float var24 = var40 * var51;
            float var25 = var41 * var51;
            float var26 = var42 * var51;
            float var27 = var43 * var51;
            if (var27 < 0.0F) {
               var27 = 0.0F;
            } else if (var27 > 1.0F) {
               var27 = 1.0F;
            }

            if (var24 < 0.0F) {
               var24 = 0.0F;
            } else if (var24 > var27) {
               var24 = var27;
            }

            if (var25 < 0.0F) {
               var25 = 0.0F;
            } else if (var25 > var27) {
               var25 = var27;
            }

            if (var26 < 0.0F) {
               var26 = 0.0F;
            } else if (var26 > var27) {
               var26 = var27;
            }

            var22[var23 + var35] = (int)(var24 * 255.0F) << 16 | (int)(var25 * 255.0F) << 8 | (int)(var26 * 255.0F) << 0 | (int)(var27 * 255.0F) << 24;
            var34 += var29;
         }

         var31 += var30;
      }

      var5[0].releaseTransformedImage(var7);
      return new ImageData(this.getFilterContext(), var20, var6);
   }
}
