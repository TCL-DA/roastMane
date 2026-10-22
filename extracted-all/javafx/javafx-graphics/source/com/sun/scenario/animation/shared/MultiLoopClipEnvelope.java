package com.sun.scenario.animation.shared;

import javafx.animation.Animation;

abstract class MultiLoopClipEnvelope extends ClipEnvelope {
   protected boolean autoReverse;
   protected long cyclePos;

   protected MultiLoopClipEnvelope(Animation var1) {
      super(var1);
   }

   protected boolean isAutoReverse() {
      return this.autoReverse;
   }

   @Override
   public void setAutoReverse(boolean var1) {
      this.autoReverse = var1;
   }

   @Override
   protected long ticksRateChange(double var1) {
      return Math.round((double)(this.ticks - this.deltaTicks) * Math.abs(var1 / this.rate));
   }

   protected boolean isDirectionChanged(double var1) {
      return var1 * this.rate < 0.0;
   }

   protected boolean isDuringEvenCycle() {
      return this.ticks % (2L * this.cycleTicks) < this.cycleTicks;
   }
}
