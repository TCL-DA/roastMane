package com.sun.pisces;

import com.sun.prism.impl.Disposer;

public abstract class AbstractSurface implements Surface {
   private long nativePtr = 0L;
   private int width;
   private int height;

   AbstractSurface(int var1, int var2) {
      if (var1 < 0) {
         throw new IllegalArgumentException("WIDTH must be positive");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("HEIGHT must be positive");
      } else {
         int var3 = 32 - Integer.numberOfLeadingZeros(var1) + 32 - Integer.numberOfLeadingZeros(var2);
         if (var3 > 31) {
            throw new IllegalArgumentException("WIDTH * HEIGHT is too large");
         } else {
            this.width = var1;
            this.height = var2;
         }
      }
   }

   protected void addDisposerRecord() {
      Disposer.addRecord(this, new AbstractSurface.AbstractSurfaceDisposerRecord(this.nativePtr));
   }

   @Override
   public final void getRGB(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.rgbCheck(var1.length, var2, var3, var4, var5, var6, var7);
      this.getRGBImpl(var1, var2, var3, var4, var5, var6, var7);
   }

   private native void getRGBImpl(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7);

   @Override
   public final void setRGB(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.rgbCheck(var1.length, var2, var3, var4, var5, var6, var7);
      this.setRGBImpl(var1, var2, var3, var4, var5, var6, var7);
   }

   private native void setRGBImpl(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7);

   private void rgbCheck(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (var4 < 0 || var4 >= this.width) {
         throw new IllegalArgumentException("X is out of surface");
      } else if (var5 < 0 || var5 >= this.height) {
         throw new IllegalArgumentException("Y is out of surface");
      } else if (var6 < 0) {
         throw new IllegalArgumentException("WIDTH must be positive");
      } else if (var7 < 0) {
         throw new IllegalArgumentException("HEIGHT must be positive");
      } else if (var4 + var6 > this.width) {
         throw new IllegalArgumentException("X+WIDTH is out of surface");
      } else if (var5 + var7 > this.height) {
         throw new IllegalArgumentException("Y+HEIGHT is out of surface");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("OFFSET must be positive");
      } else if (var3 < 0) {
         throw new IllegalArgumentException("SCAN-LENGTH must be positive");
      } else if (var3 < var6) {
         throw new IllegalArgumentException("SCAN-LENGTH must be >= WIDTH");
      } else {
         int var8 = 32 - Integer.numberOfLeadingZeros(var3) + 32 - Integer.numberOfLeadingZeros(var7);
         if (var8 > 31) {
            throw new IllegalArgumentException("SCAN-LENGTH * HEIGHT is too large");
         } else if (var2 + var3 * (var7 - 1) + var6 > var1) {
            throw new IllegalArgumentException("STRIDE * HEIGHT exceeds length of data");
         }
      }
   }

   private static native void disposeNative(long var0);

   @Override
   public final int getWidth() {
      return this.width;
   }

   @Override
   public final int getHeight() {
      return this.height;
   }

   private static class AbstractSurfaceDisposerRecord implements Disposer.Record {
      private long nativeHandle;

      AbstractSurfaceDisposerRecord(long var1) {
         this.nativeHandle = var1;
      }

      @Override
      public void dispose() {
         if (this.nativeHandle != 0L) {
            AbstractSurface.disposeNative(this.nativeHandle);
            this.nativeHandle = 0L;
         }
      }
   }
}
