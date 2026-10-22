package com.sun.prism.impl.paint;

import com.sun.javafx.geom.transform.Affine2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.paint.Color;
import com.sun.prism.paint.Gradient;
import com.sun.prism.paint.LinearGradient;
import com.sun.prism.paint.Paint;
import com.sun.prism.paint.RadialGradient;
import com.sun.prism.paint.Stop;

public class PaintUtil {
   private static final Affine2D gradXform = new Affine2D();

   public static void fillImageWithGradient(
      int[] var0, Gradient var1, BaseTransform var2, int var3, int var4, int var5, int var6, float var7, float var8, float var9, float var10
   ) {
      Gradient var11 = var1;
      int var12 = var1.getNumStops();
      float[] var13 = new float[var12];
      Color[] var14 = new Color[var12];

      for (int var15 = 0; var15 < var12; var15++) {
         Stop var16 = var11.getStops().get(var15);
         var13[var15] = var16.getOffset();
         var14[var15] = var16.getColor();
      }

      Object var26;
      if (var1.getType() == Paint.Type.LINEAR_GRADIENT) {
         LinearGradient var27 = (LinearGradient)var1;
         float var17;
         float var18;
         float var19;
         float var20;
         if (var27.isProportional()) {
            var17 = var27.getX1() * var9 + var7;
            var18 = var27.getY1() * var10 + var8;
            var19 = var27.getX2() * var9 + var7;
            var20 = var27.getY2() * var10 + var8;
         } else {
            var17 = var27.getX1();
            var18 = var27.getY1();
            var19 = var27.getX2();
            var20 = var27.getY2();
         }

         if (var17 == var19 && var18 == var20) {
            var17 -= 1.0E-6F;
            var19 += 1.0E-6F;
         }

         var26 = new LinearGradientContext(var27, var2, var17, var18, var19, var20, var13, var14, var27.getSpreadMethod());
      } else {
         RadialGradient var28 = (RadialGradient)var1;
         gradXform.setTransform(var2);
         float var29 = var28.getRadius();
         float var30 = var28.getCenterX();
         float var31 = var28.getCenterY();
         double var32 = Math.toRadians((double)var28.getFocusAngle());
         float var22 = var28.getFocusDistance();
         if (var28.isProportional()) {
            float var23 = var7 + var9 / 2.0F;
            float var24 = var8 + var10 / 2.0F;
            float var25 = Math.min(var9, var10);
            var30 = (var30 - 0.5F) * var25 + var23;
            var31 = (var31 - 0.5F) * var25 + var24;
            if (var9 != var10 && var9 != 0.0F && var10 != 0.0F) {
               gradXform.translate((double)var23, (double)var24);
               gradXform.scale((double)(var9 / var25), (double)(var10 / var25));
               gradXform.translate((double)(-var23), (double)(-var24));
            }

            var29 *= var25;
         }

         if (var29 <= 0.0F) {
            var29 = 0.001F;
         }

         var22 *= var29;
         float var34 = (float)((double)var30 + (double)var22 * Math.cos(var32));
         float var35 = (float)((double)var31 + (double)var22 * Math.sin(var32));
         var26 = new RadialGradientContext(var28, gradXform, var30, var31, var29, var34, var35, var13, var14, var28.getSpreadMethod());
      }

      ((MultipleGradientContext)var26).fillRaster(var0, 0, 0, var3, var4, var5, var6);
   }
}
