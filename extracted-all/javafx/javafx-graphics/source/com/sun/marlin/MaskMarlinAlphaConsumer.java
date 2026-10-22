package com.sun.marlin;

import com.sun.prism.impl.shape.MaskData;
import java.nio.ByteBuffer;
import java.util.Arrays;
import sun.misc.Unsafe;

public final class MaskMarlinAlphaConsumer implements MarlinAlphaConsumer {
   int x;
   int y;
   int width;
   int height;
   final byte[] alphas;
   final ByteBuffer alphabuffer;
   final MaskData maskdata = new MaskData();
   boolean useFastFill;
   int fastFillThreshold;
   OffHeapArray ALPHA_MAP_USED = null;
   static final byte[] ALPHA_MAP;
   static final OffHeapArray ALPHA_MAP_UNSAFE;
   static final byte[] ALPHA_MAP_NO_AA;
   static final OffHeapArray ALPHA_MAP_UNSAFE_NO_AA;

   public MaskMarlinAlphaConsumer(int var1) {
      this.alphas = new byte[var1];
      this.alphabuffer = ByteBuffer.wrap(this.alphas);
   }

   public void setBoundsNoClone(int var1, int var2, int var3, int var4) {
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
      this.maskdata.update(this.alphabuffer, var1, var2, var3, var4);
      this.useFastFill = var3 >= 32;
      if (this.useFastFill) {
         this.fastFillThreshold = var3 >= 128 ? var3 >> 1 : var3 >> 2;
      }
   }

   @Override
   public int getOriginX() {
      return this.x;
   }

   @Override
   public int getOriginY() {
      return this.y;
   }

   @Override
   public int getWidth() {
      return this.width;
   }

   @Override
   public int getHeight() {
      return this.height;
   }

   public int getAlphaLength() {
      return this.alphas.length;
   }

   public MaskData getMaskData() {
      return this.maskdata;
   }

   @Override
   public void setMaxAlpha(int var1) {
      this.ALPHA_MAP_USED = var1 == 1 ? ALPHA_MAP_UNSAFE_NO_AA : ALPHA_MAP_UNSAFE;
   }

   private static byte[] buildAlphaMap(int var0) {
      byte[] var1 = new byte[var0 << 1];
      int var2 = var0 >> 2;

      for (int var3 = 0; var3 <= var0; var3++) {
         var1[var3] = (byte)((var3 * 255 + var2) / var0);
      }

      return var1;
   }

   @Override
   public boolean supportBlockFlags() {
      return true;
   }

   @Override
   public void clearAlphas(int var1) {
      int var2 = this.width;
      int var3 = (var1 - this.y) * var2;
      Arrays.fill(this.alphas, var3, var3 + var2, (byte)0);
   }

   @Override
   public void setAndClearRelativeAlphas(int[] var1, int var2, int var3, int var4) {
      byte[] var5 = this.alphas;
      int var6 = this.width;
      int var7 = (var2 - this.y) * var6;
      Unsafe var8 = OffHeapArray.UNSAFE;
      long var9 = this.ALPHA_MAP_USED.address;
      int var11 = var3 - this.x;
      int var12 = var4 - this.x;
      int var13 = Math.min(var12, this.width);
      boolean var14 = this.useFastFill && var13 - var11 < this.fastFillThreshold;
      if (var14) {
         Arrays.fill(var5, var7, var7 + var6, (byte)0);
         int var15 = var11;

         for (int var16 = 0; var15 < var13; var15++) {
            var16 += var1[var15];
            var5[var7 + var15] = var8.getByte(var9 + (long)var16);
         }
      } else {
         int var17;
         for (var17 = 0; var17 < var11; var17++) {
            var5[var7 + var17] = 0;
         }

         for (int var18 = 0; var17 < var13; var17++) {
            var18 += var1[var17];
            var5[var7 + var17] = var8.getByte(var9 + (long)var18);
         }

         while (var17 < var6) {
            var5[var7 + var17] = 0;
            var17++;
         }
      }

      IntArrayCache.fill(var1, var11, var12 + 1, 0);
   }

