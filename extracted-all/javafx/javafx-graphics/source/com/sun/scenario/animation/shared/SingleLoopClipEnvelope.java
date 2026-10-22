package com.sun.scenario.animation.shared;

import com.sun.javafx.util.Utils;
import javafx.animation.Animation;
import javafx.util.Duration;

public class SingleLoopClipEnvelope extends ClipEnvelope {
   private int cycleCount;

   protected SingleLoopClipEnvelope(Animation var1) {
      super(var1);
      if (var1 != null) {
         this.cycleCount = var1.getCycleCount();
      }
   }

   @Override
   public void setAutoReverse(boolean var1) {
   }

   @Override
   public ClipEnvelope setCycleDuration(Duration var1) {
      if (this.cycleCount != 1 && !var1.isIndefinite()) {
         return create(this.animation);
      } else {
         this.updateCycleTicks(var1);
         return this;
      }
   }

   @Override
   public ClipEnvelope setCycleCount(int var1) {
      if (var1 != 1 && this.cycleTicks != Long.MAX_VALUE) {
         return create(this.animation);
      } else {
         this.cycleCount = var1;
         return this;
      }
   }

   @Override
   public void setRate(double var1) {
      Animation.Status var3 = this.animation.getStatus();
      if (var3 != Animation.Status.STOPPED) {
         this.setInternalCurrentRate(Math.abs(this.currentRate - this.rate) < 1.0E-12 ? var1 : -var1);
         this.deltaTicks = this.ticks - this.ticksRateChange(var1);
         this.abortCurrentPulse();
      }

      this.rate = var1;
   }

   @Override
   protected double calculateCurrentRate() {
      return this.rate;
   }

   @Override
   public boolean wasSynched() {
      return super.wasSynched() && this.cycleCount != 0;
   }

   @Override
   public void timePulse(long var1) {
      if (this.cycleTicks != 0L) {
         this.aborted = false;
         this.inTimePulse = true;

         try {
            long var3 = Math.round((double)var1 * this.currentRate);
            this.ticks = Utils.clamp(0L, this.deltaTicks + var3, this.cycleTicks);
            AnimationAccessor.getDefault().playTo(this.animation, this.ticks, this.cycleTicks);
            boolean var5 = this.currentRate > 0.0 ? this.ticks == this.cycleTicks : this.ticks == 0L;
            if (var5 && !this.aborted) {
               AnimationAccessor.getDefault().finished(this.animation);
            }
         } finally {
            this.inTimePulse = false;
         }
      }
   }

   @Override
   public void jumpTo(long var1) {
      if (this.cycleTicks != 0L) {
         long var3 = Utils.clamp(0L, var1, this.cycleTicks);
         this.deltaTicks = this.deltaTicks + (var3 - this.ticks);
         this.ticks = var3;
         AnimationAccessor.getDefault().jumpTo(this.animation, var3, this.cycleTicks, false);
         this.abortCurrentPulse();
      }
   }
}
