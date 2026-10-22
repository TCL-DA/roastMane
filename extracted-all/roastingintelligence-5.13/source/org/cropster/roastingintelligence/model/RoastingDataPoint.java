package org.cropster.roastingintelligence.model;

import java.util.Arrays;

public class RoastingDataPoint {
   private final long _time;
   private Double[] _values;

   public RoastingDataPoint(long time, Double[] values) {
      this._time = time;
      if (values != null) {
         this._values = Arrays.copyOf(values, values.length);
      } else {
         this._values = null;
      }
   }

   public long getTimeStamp() {
      return this._time;
   }

   public Double[] getValues() {
      return this._values;
   }
}
