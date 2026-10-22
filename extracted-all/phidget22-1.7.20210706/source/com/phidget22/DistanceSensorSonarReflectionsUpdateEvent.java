package com.phidget22;

import java.util.Arrays;

public class DistanceSensorSonarReflectionsUpdateEvent {
   DistanceSensor Source;
   int[] Distances;
   int[] Amplitudes;
   int Count;

   public DistanceSensorSonarReflectionsUpdateEvent(DistanceSensor var1, int[] var2, int[] var3, int var4) {
      this.Source = var1;
      this.Distances = var2;
      this.Amplitudes = var3;
      this.Count = var4;
   }

   public DistanceSensor getSource() {
      return this.Source;
   }

   public int[] getDistances() {
      return this.Distances;
   }

   public int[] getAmplitudes() {
      return this.Amplitudes;
   }

   public int getCount() {
      return this.Count;
   }

   @Override
   public String toString() {
      return "[DistanceSensorSonarReflectionsUpdateEvent](Source: "
         + this.Source
         + ", Distances: "
         + Arrays.toString(this.Distances)
         + ", Amplitudes: "
         + Arrays.toString(this.Amplitudes)
         + ", Count: "
         + this.Count
         + ")";
   }
}
