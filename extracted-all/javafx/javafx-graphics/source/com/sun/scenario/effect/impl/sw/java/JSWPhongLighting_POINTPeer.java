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

public class JSWPhongLighting_POINTPeer extends JSWEffectPeer {
   private FloatBuffer kvals;

   public JSWPhongLighting_POINTPeer(FilterContext var1, Renderer var2, String var3) {
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
      float[] var39 = this.getLightPosition();
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
      float var52 = this.getSurfaceScale();
      float var53 = (var27[2] - var27[0]) / (float)var29;
      float var54 = (var27[3] - var27[1]) / (float)var30;
      float var55 = (var28[2] - var28[0]) / (float)var29;
      float var56 = (var28[3] - var28[1]) / (float)var30;
      float var57 = var27[1] + var54 * 0.5F;
      float var58 = var28[1] + var56 * 0.5F;

      for (int var59 = 0; var59 < 0 + var30; var59++) {
         float var60 = (float)var59;
         int var34 = var59 * var32;
         float var61 = var27[0] + var53 * 0.5F;
         float var62 = var28[0] + var55 * 0.5F;

         for (int var63 = 0; var63 < 0 + var29; var63++) {
            float var64 = (float)var63;
            int var71;
            if (var62 >= 0.0F && var58 >= 0.0F) {
               int var72 = (int)(var62 * (float)var20);
               int var73 = (int)(var58 * (float)var21);
               boolean var74 = var72 >= var20 || var73 >= var21;
               var71 = var74 ? 0 : var23[var73 * var22 + var72];
            } else {
               var71 = 0;
            }

            float var65 = (float)(var71 >> 16 & 0xFF) / 255.0F;
            float var66 = (float)(var71 >> 8 & 0xFF) / 255.0F;
            float var67 = (float)(var71 & 0xFF) / 255.0F;
            float var68 = (float)(var71 >>> 24) / 255.0F;
            float var123 = 0.0F;
            float var75 = 0.0F;
            float var76 = 1.0F;

            for (int var122 = 0; var122 < 8; var122++) {
               float var77 = var61 + var45[var122 * 4 + 0];
               float var78 = var57 + var45[var122 * 4 + 1];
               int var79;
               if (var77 >= 0.0F && var78 >= 0.0F) {
                  int var80 = (int)(var77 * (float)var10);
                  int var81 = (int)(var78 * (float)var11);
                  boolean var82 = var80 >= var10 || var81 >= var11;
                  var79 = var82 ? 0 : var13[var81 * var12 + var80];
               } else {
                  var79 = 0;
               }

               var68 = (float)(var79 >>> 24) / 255.0F;
               var123 += var45[var122 * 4 + 2] * var68;
               var75 += var45[var122 * 4 + 3] * var68;
            }

            float var83 = (float)Math.sqrt((double)(var123 * var123 + var75 * var75 + var76 * var76));
            float var124 = var123 / var83;
            float var127 = var75 / var83;
            float var130 = var76 / var83;
            int var85;
            if (var61 >= 0.0F && var57 >= 0.0F) {
               int var86 = (int)(var61 * (float)var10);
               int var87 = (int)(var57 * (float)var11);
               boolean var88 = var86 >= var10 || var87 >= var11;
               var85 = var88 ? 0 : var13[var87 * var12 + var86];
            } else {
               var85 = 0;
            }

            float var118 = (float)(var85 >>> 24) / 255.0F;
            float var136 = var52 * var118;
            float var137 = var40 - var64;
            float var139 = var41 - var60;
            float var89 = var42 - var136;
            float var90 = (float)Math.sqrt((double)(var137 * var137 + var139 * var139 + var89 * var89));
            float var125 = var137 / var90;
            float var128 = var139 / var90;
            float var131 = var89 / var90;
            float var93 = 0.0F;
            float var94 = 0.0F;
            float var95 = 1.0F;
            float var96 = var125 + var93;
            float var97 = var128 + var94;
            float var98 = var131 + var95;
            float var99 = (float)Math.sqrt((double)(var96 * var96 + var97 * var97 + var98 * var98));
            float var126 = var96 / var99;
            float var129 = var97 / var99;
            float var132 = var98 / var99;
            float var103 = var124 * var125 + var127 * var128 + var130 * var131;
            var99 = var46 * var103 * var48;
            float var100 = var46 * var103 * var49;
            float var101 = var46 * var103 * var50;
            float var110 = 0.0F;
            float var111 = 1.0F;
            float var104 = var99 < var110 ? var110 : (var99 > var111 ? var111 : var99);
            float var105 = var100 < var110 ? var110 : (var100 > var111 ? var111 : var100);
            float var106 = var101 < var110 ? var110 : (var101 > var111 ? var111 : var101);
            float var102 = 1.0F;
            var103 = var124 * var126 + var127 * var129 + var130 * var132;
            float var112 = (float)Math.pow((double)var103, (double)var43);
            float var107 = var51 * var112 * var48;
            float var108 = var51 * var112 * var49;
            float var109 = var51 * var112 * var50;
            float var113 = var107 > var108 ? var107 : var108;
            var113 = var113 > var109 ? var113 : var109;
            float var69 = var65 * var104;
            float var70 = var66 * var105;
            float var119 = var67 * var106;
            float var121 = var68 * var102;
            var107 *= var121;
            var108 *= var121;
            var109 *= var121;
            var110 = var113 * var121;
            float var35 = var107 + var69 * (1.0F - var110);
            float var36 = var108 + var70 * (1.0F - var110);
            float var37 = var109 + var119 * (1.0F - var110);
            float var38 = var110 + var121 * (1.0F - var110);
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

            var33[var34 + var63] = (int)(var35 * 255.0F) << 16 | (int)(var36 * 255.0F) << 8 | (int)(var37 * 255.0F) << 0 | (int)(var38 * 255.0F) << 24;
            var61 += var53;
            var62 += var55;
         }

         var57 += var54;
         var58 += var56;
      }

      var5[0].releaseTransformedImage(var7);
      var5[1].releaseTransformedImage(var17);
      return new ImageData(this.getFilterContext(), var31, var6);
   }
}
