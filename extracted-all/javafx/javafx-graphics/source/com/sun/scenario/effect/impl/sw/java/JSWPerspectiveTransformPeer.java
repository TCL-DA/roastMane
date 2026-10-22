package com.sun.scenario.effect.impl.sw.java;

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.PerspectiveTransform;
import com.sun.scenario.effect.impl.HeapImage;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.state.AccessHelper;
import com.sun.scenario.effect.impl.state.PerspectiveTransformState;
import com.sun.scenario.effect.impl.state.RenderState;

public class JSWPerspectiveTransformPeer extends JSWEffectPeer {
   public JSWPerspectiveTransformPeer(FilterContext var1, Renderer var2, String var3) {
      super(var1, var2, var3);
   }

   protected final PerspectiveTransform getEffect() {
      return (PerspectiveTransform)super.getEffect();
   }

   private float[][] getITX() {
      PerspectiveTransformState var1 = (PerspectiveTransformState)AccessHelper.getState(this.getEffect());
      return var1.getITX();
   }

   private float[] getTx0() {
      Rectangle var1 = this.getInputBounds(0);
      Rectangle var2 = this.getInputNativeBounds(0);
      float var3 = (float)var1.width / (float)var2.width;
      float[] var4 = this.getITX()[0];
      return new float[]{var4[0] * var3, var4[1] * var3, var4[2] * var3};
   }

   private float[] getTx1() {
      Rectangle var1 = this.getInputBounds(0);
      Rectangle var2 = this.getInputNativeBounds(0);
      float var3 = (float)var1.height / (float)var2.height;
      float[] var4 = this.getITX()[1];
      return new float[]{var4[0] * var3, var4[1] * var3, var4[2] * var3};
   }

   private float[] getTx2() {
      return this.getITX()[2];
   }

   @Override
   public int getTextureCoordinates(int var1, float[] var2, float var3, float var4, float var5, float var6, Rectangle var7, BaseTransform var8) {
      var2[0] = (float)var7.x;
      var2[1] = (float)var7.y;
      var2[2] = (float)(var7.x + var7.width);
      var2[3] = (float)(var7.y + var7.height);
      return 4;
   }

   @Override
   public ImageData filter(Effect var1, RenderState var2, BaseTransform var3, Rectangle var4, ImageData... var5) {
      this.setEffect(var1);
      Rectangle var6 = this.getResultBounds(var3, var4, var5);
      this.setDestBounds(var6);
      HeapImage var7 = (HeapImage)var5[0].getUntransformedImage();
      byte var8 = 0;
      byte var9 = 0;
      int var10 = var7.getPhysicalWidth();
      int var11 = var7.getPhysicalHeight();
      int var12 = var7.getScanlineStride();
      int[] var13 = var7.getPixelArray();
      Rectangle var14 = new Rectangle(var8, var9, var10, var11);
      Rectangle var15 = var5[0].getUntransformedBounds();
      BaseTransform var16 = var5[0].getTransform();
      this.setInputBounds(0, var15);
      this.setInputNativeBounds(0, var14);
      float[] var17 = new float[4];
      float[] var18 = new float[4];
      this.getTextureCoordinates(0, var18, (float)var15.x, (float)var15.y, (float)var10, (float)var11, var6, var16);
      int var19 = var6.width;
      int var20 = var6.height;
      HeapImage var21 = (HeapImage)this.getRenderer().getCompatibleImage(var19, var20);
      this.setDestNativeBounds(var21.getPhysicalWidth(), var21.getPhysicalHeight());
      int var22 = var21.getScanlineStride();
      int[] var23 = var21.getPixelArray();
      float[] var29 = this.getTx1();
      float var30 = var29[0];
      float var31 = var29[1];
      float var32 = var29[2];
      float[] var33 = this.getTx0();
      float var34 = var33[0];
      float var35 = var33[1];
      float var36 = var33[2];
      float[] var37 = this.getTx2();
      float var38 = var37[0];
      float var39 = var37[1];
      float var40 = var37[2];
      float var41 = (var18[2] - var18[0]) / (float)var19;
      float var42 = (var18[3] - var18[1]) / (float)var20;
      float var43 = var18[1] + var42 * 0.5F;

      for (int var44 = 0; var44 < 0 + var20; var44++) {
         float var45 = (float)var44;
         int var24 = var44 * var22;
         float var46 = var18[0] + var41 * 0.5F;

         for (int var47 = 0; var47 < 0 + var19; var47++) {
            float var48 = (float)var47;
            float var51 = 1.0F;
            float var55 = var46 * var38 + var43 * var39 + var51 * var40;
            float var62 = var46 * var34 + var43 * var35 + var51 * var36;
            float var52 = var62 / var55;
            float var63 = var46 * var30 + var43 * var31 + var51 * var32;
            float var53 = var63 / var55;
            this.lsample(var13, var52, var53, var10, var11, var12, var17);
            float var56 = var17[0];
            float var57 = var17[1];
            float var58 = var17[2];
            float var59 = var17[3];
            float var25 = var56;
            float var26 = var57;
            float var27 = var58;
            float var28 = var59;
            if (var59 < 0.0F) {
               var28 = 0.0F;
            } else if (var59 > 1.0F) {
               var28 = 1.0F;
            }

            if (var56 < 0.0F) {
               var25 = 0.0F;
            } else if (var56 > var28) {
               var25 = var28;
            }

            if (var57 < 0.0F) {
               var26 = 0.0F;
            } else if (var57 > var28) {
               var26 = var28;
            }

            if (var58 < 0.0F) {
               var27 = 0.0F;
            } else if (var58 > var28) {
               var27 = var28;
            }

            var23[var24 + var47] = (int)(var25 * 255.0F) << 16 | (int)(var26 * 255.0F) << 8 | (int)(var27 * 255.0F) << 0 | (int)(var28 * 255.0F) << 24;
            var46 += var41;
         }

         var43 += var42;
      }

      return new ImageData(this.getFilterContext(), var21, var6);
   }
}
