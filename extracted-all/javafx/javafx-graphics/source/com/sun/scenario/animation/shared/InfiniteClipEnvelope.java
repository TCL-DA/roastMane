package com.sun.scenario.animation.shared;

import javafx.animation.Animation;
import javafx.util.Duration;

public class InfiniteClipEnvelope extends MultiLoopClipEnvelope {
   protected InfiniteClipEnvelope(Animation var1) {
      super(var1);
      if (var1 != null) {
         this.autoReverse = var1.isAutoReverse();
      }
   }

   @Override
   public ClipEnvelope setCycleDuration(Duration var1) {
      if (var1.isIndefinite()) {
         return create(this.animation);
      } else {
         this.updateCycleTicks(var1);
         return this;
      }
   }

   @Override
   public ClipEnvelope setCycleCount(int var1) {
      return (ClipEnvelope)(var1 != -1 ? create(this.animation) : this);
   }

   @Override
   public void setRate(double var1) {
      Animation.Status var3 = this.animation.getStatus();
      if (var3 != Animation.Status.STOPPED) {
         this.setInternalCurrentRate(Math.abs(this.currentRate - this.rate) < 1.0E-12 ? var1 : -var1);
         this.deltaTicks = this.ticks - this.ticksRateChange(var1);
         if (this.isDirectionChanged(var1)) {
            long var4 = 2L * this.cycleTicks - this.cyclePos;
            this.deltaTicks += var4;
            this.ticks += var4;
         }

         this.abortCurrentPulse();
      }

      this.rate = var1;
   }

   @Override
   protected double calculateCurrentRate() {
      return !this.autoReverse ? this.rate : (this.isDuringEvenCycle() ? this.rate : -this.rate);
   }

   @Override
   public void timePulse(long var1) {
      if (this.cycleTicks != 0L) {
         this.aborted = false;
         this.inTimePulse = true;

         try {
            long var3 = this.ticks;
            long var5 = Math.round((double)var1 * Math.abs(this.rate));
            this.ticks = Math.max(0L, this.deltaTicks + var5);
            long var7 = this.ticks - var3;
            if (var7 != 0L) {
               for (long var9 = this.currentRate > 0.0 ? this.cycleTicks - this.cyclePos : this.cyclePos; var7 >= var9; var9 = this.cycleTicks) {
                  if (var9 > 0L) {
                     this.cyclePos = this.currentRate > 0.0 ? this.cycleTicks : 0L;
                     var7 -= var9;
                     AnimationAccessor.getDefault().playTo(this.animation, this.cyclePos, this.cycleTicks);
                     if (this.aborted) {
                        return;
                     }
                  }

                  if (this.autoReverse) {
                     this.setCurrentRate(-this.currentRate);
                  } else {
                     this.cyclePos = this.currentRate > 0.0 ? 0L : this.cycleTicks;
                     AnimationAccessor.getDefault().jumpTo(this.animation, this.cyclePos, this.cycleTicks, false);
                  }
               }

               if (var7 > 0L) {
                  this.cyclePos = this.cyclePos + (this.currentRate > 0.0 ? var7 : -var7);
                  AnimationAccessor.getDefault().playTo(this.animation, this.cyclePos, this.cycleTicks);
               }
            }
         } finally {
            this.inTimePulse = false;
         }
      }
   }

   @Override
   public void jumpTo(long var1) {
      if (this.cycleTicks != 0L) {
         long var3 = this.ticks;
         this.ticks = Math.max(0L, var1) % (2L * this.cycleTicks);
         long var5 = this.ticks - var3;
         if (var5 != 0L) {
            this.deltaTicks += var5;
            if (this.autoReverse) {
               if (this.ticks > this.cycleTicks) {
                  this.cyclePos = 2L * this.cycleTicks - this.ticks;
                  if (this.animation.getStatus() == Animation.Status.RUNNING) {
                     this.setCurrentRate(-this.rate);
                  }
               } else {
                  this.cyclePos = this.ticks;
                  if (this.animation.getStatus() == Animation.Status.RUNNING) {
                     this.setCurrentRate(this.rate);
                  }
               }
            } else {
               this.cyclePos = this.ticks % this.cycleTicks;
               if (this.cyclePos == 0L) {
                  this.cyclePos = this.ticks;
               }
            }

            AnimationAccessor.getDefault().jumpTo(this.animation, this.cyclePos, this.cycleTicks, false);
            this.abortCurrentPulse();
         }
      }
   }
}
