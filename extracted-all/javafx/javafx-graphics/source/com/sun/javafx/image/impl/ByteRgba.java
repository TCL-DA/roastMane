package com.sun.javafx.image.impl;

import com.sun.javafx.image.AlphaType;
import com.sun.javafx.image.BytePixelAccessor;
import com.sun.javafx.image.BytePixelGetter;
import com.sun.javafx.image.BytePixelSetter;
import com.sun.javafx.image.ByteToBytePixelConverter;
import com.sun.javafx.image.ByteToIntPixelConverter;
import com.sun.javafx.image.PixelUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ByteRgba {
   public static final BytePixelGetter getter = ByteRgba.Accessor.instance;
   public static final BytePixelSetter setter = ByteRgba.Accessor.instance;
   public static final BytePixelAccessor accessor = ByteRgba.Accessor.instance;
   private static ByteToBytePixelConverter ToByteRgbaObj;
   private static ByteToBytePixelConverter ToByteBgraObj;

   public static ByteToBytePixelConverter ToByteRgbaConverter() {
      if (ToByteRgbaObj == null) {
         ToByteRgbaObj = BaseByteToByteConverter.create(accessor);
      }

      return ToByteRgbaObj;
   }

   public static ByteToBytePixelConverter ToByteBgraConverter() {
      if (ToByteBgraObj == null) {
         ToByteBgraObj = BaseByteToByteConverter.createReorderer(getter, ByteBgra.setter, 2, 1, 0, 3);
      }

      return ToByteBgraObj;
   }

   static class Accessor implements BytePixelAccessor {
      static final BytePixelAccessor instance = new ByteRgba.Accessor();

      private Accessor() {
      }

      @Override
      public AlphaType getAlphaType() {
         return AlphaType.NONPREMULTIPLIED;
      }

      @Override
      public int getNumElements() {
         return 4;
      }

      @Override
      public int getArgb(byte[] var1, int var2) {
         return var1[var2 + 2] & 0xFF | (var1[var2 + 1] & 0xFF) << 8 | (var1[var2] & 0xFF) << 16 | var1[var2 + 3] << 24;
      }

      @Override
      public int getArgbPre(byte[] var1, int var2) {
         return PixelUtils.NonPretoPre(this.getArgb(var1, var2));
      }

      public int getArgb(ByteBuffer var1, int var2) {
         return var1.get(var2 + 2) & 0xFF | (var1.get(var2 + 1) & 0xFF) << 8 | (var1.get(var2) & 0xFF) << 16 | var1.get(var2 + 3) << 24;
      }

      public int getArgbPre(ByteBuffer var1, int var2) {
         return PixelUtils.NonPretoPre(this.getArgb(var1, var2));
      }

      @Override
      public void setArgb(byte[] var1, int var2, int var3) {
         var1[var2] = (byte)(var3 >> 16);
         var1[var2 + 1] = (byte)(var3 >> 8);
         var1[var2 + 2] = (byte)var3;
         var1[var2 + 3] = (byte)(var3 >> 24);
      }

      @Override
      public void setArgbPre(byte[] var1, int var2, int var3) {
         this.setArgb(var1, var2, PixelUtils.PretoNonPre(var3));
      }

      public void setArgb(ByteBuffer var1, int var2, int var3) {
         var1.put(var2, (byte)(var3 >> 16));
         var1.put(var2 + 1, (byte)(var3 >> 8));
         var1.put(var2 + 2, (byte)var3);
         var1.put(var2 + 3, (byte)(var3 >> 24));
      }

      public void setArgbPre(ByteBuffer var1, int var2, int var3) {
         this.setArgb(var1, var2, PixelUtils.PretoNonPre(var3));
      }
   }

   static class ToByteBgraPreConv extends BaseByteToByteConverter {
      static final ByteToBytePixelConverter instance = new ByteRgba.ToByteBgraPreConv();

      private ToByteBgraPreConv() {
         super(ByteRgba.getter, ByteBgraPre.setter);
      }

      @Override
      void doConvert(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 4;
         var6 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               byte var10 = var1[var2++];
               byte var11 = var1[var2++];
               byte var12 = var1[var2++];
               int var13 = var1[var2++] & 255;
               if (var13 < 255) {
                  if (var13 == 0) {
                     var10 = 0;
                     var11 = 0;
                     var12 = 0;
                  } else {
                     var12 = (byte)(((var12 & 255) * var13 + 127) / 255);
                     var11 = (byte)(((var11 & 255) * var13 + 127) / 255);
                     var10 = (byte)(((var10 & 255) * var13 + 127) / 255);
                  }
               }

               var4[var5++] = var12;
               var4[var5++] = var11;
               var4[var5++] = var10;
               var4[var5++] = (byte)var13;
            }

            var2 += var3;
            var5 += var6;
         }
      }

      @Override
      void doConvert(ByteBuffer var1, int var2, int var3, ByteBuffer var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 4;
         var6 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               byte var10 = var1.get(var2);
               byte var11 = var1.get(var2 + 1);
               byte var12 = var1.get(var2 + 2);
               int var13 = var1.get(var2 + 3) & 255;
               var2 += 4;
               if (var13 < 255) {
                  if (var13 == 0) {
                     var10 = 0;
                     var11 = 0;
                     var12 = 0;
                  } else {
                     var12 = (byte)(((var12 & 255) * var13 + 127) / 255);
                     var11 = (byte)(((var11 & 255) * var13 + 127) / 255);
                     var10 = (byte)(((var10 & 255) * var13 + 127) / 255);
                  }
               }

               var4.put(var5, var12);
               var4.put(var5 + 1, var11);
               var4.put(var5 + 2, var10);
               var4.put(var5 + 3, (byte)var13);
               var5 += 4;
            }

            var2 += var3;
            var5 += var6;
         }
      }
   }

   static class ToIntArgbPreConv extends BaseByteToIntConverter {
      public static final ByteToIntPixelConverter instance = new ByteRgba.ToIntArgbPreConv();

      private ToIntArgbPreConv() {
         super(ByteRgba.getter, IntArgbPre.setter);
      }

      @Override
      void doConvert(byte[] var1, int var2, int var3, int[] var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 4;
         var6 -= var7;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               int var10 = var1[var2++] & 255;
               int var11 = var1[var2++] & 255;
               int var12 = var1[var2++] & 255;
               int var13 = var1[var2++] & 255;
               if (var13 < 255) {
                  if (var13 == 0) {
                     var12 = 0;
                     var11 = 0;
                     var10 = 0;
                  } else {
                     var10 = (var10 * var13 + 127) / 255;
                     var11 = (var11 * var13 + 127) / 255;
                     var12 = (var12 * var13 + 127) / 255;
                  }
               }

               var4[var5++] = var13 << 24 | var12 << 16 | var11 << 8 | var10;
            }

            var5 += var6;
            var2 += var3;
         }
      }

      @Override
      void doConvert(ByteBuffer var1, int var2, int var3, IntBuffer var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               int var10 = var1.get(var2) & 255;
               int var11 = var1.get(var2 + 1) & 255;
               int var12 = var1.get(var2 + 2) & 255;
               int var13 = var1.get(var2 + 3) & 255;
               var2 += 4;
               if (var13 < 255) {
                  if (var13 == 0) {
                     var12 = 0;
                     var11 = 0;
                     var10 = 0;
                  } else {
                     var10 = (var10 * var13 + 127) / 255;
                     var11 = (var11 * var13 + 127) / 255;
                     var12 = (var12 * var13 + 127) / 255;
                  }
               }

               var4.put(var5 + var9, var13 << 24 | var12 << 16 | var11 << 8 | var10);
            }

            var5 += var6;
            var2 += var3;
         }
      }
   }

   static class ToIntArgbSameConv extends BaseByteToIntConverter {
      static final ByteToIntPixelConverter nonpremul = new ByteRgba.ToIntArgbSameConv(false);
      static final ByteToIntPixelConverter premul = new ByteRgba.ToIntArgbSameConv(true);

      private ToIntArgbSameConv(boolean var1) {
         super(var1 ? ByteBgraPre.getter : ByteRgba.getter, var1 ? IntArgbPre.setter : IntArgb.setter);
      }

      @Override
      void doConvert(byte[] var1, int var2, int var3, int[] var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 4;
         var6 -= var7;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               var4[var5++] = var1[var2++] & 255 | (var1[var2++] & 255) << 8 | (var1[var2++] & 255) << 16 | var1[var2++] << 24;
            }

            var2 += var3;
            var5 += var6;
         }
      }

      @Override
      void doConvert(ByteBuffer var1, int var2, int var3, IntBuffer var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               var4.put(var5 + var9, var1.get(var2) & 255 | (var1.get(var2 + 1) & 255) << 8 | (var1.get(var2 + 2) & 255) << 16 | var1.get(var2 + 3) << 24);
               var2 += 4;
            }

            var2 += var3;
            var5 += var6;
         }
      }
   }
}
