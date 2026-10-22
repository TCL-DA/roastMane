package com.sun.javafx.image.impl;

import com.sun.javafx.image.AlphaType;
import com.sun.javafx.image.BytePixelGetter;
import com.sun.javafx.image.BytePixelSetter;
import com.sun.javafx.image.ByteToBytePixelConverter;
import com.sun.javafx.image.ByteToIntPixelConverter;
import com.sun.javafx.image.IntPixelSetter;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ByteRgb {
   public static final BytePixelGetter getter = ByteRgb.Getter.instance;

   public static ByteToBytePixelConverter ToByteBgraConverter() {
      return ByteRgb.ToByteBgrfConv.nonpremult;
   }

   public static ByteToBytePixelConverter ToByteBgraPreConverter() {
      return ByteRgb.ToByteBgrfConv.premult;
   }

   public static ByteToIntPixelConverter ToIntArgbConverter() {
      return ByteRgb.ToIntFrgbConv.nonpremult;
   }

   public static ByteToIntPixelConverter ToIntArgbPreConverter() {
      return ByteRgb.ToIntFrgbConv.premult;
   }

   public static ByteToBytePixelConverter ToByteArgbConverter() {
      return ByteRgb.ToByteFrgbConv.nonpremult;
   }

   public static final ByteToBytePixelConverter ToByteBgrConverter() {
      return ByteRgb.SwapThreeByteConverter.rgbToBgrInstance;
   }

   static class Getter implements BytePixelGetter {
      static final BytePixelGetter instance = new ByteRgb.Getter();

      private Getter() {
      }

      @Override
      public AlphaType getAlphaType() {
         return AlphaType.OPAQUE;
      }

      @Override
      public int getNumElements() {
         return 3;
      }

      @Override
      public int getArgb(byte[] var1, int var2) {
         return var1[var2 + 2] & 0xFF | (var1[var2 + 1] & 0xFF) << 8 | (var1[var2] & 0xFF) << 16 | 0xFF000000;
      }

      @Override
      public int getArgbPre(byte[] var1, int var2) {
         return var1[var2 + 2] & 0xFF | (var1[var2 + 1] & 0xFF) << 8 | (var1[var2] & 0xFF) << 16 | 0xFF000000;
      }

      public int getArgb(ByteBuffer var1, int var2) {
         return var1.get(var2 + 2) & 0xFF | (var1.get(var2 + 1) & 0xFF) << 8 | (var1.get(var2) & 0xFF) << 16 | 0xFF000000;
      }

      public int getArgbPre(ByteBuffer var1, int var2) {
         return var1.get(var2 + 2) & 0xFF | (var1.get(var2 + 1) & 0xFF) << 8 | (var1.get(var2) & 0xFF) << 16 | 0xFF000000;
      }
   }

   static class SwapThreeByteConverter extends BaseByteToByteConverter {
      static final ByteToBytePixelConverter rgbToBgrInstance = new ByteRgb.SwapThreeByteConverter(ByteRgb.getter, ByteBgr.accessor);

      public SwapThreeByteConverter(BytePixelGetter var1, BytePixelSetter var2) {
         super(var1, var2);
      }

      @Override
      void doConvert(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 3;
         var3 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               var4[var5++] = var1[var2 + 2];
               var4[var5++] = var1[var2 + 1];
               var4[var5++] = var1[var2];
               var2 += 3;
            }

            var2 += var3;
            var5 += var6;
         }
      }

      @Override
      void doConvert(ByteBuffer var1, int var2, int var3, ByteBuffer var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 3;
         var3 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               var4.put(var5++, var1.get(var2 + 2));
               var4.put(var5++, var1.get(var2 + 1));
               var4.put(var5++, var1.get(var2));
               var2 += 3;
            }

            var2 += var3;
            var5 += var6;
         }
      }
   }

   static class ToByteBgrfConv extends BaseByteToByteConverter {
      public static final ByteToBytePixelConverter nonpremult = new ByteRgb.ToByteBgrfConv(ByteBgra.setter);
      public static final ByteToBytePixelConverter premult = new ByteRgb.ToByteBgrfConv(ByteBgraPre.setter);

      private ToByteBgrfConv(BytePixelSetter var1) {
         super(ByteRgb.getter, var1);
      }

      @Override
      void doConvert(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 3;
         var6 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               var4[var5++] = var1[var2 + 2];
               var4[var5++] = var1[var2 + 1];
               var4[var5++] = var1[var2];
               var4[var5++] = -1;
               var2 += 3;
            }

            var2 += var3;
            var5 += var6;
         }
      }

      @Override
      void doConvert(ByteBuffer var1, int var2, int var3, ByteBuffer var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 3;
         var6 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               var4.put(var5, var1.get(var2 + 2));
               var4.put(var5 + 1, var1.get(var2 + 1));
               var4.put(var5 + 2, var1.get(var2));
               var4.put(var5 + 3, (byte)-1);
               var2 += 3;
               var5 += 4;
            }

            var2 += var3;
            var5 += var6;
         }
      }
   }

   static class ToByteFrgbConv extends BaseByteToByteConverter {
      static final ByteToBytePixelConverter nonpremult = new ByteRgb.ToByteFrgbConv(ByteArgb.setter);

      private ToByteFrgbConv(BytePixelSetter var1) {
         super(ByteRgb.getter, var1);
      }

      @Override
      void doConvert(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 3;
         var3 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               var4[var5++] = -1;
               var4[var5++] = var1[var2++];
               var4[var5++] = var1[var2++];
               var4[var5++] = var1[var2++];
            }

            var2 += var3;
            var5 += var6;
         }
      }

      @Override
      void doConvert(ByteBuffer var1, int var2, int var3, ByteBuffer var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 3;
         var3 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               var4.put(var5++, (byte)-1);
               var4.put(var5++, var1.get(var2++));
               var4.put(var5++, var1.get(var2++));
               var4.put(var5++, var1.get(var2++));
            }

            var2 += var3;
            var5 += var6;
         }
      }
   }

   static class ToIntFrgbConv extends BaseByteToIntConverter {
      public static final ByteToIntPixelConverter nonpremult = new ByteRgb.ToIntFrgbConv(IntArgb.setter);
      public static final ByteToIntPixelConverter premult = new ByteRgb.ToIntFrgbConv(IntArgbPre.setter);

      private ToIntFrgbConv(IntPixelSetter var1) {
         super(ByteRgb.getter, var1);
      }

      @Override
      void doConvert(byte[] var1, int var2, int var3, int[] var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 3;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               int var10 = var1[var2++] & 255;
               int var11 = var1[var2++] & 255;
               int var12 = var1[var2++] & 255;
               var4[var5 + var9] = 0xFF000000 | var10 << 16 | var11 << 8 | var12;
            }

            var2 += var3;
            var5 += var6;
         }
      }

      @Override
      void doConvert(ByteBuffer var1, int var2, int var3, IntBuffer var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 3;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               int var10 = var1.get(var2) & 255;
               int var11 = var1.get(var2 + 1) & 255;
               int var12 = var1.get(var2 + 2) & 255;
               var2 += 3;
               var4.put(var5 + var9, 0xFF000000 | var10 << 16 | var11 << 8 | var12);
            }

            var2 += var3;
            var5 += var6;
         }
      }
   }
}
