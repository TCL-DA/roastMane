package com.phidget22;

import java.util.Arrays;

public class DistanceSensorSonarReflections {
   public int[] distances;
   public int[] amplitudes;
   public int count;

   @Override
   public String toString() {
      return "[DistanceSensorSonarReflections](distances: "
         + Arrays.toString(this.distances)
         + ", amplitudes: "
         + Arrays.toString(this.amplitudes)
         + ", count: "
         + this.count
         + ")";
   }
}
