package com.sun.scenario.effect.impl.sw.java;

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.ColorAdjust;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.impl.HeapImage;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.state.RenderState;

public class JSWColorAdjustPeer extends JSWEffectPeer {
   public JSWColorAdjustPeer(FilterContext var1, Renderer var2, String var3) {
      super(var1, var2, var3);
   }

   protected final ColorAdjust getEffect() {
      return (ColorAdjust)super.getEffect();
   }

   private float getHue() {
      return this.getEffect().getHue() / 2.0F;
   }

   private float getSaturation() {
      return this.getEffect().getSaturation() + 1.0F;
   }

   private float getBrightness() {
      return this.getEffect().getBrightness() + 1.0F;
   }

   private float getContrast() {
      float var1 = this.getEffect().getContrast();
      if (var1 > 0.0F) {
         var1 *= 3.0F;
      }

      return var1 + 1.0F;
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
      float var28 = this.getSaturation();
      float var29 = this.getBrightness();
      float var30 = this.getContrast();
      float var31 = this.getHue();
      float var32 = (var17[2] - var17[0]) / (float)var18;
      float var33 = (var17[3] - var17[1]) / (float)var19;
      float var34 = var17[1] + var33 * 0.5F;

      for (int var35 = 0; var35 < 0 + var19; var35++) {
         float var36 = (float)var35;
         int var23 = var35 * var21;
         float var37 = var17[0] + var32 * 0.5F;

         for (int var38 = 0; var38 < 0 + var18; var38++) {
            float var39 = (float)var38;
            int var46;
            if (var37 >= 0.0F && var34 >= 0.0F) {
               int var47 = (int)(var37 * (float)var10);
               int var48 = (int)(var34 * (float)var11);
               boolean var49 = var47 >= var10 || var48 >= var11;
               var46 = var49 ? 0 : var13[var48 * var12 + var47];
            } else {
               var46 = 0;
            }

            float var40 = (float)(var46 >> 16 & 0xFF) / 255.0F;
            float var41 = (float)(var46 >> 8 & 0xFF) / 255.0F;
            float var42 = (float)(var46 & 0xFF) / 255.0F;
            float var43 = (float)(var46 >>> 24) / 255.0F;
            float var44 = var40;
            float var45 = var41;
            float var76 = var42;
            if (var43 > 0.0F) {
               var44 = var40 / var43;
               var45 = var41 / var43;
               var76 = var42 / var43;
            }

            var44 = (var44 - 0.5F) * var30 + 0.5F;
            var45 = (var45 - 0.5F) * var30 + 0.5F;
            float var77 = (var76 - 0.5F) * var30 + 0.5F;
            float var57 = var44 > var45 ? var44 : var45;
            var57 = var57 > var77 ? var57 : var77;
            float var59 = var44 < var45 ? var44 : var45;
            var59 = var59 < var77 ? var59 : var77;
            float var55;
            float var78;
            if (var57 > var59) {
               float var61 = (var57 - var44) / (var57 - var59);
               float var62 = (var57 - var45) / (var57 - var59);
               float var63 = (var57 - var77) / (var57 - var59);
               if (var44 == var57) {
                  var78 = var63 - var62;
               } else if (var45 == var57) {
                  var78 = 2.0F + var61 - var63;
               } else {
                  var78 = 4.0F + var62 - var61;
               }

               var78 /= 6.0F;
               if (var78 < 0.0F) {
                  var78++;
               }

               var55 = (var57 - var59) / var57;
            } else {
               var78 = 0.0F;
               var55 = 0.0F;
            }

            float var51 = var78 + var31;
            if (var51 < 0.0F) {
               var51++;
            } else if (var51 > 1.0F) {
               var51--;
            }

            float var52;
            if (var28 > 1.0F) {
               var78 = var28 - 1.0F;
               var52 = var55 + (1.0F - var55) * var78;
            } else {
               var52 = var55 * var28;
            }

            float var53;
            if (var29 > 1.0F) {
               var78 = var29 - 1.0F;
               var52 *= 1.0F - var78;
               var53 = var57 + (1.0F - var57) * var78;
            } else {
               var53 = var57 * var29;
            }

            float var58 = 0.0F;
            var59 = 1.0F;
            var78 = var52 < var58 ? var58 : (var52 > var59 ? var59 : var52);
            var55 = var53 < var58 ? var58 : (var53 > var59 ? var59 : var53);
            float var86 = 0.0F;
            float var88 = 0.0F;
            float var64 = 0.0F;
            float var68 = (float)Math.floor((double)var51);
            float var65 = (var51 - var68) * 6.0F;
            var68 = (float)Math.floor((double)var65);
            float var69 = var65 - var68;
            float var70 = var55 * (1.0F - var78);
            float var71 = var55 * (1.0F - var78 * var69);
            float var72 = var55 * (1.0F - var78 * (1.0F - var69));
            var68 = (float)Math.floor((double)var65);
            if (var68 < 1.0F) {
               var86 = var55;
               var88 = var72;
               var64 = var70;
            } else if (var68 < 2.0F) {
               var86 = var71;
               var88 = var55;
               var64 = var70;
            } else if (var68 < 3.0F) {
               var86 = var70;
               var88 = var55;
               var64 = var72;
            } else if (var68 < 4.0F) {
               var86 = var70;
               var88 = var71;
               var64 = var55;
            } else if (var68 < 5.0F) {
               var86 = var72;
               var88 = var70;
               var64 = var55;
            } else {
               var86 = var55;
               var88 = var70;
               var64 = var71;
            }

            float var24 = var43 * var86;
            float var25 = var43 * var88;
            float var26 = var43 * var64;
            float var27 = var43;
            if (var43 < 0.0F) {
               var27 = 0.0F;
            } else if (var43 > 1.0F) {
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

            var22[var23 + var38] = (int)(var24 * 255.0F) << 16 | (int)(var25 * 255.0F) << 8 | (int)(var26 * 255.0F) << 0 | (int)(var27 * 255.0F) << 24;
            var37 += var32;
         }

         var34 += var33;
      }

      var5[0].releaseTransformedImage(var7);
      return new ImageData(this.getFilterContext(), var20, var6);
   }
}
