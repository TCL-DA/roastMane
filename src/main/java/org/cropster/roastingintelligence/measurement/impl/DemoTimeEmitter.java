package org.cropster.roastingintelligence.measurement.impl;

import org.cropster.roastingintelligence.RoastingModel;
import org.cropster.roastingintelligence.listener.RoastingListener;
import org.cropster.roastingintelligence.measurement.TimeEmitter;
import org.cropster.roastingintelligence.util.NanoTime;

public class DemoTimeEmitter implements TimeEmitter, RoastingListener {
   private long _time = NanoTime.currentTimeMillis();

   @Override
   public long getInterval() {
      return 100L;
   }

   @Override
   public long getTime() {
      this._time += 1000L;
      return this._time;
   }

   @Override
   public void roastingStarted(RoastingModel roastingModel) {
      this._time = NanoTime.currentTimeMillis();
   }

   @Override
   public void roastingForced(RoastingModel roastingModel) {
   }

   @Override
   public void roastingAborted(RoastingModel roastingModel) {
   }

   @Override
   public void roastingStopped(RoastingModel roastingModel) {
   }

   @Override
   public void roastingFinalized(RoastingModel roastingModel) {
   }
}
