package com.sun.scenario.effect.impl.sw.java;

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.PhongLighting;
import com.sun.scenario.effect.impl.BufferUtil;
import com.sun.scenario.effect.impl.HeapImage;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.state.RenderState;
import com.sun.scenario.effect.light.PointLight;
import com.sun.scenario.effect.light.SpotLight;
import java.nio.FloatBuffer;

public class JSWPhongLighting_DISTANTPeer extends JSWEffectPeer {
   private FloatBuffer kvals;

   public JSWPhongLighting_DISTANTPeer(FilterContext var1, Renderer var2, String var3) {
      super(var1, var2, var3);
   }

   protected final PhongLighting getEffect() {
      return (PhongLighting)super.getEffect();
   }

   private float getSurfaceScale() {
      return this.getEffect().getSurfaceScale();
   }

   private float getDiffuseConstant() {
      return this.getEffect().getDiffuseConstant();
   }

   private float getSpecularConstant() {
      return this.getEffect().getSpecularConstant();
   }

   private float getSpecularExponent() {
      return this.getEffect().getSpecularExponent();
   }

   private float[] getNormalizedLightPosition() {
      return this.getEffect().getLight().getNormalizedLightPosition();
   }

   private float[] getLightPosition() {
      PointLight var1 = (PointLight)this.getEffect().getLight();
      return new float[]{var1.getX(), var1.getY(), var1.getZ()};
   }

   private float[] getLightColor() {
      return this.getEffect().getLight().getColor().getPremultipliedRGBComponents();
   }

   private float getLightSpecularExponent() {
      return ((SpotLight)this.getEffect().getLight()).getSpecularExponent();
   }

   private float[] getNormalizedLightDirection() {
      return ((SpotLight)this.getEffect().getLight()).getNormalizedLightDirection();
   }

   private FloatBuffer getKvals() {
      Rectangle var1 = this.getInputNativeBounds(0);
      float var2 = 1.0F / (float)var1.width;
      float var3 = 1.0F / (float)var1.height;
      float[] var4 = new float[]{-1.0F, 0.0F, 1.0F, -2.0F, 0.0F, 2.0F, -1.0F, 0.0F, 1.0F};
      float[] var5 = new float[]{-1.0F, -2.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F};
      if (this.kvals == null) {
         this.kvals = BufferUtil.newFloatBuffer(32);
      }

      this.kvals.clear();
      int var6 = 0;
      float var7 = -this.getSurfaceScale() * 0.25F;

      for (int var8 = -1; var8 <= 1; var8++) {
         for (int var9 = -1; var9 <= 1; var9++) {
            if (var8 != 0 || var9 != 0) {
               this.kvals.put((float)var9 * var2);
               this.kvals.put((float)var8 * var3);
               this.kvals.put(var4[var6] * var7);
               this.kvals.put(var5[var6] * var7);
            }

            var6++;
         }
      }

      this.kvals.rewind();
      return this.kvals;
   }