   @Override
   public void setAndClearRelativeAlphas(int[] var1, int[] var2, int var3, int var4, int var5) {
      byte[] var6 = this.alphas;
      int var7 = this.width;
      int var8 = (var3 - this.y) * var7;
      Unsafe var9 = OffHeapArray.UNSAFE;
      long var10 = this.ALPHA_MAP_USED.address;
      int var12 = var4 - this.x;
      int var13 = var5 - this.x;
      int var14 = Math.min(var13, this.width);
      boolean var15 = this.useFastFill && var14 - var12 < this.fastFillThreshold;
      int var16 = MarlinConst.BLOCK_SIZE_LG;
      int var17 = var12 >> var16;
      int var18 = (var14 >> var16) + 1;
      var1[var18] = 0;
      int var19 = 0;
      int var20 = Integer.MAX_VALUE;
      if (var15) {
         int var22 = var12;
         Arrays.fill(var6, var8, var8 + var7, (byte)0);

         for (int var23 = var17; var23 <= var18; var23++) {
            if (var1[var23] != 0) {
               var1[var23] = 0;
               if (var20 == Integer.MAX_VALUE) {
                  var20 = var23;
               }
            } else if (var20 != Integer.MAX_VALUE) {
               int var24 = FloatMath.max(var20 << var16, var12);
               var20 = Integer.MAX_VALUE;
               int var25 = FloatMath.min((var23 << var16) + 1, var14);

               for (int var26 = var24; var26 < var25; var26++) {
                  int var27;
                  if ((var27 = var2[var26]) != 0) {
                     var2[var26] = 0;
                     if (var26 != var22) {
                        if (var19 == 0) {
                           var22 = var26;
                        } else {
                           byte var21 = var9.getByte(var10 + (long)var19);

                           do {
                              var6[var8 + var22] = var21;
                           } while (++var22 < var26);
                        }
                     }

                     var19 += var27;
                  }
               }
            }
         }

         if (var19 != 0) {
            for (byte var28 = var9.getByte(var10 + (long)var19); var22 < var14; var22++) {
               var6[var8 + var22] = var28;
            }
         }
      } else {
         int var31;
         for (var31 = 0; var31 < var12; var31++) {
            var6[var8 + var31] = 0;
         }

         for (int var32 = var17; var32 <= var18; var32++) {
            if (var1[var32] != 0) {
               var1[var32] = 0;
               if (var20 == Integer.MAX_VALUE) {
                  var20 = var32;
               }
            } else if (var20 != Integer.MAX_VALUE) {
               int var33 = FloatMath.max(var20 << var16, var12);
               var20 = Integer.MAX_VALUE;
               int var34 = FloatMath.min((var32 << var16) + 1, var14);

               for (int var35 = var33; var35 < var34; var35++) {
                  int var36;
                  if ((var36 = var2[var35]) != 0) {
                     var2[var35] = 0;
                     if (var35 != var31) {
                        byte var29 = var9.getByte(var10 + (long)var19);

                        do {
                           var6[var8 + var31] = var29;
                        } while (++var31 < var35);
                     }

                     var19 += var36;
                  }
               }
            }
         }

         if (var19 != 0) {
            for (byte var30 = var9.getByte(var10 + (long)var19); var31 < var14; var31++) {
               var6[var8 + var31] = var30;
            }
         }

         while (var31 < var7) {
            var6[var8 + var31] = 0;
            var31++;
         }
      }

      var2[var14] = 0;
      if (MarlinConst.DO_CHECKS) {
         IntArrayCache.check(var1, var17, var18, 0);
         IntArrayCache.check(var2, var12, var13 + 1, 0);
      }
   }

   static {
      Unsafe var0 = OffHeapArray.UNSAFE;
      byte[] var1 = buildAlphaMap(MarlinConst.MAX_AA_ALPHA);
      ALPHA_MAP = var1;
      ALPHA_MAP_UNSAFE = new OffHeapArray(ALPHA_MAP, (long)ALPHA_MAP.length);
      long var2 = ALPHA_MAP_UNSAFE.address;

      for (int var4 = 0; var4 < var1.length; var4++) {
         var0.putByte(var2 + (long)var4, var1[var4]);
      }

      byte[] var7 = buildAlphaMap(1);
      ALPHA_MAP_NO_AA = var7;
      ALPHA_MAP_UNSAFE_NO_AA = new OffHeapArray(ALPHA_MAP_NO_AA, (long)ALPHA_MAP_NO_AA.length);
      var2 = ALPHA_MAP_UNSAFE_NO_AA.address;

      for (int var5 = 0; var5 < var7.length; var5++) {
         var0.putByte(var2 + (long)var5, var7[var5]);
      }
   }
}
