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

public class ByteBgr {
   public static final BytePixelGetter getter = ByteBgr.Accessor.instance;
   public static final BytePixelSetter setter = ByteBgr.Accessor.instance;
   public static final BytePixelAccessor accessor = ByteBgr.Accessor.instance;
   private static ByteToBytePixelConverter ToByteBgrObj;

   public static ByteToBytePixelConverter ToByteBgrConverter() {
      if (ToByteBgrObj == null) {
         ToByteBgrObj = BaseByteToByteConverter.create(accessor);
      }

      return ToByteBgrObj;
   }

   public static ByteToBytePixelConverter ToByteBgraConverter() {
      return ByteBgr.ToByteBgrfConv.nonpremult;
   }

   public static ByteToBytePixelConverter ToByteBgraPreConverter() {
      return ByteBgr.ToByteBgrfConv.premult;
   }

   public static ByteToIntPixelConverter ToIntArgbConverter() {
      return ByteBgr.ToIntFrgbConv.nonpremult;
   }

   public static ByteToIntPixelConverter ToIntArgbPreConverter() {
      return ByteBgr.ToIntFrgbConv.premult;
   }

   public static ByteToBytePixelConverter ToByteArgbConverter() {
      return ByteBgr.ToByteFrgbConv.nonpremult;
   }

   static class Accessor implements BytePixelAccessor {
      static final BytePixelAccessor instance = new ByteBgr.Accessor();

      private Accessor() {
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
         return var1[var2] & 0xFF | (var1[var2 + 1] & 0xFF) << 8 | (var1[var2 + 2] & 0xFF) << 16 | 0xFF000000;
      }

      @Override
      public int getArgbPre(byte[] var1, int var2) {
         return var1[var2] & 0xFF | (var1[var2 + 1] & 0xFF) << 8 | (var1[var2 + 2] & 0xFF) << 16 | 0xFF000000;
      }

      public int getArgb(ByteBuffer var1, int var2) {
         return var1.get(var2) & 0xFF | (var1.get(var2 + 1) & 0xFF) << 8 | (var1.get(var2 + 2) & 0xFF) << 16 | 0xFF000000;
      }

      public int getArgbPre(ByteBuffer var1, int var2) {
         return var1.get(var2) & 0xFF | (var1.get(var2 + 1) & 0xFF) << 8 | (var1.get(var2 + 2) & 0xFF) << 16 | 0xFF000000;
      }

      @Override
      public void setArgb(byte[] var1, int var2, int var3) {
         var1[var2] = (byte)var3;
         var1[var2 + 1] = (byte)(var3 >> 8);
         var1[var2 + 2] = (byte)(var3 >> 16);
      }

      @Override
      public void setArgbPre(byte[] var1, int var2, int var3) {
         this.setArgb(var1, var2, PixelUtils.PretoNonPre(var3));
      }

      public void setArgb(ByteBuffer var1, int var2, int var3) {
         var1.put(var2, (byte)var3);
         var1.put(var2 + 1, (byte)(var3 >> 8));
         var1.put(var2 + 2, (byte)(var3 >> 16));
      }

      public void setArgbPre(ByteBuffer var1, int var2, int var3) {
         this.setArgb(var1, var2, PixelUtils.PretoNonPre(var3));
      }
   }

   static class ToByteBgrfConv extends BaseByteToByteConverter {
      public static final ByteToBytePixelConverter nonpremult = new ByteBgr.ToByteBgrfConv(ByteBgra.setter);
      public static final ByteToBytePixelConverter premult = new ByteBgr.ToByteBgrfConv(ByteBgraPre.setter);

      private ToByteBgrfConv(BytePixelSetter var1) {
         super(ByteBgr.getter, var1);
      }

      @Override
      void doConvert(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 3;
         var6 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               var4[var5++] = var1[var2++];
               var4[var5++] = var1[var2++];
               var4[var5++] = var1[var2++];
               var4[var5++] = -1;
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
               var4.put(var5++, var1.get(var2++));
               var4.put(var5++, var1.get(var2++));
               var4.put(var5++, var1.get(var2++));
               var4.put(var5++, (byte)-1);
            }

            var2 += var3;
            var5 += var6;
         }
      }
   }

   static class ToByteFrgbConv extends BaseByteToByteConverter {
      static final ByteToBytePixelConverter nonpremult = new ByteBgr.ToByteFrgbConv(ByteArgb.setter);

      private ToByteFrgbConv(BytePixelSetter var1) {
         super(ByteBgr.getter, var1);
      }

      @Override
      void doConvert(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 3;
         var3 -= var7 * 4;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               var4[var5++] = -1;
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
               var4.put(var5++, (byte)-1);
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

   static class ToIntFrgbConv extends BaseByteToIntConverter {
      public static final ByteToIntPixelConverter nonpremult = new ByteBgr.ToIntFrgbConv(IntArgb.setter);
      public static final ByteToIntPixelConverter premult = new ByteBgr.ToIntFrgbConv(IntArgbPre.setter);

      private ToIntFrgbConv(IntPixelSetter var1) {
         super(ByteBgr.getter, var1);
      }

      @Override
      void doConvert(byte[] var1, int var2, int var3, int[] var4, int var5, int var6, int var7, int var8) {
         var3 -= var7 * 3;

         while (--var8 >= 0) {
            for (int var9 = 0; var9 < var7; var9++) {
               int var10 = var1[var2++] & 255;
               int var11 = var1[var2++] & 255;
               int var12 = var1[var2++] & 255;
               var4[var5 + var9] = 0xFF000000 | var12 << 16 | var11 << 8 | var10;
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
               int var10 = var1.get(var2++) & 255;
               int var11 = var1.get(var2++) & 255;
               int var12 = var1.get(var2++) & 255;
               var4.put(var5 + var9, 0xFF000000 | var12 << 16 | var11 << 8 | var10);
            }

            var2 += var3;
            var5 += var6;
         }
      }
   }
}