   private int getKvalsArrayLength() {
      return 8;
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
      float[] var39 = this.getNormalizedLightPosition();
      float var40 = var39[0];
      float var41 = var39[1];
      float var42 = var39[2];
      float var43 = this.getSpecularExponent();
      FloatBuffer var44 = this.getKvals();
      float[] var45 = new float[var44.capacity()];
      var44.get(var45);
      float var46 = this.getDiffuseConstant();
      float[] var47 = this.getLightColor();
      float var48 = var47[0];
      float var49 = var47[1];
      float var50 = var47[2];
      float var51 = this.getSpecularConstant();
      float var52 = (var27[2] - var27[0]) / (float)var29;
      float var53 = (var27[3] - var27[1]) / (float)var30;
      float var54 = (var28[2] - var28[0]) / (float)var29;
      float var55 = (var28[3] - var28[1]) / (float)var30;
      float var56 = var27[1] + var53 * 0.5F;
      float var57 = var28[1] + var55 * 0.5F;

      for (int var58 = 0; var58 < 0 + var30; var58++) {
         float var59 = (float)var58;
         int var34 = var58 * var32;
         float var60 = var27[0] + var52 * 0.5F;
         float var61 = var28[0] + var54 * 0.5F;

         for (int var62 = 0; var62 < 0 + var29; var62++) {
            float var63 = (float)var62;
            int var70;
            if (var61 >= 0.0F && var57 >= 0.0F) {
               int var71 = (int)(var61 * (float)var20);
               int var72 = (int)(var57 * (float)var21);
               boolean var73 = var71 >= var20 || var72 >= var21;
               var70 = var73 ? 0 : var23[var72 * var22 + var71];
            } else {
               var70 = 0;
            }

            float var64 = (float)(var70 >> 16 & 0xFF) / 255.0F;
            float var65 = (float)(var70 >> 8 & 0xFF) / 255.0F;
            float var66 = (float)(var70 & 0xFF) / 255.0F;
            float var67 = (float)(var70 >>> 24) / 255.0F;
            float var117 = 0.0F;
            float var74 = 0.0F;
            float var75 = 1.0F;

            for (int var116 = 0; var116 < 8; var116++) {
               float var76 = var60 + var45[var116 * 4 + 0];
               float var77 = var56 + var45[var116 * 4 + 1];
               int var78;
               if (var76 >= 0.0F && var77 >= 0.0F) {
                  int var79 = (int)(var76 * (float)var10);
                  int var80 = (int)(var77 * (float)var11);
                  boolean var81 = var79 >= var10 || var80 >= var11;
                  var78 = var81 ? 0 : var13[var80 * var12 + var79];
               } else {
                  var78 = 0;
               }

               var67 = (float)(var78 >>> 24) / 255.0F;
               var117 += var45[var116 * 4 + 2] * var67;
               var74 += var45[var116 * 4 + 3] * var67;
            }

            float var82 = (float)Math.sqrt((double)(var117 * var117 + var74 * var74 + var75 * var75));
            float var118 = var117 / var82;
            float var120 = var74 / var82;
            float var122 = var75 / var82;
            float var88 = 0.0F;
            float var89 = 0.0F;
            float var90 = 1.0F;
            float var91 = var40 + var88;
            float var92 = var41 + var89;
            float var93 = var42 + var90;
            float var94 = (float)Math.sqrt((double)(var91 * var91 + var92 * var92 + var93 * var93));
            float var119 = var91 / var94;
            float var121 = var92 / var94;
            float var123 = var93 / var94;
            float var98 = var118 * var40 + var120 * var41 + var122 * var42;
            var94 = var46 * var98 * var48;
            float var95 = var46 * var98 * var49;
            float var96 = var46 * var98 * var50;
            float var105 = 0.0F;
            float var106 = 1.0F;
            float var99 = var94 < var105 ? var105 : (var94 > var106 ? var106 : var94);
            float var100 = var95 < var105 ? var105 : (var95 > var106 ? var106 : var95);
            float var101 = var96 < var105 ? var105 : (var96 > var106 ? var106 : var96);
            float var97 = 1.0F;
            var98 = var118 * var119 + var120 * var121 + var122 * var123;
            float var107 = (float)Math.pow((double)var98, (double)var43);
            float var102 = var51 * var107 * var48;
            float var103 = var51 * var107 * var49;
            float var104 = var51 * var107 * var50;
            float var108 = var102 > var103 ? var102 : var103;
            var108 = var108 > var104 ? var108 : var104;
            float var68 = var64 * var99;
            float var69 = var65 * var100;
            float var113 = var66 * var101;
            float var115 = var67 * var97;
            var102 *= var115;
            var103 *= var115;
            var104 *= var115;
            var105 = var108 * var115;
            float var35 = var102 + var68 * (1.0F - var105);
            float var36 = var103 + var69 * (1.0F - var105);
            float var37 = var104 + var113 * (1.0F - var105);
            float var38 = var105 + var115 * (1.0F - var105);
            if (var38 < 0.0F) {
               var38 = 0.0F;
            } else if (var38 > 1.0F) {
               var38 = 1.0F;
            }

            if (var35 < 0.0F) {
               var35 = 0.0F;
            } else if (var35 > var38) {
               var35 = var38;
            }

            if (var36 < 0.0F) {
               var36 = 0.0F;
            } else if (var36 > var38) {
               var36 = var38;
            }

            if (var37 < 0.0F) {
               var37 = 0.0F;
            } else if (var37 > var38) {
               var37 = var38;
            }

            var33[var34 + var62] = (int)(var35 * 255.0F) << 16 | (int)(var36 * 255.0F) << 8 | (int)(var37 * 255.0F) << 0 | (int)(var38 * 255.0F) << 24;
            var60 += var52;
            var61 += var54;
         }

         var56 += var53;
         var57 += var55;
      }

      var5[0].releaseTransformedImage(var7);
      var5[1].releaseTransformedImage(var17);
      return new ImageData(this.getFilterContext(), var31, var6);
   }
}
