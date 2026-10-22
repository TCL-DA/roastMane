package org.cropster.roastingintelligence.measurement.impl;

import org.cropster.roastingintelligence.measurement.TimeEmitter;
import org.cropster.roastingintelligence.util.NanoTime;

public class DefaultTimeEmitter implements TimeEmitter {
   private long _samplingInterval = 1000L;

   public DefaultTimeEmitter() {
   }

   public DefaultTimeEmitter(long samplingInterval) {
      this._samplingInterval = samplingInterval;
   }

   @Override
   public long getInterval() {
      return this._samplingInterval;
   }

   @Override
   public long getTime() {
      return NanoTime.currentTimeMillis();
   }
}
