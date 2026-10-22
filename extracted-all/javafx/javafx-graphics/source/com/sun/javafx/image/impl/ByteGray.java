package com.sun.javafx.image.impl;

import com.sun.javafx.image.AlphaType;
import com.sun.javafx.image.BytePixelAccessor;
import com.sun.javafx.image.BytePixelGetter;
import com.sun.javafx.image.BytePixelSetter;
import com.sun.javafx.image.ByteToBytePixelConverter;
import com.sun.javafx.image.ByteToIntPixelConverter;
import com.sun.javafx.image.IntPixelSetter;
import com.sun.javafx.image.PixelUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ByteGray {
   public static final BytePixelGetter getter = ByteGray.Accessor.instance;
   public static final BytePixelSetter setter = ByteGray.Accessor.instance;
   public static final BytePixelAccessor accessor = ByteGray.Accessor.instance;
   private static ByteToBytePixelConverter ToByteGrayObj;

   public static ByteToBytePixelConverter ToByteGrayConverter() {
      if (ToByteGrayObj == null) {
         ToByteGrayObj = BaseByteToByteConverter.create(accessor);
      }

      return ToByteGrayObj;
   }

   public static ByteToBytePixelConverter ToByteBgraConverter() {
      return ByteGray.ToByteBgrfConv.nonpremult;
   }

   public static ByteToBytePixelConverter ToByteBgraPreConverter() {
      return ByteGray.ToByteBgrfConv.premult;
   }

   public static ByteToIntPixelConverter ToIntArgbConverter() {
      return ByteGray.ToIntFrgbConv.nonpremult;
   }

   public static ByteToIntPixelConverter ToIntArgbPreConverter() {
      return ByteGray.ToIntFrgbConv.premult;
   }

   public static ByteToBytePixelConverter ToByteBgrConverter() {
      return ByteGray.ToByteRgbAnyConv.bgr;
   }

   static class Accessor implements BytePixelAccessor {
      static final BytePixelAccessor instance = new ByteGray.Accessor();

      private Accessor() {
      }

      @Override
      public AlphaType getAlphaType() {
         return AlphaType.OPAQUE;
      }

      @Override
      public int getNumElements() {
         return 1;
      }

      @Override
      public int getArgb(byte[] var1, int var2) {
         int var3 = var1[var2] & 255;
         return 0xFF000000 | var3 << 16 | var3 << 8 | var3;
      }

      @Override
      public int getArgbPre(byte[] var1, int var2) {
         int var3 = var1[var2] & 255;
         return 0xFF000000 | var3 << 16 | var3 << 8 | var3;
      }

      public int getArgb(ByteBuffer var1, int var2) {
         int var3 = var1.get(var2) & 255;
         return 0xFF000000 | var3 << 16 | var3 << 8 | var3;
      }

      public int getArgbPre(ByteBuffer var1, int var2) {
         int var3 = var1.get(var2) & 255;
         return 0xFF000000 | var3 << 16 | var3 << 8 | var3;
      }

      @Override
      public void setArgb(byte[] var1, int var2, int var3) {
         var1[var2] = (byte)PixelUtils.RgbToGray(var3);
      }

      @Override
      public void setArgbPre(byte[] var1, int var2, int var3) {
         this.setArgb(var1, var2, PixelUtils.PretoNonPre(var3));
      }

      public void setArgb(ByteBuffer var1, int var2, int var3) {
         var1.put(var2, (byte)PixelUtils.RgbToGray(var3));
      }

      public void setArgbPre(ByteBuffer var1, int var2, int var3) {
         this.setArgb(var1, var2, PixelUtils.PretoNonPre(var3));
      }
   }

   static class ToByteBgrfConv extends BaseByteToByteConverter {
      public static final ByteToBytePixelConverter nonpremult = new ByteGray.ToByteBgrfConv(ByteBgra.setter);
      public static final ByteToBytePixelConverter premult = new ByteGray.ToByteBgrfConv(ByteBgraPre.setter);

      ToByteBgrfConv(BytePixelSetter var1) {
         super(ByteGray.getter, var1);
      }

      @Override
      void doConvert(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, int var7, int var8) {
         var6 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               byte var10 = var1[var2 + var9];
               var4[var5++] = var10;
               var4[var5++] = var10;
               var4[var5++] = var10;
               var4[var5++] = -1;
            }

            var2 += var3;
            var5 += var6;
         }
      }

      @Override
      void doConvert(ByteBuffer var1, int var2, int var3, ByteBuffer var4, int var5, int var6, int var7, int var8) {
         var6 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               byte var10 = var1.get(var2 + var9);
               var4.put(var5, var10);
               var4.put(var5 + 1, var10);
               var4.put(var5 + 2, var10);
               var4.put(var5 + 3, (byte)-1);
               var5 += 4;
            }

            var2 += var3;
            var5 += var6;
         }
      }
   }

   static class ToByteRgbAnyConv extends BaseByteToByteConverter {
      static ByteGray.ToByteRgbAnyConv bgr = new ByteGray.ToByteRgbAnyConv(ByteBgr.setter);

      private ToByteRgbAnyConv(BytePixelSetter var1) {
         super(ByteGray.getter, var1);
      }

      @Override
      void doConvert(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, int var7, int var8) {
         var6 -= var7 * 3;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               int var10 = var1[var2 + var9] & 255;
               var4[var5++] = (byte)var10;
               var4[var5++] = (byte)var10;
               var4[var5++] = (byte)var10;
            }

            var2 += var3;
            var5 += var6;
         }
      }

      @Override
      void doConvert(ByteBuffer var1, int var2, int var3, ByteBuffer var4, int var5, int var6, int var7, int var8) {
         var6 -= var7 * 3;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               int var10 = var1.get(var2 + var9) & 255;
               var4.put(var5++, (byte)var10);
               var4.put(var5++, (byte)var10);
               var4.put(var5++, (byte)var10);
            }

            var2 += var3;
            var5 += var6;
         }
      }
   }

   static class ToIntFrgbConv extends BaseByteToIntConverter {
      public static final ByteToIntPixelConverter nonpremult = new ByteGray.ToIntFrgbConv(IntArgb.setter);
      public static final ByteToIntPixelConverter premult = new ByteGray.ToIntFrgbConv(IntArgbPre.setter);

      private ToIntFrgbConv(IntPixelSetter var1) {
         super(ByteGray.getter, var1);
      }

      @Override
      void doConvert(byte[] var1, int var2, int var3, int[] var4, int var5, int var6, int var7, int var8) {
         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               int var10 = var1[var2 + var9] & 255;
               var4[var5 + var9] = 0xFF000000 | var10 << 16 | var10 << 8 | var10;
            }

            var2 += var3;
            var5 += var6;
         }
      }

      @Override
      void doConvert(ByteBuffer var1, int var2, int var3, IntBuffer var4, int var5, int var6, int var7, int var8) {
         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               int var10 = var1.get(var2 + var9) & 255;
               var4.put(var5 + var9, 0xFF000000 | var10 << 16 | var10 << 8 | var10);
            }

            var2 += var3;
            var5 += var6;
         }
      }
   }
}
