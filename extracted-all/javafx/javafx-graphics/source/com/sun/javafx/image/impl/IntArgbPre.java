package com.sun.javafx.image.impl;

import com.sun.javafx.image.AlphaType;
import com.sun.javafx.image.IntPixelAccessor;
import com.sun.javafx.image.IntPixelGetter;
import com.sun.javafx.image.IntPixelSetter;
import com.sun.javafx.image.IntToBytePixelConverter;
import com.sun.javafx.image.IntToIntPixelConverter;
import com.sun.javafx.image.PixelUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class IntArgbPre {
   public static final IntPixelGetter getter = IntArgbPre.Accessor.instance;
   public static final IntPixelSetter setter = IntArgbPre.Accessor.instance;
   public static final IntPixelAccessor accessor = IntArgbPre.Accessor.instance;
   private static IntToBytePixelConverter ToByteBgraPreObj;
   private static IntToIntPixelConverter ToIntArgbPreObj;

   public static IntToBytePixelConverter ToByteBgraConverter() {
      return IntArgbPre.ToByteBgraConv.instance;
   }

   public static IntToBytePixelConverter ToByteBgraPreConverter() {
      if (ToByteBgraPreObj == null) {
         ToByteBgraPreObj = new IntTo4ByteSameConverter(getter, ByteBgraPre.setter);
      }

      return ToByteBgraPreObj;
   }

   public static IntToIntPixelConverter ToIntArgbConverter() {
      return IntArgbPre.ToIntArgbConv.instance;
   }

   public static IntToIntPixelConverter ToIntArgbPreConverter() {
      if (ToIntArgbPreObj == null) {
         ToIntArgbPreObj = BaseIntToIntConverter.create(accessor);
      }

      return ToIntArgbPreObj;
   }

   static class Accessor implements IntPixelAccessor {
      static final IntPixelAccessor instance = new IntArgbPre.Accessor();

      private Accessor() {
      }

      @Override
      public AlphaType getAlphaType() {
         return AlphaType.PREMULTIPLIED;
      }

      @Override
      public int getNumElements() {
         return 1;
      }

      @Override
      public int getArgb(int[] var1, int var2) {
         return PixelUtils.PretoNonPre(var1[var2]);
      }

      @Override
      public int getArgbPre(int[] var1, int var2) {
         return var1[var2];
      }

      public int getArgb(IntBuffer var1, int var2) {
         return PixelUtils.PretoNonPre(var1.get(var2));
      }

      public int getArgbPre(IntBuffer var1, int var2) {
         return var1.get(var2);
      }

      @Override
      public void setArgb(int[] var1, int var2, int var3) {
         var1[var2] = PixelUtils.NonPretoPre(var3);
      }

      @Override
      public void setArgbPre(int[] var1, int var2, int var3) {
         var1[var2] = var3;
      }

      public void setArgb(IntBuffer var1, int var2, int var3) {
         var1.put(var2, PixelUtils.NonPretoPre(var3));
      }

      public void setArgbPre(IntBuffer var1, int var2, int var3) {
         var1.put(var2, var3);
      }
   }

   static class ToByteBgraConv extends BaseIntToByteConverter {
      public static final IntToBytePixelConverter instance = new IntArgbPre.ToByteBgraConv();

      private ToByteBgraConv() {
         super(IntArgbPre.getter, ByteBgra.setter);
      }

      @Override
      void doConvert(int[] var1, int var2, int var3, byte[] var4, int var5, int var6, int var7, int var8) {
         var3 -= var7;
         var6 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               int var10 = var1[var2++];
               int var11 = var10 >>> 24;
               int var12 = var10 >> 16 & 0xFF;
               int var13 = var10 >> 8 & 0xFF;
               int var14 = var10 & 0xFF;
               if (var11 > 0 && var11 < 255) {
                  int var15 = var11 >> 1;
                  var12 = (var12 * 255 + var15) / var11;
                  var13 = (var13 * 255 + var15) / var11;
                  var14 = (var14 * 255 + var15) / var11;
               }

               var4[var5++] = (byte)var14;
               var4[var5++] = (byte)var13;
               var4[var5++] = (byte)var12;
               var4[var5++] = (byte)var11;
            }

            var2 += var3;
            var5 += var6;
         }
      }

      @Override
      void doConvert(IntBuffer var1, int var2, int var3, ByteBuffer var4, int var5, int var6, int var7, int var8) {
         var6 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               int var10 = var1.get(var2 + var9);
               int var11 = var10 >>> 24;
               int var12 = var10 >> 16 & 0xFF;
               int var13 = var10 >> 8 & 0xFF;
               int var14 = var10 & 0xFF;
               if (var11 > 0 && var11 < 255) {
                  int var15 = var11 >> 1;
                  var12 = (var12 * 255 + var15) / var11;
                  var13 = (var13 * 255 + var15) / var11;
                  var14 = (var14 * 255 + var15) / var11;
               }

               var4.put(var5, (byte)var14);
               var4.put(var5 + 1, (byte)var13);
               var4.put(var5 + 2, (byte)var12);
               var4.put(var5 + 3, (byte)var11);
               var5 += 4;
            }

            var2 += var3;
            var5 += var6;
         }
      }
   }

   public static class ToIntArgbConv extends BaseIntToIntConverter {
      public static final IntToIntPixelConverter instance = new IntArgbPre.ToIntArgbConv();

      private ToIntArgbConv() {
         super(IntArgbPre.getter, IntArgb.setter);
      }

      @Override
      void doConvert(int[] var1, int var2, int var3, int[] var4, int var5, int var6, int var7, int var8) {
         var3 -= var7;
         var6 -= var7;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               int var10 = var1[var2++];
               int var11 = var10 >>> 24;
               if (var11 > 0 && var11 < 255) {
                  int var12 = var11 >> 1;
                  int var13 = ((var10 >> 16 & 0xFF) * 255 + var12) / var11;
                  int var14 = ((var10 >> 8 & 0xFF) * 255 + var12) / var11;
                  int var15 = ((var10 & 0xFF) * 255 + var12) / var11;
                  var10 = var11 << 24 | var13 << 16 | var14 << 8 | var15;
               }

               var4[var5++] = var10;
            }

            var2 += var3;
            var5 += var6;
         }
      }

      @Override
      void doConvert(IntBuffer var1, int var2, int var3, IntBuffer var4, int var5, int var6, int var7, int var8) {
         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               int var10 = var1.get(var2 + var9);
               int var11 = var10 >>> 24;
               if (var11 > 0 && var11 < 255) {
                  int var12 = var11 >> 1;
                  int var13 = ((var10 >> 16 & 0xFF) * 255 + var12) / var11;
                  int var14 = ((var10 >> 8 & 0xFF) * 255 + var12) / var11;
                  int var15 = ((var10 & 0xFF) * 255 + var12) / var11;
                  var10 = var11 << 24 | var13 << 16 | var14 << 8 | var15;
               }

               var4.put(var5 + var9, var10);
            }

            var2 += var3;
            var5 += var6;
         }
      }
   }
}
