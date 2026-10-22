package com.sun.scenario.effect.impl.sw.java;

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.DisplacementMap;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.FloatMap;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.impl.HeapImage;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.state.RenderState;

public class JSWDisplacementMapPeer extends JSWEffectPeer {
   public JSWDisplacementMapPeer(FilterContext var1, Renderer var2, String var3) {
      super(var1, var2, var3);
   }

   protected final DisplacementMap getEffect() {
      return (DisplacementMap)super.getEffect();
   }

   private float[] getSampletx() {
      return new float[]{this.getEffect().getOffsetX(), this.getEffect().getOffsetY(), this.getEffect().getScaleX(), this.getEffect().getScaleY()};
   }

   private float[] getImagetx() {
      float var1 = this.getEffect().getWrap() ? 0.5F : 0.0F;
      return new float[]{
         var1 / (float)this.getInputNativeBounds(0).width,
         var1 / (float)this.getInputNativeBounds(0).height,
         ((float)this.getInputBounds(0).width - 2.0F * var1) / (float)this.getInputNativeBounds(0).width,
         ((float)this.getInputBounds(0).height - 2.0F * var1) / (float)this.getInputNativeBounds(0).height
      };
   }

   private float getWrap() {
      return this.getEffect().getWrap() ? 1.0F : 0.0F;
   }

   @Override
   protected Object getSamplerData(int var1) {
      return this.getEffect().getMapData();
   }

   @Override
   public int getTextureCoordinates(int var1, float[] var2, float var3, float var4, float var5, float var6, Rectangle var7, BaseTransform var8) {
      var2[0] = var2[1] = 0.0F;
      var2[2] = var2[3] = 1.0F;
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
      FloatMap var18 = (FloatMap)this.getSamplerData(1);
      boolean var19 = false;
      boolean var20 = false;
      int var21 = var18.getWidth();
      int var22 = var18.getHeight();
      int var23 = var18.getWidth();
      float[] var24 = var18.getData();
      float[] var25 = new float[4];
      float[] var26 = new float[4];
      this.getTextureCoordinates(0, var26, (float)var15.x, (float)var15.y, (float)var10, (float)var11, var6, var16);
      float[] var27 = new float[]{0.0F, 0.0F, 1.0F, 1.0F};
      int var28 = var6.width;
      int var29 = var6.height;
      HeapImage var30 = (HeapImage)this.getRenderer().getCompatibleImage(var28, var29);
      this.setDestNativeBounds(var30.getPhysicalWidth(), var30.getPhysicalHeight());
      int var31 = var30.getScanlineStride();
      int[] var32 = var30.getPixelArray();
      float[] var38 = this.getImagetx();
      float var39 = var38[0];
      float var40 = var38[1];
      float var41 = var38[2];
      float var42 = var38[3];
      float var43 = this.getWrap();
      float[] var44 = this.getSampletx();
      float var45 = var44[0];
      float var46 = var44[1];
      float var47 = var44[2];
      float var48 = var44[3];
      float var49 = (var26[2] - var26[0]) / (float)var28;
      float var50 = (var26[3] - var26[1]) / (float)var29;
      float var51 = (var27[2] - var27[0]) / (float)var28;
      float var52 = (var27[3] - var27[1]) / (float)var29;
      float var53 = var26[1] + var50 * 0.5F;
      float var54 = var27[1] + var52 * 0.5F;

      for (int var55 = 0; var55 < 0 + var29; var55++) {
         float var56 = (float)var55;
         int var33 = var55 * var31;
         float var57 = var26[0] + var49 * 0.5F;
         float var58 = var27[0] + var51 * 0.5F;

         for (int var59 = 0; var59 < 0 + var28; var59++) {
            float var60 = (float)var59;
            this.fsample(var24, var58, var54, var21, var22, var23, var25);
            float var61 = var25[0];
            float var62 = var25[1];
            float var63 = var25[2];
            float var64 = var25[3];
            float var69 = var57 + var47 * (var61 + var45);
            float var70 = var53 + var48 * (var62 + var46);
            float var71 = (float)Math.floor((double)var69);
            float var72 = (float)Math.floor((double)var70);
            var69 -= var43 * var71;
            var70 -= var43 * var72;
            var69 = var39 + var69 * var41;
            var70 = var40 + var70 * var42;
            this.lsample(var13, var69, var70, var10, var11, var12, var17);
            var61 = var17[0];
            var62 = var17[1];
            var63 = var17[2];
            var64 = var17[3];
            float var34 = var61;
            float var35 = var62;
            float var36 = var63;
            float var37 = var64;
            if (var64 < 0.0F) {
               var37 = 0.0F;
            } else if (var64 > 1.0F) {
               var37 = 1.0F;
            }

            if (var61 < 0.0F) {
               var34 = 0.0F;
            } else if (var61 > var37) {
               var34 = var37;
            }

            if (var62 < 0.0F) {
               var35 = 0.0F;
            } else if (var62 > var37) {
               var35 = var37;
            }

            if (var63 < 0.0F) {
               var36 = 0.0F;
            } else if (var63 > var37) {
               var36 = var37;
            }

            var32[var33 + var59] = (int)(var34 * 255.0F) << 16 | (int)(var35 * 255.0F) << 8 | (int)(var36 * 255.0F) << 0 | (int)(var37 * 255.0F) << 24;
            var57 += var49;
            var58 += var51;
         }

         var53 += var50;
         var54 += var52;
      }

      return new ImageData(this.getFilterContext(), var30, var6);
   }
}
