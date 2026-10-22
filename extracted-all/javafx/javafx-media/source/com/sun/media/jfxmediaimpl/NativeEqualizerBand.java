package com.sun.media.jfxmediaimpl;

import com.sun.media.jfxmedia.effects.EqualizerBand;

final class NativeEqualizerBand implements EqualizerBand {
   private final long bandRef;

   private NativeEqualizerBand(long var1) {
      if (var1 != 0L) {
         this.bandRef = var1;
      } else {
         throw new IllegalArgumentException("bandRef == 0");
      }
   }

   @Override
   public double getCenterFrequency() {
      return this.nativeGetCenterFrequency(this.bandRef);
   }

   @Override
   public void setCenterFrequency(double var1) {
      this.nativeSetCenterFrequency(this.bandRef, var1);
   }

   @Override
   public double getBandwidth() {
      return this.nativeGetBandwidth(this.bandRef);
   }

   @Override
   public void setBandwidth(double var1) {
      this.nativeSetBandwidth(this.bandRef, var1);
   }

   @Override
   public double getGain() {
      return this.nativeGetGain(this.bandRef);
   }

   @Override
   public void setGain(double var1) {
      if (var1 >= -24.0 && var1 <= 12.0) {
         this.nativeSetGain(this.bandRef, var1);
      }
   }

   private native double nativeGetCenterFrequency(long var1);

   private native void nativeSetCenterFrequency(long var1, double var3);

   private native double nativeGetBandwidth(long var1);

   private native void nativeSetBandwidth(long var1, double var3);

   private native double nativeGetGain(long var1);

   private native void nativeSetGain(long var1, double var3);
}
