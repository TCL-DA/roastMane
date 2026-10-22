package com.sun.scenario.animation.shared;

import com.sun.javafx.util.Utils;
import javafx.animation.Animation;
import javafx.util.Duration;

public class FiniteClipEnvelope extends MultiLoopClipEnvelope {
   private int cycleCount;
   private long totalTicks;

   protected FiniteClipEnvelope(Animation var1) {
      super(var1);
      if (var1 != null) {
         this.autoReverse = var1.isAutoReverse();
         this.cycleCount = var1.getCycleCount();
      }

      this.updateTotalTicks();
   }

   @Override
   public ClipEnvelope setCycleDuration(Duration var1) {
      if (var1.isIndefinite()) {
         return create(this.animation);
      } else {
         this.updateCycleTicks(var1);
         this.updateTotalTicks();
         return this;
      }
   }

   @Override
   public ClipEnvelope setCycleCount(int var1) {
      if (var1 != 1 && var1 != -1) {
         this.cycleCount = var1;
         this.updateTotalTicks();
         return this;
      } else {
         return create(this.animation);
      }
   }

   @Override
   public void setRate(double var1) {
      boolean var3 = this.isDirectionChanged(var1);
      long var4 = var3 ? this.totalTicks - this.ticks : this.ticks;
      Animation.Status var6 = this.animation.getStatus();
      if (var6 != Animation.Status.STOPPED) {
         this.setInternalCurrentRate(Math.abs(this.currentRate - this.rate) < 1.0E-12 ? var1 : -var1);
         this.deltaTicks = var4 - this.ticksRateChange(var1);
         this.abortCurrentPulse();
      }

      this.ticks = var4;
      this.rate = var1;
   }

   @Override
   protected double calculateCurrentRate() {
      return !this.autoReverse ? this.rate : (this.isDuringEvenCycle() == this.rate > 0.0 ? this.rate : -this.rate);
   }

   private void updateTotalTicks() {
      this.totalTicks = (long)this.cycleCount * this.cycleTicks;
   }

   @Override
   public void timePulse(long var1) {
      if (this.cycleTicks != 0L) {
         this.aborted = false;
         this.inTimePulse = true;

         try {
            long var3 = this.ticks;
            long var5 = Math.round((double)var1 * Math.abs(this.rate));
            this.ticks = Utils.clamp(0L, this.deltaTicks + var5, this.totalTicks);
            boolean var7 = this.ticks >= this.totalTicks;
            long var8 = this.ticks - var3;
            if (var8 != 0L) {
               for (long var10 = this.currentRate > 0.0 ? this.cycleTicks - this.cyclePos : this.cyclePos; var8 >= var10; var10 = this.cycleTicks) {
                  if (var10 > 0L) {
                     this.cyclePos = this.currentRate > 0.0 ? this.cycleTicks : 0L;
                     var8 -= var10;
                     AnimationAccessor.getDefault().playTo(this.animation, this.cyclePos, this.cycleTicks);
                     if (this.aborted) {
                        return;
                     }
                  }

                  if (!var7 || var8 > 0L) {
                     if (this.autoReverse) {
                        this.setCurrentRate(-this.currentRate);
                     } else {
                        this.cyclePos = this.currentRate > 0.0 ? 0L : this.cycleTicks;
                        AnimationAccessor.getDefault().jumpTo(this.animation, this.cyclePos, this.cycleTicks, false);
                     }
                  }
               }

               if (var8 > 0L && !var7) {
                  this.cyclePos = this.cyclePos + (this.currentRate > 0.0 ? var8 : -var8);
                  AnimationAccessor.getDefault().playTo(this.animation, this.cyclePos, this.cycleTicks);
               }

               if (var7 && !this.aborted) {
                  AnimationAccessor.getDefault().finished(this.animation);
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
         if (this.rate < 0.0) {
            var1 = this.totalTicks - var1;
         }

         this.ticks = Utils.clamp(0L, var1, this.totalTicks);
         long var5 = this.ticks - var3;
         if (var5 != 0L) {
            this.deltaTicks += var5;
            if (this.autoReverse) {
               boolean var7 = this.ticks % (2L * this.cycleTicks) < this.cycleTicks;
               if (var7 == this.rate > 0.0) {
                  this.cyclePos = this.ticks % this.cycleTicks;
                  if (this.animation.getStatus() == Animation.Status.RUNNING) {
                     this.setCurrentRate(Math.abs(this.rate));
                  }
               } else {
                  this.cyclePos = this.cycleTicks - this.ticks % this.cycleTicks;
                  if (this.animation.getStatus() == Animation.Status.RUNNING) {
                     this.setCurrentRate(-Math.abs(this.rate));
                  }
               }
            } else {
               this.cyclePos = this.ticks % this.cycleTicks;
               if (this.rate < 0.0) {
                  this.cyclePos = this.cycleTicks - this.cyclePos;
               }

               if (this.cyclePos == 0L && this.ticks > 0L) {
                  this.cyclePos = this.cycleTicks;
               }
            }

            AnimationAccessor.getDefault().jumpTo(this.animation, this.cyclePos, this.cycleTicks, false);
            this.abortCurrentPulse();
         }
      }
   }
}
