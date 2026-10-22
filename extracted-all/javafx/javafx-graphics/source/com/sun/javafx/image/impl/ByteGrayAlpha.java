package com.sun.javafx.image.impl;

import com.sun.javafx.image.AlphaType;
import com.sun.javafx.image.BytePixelAccessor;
import com.sun.javafx.image.BytePixelGetter;
import com.sun.javafx.image.BytePixelSetter;
import com.sun.javafx.image.ByteToBytePixelConverter;
import com.sun.javafx.image.PixelUtils;
import java.nio.ByteBuffer;

public class ByteGrayAlpha {
   public static final BytePixelGetter getter = ByteGrayAlpha.Accessor.nonpremul;
   public static final BytePixelSetter setter = ByteGrayAlpha.Accessor.nonpremul;
   public static final BytePixelAccessor accessor = ByteGrayAlpha.Accessor.nonpremul;

   public static ByteToBytePixelConverter ToByteGrayAlphaPreConverter() {
      return ByteGrayAlpha.ToByteGrayAlphaPreConv.instance;
   }

   public static ByteToBytePixelConverter ToByteBgraConverter() {
      return ByteGrayAlpha.ToByteBgraSameConv.nonpremul;
   }

   static class Accessor implements BytePixelAccessor {
      static final BytePixelAccessor nonpremul = new ByteGrayAlpha.Accessor(false);
      static final BytePixelAccessor premul = new ByteGrayAlpha.Accessor(true);
      private boolean isPremult;

      private Accessor(boolean var1) {
         this.isPremult = var1;
      }

      @Override
      public AlphaType getAlphaType() {
         return this.isPremult ? AlphaType.PREMULTIPLIED : AlphaType.NONPREMULTIPLIED;
      }

      @Override
      public int getNumElements() {
         return 2;
      }

      @Override
      public int getArgb(byte[] var1, int var2) {
         int var3 = var1[var2] & 255;
         int var4 = var1[var2 + 1] & 255;
         if (this.isPremult) {
            var3 = PixelUtils.PreToNonPre(var3, var4);
         }

         return var4 << 24 | var3 << 16 | var3 << 8 | var3;
      }

      @Override
      public int getArgbPre(byte[] var1, int var2) {
         int var3 = var1[var2] & 255;
         int var4 = var1[var2 + 1] & 255;
         if (!this.isPremult) {
            var3 = PixelUtils.NonPretoPre(var3, var4);
         }

         return var4 << 24 | var3 << 16 | var3 << 8 | var3;
      }

      public int getArgb(ByteBuffer var1, int var2) {
         int var3 = var1.get(var2) & 255;
         int var4 = var1.get(var2 + 1) & 255;
         if (this.isPremult) {
            var3 = PixelUtils.PreToNonPre(var3, var4);
         }

         return var4 << 24 | var3 << 16 | var3 << 8 | var3;
      }

      public int getArgbPre(ByteBuffer var1, int var2) {
         int var3 = var1.get(var2) & 255;
         int var4 = var1.get(var2 + 1) & 255;
         if (!this.isPremult) {
            var3 = PixelUtils.NonPretoPre(var3, var4);
         }

         return var4 << 24 | var3 << 16 | var3 << 8 | var3;
      }

      @Override
      public void setArgb(byte[] var1, int var2, int var3) {
         int var4 = PixelUtils.RgbToGray(var3);
         int var5 = var3 >>> 24;
         if (this.isPremult) {
            var4 = PixelUtils.NonPretoPre(var4, var5);
         }

         var1[var2] = (byte)var4;
         var1[var2 + 1] = (byte)var5;
      }

      @Override
      public void setArgbPre(byte[] var1, int var2, int var3) {
         int var4 = PixelUtils.RgbToGray(var3);
         int var5 = var3 >>> 24;
         if (!this.isPremult) {
            var4 = PixelUtils.PreToNonPre(var4, var5);
         }

         var1[var2] = (byte)var4;
         var1[var2 + 1] = (byte)var5;
      }

      public void setArgb(ByteBuffer var1, int var2, int var3) {
         int var4 = PixelUtils.RgbToGray(var3);
         int var5 = var3 >>> 24;
         if (this.isPremult) {
            var4 = PixelUtils.NonPretoPre(var4, var5);
         }

         var1.put(var2, (byte)var4);
         var1.put(var2 + 1, (byte)var5);
      }

      public void setArgbPre(ByteBuffer var1, int var2, int var3) {
         int var4 = PixelUtils.RgbToGray(var3);
         int var5 = var3 >>> 24;
         if (!this.isPremult) {
            var4 = PixelUtils.PreToNonPre(var4, var5);
         }

         var1.put(var2, (byte)var4);
         var1.put(var2 + 1, (byte)var5);
      }
   }

   static class ToByteBgraSameConv extends BaseByteToByteConverter {
      static final ByteToBytePixelConverter nonpremul = new ByteGrayAlpha.ToByteBgraSameConv(false);
      static final ByteToBytePixelConverter premul = new ByteGrayAlpha.ToByteBgraSameConv(true);

      private ToByteBgraSameConv(boolean var1) {
         super(var1 ? ByteGrayAlphaPre.getter : ByteGrayAlpha.getter, var1 ? ByteBgraPre.setter : ByteBgra.setter);
      }

      @Override
      void doConvert(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 2;
         var6 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               byte var10 = var1[var2++];
               byte var11 = var1[var2++];
               var4[var5++] = var10;
               var4[var5++] = var10;
               var4[var5++] = var10;
               var4[var5++] = var11;
            }

            var2 += var3;
            var5 += var6;
         }
      }

      @Override
      void doConvert(ByteBuffer var1, int var2, int var3, ByteBuffer var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 2;
         var6 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               byte var10 = var1.get(var2++);
               byte var11 = var1.get(var2++);
               var4.put(var5++, var10);
               var4.put(var5++, var10);
               var4.put(var5++, var10);
               var4.put(var5++, var11);
            }

            var2 += var3;
            var5 += var6;
         }
      }
   }

   static class ToByteGrayAlphaPreConv extends BaseByteToByteConverter {
      static final ByteToBytePixelConverter instance = new ByteGrayAlpha.ToByteGrayAlphaPreConv();

      private ToByteGrayAlphaPreConv() {
         super(ByteGrayAlpha.getter, ByteGrayAlphaPre.setter);
      }

      @Override
      void doConvert(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 2;
         var6 -= var7 * 2;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               int var10 = var1[var2++] & 255;
               byte var11 = var1[var2++];
               if (var11 != -1) {
                  if (var11 == 0) {
                     var10 = 0;
                  } else {
                     var10 = (var10 * (var11 & 255) + 127) / 255;
                  }
               }

               var4[var5++] = (byte)var10;
               var4[var5++] = (byte)var11;
            }

            var2 += var3;
            var5 += var6;
         }
      }

      @Override
      void doConvert(ByteBuffer var1, int var2, int var3, ByteBuffer var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 2;
         var6 -= var7 * 2;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               int var10 = var1.get(var2++) & 255;
               byte var11 = var1.get(var2++);
               if (var11 != -1) {
                  if (var11 == 0) {
                     var10 = 0;
                  } else {
                     var10 = (var10 * (var11 & 255) + 127) / 255;
                  }
               }

               var4.put(var5++, (byte)var10);
               var4.put(var5++, (byte)var11);
            }

            var2 += var3;
            var5 += var6;
         }
      }
   }
}
