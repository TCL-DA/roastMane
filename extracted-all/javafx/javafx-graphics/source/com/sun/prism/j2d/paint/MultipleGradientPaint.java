package com.sun.prism.j2d.paint;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.lang.ref.SoftReference;

public abstract class MultipleGradientPaint implements Paint {
   final int transparency;
   final float[] fractions;
   final Color[] colors;
   final AffineTransform gradientTransform;
   final MultipleGradientPaint.CycleMethod cycleMethod;
   final MultipleGradientPaint.ColorSpaceType colorSpace;
   ColorModel model;
   float[] normalizedIntervals;
   boolean isSimpleLookup;
   SoftReference<int[][]> gradients;
   SoftReference<int[]> gradient;
   int fastGradientArraySize;

   MultipleGradientPaint(float[] var1, Color[] var2, MultipleGradientPaint.CycleMethod var3, MultipleGradientPaint.ColorSpaceType var4, AffineTransform var5) {
      if (var1 == null) {
         throw new NullPointerException("Fractions array cannot be null");
      } else if (var2 == null) {
         throw new NullPointerException("Colors array cannot be null");
      } else if (var3 == null) {
         throw new NullPointerException("Cycle method cannot be null");
      } else if (var4 == null) {
         throw new NullPointerException("Color space cannot be null");
      } else if (var5 == null) {
         throw new NullPointerException("Gradient transform cannot be null");
      } else if (var1.length != var2.length) {
         throw new IllegalArgumentException("Colors and fractions must have equal size");
      } else if (var2.length < 2) {
         throw new IllegalArgumentException("User must specify at least 2 colors");
      } else {
         float var6 = -1.0F;

         for (float var10 : var1) {
            if (var10 < 0.0F || var10 > 1.0F) {
               throw new IllegalArgumentException("Fraction values must be in the range 0 to 1: " + var10);
            }

            if (var10 <= var6) {
               throw new IllegalArgumentException("Keyframe fractions must be increasing: " + var10);
            }

            var6 = var10;
         }

         boolean var13 = false;
         boolean var14 = false;
         int var15 = var1.length;
         int var16 = 0;
         if (var1[0] != 0.0F) {
            var13 = true;
            var15++;
            var16++;
         }

         if (var1[var1.length - 1] != 1.0F) {
            var14 = true;
            var15++;
         }

         this.fractions = new float[var15];
         System.arraycopy(var1, 0, this.fractions, var16, var1.length);
         this.colors = new Color[var15];
         System.arraycopy(var2, 0, this.colors, var16, var2.length);
         if (var13) {
            this.fractions[0] = 0.0F;
            this.colors[0] = var2[0];
         }

         if (var14) {
            this.fractions[var15 - 1] = 1.0F;
            this.colors[var15 - 1] = var2[var2.length - 1];
         }

         this.colorSpace = var4;
         this.cycleMethod = var3;
         this.gradientTransform = new AffineTransform(var5);
         boolean var11 = true;

         for (int var12 = 0; var12 < var2.length; var12++) {
            var11 = var11 && var2[var12].getAlpha() == 255;
         }

         this.transparency = var11 ? 1 : 3;
      }
   }

   public final float[] getFractions() {
      float[] var1 = new float[this.fractions.length];
      System.arraycopy(this.fractions, 0, var1, 0, this.fractions.length);
      return var1;
   }

   public final Color[] getColors() {
      Color[] var1 = new Color[this.fractions.length];
      System.arraycopy(this.fractions, 0, var1, 0, this.fractions.length);
      return var1;
   }

   public final MultipleGradientPaint.CycleMethod getCycleMethod() {
      return this.cycleMethod;
   }

   public final MultipleGradientPaint.ColorSpaceType getColorSpace() {
      return this.colorSpace;
   }

   public final AffineTransform getTransform() {
      return new AffineTransform(this.gradientTransform);
   }

   @Override
   public final int getTransparency() {
      return this.transparency;
   }

   public static enum ColorSpaceType {
      SRGB,
      LINEAR_RGB;
   }

   public static enum CycleMethod {
      NO_CYCLE,
      REFLECT,
      REPEAT;
   }
}
