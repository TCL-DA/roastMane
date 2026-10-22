package com.sun.scenario.effect.impl.sw.java;

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.InvertMask;
import com.sun.scenario.effect.impl.HeapImage;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.state.RenderState;

public class JSWInvertMaskPeer extends JSWEffectPeer {
   public JSWInvertMaskPeer(FilterContext var1, Renderer var2, String var3) {
      super(var1, var2, var3);
   }

   protected final InvertMask getEffect() {
      return (InvertMask)super.getEffect();
   }

   private float[] getOffset() {
      float var1 = (float)this.getEffect().getOffsetX();
      float var2 = (float)this.getEffect().getOffsetY();
      float[] var3 = new float[]{var1, var2};

      try {
         this.getInputTransform(0).inverseDeltaTransform(var3, 0, var3, 0, 1);
      } catch (Exception var5) {
      }

      var3[0] /= (float)this.getInputNativeBounds(0).width;
      var3[1] /= (float)this.getInputNativeBounds(0).height;
      return var3;
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
      float[] var28 = this.getOffset();
      float var29 = var28[0];
      float var30 = var28[1];
      float var31 = (var17[2] - var17[0]) / (float)var18;
      float var32 = (var17[3] - var17[1]) / (float)var19;
      float var33 = var17[1] + var32 * 0.5F;

      for (int var34 = 0; var34 < 0 + var19; var34++) {
         float var35 = (float)var34;
         int var23 = var34 * var21;
         float var36 = var17[0] + var31 * 0.5F;

         for (int var37 = 0; var37 < 0 + var18; var37++) {
            float var38 = (float)var37;
            float var40 = var36 - var29;
            float var41 = var33 - var30;
            int var42;
            if (var40 >= 0.0F && var41 >= 0.0F) {
               int var43 = (int)(var40 * (float)var10);
               int var44 = (int)(var41 * (float)var11);
               boolean var45 = var43 >= var10 || var44 >= var11;
               var42 = var45 ? 0 : var13[var44 * var12 + var43];
            } else {
               var42 = 0;
            }

            float var39 = (float)(var42 >>> 24) / 255.0F;
            var41 = 1.0F - var39;
            float var24 = var41;
            float var25 = var41;
            float var26 = var41;
            float var27 = var41;
            if (var41 < 0.0F) {
               var27 = 0.0F;
            } else if (var41 > 1.0F) {
               var27 = 1.0F;
            }

            if (var41 < 0.0F) {
               var24 = 0.0F;
            } else if (var41 > var27) {
               var24 = var27;
            }

            if (var41 < 0.0F) {
               var25 = 0.0F;
            } else if (var41 > var27) {
               var25 = var27;
            }

            if (var41 < 0.0F) {
               var26 = 0.0F;
            } else if (var41 > var27) {
               var26 = var27;
            }

            var22[var23 + var37] = (int)(var24 * 255.0F) << 16 | (int)(var25 * 255.0F) << 8 | (int)(var26 * 255.0F) << 0 | (int)(var27 * 255.0F) << 24;
            var36 += var31;
         }

         var33 += var32;
      }

      var5[0].releaseTransformedImage(var7);
      return new ImageData(this.getFilterContext(), var20, var6);
   }
}
