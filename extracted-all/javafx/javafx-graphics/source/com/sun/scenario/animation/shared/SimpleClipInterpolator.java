package com.sun.scenario.animation.shared;

import java.util.HashMap;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.value.WritableValue;
import javafx.util.Duration;

class SimpleClipInterpolator extends ClipInterpolator {
   private static final KeyFrame ZERO_FRAME = new KeyFrame(Duration.ZERO);
   private KeyFrame startKeyFrame;
   private KeyFrame endKeyFrame;
   private long endTicks;
   private InterpolationInterval[] interval;
   private int undefinedStartValueCount;
   private long ticks;
   private boolean invalid = true;

   SimpleClipInterpolator(KeyFrame var1, KeyFrame var2, long var3) {
      this.startKeyFrame = var1;
      this.endKeyFrame = var2;
      this.endTicks = var3;
   }

   SimpleClipInterpolator(KeyFrame var1, long var2) {
      this.startKeyFrame = ZERO_FRAME;
      this.endKeyFrame = var1;
      this.endTicks = var2;
   }

   @Override
   ClipInterpolator setKeyFrames(KeyFrame[] var1, long[] var2) {
      if (ClipInterpolator.getRealKeyFrameCount(var1) != 2) {
         return ClipInterpolator.create(var1, var2);
      } else {
         if (var1.length == 1) {
            this.startKeyFrame = ZERO_FRAME;
            this.endKeyFrame = var1[0];
            this.endTicks = var2[0];
         } else {
            this.startKeyFrame = var1[0];
            this.endKeyFrame = var1[1];
            this.endTicks = var2[1];
         }

         this.invalid = true;
         return this;
      }
   }

   @Override
   void validate(boolean var1) {
      if (this.invalid) {
         this.ticks = this.endTicks;
         HashMap var2 = new HashMap();

         for (KeyValue var4 : this.endKeyFrame.getValues()) {
            var2.put(var4.getTarget(), var4);
         }

         int var10 = var2.size();
         this.interval = new InterpolationInterval[var10];
         int var12 = 0;

         for (KeyValue var6 : this.startKeyFrame.getValues()) {
            WritableValue var7 = var6.getTarget();
            KeyValue var8 = (KeyValue)var2.get(var7);
            if (var8 != null) {
               this.interval[var12++] = InterpolationInterval.create(var8, this.ticks, var6, this.ticks);
               var2.remove(var7);
            }
         }

         this.undefinedStartValueCount = var2.values().size();

         for (KeyValue var14 : var2.values()) {
            this.interval[var12++] = InterpolationInterval.create(var14, this.ticks);
         }

         this.invalid = false;
      } else if (var1) {
         int var9 = this.interval.length;

         for (int var11 = var9 - this.undefinedStartValueCount; var11 < var9; var11++) {
            this.interval[var11].recalculateStartValue();
         }
      }
   }

   @Override
   void interpolate(long var1) {
      double var3 = (double)var1 / (double)this.ticks;
      int var5 = this.interval.length;

      for (int var6 = 0; var6 < var5; var6++) {
         this.interval[var6].interpolate(var3);
      }
   }
}
