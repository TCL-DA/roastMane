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

public class JSWPhongLighting_SPOTPeer extends JSWEffectPeer {
   private FloatBuffer kvals;

   public JSWPhongLighting_SPOTPeer(FilterContext var1, Renderer var2, String var3) {
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
      float var43 = this.getLightSpecularExponent();
      float var44 = this.getSpecularExponent();
      FloatBuffer var45 = this.getKvals();
      float[] var46 = new float[var45.capacity()];
      var45.get(var46);
      float var47 = this.getDiffuseConstant();
      float[] var48 = this.getLightColor();
      float var49 = var48[0];
      float var50 = var48[1];
      float var51 = var48[2];
      float[] var52 = this.getNormalizedLightDirection();
      float var53 = var52[0];
      float var54 = var52[1];
      float var55 = var52[2];
      float var56 = this.getSpecularConstant();
      float var57 = this.getSurfaceScale();
      float var58 = (var27[2] - var27[0]) / (float)var29;
      float var59 = (var27[3] - var27[1]) / (float)var30;
      float var60 = (var28[2] - var28[0]) / (float)var29;
      float var61 = (var28[3] - var28[1]) / (float)var30;
      float var62 = var27[1] + var59 * 0.5F;
      float var63 = var28[1] + var61 * 0.5F;

      for (int var64 = 0; var64 < 0 + var30; var64++) {
         float var65 = (float)var64;
         int var34 = var64 * var32;
         float var66 = var27[0] + var58 * 0.5F;
         float var67 = var28[0] + var60 * 0.5F;

         for (int var68 = 0; var68 < 0 + var29; var68++) {
            float var69 = (float)var68;
            int var76;
            if (var67 >= 0.0F && var63 >= 0.0F) {
               int var77 = (int)(var67 * (float)var20);
               int var78 = (int)(var63 * (float)var21);
               boolean var79 = var77 >= var20 || var78 >= var21;
               var76 = var79 ? 0 : var23[var78 * var22 + var77];
            } else {
               var76 = 0;
            }

            float var70 = (float)(var76 >> 16 & 0xFF) / 255.0F;
            float var71 = (float)(var76 >> 8 & 0xFF) / 255.0F;
            float var72 = (float)(var76 & 0xFF) / 255.0F;
            float var73 = (float)(var76 >>> 24) / 255.0F;
            float var131 = 0.0F;
            float var80 = 0.0F;
            float var81 = 1.0F;

            for (int var130 = 0; var130 < 8; var130++) {
               float var82 = var66 + var46[var130 * 4 + 0];
               float var83 = var62 + var46[var130 * 4 + 1];
               int var84;
               if (var82 >= 0.0F && var83 >= 0.0F) {
                  int var85 = (int)(var82 * (float)var10);
                  int var86 = (int)(var83 * (float)var11);
                  boolean var87 = var85 >= var10 || var86 >= var11;
                  var84 = var87 ? 0 : var13[var86 * var12 + var85];
               } else {
                  var84 = 0;
               }

               var73 = (float)(var84 >>> 24) / 255.0F;
               var131 += var46[var130 * 4 + 2] * var73;
               var80 += var46[var130 * 4 + 3] * var73;
            }

            float var88 = (float)Math.sqrt((double)(var131 * var131 + var80 * var80 + var81 * var81));
            float var132 = var131 / var88;
            float var135 = var80 / var88;
            float var138 = var81 / var88;
            int var90;
            if (var66 >= 0.0F && var62 >= 0.0F) {
               int var91 = (int)(var66 * (float)var10);
               int var92 = (int)(var62 * (float)var11);
               boolean var93 = var91 >= var10 || var92 >= var11;
               var90 = var93 ? 0 : var13[var92 * var12 + var91];
            } else {
               var90 = 0;
            }

            float var126 = (float)(var90 >>> 24) / 255.0F;
            float var144 = var57 * var126;
            float var145 = var40 - var69;
            float var147 = var41 - var65;
            float var94 = var42 - var144;
            float var95 = (float)Math.sqrt((double)(var145 * var145 + var147 * var147 + var94 * var94));
            float var133 = var145 / var95;
            float var136 = var147 / var95;
            float var139 = var94 / var95;
            var95 = var133 * var53 + var136 * var54 + var139 * var55;
            float var99 = 0.0F;
            float var97 = var95 < var99 ? var95 : var99;
            var99 = -var97;
            float var98 = (float)Math.pow((double)var99, (double)var43);
            var99 = var49 * var98;
            float var100 = var50 * var98;
            float var101 = var51 * var98;
            float var102 = 0.0F;
            float var103 = 0.0F;
            float var104 = 1.0F;
            float var105 = var133 + var102;
            float var106 = var136 + var103;
            float var107 = var139 + var104;
            float var108 = (float)Math.sqrt((double)(var105 * var105 + var106 * var106 + var107 * var107));
            float var134 = var105 / var108;
            float var137 = var106 / var108;
            float var140 = var107 / var108;
            var95 = var132 * var133 + var135 * var136 + var138 * var139;
            var108 = var47 * var95 * var99;
            float var109 = var47 * var95 * var100;
            float var110 = var47 * var95 * var101;
            float var118 = 0.0F;
            float var119 = 1.0F;
            float var112 = var108 < var118 ? var118 : (var108 > var119 ? var119 : var108);
            float var113 = var109 < var118 ? var118 : (var109 > var119 ? var119 : var109);
            float var114 = var110 < var118 ? var118 : (var110 > var119 ? var119 : var110);
            float var111 = 1.0F;
            var95 = var132 * var134 + var135 * var137 + var138 * var140;
            var98 = (float)Math.pow((double)var95, (double)var44);
            float var115 = var56 * var98 * var99;
            float var116 = var56 * var98 * var100;
            float var117 = var56 * var98 * var101;
            float var120 = var115 > var116 ? var115 : var116;
            var120 = var120 > var117 ? var120 : var117;
            float var74 = var70 * var112;
            float var75 = var71 * var113;
            float var127 = var72 * var114;
            float var129 = var73 * var111;
            var115 *= var129;
            var116 *= var129;
            var117 *= var129;
            var118 = var120 * var129;
            float var35 = var115 + var74 * (1.0F - var118);
            float var36 = var116 + var75 * (1.0F - var118);
            float var37 = var117 + var127 * (1.0F - var118);
            float var38 = var118 + var129 * (1.0F - var118);
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

            var33[var34 + var68] = (int)(var35 * 255.0F) << 16 | (int)(var36 * 255.0F) << 8 | (int)(var37 * 255.0F) << 0 | (int)(var38 * 255.0F) << 24;
            var66 += var58;
            var67 += var60;
         }

         var62 += var59;
         var63 += var61;
      }

      var5[0].releaseTransformedImage(var7);
      var5[1].releaseTransformedImage(var17);
      return new ImageData(this.getFilterContext(), var31, var6);
   }
}
