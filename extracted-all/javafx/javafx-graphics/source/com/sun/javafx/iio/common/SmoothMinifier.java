package com.sun.javafx.iio.common;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class SmoothMinifier implements PushbroomScaler {
   protected int sourceWidth;
   protected int sourceHeight;
   protected int numBands;
   protected int destWidth;
   protected int destHeight;
   protected double scaleY;
   protected ByteBuffer destBuf;
   protected int boxHeight;
   protected byte[][] sourceData;
   protected int[] leftPoints;
   protected int[] rightPoints;
   protected int[] topPoints;
   protected int[] bottomPoints;
   protected int sourceLine;
   protected int sourceDataLine;
   protected int destLine;
   protected int[] tmpBuf;

   SmoothMinifier(int var1, int var2, int var3, int var4, int var5) {
      if (var1 > 0 && var2 > 0 && var3 > 0 && var4 > 0 && var5 > 0 && var4 <= var1 && var5 <= var2) {
         this.sourceWidth = var1;
         this.sourceHeight = var2;
         this.numBands = var3;
         this.destWidth = var4;
         this.destHeight = var5;
         this.destBuf = ByteBuffer.wrap(new byte[var5 * var4 * var3]);
         double var6 = (double)var1 / (double)var4;
         this.scaleY = (double)var2 / (double)var5;
         int var8 = (var1 + var4 - 1) / var4;
         this.boxHeight = (var2 + var5 - 1) / var5;
         int var9 = var8 / 2;
         int var10 = var8 - var9 - 1;
         int var11 = this.boxHeight / 2;
         int var12 = this.boxHeight - var11 - 1;
         this.sourceData = new byte[this.boxHeight][var4 * var3];
         this.leftPoints = new int[var4];
         this.rightPoints = new int[var4];

         for (int var13 = 0; var13 < var4; var13++) {
            int var14 = (int)((double)var13 * var6);
            this.leftPoints[var13] = var14 - var9;
            this.rightPoints[var13] = var14 + var10;
         }

         this.topPoints = new int[var5];
         this.bottomPoints = new int[var5];

         for (int var15 = 0; var15 < var5; var15++) {
            int var16 = (int)((double)var15 * this.scaleY);
            this.topPoints[var15] = var16 - var11;
            this.bottomPoints[var15] = var16 + var12;
         }

         this.sourceLine = 0;
         this.sourceDataLine = 0;
         this.destLine = 0;
         this.tmpBuf = new int[var4 * var3];
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public ByteBuffer getDestination() {
      return this.destBuf;
   }

   @Override
   public boolean putSourceScanline(byte[] var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("off < 0!");
      } else {
         if (this.numBands == 1) {
            int var3 = var1[var2] & 255;
            int var4 = var1[var2 + this.sourceWidth - 1] & 255;

            for (int var5 = 0; var5 < this.destWidth; var5++) {
               int var6 = 0;
               int var7 = this.rightPoints[var5];

               for (int var8 = this.leftPoints[var5]; var8 <= var7; var8++) {
                  if (var8 < 0) {
                     var6 += var3;
                  } else if (var8 >= this.sourceWidth) {
                     var6 += var4;
                  } else {
                     var6 += var1[var2 + var8] & 255;
                  }
               }

               var6 /= var7 - this.leftPoints[var5] + 1;
               this.sourceData[this.sourceDataLine][var5] = (byte)var6;
            }
         } else {
            int var14 = var2 + (this.sourceWidth - 1) * this.numBands;

            for (int var16 = 0; var16 < this.destWidth; var16++) {
               int var18 = this.leftPoints[var16];
               int var22 = this.rightPoints[var16];
               int var26 = var22 - var18 + 1;
               int var28 = var16 * this.numBands;

               for (int var9 = 0; var9 < this.numBands; var9++) {
                  int var10 = var1[var2 + var9] & 255;
                  int var11 = var1[var14 + var9] & 255;
                  int var12 = 0;

                  for (int var13 = var18; var13 <= var22; var13++) {
                     if (var13 < 0) {
                        var12 += var10;
                     } else if (var13 >= this.sourceWidth) {
                        var12 += var11;
                     } else {
                        var12 += var1[var2 + var13 * this.numBands + var9] & 255;
                     }
                  }

                  var12 /= var26;
                  this.sourceData[this.sourceDataLine][var28 + var9] = (byte)var12;
               }
            }
         }

         if (this.sourceLine == this.bottomPoints[this.destLine] || this.destLine == this.destHeight - 1 && this.sourceLine == this.sourceHeight - 1) {
            assert this.destBuf.hasArray() : "destBuf.hasArray() == false => destBuf is direct";

            byte[] var15 = this.destBuf.array();
            int var17 = this.destLine * this.destWidth * this.numBands;
            Arrays.fill(this.tmpBuf, 0);

            for (int var19 = this.topPoints[this.destLine]; var19 <= this.bottomPoints[this.destLine]; var19++) {
               int var23 = 0;
               if (var19 < 0) {
                  var23 = 0 - this.sourceLine + this.sourceDataLine;
               } else if (var19 >= this.sourceHeight) {
                  var23 = (this.sourceHeight - 1 - this.sourceLine + this.sourceDataLine) % this.boxHeight;
               } else {
                  var23 = (var19 - this.sourceLine + this.sourceDataLine) % this.boxHeight;
               }

               if (var23 < 0) {
                  var23 += this.boxHeight;
               }

               byte[] var27 = this.sourceData[var23];
               int var29 = var27.length;

               for (int var30 = 0; var30 < var29; var30++) {
                  this.tmpBuf[var30] = this.tmpBuf[var30] + (var27[var30] & 255);
               }
            }

            int var20 = this.tmpBuf.length;

            for (int var25 = 0; var25 < var20; var25++) {
               var15[var17 + var25] = (byte)(this.tmpBuf[var25] / this.boxHeight);
            }

            if (this.destLine < this.destHeight - 1) {
               this.destLine++;
            }
         }

         if (++this.sourceLine != this.sourceHeight) {
            this.sourceDataLine = (this.sourceDataLine + 1) % this.boxHeight;
         }

         return this.destLine == this.destHeight;
      }
   }
}
